package com.syc.adquisiciones.businessLogic;

import java.io.DataInputStream;
import java.io.File;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import com.axtel.contratos.ContractStatus;
import com.axtel.contratos.core.Contrato;
import com.axtel.contratos.core.ContratoAmpliacion;
import com.axtel.contratos.core.ContratoDiversoConvenio;
import com.axtel.contratos.core.ContratosConGarantia;
import com.axtel.contratos.core.ConvenioCap4;
import com.axtel.contratos.core.DatosContratoPSP;
import com.axtel.contratos.core.GarantiaContrato;
import com.axtel.contratos.core.LiberaGarantiaContrato;
import com.axtel.proveedores.dao.ProveedorDAO;
import com.axtel.proveedores.model.Proveedor;
import com.syc.adquisiciones.ContratoArt25;
import com.syc.adquisiciones.DatosEP_TMP;
import com.syc.adquisiciones.core.ContratoModificado;
import com.syc.adquisiciones.core.DatosArchivo;
import com.syc.adquisiciones.manager.BaseSACManager;
import com.syc.adquisiciones.manager.ContratacionFormalizadaManager;
import com.syc.adquisiciones.manager.ContratoModificadoManager;
import com.syc.adquisiciones.servlet.CambiaPropiedadesUsuario;
import com.syc.adquisiciones.util.Util;
import com.syc.contable.ContableInterface;
import com.syc.contable.core.AplicacionContable;
import com.syc.contable.core.AplicacionContable.AplicarContableReturn;
import com.syc.dsmngr.DataSourceManager;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoDatoManager;
import com.syc.gestion.core.CasoManager;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.obrapublica.ConfiguraAplicativoBusinessLogic;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import com.syc.sai.procesosAutomaticos.AdjuntaArchivoMasivoManager;

public class ContratacionFormalizadaBusinessLogic extends DataSourceManager{
	public static Logger log = Logger.getLogger(ContratacionFormalizadaBusinessLogic.class);
	public ContratacionFormalizadaBusinessLogic(String jniName) {
		super.init(jniName);
	}
	public JSONObject consultaAreasResponsables(String cCoord) throws Exception {
		Connection conn = null;
		JSONArray arrayObj=null;
		JSONObject jsonObj=new JSONObject();
		BaseSACManager manager;
		String cEjercicio="2021";
		String query;
		ConfiguraAplicativoBusinessLogic configApp=null;
		String desa="";
		try {
			conn=getConnection();
			configApp = new ConfiguraAplicativoBusinessLogic(GestionInterface.ATT_CONEXION);
			manager= new BaseSACManager();
			boolean esSAIAlterno = "true".equalsIgnoreCase(configApp.getSystemSetting("AMBIENTE_DESARROLLO"));
			if(esSAIAlterno) {
				desa="_desa";
			}
			cEjercicio=Util.obtieneEjercicioFiscalActivo( conn );
			//catalogo area responsable "unidades ejecutoras"
			query="select '<Seleccione una opción>'cunidad,'0' cUejecutora union SELECT cUejecutora+' - '+cDescripcion AS UNIDAD,cUejecutora \r\n" + 
					"FROM nomina_"+cEjercicio+desa+".dbo.nom_Unidad_Ejecutora WITH(nOLOCK) WHERE c_coordinacion='"+cCoord+"'\r\n"+
					"order by cUejecutora";
			arrayObj=manager.obtieneDatQuery(conn, query);
			jsonObj.put("catalogoAreaResponsable", arrayObj);
			arrayObj=null;
			conn.commit();
		} catch ( Exception e ) {
			if(conn!=null) {
				conn.rollback();
			}
			log.error( e.getMessage() );
			e.printStackTrace();
		}finally {
			if(conn!=null) {
				conn.close();
			}
			conn=null;
			manager=null;
		}
		return jsonObj;
	}
	public JSONObject consultaSelectsPSP(String cIdcontratoDef) throws Exception {
		Connection conn = null;
		JSONArray arrayObj=null;
		JSONObject jsonObj=new JSONObject();
		BaseSACManager manager;
		String cEjercicio="2021";
		String query;
		boolean hayInfo=false;
		String cCoord="";
		ConfiguraAplicativoBusinessLogic configApp=null;
		String desa="";
		try {
			conn=getConnection();
			configApp = new ConfiguraAplicativoBusinessLogic(GestionInterface.ATT_CONEXION);
			manager= new BaseSACManager();
			boolean esSAIAlterno = "true".equalsIgnoreCase(configApp.getSystemSetting("AMBIENTE_DESARROLLO"));
			if(esSAIAlterno) {
				desa="_desa";
			}
			//codificar
			cEjercicio=Util.obtieneEjercicioFiscalActivo( conn );			
			//Datos PSP
			arrayObj=manager.datGuardados(conn, "select cIdcontratoDefinitivo,cAreaRequirente cAreaReq,cAreaResponsable cAreaResp,nCentroTrabajo cCentroTrabajo"
					+ ",lEsMaestro nElPSPEsMaestro,1 nEsContratacionPSP,convert(varchar,mMontoMensual)mMontoMensual,cDenominacionProyecto from mDatosContratoPSP with(Nolock) where cIdcontratoDefinitivo='"+cIdcontratoDef+"'");
			jsonObj.put("datosGuardados", arrayObj);
			hayInfo=arrayObj.getJSONObject(0).getBoolean("HAYINFO");
			if(hayInfo) {
				cCoord=arrayObj.getJSONObject(0).getString("cAreaReq");
			}
			arrayObj=null;
			//Datos es contratación PSP
			arrayObj=manager.datGuardados(conn, "select lEsPSP nEsContratacionPSP from mcontrato with(Nolock) where cIdContratoDefinitivo='"+cIdcontratoDef+"'");
			jsonObj.put("datoContratacionPSP", arrayObj);
			arrayObj=null;
			//Catálogo Coordinaciones "areas requirentes"
			query="select '<Seleccione una opción>'cunidad,'0' cUejecutora\r\n"
					+ "union select \r\n" + 
					"cUejecutora +' - '+cDescripcion cunidad,cUejecutora\r\n" + 
					"from nomina_"+cEjercicio+desa+".dbo.nom_Unidad_Ejecutora cat with(Nolock)\r\n" + 
					"inner join(\r\n" + 
					"	select \r\n" + 
					"	c_coordinacion,cDescCoortaCordinacion\r\n" + 
					"	from nomina_"+cEjercicio+desa+".dbo.nom_Unidad_Ejecutora with(Nolock) \r\n" + 
					"	group by c_coordinacion,cDescCoortaCordinacion\r\n" + 
					")coor\r\n" + 
					"on coor.c_coordinacion=cat.cUejecutora";
			arrayObj=manager.obtieneDatQuery(conn, query);
			jsonObj.put("catalogoCoordinaciones", arrayObj);
			//String cCoord=(arrayObj!=null && arrayObj.length()>0)?arrayObj.getJSONObject(0).getString( "Id" ):"A01";
			arrayObj=null;
			//catalogo area responsable "unidades ejecutoras"
			if(hayInfo) {
				query="select '<Seleccione una opción>'cunidad,'0' cUejecutora union SELECT cUejecutora+' - '+cDescripcion AS UNIDAD,cUejecutora \r\n" + 
						"FROM nomina_"+cEjercicio+desa+".dbo.nom_Unidad_Ejecutora WITH(nOLOCK) WHERE c_coordinacion='"+cCoord+"'\r\n"+
						"order by cUejecutora";
			}else {
				query="select '<Seleccione una opción>'cunidad,'0' cUejecutora ";
			}
			arrayObj=manager.obtieneDatQuery(conn, query);
			jsonObj.put("catalogoAreaResponsable", arrayObj);
			arrayObj=null;
			//Catálogo centro de trabajo
			query="select '<Seleccione una opción>'d_centrotrab,'0' c_centrotrab  "
					+ "union SELECT CONVERT(varchar, c_centrotrab)+' - '+d_centrotrab as cCentroTrab,c_centrotrab  FROM  nomina_"+cEjercicio+desa+".dbo.nom_centrotrabajo with(Nolock) where nActivo=1 order by c_centrotrab ";
			arrayObj=manager.obtieneDatQuery(conn, query);
			jsonObj.put("catalogoCentroTrabajo", arrayObj);
			arrayObj=null;
			conn.commit();
		} catch ( Exception e ) {
			if(conn!=null) {
				conn.rollback();
			}
			log.error( e.getMessage() );
			e.printStackTrace();
		}finally {
			if(conn!=null) {
				conn.close();
			}
			conn=null;
			manager=null;
			configApp=null;
		}
		return jsonObj;
	}
	public void guardaDatosPSP(Contrato cont,Usuario usuario) throws Exception{
		Connection conn = null;
		ContratacionFormalizadaManager manager=null;
		StringBuilder msg=null;
		try {
			conn=getConnection();
			manager=new ContratacionFormalizadaManager();
			if(cont.isnEsContratacionPSP()) {
				msg=validaDatosPSP( cont.getDatPSP() );
				if(msg.length()>0) {
					throw new Exception(msg.toString());
				}
				manager.deleteDatosPSP( conn, cont.getcIdContratoDefinitivo() );
				manager.addDatosPSP( conn, cont.getDatPSP() );
				manager.updateEsPSP( conn, cont.getcIdContratoDefinitivo(), 1 );
				Util.bitacoraMovimientos( cont.getcIdContratoDefinitivo(), "Guarda datos PSP", usuario.getLogin(), conn );
			}else {
				manager.deleteDatosPSP( conn, cont.getcIdContratoDefinitivo() );
				manager.updateEsPSP( conn, cont.getcIdContratoDefinitivo(), 0 );
			}
			conn.commit();
		} catch ( SQLException e ) {
			if(conn!=null) {
				conn.rollback();
			}
			throw(e);
		}finally {
			if(conn!=null) {
				conn.close();
			}
			conn=null;
			manager=null;
			msg=null;
		}
	}
	public void apruebaContratoArt25LAASSP(Contrato cont,Usuario usuario) throws Exception{
		Connection conn = null;
		ContratacionFormalizadaManager manager=null;
		ConvenioCap4 conv=null;
		Caso caso=null;
		String folioCaso="";
		int folio=0;
		try {
			conn=getConnection();
			manager=new ContratacionFormalizadaManager();
			conv=new ConvenioCap4();
			conv.setcIdContratoDefinitivo( cont.getcIdContratoDefinitivo() );
			if(manager.existPcontratoDiverso( conn, conv )) {
				throw new Exception("El contrato ya existe en contrato diverso.");
			}
			//Generar caso
			caso=Util.generaGuardaCaso(usuario.getU_UR(),  (GestionInterface.IDTC_CONTRATO_DIVERSO+""), "Aprueba contrato art. 25 LAASSP", usuario, cont.getJndiName(), cont.getcEjercicio());
			folioCaso = caso.getFolio();
			int indice = folioCaso.lastIndexOf('-') + 1;
			folio = Integer.parseInt(folioCaso.substring(indice));
			cont.setcFOLIO( folioCaso );
			cont.setnConsecutivoCDIV( folio );
			//Insertar en pcontratodiverso
			manager.addContratoDiverso( conn, cont );
			//Cambiar de estatus el contrato
			cont.setnIdEstado( ContractStatus.APPROVED );
			manager.updateEstateContrato( conn, cont );
			//se agrega la garantia
			manager.addContratoConGarantia( conn, cont.getcIdContratoDefinitivo() );
			//Avanza el caso
			Util.avanzaCaso(caso, usuario, cont.getPrefixPath(), new String[] {"CONSULTA_CONTRATODIVERSO"}, new String[] {"consulta_contrato"}, cont.getJndiName());
			//Guardar bitacora
			Util.bitacoraMovimientos( cont.getcIdContratoDefinitivo(), "Contrato art 25 autorizado", usuario.getLogin(), conn );
			
			conn.commit();
		} catch ( Exception e ) {
			if(conn!=null) {
				conn.rollback();
			}
			throw(e);
		}finally {
			if(conn!=null) {
				conn.close();
			}
			conn=null;
			caso=null;
			manager=null;
			conv=null;
		}
	}
	private StringBuilder validaDatosPSP(DatosContratoPSP datPSP) {
		StringBuilder msg=new StringBuilder();
		if("0".equalsIgnoreCase( datPSP.getcAreaRequirente()) ) {
			msg.append( "Favor de seleccionar un área requirente.\n " );
		}
		if("0".equalsIgnoreCase( datPSP.getcAreaResponsable()) ) {
			msg.append( "Favor de seleccionar un área responsable.\n " );
		}
		if(datPSP.getnCentroTrabajo()==0) {
			msg.append( "Favor de seleccionar un centro de trabajo.\n" );
		}
		if( "".equalsIgnoreCase(datPSP.getcDenominacionProyecto()) ) {
			msg.append( "Favor de capturar la denominación del proyecto.\n" );
		}
		if(datPSP.getmMontoMensual() <=0.0) {
			msg.append( "Favor de capturar el monto mensual del servicio.\n" );
		}
		return msg;
	}
	public String guardaCaratulaContrato(Contrato cont,Usuario usuario) throws Exception{
		Connection conn = null;
		ContratacionFormalizadaManager manager=null;
		StringBuilder msg=null;
		int nIdFechaInicio=17;
		int nIdFechaFin=18;
		String resp="";
		boolean error=true;
		try {
			conn=getConnection();
			manager=new ContratacionFormalizadaManager();
			
			if(cont.getnIdEstado()<4 && !manager.existeCompromiso( conn, cont ) ) {
				//validar la exeptuación de la garantia.
				if(cont.getITieneAnticipo()==1 && cont.getmGarantiaAnticipo()<=0.00) {
					throw new Exception("Se está diciendo que se otorga anticipo, favor de capturar el monto de la garantia de anticipo.");
				}
				if(cont.getlExcentaGarantia()==0 && cont.getmTotalGarantia()<=0.00) {
					throw new Exception("Si el contrato no cuenta con garantias, favor de excentar el contrato.");
				}
				if(!manager.existePrecom( conn, cont )){//si ya se precomprometio ya no se permite agregar impuestos "Porque los impuesto modifican el valor del contrato"
					manager.deleteOtrosImpuestos( conn, cont );
					manager.addOtrosImpuestos( conn, cont );
				}
				manager.updateContrato( conn, cont );
				manager.updateFundamentoLegProced( conn, cont.getcIdProcedimiento(), cont.getProcedAdj().getnIdfundamentoLeg() );
				manager.updateFundamentoLegProcedAdj( conn, cont.getProcedAdj() );
				//dependiendo del tipo de procedimiento de contratación, los id de fechas de inicio  y fin cambian
				if(cont.getnCategoriaProcedimiento()==13){//parseInt($("#cIdCategoriaProcedimiento").val(),10)
					nIdFechaInicio=15;
					nIdFechaFin=16;
				}
				manager.deleteFechasContrato( conn, cont.getcIdProcedimiento(), nIdFechaInicio, nIdFechaFin );
				manager.addFechasProcedimiento( conn, cont.getcIdProcedimiento(), GestionInterface.ATT_NID_FECHA_FORMALIZACION, cont.getfFormalizacion() );
				manager.addFechasProcedimiento( conn, cont.getcIdProcedimiento(), nIdFechaInicio, cont.getfInicio() );
				if("PC".equalsIgnoreCase( cont.getcIdtipoProcedimiento()) || "PT".equalsIgnoreCase( cont.getcIdtipoProcedimiento()) || "PR".equalsIgnoreCase( cont.getcIdtipoProcedimiento()) ) {
					manager.addFechasProcedimiento( conn, cont.getcIdProcedimiento(), GestionInterface.ATT_NID_FECHA_ENTREGA, cont.getfEntrega() );
				}else {
					manager.addFechasProcedimiento( conn, cont.getcIdProcedimiento(), nIdFechaFin, cont.getfFin() );
				}
				//actualiza pcontratodiverso
				manager.updateContratoDiverso( conn, cont );
				
				
				//VALIDACIÓN DE HIPERVINCULOS
				if(manager.existeDocumentacionHiperv( conn, cont.getcIdContratoDefinitivo() )){
					manager.updateDocumentacionContrato( conn, cont );
				}else{
					manager.addDocumentacionHiperv( conn, cont );
				}
				//valida si es contratación de un PSP
				if(cont.isnEsContratacionPSP()) {
					msg=validaDatosPSP( cont.getDatPSP() );
					if(msg.length()>0) {
						throw new Exception(msg.toString());
					}
					manager.deleteDatosPSP( conn, cont.getcIdContratoDefinitivo() );
					manager.addDatosPSP( conn, cont.getDatPSP() );
					manager.deletePrestaciondelServicio( conn, cont.getcIdContratoDefinitivo() );//Solo aplica cuando no es un PSP
				}else {
					manager.deleteDatosPSP( conn, cont.getcIdContratoDefinitivo() );
					if(!manager.existenDatosPrestacionServicio( conn, cont.getcIdContratoDefinitivo() )) {
						throw new Exception("Favor de capturar el lugar de prestaci\u00f3n del servicio.");
					}
				}
				Util.bitacoraMovimientos( cont.getcIdContratoDefinitivo(), "Carátula de contrato guardada", usuario.getLogin(), conn );
				resp="Carátula de contrato guardada";
			}else {
				resp="No es posible guardar los cambios porque el estatus del contrato no lo permite.";
			}
			conn.commit();
			error=false;
			return resp;
		} catch ( SQLException e ) {
			if(conn!=null) {
				conn.rollback();
			}
			throw(e);
		}finally {
			if(error && conn!=null) {
				conn.rollback();
			}
			CloseObject.closeObject(conn, false);
			conn=null;
			manager=null;
		}
	}
	public void apruebaContratoRemanente(Contrato cont,Usuario usuario) throws Exception{
		Connection conn = null;
		ContratacionFormalizadaManager manager=null;
		Caso caso=null;
		String folioCaso="";
		int folio=-1;
		Proveedor p=null;
		try {
			conn=getConnection();
			manager=new ContratacionFormalizadaManager();
			cont.setcIdEntidadContable( usuario.getPropiedad( "CCENTROCONTABLE" ).getValor() );
			//insert en pcontratodiverso
			manager.registraRemanenteContratoDiverso( conn, cont.getcIdContratoDefinitivo(), usuario.getLogin() );
			//Agregar retencion
			p=ProveedorDAO.selectByRfc( conn, cont.getcIdRFC() );
			manager.addRetencionContrato( conn, cont, p.getnPersonaFisica(), p.getnPersonaMoral(), p.getEsResico() );
			//insert en tcontratoep
			manager.insertContratoEP( conn, cont.getcIdContratoDefinitivo() );
			//Generar caso
			caso=Util.generaGuardaCaso(usuario.getU_UR(),  (GestionInterface.IDTC_CONTRATO_DIVERSO+""), "Aprueba contrato remanente", usuario, cont.getJndiName(), cont.getcEjercicio());
			folioCaso = caso.getFolio();
			int indice = folioCaso.lastIndexOf('-') + 1;
			folio = Integer.parseInt(folioCaso.substring(indice));
			//Avanza el caso
			Util.avanzaCaso(caso, usuario, cont.getPrefixPath(), new String[] {"CONSULTA_CONTRATODIVERSO"}, new String[] {"consulta_contrato"}, cont.getJndiName());
			//Actualiza el caso en pcontratodiverso y mcontratoremanente
			cont.setnIdEstado( ContractStatus.APPROVED );
			manager.updateEstatusContratoRemanente( conn, cont );
			//Actualiza folio del caso en pcontratodiv y mcontratoremanente
			cont.setcFOLIO( folioCaso );
			cont.setnConsecutivoCDIV( folio );
			manager.updateFolioCasoContratoRemanente( conn, cont );
			manager.updateFolioCasoContratoDiverso( conn, cont );
			//Cambiar el estatus del contrato a aprobado
			Util.bitacoraMovimientos( cont.getcIdContratoDefinitivo(), "Contrato remanente aprobado", usuario.getLogin(), conn );
			conn.commit();
		} catch ( SQLException e ) {
			if(conn!=null) {
				conn.rollback();
			}
			throw(e);
		}finally {
			CloseObject.closeObject(conn, false);
			conn=null;
			manager=null;
			caso=null;
			folioCaso=null;
			p=null;
		}
	}
	public JSONObject queryDataNewConvCap4() throws Exception {
		boolean error=true;
		Connection conn = null;
		StringBuilder query=null;
		JSONArray arrayObj=null;
		JSONObject jsonObj=null;
		try {
			conn=getConnection();
			query=new StringBuilder();
			jsonObj=new JSONObject();
			query.append( "SELECT (cUnidadResponsable + ' - ' + D_DESCRIPCION) AS cIdUnidadEjecutora, cUnidadResponsable AS cUnidadEjecutora FROM v_unidadEjecutoraPadre WITH(NOLOCK)" );
			log.info(query);
			arrayObj=Util.obtieneDatQuery(conn, query.toString());
			jsonObj.put("catalogoAreaResponsable", arrayObj);
			arrayObj=null;
			query.delete( 0, query.length() );
			
			query.append( "select cDescripcion,nIdTipoMod from mcatalogoTipoMod with(Nolock) where nActivo=1 and nIdTipoMod in(0)" );
			log.info(query);
			arrayObj=Util.obtieneDatQuery(conn, query.toString());
			jsonObj.put("catalogoTipoMod", arrayObj);
			
			conn.commit();
			error=false;
			return jsonObj;
		} catch ( Exception e ) {
			log.error( "Bug, consulting data new contract cap 4000: " +e );
			throw new Exception( "Bug, consulting data new contract cap 4000: " + e.toString());
		}finally {
			if ( error ) {
				if ( conn != null )
					try {
						conn.rollback();
					} catch ( Exception e ) {
						log.error( "Bug, Rollback: " + e );
						throw new Exception( "Bug, Rollback: " + e.toString(), e );
					}
			}
			CloseObject.closeObject( conn );
			arrayObj=null;
			if(query!=null) {
				query.delete( 0, query.length() );
			}
			query=null;
		}
	}
	public JSONObject queryDataConvCap4() throws Exception {
		boolean error=true;
		Connection conn = null;
		StringBuilder query=null;
		JSONArray arrayObj=null;
		JSONObject jsonObj=null;
		try {
			conn=getConnection();
			query=new StringBuilder();
			jsonObj=new JSONObject();
			query.append( "SELECT (cUnidadResponsable + ' - ' + D_DESCRIPCION) AS cIdUnidadEjecutora, cUnidadResponsable AS cUnidadEjecutora FROM v_unidadEjecutoraPadre WITH(NOLOCK)" );
			log.info(query);
			arrayObj=Util.obtieneDatQuery(conn, query.toString());
			jsonObj.put("catalogoAreaResponsable", arrayObj);
			
			
			conn.commit();
			error=false;
			return jsonObj;
		} catch ( Exception e ) {
			log.error( "Bug, consulting data new contract cap 4000: " +e );
			throw new Exception( "Bug, consulting data new contract cap 4000: " + e.toString());
		}finally {
			if ( error ) {
				if ( conn != null )
					try {
						conn.rollback();
					} catch ( Exception e ) {
						log.error( "Bug, Rollback: " + e );
						throw new Exception( "Bug, Rollback: " + e.toString(), e );
					}
			}
			CloseObject.closeObject( conn );
			arrayObj=null;
			if(query!=null) {
				query.delete( 0, query.length() );
			}
			query=null;
		}
	}
	public JSONObject addNewConvenioCap4(ConvenioCap4 conv) throws Exception {
		boolean error=true;
		Connection conn = null;
		ContratacionFormalizadaManager manager=null;
		String cEjercicioActual="2022";
		
		try {
			conn=getConnection();
			manager=new ContratacionFormalizadaManager();
			//Valida que no haya otro convenio en tramite
			if(manager.existConvenioCap4SinAutorizar( conn, conv.getcIdContratoDefinitivo() )) {
				throw new Exception( "No se puede generar otro convenio al contrato "+conv.getcIdContratoDefinitivo()+" porque se tiene un convenio que no está autorizado." );
			}
			//year phiscal current
			cEjercicioActual=Util.obtieneEjercicioFiscalActivo( conn );
			//search contract
			conv.setcEjercicio( cEjercicioActual );
			manager.searchContractCap4( conn, conv );
			//Consecutivo de modificación
			manager.getnConsecutiveModificationConvCap4( conn, conv );
			//add convenio
			manager.addNewConvCap4( conn, conv );
			//add lines, only if it is expand money
			if(conv.getnTipoModificacion()==0) {
				manager.addPartidasNewConvCap4( conn, conv );
			}
			conn.commit();
			error=false;
			return null;
		} catch ( Exception e ) {
			log.error( "Bug, addNewConvenioCap4: " +e );
			throw new Exception( "Bug, addNewConvenioCap4: " + e.toString());
		}finally {
			if ( error ) {
				if ( conn != null )
					try {
						conn.rollback();
					} catch ( Exception e ) {
						log.error( "Bug, Rollback: " + e );
						throw new Exception( "Bug, Rollback: " + e.toString(), e );
					}
			}
			CloseObject.closeObject( conn );
			manager=null;
			conv=null;
		}
	}
	public JSONObject queryDataCaratulaConvCap4(ConvenioCap4 conv) throws Exception {
		boolean error=true;
		Connection conn = null;
		StringBuilder query=null;
		JSONArray arrayObj=null;
		JSONObject jsonObj=null;
		try {
			conn=getConnection();
			query=new StringBuilder();
			jsonObj=new JSONObject();
			query.append( "  select conv.cIdUnidadEjecutora+' - '+ue.D_DESCRIPCION lblUnidadEjecutora " );
			query.append( " ,'Contrato Definitivo: '+conv.cIdContratoDefinitivo lblDefinitivo " );
			query.append( " ,conv.cIdRFC+' - '+prov.cRazonSocial lblProveedor " );
			query.append( " ,'ESTATUS DEL CONVENIO: '+estatus.cEstado lblEstadoMod " );
			query.append( " ,'Tipo de Modificación: '+upper(modd.cDescripcion) lblTipoMod " );
			query.append( " ,'Monto Original: $ '+convert(varchar,conv.mTotalAnterior,1) lblTotalContratoOriginal " );
			query.append( " ,'Monto Modificado: $ '+convert(varchar,conv.mTotalNuevo-conv.mTotalAnterior,1) lblTotalContratoModificado " );
			query.append( " ,'Porcentaje de Modificación: '+convert(varchar,convert(float,round((conv.mTotalNuevo-conv.mTotalAnterior)/(conv.mTotalAnterior)*100.0,2)))+' %' lblTotalPorcentajeMod  " );
			query.append( " ,conv.nEstatus,unidadMed.cIdUnidadMedida " );
			query.append( " ,case when fFechaFormalizacion is null then '' else convert(varchar,isnull(fFechaFormalizacion,''),103) end fechaFormalizacion " );
			query.append( " ,case when fFechaInicioEntrega is null then '' else convert(varchar,isnull(fFechaInicioEntrega,''),103) end fechaInicio " );
			query.append( " ,case when fFechaFin is null then '' else convert(varchar,isnull(fFechaFin,''),103)end fechaFin  " );
			query.append( " ,case when fFechaInicioEntrega is null then '' else convert(varchar,isnull(fFechaInicioEntrega,''),103)end fechaEntrega  " );
			query.append( " ,isnull(cObjetoConvenio,'') objConv,isnull(cNoConvenio,'') cNoConvenio,cEjercicio " );
			query.append( ",convert(varchar,conv.mTotalNuevo-conv.mTotalAnterior,1) mImporteTotal" );
			query.append( " from  mContratoModificadoCap4 conv with(Nolock) " );
			query.append( " inner join tCatUnidadEjecutora as ue with(Nolock) " );
			query.append( " on ue.cUnidadEjecutora=conv.cIdUnidadEjecutora " );
			query.append( " inner join mCatalogoProveedor as prov with(Nolock) " );
			query.append( " on prov.cIdRFC=conv.cIdRFC " );
			query.append( " inner join mCatalogoEstadoContrato as estatus with(Nolock) " );
			query.append( " on estatus.nIdEstado=conv.nEstatus " );
			query.append( " INNER JOIN mcatalogoTipoMod modd  " );
			query.append( " on modd.nIdTipoMod=conv.nTipoModificacion " );
			query.append( "inner join ( select nIdContModCap4,cIdUnidadMedida from mContratoModificadoCap4Partida with(Nolock) group by nIdContModCap4,cIdUnidadMedida )unidadMed on unidadMed.nIdContModCap4=conv.nIdContModCap4 " );
			query.append( " where conv.cIdContratoDefinitivo='"+conv.getcIdContratoDefinitivo()+"' and conv.nIdContModCap4="+conv.getnIdContModCap4()+" and conv.nConsecutivoModificacion= " );
			query.append( conv.getnConsecutivoModificacion() );
			
			log.info(query.toString());
			arrayObj=Util.datGuardados(conn, query.toString());
			jsonObj.put("datosCaratula", arrayObj);
			
			conn.commit();
			error=false;
			return jsonObj;
		} catch ( Exception e ) {
			log.error( "Bug, queryDataCaratulaConvCap4: " +e );
			throw new Exception( "Bug, queryDataCaratulaConvCap4: " + e.toString());
		}finally {
			if ( error ) {
				if ( conn != null )
					try {
						conn.rollback();
					} catch ( Exception e ) {
						log.error( "Bug, Rollback: " + e );
						throw new Exception( "Bug, Rollback: " + e.toString(), e );
					}
			}
			CloseObject.closeObject( conn );
			arrayObj=null;
			if(query!=null) {
				query.delete( 0, query.length() );
			}
			query=null;
		}
	}
	public void deleteConvenioCap4(ConvenioCap4 conv) throws Exception {
		boolean error=true;
		Connection conn = null;
		ContratacionFormalizadaManager manager=null;
		
		try {
			conn=getConnection();
			manager=new ContratacionFormalizadaManager();
			//Valida que esté en captura
			if(manager.stateConvenioCap4( conn, conv )>ContractStatus.CAPTURED) {
				throw new Exception( "El estatus del convenio debe estar en captura para poder eliminarlo." );
			}
			//delete partidas
			manager.deletePartidasConvCap4( conn, conv );
			//delete convenio
			manager.deleteConvenioCap4( conn, conv );
			//delete eps
			manager.deleteContratoEP_TEMP( conn, conv.getcIdContratoDefinitivo()+"#M"+conv.getnConsecutivoModificacion()  );
			
			conn.commit();
			error=false;
		} catch ( Exception e ) {
			log.error( "Bug, addNewConvenioCap4: " +e );
			throw new Exception( "Bug, addNewConvenioCap4: " + e.toString());
		}finally {
			if ( error ) {
				if ( conn != null )
					try {
						conn.rollback();
					} catch ( Exception e ) {
						log.error( "Bug, Rollback: " + e );
						throw new Exception( "Bug, Rollback: " + e.toString(), e );
					}
			}
			CloseObject.closeObject( conn );
			manager=null;
			conv=null;
		}
	}
	public void updateItemsConvenioCap4(ConvenioCap4 conv) throws Exception {
		boolean error=true;
		Connection conn = null;
		ContratacionFormalizadaManager manager=null;
		double nPorcentajeMod=0.0d;
		double nPorcentajeIVA=0.0d;
		try {
			conn=getConnection();
			manager=new ContratacionFormalizadaManager();
			//Consulta datos en base
			manager.queryItemConvCap4( conn, conv );
			nPorcentajeIVA=manager.getPorcentajeIVA( conn, conv.getPartidas().get( 0 ).getnIdIVA() );
			if(manager.stateConvenioCap4( conn, conv )>ContractStatus.CAPTURED) {
				throw new Exception( "El estatus del convenio debe estar en captura para poder modificar el monto." );
			}
			if("SRV".equalsIgnoreCase(  conv.getPartidas().get( 0 ).getcIdUnidadMedida())) {
				conv.getPartidas().get( 0 ).setmPrecioUnitario( Math.round( (conv.getPartidas().get( 0 ).getmMontoNeto()/(conv.getPartidas().get( 0 ).getnCantidad()*(1+(0.01*nPorcentajeIVA))) )*100.0 )/100.0 );
			}else {
				conv.getPartidas().get( 0 ).setmMontoNeto( Math.round((conv.getPartidas().get( 0 ).getnCantidad()*conv.getPartidas().get( 0 ).getmPrecioUnitario()*(1+(0.01*nPorcentajeIVA)) ) *100.0 )/100.0 );
			}
			nPorcentajeMod=conv.getPartidas().get( 0 ).getmMontoNeto()/conv.getPartidas().get( 0 ).getmMontoNetoOriginal()*100;
			//Valida porcentaje de modificación
			if(nPorcentajeMod>20.00) {
				throw new Exception( "No se puede ampliar mas del 20 %." );
			}
			//update item
			manager.updateItemConvCap4( conn, conv );
			//update monto modificado
			manager.updateMontoModConvCap4( conn, conv );
			conn.commit();
			error=false;
		} catch ( Exception e ) {
			log.error( "Bug, updateItemsConvenioCap4: " +e );
			throw new Exception( "Bug, updateItemsConvenioCap4: " + e.toString());
		}finally {
			if ( error ) {
				if ( conn != null )
					try {
						conn.rollback();
					} catch ( Exception e ) {
						log.error( "Bug, Rollback: " + e );
						throw new Exception( "Bug, Rollback: " + e.toString(), e );
					}
			}
			CloseObject.closeObject( conn );
			manager=null;
			conv=null;
		}
	}
	public void apruebaConvenioCap4(ConvenioCap4 conv,Usuario usuario) throws Exception {
		boolean error=true;
		Connection conn = null;
		ContratacionFormalizadaManager manager=null;
		Caso caso=null;
		String folioCaso="";
		int nFolio;
		try {
			conn=getConnection();
			manager=new ContratacionFormalizadaManager();
			//Valida que esté en captura
			if(manager.stateConvenioCap4( conn, conv )>ContractStatus.CAPTURED) {
				throw new Exception( "El estatus del convenio debe estar en captura para poder aprobarlo." );
			}
			if(manager.arethereEP( conn, conv.getcIdContratoDefinitivo()+"#M"+conv.getnConsecutivoModificacion() )==0) {
				throw new Exception( "Favor de agregar una estructura presupuestal." );
			}
			//obtener datos
			manager.searchDataConvenioCap4( conn, conv );
			//insert en pcontratodiverso
			if(conv.getlEsConvEjercicioAnt()==1) {
				if(!manager.existPcontratoDiverso( conn, conv )) {
					caso=Util.generaGuardaCaso(usuario.getU_UR(),  (GestionInterface.IDTC_CONTRATO_DIVERSO+""), "Convenio Ejercicio anterior de contratos cap4", usuario, conv.getJndiName(), conv.getcEjercicio());
					folioCaso = caso.getFolio();
					int indice = folioCaso.lastIndexOf('-') + 1;
					nFolio = Integer.parseInt(folioCaso.substring(indice));
					conv.setnConsecutivoCDIV( nFolio );
					conv.setcFolio( folioCaso );
					//se avanza caso para que no se vea la operacion  en el inbox  
					Util.avanzaCaso( caso, usuario, conv.getPrefixPath(),  new String[] {"CONSULTA_CONTRATODIVERSO"}, new String[] {"consulta_contrato"}, conv.getJndiName() );
					manager.addContratoDiverso( conn, conv );
				}else {
					folioCaso="CDIV-"+conv.getcIdUnidadEjecutora()+"-"+conv.getnConsecutivoCDIV();
					conv.setcFolio( folioCaso );
				}
				manager.updateFolioCasoConvCap4( conn, conv );
			}
			//update mContratoModificado
			conv.setnEstatus( ContractStatus.NO_BUDGET );
			manager.updatePresupuestoConvCap4( conn, conv );
			//insert into pContratoDiversoConvenio
			manager.addContratoDiversoConvenio( conn, conv );
			//insert into tContratoEP
			manager.insertContratoEP( conn, conv.getcIdContratoDefinitivo() );
			
			conn.commit();
			error=false;
		} catch ( Exception e ) {
			log.error( "Bug, apruebaConvenioCap4: " +e );
			throw new Exception( "Bug, apruebaConvenioCap4: " + e.toString());
		}finally {
			if ( error ) {
				if ( conn != null )
					try {
						conn.rollback();
					} catch ( Exception e ) {
						log.error( "Bug, Rollback: " + e );
						throw new Exception( "Bug, Rollback: " + e.toString(), e );
					}
			}
			CloseObject.closeObject( conn );
			manager=null;
			conv=null;
			caso=null;
		}
	}
	public void devuelvePresupConvenioCap4(ConvenioCap4 conv) throws Exception {
		boolean error=true;
		Connection conn = null;
		ContratacionFormalizadaManager manager=null;
		
		try {
			conn=getConnection();
			manager=new ContratacionFormalizadaManager();
			//Valida que esté en SAI SIN PRESUPUESTO
			if(manager.stateConvenioCap4( conn, conv )!=ContractStatus.NO_BUDGET) {
				throw new Exception( "El estatus del convenio debe estar en SAI SIN PRESUPUESTO." );
			}
			conv.setnEstatus( ContractStatus.CAPTURED );
			manager.updateEstatusConvCap4( conn, conv );
			manager.deleteContratoDiversoConvenio( conn, conv.getcIdContratoDefinitivo(), conv.getnConsecutivoModificacion(),conv.getcIdContratoDefinitivo()+"#M"+conv.getnConsecutivoModificacion() );
			conn.commit();
			error=false;
		} catch ( Exception e ) {
			log.error( "Bug, devuelveConvenioCap4: " +e );
			throw new Exception( "Bug, devuelveConvenioCap4: " + e.toString());
		}finally {
			if ( error ) {
				if ( conn != null )
					try {
						conn.rollback();
					} catch ( Exception e ) {
						log.error( "Bug, Rollback: " + e );
						throw new Exception( "Bug, Rollback: " + e.toString(), e );
					}
			}
			CloseObject.closeObject( conn );
			manager=null;
			conv=null;
		}
	}
	public void deleteEPConvenioCap4(ConvenioCap4 conv) throws Exception {
		boolean error=true;
		Connection conn = null;
		ContratacionFormalizadaManager manager=null;
		
		try {
			conn=getConnection();
			manager=new ContratacionFormalizadaManager();
			//Valida que esté en captura
			if(manager.stateConvenioCap4( conn, conv )!=ContractStatus.CAPTURED) {
				throw new Exception( "El estatus del convenio no permite la eliminación de EP´S." );
			}
			manager.deleteContratoEP_TEMP( conn, conv.getcIdContratoDefinitivo()+"#M"+conv.getnConsecutivoModificacion(), conv.getcEP() );
			conn.commit();
			error=false;
		} catch ( Exception e ) {
			log.error( "Bug, devuelveConvenioCap4: " +e );
			throw new Exception( "Bug, devuelveConvenioCap4: " + e.toString());
		}finally {
			if ( error ) {
				if ( conn != null )
					try {
						conn.rollback();
					} catch ( Exception e ) {
						log.error( "Bug, Rollback: " + e );
						throw new Exception( "Bug, Rollback: " + e.toString(), e );
					}
			}
			CloseObject.closeObject( conn );
			manager=null;
			conv=null;
		}
	}
	public void generaPrecompromisoContArt25(ContratoArt25 cont,HttpServletRequest request,Usuario usuario) throws Exception{
		boolean error=true;
		Connection conn = null;
		ContratacionFormalizadaManager manager=null;
		String param[]=new String[6];
		String cEevento="PRECOM";
		try {
			conn=getConnection();
			manager=new ContratacionFormalizadaManager();
			//Obtener los datos del Contrato
			manager.searchDataContArt25( conn, cont );
			//Valida que esté en SAI SIN PRESUPUESTO
			if(cont.getnIdEstado()!=ContractStatus.NO_BUDGET) {
				throw new Exception( "El estatus del contrato debe estar en SAI SIN PRESUPUESTO para poder generar el precompromiso." );
			}
			//Validar si ya hay precompromiso
			if(Util.hayPrecompromiso(conn, cont.getcIdContratoDef())){
				throw new Exception( "El contrato ya tiene un precompromiso." );
			}
			//Precomprometer
			param[0]=cont.getcIdContratoDef();
			param[1]="N";
			param[2]=Util.obtieneEjercicioFiscalActivo( conn );
			param[3]="Precompromiso de Contratos Art 25";
			if(cont.getnEsDescentralizado()==0) {
				precompromisoCentralizado( conn, request, usuario, param, cont.getcPrefixPath(), cont.getJndiName(), cEevento );
			}else {
				precompromisoDescentralizado( conn, request, usuario, param, cont.getcPrefixPath(), cont.getJndiName(),cEevento );
			}
			//Actualizar estatus del contrato
			cont.setnIdEstado( ContractStatus.BUDGET );
			manager.updateEstatusContratoArt25( conn, cont );
			//Guardaar en bitacora los movimientos
			Util.bitacoraMovimientos(param[0], "Generación de precompromiso para contratos art25 con Folio="+param[4], usuario.getLogin(), conn);
			
			conn.commit();
			error=false;
		} catch ( Exception e ) {
			log.error( "Bug, Genera Precompromiso: " +e );
			throw new Exception( "Bug, Genera Precompromiso: " + e.toString());
		}finally {
			if ( error ) {
				if ( conn != null )
					try {
						conn.rollback();
					} catch ( Exception e ) {
						log.error( "Bug, Rollback: " + e );
						throw new Exception( "Bug, Rollback: " + e.toString(), e );
					}
			}
			CloseObject.closeObject( conn );
			cEevento=null;
			conn = null;
			manager=null;
			param=null;
		}
	}
	private void precompromisoDescentralizado(Connection conn,HttpServletRequest request,Usuario usuario,String param[],String prefixPath,String jndiName,String cEevento)throws Exception {
		String ueOriginal=usuario.getU_UR();
	 	String cCentroContableOrig = usuario.getPropiedad("CCENTROCONTABLE").getValor();
	 	CambiaPropiedadesUsuario cpu=new CambiaPropiedadesUsuario();
	 	PreparedStatement pstm=null;
	 	String sql=null;
	 	ResultSet rs=null;
	 	String folio=null;
	 	AplicarContableReturn acr=null;
		ContableInterface conInt =null;
		Caso caso=null;
		String cadenaFolios="";
		String token="";
		String cadTabla=null;
		String arrayTabla[];
		ArrayList<List<String>> tabla=null;
		int val=-1;
		Map  <String, String> map=null;
		String [] responsable=null;
		String [] nombre=null;
		try {
			cadTabla=request.getParameter("tablaDatos");
			arrayTabla=cadTabla.split(",");
			tabla=Util.creaArray(arrayTabla);
			sql="select *from v_obtieneCentroContable with(Nolock) where cIdContratoDefinitivo='"+param[0]+"' and cEjercicio='"+param[2]+"'";
			log.info(sql);
			pstm = conn.prepareStatement(sql);
			rs = pstm.executeQuery();
			conInt = new AplicacionContable();
			while(rs.next()){
				//Cambia el centro contable al usuario
				cpu.cambiaCentroContableUE(rs.getString("ur"), rs.getString("centroContable"), usuario);
				//Obtiene un nuevo caso.
				caso=Util.generaGuardaCaso(usuario.getU_UR(),  (GestionInterface.IDTC_PRECOMPROMISO+""), param[3], usuario, jndiName, param[2]);
				//caso = generaGuardaCaso(conn, usuario, request, rsEnc.getString("aEjercicioFiscal"), GestionInterface.IDTC_COMPROMISO);
				folio = caso.getFolio().substring(caso.getFolio().lastIndexOf("-") + 1);
				param[4]=folio;
				param[5]=caso.getFolio();
				cadenaFolios=cadenaFolios+token+caso.getFolio();
				token=",";
				//Crea Encbezado y detalle
				val=Util.creaEncDetPreCompromiso(usuario, conn, param, tabla, cEevento,true);
				if(val!=0){
					CloseObject.closeObject( rs );
				 	CloseObject.closeObject( pstm );
					throw new Exception( "Bug, al crear el encabezado y detalle del precompromiso con folios: "+cadenaFolios );
				}
				//Aplicación contable
				map = CasoDatoManager.readValuesCasoDato(request, caso.getCasoDato(), true);
				acr= conInt.aplicarContableNuevo(conn, caso, "", "", "", 0, "",map,prefixPath,usuario.getLogin(),"");
				if (!acr.isSuccess()) {
					CloseObject.closeObject( rs );
				 	CloseObject.closeObject( pstm );
					throw new Exception( "Bug, en la aplicación contable del precompromiso con folios : "+cadenaFolios );
				}
				// Una vez que ha hecho la aplicación contable avanza el caso A CONSULTA
				responsable=new String[] { "CONSULTA_PRECOMPROMISO" };//VENTANILLA_PRECOMPROMISO
				nombre=new String[] { "consulta_precomp" };//autoriza_precomp
				Util.avanzaCaso(request, caso, usuario, prefixPath, responsable,nombre,jndiName);
				log.debug(caso.getCasoDato("APLICADO_CONT").getValor());
			}
			usuario.setU_UR( ueOriginal );
			usuario.getPropiedad( "CCENTROCONTABLE" ).setValor( cCentroContableOrig );
		} catch ( Exception e ) {
			throw new Exception( e);
		}finally {
			ueOriginal=null;
		 	cCentroContableOrig = null;
		 	cpu=null;
		 	CloseObject.closeObject( rs );
		 	CloseObject.closeObject( pstm );
		 	rs=null;
		 	folio=null;
		 	acr=null;
			conInt =null;
			caso=null;
			cadenaFolios=null;
			token=null;
			map=null;
			responsable=null;
			nombre=null;
		}
		
	}
	private void precompromisoCentralizado(Connection conn,HttpServletRequest request,Usuario usuario,String param[],String prefixPath,String jndiName,String cEevento)throws Exception {
		ContableInterface conInt=null;
		Caso caso=null;
		String folio=null;
		int val=-1;
		AplicarContableReturn acr=null;
		Map  <String, String> map=null;
		String [] responsable=null;
		String [] nombre=null;
		String cadTabla=null;
		String arrayTabla[];
		ArrayList<List<String>> tabla=null;
		try {
			conInt = new AplicacionContable();
			cadTabla=request.getParameter("tablaDatos");
			if(cadTabla==null) {
				throw new Exception( "Bug, No se recibi\u00f3 el calendario de las EPS." );
			}
			arrayTabla=cadTabla.split(",");
			tabla=Util.creaArray(arrayTabla);
			
			caso=Util.generaGuardaCaso(usuario.getU_UR(),  (GestionInterface.IDTC_PRECOMPROMISO+""), param[3], usuario, jndiName, param[2]);
			folio = caso.getFolio().substring(caso.getFolio().lastIndexOf("-") + 1);
			param[4]=folio;
			param[5]=caso.getFolio();
			
			//Crea Encbezado y detalle
			val=Util.creaEncDetPreCompromiso(usuario, conn, param, tabla, cEevento, false);
			if(val!=0){
				throw new Exception( "Bug, al crear el encabezado y detalle del precompromiso " );
			}
			//Aplicación contable
			map = CasoDatoManager.readValuesCasoDato(request, caso.getCasoDato(), true);
			acr= conInt.aplicarContableNuevo(conn, caso, "", "", "", 0, "",map,prefixPath,usuario.getLogin(),"");
			if (!acr.isSuccess()) {
				throw new Exception( "Bug, en la aplicación contable del precompromiso  del convenio de cap 4 mil." );
			}
			// Una vez que ha hecho la aplicación contable avanza el caso A CONSULTA
			responsable=new String[] { "CONSULTA_PRECOMPROMISO" };//VENTANILLA_PRECOMPROMISO
			nombre=new String[] { "consulta_precomp" };//autoriza_precomp
			Util.avanzaCaso(request, caso, usuario, prefixPath, responsable,nombre,jndiName);
			log.debug(caso.getCasoDato("APLICADO_CONT").getValor());
		} catch ( Exception e ) {
			throw new Exception( e );
		}finally {
			if(tabla!=null &&!tabla.isEmpty()) {
				tabla.clear();
			}
			param=null;
			acr=null;
			tabla=null;
			conInt=null;
			caso=null;
			folio=null;
			map=null;
			responsable=null;
			nombre=null;
		}
	}
	public void generaPrecompromisoConvenio(ContratoModificado conv,HttpServletRequest request,Usuario usuario) throws Exception{
		boolean error=true;
		Connection conn = null;
		ContratacionFormalizadaManager manager=null;
		ContratoModificadoManager modManager=null;
		String cEjercicioActual="2024";
		AplicarContableReturn acr=null;
		Map  <String, String> map=null;
		ContableInterface conInt=null;
		Caso caso=null;
		String folio=null;
		int val=-1;
		ArrayList<Map<String, String>> registros = null;
		Map<String, String> datosMap =null;
		int i=0;
		String [] responsable=null;
		String [] nombre=null;
		String param[]=new String[6];
		String nameStoreProcedure=null;
		CambiaPropiedadesUsuario cpu=null;
		String ueOriginal=usuario.getU_UR();
		String cCentroContableOrig = usuario.getPropiedad("CCENTROCONTABLE").getValor();
		try {
			conn=getConnection();
			manager=new ContratacionFormalizadaManager();
			modManager=new ContratoModificadoManager();
			conInt = new AplicacionContable();
			cpu=new CambiaPropiedadesUsuario();
			cEjercicioActual=Util.obtieneEjercicioFiscalActivo( conn );
			manager.searchDataConvenio( conn, conv );
			//Valida que esté en SAI SIN PRESUPUESTO
			if(conv.getnEstado()!=ContractStatus.NO_BUDGET) {
				throw new Exception( "El estatus del convenio debe estar en SAI SIN PRESUPUESTO para poder generar el precompromiso." );
			}
			if(Util.hayPrecompromiso(conn, conv.getcIdContratoDefinitivo()+"#M"+conv.getnConsecutivoModificacion())){
				throw new Exception( "El contrato ya tiene un precompromiso." );
			}
			param[0]=conv.getcIdContratoDefinitivo()+"#M"+conv.getnConsecutivoModificacion();
			param[1]="N";
			param[2]=Util.obtieneEjercicioFiscalActivo( conn );
			param[3]="REGISTRO DEL CONTRATO FOLIO "+conv.getcIdContratoDefinitivo()+"#M"+conv.getnConsecutivoModificacion();
			//Generar un ciclo de las unidades ejecutoras que participan en el precompromiso
			registros=modManager.queryUnidadesCont( conn, conv );
			while( i<registros.size()){
				datosMap=registros.get( i );
				conv.setcCanoCompromiso(  Util.generaCaNoContrarecibo( datosMap.get( "cCentroContable" ), cEjercicioActual, "CO" ));
				conv.setcCentroContable( datosMap.get( "cCentroContable" ) );
				conv.setcUnidadEjecutoraLinea( datosMap.get( "cUnidadResponsable" ) );
				cpu.cambiaCentroContableUE(datosMap.get( "cUnidadResponsable" ), datosMap.get( "cCentroContable" ), usuario);
				//validar la unidad
				if(null==datosMap.get( "cUnidadResponsable" ) || "".equalsIgnoreCase( datosMap.get( "cUnidadResponsable" ) )) {
					log.info("La tabla de partidas del contrato modificado no tiene la unidad ejecutora de cada l\u00ednea");
					throw new Exception("La tabla de partidas del contrato modificado no tiene la unidad ejecutora de cada l\u00ednea");
				}
				//Se genera el caso
		 		caso=Util.generaGuardaCaso(datosMap.get( "cUnidadResponsable" ),  (GestionInterface.IDTC_PRECOMPROMISO+""), "REGISTRO DEL CONTRATO FOLIO "+conv.getcIdContratoDefinitivo()+"#M"+conv.getnConsecutivoModificacion(), usuario, conv.getJndiName(), cEjercicioActual);
				if(caso==null){
					//no se genero correctamente folio
					log.info("NO SE PUDO GENERAR EL FOLIO DE COMPROMISO INTENTE NUEVAMENTE");
					throw new Exception("NO SE PUDO GENERAR EL FOLIO DE COMPROMISO INTENTE NUEVAMENTE");
				}else{
					folio = caso.getFolio().substring(caso.getFolio().lastIndexOf("-") + 1);
					param[4]=folio;
					param[5]=caso.getFolio();
					conv.setnFolioPreCompromiso( Integer.parseInt( folio ) );
					conv.setcFolioPreCompromiso( caso.getFolio() );
					//Crea Encbezado
					val=Util.creaEncPreCompromiso( usuario, conn, param, conv.getcCanoCompromiso() );
					if(val!=0){
						throw new Exception( "Bug, al crear el encabezado del precompromiso "+folio );
					}
					//Crea el detalle
					if(conv.getnConvenioEjercicioAnt()==1) {//Validar si es un convenio del ejercicio Anterior
						nameStoreProcedure="pa_mAptdPrccDetalleModEjerAnt";
					}else {
						if(conv.getcIdContratoDefinitivo().indexOf("PLU")>=0&&conv.getcIdContratoDefinitivo().indexOf("/"+cEjercicioActual)<=0) {//Validar si es un convenio de un contrato plurianual de ejercicios anteriores
							//String cIdContratoDefinitivo, int nConsecutivoMod,int nFolioPreCompromiso,String cCentroContable,String ur, int nTipoPago, Connection conn
							nameStoreProcedure="pa_mAptdPrccDetalleModPlu";
						}else {//Convenios de contrataciones del ejercicio actual
							//queryFormPost("pa_mAptdPrccDetalleMod", {async: false });
							nameStoreProcedure="pa_mAptdPrccDetalleMod";
						}
					}
					Util.creaDetPreCompromisoConvenio( conn,conv, nameStoreProcedure);
					//Aplicación contable
					map = CasoDatoManager.readValuesCasoDato(request, caso.getCasoDato(), true);
					acr= conInt.aplicarContableNuevo(conn, caso, "", "", "", 0, "",map,conv.getPrefixPath(),usuario.getLogin(),"");
					if (!acr.isSuccess()) {
						throw new Exception( "Bug, en la aplicación contable del precompromiso." );
					}
					// Una vez que ha hecho la aplicación contable avanza el caso A CONSULTA
					responsable=new String[] { "CONSULTA_PRECOMPROMISO" };//VENTANILLA_PRECOMPROMISO
					nombre=new String[] { "consulta_precomp" };//autoriza_precomp
					Util.avanzaCaso(request, caso, usuario, conv.getPrefixPath(), responsable,nombre,conv.getJndiName());
					log.debug(caso.getCasoDato("APLICADO_CONT").getValor());
					//Guardaar en bitacora los movimientos
					Util.bitacoraMovimientos(conv.getcIdContratoDefinitivo()+"#M"+conv.getnConsecutivoModificacion(), "Generación de precompromiso para convenios con Folio="+folio, usuario.getLogin(), conn);
					
					//Guarda la relación de precompromisos
					manager.addRelacionPrecom( conn, param[0], param[5], conv.getnFolioPreCompromiso(), cEjercicioActual, datosMap.get( "cCentroContable" ), datosMap.get( "cUnidadResponsable" ) );
					manager.addRelacionPrecomCompromiso( conn, param[0], param[5], conv.getnFolioPreCompromiso() );
					caso=null;
					i++;
				}
			}
			
			//Actualizar estatus
			conv.setnEstado( ContractStatus.BUDGET );
			manager.updateStateConvenio( conn, conv );
			manager.updateFolioPrecompromisoConvenio( conn, conv );
			error=false;
			conn.commit();
		} catch ( Exception e ) {
			log.error( "Bug, generaPrecompromiso: " +e );
			throw new Exception( "Bug, generaPrecompromiso: " + e.toString());
		}finally {
			if ( error ) {
				if ( conn != null )
					try {
						conn.rollback();
					} catch ( Exception e ) {
						log.error( "Bug, Rollback: " + e );
						throw new Exception( "Bug, Rollback: " + e.toString(), e );
					}
			}
			CloseObject.closeObject( conn );
			manager=null;
			modManager=null;
			registros = null;
			datosMap =null;
			responsable=null;
			nombre=null;
			param=null;
			cpu.cambiaCentroContableUE(ueOriginal, cCentroContableOrig, usuario);
		}
	}
	public void generaPrecompromisoConvCap4(ConvenioCap4 conv,HttpServletRequest request,Usuario usuario,String prefixPath,String jndiName) throws Exception{
		boolean error=true;
		Connection conn = null;
		String cEevento="PRECOM";
		String param[]=new String[6];
		ArrayList<List<String>> tabla=null;
		String cadTabla=null;
		String arrayTabla[];
		ContratacionFormalizadaManager manager=null;
		ContableInterface conInt=null;
		Caso caso=null;
		String folio=null;
		int val=-1;
		AplicarContableReturn acr=null;
		Map  <String, String> map=null;
		String [] responsable=null;
		String [] nombre=null;
		try {
			conn=getConnection();
			manager=new ContratacionFormalizadaManager();
			
			cadTabla=request.getParameter("tablaDatos");
			arrayTabla=cadTabla.split(",");
			tabla=Util.creaArray(arrayTabla);
			param[0]=conv.getcIdContratoDefinitivo()+"#M"+conv.getnConsecutivoModificacion();
			param[1]="N";
			param[2]=Util.obtieneEjercicioFiscalActivo( conn );
			param[3]=request.getParameter( "cDescripPoliza" ); 
			//Valida que esté en SAI SIN PRESUPUESTO
			if(manager.stateConvenioCap4( conn, conv )!=ContractStatus.NO_BUDGET) {
				throw new Exception( "El estatus del convenio debe estar en SAI SIN PRESUPUESTO para poder generar el precompromiso." );
			}
			if(Util.hayPrecompromiso(conn, conv.getcIdContratoDefinitivo()+"#M"+conv.getnConsecutivoModificacion())){
				throw new Exception( "El contrato ya tiene un precompromiso." );
			}

			conInt = new AplicacionContable();
			caso=Util.generaGuardaCaso(usuario.getU_UR(),  (GestionInterface.IDTC_PRECOMPROMISO+""), param[3], usuario, jndiName, param[2]);
			folio = caso.getFolio().substring(caso.getFolio().lastIndexOf("-") + 1);
			param[4]=folio;
			param[5]=caso.getFolio();
			//Crea Encbezado y detalle
			val=Util.creaEncDetPreCompromiso(usuario, conn, param, tabla, cEevento, false);
			if(val!=0){
				throw new Exception( "Bug, al crear el encabezado y detalle del precompromiso " );
			}
			//Aplicación contable
			map = CasoDatoManager.readValuesCasoDato(request, caso.getCasoDato(), true);
			acr= conInt.aplicarContableNuevo(conn, caso, "", "", "", 0, "",map,prefixPath,usuario.getLogin(),"");
			if (!acr.isSuccess()) {
				throw new Exception( "Bug, en la aplicación contable del precompromiso  del convenio de cap 4 mil." );
			}
			//Guardaar en bitacora los movimientos
			Util.bitacoraMovimientos(param[0], "Generación de precompromiso para convenios de capitulo 4000 con Folio="+folio, usuario.getLogin(), conn);
			// Una vez que ha hecho la aplicación contable avanza el caso A CONSULTA
			responsable=new String[] { "CONSULTA_PRECOMPROMISO" };//VENTANILLA_PRECOMPROMISO
			nombre=new String[] { "consulta_precomp" };//autoriza_precomp
			Util.avanzaCaso(request, caso, usuario, prefixPath, responsable,nombre,jndiName);
			log.debug(caso.getCasoDato("APLICADO_CONT").getValor());
			
			conv.setcFolioPre( caso.getFolio() );
			conv.setnConsecutivoPrecom( Integer.parseInt( folio ));
			conv.setnEstatus( ContractStatus.BUDGET );
			manager.updatePrecomConvCap4( conn, conv );
			conn.commit();
			error=false;
		} catch ( Exception e ) {
			log.error( "Bug, generaPrecompromiso: " +e );
			throw new Exception( "Bug, generaPrecompromiso: " + e.toString());
		}finally {
			if ( error ) {
				if ( conn != null )
					try {
						conn.rollback();
					} catch ( Exception e ) {
						log.error( "Bug, Rollback: " + e );
						throw new Exception( "Bug, Rollback: " + e.toString(), e );
					}
			}
			CloseObject.closeObject( conn );
			if(tabla!=null &&!tabla.isEmpty()) {
				tabla.clear();
			}
			if(map!=null && !map.isEmpty()) {
				map.clear();
			}
			arrayTabla=null;
			tabla=null;
			cadTabla=null;
			
			param =null;
			cEevento=null;
			manager=null;
			conInt=null;
			caso=null;
			acr=null;
			map=null;
			responsable=null;
			nombre=null;
		}
	}
	public void devuelvePrecomConvenioCap4(HttpServletRequest request,String prefixPath,ConvenioCap4 conv,Usuario usuario) throws Exception {
		boolean error=true;
		Connection conn = null;
		ContratacionFormalizadaManager manager=null;
		Caso sc =null,c=null;
		AplicarContableReturn acr=null;
		ContableInterface conInt = null;
		Map <String, String>m =null;
		try {
			conn=getConnection();
			manager=new ContratacionFormalizadaManager();
			conInt = new AplicacionContable();
			//Valida que esté en SAI SIN PRESUPUESTO
			if(manager.stateConvenioCap4( conn, conv )!=ContractStatus.BUDGET) {
				throw new Exception( "El estatus del convenio debe estar en SAI PRESUPUESTADO para poder devolver el precompromiso." );
			}
			//get idCaso
			manager.getIdCasoConvCap4( conn, conv );
			if(conv.getnConsecutivoCDIV()<=0) {
				throw new Exception( "No se encontro el caso." );
			}
			sc = new Caso();
			sc.setIdCaso( conv.getnConsecutivoCDIV());
			c = CasoManager.select(conn, sc);
			//Cancelar Aplicación contable
			if(c==null){
				throw new Exception( "Error al obtener el caso." );
			}
			m = CasoDatoManager.readValuesCasoDato(request, c.getCasoDato(), true);
			acr = conInt.cancelarAppContableNueva(conn, c, "", "", "", 0, "", m, prefixPath, usuario.getLogin(), "");
			if (!acr.isSuccess()) {
				throw new Exception( "Error al hacer la aplicación contable." );
			}
			//Guardaar en bitacora los movimientos
			Util.bitacoraMovimientos(conv.getcIdContratoDefinitivo()+"#M"+conv.getnConsecutivoModificacion(), "Cancelación de precompromiso para convenios de capitulo 4000 con Folio="+conv.getcFolioPre(), usuario.getLogin(), conn);
			conv.setnEstatus( ContractStatus.NO_BUDGET );
			conv.setcFolioPre( null );
			conv.setnConsecutivoPrecom( 0 );
			manager.updatePrecomConvCap4( conn, conv );
			conn.commit();
			error=false;
		} catch ( Exception e ) {
			log.error( "Bug, devuelvePrecomConvenioCap4: " +e );
			throw new Exception( "Bug, devuelvePrecomConvenioCap4: " + e.toString());
		}finally {
			if ( error ) {
				if ( conn != null )
					try {
						conn.rollback();
					} catch ( Exception e ) {
						log.error( "Bug, Rollback: " + e );
						throw new Exception( "Bug, Rollback: " + e.toString(), e );
					}
			}
			CloseObject.closeObject( conn );
			if(m!=null && !m.isEmpty()) {
				m.clear();
			}
			manager=null;
			conv=null;
			sc =null;
			c=null;
			conInt = null;
			acr=null;
			m=null;
		}
	}
	public void generaCompromisoConvCap4(ConvenioCap4 conv,HttpServletRequest request,Usuario usuario,String prefixPath,String jndiName) throws Exception{
		boolean error=true;
		Connection conn = null;
		ContratacionFormalizadaManager manager=null;
		AplicarContableReturn acr=null;
	 	ContableInterface conInt = null;
	 	ConfiguraAplicativoBusinessLogic configApp = null;
	 	boolean esSAIAlterno=false;
	 	String folioComp;
	 	int folioCompromiso;
	 	String cEjercicioActivo="2022";
	 	Caso caso=null;
	 	int retVal=-1;
	 	Map <String,String> m=null;
		try {
			conn=getConnection();
			manager=new ContratacionFormalizadaManager();
			conInt = new AplicacionContable();
			configApp = new ConfiguraAplicativoBusinessLogic(GestionInterface.ATT_CONEXION);
			//Valida que esté en SAI  PRESUPUESTADO
			if(manager.stateConvenioCap4( conn, conv )!=ContractStatus.BUDGET) {
				throw new Exception( "El estatus del convenio debe estar en SAI PRESUPUESTADO para poder autorizarlo." );
			}
			esSAIAlterno = "true".equals(configApp.getSystemSetting("SAI_AMBIENTAL"))|| "true".equals(configApp.getSystemSetting("SAI_FONDEN"));
			//Validar si hay compromisos por autorizar en sicop
			if(Util.hayCompromisoPendienteAutSICOP(conn, conv.getcIdContratoDefinitivo())){
				throw new Exception( "Hay compromisos pendientes por autorizar en SICOP." );
				
			}
			//get data convenio
			manager.searchDataConvenioCap4( conn, conv );
			cEjercicioActivo=Util.obtieneEjercicioFiscalActivo( conn );
			//folioComp=generaGuardaCaso1(request, response,session,(GestionInterface.IDTC_COMPROMISO+""),"Aplicación de Compromiso");
			caso=Util.generaGuardaCaso(usuario.getU_UR(),  (GestionInterface.IDTC_COMPROMISO+""), "Aplicación de Compromiso", usuario, jndiName, cEjercicioActivo);
			if (caso == null) {
				throw new Exception( "NO SE PUDO GENERAR EL FOLIO DE COMPROMISO, INTENTE NUEVAMENTE." );
			}
			folioComp=caso.getFolio();
			folioCompromiso=Integer.parseInt(folioComp.substring(folioComp.lastIndexOf('-')+1));
			//Crear encabezado y detalle
			retVal=Util.creaEncabezadoDetalleComp( conv.getcIdContratoDefinitivo()+"#M"+conv.getnConsecutivoModificacion(), conv.getnConsecutivoPrecom(), folioCompromiso, "N", folioComp, usuario, conn );
			if(retVal!=0){
				throw new Exception( "Error al crear el encabezado y detlle." );
			}
			if(!Util.esRecursoFiscal(conn, conv.getcIdContratoDefinitivo()+"#M"+conv.getnConsecutivoModificacion()) || esSAIAlterno){
				//Aplicación contable
				m = CasoDatoManager.readValuesCasoDato(request, caso.getCasoDato(), true);
				acr= conInt.aplicarContableNuevo(conn, caso, "", "", "", 0, "",m,prefixPath,usuario.getLogin(),"");
				if (!acr.isSuccess()) {
					throw new Exception( "Error en la aplicación contable para el folio precompromiso="+conv.getnConsecutivoPrecom() );
				}
			}
			//Guardaar en bitacora los movimientos
			Util.bitacoraMovimientos(conv.getcIdContratoDefinitivo()+"#M"+conv.getnConsecutivoModificacion(), "Generación de compromiso para convenios de cap4 con Folio="+folioCompromiso, usuario.getLogin(), conn);
			// Una vez que ha hecho la aplicación contable avanza el caso
			Util.avanzaCaso(request, caso, usuario, prefixPath, new String[] { "CONSULTA_PAGOS" },new String[] { "consulta_compromiso" },jndiName);
			conv.setnEstatus( ContractStatus.APPROVED );
			manager.updateEstatusConvCap4( conn, conv );
			conn.commit();
			error=false;
		} catch ( Exception e ) {
			log.error( "Bug, generaCompromisoConvCap4: " +e );
			throw new Exception( "Bug, generaCompromisoConvCap4: " + e.toString());
		}finally {
			if ( error ) {
				if ( conn != null )
					try {
						conn.rollback();
					} catch ( Exception e ) {
						log.error( "Bug, Rollback: " + e );
						throw new Exception( "Bug, Rollback: " + e.toString(), e );
					}
			}
			CloseObject.closeObject( conn );
			if(m!=null && !m.isEmpty()) {
				m.clear();
			}
			m=null;
			manager=null;
			acr=null;
		 	conInt = null;
		 	configApp = null;
		 	caso=null;
		}
	}
	public void addCantDeAnticipoNoRecepcionado(String cIdcontratoDef,Connection conn) throws Exception{
		String cEjercicioActual="2023";
		String partContrato[]=null;
		String nameDBEjercicioCont=null;
		ContratacionFormalizadaManager manager=null;
		boolean tienePagoAnticipo=false;
		boolean tieneRecepMat=false;
		try {
			manager=new ContratacionFormalizadaManager();
			cEjercicioActual=Util.obtieneEjercicioFiscalActivo( conn );
			partContrato=cIdcontratoDef.split( "/" );
			if(Integer.parseInt( cEjercicioActual )-Integer.parseInt( partContrato[1] )==1) {//Validar que el anticipo se genero el año inmediato anterior
				nameDBEjercicioCont=Util.getNameDB( conn, Integer.parseInt( partContrato[1] ) );
				//Validar que solo se haya pagado el anticipo
				tienePagoAnticipo=manager.tienePagoDeAnticipo( conn, nameDBEjercicioCont, cIdcontratoDef );
				tieneRecepMat=manager.tieneRecepMat( conn, nameDBEjercicioCont, cIdcontratoDef );
				if(tienePagoAnticipo && !tieneRecepMat) {
					//Agregar el monto no recepcionado
					manager.addMontoNoRecepDelEjercicioAnt( conn, nameDBEjercicioCont, cIdcontratoDef );
				}
			}
		} catch ( Exception e ) {
			log.error( "Bug, addAnticipoNoRecepcionado: " +e );
			throw new Exception( "Bug, addAnticipoNoRecepcionado: " + e.toString());
		}finally {
			cEjercicioActual=null;
			partContrato=null;
			nameDBEjercicioCont=null;
			manager=null;
		}
	}
	public JSONObject queryGarantias() throws Exception {
		boolean error=true;
		Connection conn = null;
		StringBuilder query=null;
		JSONArray arrayObj=null;
		JSONObject jsonObj=null;
		try {
			conn=getConnection();
			query=new StringBuilder();
			jsonObj=new JSONObject();
			query.append( "Select 'Todo' as descript,'0' cid union SELECT  (cUnidadResponsable + ' - ' + D_DESCRIPCION) AS descrip, cUnidadResponsable AS cIdUnidadEjecutora FROM v_unidadEjecutoraPadre WITH(NOLOCK)" );
			log.info(query);
			arrayObj=Util.obtieneDatQuery(conn, query.toString());
			jsonObj.put("catalogoAreaResponsable", arrayObj);
			arrayObj=null;
			query.delete( 0, query.length() );
			
			
			conn.commit();
			error=false;
			return jsonObj;
		} catch ( Exception e ) {
			log.error( "Bug, consulting data new garantias: " +e );
			throw new Exception( "Bug, consulting data garantias: " + e.toString());
		}finally {
			if ( error ) {
				if ( conn != null )
					try {
						conn.rollback();
					} catch ( Exception e ) {
						log.error( "Bug, Rollback: " + e );
						throw new Exception( "Bug, Rollback: " + e.toString(), e );
					}
			}
			CloseObject.closeObject( conn );
			arrayObj=null;
			if(query!=null) {
				query.delete( 0, query.length() );
			}
			query=null;
		}
	}
	public JSONObject datosGarantias(String cIdContratoDef) throws Exception {
		boolean error=true;
		Connection conn = null;
		StringBuilder query=null;
		JSONArray arrayObj=null;
		JSONObject jsonObj=null;
		LeeArchivosBusinessLogic arch=null;
		String rutasArchGarantia[]=null;
		String rutasArchEndoso[]=null;
		DatosArchivo datosArchivo=null;
		try {
			conn=getConnection();
			arch=new LeeArchivosBusinessLogic();
			datosArchivo=new DatosArchivo();
			query=new StringBuilder();
			jsonObj=new JSONObject();
			//Obtener rutas de archivos
			datosArchivo.setcIdContratoDefinitivo( cIdContratoDef );
			datosArchivo.setcTituloAplicacion( "CONTRATODIVERSO" );
			datosArchivo.setcNameCarpeta( "DoctosGarantia" );
			datosArchivo.setcNombreArchivo( "Garantia" );
			rutasArchGarantia=arch.downloadGarantia( datosArchivo, null, true );
			datosArchivo.setcNombreArchivo( "Endoso" );
			rutasArchEndoso=arch.downloadGarantia( datosArchivo, null, true );
			
			query.append( "select " );
			query.append( "	'Ejercicio Fiscal de Contratación: '+cont.cEjercicioFiscal lblEjercicioFiscal" );
			query.append( "	,cont.cIdUnidadEjecutora +' - '+ue.D_DESCRIPCION lblUnidadEjecutora" );
			query.append( "	,'Contrato SAI: '+cont.cIdContratoDefinitivo lblDefinitivo" );
			query.append( "	,'Contrato CNET: '+cont.cNumContratoCNET lblContratoCNET" );
			query.append( "	,'Procedimiento CNET: '+cont.cNumProcedimientoCNET lblProcedimientoCNET" );
			query.append( "	,'[ '+cont.cIdRFC+' ] '+prov.cRazonSocial as lblProveedor" );
			query.append( "	,cont.cConcepto cConceptoContrato" );
			query.append( "	,'Total Contratado: $ '+convert(varchar,cont.mTotalContrato,1) lblTotalNeto" );
			query.append( " ,'Monto Total de Garantías $ '+ convert(varchar,cont.mTotalGarantias,1) mTotalGarantias" );
			query.append( " ,cont.cNumContratoCNET contratoCNET,cont.cFolioCaso cFolio");
			query.append( "	from mContratosConGarantia cont with(Nolock)" );
			query.append( "	inner join mCatalogoProveedor as prov with(Nolock)" );
			query.append( "	on prov.cIdRFC=cont.cIdRFC" );
			query.append( "	inner join tCatUnidadEjecutora as ue with(Nolock)" );
			query.append( "	on ue.cUnidadEjecutora=cont.cIdUnidadEjecutora" );
			query.append( "	where  cont.cIdContratoDefinitivo='"+cIdContratoDef+"'" );
			
			log.info(query);
			arrayObj=Util.datGuardados( conn, query.toString() );
			if(rutasArchGarantia!=null && rutasArchGarantia[0]!=null){
				arrayObj.getJSONObject( 0 ).put( "existeDoctoGarantia", 1 );
			}
			if(rutasArchEndoso!=null && rutasArchEndoso[0]!=null){
				arrayObj.getJSONObject( 0 ).put( "existeDoctoEndoso", 1 );
			}
			jsonObj.put("datosContratoGarantia", arrayObj);
			arrayObj=null;
			query.delete( 0, query.length() );
			query.append( "select " );
			query.append( "isnull(lFianza,0) lFianzaGA" );
			query.append( ",isnull(lcheque,0) lchequeGA" );
			query.append( ",isnull(cAseguradora,'') cAseguradoraGA" );
			query.append( ",isnull(cNumeroChequeFianza,0) nChequeFianzaGA" );
			query.append( ",convert(varchar,isnull(fFechaExpedicion,''),103) fFechaExpedicionGA" );
			query.append( ",'$ '+convert(varchar,isnull(mMontoGarantia,0),1) mMontoGarantiaAnticipo" );
			query.append( " from mGarantiasContrato garantia with(Nolock)" );
			query.append( " where garantia.nIdtipoProcesoGarantia=1 and garantia.nTipoGarantia="+GestionInterface.GARANTIA_ANTICIPO );
			query.append( " and garantia.cIdContratoDefinitivo='"+cIdContratoDef+"'" );
			arrayObj=Util.datGuardados( conn, query.toString() );
			jsonObj.put("datosGarantiaGA", arrayObj);
			arrayObj=null;
			query.delete( 0, query.length() );
			query.append( "select " );
			query.append( "isnull(lFianza,0) lFianzaGC" );
			query.append( ",isnull(lcheque,0) lchequeGC" );
			query.append( ",isnull(cAseguradora,'') cAseguradoraGC" );
			query.append( ",isnull(cNumeroChequeFianza,0) nChequeFianzaGC" );
			query.append( ",convert(varchar,isnull(fFechaExpedicion,''),103) fFechaExpedicionGC" );
			query.append( ",'$ '+convert(varchar,isnull(mMontoGarantia,0),1) mMontoGarantiaCumplimiento" );
			query.append( " from mGarantiasContrato garantia with(Nolock)" );
			query.append( " where garantia.nIdtipoProcesoGarantia="+GestionInterface.GARANTIA+" and garantia.nTipoGarantia="+GestionInterface.GARANTIA_CUMPLIMIENTO );
			query.append( " and garantia.cIdContratoDefinitivo='"+cIdContratoDef+"'" );
			arrayObj=Util.datGuardados( conn, query.toString() );
			jsonObj.put("datosGarantiaGC", arrayObj);
			arrayObj=null;
			query.delete( 0, query.length() );
			query.append( "select " );
			query.append( "isnull(lFianza,0) lFianzaGV" );
			query.append( ",isnull(lcheque,0) lchequeGV" );
			query.append( ",isnull(cAseguradora,'') cAseguradoraGV" );
			query.append( ",isnull(cNumeroChequeFianza,0) nChequeFianzaGV" );
			query.append( ",convert(varchar,isnull(fFechaExpedicion,''),103) fFechaExpedicionGV" );
			query.append( ",'$ '+convert(varchar,isnull(mMontoGarantia,0),1) mMontoGarantiaViciosO" );
			query.append( " from mGarantiasContrato garantia with(Nolock)" );
			query.append( " where garantia.nIdtipoProcesoGarantia="+GestionInterface.GARANTIA+" and garantia.nTipoGarantia="+GestionInterface.GARANTIA_VICIOS_OCULTOS );
			query.append( " and garantia.cIdContratoDefinitivo='"+cIdContratoDef+"'" );
			arrayObj=Util.datGuardados( conn, query.toString() );
			jsonObj.put("datosGarantiaGV", arrayObj);
			arrayObj=null;
			query.delete( 0, query.length() );
			query.append( "select " );
			query.append( "isnull(lFianza,0) lFianzaE" );
			query.append( ",isnull(lcheque,0) lchequeE" );
			query.append( ",isnull(cAseguradora,'') cAseguradora_endoso" );
			query.append( ",isnull(cNumeroChequeFianza,0) nChequeFianza_endoso" );
			query.append( ",convert(varchar,isnull(fFechaExpedicion,''),103) fFechaExpedicion_endoso" );
			query.append( ",'$ '+convert(varchar,isnull(mMontoGarantia,0),1) mMontoEndosoCumplimiento" );
			query.append( " from mGarantiasContrato garantia with(Nolock)" );
			query.append( " where garantia.nIdtipoProcesoGarantia="+GestionInterface.ENDOSO+" and garantia.nTipoGarantia="+GestionInterface.GARANTIA_CUMPLIMIENTO );
			query.append( " and garantia.cIdContratoDefinitivo='"+cIdContratoDef+"' and garantia.nIdConsecutivoEndosoGarantia=1" );
			arrayObj=Util.datGuardados( conn, query.toString() );
			jsonObj.put("datosEndoso", arrayObj);
			arrayObj=null;
			query.delete( 0, query.length() );
			query.append( "select " );
			query.append( "isnull(lFianza,0) lFianzaE2" );
			query.append( ",isnull(lcheque,0) lchequeE2" );
			query.append( ",isnull(cNumeroChequeFianza,0) nChequeFianza_endoso2" );
			query.append( ",convert(varchar,isnull(fFechaExpedicion,''),103) fFechaExpedicion_endoso2" );
			query.append( ",'$ '+convert(varchar,isnull(mMontoGarantia,0),1) mMontoEndosoCumplimiento2" );
			query.append( " from mGarantiasContrato garantia with(Nolock)" );
			query.append( " where garantia.nIdtipoProcesoGarantia="+GestionInterface.ENDOSO+" and garantia.nTipoGarantia="+GestionInterface.GARANTIA_CUMPLIMIENTO );
			query.append( " and garantia.cIdContratoDefinitivo='"+cIdContratoDef+"' and garantia.nIdConsecutivoEndosoGarantia=2" );
			arrayObj=Util.datGuardados( conn, query.toString() );
			jsonObj.put("datosEndoso2", arrayObj);
			arrayObj=null;
			query.delete( 0, query.length() );
			query.append( "select " );
			query.append( "isnull(lFianza,0) lFianzaE3" );
			query.append( ",isnull(lcheque,0) lchequeE3" );
			query.append( ",isnull(cNumeroChequeFianza,0) nChequeFianza_endoso3" );
			query.append( ",convert(varchar,isnull(fFechaExpedicion,''),103) fFechaExpedicion_endoso3" );
			query.append( ",'$ '+convert(varchar,isnull(mMontoGarantia,0),1) mMontoEndosoCumplimiento3" );
			query.append( " from mGarantiasContrato garantia with(Nolock)" );
			query.append( " where garantia.nIdtipoProcesoGarantia="+GestionInterface.ENDOSO+" and garantia.nTipoGarantia="+GestionInterface.GARANTIA_CUMPLIMIENTO );
			query.append( " and garantia.cIdContratoDefinitivo='"+cIdContratoDef+"' and garantia.nIdConsecutivoEndosoGarantia=3" );
			arrayObj=Util.datGuardados( conn, query.toString() );
			jsonObj.put("datosEndoso3", arrayObj);
			conn.commit();
			error=false;
			return jsonObj;
		} catch ( Exception e ) {
			log.error( "Bug, consulting data new garantias: " +e );
			throw new Exception( "Bug, consulting data garantias: " + e.toString());
		}finally {
			if ( error ) {
				if ( conn != null )
					try {
						conn.rollback();
					} catch ( Exception e ) {
						log.error( "Bug, Rollback: " + e );
						throw new Exception( "Bug, Rollback: " + e.toString(), e );
					}
			}
			CloseObject.closeObject( conn );
			arrayObj=null;
			if(query!=null) {
				query.delete( 0, query.length() );
			}
			query=null;
			arch=null;
			rutasArchGarantia=null;
			rutasArchEndoso=null;
			datosArchivo=null;
		}
	}
	public JSONObject datosLiberaGarantia(String cIdContratoDef) throws Exception {
		boolean error=true;
		Connection conn = null;
		StringBuilder query=null;
		JSONArray arrayObj=null;
		JSONObject jsonObj=null;
		LeeArchivosBusinessLogic arch=null;
		String rutasArchGarantiaLib[]=null;
		DatosArchivo datosArchivo=null;
		try {
			conn=getConnection();
			arch=new LeeArchivosBusinessLogic();
			datosArchivo=new DatosArchivo();
			query=new StringBuilder();
			jsonObj=new JSONObject();
			//Obtener rutas de archivos
			datosArchivo.setcIdContratoDefinitivo( cIdContratoDef );
			datosArchivo.setcTituloAplicacion( "CONTRATODIVERSO" );
			datosArchivo.setcNameCarpeta( "DoctosGarantia" );
			datosArchivo.setcNombreArchivo( "LiberaGarantia" );
			rutasArchGarantiaLib=arch.downloadGarantia( datosArchivo, null, true );
			
			query.append( " select 	'Ejercicio Fiscal de Contratación: '+cont.cEjercicioFiscal lblEjercicioFiscal	 " );
			query.append( " ,cont.cIdUnidadEjecutora +' - '+ue.D_DESCRIPCION lblUnidadEjecutora	 " );
			query.append( " ,'Contrato SAI: '+cont.cIdContratoDefinitivo lblDefinitivo	 " );
			query.append( " ,'Contrato CNET: '+cont.cNumContratoCNET lblContratoCNET	 " );
			query.append( " ,'Procedimiento CNET: '+cont.cNumProcedimientoCNET lblProcedimientoCNET	 " );
			query.append( " ,'[ '+cont.cIdRFC+' ] '+prov.cRazonSocial as lblProveedor	 " );
			query.append( " ,cont.cConcepto cConceptoContrato	,'Total Contratado: $ '+convert(varchar,cont.mTotalContrato,1) lblTotalNeto  " );
			query.append( " ,'Monto Total de Garantías $ '+ convert(varchar,cont.mTotalGarantias,1) mTotalGarantias ,cont.cNumContratoCNET contratoCNET " );
			query.append( " ,cont.cFolioCaso cFolio  ,'Garantía de Anticipo: $ '+convert(varchar,isnull(subGA.totalAnticipo,0),1)lblGarantiaAnticipo   " );
			query.append( " ,'Garantía de Cumplimiento: $ '+convert(varchar,isnull(subGC.totalCumplimiento,0),1)lblGarantiaCumplimiento   " );
			query.append( " ,'Garantía de Vicios Ocultos: $ '+convert(varchar,isnull(subGV.totalViciosOcultos,0),1) lblGarantiaViciosOcultos  " );
			query.append( " ,isnull(lFianza,0) lFianzaG,isnull(lcheque,0) lchequeG	 " );
			query.append( " from mContratosConGarantia cont with(Nolock)	 " );
			query.append( " inner join mCatalogoProveedor as prov with(Nolock)	on prov.cIdRFC=cont.cIdRFC	 " );
			query.append( " inner join tCatUnidadEjecutora as ue with(Nolock)	on ue.cUnidadEjecutora=cont.cIdUnidadEjecutora  " );
			query.append( " left join (   " );
			query.append( " 	select 	sum(isnull(mMontoGarantia,0))totalAnticipo 	  " );
			query.append( " 	,cIdContratoDefinitivo 	 " );
			query.append( " 	from mGarantiasContrato garantia with(Nolock)  " );
			query.append( " 	where nTipoGarantia="+GestionInterface.GARANTIA_ANTICIPO+" and nIdtipoProcesoGarantia="+GestionInterface.GARANTIA );
			query.append( " 	group by cIdContratoDefinitivo   " );
			query.append( " )subGA on subGA.cIdContratoDefinitivo=cont.cIdContratoDefinitivo   " );
			query.append( " left join (   " );
			query.append( " 	select 	sum(isnull(mMontoGarantia,0))totalCumplimiento 	   " );
			query.append( " 	,cIdContratoDefinitivo 	 " );
			query.append( " 	from mGarantiasContrato garantia with(Nolock)  " );
			query.append( " 	where nTipoGarantia="+GestionInterface.GARANTIA_CUMPLIMIENTO+" and nIdtipoProcesoGarantia="+GestionInterface.GARANTIA );
			query.append( " 	group by cIdContratoDefinitivo   " );
			query.append( " )subGC on subGC.cIdContratoDefinitivo=cont.cIdContratoDefinitivo  " );
			query.append( " left join (   " );
			query.append( " 	select 	sum(isnull(mMontoGarantia,0))totalViciosOcultos 	   " );
			query.append( " 	,cIdContratoDefinitivo 	 " );
			query.append( " 	from mGarantiasContrato garantia with(Nolock)  " );
			query.append( " 	where nTipoGarantia="+GestionInterface.GARANTIA_VICIOS_OCULTOS+" and nIdtipoProcesoGarantia="+GestionInterface.GARANTIA );
			query.append( " 	group by cIdContratoDefinitivo   " );
			query.append( " )subGV on subGV.cIdContratoDefinitivo=cont.cIdContratoDefinitivo " );
			query.append( " left join ( " );
			query.append( " 	select cIdContratoDefinitivo,lFianza,lcheque   " );
			query.append( " 	from mGarantiasContrato garantia with(Nolock) 	  " );
			query.append( " 	where nTipoGarantia="+GestionInterface.GARANTIA_CUMPLIMIENTO+" and nIdtipoProcesoGarantia="+GestionInterface.GARANTIA );
			query.append( " 	group by cIdContratoDefinitivo,lFianza,lcheque " );
			query.append( " ) garant on garant.cIdContratoDefinitivo=cont.cIdContratoDefinitivo	 " );
			query.append( "	where  cont.cIdContratoDefinitivo='"+cIdContratoDef+"'" );
			log.info(query);
			arrayObj=Util.datGuardados( conn, query.toString() );
			if(rutasArchGarantiaLib!=null && rutasArchGarantiaLib[0]!=null){
				arrayObj.getJSONObject( 0 ).put( "existeDoctoGarantiaLiberada", 1 );
			}
			jsonObj.put("datosContratoGarantia", arrayObj);
			arrayObj=null;
			query.delete( 0, query.length() );
			
			query.append( " select " );
			query.append( " cMotivoLiberacion " );
			query.append( " ,cOficioSolicitud nOficioSolicitud" );
			query.append( " ,convert(varchar,isnull(fFechaSolicitud,''),103) fFechaOficioSol" );
			query.append( " ,cOficioLiberacion nOficioLiberacion" );
			query.append( " ,convert(varchar,isnull(fFechaLiberacion,''),103) fFechaOficioLibera" );
			query.append( " ,lchequeEntregadoProveedor lChequeEntregado" );
			query.append( " from mGarantiacontratoLiberada lib with(Nolock)" );
			query.append( " where lib.cIdContratoDefinitivo='"+cIdContratoDef+"'");
			arrayObj=Util.datGuardados( conn, query.toString() );
			jsonObj.put("datosGarantiaLiberada", arrayObj);
			
			
			conn.commit();
			error=false;
			return jsonObj;
		} catch ( Exception e ) {
			log.error( "Bug, consulting data new garantias: " +e );
			throw new Exception( "Bug, consulting data garantias: " + e.toString());
		}finally {
			if ( error ) {
				if ( conn != null )
					try {
						conn.rollback();
					} catch ( Exception e ) {
						log.error( "Bug, Rollback: " + e );
						throw new Exception( "Bug, Rollback: " + e.toString(), e );
					}
			}
			CloseObject.closeObject( conn );
			arrayObj=null;
			if(query!=null) {
				query.delete( 0, query.length() );
			}
			query=null;
			arch=null;
			datosArchivo=null;
			rutasArchGarantiaLib=null;
		}
	}
	public void saveLiberaGarantiaContrato(LiberaGarantiaContrato libGarantia)throws Exception {
		Connection conn = null;
		Connection connContrato = null;
		boolean error=true;
		ContratacionFormalizadaManager manager=null;
		boolean existGarantia=false;
		Caso casoCont = null;
		Caso c=null;
		ContratosConGarantia contrato=null;
		String nameDocto=null;
		String cDBContrato="sai";
		GarantiaContrato garantia=null;
		try {
			conn=getConnection();
			manager=new ContratacionFormalizadaManager();
			garantia=new GarantiaContrato();
			contrato=new ContratosConGarantia();
			garantia.setcIdContratoDefinitivo( libGarantia.getcIdContratoDefinitivo() );
			garantia.setnTipoProcesoGarantia( GestionInterface.GARANTIA );
			contrato.setcIdContratoDefinitivo( libGarantia.getcIdContratoDefinitivo() );
			manager.contratosConGarantia( conn, contrato );
			nameDocto="LiberaGarantia";
			//validar si existe la captura de la garantia para poderla liberar
			existGarantia=manager.existGarantiaContrato( conn, garantia );
			if(!existGarantia) {
				throw new Exception( "No se puede hacer el guardado de la liberación de garantía porque aun no existen los datos de captura de la garantía.");
			}else {
				if(manager.existLiberaGarantiaContrato( conn, libGarantia )){//Existen datos de liberación
					manager.updateLiberaGarantiaContrato( conn, libGarantia );
					Util.bitacoraMovimientos( libGarantia.getcIdContratoDefinitivo(), "Actualiza datos de liberación de garantía", libGarantia.getcUsuarioActualiza(), conn );
				}else {
					manager.addLiberaGarantiaContrato( conn, libGarantia );
					Util.bitacoraMovimientos( libGarantia.getcIdContratoDefinitivo(), "Captura liberación de garantía", libGarantia.getcUsuarioActualiza(), conn );
				}
				//Upload File
				if(libGarantia.getArchivoStream()!=null) {
					cDBContrato=Util.getNameDB( conn, Integer.parseInt( contrato.getcEjercicioFiscal() ));
					connContrato=com.syc.gestion.util.Util.getSAIConnection( conn, cDBContrato, contrato.getcEjercicioFiscal() );
					if(connContrato==null) {
						throw new Exception( "No se genero la conección a la DB para la carga del archivo." );
					}
					// 
					com.syc.gestion.util.Util.copiaArchivo( new DataInputStream( libGarantia.getArchivoStream() ), libGarantia.getcNombreArchivoDestino() );
					c = new Caso();
					c.setFolio( contrato.getcFolioCaso() );
					casoCont=CasoManager.select( connContrato, c );
					if ( casoCont == null ) {
						throw new Exception( "No se encontro el caso del contrato." );
					}
					// 
					AdjuntaArchivoMasivoManager.adjuntaArchivo( connContrato, libGarantia.getcUsuarioCaptura(), casoCont.getTipoCaso().getGavetaAsociada(), casoCont.getIdGabinete(), "DoctosGarantia", nameDocto, new File( libGarantia.getcNombreArchivoDestino() ) );
					Util.bitacoraMovimientos( libGarantia.getcIdContratoDefinitivo(), "Guarda el documento", libGarantia.getcUsuarioCaptura(), conn );
					connContrato.commit();
				}
			}
			conn.commit();
			error=false;
		} catch ( Exception e ) {
			log.error( "Bug, Save Libera Garantia: " +e );
			throw new Exception( "Bug, Save Libera Garantia: " + e.toString());
		}finally {
			if ( error ) {
				if ( conn != null )
					try {
						conn.rollback();
					} catch ( Exception e ) {
						log.error( "Bug, Rollback: " + e );
						throw new Exception( "Bug, Rollback: " + e.toString(), e );
					}
				if ( connContrato != null )
					try {
						connContrato.rollback();
					} catch ( Exception e ) {
						log.error( "Bug, Rollback: " + e );
						throw new Exception( "Bug, Rollback: " + e.toString(), e );
					}
			}
			CloseObject.closeObject( conn );
			CloseObject.closeObject( connContrato );
			
			cDBContrato=null;
			garantia=null;
			contrato=null;
			manager=null;
			c=null;
			casoCont=null;
		}
	}
	public void saveGarantiaContrato(GarantiaContrato garantia)throws Exception {
		Connection conn = null;
		Connection connContrato = null;
		boolean error=true;
		ContratacionFormalizadaManager manager=null;
		boolean existGarantia=false;
		boolean existEndoso=false;
		Caso casoCont = null;
		Caso c=null;
		ContratosConGarantia contrato=new ContratosConGarantia();
		String nameDocto=null;
		String cDBContrato="sai";
		try {
			conn=getConnection();
			manager=new ContratacionFormalizadaManager();
			contrato.setcIdContratoDefinitivo( garantia.getcIdContratoDefinitivo() );
			manager.contratosConGarantia( conn, contrato );
			nameDocto="Garantia";
			//validar si es Garantia o endoso
			if(garantia.getnTipoProcesoGarantia() ==GestionInterface.GARANTIA) {
				for(int index=0;index<garantia.getListGarantia().size();index++) {
					//Validar que se capturo el dato de la aseguradora y el monto de la garantia
					garantia.getListGarantia().get( index ).setnIdConsecutivoEndosoGarantia( 1 );
					if(garantia.getListGarantia().get( index ).getmMontoGarantia()<=0.00 
							|| StringUtils.isBlank( garantia.getListGarantia().get( index ).getcAseguradora() )
							|| StringUtils.isBlank( garantia.getListGarantia().get( index ).getcNumeroChequeFianza() )
							|| StringUtils.isBlank( garantia.getListGarantia().get( index ).getfFechaExpedicion() )
							|| (garantia.getListGarantia().get( index ).getlCheque()==0 && garantia.getListGarantia().get( index ).getlFianza()==0)
					) {
						manager.deleteGarantiaContrato( conn, garantia, index );
						continue;
					}
					//Validar si ya existe una garantia
					existGarantia=manager.existGarantiaContrato( conn, garantia,index );
					if(existGarantia) {
						//Actualizar datos de la garantia
						manager.updateGarantiaContrato( conn, garantia,index );
						Util.bitacoraMovimientos( garantia.getcIdContratoDefinitivo(), "Actualiza datos de la garantía", garantia.getcUsuarioActualiza(), conn );
					}else {
						//insertar datos de la garantia
						manager.addGarantiaContrato( conn, garantia, index );
						Util.bitacoraMovimientos( garantia.getcIdContratoDefinitivo(), "Guarda datos de la garantía", garantia.getcUsuarioCaptura(), conn );
					}
					if(manager.existUsuarioCapGarantiaContrato( conn, garantia, index )) {
						manager.updateUsuarioCapGarantiaContrato( conn, garantia,index );
					}else {
						manager.addUsuarioCapGarantiaContrato( conn, garantia,index );
					}
				}
				manager.updateTotalGarantiaContrato( conn, garantia.getcIdContratoDefinitivo() );
			}else {
				//Validar si ya existe una garantia; puede haber haste 3 endosos para una garantia de cumplimiento
				nameDocto="Endoso";
				garantia.setnTipoProcesoGarantia( GestionInterface.GARANTIA );//Garantia
				existGarantia=manager.existGarantiaContrato( conn, garantia );
				garantia.setnTipoProcesoGarantia( GestionInterface.ENDOSO );//endoso
				if(existGarantia) {
					for(int index=0;index<garantia.getListGarantia().size();index++) {
						if(index!=1) {
							garantia.getListGarantia().get( index ).setcAseguradora( garantia.getListGarantia().get( 1 ).getcAseguradora() );
							garantia.getListGarantia().get( index ).setnTipoGarantia( GestionInterface.GARANTIA_CUMPLIMIENTO );
						}
						//Validar que se capturo el dato de la aseguradora y el monto de la garantia
						if( StringUtils.isBlank( garantia.getListGarantia().get( index ).getcNumeroChequeFianza() )
								|| StringUtils.isBlank( garantia.getListGarantia().get( index ).getfFechaExpedicion() )
								|| (garantia.getListGarantia().get( index ).getlCheque()==0 && garantia.getListGarantia().get( index ).getlFianza()==0)
						) {
							manager.deleteGarantiaContrato( conn, garantia, index );
							continue;
						}
						existEndoso=manager.existGarantiaContrato( conn, garantia,index );
						if(existEndoso) {
							//Actualizar datos del endoso
							manager.updateGarantiaContrato( conn, garantia,index );
							Util.bitacoraMovimientos( garantia.getcIdContratoDefinitivo(), "Actualiza datos del endoso #"+garantia.getListGarantia().get( index ).getnIdConsecutivoEndosoGarantia(), garantia.getcUsuarioActualiza(), conn );
						}else {
							//insertar datos del endoso
							manager.addGarantiaContrato( conn, garantia,index );
							Util.bitacoraMovimientos( garantia.getcIdContratoDefinitivo(), "Guarda datos del endoso #"+garantia.getListGarantia().get( index ).getnIdConsecutivoEndosoGarantia(), garantia.getcUsuarioCaptura(), conn );
						}
						if(manager.existUsuarioCapGarantiaContrato( conn, garantia,index )) {
							manager.updateUsuarioCapGarantiaContrato( conn, garantia,index );
						}else {
							manager.addUsuarioCapGarantiaContrato( conn, garantia,index );
						}
						manager.updateTotalGarantiaContrato( conn, garantia.getcIdContratoDefinitivo() );
					}
					
				}else {
					throw new Exception( "No se puede guardar un endoso sin antes haber una garantía.");
				}
			}
			//Upload File
			if(garantia.getArchivoStream()!=null) {
				cDBContrato=Util.getNameDB( conn, Integer.parseInt( contrato.getcEjercicioFiscal() ));
				connContrato=com.syc.gestion.util.Util.getSAIConnection( conn, cDBContrato, contrato.getcEjercicioFiscal() );
				if(connContrato==null) {
					throw new Exception( "No se genero la conección a la DB para la carga del archivo." );
				}
				// 
				com.syc.gestion.util.Util.copiaArchivo( new DataInputStream( garantia.getArchivoStream() ), garantia.getcNombreArchivoDestino() );
				c = new Caso();
				c.setFolio( contrato.getcFolioCaso() );
				casoCont=CasoManager.select( connContrato, c );
				if ( casoCont == null ) {
					throw new Exception( "No se encontro el caso del contrato." );
				}
				// 
				AdjuntaArchivoMasivoManager.adjuntaArchivo( connContrato, garantia.getcUsuarioCaptura(), casoCont.getTipoCaso().getGavetaAsociada(), casoCont.getIdGabinete(), "DoctosGarantia", nameDocto, new File( garantia.getcNombreArchivoDestino() ) );
				Util.bitacoraMovimientos( garantia.getcIdContratoDefinitivo(), "Guarda el documento", garantia.getcUsuarioCaptura(), conn );
				connContrato.commit();
			}
			
			conn.commit();
			error=false;
		} catch ( Exception e ) {
			log.error( "Bug, Save garantia: " +e );
			throw new Exception( "Bug, Save garantia: " + e.toString());
		}finally {
			if ( error ) {
				if ( conn != null )
					try {
						conn.rollback();
					} catch ( Exception e ) {
						log.error( "Bug, Rollback: " + e );
						throw new Exception( "Bug, Rollback: " + e.toString(), e );
					}
				if ( connContrato != null )
					try {
						connContrato.rollback();
					} catch ( Exception e ) {
						log.error( "Bug, Rollback: " + e );
						throw new Exception( "Bug, Rollback: " + e.toString(), e );
					}
			}
			CloseObject.closeObject( conn );
			CloseObject.closeObject( connContrato );
			manager=null;
			casoCont = null;
			c=null;
			contrato=null;
			nameDocto=null;
			cDBContrato=null;
			connContrato = null;
		}
	}
	public JSONObject queryDataContArt25() throws Exception {
		boolean error=true;
		Connection conn = null;
		StringBuilder query=null;
		JSONArray arrayObj=null;
		JSONObject jsonObj=null;
		try {
			conn=getConnection();
			query=new StringBuilder();
			jsonObj=new JSONObject();
			query.append( "SELECT  (cUnidadResponsable + ' - ' + D_DESCRIPCION) AS descrip, cUnidadResponsable AS cIdUnidadEjecutora FROM v_unidadEjecutoraPadre WITH(NOLOCK)" );
			log.info(query);
			arrayObj=Util.obtieneDatQuery(conn, query.toString());
			jsonObj.put("catalogoAreaResponsable", arrayObj);
			arrayObj=null;
			query.delete( 0, query.length() );
			
			query.append( "select 'Todo' cEstado,0 nIdEstado union select cEstado,nIdEstado from mCatalogoEstadoContrato with(Nolock) where nIdEstado<5" );
			log.info(query);
			arrayObj=Util.obtieneDatQuery(conn, query.toString());
			jsonObj.put("catalogoEstatus", arrayObj);
			arrayObj=null;
			query.delete( 0, query.length() );
			
			query.append( "select 'Todo' descrip,'' cIdTipoContrato  union select cIdTipoContrato+' - '+cTipoContrato as descrip,cIdTipoContrato from mCatalogoTipoContrato with(Nolock) where nActivo=1 and cIdTipoContrato in('CR','CS')" );
			log.info(query);
			arrayObj=Util.obtieneDatQuery(conn, query.toString());
			jsonObj.put("catalogoTipoContrato", arrayObj);
			conn.commit();
			error=false;
			return jsonObj;
		} catch ( Exception e ) {
			log.error( "Bug, consulting data new contract art 25: " +e );
			throw new Exception( "Bug, consulting data new contract cart 25: " + e.toString());
		}finally {
			if ( error ) {
				if ( conn != null )
					try {
						conn.rollback();
					} catch ( Exception e ) {
						log.error( "Bug, Rollback: " + e );
						throw new Exception( "Bug, Rollback: " + e.toString(), e );
					}
			}
			CloseObject.closeObject( conn );
			arrayObj=null;
			if(query!=null) {
				query.delete( 0, query.length() );
			}
			query=null;
		}
	}
	public JSONObject queryItemsContArt25(String cIdcontratoDef) throws Exception {
		boolean error=true;
		Connection conn = null;
		StringBuilder query=null;
		JSONArray arrayObj=null;
		JSONObject jsonObj=null;
		try {
			conn=getConnection();
			query=new StringBuilder();
			jsonObj=new JSONObject();
			query.append( "select  " );
			query.append( "'['+cont.cidUnidadEjecutora+'] '+upper(ue.D_DESCRIPCION) lblUnidadEjecutora " );
			query.append( ",cont.cIdContratoDefinitivo lblDefinitivo,cConceptoContrato " );
			query.append( ",'['+cont.cIdRFC+'] '+prov.cRazonSocial lblProveedor " );
			query.append( ",tipoCont.cTipoContrato lblTipoContrato " );
			query.append( ",'ESTADO: '+estadoCont.cEstado cEstado,'SubTotal: $ '+Convert(varchar,cont.mMontoBruto,1) lblSubtotal " );
			query.append( ",'IVA: $ '+Convert(varchar,cont.mMontoIVA,1)lblTotalIVA,'Monto Con IVA: $'+Convert(varchar,cont.mMontoNeto,1) lblTotalNeto " );
			query.append( ",nEsAbierto,cont.cIdEntidadContable,cont.nEsDescentralizado " );
			query.append( ",'Monto Máximo Con IVA: '+convert(varchar,part.totalMaximo,1) lblTotalNetoMax " );
			query.append( ",convert(varchar,part.totalMaximo,1) totalNetoMax " );
			query.append( ",convert(varchar,cont.mMontoNeto,1) mMontoActualContrato " );
			query.append( ",cont.cNoContratoCNET,cont.cIdTipoContrato,cont.nIdEstado nIdEstatus " );
			query.append( ",case when esCucopGasolina is null then 0 else 1 end esCucopGasolina,cEjercicio, cont.cIdUnidadEjecutora cIdUnidadEjecutoraCont " );
			query.append( ",'$'+convert(varchar,cont.mMontoNeto,1) mImporteTotal,case when cont.nIdEstado in(3,4 ) then '$'+convert(varchar,cont.mMontoNeto,1) else '$'+convert(varchar,0,1) end mComprometido " );
			query.append( ",cont.cIdRFC " );
			query.append( "from mContratoArt25 cont with(Nolock) " );
			query.append( "inner join mcatalogoProveedor as prov with(Nolock) " );
			query.append( "on prov.cIdRFC=cont.cIdRFC " );
			query.append( "inner join mcatalogoTipoContrato tipoCont with(Nolock) " );
			query.append( "on tipoCont.cIdTipoContrato=cont.cIdTipoContrato " );
			query.append( "inner join mcatalogoEstadoContrato estadoCont with(Nolock) " );
			query.append( "on estadoCont.nIdEstado=cont.nIdEstado " );
			query.append( "inner join ( " );
			query.append( "select cIdContratoDefinitivo,SUM(mMontoNetoMaximo)totalMaximo from mContratoArt25Partidas with(Nolock) " );
			query.append( "group by cIdContratoDefinitivo " );
			query.append( ")part on part.cIdContratoDefinitivo=cont.cIdContratoDefinitivo " );
			query.append( "inner join tCatUnidadEjecutora as ue with(Nolock) " );
			query.append( "on ue.cUnidadEjecutora=cont.cIdUnidadEjecutora " );
			query.append( "left join(select top(1) cIdContratoDefinitivo,cIdCABM esCucopGasolina " );
			query.append( "from mContratoArt25Partidas with(Nolock) where cIdSubPartida='26102' AND cIdCABM IN('26100013','26100024') " );
			query.append( ")cucop on cucop.cIdContratoDefinitivo=cont.cIdContratoDefinitivo " );
			query.append( "where cont.cIdContratoDefinitivo='"+cIdcontratoDef+"' " );
			
			arrayObj=Util.datGuardados(conn, query.toString());
			jsonObj.put("datosContratoArt25", arrayObj);
			conn.commit();
			error=false;
			return jsonObj;
		} catch ( Exception e ) {
			log.error( "Bug, consulting data new contract art 25: " +e );
			throw new Exception( "Bug, consulting data new contract art 25: " + e.toString());
		}finally {
			if ( error ) {
				if ( conn != null )
					try {
						conn.rollback();
					} catch ( Exception e ) {
						log.error( "Bug, Rollback: " + e );
						throw new Exception( "Bug, Rollback: " + e.toString(), e );
					}
			}
			CloseObject.closeObject( conn );
			arrayObj=null;
			if(query!=null) {
				query.delete( 0, query.length() );
			}
			query=null;
		}
	}
	public void addEp_tmp(DatosEP_TMP dat)throws Exception {
		Connection conn = null;
		boolean error=true;
		ContratacionFormalizadaManager manager=null;
		try {
			conn=getConnection();
			manager=new ContratacionFormalizadaManager();
			if(manager.existThisEP( conn, dat.getcIdContratoDefinitivo(),dat.getcIdClaveEgresos() )==0) {
				manager.addContratoEP_TEMP( conn, dat );
			}else {
				throw new Exception( "La EP  " + dat.getcIdClaveEgresos()+" ya se encuentra dada de alta");
			}
			conn.commit();
			error=false;
		} catch ( Exception e ) {
			log.error( "Bug, Add EP: " +e );
			throw new Exception( "Bug, Add EP: " + e.toString());
		}finally {
			if ( error ) {
				if ( conn != null )
					try {
						conn.rollback();
					} catch ( Exception e ) {
						log.error( "Bug, Rollback: " + e );
						throw new Exception( "Bug, Rollback: " + e.toString(), e );
					}
			}
			CloseObject.closeObject( conn );
			manager=null;
		}
	}
	public void addNewEps(DatosEP_TMP dat)throws Exception {
		Connection conn = null;
		boolean error=true;
		ContratacionFormalizadaManager manager=null;
		try {
			conn=getConnection();
			manager=new ContratacionFormalizadaManager();
			if(manager.existThisEP( conn, dat.getcIdContratoDefinitivo(),dat.getcIdClaveEgresos() )==0) {
				manager.addContratoEP_TEMP( conn, dat );
				manager.insertContratoEP( conn, dat.getcIdContratoDefinitivo() );
			}else {
				throw new Exception( "La EP  " + dat.getcIdClaveEgresos()+" ya se encuentra dada de alta");
			}
			conn.commit();
			error=false;
		} catch ( Exception e ) {
			log.error( "Bug, Add EP: " +e );
			throw new Exception( "Bug, Add EP: " + e.toString());
		}finally {
			if ( error ) {
				if ( conn != null )
					try {
						conn.rollback();
					} catch ( Exception e ) {
						log.error( "Bug, Rollback: " + e );
						throw new Exception( "Bug, Rollback: " + e.toString(), e );
					}
			}
			CloseObject.closeObject( conn );
			manager=null;
		}
	}
	public void aprovedExtensionContractArt25(ContratoArt25 cont,Usuario usuario)throws Exception {
		Connection conn = null;
		boolean error=true;
		ContratacionFormalizadaManager manager=null;
		ContratoDiversoConvenio conv=null;
		ContratoAmpliacion amp=null;
		try {
			conn=getConnection();
			manager=new ContratacionFormalizadaManager();
			conv=new ContratoDiversoConvenio();
			amp=new ContratoAmpliacion();
			//obtener datos del contrato
			manager.searchDataContArt25( conn, cont );
			amp.setcIdContratoDef( cont.getcIdContratoDef() );
			amp.setnIdConsecutivoAmpliacion( cont.getnIdConsecutivoAmpliacion() );
			manager.searchDataExtensionContArt25( conn, amp );
			
			conv.setcEjercicio( cont.getcEjercicio() );
			conv.setcIdEntidadContable( cont.getcIdEntidadContable() );
			conv.setcIdContrato( cont.getcIdContratoDef() );
			conv.setnConsecutivoModificacion( cont.getnIdConsecutivoAmpliacion() );
			conv.setfInicio( cont.getfFechaInicio() );
			conv.setfTermino( cont.getfFechaFin() );
			conv.setfFirmaContrato( cont.getfFechaFormalizacion() );
			conv.setcIdModificacion( cont.getcIdContratoDef()+"-AMP-"+cont.getnIdConsecutivoAmpliacion() );
			conv.setcOrigenRM( "CONTRATO ART 25 AMPLIACION" );
			conv.setfAdjudicacion( cont.getfFechaFallo() );
			conv.setnDocumentoAbierto( cont.getnEsAbierto() );
			//
			if(cont.getnIdEstado()!=ContractStatus.APPROVED) {
				throw new Exception( "Para poder hacer una ampliación el contrato debe de estar aprobado." );
			}
			if(amp.getnIdEstado()!=ContractStatus.CAPTURED) {
				throw new Exception( "Para poder aprobar una ampliación debe de estar en estatus de captura." );
			}
			manager.addContratoDiversoConvenio( conn, conv );
			manager.insertContratoEP( conn, cont.getcIdContratoDef(), cont.getnIdConsecutivoAmpliacion() );
			manager.updateEstateExtensionContractArt25( conn, cont.getcIdContratoDef(), ContractStatus.NO_BUDGET, cont.getnIdConsecutivoAmpliacion() );
			Util.bitacoraMovimientos( conv.getcIdModificacion(), "Ampliación de contrato Art 25 aprobada", usuario.getLogin(), conn );
			
			conn.commit();
			error=false;
		} catch ( Exception e ) {
			log.error( "Bug, AprovedExtensionContractArt25: " +e );
			throw new Exception( "Bug, AprovedExtensionContractArt25: " + e.toString());
		}finally {
			if ( error ) {
				if ( conn != null )
					try {
						conn.rollback();
					} catch ( Exception e ) {
						log.error( "Bug, Rollback: " + e );
						throw new Exception( "Bug, Rollback: " + e.toString(), e );
					}
			}
			CloseObject.closeObject( conn );
			manager=null;
			conv=null;
			amp=null;
		}
	}
	public void deleteExtensionContractArt25(ContratoArt25 cont,Usuario usuario)throws Exception {
		Connection conn = null;
		boolean error=true;
		ContratacionFormalizadaManager manager=null;
		ContratoDiversoConvenio conv=null;
		ContratoAmpliacion amp=null;
		try {
			conn=getConnection();
			manager=new ContratacionFormalizadaManager();
			conv=new ContratoDiversoConvenio();
			amp=new ContratoAmpliacion();
			//obtener datos del contrato
			manager.searchDataContArt25( conn, cont );
			amp.setcIdContratoDef( cont.getcIdContratoDef() );
			amp.setnIdConsecutivoAmpliacion( cont.getnIdConsecutivoAmpliacion() );
			manager.searchDataExtensionContArt25( conn, amp );
			
			conv.setcEjercicio( cont.getcEjercicio() );
			conv.setcIdEntidadContable( cont.getcIdEntidadContable() );
			conv.setcIdContrato( cont.getcIdContratoDef() );
			conv.setnConsecutivoModificacion( cont.getnIdConsecutivoAmpliacion() );
			conv.setfInicio( cont.getfFechaInicio() );
			conv.setfTermino( cont.getfFechaFin() );
			conv.setfFirmaContrato( cont.getfFechaFormalizacion() );
			conv.setcIdModificacion( cont.getcIdContratoDef()+"-AMP-"+cont.getnIdConsecutivoAmpliacion() );
			conv.setcOrigenRM( "CONTRATO ART 25 AMPLIACION" );
			conv.setfAdjudicacion( cont.getfFechaFallo() );
			conv.setnDocumentoAbierto( cont.getnEsAbierto() );
			if(amp.getnIdEstado()!=ContractStatus.NO_BUDGET) {
				throw new Exception( "Para poder devolver la ampliación debe de estar en estatus de EN SAI SIN PRESUPUESTO." );
			}
			manager.deleteContratoDiversoConvenio( conn, conv.getcIdContrato(), conv.getnConsecutivoModificacion(), conv.getcIdModificacion() );//(Connection conn,String cIdContratoDef,int nConsecutivoMod,String cIdModificacion) 
			manager.updateEstateExtensionContractArt25( conn, cont.getcIdContratoDef(), ContractStatus.CAPTURED, cont.getnIdConsecutivoAmpliacion() );
			Util.bitacoraMovimientos( conv.getcIdModificacion(), "Ampliación de contrato Art 25 devuelta", usuario.getLogin(), conn );
			
			conn.commit();
			error=false;
		} catch ( Exception e ) {
			log.error( "Bug, AprovedExtensionContractArt25: " +e );
			throw new Exception( "Bug, AprovedExtensionContractArt25: " + e.toString());
		}finally {
			if ( error ) {
				if ( conn != null )
					try {
						conn.rollback();
					} catch ( Exception e ) {
						log.error( "Bug, Rollback: " + e );
						throw new Exception( "Bug, Rollback: " + e.toString(), e );
					}
			}
			CloseObject.closeObject( conn );
			manager=null;
			conv=null;
			amp=null;
		}
	}
	public void apruebaContratoArt25(ContratoArt25 cont,Usuario usuario) throws Exception{
		Connection conn = null;
		ContratacionFormalizadaManager manager=null;
		Caso caso=null;
		String folioCaso="";
		int folio=-1;
		
		try {
			conn=getConnection();
			manager=new ContratacionFormalizadaManager();
			//obtener datos del contrato
			manager.searchDataContArt25( conn, cont );
			//Valida que esté en captura para poder aprobar
			if(cont.getnIdEstado()!=ContractStatus.CAPTURED) {
				throw new Exception( "El estatus del contrato debe estar en estatus de captura." );
			}
			if(manager.arethereEP( conn, cont.getcIdContratoDef() )==0) {
				throw new Exception( "Para poder aprobar el contrato almenos debe de agregar una EP." );
			}
			//Generar caso
			caso=Util.generaGuardaCaso(usuario.getU_UR(),  (GestionInterface.IDTC_CONTRATO_DIVERSO+""), "Aprueba contrato art25", usuario, cont.getJndiName(), cont.getcEjercicio());
			folioCaso = caso.getFolio();
			int indice = folioCaso.lastIndexOf('-') + 1;
			folio = Integer.parseInt(folioCaso.substring(indice));
			//Avanza el caso
			Util.avanzaCaso(caso, usuario, cont.getcPrefixPath(), new String[] {"CONSULTA_CONTRATODIVERSO"}, new String[] {"consulta_contrato"}, cont.getJndiName());
			//Actualiza el caso en pcontratodiverso y mcontratoremanente
			cont.setnIdEstado( ContractStatus.NO_BUDGET );
			manager.updateEstatusContratoArt25( conn, cont );
			//Actualiza folio del caso en pcontratodiv y mcontratoremanente
			cont.setCfolio( folioCaso );
			cont.setnConsecutivoCDIV( folio );
			manager.updateFolioCasoContratoArt25( conn, cont );
			manager.addContratoDiverso( conn, cont );
			//Add EP a tcontratoEP
			manager.insertContratoEP( conn, cont.getcIdContratoDef() );
			//Cambiar el estatus del contrato a aprobado
			Util.bitacoraMovimientos( cont.getcIdContratoDef(), "Contrato art25 aprobado", usuario.getLogin(), conn );
			conn.commit();
		} catch ( SQLException e ) {
			if(conn!=null) {
				conn.rollback();
			}
			throw(e);
		}finally {
			CloseObject.closeObject(conn, false);
			conn=null;
			manager=null;
			caso=null;
			folioCaso=null;
			cont=null;
		}
	}
	
	public void devuelveContratoArt25(ContratoArt25 cont,String cLogin) throws Exception {
		boolean error=true;
		Connection conn = null;
		ContratacionFormalizadaManager manager=null;
		
		try {
			conn=getConnection();
			manager=new ContratacionFormalizadaManager();
			
			cont.setnIdEstado( ContractStatus.CAPTURED );
			manager.updateEstatusContratoArt25( conn, cont );
			manager.deleteContratoDiverso( conn, cont.getcIdContratoDef() );
			Util.bitacoraMovimientos( cont.getcIdContratoDef(), "Se devuelve el contrato Art25", cLogin, conn );
			conn.commit();
			error=false;
		} catch ( Exception e ) {
			log.error( "Bug, devuelveConvenioCap4: " +e );
			throw new Exception( "Bug, devuelveConvenioCap4: " + e.toString());
		}finally {
			if ( error ) {
				if ( conn != null )
					try {
						conn.rollback();
					} catch ( Exception e ) {
						log.error( "Bug, Rollback: " + e );
						throw new Exception( "Bug, Rollback: " + e.toString(), e );
					}
			}
			CloseObject.closeObject( conn );
			manager=null;
			cont=null;
		}
	}
	public void cancelaPrecompromisoContArt25(ContratoArt25 cont,HttpServletRequest request,Usuario usuario) throws Exception{
		Connection conn = null;
		ContratacionFormalizadaManager manager=null;
		boolean error=true;
		try {
			conn=getConnection();
			manager=new ContratacionFormalizadaManager();
			//Obtener los datos del Contrato
			manager.searchDataContArt25( conn, cont );
			//Validar el estatus del contrato
			if(cont.getnIdEstado()!=ContractStatus.BUDGET) {
				throw new Exception( "El estatus del contrato debe estar en SAI PRESUPUESTADO para poder cancelar el precompromiso." );
			}
			//Validar si ya hay precompromiso
			if(!Util.hayPrecompromiso(conn, cont.getcIdContratoDef())){
				throw new Exception( "No hay precompromiso para cancelar." );
			}
			//Cancelar precompromiso
			cancelaPrecompromiso( conn, request, usuario, cont.getcIdContratoDef(), cont.getcPrefixPath(), cont.getJndiName() );
			//Actualizar estatus del contrato
			cont.setnIdEstado( ContractStatus.NO_BUDGET );
			manager.updateEstatusContratoArt25( conn, cont );
			//Guardaar en bitacora los movimientos
			Util.bitacoraMovimientos(cont.getcIdContratoDef(), "Cancelación de precompromiso para contratos art25", usuario.getLogin(), conn);
			
			conn.commit();
			error=false;
		} catch ( Exception e ) {
			throw new Exception( "Bug, Cancela Precompromiso Contrato Art 25 : " +e );
		}finally {
			if ( error ) {
				if ( conn != null )
					try {
						conn.rollback();
					} catch ( Exception e ) {
						log.error( "Bug, Rollback: " + e );
						throw new Exception( "Bug, Rollback: " + e.toString(), e );
					}
			}
			CloseObject.closeObject( conn );
			manager=null;			
		}
	}
	public void cancelaPrecompromisoConvenio(ContratoModificado conv,HttpServletRequest request,Usuario usuario) throws Exception{
		Connection conn = null;
		ContratacionFormalizadaManager manager=null;
		boolean error=true;
		try {
			conn=getConnection();
			manager=new ContratacionFormalizadaManager();
			//Obtener los datos del Contrato
			manager.searchDataConvenio( conn, conv );
			//Validar el estatus del contrato
			if(conv.getnEstado()!=ContractStatus.BUDGET) {
				throw new Exception( "El estatus del convenio debe estar en SAI PRESUPUESTADO para poder cancelar el precompromiso." );
			}
			//Validar si ya hay precompromiso
			if(!Util.hayPrecompromiso(conn, conv.getcIdContratoDefinitivo()+"#M"+conv.getnConsecutivoModificacion() ) ){
				throw new Exception( "No hay precompromiso para cancelar." );
			}
			//Cancelar precompromiso
			cancelaPrecompromiso( conn, request, usuario,conv.getcIdContratoDefinitivo()+"#M"+conv.getnConsecutivoModificacion(), conv.getPrefixPath(), conv.getJndiName() );
			//Actualizar estatus del contrato
			conv.setnEstado( ContractStatus.NO_BUDGET );
			manager.updateStateConvenio( conn, conv );
			conv.setnFolioPreCompromiso( 0 );
			manager.updateFolioPrecompromisoConvenio( conn, conv );
			//Guardaar en bitacora los movimientos
			Util.bitacoraMovimientos(conv.getcIdContratoDefinitivo()+"#M"+conv.getnConsecutivoModificacion(), "Cancelación de precompromiso para convenio ", usuario.getLogin(), conn);
			
			conn.commit();
			error=false;
		} catch ( Exception e ) {
			throw new Exception( "Bug, Cancela Precompromiso Convenio: " +e );
		}finally {
			if ( error ) {
				if ( conn != null )
					try {
						conn.rollback();
					} catch ( Exception e ) {
						log.error( "Bug, Rollback: " + e );
						throw new Exception( "Bug, Rollback: " + e.toString(), e );
					}
			}
			CloseObject.closeObject( conn );
			manager=null;			
		}
	}
	private void cancelaPrecompromiso(Connection conn,HttpServletRequest request,Usuario usuario,String cIdContratoDefinitivo,String prefixPath,String jndiName) throws Exception{
		Caso c=null;
		String ueOriginal=usuario.getU_UR();
	 	String cCentroContableOrig = usuario.getPropiedad("CCENTROCONTABLE").getValor();
	 	CambiaPropiedadesUsuario cpu=null;
	 	PreparedStatement pstmt=null;
	 	String query=null;
	 	ResultSet rs=null;
	 	Caso sc=null;
	 	int id_caso=0;
	 	int nFolioPrecom=0;
	 	Map  <String, String> m=null;
	 	AplicarContableReturn acr=null;
	 	ContableInterface conInt=null;
	 	Caso scc=null;
	 	String sqlDoc=null;
	 	Statement stmDoc=null,stmRel=null;
	 	String sqlRel=null;
		try {
			cpu=new CambiaPropiedadesUsuario();
			conInt = new AplicacionContable();
			query="select enc.nFolioPreCompromiso,det.cCentroContable,enc.cUnidadResponsable,caso.ID_CASO,mdoc.C_FOLIO_PRE "
					+" from tPreCompromisoEncabezado as enc with(Nolock) "  
					+" inner join tPreCompromisoDetalle as det with(Nolock) on enc.nFolioPreCompromiso=det.nFolioPreCompromiso "  
					+" and enc.cDocumentoHaplicado='S' and enc.cIdContrato='"+cIdContratoDefinitivo+"' "
					+" inner join mDocumentoFolio as mdoc with(Nolock) on mdoc.ConsecutivoPRECOMP=enc.nFolioPreCompromiso and mdoc.cIdUnidadResponsable=enc.cUnidadResponsable "
					+" and det.cCentroContable=mdoc.cCentroContable "
					+" inner join CG_CASO as caso with(nolock) on caso.C_FOLIO=mdoc.C_FOLIO_PRE "
					+" inner join CG_CASO_OPERACION as oper with(Nolock) on oper.ID_CASO=caso.ID_CASO "
					+" group by enc.nFolioPreCompromiso,det.cCentroContable,enc.cUnidadResponsable,caso.ID_CASO,mdoc.C_FOLIO_PRE";
			log.info(query);
			pstmt = conn.prepareStatement(query);
			rs = pstmt.executeQuery();
			while(rs.next()){
				//Cambia el centro contable al usuario
				cpu.cambiaCentroContableUE(rs.getString("cUnidadResponsable"), rs.getString("cCentroContable"), usuario);
				//Se obtiene el cso
				sc = new Caso();
				id_caso=rs.getInt("ID_CASO");
				nFolioPrecom=rs.getInt("nFolioPreCompromiso");
				
				sc.setIdCaso(id_caso);
				c = CasoManager.select(conn, sc);
				if(c!=null){
					m = CasoDatoManager.readValuesCasoDato(request, c.getCasoDato(), true);
					acr = conInt.cancelarAppContableNueva(conn, c, "", "", "", 0, "", m, prefixPath, usuario.getLogin(), "");
					if (!acr.isSuccess()) {
						CloseObject.closeObject( rs );
						CloseObject.closeObject( pstmt );
						CloseObject.closeObject( stmDoc );
						CloseObject.closeObject( stmRel );
						throw new Exception( "Bug, en la cancelación de aplicación contable del precompromiso." );
					}
					//Elinar los datos de las tablas mDocumentoFolio, mRelPedContPrecomComp
					sqlDoc="delete mDocumentoFolio where cIdDocumentoDefinitivo='"+cIdContratoDefinitivo+"' and ConsecutivoPRECOMP="+nFolioPrecom;
					log.info(sqlDoc);
					stmDoc = conn.createStatement();
					stmDoc.executeUpdate(sqlDoc);
					sqlRel="delete mRelPedContPrecomComp where cIdPedContDef='"+cIdContratoDefinitivo+"' and nConsecutivoPrecom="+nFolioPrecom;
					log.info(sqlRel);
					stmRel = conn.createStatement();
					stmRel.executeUpdate(sqlRel);
					//Recargando el caso
					scc = new Caso();
					scc.setIdCaso(c.getIdCaso());
					c = CasoManager.select(conn, scc);
					String [] responsable=new String[] { "CONSULTA_PRECOMPROMISO" };//VENTANILLA_PRECOMPROMISO
					String [] nombre=new String[] { "consulta_precomp" };//autoriza_precomp
					Util.avanzaCaso(request, c, usuario, prefixPath, responsable,nombre,jndiName);
					log.debug(c.getCasoDato("APLICADO_CONT").getValor());
				}
			}
			
		} catch ( Exception e ) {
			throw new Exception( "Bug, Cancela Precompromiso: " +e );
		}finally {
			cpu.cambiaCentroContableUE(ueOriginal, cCentroContableOrig, usuario);
			c=null;
			sc=null;
			cpu=null;
			CloseObject.closeObject( rs );
			CloseObject.closeObject( pstmt );
			CloseObject.closeObject( stmDoc );
			CloseObject.closeObject( stmRel );
			m=null;
			acr=null;
			scc=null;
			sqlDoc=null;
			sqlRel=null;
		}
	}
	public void autorizaContArt25(ContratoArt25 cont,HttpServletRequest request,Usuario usuario) throws Exception{
		Connection conn = null;
		ContratacionFormalizadaManager manager=null;
		boolean error=true;
		String param[]=new String[6];
		CallableStatement cmst = null;
		try {
			conn=getConnection();
			manager=new ContratacionFormalizadaManager();
			param[0]=cont.getcIdContratoDef();
			param[1]="N";
			param[2]=Util.obtieneEjercicioFiscalActivo( conn );
			param[3]="Aplicación de Compromiso";
			//Obtener los datos del Contrato
			manager.searchDataContArt25( conn, cont );
			//Valida que esté en SAI  PRESUPUESTADO
			if(cont.getnIdEstado()!=ContractStatus.BUDGET) {
				throw new Exception( "El estatus del contrato debe estar en SAI PRESUPUESTADO para poder autorizarlo." );
			}
			//Validar si hay compromisos por autorizar en sicop
			if(Util.hayCompromisoPendienteAutSICOP(conn, cont.getcIdContratoDef())){
				throw new Exception( "Hay compromisos pendientes por autorizar en SICOP." );
				
			}
			//retenciones
			cmst = conn.prepareCall("{call pa_mAgreRetenAutomaticoPLU (?,?,?,?)}");
			cmst.setString(1, param[2]);
			cmst.setString(2, cont.getcIdRFC());
			cmst.setString(3, cont.getcIdContratoDef());
			cmst.setString(4, cont.getcIdEntidadContable());
			cmst.execute();
			//Autorizar compromiso
			compromete( conn, request, usuario, param, cont.getJndiName(), cont.getcPrefixPath() );
			//Actualizar estatus del contrato
			cont.setnIdEstado( ContractStatus.APPROVED );
			manager.updateEstatusContratoArt25( conn, cont );
			//Guardaar en bitacora los movimientos
			Util.bitacoraMovimientos(cont.getcIdContratoDef(), "Autorización de contrato art25", usuario.getLogin(), conn);
			
			conn.commit();
			error=false;
		} catch ( Exception e ) {
			throw new Exception( "Bug, Autoriza Contrato Art 25: " +e );
		}finally {
			if ( error ) {
				if ( conn != null )
					try {
						conn.rollback();
					} catch ( Exception e ) {
						log.error( "Bug, Rollback: " + e );
						throw new Exception( "Bug, Rollback: " + e.toString(), e );
					}
			}
			CloseObject.closeObject( conn );
			CloseObject.closeObject( cmst );
			
			manager=null;
			param=null;
		}
	}
	private void compromete(Connection conn,HttpServletRequest request,Usuario usuario,String param[],String jndiName,String prefixPath) throws Exception{
		PreparedStatement pstmt=null;
		CambiaPropiedadesUsuario cpu=null;
		AplicarContableReturn acr=null;
		ContableInterface conInt = null;
		String query=null;
		ResultSet rs=null;
		String folioComp=null;
		Caso caso=null;
		int retVal=0;
		ConfiguraAplicativoBusinessLogic configApp=null;
		Map  <String, String> m=null;
		String ueOriginal=usuario.getU_UR();
	 	String cCentroContableOrig = usuario.getPropiedad("CCENTROCONTABLE").getValor();
	 	List<Integer> listFoliosComp = null;
		try {
			cpu=new CambiaPropiedadesUsuario();
			conInt = new AplicacionContable();
			query="select pe.nFolioPreCompromiso,rpc.cFolioPrecom "+
					",pe.cCentroContable "+
					",cUnidadResponsable from tPreCompromisoEncabezado pe with(Nolock) "+
					"inner join mRelPedContPrecomComp as rpc with(Nolock) on pe.cIdContrato=rpc.cIdPedContDef "+ 
					"and pe.nFolioPreCompromiso=rpc.nConsecutivoPrecom and pe.cDocumentoHaplicado='S' and pe.cIdContrato=?";
			log.info(query);
			pstmt = conn.prepareStatement(query);
		 	pstmt.setString(1, param[0]);
		 	rs = pstmt.executeQuery();
		 	configApp = new ConfiguraAplicativoBusinessLogic(GestionInterface.ATT_CONEXION);
		 	boolean esSAIAlterno = "true".equals(configApp.getSystemSetting("SAI_AMBIENTAL"))|| "true".equals(configApp.getSystemSetting("SAI_FONDEN"));
		 	listFoliosComp = new ArrayList<>();
		 	while(rs.next()){
		 		cpu.cambiaCentroContableUE(rs.getString("cUnidadResponsable"), rs.getString("cCentroContable"), usuario);
		 		//folioComp=generaGuardaCaso1(request, response,session,(GestionInterface.IDTC_COMPROMISO+""),"Aplicación de Compromiso");
		 		caso=Util.generaGuardaCaso(usuario.getU_UR(),  (GestionInterface.IDTC_COMPROMISO+""), param[3], usuario, jndiName, param[2]);
		 		folioComp = caso.getFolio().substring(caso.getFolio().lastIndexOf("-") + 1);
		 		listFoliosComp.add(Integer.parseInt( folioComp ));
				param[4]=folioComp;
				param[5]=caso.getFolio();
				//Crear encabezado y detalle
				//String cIdContratoDefinitivo, int nFolioPrecom, int nFolioComp, String isRadicado, String folioCasoCompromiso, Usuario usuario, Connection conn
				retVal=Util.creaEncabezadoDetalleComp(param[0], rs.getInt("nFolioPreCompromiso"), Integer.parseInt( folioComp ), param[1], param[5], usuario, conn);
				Util.updateRelacionPrecomCompromiso( conn, caso.getFolio(), Integer.parseInt( folioComp ), param[0], rs.getString("cFolioPrecom"), rs.getInt("nFolioPreCompromiso") );
				if(retVal!=0){
					CloseObject.closeObject( rs );
				 	CloseObject.closeObject( pstmt );
					throw new Exception("Error al crear el encabezado y detalle."  );
				}
				if(!Util.esRecursoFiscal(conn, param[0]) || esSAIAlterno){
					//Aplicación contable
					m = CasoDatoManager.readValuesCasoDato(request, caso.getCasoDato(), true);
					acr= conInt.aplicarContableNuevo(conn, caso, "", "", "", 0, "",m,prefixPath,usuario.getLogin(),"");
					if (!acr.isSuccess()) {
						CloseObject.closeObject( rs );
					 	CloseObject.closeObject( pstmt );
						throw new Exception("Error en la aplicación contable"  );
					}
				}
				// Una vez que ha hecho la aplicación contable avanza el caso
				Util.avanzaCaso(request, caso, usuario, prefixPath, new String[] { "CONSULTA_PAGOS" },new String[] { "consulta_compromiso" },jndiName);
				log.debug(caso.getCasoDato("APLICADO_CONT").getValor());
		 	}
		 	if(listFoliosComp.size()>1) {
		 		Util.integraFoliosCompromiso( conn, param[0], param[2] );
			}
		} catch ( Exception e ) {
			throw new Exception( "Bug, Compromiso: " +e );
		}finally {
			cpu.cambiaCentroContableUE(ueOriginal, cCentroContableOrig, usuario);
			CloseObject.closeObject( rs );
			CloseObject.closeObject( pstmt );
			
			acr=null;
			cpu=null;
			conInt = null;
			query=null;
			folioComp=null;
			caso=null;
			configApp=null;
			m=null;
			if(listFoliosComp!=null)
				listFoliosComp.clear();
			
			listFoliosComp=null;
		}
	}
	public void autorizaConvenio(ContratoModificado cont,HttpServletRequest request,Usuario usuario) throws Exception{
		Connection conn = null;
		ContratacionFormalizadaManager manager=null;
		boolean error=true;
		String param[]=new String[6];
		CallableStatement cmst = null;
		Proveedor p=null;
		try {
			conn=getConnection();
			manager=new ContratacionFormalizadaManager();
			param[0]=cont.getcIdContratoDefinitivo()+"#M"+cont.getnConsecutivoModificacion();
			param[1]="N";
			param[2]=Util.obtieneEjercicioFiscalActivo( conn );
			param[3]="REGISTRO DEL CONTRATO FOLIO "+cont.getcIdContratoDefinitivo()+"#M"+cont.getnConsecutivoModificacion();
			cont.setcEjercicio( param[2] );
			cont.setcCentroContable( usuario.getPropiedad("CCENTROCONTABLE").getValor() );
			//Obtener los datos del Contrato
			manager.searchDataConvenio( conn, cont );
			//Valida que esté en SAI  PRESUPUESTADO
			if(cont.getnEstado()!=ContractStatus.BUDGET) {
				throw new Exception( "El estatus del convenio debe estar en SAI PRESUPUESTADO para poder autorizarlo." );
			}
			//Validar si hay compromisos por autorizar en sicop
			if(Util.hayCompromisoPendienteAutSICOP(conn, cont.getcIdContratoDefinitivo())){
				throw new Exception( "Hay compromisos pendientes por autorizar en SICOP." );
				
			}
			//retenciones
			if(cont.getnConvenioEjercicioAnt()==1) {
				p=ProveedorDAO.selectByRfc( conn, cont.getcIdRFC() );
				manager.addRetencionConvenio( conn, cont, p.getnPersonaFisica(), p.getnPersonaMoral(), p.getEsResico() );
			}
			//Autorizar compromiso
			compromete( conn, request, usuario, param, cont.getJndiName(), cont.getPrefixPath() );
			//Actualizar estatus del contrato
			cont.setnEstado( ContractStatus.APPROVED );
			manager.updateStateConvenio( conn, cont );
			//Guardaar en bitacora los movimientos
			Util.bitacoraMovimientos(cont.getcIdContratoDefinitivo()+"#M"+cont.getnConsecutivoModificacion(), "Autorización de convenio ", usuario.getLogin(), conn);
			
			error=false;
			conn.commit();
		} catch ( Exception e ) {
			throw new Exception( "Bug, Autoriza Contrato Art 25: " +e );
		}finally {
			if ( error ) {
				if ( conn != null )
					try {
						conn.rollback();
					} catch ( Exception e ) {
						log.error( "Bug, Rollback: " + e );
						throw new Exception( "Bug, Rollback: " + e.toString(), e );
					}
			}
			CloseObject.closeObject( conn );
			CloseObject.closeObject( cmst );
			
			manager=null;
			param=null;
		}
	}
	public void generaPrecompromisoAmpContArt25(ContratoArt25 cont,HttpServletRequest request,Usuario usuario) throws Exception{
		boolean error=true;
		Connection conn = null;
		ContratacionFormalizadaManager manager=null;
		String param[]=new String[6];
		String cEevento="PRECOM";
		ContratoDiversoConvenio conv=null;
		ContratoAmpliacion amp=null;
		try {
			conn=getConnection();
			manager=new ContratacionFormalizadaManager();
			conv=new ContratoDiversoConvenio();
			amp=new ContratoAmpliacion();
			//obtener datos del contrato
			manager.searchDataContArt25( conn, cont );
			amp.setcIdContratoDef( cont.getcIdContratoDef() );
			amp.setnIdConsecutivoAmpliacion( cont.getnIdConsecutivoAmpliacion() );
			manager.searchDataExtensionContArt25( conn, amp );
			
			conv.setcEjercicio( cont.getcEjercicio() );
			conv.setcIdEntidadContable( cont.getcIdEntidadContable() );
			conv.setcIdContrato( cont.getcIdContratoDef() );
			conv.setnConsecutivoModificacion( cont.getnIdConsecutivoAmpliacion() );
			conv.setfInicio( cont.getfFechaInicio() );
			conv.setfTermino( cont.getfFechaFin() );
			conv.setfFirmaContrato( cont.getfFechaFormalizacion() );
			conv.setcIdModificacion( cont.getcIdContratoDef()+"-AMP-"+cont.getnIdConsecutivoAmpliacion() );
			conv.setcOrigenRM( "CONTRATO ART 25 AMPLIACION" );
			conv.setfAdjudicacion( cont.getfFechaFallo() );
			conv.setnDocumentoAbierto( cont.getnEsAbierto() );
			if(amp.getnIdEstado()!=ContractStatus.NO_BUDGET) {
				throw new Exception( "Para precomprometer el recurso la ampliación debe de estar en estatus de EN SAI SIN PRESUPUESTO." );
			}
			//Validar si ya hay precompromiso
			if(Util.hayPrecompromiso(conn, conv.getcIdModificacion())){
				throw new Exception( "La ampliación del contrato ya tiene un precompromiso." );
			}
			//Precomprometer
			param[0]=conv.getcIdModificacion();
			param[1]="N";
			param[2]=Util.obtieneEjercicioFiscalActivo( conn );
			param[3]="Precompromiso Contratos Art 25 Ampliación de Máximos";
			if(cont.getnEsDescentralizado()==0) {
				precompromisoCentralizado( conn, request, usuario, param, cont.getcPrefixPath(), cont.getJndiName(), cEevento );
			}else {
				precompromisoDescentralizado( conn, request, usuario, param, cont.getcPrefixPath(), cont.getJndiName(),cEevento );
			}
			manager.updateFolioPrecomExtensionContractArt25( conn, amp.getcIdContratoDef(), param[5], cont.getnIdConsecutivoAmpliacion(),Integer.parseInt( param[4]));
			//Actualizar estatus de la mpliación de contrato
			manager.updateEstateExtensionContractArt25( conn, cont.getcIdContratoDef(), ContractStatus.BUDGET, cont.getnIdConsecutivoAmpliacion() );
			//Guardaar en bitacora los movimientos
			Util.bitacoraMovimientos(param[0], "Generación de precompromiso para ampliación de contratos art25 con Folio="+param[4], usuario.getLogin(), conn);
			
			conn.commit();
			error=false;
		} catch ( Exception e ) {
			log.error( "Bug, Genera Precompromiso: " +e );
			throw new Exception( "Bug, Genera Precompromiso: " + e.toString());
		}finally {
			if ( error ) {
				if ( conn != null )
					try {
						conn.rollback();
					} catch ( Exception e ) {
						log.error( "Bug, Rollback: " + e );
						throw new Exception( "Bug, Rollback: " + e.toString(), e );
					}
			}
			CloseObject.closeObject( conn );
			cEevento=null;
			conn = null;
			manager=null;
			param=null;
			conv=null;
			amp=null;
		}
	}
	public void cancelaPrecompromisoAmpContArt25(ContratoArt25 cont,HttpServletRequest request,Usuario usuario) throws Exception{
		boolean error=true;
		Connection conn = null;
		ContratacionFormalizadaManager manager=null;
		ContratoAmpliacion amp=null;
		try {
			conn=getConnection();
			manager=new ContratacionFormalizadaManager();
			
			amp=new ContratoAmpliacion();
			//obtener datos del contrato
			manager.searchDataContArt25( conn, cont );
			amp.setcIdContratoDef( cont.getcIdContratoDef() );
			amp.setnIdConsecutivoAmpliacion( cont.getnIdConsecutivoAmpliacion() );
			manager.searchDataExtensionContArt25( conn, amp );
			
			if(amp.getnIdEstado()!=ContractStatus.BUDGET) {
				throw new Exception( "Para poder devolver la ampliación debe de estar en estatus de EN SAI PRESUPUESTADO." );
			}
			//Validar si ya hay precompromiso
			if(!Util.hayPrecompromiso(conn, cont.getcIdContratoDef())){
				throw new Exception( "No hay precompromiso para cancelar." );
			}
			//Cancelar precompromiso
			cancelaPrecompromiso( conn, request, usuario, cont.getcIdContratoDef()+"-AMP-"+cont.getnIdConsecutivoAmpliacion(), cont.getcPrefixPath(), cont.getJndiName() );
			manager.updateFolioPrecomExtensionContractArt25( conn, cont.getcIdContratoDef(),"", cont.getnIdConsecutivoAmpliacion(),0);
			//Actualizar estatus de la mpliación de contrato
			manager.updateEstateExtensionContractArt25( conn, cont.getcIdContratoDef(), ContractStatus.NO_BUDGET, cont.getnIdConsecutivoAmpliacion() );
			//Guardaar en bitacora los movimientos
			Util.bitacoraMovimientos(cont.getcIdContratoDef()+"-AMP-"+cont.getnIdConsecutivoAmpliacion(), "Cancelación de precompromiso para ampliación de contratos art25", usuario.getLogin(), conn);
			
			conn.commit();
			error=false;
		} catch ( Exception e ) {
			log.error( "Bug, Genera Precompromiso: " +e );
			throw new Exception( "Bug, Genera Precompromiso: " + e.toString());
		}finally {
			if ( error ) {
				if ( conn != null )
					try {
						conn.rollback();
					} catch ( Exception e ) {
						log.error( "Bug, Rollback: " + e );
						throw new Exception( "Bug, Rollback: " + e.toString(), e );
					}
			}
			CloseObject.closeObject( conn );
			conn = null;
			manager=null;
			amp=null;
		}
	}
	public void autorizaAmpContArt25(ContratoArt25 cont,HttpServletRequest request,Usuario usuario) throws Exception{
		Connection conn = null;
		ContratacionFormalizadaManager manager=null;
		boolean error=true;
		String param[]=new String[6];
		CallableStatement cmst = null;
		ContratoAmpliacion amp=null;
		ContratoDiversoConvenio conv=null;
		try {
			conn=getConnection();
			manager=new ContratacionFormalizadaManager();
			amp=new ContratoAmpliacion();
			conv=new ContratoDiversoConvenio();
			param[0]=cont.getcIdContratoDef()+"-AMP-"+cont.getnIdConsecutivoAmpliacion();
			param[1]="N";
			param[2]=Util.obtieneEjercicioFiscalActivo( conn );
			param[3]="Compromiso Contrato Art 25 Ampliación de Máximos";
			
			//Obtener los datos del Contrato
			manager.searchDataContArt25( conn, cont );
			amp.setcIdContratoDef( cont.getcIdContratoDef() );
			amp.setnIdConsecutivoAmpliacion( cont.getnIdConsecutivoAmpliacion() );
			manager.searchDataExtensionContArt25( conn, amp );
			manager.searchMountExtensionContArt25( conn, amp );
			
			conv.setcEjercicio( cont.getcEjercicio() );
			conv.setcIdEntidadContable( cont.getcIdEntidadContable() );
			conv.setcIdContrato( cont.getcIdContratoDef() );
			conv.setnConsecutivoModificacion( cont.getnIdConsecutivoAmpliacion() );
			conv.setcIdModificacion( cont.getcIdContratoDef()+"-AMP-"+cont.getnIdConsecutivoAmpliacion() );
			conv.setmConvenio( amp.getmSubtotal() );
			conv.setmGlobalContrato( amp.getmMontoNeto() );
			conv.setmIVAConvenio( amp.getmMontoIVA() );
			conv.setmTotal( amp.getmMontoNeto() );
			
			//Valida que esté en SAI  PRESUPUESTADO
			if(amp.getnIdEstado()!=ContractStatus.BUDGET) {
				throw new Exception( "El estatus de la ampliación de contrato debe estar en SAI PRESUPUESTADO para poder autorizarlo." );
			}
			//Validar si hay compromisos por autorizar en sicop
			if(Util.hayCompromisoPendienteAutSICOP(conn, cont.getcIdContratoDef())){
				throw new Exception( "Hay compromisos pendientes por autorizar en SICOP." );
			}
			//Autorizar compromiso
			compromete( conn, request, usuario, param, cont.getJndiName(), cont.getcPrefixPath() );
			//Actualizar estatus de la mpliación de contrato
			manager.updateEstateExtensionContractArt25( conn, cont.getcIdContratoDef(), ContractStatus.APPROVED, cont.getnIdConsecutivoAmpliacion() );
			//Actualizar montos del convenio
			manager.updateMontosDiversoConvenio( conn, conv );
			//Guardaar en bitacora los movimientos
			Util.bitacoraMovimientos(conv.getcIdModificacion(), "Autoriza ampliación contrato art25", usuario.getLogin(), conn);
			
			conn.commit();
			error=false;
		} catch ( Exception e ) {
			throw new Exception( "Bug, Autoriza Contrato Art 25: " +e );
		}finally {
			if ( error ) {
				if ( conn != null )
					try {
						conn.rollback();
					} catch ( Exception e ) {
						log.error( "Bug, Rollback: " + e );
						throw new Exception( "Bug, Rollback: " + e.toString(), e );
					}
			}
			CloseObject.closeObject( conn );
			CloseObject.closeObject( cmst );
			
			manager=null;
			param=null;
			conv=null;
		}
	}
}
