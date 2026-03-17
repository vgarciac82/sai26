<%@page import="com.syc.contable.core.CatalogoURFIELBusinessLogic"%>
<%@page import="com.syc.obrapublica.ConfiguraAplicativoBusinessLogic"%>
<%@page import="com.syc.obrapublica.core.ConfiguraAplicacion"%>
<%@page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" pageEncoding="utf-8"%>
<%@page	import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@page import="com.syc.contable.core.Saldo"%>
<%@page import="java.text.DecimalFormat"%>
<%
	String DATE_FORMAT = "dd/MM/yyyy";
	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	Calendar c1 = Calendar.getInstance(); // today
	String today = sdf.format(c1.getTime());
	String cCentroContable = "";
	String cUR = "";
	String cRamo = "";
	String usu = "";
	boolean bAplicadoCont = false;
	Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
	Usuario usuario = (Usuario) session
	.getAttribute(GestionInterface.ATT_USER);
	
	if (c == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	
	int idTipoCaso = c.getIdTC();

	if (c.getCasoDato("APLICADO_CONT").getValor() != null) {
		if ("true".equals(c.getCasoDato("APLICADO_CONT").getValor()))
	bAplicadoCont = true;
	}

	String mensaje = "";
	
	int id_oper = -1;
	if (request.getParameter("id_oper") != null)
		id_oper = new Integer(request.getParameter("id_oper")).intValue();
	else
		id_oper = c.getCasoOperacion(0).getIdOperacion();

	Empleado e = new Empleado();
	EmpleadoBusinessLogic ebl = new EmpleadoBusinessLogic(GestionInterface.ATT_CONEXION);
	e.setClaveUsuario(usuario.getLogin());
	e = ebl.getEmpleado(e);
	EmpleadoArea ea = new EmpleadoArea();
	ea.setId(e.getClaveArea());
	ea = ebl.getEmpleadoArea(ea);

	//documentos del caso
	CasoBusinessLogic cbl = new CasoBusinessLogic(
	GestionInterface.ATT_CONEXION);

	if (usuario.getPropiedades() != null
	&& usuario.getPropiedades().containsKey("CCENTROCONTABLE")) {
		cCentroContable = usuario.getPropiedad("CCENTROCONTABLE").getValor();
	}

	if (cCentroContable.isEmpty() || cCentroContable.equals("")) {
		mensaje = "Error: El Usuario no tiene Centro Contable asignado y no podra realizar aplicacion Contable, Consulte a su administrador.";
	}

	cUR = usuario.getU_UR();
	cRamo = usuario.getU_Ramo();
	usu = usuario.getLogin();
	String numeroEmpleado = usuario.getNumeroEmpleado();
	
	/*VGC20151228 Se parametriza el habilitar radicado*/
	ConfiguraAplicativoBusinessLogic configApp = new ConfiguraAplicativoBusinessLogic(GestionInterface.ATT_CONEXION);
	boolean habilitaCBRadicado = "S".equalsIgnoreCase(   configApp.getSystemSetting("HABILITA_SEL_RADICADO_RG")  );
	boolean muestraCBRadicado = "S".equalsIgnoreCase(   configApp.getSystemSetting("MUESTRA_RADICADO_RG")  );
	
	CatalogoURFIELBusinessLogic curbl = new CatalogoURFIELBusinessLogic( GestionInterface.ATT_CONEXION );
	boolean permitePagoSinFIEL = curbl.permitePagoSinFiel(cUR);
	
	/*FAV20171019 Se guarda en base el prefijo de CxP*/	
	String cxpPrefijo = configApp.getSystemSetting("CXP_PREFIJO") == null?"CP":configApp.getSystemSetting("CXP_PREFIJO");
	
	String cNombreElabora = e.getNombre();
	String cApellidoPaternoElabora  = e.getApellidoPaterno();
	String cApellidoMaternoElabora  = e.getApellidoMaterno();
	String cPuestoElabora = e.getCargo();
	
	boolean esConsulta = c.getCasoOperacion(0).getOperacion().getNumero() == 3;
	
	/*ARLA20231213 Permite habilitar o deshabilitar la validacion del saldo de Ingreso Propios*/
	boolean validaSaldoIP = "S".equalsIgnoreCase(   configApp.getSystemSetting("VALIDA_SALDO_IP")  );
	
%>
<!DOCTYPE html>
<html lang="es">  
	<head>

		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">
		<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
		<meta charset="UTF-8"/>

		<link rel="shortcut icon" type="image/ico" href="imagenes/favicon.ico" />
		<link rel="stylesheet" type="text/css"	href="../Generador/css/bootstrap.min.css"></link>		
		<link rel="stylesheet" type="text/css" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.2/font/bootstrap-icons.css"></link>
		<script src="../Generador/js/bootstrap.bundle.min.js"></script>
		
		<style type="text/css" title="currentStyle">
			@import "themes/smoothness/jquery-ui-1.8.4.custom.css";
			
			@import "css/demo_table_jui.css";
			@import "css/sweetalert2.min.css";
		</style>
		<style>
			.notEditable {
				background-color: #CCCCCC;
				color: #000000;
			}

			.eliminar{ cursor: pointer; color: #000; }
		</style>
	</head>

	<body id="dt_example">
		<form id="formPagos">
			<div id="container" class="container" align="center">
					<input type="hidden" id="esEFO"       name="esEFO"       value="N"/>
					<input type="hidden" id="rfcValidar"  name="rfcValidar"  value=""/>
					<input type="hidden" id="TipoAutorizacion" name="TipoAutorizacion"  value="0"/>
					<input type="hidden" id="correcto" name="correcto" value=""/>
					<input type="hidden" id="apartado" name="apartado" value=""/>
					<input type="hidden" id="listaSol"  name="listaSol" value=""/>
					<input type="hidden" id="cantMeses"  name="cantMeses" value="0"/>
					<!-- VGC20180709 Carga de oficios -->
					<input type="hidden" id="destinoGastoElegido" value=""/> 
					<input type="hidden" id="DCD_TBEN" name="DCD_TBEN"  value="3"/>  
					<input type="hidden" id="DCD_CONTRIBUCION" name="DCD_CONTRIBUCION"  value="0"/>
					<!-- VGC20150930 FID -->
					<input type="hidden" id="cSubPrograma" name="cSubPrograma"  value=""/>
					<input type="hidden" id="nIDPrograma" name="nIDPrograma"  value=""/>
					<input type="hidden" id="esPagoFID" name="esPagoFID"  value="0"/>
					<input type="hidden" id="cPrograma" name="cPrograma"  value="0" />
					<input type="hidden" id="cPagoReferenciado" name="cPagoReferenciado" value="N"/>
					<!-- /FID -->
					<input name="otrosImpuestos" id="otrosImpuestos" type="hidden" value="0"/>
					<input name="nFacturasCapturadas" id="nFacturasCapturadas" type="hidden" value="0"/>
					<input name="nFolioPago" id="nFolioPago" type="hidden" value="<%=c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)%>"/>
					<input name="cTipoPago" id="cTipoPago" type="hidden" value="RELACIONGASTOS"/>
					<input name="tipo_pago" id="tipo_pago" type="hidden" value="RELACIONGASTOS"/>
					<input name="tipoTramite" id="tipoTramite" type="hidden" value="<%=idTipoCaso%>"/>
					<input name="campoRFC" id="campoRFC" type="hidden"/>
					<input name="importeAuxNeto" id="importeAuxNeto" type="hidden" value=""/>
					<input name="totFacturas" id="totFacturas" type="hidden" value="0"/>
					<input name="importeCapturadoBD" id="importeCapturadoBD" type="hidden" value="0"/>
					<input name="mImporteNetoEP" type="hidden" id="mImporteNetoEP" value="0"/>
					<input name="mImporteIvaTotal" type="hidden" id="mImporteIvaTotal" value="0"/>
					<input name="mImporteIvaEP" type="hidden" id="mImporteIvaEP" value="0"/>
					<!-- URVP.03112014 SE OBTIENE EL DISPONIBLE AL MES DE LA EP ENVIADA -->
					<input type="hidden" name="mImporteDisponibleEP" id="mImporteDisponibleEP" value=""/>
					<input type="hidden" name="disponibleEP" id="disponibleEP" value=""/>
					<input type="hidden" name="cEstatusPago" id="cEstatusPago" value=""/>
					<!-- VGC 20140913 Almacen temporal para el No. de solicitud que se selecciona -->
					<input type="hidden" name="tmpNoCaja" id="tmpNoCaja"/>
					<input type="hidden" name="cCtaBanc" id="cCtaBanc" value="N/A"/>
					<input type="hidden" name="dCtaBanc" id="dCtaBanc" value="N/A"/>
					<!-- VGC 20140916 Almacen temporal para el remanente de la solicitud -->
					<input type="hidden" name="remanente" id="remanente"/>
					<input type="hidden" name="mMovimiento2" id="mMovimiento2"/>
					<input type="hidden" name="cInterna" id="cInterna" />
					<input type="hidden" name="TOTALSUBCUENTA" id="TOTALSUBCUENTA"/>
					<input type="hidden" name="total1" id="total1" value="0" />
					<input type="hidden" name="nfoliocontratodiversoencabezado" id="nfoliocontratodiversoencabezado" value=""/>
					<input type="hidden" id="rowsAffected" name="rowsAffected" />
					<input type="hidden" name="numPaso" id="numPaso" value="1"/>
					<input type="hidden" id="TIPO_CONCEPTO" name="TIPO_CONCEPTO"/>
					<input type="hidden" id="cDocumento" name="cDocumento" value="<%=c.getTipoCaso().getGavetaAsociada()%>"/>
					<input type="hidden" id="cTipoPoliza" name="cTipoPoliza" value=""/>
					<input type="hidden" id="docAplicado" value=""/>
					<input type="hidden" id="laPoliza" />
					<input type="hidden" id="cRamo" name="cRamo" value="16" />
					<input type="hidden" id="cUnidadResponsable" name="cUnidadResponsable" />
					<input type="hidden" id="cUnidadResponsable2" name="cUnidadResponsable2" />
					<input type="hidden" id="contra2" name="contra2" />
					<input type="hidden" id="cCentroContable" name="cCentroContable" />
					<input type="hidden" id="mImporteMasIva" name="mImporteMasIva" value="0" />
					<input type="hidden" id="cUnidadEjecutora" name="cUnidadEjecutora" value="A02" />
					<input type="hidden" id="cveRetencion" name="cveRetencion" value="0" />
					<input type="hidden" id="nMes" name="nMes" />
					<input type="hidden" id="cMes" name="cMes" />
					<input type="hidden" id="RFC" name="RFC" />
					<input type="hidden" id="aEjercicioFiscal" value=""	name="aEjercicioFiscal" />
					<input type="hidden" id="usur" name="usur" />
					<input type="hidden" id="operacio" name="operacio" value="0" />
					<input type="hidden" id="anex" name="anex" value="1" />
					<input type="hidden" id="partida" name="partida" value="" />
					<input type="hidden" id="vOGT" name="vOGT" value="" />
					<input type="hidden" id="montoprevio" name="montoprevio"/>
					<input type="hidden" id="cEvento" name="cEvento" value="CD_AL01" />
					<input type="hidden" id="cimpo" name="cimpo" value="" />
					<input type="hidden" name="FOLIO" id="FOLIO" value="<%=c.getFolio()%>" />
					<input type="hidden" name="OPERADOR" id="OPERADOR"	value="<%=c.getCasoOperacion(0).getResponsable()%>" />
					<input type="hidden" name="FECHA_CARGA" id="FECHA_CARGA"	value="<%=today%>" />
					<input type="hidden" name="cIdRFC_RelacionGasto2" id="cIdRFC_RelacionGasto2" />
					<input type="hidden" name="cFolioGestion" id="cFolioGestion" value="<%=c.getFolio()%>"/>
					<input type="hidden" id="cEjercicio" name="cEjercicio" value="2012" />
					<input type="hidden" id="cIdTipoDocumento" name="cIdTipoDocumento"	value="3" />
					<input type="hidden" name="TO_TIPO_DOCTO" id="TO_TIPO_DOCTO" value="RELGASTO" />
					<input type="hidden" id="cIdCuentaContable" name="cIdCuentaContable" size="20" />
					<input type="hidden" name="cllave" id="cllave" />
					<input type="hidden" name="ID_DESTINO_GASTO" id="ID_DESTINO_GASTO" />
					<input type="hidden" name="idDestinoGasto" id="idDestinoGasto" />
					<input type="hidden" id="campo" name="campo" value=""/>
					<input type="hidden" id="tablaEnc" name="tablaEnc" value=""/>
					<input type="hidden" id="campoCondicion" name="campoCondicion" value=""/> 
					<input type="hidden" id="tablaDet" name="tablaDet" value=""/>
					<input type="hidden" id="tipoAplicar" name="tipoAplicar" value=""/>
					<input type="hidden" id="nFolioApartado" name="nFolioApartado" value=""/>
					<input type="hidden" id="cDescripcionPoliza" name="cDescripcionPoliza" value=""/>
					<input type="hidden" id="FechaAplAptd" name="FechaAplAptd" value=""/>
					<input type="hidden" id="elcontra" name="elcontra" value=""/>
					<input type="hidden" id="Fecha_Pago" name="Fecha_Pago" value=""/>
					<input type="hidden" name="cartera" id="cartera" value=""/>
					<input type="hidden" name="carteraMeta" id="carteraMeta" value=""/>
					<input type="hidden" id="idMeta" name="idMeta" value=""/>
					<input type="hidden" id="dMeta" name="dMeta" value=""/>
					<input type="hidden" id="esPagoMeta" name="esPagoMeta" value="N"/>
					<input type="hidden" id="capitulo" name="capitulo" value=""/>
					<input type="hidden" id="UE" name="UE" value=""/>
					<input type="hidden" id="mImporteCartera" name="mImporteCartera" value="">
					<input type="hidden" id="ayuMov" name="ayuMov" value="AL"/>
					<input type="hidden" id="COMSOCAutoriza" name="COMSOCAutoriza" value="" />
					<input type="hidden" id="partCOMSOC" name="partCOMSOC" value="" />
					<input type="hidden" id="cMsjCOMSOC" name="cMsjCOMSOC" value=""/>
					<input type="hidden" id="cOrigen" name="cOrigen" value=""/>
					<input type="hidden" id="cIdDocumento" name="cIdDocumento" value=""/>
					<input type="hidden" id="cMotivoRechazoCRUD" name="cMotivoRechazoCRUD" />
					<input type="hidden" id="nIdEstado" name="nIdEstado" />
					<input type="hidden" id="folioDevolucion" name="folioDevolucion" />
					<input type="hidden" id="noFolio" name="noFolio" />
					<input type="hidden" name="DPC_FECHA" id="DPC_FECHA"  value="<%=today%>" />
					<input type="hidden" id="nfolio" name="nfolio" value=""/>
					<input type="hidden" id="motivo" name="motivo" value=""/>
					<input type="hidden" id="cnombreRFC" name="cnombreRFC" value=""/>
					<input type="hidden" id="cIdRFC" name="cIdRFC" value=""/>
					<input type="hidden" id="cIdMotivoCancelacion" name="cIdMotivoCancelacion" value=""/>
					<input type="hidden" id="firmanteExiste" name="firmanteExiste"/>
					<input type="hidden" id="cNombreVo" name="cNombreVo" />
					<input type="hidden" id="cPaternoVo" name="cPaternoVo" />
					<input type="hidden" id="cMaternoVo" name="cMaternoVo" />
					<input type="hidden" id="cPuestoVo" name="cPuestoVo" size=40/>
					<input type="hidden" id="cEmpleadoVo" name="cEmpleadoVo" />
					<input type="hidden" id="firmanteVoBo" name="firmanteVoBo" />
					<input type="hidden" id="tipoFirmante" name="tipoFirmante" size=15 value="PAGO_VOBO"/>
					<input type="hidden" id="cNombreA" name="cNombreA" />
					<input type="hidden" id="cPaternoA" name="cPaternoA" />
					<input type="hidden" id="cMaternoA" name="cMaternoA" />
					<input type="hidden" id="cPuestoA" name="cPuestoA" />
					<input type="hidden" id="cEmpleadoA" name="cEmpleadoA" />
					<input type="hidden" id="firmanteAut" name="firmanteAut" />
					<input type="hidden" id="tipoFirmanteA" name="tipoFirmanteA" value="PAGO_AUT"/>
					<input type="hidden" id="infFinanciamiento" name="infFinanciamiento" size=15 value="sinInformacion"/>
					<input type="hidden" id="tipoPoliza" name="tipoPoliza" value="EG"/>
					<input type="hidden" id="eventoPoliza" name="eventoPoliza" value=""/>
					<input type="hidden" id="cTipoRfc" name="cTipoRfc" value=""/>
					<input type="hidden" id="cTipoFuente" name="cTipoFuente" value="1"/> 
					<input type="hidden" id="estatusRG" name="estatusRG" value=""/>
					<input type="hidden" id="polManual" name="polManual" value=""/>
					<input type="hidden" id="destinoRG" name="destinoRG" value=""/>
					<input type="hidden" id="folioDisponibleReclasif" name="folioDisponibleReclasif" value=""/>
					<input type="hidden" id="folioCajaUsado" name="folioCajaUsado" value=""/> 
					<input type="hidden" id="cEsRadicado" name="cEsRadicado" value="S"/> 
					<input type="hidden" id="esExtranjero" name="esExtranjero" value=""/>
					<input type="hidden" value="" id="borrarFFM"  name="borrarFFM" />
					<input type="hidden" value="" id="mImporteSinIVA"  name="mImporteSinIVA" />
					<input type="hidden" name="elcontraTemp" id="elcontraTemp" value=""/>
					
					<!-- Variables hidden para la seleccion del tipo de gasto de viaticos -->
					<input name="nIdComision" id="nIdComision" type="hidden" value="0"/>
					<input name="nIdComisionAux" id="nIdComisionAux" type="hidden" value="0"/>
					<input name="nIdComisionReloj" id="nIdComisionReloj" type="hidden" value="0"/>
					<input name="tipoGasto" id="tipoGasto" type="hidden"/>
					<input name="conceptoComision" id="conceptoComision" type="hidden"/>	
					<input name="mMontoComision" id="mMontoComision" type="hidden"/>
					<input name="mMontoComisionAux" id="mMontoComisionAux" type="hidden"/>							
					<input name="statusComision" id="statusComision" type="hidden" value=""/>
					
					<!-- hidden para la captura de oficio delegatorio -->
					<input type="hidden" name="cFolioOficioAux" id="cFolioOficioAux" value=""/>
					<input type="hidden" name="dFechaOficioAux" id="dFechaOficioAux" value=""/>
					<input type="hidden" name="cNombreTitularAux" id="cNombreTitularAux" value=""/>
					<input type="hidden" name="cApellidoPaternoTitularAux" id="cApellidoPaternoTitularAux" value=""/>
					<input type="hidden" name="cApellidoMaternoTitularAux" id="cApellidoMaternoTitularAux" value=""/>
					<input type="hidden" name="cPuestoTitularAux" id="cPuestoTitularAux" value=""/>
					<input type="hidden" name="firmanteOficioExiste" id="firmanteOficioExiste" value=""/>
					
					<!-- hidden para el Tipo de Partida Hospedaje -->
					<input type="hidden" name="cPartida" id="cPartida" value=""/>
					<input type="hidden" name="esPartidaViaticos" id="esPartidaViaticos" value="0"/>
					
					<!-- hidden para Informe de Comision -->
					<input type="hidden" name="cInformeComision" id="cInformeComision" value=""/>
					
					<!-- hidden para la captura de los datos de quien elaboro -->
					<input type="hidden" id="cNombreE" name="cNombreE" />
					<input type="hidden" id="cPaternoE" name="cPaternoE" />
					<input type="hidden" id="cMaternoE" name="cMaternoE" />
					<input type="hidden" id="cPuestoE" name="cPuestoE" />
					<input type="hidden" id="firmanteEla" name="firmanteEla" />
					
					<!-- hidden para validar que se haya capturado la informacion de boletos -->
					<input type="hidden" name="capturaBoletos" id="capturaBoletos" value="N"/>	
					
					<!-- Relacion Ingreso Fiscal con el pago -->
					<input type="hidden" id="nExisteIngresoPago" name="nExisteIngresoPago" value="0"/>
					<input type="hidden" id="mTotalIngresoPago" name="mTotalIngresoPago" value="0"/>
					<input type="hidden" id="nFolioRegistroIngreso" name="nFolioRegistroIngreso" value="0"/>
					<input type="hidden" id="cEPIngresoPago" name="cEPIngresoPago" value="0"/>
					<input type="hidden" id="nMesIngresoPago" name="nMesIngresoPago" value="0"/>
					<input type="hidden" id="mImporteIngresoPago" name="mImporteIngresoPago" value="0"/>
					<input type="hidden" id="EPAgregada" name="EPAgregada" value="0"/>
					
					<!-- hidden para la captura de oficio delegatorio VoBo-->
					<input type="hidden" name="cFolioOficioVoBoAux" id="cFolioOficioVoBoAux" value=""/>
					<input type="hidden" name="dFechaOficioVoBoAux" id="dFechaOficioVoBoAux" value=""/>
					<input type="hidden" name="cNombreTitularVoBoAux" id="cNombreTitularVoBoAux" value=""/>
					<input type="hidden" name="cApellidoPaternoTitularVoBoAux" id="cApellidoPaternoTitularVoBoAux" value=""/>
					<input type="hidden" name="cApellidoMaternoTitularVoBoAux" id="cApellidoMaternoTitularVoBoAux" value=""/>
					<input type="hidden" name="cPuestoTitularVoBoAux" id="cPuestoTitularVoBoAux" value=""/>
					<input type="hidden" name="firmanteOficioVoBoExiste" id="firmanteOficioVoBoExiste" value=""/>
					
					<!-- hidden para VALIDAR SI EXISTE SUFICIENCIA DE INGRESOS PROPIOS -->
					<input type="hidden" name="nValidaSuficienciaIP" id="nValidaSuficienciaIP" value="0"/>
					<input type="hidden" id="cUEjecutora" name="cUEjecutora" value="<%=cUR%>"/>
					
					<!-- hidden para VALIDAR LA EDICION DEL MONTO CUANDO ES RFC: CSS160330CP7 (CFE) -->
					<input type="hidden" name="editaMontoRFC" id="editaMontoRFC" value="0"/>
					<input type="hidden" name="cPasivo" id="cPasivo" value=""/>
					
						<!-- hidden para el editar las comisiones -->
					<input type="hidden" name="fInicio" id="fInicio" />
					<input type="hidden" name="fFin" id="fFin" />
					<input type="hidden" name="conceptoCom" id="conceptoCom" />
					<input type="hidden" name="existeComision" id="existeComision" />
					
					<!-- hidden para validar si el XML tiene retenciones, en caso afirmativo NO permitir la emision del pago -->
					<input type="hidden" name="tieneRetenXML" id="tieneRetenXML" value="0"/>
					<input type="hidden" name="mImporteBrutoEP" id="mImporteBrutoEP" value="0"/>
					<input type="hidden" name="esResico" id="esResico" value="0"/>
					<input type="hidden" name="nDocRenglon" id="nDocRenglon" value="0"/>
					
					<!-- hidden para validar si la partida es obligatoria para retenciones, en caso afirmativo NO permitir la captura de la EP -->
					<input type="hidden" name="esPartidaObligatoria" id="esPartidaObligatoria" value="0"/>
					
					<!-- hidden para validar el saldo de la cuenta 11151 cuando el DESTINO GASTO sea RCRE -->
					<input type="hidden" name="validaRCRE" id="validaRCRE" value="0"/>
					<input type="hidden" name="cEntidadFederativaEP" id="cEntidadFederativaEP" value="0"/>
					<input type="hidden" name="esViaticos" id="esViaticos" value="0"/>
					<input type="hidden" name="cMeta" id="cMeta"/>
					<input type="hidden" name="noEmpleadoRFC" id="noEmpleadoRFC" value=""/>
					<input type="hidden" name="idAgenda" id="idAgenda" value=""/>
					<input type="hidden" id="msgRF" name="msgRF" value="">
					<input type="hidden" id="nombre" name="nombre" value="0">
					<input type="hidden" name="fRecepcion2" id="fRecepcion2" value="05/12/2012" readonly />
					<input type="hidden" name="txtFechaPago" id="txtFechaPago" value="<%=today%>"/>
					<input type="hidden" name="txtFechaPago2"  id="txtFechaPago2"/>
					<!-- hidden para captura de correo-->
					<input type="hidden" id="esPPD" name="esPPD" value=""/>
					<input type="hidden" id="cIDRFC" name="cIDRFC" value=""/>
					<input type="hidden" name="correo" id="correo" />
					<input type="hidden" name="nCorreo" id="nCorreo" />
					<input type="hidden" name="paternoCorreo" id="paternoCorreo" />
					<input type="hidden" name="maternoCorreo" id="maternoCorreo" />
					<input type="hidden" name="cCargo" id="cCargo" />
					<input name="fAplicacion" type="hidden" id="fAplicacion" value="30/01/2012"/>
					<input type="hidden" name="sDataFoliosING" id="sDataFoliosING"/>
					<input type="hidden" name="sDataRemanenteING" id="sDataRemanenteING"/>
					<input type="hidden" name="mImpPasaje" id="mImpPasaje" value=""/>
					<input type="hidden" name="mImpTaxi" id="mImpTaxi" value=""/>
					<input type="hidden" name="mImpPeaje" id="mImpPeaje" value=""/>
					<input type="hidden" name="mImpHotel" id="mImpHotel" value=""/>
					<input type="hidden" name="mImpConsumos" id="mImpConsumos" value=""/>
					<input type="hidden" name="mImpOtros" id="mImpOtros" value=""/>
					<input type="hidden" name="mImpTotal1" id="mImpTotal1" value=""/>
					<input type="hidden" name="mImpPasajeLocal" id="mImpPasajeLocal" value=""/>
					<input type="hidden" name="mImpTaxiLocal" id="mImpTaxiLocal" value=""/>
					<input type="hidden" name="mImpGasolinaLocal" id="mImpGasolinaLocal" value=""/>
					<input type="hidden" name="mImpPeajeLocal" id="mImpPeajeLocal" value=""/>
					<input type="hidden" name="mImpMaritimoLocal" id="mImpMaritimoLocal" value=""/>
					<input type="hidden" name="mImpAereoLocal" id="mImpAereoLocal" value=""/>
					<input type="hidden" name="cOBGT" id="cOBGT" />
					<input type="hidden" name="nClaveCNA1" id="nClaveCNA1"/>
					<input type="hidden" name="cTieneJustificacion" id="cTieneJustificacion"/>
					<input type="hidden" name="nEnviadoSICOP" 	id="nEnviadoSICOP"/>
					<input type="hidden" name="tipoRetIVA" 		id="tipoRetIVA" />
					<input type="hidden" name="tipoRetISR" 		id="tipoRetISR" />
					<input type="hidden" name="rfcContrato" 	id="rfcContrato" />
					<input type="hidden" name="cIdContrato" 	id="cIdContrato" />
					<input type="hidden" name="nTipoRetencion" 	id="nTipoRetencion" />
					<input type="hidden" name="nPorcRetencionIva" 	id="nPorcRetencionIva" />
					<input type="hidden" name="nPorcRetencion" 	id="nPorcRetencion" />
					<input type="hidden" name="gp_valor" 		id="gp_valor" />
					
					<h5>
						Relaci&oacute;n de Gastos- Recepci&oacute;n de Documentos</h5>
					<hr>
					<div class="dvGeneral">
						<div id="divAutorizar" >
							<div class="row d-flex justify-content-center">
								<div class="col-auto">
									<label id="lbAutorizar" style="font-size: 18px"> Autorizar</label>
								</div>
							</div>	
							<div class="row d-flex justify-content-center">
									<div class="col-auto">
										<input type="radio" id="grpAutorizar" name="grpAutorizar" checked="checked" onclick="habilitaGuardar(1)" value="Si">
										<label style="font-size: 18px"> Si </label> 
									</div>
									<div class="col-auto"> 
										<label style="font-size: 18px"> No </label> 
										<input type="radio" id="grpAutorizar" name="grpAutorizar" onclick="habilitaGuardar(2)" value="No">
									</div>
							</div>
						</div>
						<div class="row d-flex justify-content-center">
							<div class="col-auto">
								<div id="divImprimePoliza">
									<img src="imagenes/Imprimir.png" width="25" height="21" onClick="imprimir('PolizaPago');" />
									Póliza
								</div>
							</div>
							<div class="col-auto">
								<div id="divImprimeAnexo">
									<div class=" input-group">
										<button class="input-group-text" onClick="anexo();"><i class="bi bi-card-checklist"></i></button> 
										<input type="button" class="btn btn-secondary" id="btnAnexo" name="btnAnexo" onClick="anexo();" value ="Anexo"/>    
									</div>
								</div>
							</div>
							<div class="col-2">
								<div id="divImprimeJustificacion">
									<div class=" input-group">
										<span class="input-group-text"><i class="bi bi-printer" onClick="imprimirJustificacion()"></i></span>
										<input type="button" class="btn btn-secondary" id="btnImprimirJust" name="btnImprimirJust" onclick="imprimirJustificacion()" value ="Justificación"/>
									</div>
								</div>
							</div>
							<div class="col-2">
								<div id="divImprimeInforme">
									<div class=" input-group">
										<span class="input-group-text"><i class="bi bi-card-list" onClick="imprimirInforme()"></i></span>
										<input type="button" class="btn btn-dark" id="btnImprimirJust" name="btnImprimirJust" onclick="imprimirInforme()" value ="Informe Comisión"/>
									</div>
								</div>
							</div>
							<div class="col-auto">
								<span id="EditaFirmas" style="visibility:hidden"><a href="#" onclick="updateFirmantes();">Firmas*</a></span>
							</div>						
						</div>
						<div id="divUE" align="left">
							<div class="row">
								<div class="col-12">
									<label for="UEjecutora" class="col-form-label">Unidad Ejecutora:</label>
									<select id="UEjecutora" name="UEjecutora" class="form-select">
										<option value="A02"></option>
									</select>
								</div>
							</div>
						</div>
					<div>
						<div align="left">
							<div class="row">
								
								<div class="col-3">
									Folio Relaci&oacute;n :
									<input id="cIdRelacion" name="cIdRelacion" maxlength="40" style="text-transform:uppercase" size="40" type="text" class="paso01 form-control">
								</div>
								<div class="col-3">
									No. Folio Pago:
									<input name="id_caso" type="text" id="id_caso" readonly class="form-control"/>
								</div>
								<div class="col-3">
									Contrarrecibo
									<input name="caNoContrarrecibo" id="caNoContrarrecibo" size="14" value="0" readonly class="form-control"/>
								</div>
								<div class="col-3">
									Fecha de Recepci&oacute;n:
									<input name="fRecepcion" class="paso01 form-control" id="fRecepcion" size="10" value="05/12/2012" readonly />
								</div>
							</div>
							<div class="row align-items-center">
								<div class="col-3">
									Tipo Fuente:
									<select class="paso01 form-select" id="tipo_fuente" name="tipo_fuente"  onchange="cargaTipoDestino();">
										<option value="FF">
											Fondos Fiscales
										</option>
										<option value="IP">
											Ingresos Propios
										</option>
										<option value="CE">
											Credito Externo
										</option>
									</select>
								</div>
								<div class="col-6">
									Tipo Destino:
									<select class="paso01 form-select" id="DESTINO_GASTO" name="DESTINO_GASTO" onchange="borraDatos();"></select>
								</div>
								<div class="col-3">
									Tipo de Pago:
									<select class="paso01 form-select" id="TIPO_OPERACION" name="TIPO_OPERACION" ></select>
									<input name="txtFolioFact" type="hidden" id="txtFolioFact" value="" size="5">
								</div>
							</div>
							<div class="row align-items-center">
								<div class="col-3">
									RFC:
									<div class="input-group">
									    <input type="text" maxlength="15" size="15" name="cIdRFC_RelacionGasto" id="cIdRFC_RelacionGasto" style='text-transform:uppercase;' onchange="cargaCtaBancariaRFC();creaDiagloFacturas(true);" readonly class="form-control"/>
									    <input type="button" name="btnBeneficiario" id="btnBeneficiario" value="..." size="5" onclick="cat_beneficiario()" class="btn btn-secondary"/>
								    </div>
								  </div>
								  <div class="col-6">
								  	Nombre
								    <input type="text" maxlength="100" size="72" name="cnombre" ID="cnombre" value="" readonly class="paso01 form-control" />
								  </div>
								  <div class="col-1">
									<span id="capturaFolioCaja" style="visibility:hidden"><a href="#" onclick="updateFolioCaja();">Folio Caja*</a></span>
								</div>
								<div class="col-2">
									<br>
									<input type="button" onclick="Borrar1()" name="Borrar"	id="Borrar" value="Borrar" class="btn btn-secondary"/>
								</div>
							</div>
							<div class="row align-items-left">
								<div class="col-3 ctaBancComproba" id="tdctaBancaria">
										<label id="lblctaBancaria"  class="ctaBancComproba"> Cuenta Bancaria: </label>
										<select id="ctaBancaria" name="ctaBancaria" class="ctaBancComproba form-select"></select>
								</div>
								<div class="col-3">
									Pago con Referencia Bancaria
									<input type="checkbox" id="chk_RB" name="chk_RB" >
								</div>
								<div class="col-3" id="trReferenciaBancaria">
									<label id ="idReferencia" for="cReferenciaBancaria">Referencia:</label>
									<input type="text" name="cReferenciaBancaria" id="cReferenciaBancaria" size="30" maxlength="30" class="form-control"/>
								</div>
								
							</div>
						</div>
						<div>
							<div class="row">
								<div class="col-12">
									<span id="EditaFacturas"><a href="#" onclick="creaDiagloFacturas('true'); return false;"> Facturas... </a></span>
									<span id="VerDetalleAgenda" style="visibility:hidden"><a href="#" onclick="consultaAgenda();">Ver Detalle de Agenda...</a></span>
									<span id="EditaCorreoE" style="visibility:hidden"><a href="#" onclick="abrirDialogCorreo();">Captura correo...</a></span>
								</div>
							</div>
						</div>
						<div align="left">
							<div class="row">
							<!-- 
								<div id="divSuficiencia" class="col-3" >
										<label id="lblcontrato"> Suficiencia: </label>
										<select id="cFolioSuficiencia" name="cFolioSuficiencia" class="form-select" onChange="actualizaContrato();"></select>
								</div> -->
								<div class="col-3">
									Tipo Concepto:
									<select id="tConcepto" name="tConcepto" class="paso01 form-select" onChange="concepto();"></select>
									<input type="text" id="id_concepto" name = "id_concepto" readonly class="form-control"/>
								</div>
								<div class="col-3">
									Tipo De Movimiento
									<select id="TIPO_MOVIMIENTO" name="TIPO_MOVIMIENTO" class="paso01 form-select"></select>
									<input type="text" id="id_mov" name = "id_mov" readonly class="form-control"/>
								</div>								
								<div class="col-3">
									<div id="divGreenMex">
										Proyecto Green Mex:
										<select id="idGreenMex" name="idGreenMex" class="paso01 form-select" onChange="seleccionaAnticipoGreenMex();">
											<option value="NO">No Aplica</option>
											<option value="SI">SI</option>
										</select>
									</div>									
								</div>								
								<div class="col-3" id="solCaja" align="left">
									<label id="lblSolicitud" ># De solicitud</label>
									<input type="text" id="noSolicitudCaja" name="noSolicitudCaja" style="width: 100px;" readonly class="notEditable form-control" value=""/>
									<span id="hrefSolicitud"><a href="#" onclick="abrirDialogSolicitudes();return false;"> Cambiar </a></span>
								</div>
							</div>
						</div>
						<div class="row align-items-left">
							<div class= "card" id="editaimporte">
								<div class="card-header">
									Importe en Facturas
								</div>
								<div class="card-body">
									<div class="row">
										<div class="col-2">
											<label id="lbl_noComprobable" >No Comprobable: </label>
											<input type="text" id="importenoComprobable" name="importenoComprobable" value="0.00" readonly  size="10" class="form-control"/>
										</div>
										<div class="col-2">
											<label id="lbl_ImporteBruto" >Facturas + No Comp: </label>
											<input type="text" id="mImporteBruto" name="mImporteBruto" onchange="cambiafrmt(this)" value="0.00" readonly  size="10" class="form-control"/>
										</div>
										<div class="col-2">
											<label id="lbl_ImporteRet" >Retencion: </label>
											<input type="text" id="mImporteRetencion" name="mImporteRetencion" onchange="cambiafrmt(this)" value="0.00" readonly  size="10" class="form-control" />
										</div>
										<div class="col-2">
											<label id="lbl_ImporteMaximo" >Importe en Facturas: </label>
											<input type="text" id="importeEdicionMaximo" name="importeEdicionMaximo" value="0.00" readonly  size="10" class="form-control"/>
										</div>
										<div class="col-2">
											<br>
											<label id="lbl_EditaImporte" >Editar Importe: </label>
											<input type="checkbox" id="chk_EditaImporte" name="chk_EditaImporte" value="0" >
										</div>
										<div class="col-2">
											<br>
											<input type="text" id="importeEdicion" name="importeEdicion" value="0.00" size="10" onkeypress="return validar2(event);" onblur="validaimportemaximo();" disabled class="form-control"/>
										</div>
									</div>
								</div>
							</div>
						</div> 
						<br />
					</div>

					<div id="multitabs" style="width: 1300px">
						<div id="tabs" style="width: 100%">
							<ul>
								<li>
									<a id="L01" href="#tabs-1">Concepto</a>
								</li>
								
								<li>
									<a id="L03" href="#tabs-3" class="pasoDos">Movimientos</a>
								</li>
								<li>
									<a id="L04" href="#tabs-4" class="pasoTres">Documentaci&oacute;n</a>
								</li>
								<li>
									<a id="L05" href="#tabs-5" class="pasoULTIMO"></a>
								</li>

							</ul>
							<div id="tabs-1">
								<div align="left">
									<div class="row align-items-left">
										<div class="col-auto">
											Importe neto:
											<input name="mImporteNeto2" type="hidden" id="mImporteNeto2" size="10" class="form-control"/>
											<input name="mImporteNeto" type="text" id="mImporteNeto" size="14" readonly="readonly" class="form-control"/>
										</div>
										<div class="col-9">
											<label id="lblcConcepto" class="align-items-left">Concepto</label>
											<textarea name="cConcepto" class="paso01 form-control" id="cConcepto" rows= 4
													onkeypress="return validar3(event)"></textarea>
										</div>
										<div class="row mt-2">
											<div class="col-md-4">
												<div class="input-group">
												<!-- 
													<input type="checkbox" id="chk_tieneRete" name="chk_tieneRete" value="0" class="form-check" style="display:none;"/>
													<label id="lbl_tieneRetenciones" for="chk_tieneRete" style="visibility:hidden;">  Tiene Retenciones diferentes a RESICO?: </label>
													 -->
													<input type="checkbox" id="chk_tieneRete" name="chk_tieneRete" value="0" class="form-check""/>
													<label id="lbl_tieneRetenciones" for="chk_tieneRete">  Tiene Retenciones diferentes a RESICO?: </label>
												</div>
											</div>
										</div>
										<div class="row mt-2" id="divRetenciones">
												<div class="col-12">
													<div class="card mt-2">
														<div class="card-header"> Retenciones
														</div>
														<div class="card-body">
															<div class="row gy-2 gx-3 align-items-center">
																<div class="col-3">
																	<label id="lblRete">Tipo Retención</label>
																	<select  id="cTipoRetencion" name ="cTipoRetencion" class="form-select" >
																	</select>
																</div>
																<div class="col-2">
																	<label id="lblIva">Calculo IVA</label>
																	<div class="input-group">
														    			<span class="input-group-text"><i class="bi bi-envelope"></i></span>
										              					<input type="text" id="mImporteIva" name="mImporteIva" value="0.00" readonly  class="form-control" />
										              				</div>
																</div>
																<div class="col-2">
																	<label id="lblISR">Calculo ISR</label>
																	<div class="input-group">
														    			<span class="input-group-text"><i class="bi bi-envelope-paper"></i></span>
										              					<input type="text" id="mImporteISR" name="mImporteISR" value="0.00" readonly  class="form-control" />
										              				</div>
																</div>
																<div class="col-2">
																	<label id="lblIva">IVA Factura</label>
																	<div class="input-group">
														    			<span class="input-group-text"><i class="bi bi-currency-dollar"></i></span>
										              					<input type="text" id="mIvaFactura" name="mIvaFactura" value="0.00" readonly  class="form-control" />
										              				</div>
																</div>
																<div class="col-2">
																	<label id="lblISR">ISR Factura</label>
																	<div class="input-group">
														    			<span class="input-group-text"><i class="bi bi-currency-dollar"></i></span>
										              					<input type="text" id="mISRFactura" name="mISRFactura" value="0.00" readonly  class="form-control" />
										              				</div>
																</div>
															</div>
								             			</div>
								             		</div>
								             	</div>
												<div class="col-12" id="divOtrasRete">
													<div class="card mt-2">
														<div class="card-header"> Otras Retenciones
														</div>
														<div class="card-body">
															<div class="row gy-2 gx-3 align-items-center">
																<div class="col-3">
																	<label id="lblOtrasRete">Tipo Otras Retención</label>
																	<select  id="cOtraRetencion" name ="cOtraRetencion" class="form-select" >
																	</select>
																</div>
																<div class="col-2">
																	<label id="lblOtras">Calculo Otras</label>
																	<div class="input-group">
														    			<span class="input-group-text"><i class="bi bi-envelope"></i></span>
										              					<input type="text" id="mOtras" name="mOtras" value="0.00" readonly  class="form-control" />
										              				</div>
																</div>
																<div class="col-2">
																	<label id="lblFOtras">Factura Otras Reten</label>
																	<div class="input-group">
														    			<span class="input-group-text"><i class="bi bi-currency-dollar"></i></span>
										              					<input type="text" id="mFacturaOtros" name="mFacturaOtros" value="0.00" readonly  class="form-control" />
										              				</div>
																	
																</div>
																
															</div>
								             			</div>
								             		</div>
								             	</div>
						             	</div>
										
									</div>
								</div>
							</div>
							<div id="tabs-3">
								<jsp:include page="../plantillasCasos/ComponentesPago/CapturaMovimientos.jsp"></jsp:include>
							</div>
							<div id="tabs-4">
								<div class="row d-flex justify-content-center">
									<div class="col-2">
										No. factura
										<input name="DCD_FACTURA" type="text" id="DCD_FACTURA"  style="width: 120px;"
															onkeypress="return validar(event)" class="paso03 form-control" readonly/>
									</div>
									<div class="col-2">
										Fecha de factura
										<input name="DCD_FECHA_FACTURA" type="text" id="DCD_FECHA_FACTURA" style="width: 120px;" class="paso03 form-control" readonly/>
									</div>
									<div class="col-2">
										CBEN
										<input name="DCD_CBEN" type="text" class="paso03 form-control" style="width: 120px;" id="DCD_CBEN" readonly/>
									</div>
									<div class="col-2">
										Tipo de operaci&oacute;n
										<select name="DCD_TIPO_OPE" id="DCD_TIPO_OPE" class="paso03 form-select"  disabled style="width: 120px;">
															<option value="85" class="paso03">
																85 OTROS
															</option> 
										</select>
									</div>
									<div class="col-2">
										% IVA
										<input name="DESCRIPCION20" type="text" class="paso03 form-control" style="width: 120px;"
															id="DESCRIPCION20" onKeyPress="return validar2(event)"	value="0" readonly/>
									</div>
									<div class="col-2">
										 Total
										<input type="text" id="DCD_IMP_BRUTO" class="paso03 form-control" style="width: 120px;"
											onkeypress="return validar2(event)" name="DCD_IMP_BRUTO" readonly/>
									</div>
								</div>
								<div class="row d-flex justify-content-center">
									<div class="col-2">
										IVA Desgl
										<input name="DCD_IVADES" type="text" id="DCD_IVADES" style="width: 120px;"
											class="paso03 form-control" value="0" onkeypress="return validar2(event)" readonly/>
									</div>
									<div class="col-2">
										IVA Ret
										<input name="DCD_IVA" type="text" id="DCD_IVA" value="0" style="width: 120px;"
											class="paso03 form-control" onkeypress="return validar2(event)" readonly/>
									</div>
									<div class="col-2">
										ISR
										<input name="DCD_ISR" type="text" id="DCD_ISR" value="0" style="width: 120px;"
											class="paso03 form-control" onkeypress="return validar2(event)" readonly/>
									</div>
									<div class="col-2">
										Ret. 0.5%
										<input name="DCD_MIL5" type="text" id="DCD_MIL5" style="width: 120px;"
											class="paso03 form-control" value="0" onkeypress="return validar2(event)" readonly/>
									</div>
									<div class="col-2">
										Ret. 0.2%
										<input name="DCD_MIL2" type="text" id="DCD_MIL2" value="0" style="width: 120px;"
											class="paso03 form-control" onkeypress="return validar2(event)" readonly/>
									</div>
									<div class="col-2">
										Imp. Cedular
										<input name="DCD_OTRAS_RET" type="text" id="DCD_OTRAS_RET" style="width: 120px;"
											value="0" class="paso03 form-control" onkeypress="return validar2(event)" readonly/>
									</div>
								</div>
								<div class="row d-flex justify-content-center">
									<div class="col-2">
										Penalizaciones
										<input name="DCD_PENALIZACION" type="text" style="width: 120px;"
											id="DCD_PENALIZACION" value="0" class="paso03 form-control"	onkeypress="return validar2(event)" readonly/>
									</div>
									<div class="col-2">
										Sanci&oacute;n
										<input name="DCD_SANCION" type="text" id="DCD_SANCION" style="width: 120px;"
											class="paso03 form-control" value="0" onkeypress="return validar2(event)" readonly/>
									</div>
									<div class="col-2">
										Devoluci&oacute;n
										<input name="DCD_DEVOL" type="text" id="DCD_DEVOL" style="width: 120px;"
											value="0" class="paso03 form-control" onkeypress="return validar2(event)" readonly/>
									</div>
									<div class="col-2">
										Amort. Anticipo
										<input name="DCD_AMORT" type="text" id="DCD_AMORT" style="width: 120px;"
											value="0" class="paso03 form-control" onkeypress="return validar2(event)" readonly/>
									</div>
									<div class="col-2">
										Retenci&oacute;n
										<input name="DCD_RETENCION" type="text" id="DCD_RETENCION" style="width: 120px;"
											value="0" class="paso03 form-control"	onkeypress="return validar2(event)" readonly/>
									</div>
									<div class="col-2">
										Neto
										<input name="DCD_NETO" type="text" id="DCD_NETO" value="0" style="width: 120px;"
											class="paso03 form-control" onkeypress="return validar2(event)" readonly/>
									</div>
								</div>
								<div class="row d-flex justify-content-center mt-2">
									<div class="col-auto">
										<input type="button" id="Agregar" value="Agregar" onclick="validaFID();" class="btn btn-secondary"/>
									</div>
									<div class="col-auto">
										<div id="divImprime" style="font-size: 18px">
											<img src="imagenes/Imprimir.png" width="33" height="28"	onClick="cmdImprimir('Contrarecibo');" class="btn btn-secondary"/>
											Contrarecibo
										</div>
									</div>
								</div>
								<div class="row d-flex">
									<div class="col-12" align="left">
										Concepto:
										<textarea id="DCD_CONCEPTO" name="DCD_CONCEPTO" class="paso03 form-control"  onkeypress="return validar(event)" readonly></textarea>
									</div>
								</div>
								<br>
								<table id="grdFacturas" align="left">
									<thead align="left">
										<tr>
											<th nowrap>
												No. Factura
											</th>
											<th nowrap>
												Fecha Factura
											</th>
											<th>
												Cve Beneficiario
											</th>
											<th>
												Tipo operac
											</th>
											<th>
												Tasa IVA
											</th>
											<th nowrap>
												Importe Neto
											</th>
											<th nowrap>
												Importe Bruto
											</th>
											<th>
												IVA desglose
											</th>
											<th>
												IVA
											</th>
											<th>
												ISR
											</th>
											<th>
												Reten 0.5
											</th>
											<th>
												Reten 0.2
											</th>
											<th>
												Impuesto Cedular
											</th>
											<th>
												Penas
											</th>
										</tr>
									</thead>
									<tbody>
									</tbody>
								</table>
							</div>
							<div id="tabs-5">

								<table class="display" id="grdCompromisos" >
									<thead>
										<tr>
											<th>
												Cuenta
											</th>
											<th>
												Subcuenta
											</th>
											<th>
												Saldo
											</th>
										</tr>
									</thead>
									<tbody>
									</tbody>
								</table>
							</div>
							
						</div>
					</div>


				</div>
			</div>

			<div id="dialog-form" title="Aplicación Presupuestal/Contable">	
				<div id="divEspera" align="center">Espere por favor....
				  <img border="0" src="../imagenes/espera.gif" height="30">
				</div>
				<div id="divAplica" >				
					<iframe id="ifAplica" src="about:blank"></iframe>
				</div>
			</div>
			<div id="dialog-Procesando" title="Procesando">
	  			<div id="divEsperaProcesando" style="visibility: hidden" align="center">Espere por favor....
				  <img border="0" src="../imagenes/espera.gif" height="30">
				</div>
			</div>
  			<div id="dialog-Cancela" title="Cancela Documento">
	  			<div id="divRechazoMat" style="visibility: hidden">Motivo de Rechazo
				  <textarea id="cMotivoRechazo" name="cMotivoRechazo" cols="40" rows="5" class="form-control"></textarea>
				</div>					
				<div id="divEsperaCancelando" style="visibility: hidden" align="center">Espere por favor....
					<img border="0" src="../imagenes/espera.gif" height="30">
				</div>
			</div>
			<div id="dialog-validaOLI" title="Validación de OLI">
				<h1>Digite el Número de OLI a Validar</h1>
	  			<input style="text-align: right;" name="cOLI" type="text" id="cOLI" value="" size="6" maxlength="6" />
			</div>	

		<!-- Dialogo Firmantes -->
		<jsp:include page="Firmantes.jsp"></jsp:include>
					
<!--VGC 20140909 Dialogo que mostrara una datatable con las solicitudes con remanente a comprobar. Por validar que solo muestre las solicitudes seguna la vista del usuario.  -->	
				<div class="modal fade" tabindex="-1" role="dialog" id="DialogGastosPorComprobar" data-mdb-keyboard="true" data-mdb-backdrop="static">
				  <div class="modal-dialog modal-lg" role="document">
				    <div class="modal-content">
					    <div class="modal-header">
					        <h5 class="modal-title">Solicitudes por comprobar</h5>
					        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
					    </div>
					    <div class="modal-body">
					      	<label style="font-size: 11px; font-weight: bold;"> Seleccione una solicitud dando clic en ella y despues clic en aceptar</label>
							<div id="dtSolDiv">
								<table id="tblSolicitudes" class="display" align="center"> 
									<thead>
										<tr>
											<th> #Solicitud
											</th>
											<th> Concepto
											</th>
											<th> Remanente
											</th>
											<th> Meta
											</th>
										</tr>
									</thead>
								</table>
							</div>
					    </div>
						<div class="modal-footer">
				       		<button type="button" id="btnAceptarEdicion" onclick="aceptarDialogSolicitudes();" class="btn btn-primary">Aceptar</button>
				        	<button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cerrar</button>
				       </div>
			    </div>
			  </div>
			</div>
				<div class="modal fade" tabindex="-1" role="dialog" id="dialog-validaFact" data-mdb-keyboard="true" data-mdb-backdrop="static">
				  <div class="modal-dialog modal-lg" role="document">
				    <div class="modal-content">
				      <div class="modal-header">
				        <h5 class="modal-title">Facturas.</h5>
				        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
				      </div>
				      <div class="modal-body">
							<div id="uploadFacturasDiv" >
								<iframe id="uploadFacturasFrm" src="UploadFacturasRG.jsp?tipo_pago=RELACIONGASTOS" align="top" frameborder="0" height="468" width="100%">
								</iframe>
							</div>
							<div id="facturasCapturadasDiv">
								<div class="row">
									<div class="col-6">
										<h6>Facturas Capturadas.</h6>
									</div>
									<div class="col-6" align="right">
										<a href="#" onclick="togleDivFacts(1);return false;">Cargar Facturas</a>
									</div>
								</div>
								
											<table id="grdValidaFacturas" width="95%">
													<thead>
														<tr>
															<th style="width: 400px;">Factura</th>
															<th style="width: 60px;">Importe Bruto</th>
														</tr>
													</thead>
												</table>
												<div class="row g-2 align-items-center">
													<div class="col-auto">
													Total de las facturas:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
													</div>
													<div class="col-auto">
														<input type="text" style="text-align: right;" name="mTotalFacturaV" class="form-control"
														id="mTotalFacturaV" value="0.00" size="15" maxlength="15" readonly />
													</div>
												</div>											
											
							</div>
						</div>
					<div class="modal-footer">
			       		<button type="button" id="btnAceptarFacturas" onclick="aceptarFacturas();" class="btn btn-primary">Aceptar</button>
			        	<button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cerrar</button>
			       </div>
			    </div>
			  </div>
			</div>
			<div id="dialog-folioCaja" title="Captura de Folio Caja">
				<fieldset>
					<table>
						<tr>
							<td>Folio</td>
							<td>
								<input type="text" id="cFolioCaja" name="cFolioCaja" size=10 onkeypress="return validar2(event);" onblur="quitaPunto();" class="form-control"/>
							</td>
						</tr>
					</table>
				</fieldset>
			</div>
		</form>
		
		<!--VGC20150930 Cambios para FID -->
		<div id="capturaFID_DIV">
				<h6>Capture Datos FID</h6>
				<div class="row">
					<div class="col-12">
						Programa:
						<select id="cProgramaDesc" onchange="cambiaPrograma()" class="form-select"></select>
					</div>
				</div>
				<div class="row">
					<div class="col-12">
						Subprograma:
						<select id="cSubProgramaDesc" class="form-select"></select>
					</div>
				</div>
		</div>
		

		<!-- Dialogo donde se ve el detalle de la agenda  -->
<div class="modal" tabindex="-1" role="dialog" id="dglAgendaComision" data-mdb-keyboard="true" data-mdb-backdrop="static">
  <div class="modal-dialog modal-xl" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <h6 class="modal-title">Agenda de la Comisión</h6>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
      </div>
      <div class="modal-body">
		<div class="row">
			<div class="col-md-6">				
	  			<div class="form-group row">
		  			<label class="col-6 col-form-label" for="mPasaje">ID Comision Viatico </label>
	  				<div class="col-5">								  		
		   				<div class="input-group">
    						<span class="input-group-text"><i class="bi bi-hash"></i></span>
          					<input type="text" class="form-control col-2 inputDetalle" id="nIdComisionViatico" name="nIdComisionViatico" value="0" readonly />			              					
	              		</div>
              		</div>            	
				</div>
			</div>
		</div>
		</br>
      		<div class="row">
				<div class="row">
					<div class="col-md-6">
						<div class="card">
						  	<h5 class="card-header">Detalle de Comisión</h5>
							<div class="card-body" id="cardBodyDetalleRG">
							  	<div class="form-group row">
								  	<label class="col-6 col-form-label" for="mPasaje">Pasajes </label>
								  	<div class="col-5">								  		
									   	<div class="input-group">
							    			<span class="input-group-text"><i class="bi bi-bus-front"></i></span>
			              					<input type="text" class="form-control col-2 inputDetalle" id="mPasaje" name="mPasaje" value="0.00" onblur="sumaDetalle();sumaTotales();"/>			              					
						              	</div>
					              	</div>
					            </div>
					            <div class="form-group row">
					              	<label class="col-6 col-form-label" for="mTaxi">Taxi </label>
				              		<div class="col-5">
									   	<div class="input-group">
							    			<span class="input-group-text"><i class="bi bi-taxi-front"></i></span>
			              					<input type="text" class="form-control col-2 inputDetalle" id="mTaxi" name="mTaxi" value="0.00" onblur="sumaDetalle();sumaTotales();" />
						              	</div>
					             	</div>
					            </div>
					            <div class="form-group row">
					              	<label class="col-6 col-form-label" for="mPeaje">Combustible y peaje</label>
				              		<div class="col-5">
									   	<div class="input-group">
							    			<span class="input-group-text"><i class="bi bi-fuel-pump"></i></span>
			              					<input type="text" class="form-control col-2 inputDetalle" id="mPeaje" name="mPeaje" value="0.00" onblur="sumaDetalle();sumaTotales();" />
						              	</div>
						            </div>
					            </div>
					            <div class="form-group row">
					              	<label class="col-6 col-form-label" for="mHotel">Factura(s) de Hotel </label>
					              	<div class="col-5">
									   	<div class="input-group">
							    			<span class="input-group-text"><i class="bi bi-building"></i></span>
			              					<input type="text" class="form-control col-2 inputDetalle" id="mHotel" name="mHotel" value="0.00" onblur="sumaDetalle();sumaTotales();"/>
						              	</div>
						            </div>
					            </div>
					            <div class="form-group row">
					              	<label class="col-6 col-form-label" for="mConsumos">Consumos </label>
					              	<div class="col-5">
									   	<div class="input-group">
							    			<span class="input-group-text"><i class="bi bi-cash"></i></span>
			              					<input type="text" class="form-control col-2 inputDetalle" id="mConsumos" name="mConsumos" value="0.00" onblur="sumaDetalle();sumaTotales();"/>
						              	</div>
						              </div>
					            </div>
					            <div class="form-group row">
					              	<label class="col-6 col-form-label" for="mOtros">Otros </label>
								   	<div class="col-5">
									   	<div class="input-group">
							    			<span class="input-group-text"><i class="bi bi-coin"></i></span>
			              					<input type="text" class="form-control col-2 inputDetalle" id="mOtros" name="mOtros" value="0.00" onblur="sumaDetalle();sumaTotales();"/>
						              	</div>
						              </div>
					            </div>
					            <hr>
					            <div class="form-group row">
					              	<label class="col-6 col-form-label" for="mTotal1">Total Comision </label>
					              	<div class="col-5">
									   	<div class="input-group">
							    			<span class="input-group-text"><i class="bi bi-plus-square"></i></span>
			              					<input type="text" class="form-control col-2 inputDetalle" id="mTotal1" name="mTotal1" value="0.00" readonly/>
						              	</div>
						            </div>
					            </div>
					            
							</div>
							<div class="card-body" id="cardBodyDetalleCaja">
								<div class="form-group row">
					              	<label class="col-6 col-form-label" for="mTotal1">Total </label>
					              	<div class="col-5">
									   	<div class="input-group">
							    			<span class="input-group-text"><i class="bi bi-plus-square"></i></span>
			              					<input type="text" class="form-control col-2 inputDetalle" id="mTotal2" name="mTotal2" value="0.00" readonly/>
						              	</div>
						            </div>
					            </div>
							</div>
						</div>
					</div>
					<div class="col-md-6">
						<div class="card">
						  	<h5 class="card-header">Detalle de Transporte Local</h5>
						  	<div class="card-body">
						    	<div class="form-group row">
								  	<label class="col-6 col-form-label" for="mPasajeLocal">Pasajes </label>
								  	<div class="col-5">
									   	<div class="input-group">
							    			<span class="input-group-text"><i class="bi bi-bus-front"></i></span>
			              					<input type="text" class="form-control col-2 inputDetalle" id="mPasajeLocal" name="mPasajeLocal" value="0.00" onblur="sumaTransporte();sumaTotales();"/>
						              	</div>
						             </div>
					            </div>
					            <div class="form-group row">
					              	<label class="col-6 col-form-label" for="mTaxiLocal">Taxi </label>
					              	<div class="col-5">
									   	<div class="input-group">
							    			<span class="input-group-text"><i class="bi bi-taxi-front"></i></span>
			              					<input type="text" class="form-control col-2 inputDetalle" id="mTaxiLocal" name="mTaxiLocal" value="0.00" onblur="sumaTransporte();sumaTotales();"/>
						              	</div>
						            </div>
					            </div>
					            <div class="form-group row">
					              	<label class="col-6 col-form-label" for="mPeajeLocal">Peaje y Estacionamiento</label>
								   	<div class="col-5">
									   	<div class="input-group">
							    			<span class="input-group-text"><i class="bi bi-fuel-pump"></i></span>
			              					<input type="text" class="form-control col-2 inputDetalle" id="mPeajeLocal" name="mPeajeLocal" value="0.00" onblur="sumaTransporte();sumaTotales();"/>
						              	</div>
						            </div>
					            </div>
					             <div class="form-group row">
					              	<label class="col-6 col-form-label" for="mGasolinaLocal">Combustible</label>
								   	<div class="col-5">
									   	<div class="input-group">
							    			<span class="input-group-text"><i class="bi bi-fuel-pump"></i></span>
			              					<input type="text" class="form-control col-2 inputDetalle" id="mGasolinaLocal" name="mGasolinaLocal" value="0.00" onblur="sumaTransporte();sumaTotales();"/>
						              	</div>
						            </div>
					            </div>
					            <div class="form-group row">
					              	<label class="col-6 col-form-label" for="mMaritimoLocal">Maritimos</label>
								   	<div class="col-5">
									   	<div class="input-group">
							    			<span class="input-group-text"><i class="bi bi-tsunami"></i></span>
			              					<input type="text" class="form-control col-2 inputDetalle" id="mMaritimoLocal" name="mMaritimoLocal" value="0.00" onblur="sumaTransporte();sumaTotales();"/>
						              	</div>
						            </div>
					            </div>
					             <div class="form-group row">
					              	<label class="col-6 col-form-label" for="mAereoLocal">Pasajes Aereos</label>
								   	<div class="col-5">
									   	<div class="input-group">
							    			<span class="input-group-text"><i class="bi bi-airplane"></i></span>
			              					<input type="text" class="form-control col-2 inputDetalle" id="mAereoLocal" name="mAereoLocal" value="0.00" onblur="sumaTransporte();sumaTotales();"/>
						              	</div>
						            </div>
					            </div>
								<hr>
					            <div class="form-group row">
					              	<label class="col-6 col-form-label" for="mTotalLocal">Total Transporte </label>
									<div class="col-5">
									   	<div class="input-group">
							    			<span class="input-group-text"><i class="bi bi-plus-square"></i></span>
			              					<input type="text" class="form-control inputDetalle" id="mTotalLocal" name="mTotalLocal" value="0.00" readonly/>
						              	</div>
						            </div>
					            </div>
							</div>
						</div>
					</div>							
			</div>		
		</div>
	  </div>
		<div class="modal-footer">
       		<button type="button" id="btnAceptarAgenda" onclick="actualizaAgendaComision();" class="btn btn-primary">Actualizar</button>
        	<button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cerrar</button>
       </div>
    </div>
  </div>
</div>

	
<!-- DIV Para capturar correo -->
<div class="modal" tabindex="-1" role="dialog" id="dialog-actualizaCorreo" data-mdb-keyboard="true" data-mdb-backdrop="static">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <h6 class="modal-title">Actualiza correo del proveedor</h6>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
      </div>
      <div class="modal-body">
      	<h6>Favor de capturar los siguientes datos para seguimiento del PPD:</h6>
       				<div class="row">
						<div class="col-6">
							<label for="cRFCFactura">RFC</label>
							<div class="input-group">
								<span class="input-group-text"><i class="bi bi-check-circle-fill"></i></span>
								<input type="text"  id="cRFCFactura" name="cRFCFactura" placeholder="RFC" class="form-control" readonly/>
							</div>
						</div>
					</div>
					<div class="row">
						<div class="col-12">
							<label for="nombreCorreo">Nombre</label>
							<div class="input-group">
								<span class="input-group-text"><i class="bi bi-person"></i></span>
								<input type="text"  id="nombreCorreo" name="nombreCorreo" placeholder="Nombre" class="form-control"/>
								<input type="text"  id="aPCorreo" name="aPCorreo" placeholder="Apellido Paterno" class="form-control"/>
								<input type="text"  id="aMCorreo" name="aMCorreo" placeholder="Apellido Materno" class="form-control"/>
								
							</div>
						</div>
					</div>
					<div class="row">
						<div class="col-12">
							<label for="cargo">Cargo</label>
							<div class="input-group">
								<span class="input-group-text"><i class="bi bi-briefcase"></i></span>
								<input type="text" id="cargo" name="cargo" class="form-control" placeholder="Cargo"/>
							</div>	
						</div>
					</div>
					<div class="row">
						<div class="col-12">
							<label for="correoActual">Correo</label>
							<div class="input-group">
						        <div class="input-group-prepend">
						          <div class="input-group-text">@</div>
						        </div>
						        <input type="text" class="form-control" id="correoActual" name="correoActual" placeholder="Correo electrónico"/>
						    </div>
						</div>		
					</div>	
      </div>
      <div class="modal-footer">
        <button type="button" id="btnAceptarCorreo" onclick="validarCorreo();" class="btn btn-primary">Aceptar</button>
        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cerrar</button>
      </div>
    </div>
  </div>
</div>

<div class="modal" tabindex="-1" role="dialog" id="dialog-AnticipoGreenMex" data-mdb-keyboard="true" data-mdb-backdrop="static">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <h6 class="modal-title">Solicitudes de Ingreso</h6>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
      </div>
      <div class="modal-body">
      	<h6>Seleccione una solicitud dando clic en el cuadro de selección.</h6>
      	
       	<table id="tblSolicitudesIngreso" class="display"> 
				<thead>
					<tr>
						<th> id</th>
						<th> #Solicitud</th>
						<th> Concepto</th>
						<th style="text-align: left;"> Remanente</th>
						<th style="text-align: left;"> RFC</th>
					</tr>
				</thead>
				<tbody></tbody>
			</table>
		
		<br/>
 		
 		<div class="row">
			<div class="col-4">
				<label for="cargo">Importe a Comprobar:</label>
			</div>
			<div class="col-4">				
				<input type="hidden" name="totalAcumulado" id="totalAcumulado" value = "0.00"/>
				<input type="text" id="mImporteComprobar" name="mImporteComprobar" class="form-control" value="0.00" readonly/>				
			</div>
		</div>								
		
      </div>
      <div class="modal-footer">
        <button type="button" id="btnAceptarCorreo" onclick="guardarAnticipoIngGreenMex();" class="btn btn-primary">Aceptar</button>
        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cerrar</button>
      </div>
    </div>
  </div>
</div>

		<div id="DivFormAnexo">
			<form id="formAnexo" name="formAnexo" action="../admin/SeguridadCatalogos" target="_blank" method="post">
				<input type="hidden" id="catalogo" name="catalogo" value="ANEXO"/>
				<input type="hidden" id="accion" name="accion" value="run"/>
				<input type="hidden" id="rn" name="rn" value="Anexo1.jasper"/>
				<input type="hidden" id="swhere" name="swhere" value=""/>
			</form>
		</div>
		
		
<div class="modal" tabindex="-1" role="dialog" id="dlg-CalendarioMontosRG" data-mdb-keyboard="true" data-mdb-backdrop="static">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title">Calendario de Movimientos</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
      </div>
      <div class="modal-body">
        <div class="form-group row">
				<label for="epShowRG">EP:</label>
				<div class="col-sm-12 col-form-label">
					<input type="text" id="epShowRG" size="60" class="form-control"  readonly />
				</div>
			</div>
			<div class="form-group row">
				<label for="epShowRG">Disponible:</label>
				<div class="col-sm-6 col-form-label">
					<input type="text" id="disponibleEPTotalRG" class="form-control" readonly />
				</div>
			</div>
			<div class="row">
				<div class="col-sm-6 col-form-label">
					<label for="montoEjercerRG">Monto a Ejercer:</label>
					<input type="text" id="montoEjercerRG" class="form-control money" onChange="validaOlis();"  onKeyPress="return onlyNumbers(event)"/>
				</div>
				<div class="col-sm-6 col-form-label">
					<label for="montoRetencionRG">Monto Retenciones:</label>
					<input type="text" id="montoRetencionRG" class="form-control money" onKeyPress="return onlyNumbers(event)" onChange="validaRetenciones();" value="0.00"/>
				</div>
			</div>
      </div>
      <div class="modal-footer">
        <button type="button" id="btnAceptarDlgEP" class="btn btn-primary">Aceptar</button>
        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cerrar</button>
      </div>
    </div>
  </div>
</div>
		
		<script type="text/javascript" src="js/jquery-1.6.2.min.js" charset="UTF-8"></script>
		<script type="text/javascript" src="js/jquery.dataTables.js" charset="UTF-8"></script>
		<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js" charset="UTF-8"></script>
		<script type="text/javascript" src="js/jquery.ui.datepicker-es.js" charset="UTF-8"></script>
		<script type="text/javascript" src="js/bootstrap.min.js"></script>
		<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js" charset="UTF-8"></script>
		<script type="text/javascript" src="../Ayudas/js/autoCompleta.js" charset="UTF-8"></script>
		<script type="text/javascript" src="js/jquery.formatCurrency.js" charset="UTF-8"></script>
		<script type="text/javascript" src="js/jquery.formatCurrency.all.js" charset="UTF-8"></script>
		<script type="text/javascript" src="../js/catalogo/general.js" charset="UTF-8"></script>
		<script type="text/javascript" src="js/validaciones.js" charset="UTF-8"></script>
		<script type="text/javascript" src="js/jquery.form-2.94.js" charset="UTF-8"></script>
		<script type="text/javascript" src="js/Firmantes.js"></script>
		<script type="text/javascript" src="js/crud.js" charset="UTF-8"></script>
		<script type="text/javascript" src="js/ValidaMesContable.js" charset="UTF-8"></script>
		<script type="text/javascript" src="js/jquery.blockUI-2.70.0.js" charset="UTF-8"></script>
		<script type="text/javascript" src="js/FacturaRelGastos.js" charset="UTF-8"></script>
		<script type="text/javascript" src="js/ActualizaFIEL.js" charset="UTF-8"></script>
		<script type="text/javascript" src="js/sweetalert2.all.min.js" charset="UTF-8"></script>
		<script type="text/javascript" src="../plantillasCasos/ComponentesPago/js/CapturaMovimientos.js"></script>
		<script type="text/javascript" charset="utf-8">
		
		var muestraDivImprimePoliza = false;
		var esConsulta = <%=esConsulta%>;
		var permitePagoSinFIEL = <%=permitePagoSinFIEL%>;
		var bClicBtn = false;
		var bAptdoCancel = false;
		var breturnVal = false;
		var bCOMSOC = false;

		var cNombreElabora = "<%=cNombreElabora%>";
		var cApellidoPaternoElabora  = "<%=cApellidoPaternoElabora%>";
		var cApellidoMaternoElabora  = "<%=cApellidoMaternoElabora%>";
		var cPuestoElabora = "<%=cPuestoElabora%>";
		
		var tipoRelacion = "";
		var creditoExt23 = true;
		var oTablevFact;
		var habilitaCBRadicado = <%=habilitaCBRadicado%>;
		var muestraCBRadicado = <%=muestraCBRadicado%>;
		var relaciones;
		var validaSaldoIP = <%=validaSaldoIP%>;
		
		/*FAV20171019 Se guarda en base el prefijo de CxP*/
		var cxpPrefijo = "<%=cxpPrefijo%>";
		var modalCorreo;
		var modalSolicitudes;
		var myModal;
		var modalFactura;
		var modalAnticipoGreenMex;
		var numeroEmpleado = "<%=numeroEmpleado%>";
		var eventoQueretaro;

		$(document).ready(function() {
			$('.currency').blur(function() {
				$('.currency').formatCurrency();
			});
			
			if( permitePagoSinFIEL ) {
				$("#AutorizaConFielTD").css("display","block");
			}else{
				$("#autorizadoPorFiel ").val("true");
				
			}
			
			$("#divGreenMex").hide();
				
			queryFormPost({
				queryName : "tipoAutorizacionRead",
				async:false,
				callback: function(){
					if( $("#TipoAutorizacion").val() == "N" ){
						muestraDivImprimePoliza = true;
					}
				}
			});
			
			querySelectPost("CatalogoOtrasRetencionesRead", "cOtraRetencion",  {async: false});
			querySelectPost("CatalogoRetencionesRead", "cTipoRetencion",  {async: false});
			
			$("#cTipoRetencion").change(function(){ 
				$("#nTipoRetencion").val($("#cTipoRetencion").val());
				
				if ($("#nTipoRetencion").val()==0) {
					$("#mImporteIva").val("0.00");
					$("#mImporteISR").val("0.00");
				
				} else {
					queryFormPost("calculaRetencionIVA", {async: false});
					queryFormPost("validaResico",{async: false });
					
					if($("#esResico").val() > 0 ){
						queryFormPost("readPorcRetencionISR", {async: false});
					} else {
						queryFormPost("calculaRetencionISR", {async: false});	
					}	
					
					$("#mImporteIva").val(Number(quitaFmt($("#mImporteSinIVA").val()) * $("#nPorcRetencionIva").val()).toFixed(2) );
					$("#mImporteISR").val(Number(quitaFmt($("#mImporteSinIVA").val()) * $("#nPorcRetencion").val()).toFixed(2) );
				}
			});
			
			$("#cOtraRetencion").change(function(){ 
				$("#nTipoRetencion").val($("#cOtraRetencion").val());
				
				if ($("#nTipoRetencion").val()==0) {
					$("#mOtras").val("0.00");
				} else {
					queryFormPost("calculaOtrasRetenciones", {async: false});				
					$("#mOtras").val(Number(quitaFmt($("#mImporteSinIVA").val()) * $("#nPorcRetencionOtras").val()).toFixed(2) );
				}
			});
			
			$("#trReferenciaBancaria").css("visibility","hidden");
			document.getElementById("solCaja").style.display = "none";
			$("#lblSolicitud").hide();

			$("#chk_RB").change(
					 	function(){
							referenciaBancaria();
						}
					);
			
			$("#btnBeneficiario").button();
			$("#Borrar").button();
			$("#Limpia").button();
			$("#Agregar").button();
			$("#agrega2").button();
			//$("#editar").button();
			//$("#btnActualizaDatos").button();
			$("#nIdClaveEgresos2").button();
			$("#btnSeleccionarBoletos").button();
			$("#btnAgregarBoleto").button();
		
			$("#editaimporte").hide();
			$(".RetISR").hide();	
			
			$("#oficioDelegatorioCaptura").hide();
			$("#oficioDelegatorioCapturaUpdate").hide();
			
			$( "#dFechaOficio" ).datepicker({
				showOn: "button",
				buttonImage: "images/calendar.gif",
				buttonImageOnly: true
			});
				
			$( "#dFechaOficioUpdate" ).datepicker({
				showOn: "button",
				buttonImage: "images/calendar.gif",
				buttonImageOnly: true
			});
			
			$( "#fInicioComision" ).datepicker({
				showOn: "button",
				buttonImage: "images/calendar.gif",
				buttonImageOnly: true
			});
			
			$( "#fFinComision" ).datepicker({
				showOn: "button",
				buttonImage: "images/calendar.gif",
				buttonImageOnly: true
			});
			$("#oficioDelegatorioVoBo").hide();
			$("#oficioDelegatorioVoBoUpdate").hide();
			$( "#dFechaOficioVoBo" ).datepicker({
				showOn: "button",
				buttonImage: "images/calendar.gif",
				buttonImageOnly: true
			});
				
			$( "#dFechaOficioVoBoUpdate" ).datepicker({
				showOn: "button",
				buttonImage: "images/calendar.gif",
				buttonImageOnly: true
			});
			
			//VGC20151228 Si esta activado el CB lo muestra habilitado.
			if( habilitaCBRadicado )
				$("#chk_radicado").attr('disabled', false);
			else
				$("#chk_radicado").attr('disabled', true);
			
			$("#divRetenciones").hide();
			$("#chk_tieneRete").change(function(){ 
				if ($("#chk_tieneRete").prop("checked")) {
					Swal.fire("Atencion!","Debe cargar únicamente las facturas con retenciones diferentes a RESICO", "info");
					$("#divRetenciones").show();
				} else {
					$("#divRetenciones").hide();
				}
			});
				
			//VGC20150930 Crea el dialog que mostrara las solicitudes con remanente a comprobar
			creaDialogoFID();
			modalCorreo = new bootstrap.Modal(document.getElementById('dialog-actualizaCorreo'), 'data-bs-backdrop');
			modalSolicitudes = new bootstrap.Modal(document.getElementById('DialogGastosPorComprobar'), 'data-bs-backdrop');
			modalFactura = new bootstrap.Modal(document.getElementById('dialog-validaFact'), 'data-bs-backdrop');
			modalAnticipoGreenMex = new bootstrap.Modal(document.getElementById('dialog-AnticipoGreenMex'), 'data-bs-backdrop');
			modalAgendaComision = new bootstrap.Modal(document.getElementById('dglAgendaComision'), 'data-bs-backdrop');
			myModal = new bootstrap.Modal(document.getElementById('dlg-CalendarioMontosRG'), 'data-bs-backdrop');
			
			//VGC2016019 Parametriza si se muestra o no el checkbox de radicado
			if( !muestraCBRadicado ){
				$("#divRadicado").hide();
				$("#chk_radicado").attr("checked", false);
				$("#chk_radicado").change();
				$("#cEsRadicado").val("N");
			}
			
			$("#lblComision").hide();

			$("#btnAceptarDlgEP").button().click(function() {
				guardaMontosRG();
			});
							
			$( "#dialog-Cancela" ).dialog({
				autoOpen: false,
				height: 300,
				width: 450,
				modal: true,
				buttons: {
						"Aceptar": function() 
						{
								$("#divEsperaCancelando").attr("style","visibility=visible");
								queryFormPost("leerPagoApartado", {async: false});
								var nFolioPagado = $("#nFolioApartado").val();
								queryFormPost("pagoApartadoAplicado", {async: false});
								$("#divRechazoMat").hide();
								$("#divEsperaCancelando").attr("style","visibility=visible");
								$("#cIdDocumento").val( $("#cIdRelacion").val() );
								$("#cMotivoRechazoCRUD").val( $("#cMotivoRechazo").val() );
								$("#nIdEstado").val( "5" );

				     			$("#noFolio").val( "RELG-" + "<%=cUR%>" + "-" + $("#id_caso").val() );
				     			$("#nfolio").val( $("#id_caso").val() );
				     			$("#cIdMotivoCancelacion").val( "1" );
				     			$("#motivo").val( $("#cMotivoRechazo").val() );
				     			$("#cIdRFC").val( $("#cIdRFC_RelacionGasto").val() );
				     			$("#cnombreRFC").val( $("#cnombre").val() );
				     			
								alert("Documento Rechazado: " + $("#caNoContrarrecibo").val() );

								queryFormPost("mRelaciongastosUpdate,tRelGastosEncCancelaUpdate", {async: false });
								parent.document.getElementById("pb_send").disabled = false;
								parent.document.getElementById("pb_send").click();		
							
						},
						
						"Cancelar": function() {
							bAptdoCancel = false;
							$("#divEsperaCancelando").hide();
							$("#divRechazoMat").attr("style","visibility=hidden");
							$( this ).dialog( "close" );							
						}
					},
				close: function() {										
				},
				open: function() {
						//if ($("#cOrigen").val() == "MATERIALES"){
							$("#divRechazoMat").attr("style","visibility=visible");
						//}
				}
			});
		
					
			$( "#dialog-Procesando" ).dialog(
				{
				autoOpen: false,
				height: 400,
				width: 400,
				modal: true,
				async: false,
				open: function() {
					var tipo = "aplicarMotor";
					var caNoContrarrecibo = $("#caNoContrarrecibo").val();
					var campo = $("#campo").val();
					var tablaEnc = $("#tablaEnc").val();
					var campoCondicion = $("#campoCondicion").val(); 
					var tablaDet = $("#tablaDet").val(); 
					var tipoAplicar = $("#tipoAplicar").val();
					
					var numEmpleadoCaptura = numeroEmpleado;
					var numEmpleadoVoBo = $("#cboVoBo").val();
					var numEmpleadoAut = $("#cboAutoriza").val();
					
					var autorizadoPorFiel = $("#autorizadoPorFiel").val();
					
					$("#divEsperaProcesando").attr("style","visibility=visible");
					var fAppActualizada = actualizaMesAplicacion();
					
					if( !fAppActualizada){
						$("#divEsperaProcesando").attr("style","visibility=hidden");
						$( "#dialog-Procesando" ).dialog( "close" );
						return false;
					}
				 	//URVP.19082014 EN CASO DE SER INGRESOS PROPIOS Y POLIZA DE TIPO DIARIO SE HACE EL UPDATE DEL TIPO DE POLIZA
					if ($("#tipo_fuente").val()=="IP"){
						queryFormPost("tipoPolizaIngresosPropios", {async : false});
						if($("#tipoPoliza").val()=="DI")
							queryFormPost("updatetipoPolizaIngresosPropios", {async : false});
					}
				 	
					/*Validar si la retencion insertada en el detalle corresponde al regimen fiscal del proveedor */			
					queryFormPost("validaRegimenFiscal", {async: false});
					var msgRF = $("#msgRF").val();
					
					if ( msgRF != "" ){
						alert(msgRF);
						$("#dialog-Procesando").dialog("close");
						return; 			
					}
					
					/* Asegura que el importe Neto del detalle el igual al importe Neto del encabezado*/
					queryFormPost("actualizaImporteNetoRG", {async: false});
					
					
					/*Validar si el regimen es 626 y el tipo persona es Moral no debe tener retencion RESICO*/			
					queryFormPost("validaRESICOPersonaMoral", {async: false});
					var msgRF = $("#msgRF").val();
					
					if ( msgRF != "" ){
						alert(msgRF);
						$("#dialog-Procesando").dialog("close");
						return; 			
					}
					
					//if ($("#tipo_fuente").val()=="IP" || tipoAplicar == "RELACIONGASTOS"){
						
						$.ajax({
							url:'./cierrePresupuestal.jsp',
							type:'post',
							dataType: 'json',
							data:{
								tipo:tipo,
								caNoContrarrecibo:caNoContrarrecibo,
								campo:campo,
								tablaEnc:tablaEnc,
								campoCondicion:campoCondicion,
								tablaDet:tablaDet,
								tipoAplicar:tipoAplicar,
								empleadoCaptura : numEmpleadoCaptura,
								empleadoVoBo : numEmpleadoVoBo,
								empleadoAutoriza : numEmpleadoAut,
								autorizadoPorFiel : autorizadoPorFiel
							},
							async: false,
							success:function(data){
									if(data.sinSesion == 'sinSesion'){
											location.href = "../index.jsp";
									}
									if(data.estatus == "guardado"){
											/* JGDS Se agrega este crud para ver si tiene contrarrecibo y no crearlo*/
											var tieneContrarecibo = undefined;
											var contrareciboRegenerado = false;													
											queryFormPost({
												queryName : "tieneContrarrecibo",
												async:false,
												callback: function(){
													 tieneContrarecibo = ( $("#correcto").val() == "1" );
												}
										   });
											
											if( tieneContrarecibo == undefined )
												throw "No se pudo determinar si el pago tiene contrarecibo";
											
											if(!tieneContrarecibo){
												queryFormPost({
													queryName : "insertaContrarreciboRG",
													async:false,
													callback: function(){
														contrareciboRegenerado = true;
													}
											   });
												if(!contrareciboRegenerado)
													throw "El pago no genero contrarecibo. Descarte el tramite y reinicie";
											}
										
										if(<%=id_oper==1%>){
											if (bCOMSOC){
												creaCasoComsoc($("#cDocumento").val(), $("#id_caso").val());	
											}
											
											alert( "Documento Aplicado Correctamente: "+ $("#caNoContrarrecibo").val() ) ;
											parent.document.getElementById("pb_send").disabled = false;
											parent.execOperacion();
											parent.execResponsable();
											parent.document.getElementById("pb_send").click();
											
											
										}else{
											$("#docAplicado").val( "S" );
											
											
											if( $("#autorizadoPorFiel").val() != "true" )
												cmdImprimir('PolizaPago');
													
											alert( "Documento Aplicado Correctamente: "+ $("#caNoContrarrecibo").val());
											
											$("#cTipoPago").val("RELACIONGASTOS");
											queryFormPost("cBuscaRadicado", {async: false });
											
											if ($("#cEsRadicado").val()=="S"){
												queryFormPost("devengadoIngresoPago",{async: false });
											}
											
											queryFormPost("updateCampoFacturaDC", {async: false }); 
											parent.document.getElementById("pb_send").disabled = false;
											parent.document.getElementById("pb_send").click();
											
										}	
										$("#cIdDocumento").val( $("#cIdRelacion").val() );
										$("#cMotivoRechazoCRUD").val( "" );
										$("#nIdEstado").val( "4" );
		
										queryFormPost("mRelaciongastosUpdate", {async: false });
										
										breturnVal = true;
									}else{
										breturnVal = false;
										alert( "El Documento No Se Aplico: "+$("#caNoContrarrecibo").val() + " - " + data.estatus ) ;		
									}
									$("#divEsperaProcesando").attr("style","visibility=hidden");
									$( "#dialog-Procesando" ).dialog( "close" );
							}
						
						});
					/*
					} else {
						if(<%=id_oper==1%>){
							if (bCOMSOC){
								creaCasoComsoc($("#cDocumento").val(), $("#id_caso").val());	
							}
							
							alert( "Documento Aplicado Correctamente: "+ $("#caNoContrarrecibo").val() ) ;
							parent.document.getElementById("pb_send").disabled = false;
							parent.execOperacion();
							parent.execResponsable();
							parent.document.getElementById("pb_send").click();
							
							
						}else{
							$("#docAplicado").val( "S" );
							
							
							if( $("#autorizadoPorFiel").val() != "true" )
								cmdImprimir('PolizaPago');
									
							alert( "Documento Aplicado Correctamente: "+ $("#caNoContrarrecibo").val());
							
							$("#cTipoPago").val("RELACIONGASTOS");
							queryFormPost("cBuscaRadicado", {async: false });
							
							if ($("#cEsRadicado").val()=="S"){
								queryFormPost("devengadoIngresoPago",{async: false });
							}
							
							queryFormPost("updateCampoFacturaDC", {async: false }); 
							parent.document.getElementById("pb_send").disabled = false;
							parent.document.getElementById("pb_send").click();
							
						}
						$("#divEsperaProcesando").attr("style","visibility=hidden");
						$( "#dialog-Procesando" ).dialog( "close" );
					}
					*/
				},
				close: function() {
				}				
			});
		
			$( "#dialog-validaOLI" ).dialog({
				autoOpen: false,
				height: 250,
				width: 250,
				modal: false,
				buttons: {
						"Aceptar": function() 
						{
							var vEP = $("#epShowRG").val();
							var vCapitulo = "%" + vEP.substring(31, 36) + "%";
							var vCartera = vEP.substring(44, 55);
							var vUnidEj = vEP.substring(56, 59);
							var vUnidRe = vEP.substring(60, 64);
							var url = '../proimpro/readInfo';
							$.ajax({
								url : url,
								dataType : 'json',
								data : {
									"accion" : "READ_IMPORTEOLI",
									"cartera" : vCartera,
									"capitulo" : vCapitulo,
									"oli": $("#cOLI").val(),
									"ue":vUnidEj
								},
								async : false,
								success : function(RS) {
									var exito = RS.success;
									if(exito=="true"){
										var MontoOlis = RS.data_1.result;
										if (Number( MontoOlis ) > 0){
											$("#cartera").val( vCartera );
											$("#capitulo").val( vEP.substring(31, 36) );
											$("#UE").val( vUnidEj );
											$("#mImporteCartera").val("0.00");
											queryFormPost("vAcumulaImporteCarteraEP", {async: false });
											var importeTotal = Number( $("#mImporteCartera").val() ) + Number( quitaFmt($("#mMovimiento").val()) );
											var importeTotal = importeTotal.toFixed(2);
											
											if( Number( importeTotal ) > Number( MontoOlis ) ){
												alert("El Importe Capturado sobregira la Cartera ");
											}
										}else{
											alert("El Número de OLI no es Válido ");
										}
									}else{
										var msg = RS.data_1.result;
										alert("Error al intentar validar OLI:\n"+msg);
									}
								},
								error : function(xhr, textStatus, errorThrown) {
									alert("Advertencia: " + xhr.responseText + "\nEstatus: "
											+ textStatus + "\n" + errorThrown);
									r = true;
								}
							});
							$( this ).dialog( "close" );
						},
						"Cancelar": function() {
							breturnVal = false;
							$( this ).dialog( "close" );							
						}
					},
				close: function() {										
				}							
			});

	
	$("#dialog-folioCaja").dialog({
		autoOpen: false,
		height: 150,
		width: 200,
		modal: true,
		buttons: {
			"Aceptar": function() {
			
				if ($("#polManual").val() != "0"){
					$(this).dialog("close");
					return;
				}
				if($.trim($("#cFolioCaja").val()) == ""){
					alert("Favor de Capturar el Folio");
					return;
				}
				$("#folioCajaUsado").val($("#cFolioCaja").val());
				queryFormPost("buscaFolioDispReclasif", {async: false });
				if ($("#folioDisponibleReclasif").val()!="1"){
					alert("El folio " + $("#cFolioCaja").val() + " no puede ser usado debido a que no existe o ya fue utilizado.");
					return;
				}
				if (confirm("¿Desea guardar el folio " + $("#cFolioCaja").val() + " en esta Relacion de Gastos?")){
					try {
						queryFormPost({
							queryName : "guardaFolioCaja ", 
							async : false, 
							callback : function(){
								$("#polManual").val($("#cFolioCaja").val());
								$("#cFolioCaja").attr("readonly","readonly");
								//cssReadOnly();
								alert("Se guardo el Folio Correctamente.");
							} 
						});
					} catch (e) {
						alert("No se guardo el folio.");
						return;
					}
				}
				$(this).dialog("close");
			},
			"Cancelar": function() {
				$(this).dialog("close");
			}
		},
		close: function(){}				
	});
	
			$( "#dialog-form" ).dialog({
				autoOpen: false,
				height: 400,
				width: 800,
				modal: true,
				beforeClose: function( event, ui ) {
					return bClicBtn;			
				}
			});

			$('#cConcepto').bind('copy paste', function (e) {       
				e.preventDefault();
	    	});


			var bCarga = false;
	    	var aaa =<%=request.getParameter("folio")%>;				
			//var Cont1=<%=request.getParameter("folio")%>;		
			
			$("#id_caso").val(aaa);
			$("#cUnidadResponsable").val( "<%=cUR%>" );       
			$("#cUnidadEjecutora").val( "<%=cUR%>" );       
			$("#usur").val( "<%=usu%>" );
			$("#cUnidadResponsable2").val( "<%=cUR%>" );
			$("#cCentroContable").val( "<%=cCentroContable%>");

		  	$("#tabs").tabs( {"show": function(event, ui) {var oTable = $('div.dataTables_scrollBody>table.display', ui.panel).dataTable();
            		if ( oTable.length > 0 ) {
                		oTable.fnAdjustColumnSizing();
            		}
        			}
    			} );
			
		  	$(".fsproductsStcokistButton[href*='javascript']").hide();
			$("input.AyudaSyC").subIniciaDlg();	
			$("input.autoCompletaSyC").subIniciaAutoCompleta();
			$("#fmFactDocDiversos").ajaxForm({dataType:  "json",success: formSubmited});
			$('#grdRetencion').dataTable(
				{         
					"iDisplayLength": 20,
					"bPaginate": false,
        			"bLengthChange": false,
        			"bFilter": false,
        			"bSort": false,
        			"bInfo": false,
        			"bAutoWidth": false, 
					"sScrollY": 100,         
					"bJQueryUI": true,    
					"bRetrive" : true,
					"bDestroy" : true,
					"sPaginationType": "full_numbers"    

				} );
			$('#grdRetClave').dataTable(
				{         
					"iDisplayLength": 20,
					"bPaginate": false,
        			"bLengthChange": false,
        			"bFilter": false,
        			"bSort": false,
        			"bInfo": false,
        			"bAutoWidth": false, 
					"sScrollY": 100,         
					"bJQueryUI": true,    
					"bRetrive" : true,
					"bDestroy" : true,
					"sPaginationType": "full_numbers"    

				} );
			$('#grdMovimientos').dataTable(
				{         
					"iDisplayLength": 20,
					"sScrollY" : "150px",
					"sScrollX" : "700px",
					"bPaginate": false,
        			"bLengthChange": false,
        			"bFilter": false,
        			"bSort": false,
        			"bInfo": false,
        			"bAutoWidth": true, 
					"bJQueryUI": true,    
					"bRetrive" : true,
					"bDestroy" : true,
					"sPaginationType": "full_numbers"    

				} );
		reiniciaCompromisos();
		$('#grdFacturas').dataTable(
			{         
				"bPaginate": false,
       			"bFilter": false,
       			"bSort": false,
       			"bInfo": false, 
				"bJQueryUI": true,    
				"bRetrive" : true,
				"bDestroy" : true,
				"sPaginationType": "full_numbers"    
			} );
		$("#nfoliorelacionGastofactura").attr('disabled', true);
	
		$('#tblSolicitudesIngreso').dataTable(
				{
					"bPaginate": false,
        			"bLengthChange": false,
        			"bFilter": false,
        			"bSort": false,
        			"bInfo": false,
        			"bAutoWidth": false,
					"bJQueryUI": true,
					"bRetrive" : true,
					"bDestroy" : true,
					"sPaginationType": "full_numbers"

				} );
				
	$("#UEjecutora").change(function () {
		var cUe = $(this).val();
			$("#cUnidadResponsable").val( cUe );       
			$("#cUnidadEjecutora").val( cUe );       
			$("#cUnidadResponsable2").val( cUe );
	});
	
	creaDiagloFacturas();
	creaDTFacturas();
	queryFormPost({
		queryName:"readImporteNetoPagoDiverso", 
		async:false, 
		callback:function(){
			if( $("#mImporteNeto").val() == "" )
				$("#mImporteNeto").val("$0.00");
			$(".subtotall"). change();
		} 
	});
	
		
	$("#chk_EditaImporte").change(function(){
		if ($("#chk_EditaImporte").prop("checked")){
			Swal.fire('Nota', 'Al usar esta opcion, el importe a editar es el leido de las facturas.', 'info');			
			
			if ($.trim($("#cIdRFC_RelacionGasto").val())==""){
				$("#chk_EditaImporte").prop("checked",false);
				Swal.fire('Importante', 'Primero debes de selecccionar el RFC', 'warning');	
				return;
			}
			
			if (Number($("#mImporteNeto").val())==0){
				$("#chk_EditaImporte").prop("checked",false);
				Swal.fire('Importante', 'Primero se deben de cargar las facturas', 'warning');
				return;
			}
			
			if (Number($("#importeEdicion").val())==0){
				$("#mImporteBruto").val(parseFloat(Number(quitaFmt($("#importeEdicionMaximo").val())) + Number(quitaFmt($("#importenoComprobable").val()))+Number(quitaFmt($("#mImporteRetencion").val()))).toFixed(2));
			} else {
				$("#mImporteBruto").val(parseFloat(Number(quitaFmt($("#importeEdicion").val())) + Number(quitaFmt($("#importenoComprobable").val())) +Number(quitaFmt($("#mImporteRetencion").val()))).toFixed(2));	
			}
			
			document.getElementById("importeEdicion").removeAttribute("disabled",false);
			$("#mImporteNeto").val(parseFloat(Number(quitaFmt($("#importeEdicionMaximo").val()))+Number(quitaFmt($("#importenoComprobable").val()))).toFixed(2));
		}else{
			$("#importeEdicion").val("0.00");
			$("#importeEdicion").attr("disabled",true);
			$("#mImporteNeto").val($("#importeAuxNeto").val());
		}			
	});
	
	$("#chk_radicado").change(function(){
		if ($("#chk_radicado").prop("checked"))
			$("#cEsRadicado").val("S");
		else
			$("#cEsRadicado").val("N");
	});
	
});//fin del ready

function muestraJson(){
		for( i = 0; i < relaciones.length; i++ )
			alert( relaciones[i].folioIngreso );
	}




//VGC 20140916 Funcion en doble click
function dialogGastosPorComprobarAceptar(event){

	var aPos = tablaSolicitudes.fnGetPosition(event.target.parentNode);
	var aData = tablaSolicitudes.fnGetData(aPos);
	
	var noCajaSel = aData[0];
	$("#tmpNoCaja").val(noCajaSel);
	
	var meta = aData[3];
	$("#cMeta").val(meta);
	
	if( validaRemanenteRG() ){
		$("#noSolicitudCaja").val( noCajaSel );
		$("#tmpNoCaja").val("");
		modalSolicitudes.hide();
	}else{
		Swal.fire("Verifique","El monto de la relacion de gastos supera el monto remanente de la solicitud seleccionada","warning");
	}
}

function habilitaGuardar(elPar){
	if (elPar == 1){
		$("#operacio").val(elPar);
		$("#grpAutorizar").val("Si");
	}
	else{
		$("#operacio").val(elPar);
		$("#grpAutorizar").val("No");
	}
}

function onSubmit(id_oper){ 
	
  		var p = window.parent;
  		var valida_campos = true;
		if ($("#docAplicado").val() == "S") {
			alert("Documento ya fue aplicado, se avanzará a modo de CONSULTA");
		}		
		else {
			try{
				//validaciones de la forma
				//poner aqui las validaciones a efectual en el boton guardar, en este caso no hay
	
				//Guardado de los campos correspondientes a cada variable de caso
				p.gestion.setFolio( $("#FOLIO").val() );
				p.gestion.setOperador( $("#OPERADOR").val() );
				p.gestion.setFechaDocumento( $("#FECHA_CARGA").val() );
				p.gestion.setEjercicioFiscal( $("#aEjercicioFiscal").val() );
				p.gestion.setConceptoMov("Aplicación Relacion de Gastos");
				p.gestion.setMoneda("MXP");//el presupuesto siempre es en pesos
				p.gestion.setFechaApCont( $("#FECHA_CARGA").val() );
				p.gestion.setAplicadoCont("false");
				
				
				
				if(id_oper==1){
					
					var esEFO = validaEFO();
					if( esEFO ){
						alert("No se puede realizar el pago a un EFO. Solicite mas informacion con el administrador");
						return false;
					}
						
					if (cmdGuardar()) {
						//alert('error al guardar información');
						return false;
					}
				}

				if(id_oper==2){
					if ( $("#grpAutorizar").val() == "Si" ) {
					  	if ($( "#txtFechaPago" ).val() == ""){
					  		alert("Falta Capturar la Fecha de Pago");
					  		return false;
					  	}
						
						var bValidaTipoFacturaFecha = validaTipoFacturaFecha();
						if ( !bValidaTipoFacturaFecha ){
							return false;
						}

					  	$("#COMSOCAutoriza").val("");
						queryFormPost("BuscaCOMSOCAutorizaRead", {async : false});
						
						if ( $("#COMSOCAutoriza").val() == "0" ){
							alert("Pago en Proceso de Autorización del Área Normativa");
							return false;
						}
						
						if ( $("#COMSOCAutoriza").val() == "-1" ){
							alert("Pago Rechazado por el Área Normativa");
							return false;
						}
						
						// VALIDAR QUE NO SOPREPASE EL SALDO ARRASTRE CUANDO SEA EL DESTINO GASTO: REPOSICION DE CAJA CHICA (RCRE) O CANCELACION DE CAJA CHICA (CCRE)
						if($("#DESTINO_GASTO").val() == "RCRE" || $("#DESTINO_GASTO").val() == "CCRE"){
							
							$("#validaRCRE").val("0");
							queryFormPost("validaSaldoDestinoGasto_RCRE_Read",{async: false });
							
							if($("#validaRCRE").val() == "1"){
								Swal.fire("No se puede continuar.","El importe excede el Saldo en Caja Chica.","error");
								return;
							}
						}						
						
						
						/*
						if ( $( "#cCentroContable" ).val() == "10" ){
						  	$("#Fecha_Pago").val( $( "#txtFechaPago" ).val() );
						  	$("#elcontraTemp").val($("#caNoContrarrecibo").val());
						  	if($("#elcontraTemp").val().substring(0,4) != $("#cCentroContable").val() + cxpPrefijo ){
						  		getNextSequenceVal({seqName: "CR-" + $("#cCentroContable").val(), async: false, callback: setSequenceVal});	
						  	}						
						  	$("#elcontra").val($("#caNoContrarrecibo").val());
						  	
							queryFormPost("tRelacionGtosEncFPagoUpdate,tContraReciboFPagoUpdate,tDocCompFPagoUpdate",{async: false });
					  	}
						*/
						parent.document.getElementById("pb_save").disabled=true;
						
						$("#campo").val( "nFolio" + $("#cDocumento").val() );
						$("#tablaEnc").val( "t" + $("#cDocumento").val() + "Encabezado" );
						$("#campoCondicion").val( "caNoContrarrecibo" ); 
						$("#tablaDet").val( "t" + $("#cDocumento").val() + "Detalle" ); 
						$("#tipoAplicar").val( $("#cDocumento").val() );
						
						tipoFirmantes();
						
					}else {
					  	$("#COMSOCAutoriza").val("");
						queryFormPost("BuscaCOMSOCAutorizaRead", {async : false});
						
						if ( $("#COMSOCAutoriza").val() == "0" ){
							alert("Pago en Proceso de Autorización del Área Normativa, No es posible Realizar Rechazo");
							return false;
						}
						
						if ( $("#COMSOCAutoriza").val() == "1" ){
							alert("Pago Autorizado por el Área Normativa, No es posible Realizar Rechazo");
							return false;
						}

						CancelaApartado();
						
						queryFormPost("borrarEdoCtaGreenMexRGDelete", {async: false });
					}

				}
			}
			catch (e) {
				window.alert("onSubmit: Error: " + e.message);
				return false;
			}
		
		}
		return valida_campos;
  	}

 	function fnAplicaMotor()
 	{
 		divAplica.innerHTML = "<iframe id='ifAplica' src='../gstnmngr/AppCont?elContra=" + $('#caNoContrarrecibo').val()+"&cllave="+$("#cllave").val()+"&numeroC= " + $("#id_caso").val() +"&tipodoc=RG" + "' style='BORDER: white 1px solid; WIDTH: 90%; HEIGHT: 90%;FONT-FAMILY: Verdana,Tahoma ;'> </iframe>";
 	}


 	function CancelaApartado(){
		$( "#dialog-Cancela" ).dialog( "open" );
		return bAptdoCancel;
 	}
 	

	function onPostSubmit(id_oper){//validaciones del boton enviar
  		return true;
  	}
  	
  	function ResponsableSiguiente(id_oper){
  		 if(id_oper==1){

  		 	return "AUTORIZA_" + $("#cDocumento").val();
			}
  		 if(id_oper==2){
			
  		 	return "CONSULTA_" + $("#cDocumento").val();
			}
  				
  	}
  	  	
  	function OperacionSiguiente(id_oper){
  		if(id_oper==1)
  		 	return "autoriza_factura";
  		if(id_oper==2)
  		 	return "consulta_factura";
  	}

	function onPostDisplay(id_oper){
		if ($("#docAplicado").val() == "S") {
			parent.execOperacion();
			parent.execResponsable();
			parent.document.getElementById("pb_send").disabled = false;
			parent.document.getElementById("pb_send").click();
		}
	}
	
	
	function formSubmited() {
                
            }
	
	function cmdGuardar() {
		let id = $("#idAgenda").val();
		$("#nombre").val($("#cIdRelacion").val());
    	$("#DCD_IMP_BRUTO").val($("#mImporteNeto2").val());
	   	$("#nMes").val($("#fAplicacion").val().split("/")[1]); 
		$("#cMes").val($("#nMes").val());
						
		if ($("#numPaso").val() == '1'){
			
			if ($.trim($("#ctaBancaria option:selected").text())==""){
				Swal.fire({
					  icon: 'error',
					  title: 'No se puede proceder con el pago',
					  text: 'El beneficiario ' + $("#cIdRFC_RelacionGasto").val() + ' no tiene dada de alta alguna cuenta bancaria'
					})
				return;
			} 
			
			if( $("#TIPO_OPERACION").val() == "" || $("#TIPO_OPERACION").val() == "-1"){
				Swal.fire({
					  icon: 'error',
					  title: 'Capturar',
					  text: 'Debe seleccionar el tipo de pago.'
					})
				$("#TIPO_OPERACION").focus();
				return false;
			}
			
			if( $("#TIPO_MOVIMIENTO").val() == "" || $("#TIPO_MOVIMIENTO").val() == "-1"){
				Swal.fire("Capturar","Debe capturar tipo de movimiento", "warning")
			}
			
			if( $("#tConcepto").val() == "" || $("#tConcepto").val() == "-1"){
				Swal.fire("Capturar","Debe capturar el Tipo de Concepto", "warning")
			}
			/*
			if ($("#tipo_fuente").val() != "IP" && ( $("#cFolioSuficiencia").val() == "" || $("#cFolioSuficiencia").val() == "-1") ){
				Swal.fire("Capturar","Debe capturar la Suficiencia", "warning")
				return;
			}
			*/
			if (!validaTipoRetencion()) {
				return false;
			}
				
			if (!solicitudesSinRetencion()) {
				return false;
			}
					
			if (!validaCalculoResico()) {
				return false;
			}
			
			if (!validaRegimenFacturasRG()) {
				return false;
			}
			// VALIDAR QUE NO SOPREPASE EL SALDO ARRASTRE CUANDO SEA EL DESTINO GASTO: REPOSICION DE CAJA CHICA (RCRE) O CANCELACION DE CAJA CHICA (CCRE)
			if($("#DESTINO_GASTO").val() == "RCRE" || $("#DESTINO_GASTO").val() == "CCRE"){
				
				$("#validaRCRE").val("0");
				queryFormPost("validaSaldoDestinoGasto_RCRE_Read",{async: false });
				
				if($("#validaRCRE").val() == "1"){
					Swal.fire("No se puede continuar.", "El importe excede el Saldo en Caja Chica.","error");
					return;
				}
			}
			
			var mensaje = validaCapturaCorreo();
			if (mensaje != "")
				hayError += "\n" + mensaje;
			
        	var hayError = '';
        	$('.paso01').each(function(){
		    	 var cnameCol = '';
			  	if ($(this).val()==''){
			  		cnameCol = this.name + ', ';
					if("<%=cUR%>" != "A02" && $( "#cCentroContable" ).val() == "10" && this.name == "txtFechaPago"){
						cnameCol = "";
					}
					hayError = hayError + cnameCol ;
				}//nuevo
       	  	});
			$('.AyudaSyC').each(function(){
				if ($(this).val()==''){
					hayError = hayError + this.name+', ';
				}
			});	
			
			if (hayError==''){
				/*
				if ($("#DESTINO_GASTO").val() == "CCRE" || $("#DESTINO_GASTO").val() == "CERE" ||$("#DESTINO_GASTO").val() == "CPRP"
						|| $("#DESTINO_GASTO").val() == "GCRE" ||$("#DESTINO_GASTO").val() == "RCRE") {
					$("#mImporteNeto").val($("#importeEdicionMaximo").val());

				} else {
					$("#importeEdicionMaximo").val($("#mImporteNeto").val());
					
				}
				*/
				var ineto = $("#mImporteNeto").val(); 
				ineto = ineto.replace("$", "");
				ineto = ineto.replace(",", "");
				if (ineto==0)
				{
					alert("El importe no puede ser 0");
					return false;
				}			
				
				if ($("#DESTINO_GASTO").val() == "CPRP"){
					queryFormPost("borraNoComprobable",{async: false });
					queryFormPost("readImporteIvaFacturaRG",{async: false });
				
				}  
				$("#editaimporte").attr('disabled', true);

				
				$("#ID_DESTINO_GASTO").val( $("#DESTINO_GASTO").val() );
				$("#idDestinoGasto").val( $("#DESTINO_GASTO").val() );
				
				//ARLA 21062024. SI ES EL PAGO DE ISN Y EL RFC DEL GOBIERNO DE QUERETARO SE CAMBIA EL EVENTO PARA APLICAR EL PAGADO MANUAL
				if ($("#DESTINO_GASTO").val() == "2NRP" && $("#cIdRFC_RelacionGasto").val() == "GEQ790916MJ0"){								
					eventoQueretaro = "2NRQ";
					$("#ID_DESTINO_GASTO").val(eventoQueretaro);
					queryFormPost("tContratoRelacionGastosEncabezadoISNCreate",{async: false });
				} else {				
		   	  		queryFormPost("tContratoRelacionGastosEncabezadoCuentasCreate",{async: false });
				}
		   	  	queryFormPost("tRelacionGastosEncabezadoUpdate,tRELACIONGASTOSEncabezadoUpdateReferenciaBancaria",{async: false });

		   	  	if ($("#mImporteRetencion").val() !="0.00") {
		   	  		queryFormPost("tActualizaImporteMasIvaRG",{async: false });
		   	  	}
		   	  	
				$("#UEjecutora").attr('disabled', true);//URVP
				$("#cIdRelacion").attr('readonly', true);//URVP
				$("#TIPO_OPERACION").attr('disabled', true);//URVP
				$("#DESTINO_GASTO").attr('disabled', true);//URVP
				$("#mImporteNeto").attr('readonly', true);//URVP
				$("#cConcepto").attr('readonly', true);//URVP
				$("#txtFechaPago").attr('readonly', true);//URVP
				$("#btncIdRFC_RelacionGasto").attr('disabled', true);//URVP
				$("#tipo_fuente").attr('disabled', true);//URVP	
				$("#cReferenciaBancaria").attr('readonly', true);				
				$("#TIPO_MOVIMIENTO").attr('disabled', true);//JGDS
				$("#tConcepto").attr('disabled', true);//JGDS
				$("#chk_RB").attr('disabled', true);
				$("#ctaBancaria").attr('disabled', true);
              	$("#btnBeneficiario").attr('disabled', true); //Se deshabilita el boton de los beneficiario para no poder cambiar unaa vez guardada la caratula
              	
              	$("#chk_radicado").attr('disabled', true);
              	if ($("#cEsRadicado").val()=="S")
              		queryFormPost("updateEsRadicadoRG",{async: false });
              		
              	Swal.fire("OK","Carátula guardada!", "success");              	
              	
              	if ($("#DESTINO_GASTO").val() == "CPRP"){
					$("#DCD_IVADES").val(parseFloat($("#mImporteIvaTotal").val()).toFixed(2));
					$("#DCD_IMP_BRUTO").val(Number(Number($("#DCD_IMP_BRUTO").val())-Number($("#mImporteIvaTotal").val())).toFixed(2));
				}
				 
              	
              	$("#cIdRFC_RelacionGasto2").val($("#cIdRFC_RelacionGasto").val());  
				queryFormPost("tRelacionGastClaveBeneficiarioRead","cIdRFC_RelacionGasto2",{async: false });
			
				$("#numPaso").val(5);
				$("#fRecepcion2").val($("#fRecepcion").val());
				$("#DCD_FECHA_FACTURA").val($("#fRecepcion2").val());
				$("#DCD_FACTURA").val($("#nombre").val());
				$("#DCD_IMP_BRUTO").val($("#mImporteBruto").val());
				$("#DCD_NETO").val(Number($("#mImporteBruto").val()) - Number($("#mImporteRetencion").val()));
				$("#DCD_RETENCION").val($("#mImporteRetencion").val());
				$("#DCD_CONCEPTO").val($("#cConcepto").val());
			  	$(".pasoDos").show();
				$("#L03").click();
				iniciaCapturaMovimientos();
				
				//ARLA.28102021 Se consulta el catalogo de destino gasto con tipo de poliza para validar si aplica la actualizacion a DI
				queryFormPost("ReadCatalogoTipoPoliza",{async: false });				
				if($("#cTipoPoliza").val() == "DI"){
					queryFormPost("updatetipoPolizaDiario",{async: false });
				}
			}
			else
			{
				Swal.fire({
					  icon: 'info',
					  title: 'Faltan datos',
					  text: 'Debe ingresar los siguientes datos: '+ hayError
					})
			}
		
		} else if ($("#numPaso").val() == '5'){
			if (validacionImportes()) {
				agregarDetalle();
				 
			}
			
			//if ($("#tipo_fuente").val()=="IP"){					
				queryFormPost("consultaFolioApartado",{async: false });
				if ($("#nFolioApartado").val() ==""){	
					Swal.fire("Error","No se genero el folio del apartado","error");
					return false;
				}
			//}
			
		} else if ($("#numPaso").val() == '6'){
			queryFormPost("tRelacionGastosConsultaCXP",{async: false });
			$("#campo").val( "nFolioPagoApartado" );
			$("#tablaEnc").val( "tPagoApartadoEncabezado" );
			$("#campoCondicion").val( "caNoContrarrecibo" ); 
			$("#tablaDet").val( "tPagoApartadoDetalle" ); 
			$("#tipoAplicar").val( "PAGOAPARTADO" );
			if ( procesar() ){
				$("#Borrar").attr('disabled', true);
				$("#Borrar").hide();
				$("#Limpia").attr('disabled', true);
			}
		}
	}
	
function setSequenceAptd(seqValue) {
	$("#nFolioApartado").val( seqValue );
}

function confolio() {
	 queryFormPost("tRelacionGastoEncabezadoRead",{async: false });
	 queryFormPost("tRelacionGastoConceptoRead",{async: false });
	 $("#elcontra").val($("#caNoContrarrecibo").val());
	 
	 if ($("#TIPO_OPERACION").val()== -1) {
		 	document.getElementById("tConcepto").style.display = "block";
			document.getElementById("TIPO_MOVIMIENTO").style.display = "block";
			document.getElementById("id_mov").style.display = "none";
			$("#id_concepto").css('visibility', 'hidden');
	 } else {
		 	document.getElementById("tConcepto").style.display = "none";
			document.getElementById("TIPO_MOVIMIENTO").style.display = "none";
			document.getElementById("id_mov").style.display = "block";
			$("#id_concepto").css('visibility', 'visible');
	 }
	 
	 queryFormPost("tPagoApartadoEncabezadoRead",{async: false });
 	if ($( "#txtFechaPago" ).val() == "01/01/1900"){
 		$( "#txtFechaPago" ).val("");
 	}
	 setTimeout("retraso()",100);
	 		 
	  
	 if($("#cInformeComision").val() != ""){
	 	$("#informeComision").val($("#cInformeComision").val());
	 	if (<%=id_oper%> == 3 ){
	 		//$("#EditaInformeComision").css('visibility', 'visible');
	 		$("#VerDetalleAgenda").css('visibility', 'visible');
	 	}		
	 }
	 
	
}   

function retraso(){   
	if ( $("#cIdRelacion").val()!=""){
		$(".pasoDos").show();
		$(".paso01").attr('disabled', true);
		$("#mImporteNeto").attr('readonly', true);
		$("#numPaso").val('2');
		$("#cIdRFC_RelacionGasto2").val($("#cIdRFC_RelacionGasto").val());  
		queryFormPost({	queryName : "tRelacionGastFinanciamientoRead", async : false, 
			callback : function(){ 
			
									queryFormPost("tRelacionGastClaveBeneficiarioRead","cIdRFC_RelacionGasto2",{async: false });
									$("#numPaso").val('2');
									$(".pasoDos").show();
									iniciaCapturaMovimientos();
									$("#L02").click();
									$(".pasoTres").hide();
									$(".pasoULTIMO").hide();
									setTimeout("retraso2()",100);
								} 
		});
		
	}else{
		
	   $(".pasoDos").hide();
	   $(".pasoTres").hide();
	   $(".pasoULTIMO").hide();
	 }
}

function BuscaPoliza(){
	var szWhere = " nFolioRelacionGastos = " + $("#id_caso").val();
	var elMonto = "1";
	$("#laPoliza").val("");
	$("#docAplicado").val("");
	var szTabla = "TPAGORELGDOCAPLICADOREAD";
	$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, MaxReg: elMonto, ajax: 'false'}, function(j){
		for (var i = 0; i < j.length; i++) {
			$("#laPoliza").val(j[i].Col0);
			$("#docAplicado").val(j[i].Col1);
		};
	});
}	


function muestraCamposSolicitud(mostrar){
	
	if( mostrar ){
		document.getElementById("solCaja").style.display = "block";
		//document.getElementById("btnActualizaDatos").style.display = "block";
		$("#lblSolicitud").show();
	}else{
		document.getElementById("solCaja").style.display = "none";
		//document.getElementById("btnActualizaDatos").style.display = "none";
		$("#lblSolicitud").hide();
	}
	
}

function concepto(){		
	
		$("#TIPO_CONCEPTO").val($("#tConcepto").val());
		querySelectPost("CatalogoRGTMovimendoRead", "TIPO_MOVIMIENTO",{async: false }); 
		if ($("#tConcepto").val() == "AL"){
			querySelectPost("CatalogoAlmacenRead","ALM", {async: false });
			$("#altaAlmacen").val("");
			$("#ALM").show();
			$("#cAnioFactEP").val("");
			$("#nFacturaEP").val("");
		}
		else{
			$("#altaAlmacen").val("");
			$("#altaAlmacen").hide();
			querySelectPost("CatalogoAlmacenVacioRead","ALM", {async: false });
			$("#ALM").hide();
			$("#cAnioFactEP").val("");
			$("#nFacturaEP").val("");
			$("#cAnioFactEP").hide(); 
			$("#nFacturaEP").hide();
	
		}
		concepto2();
		$("#EP").val('');
		$("#mMovimiento").val('');
		$("#noSolicitudCaja").val('');
		
		if(($("#tConcepto").val()=="DD" || $("#tConcepto").val()=="DDV") && $("#DESTINO_GASTO").val() == "GCRE"){
			//Si es una comprobacion de gasto, muestra el div con las relaciones de gastos con remanente por comprobar para saber a quien restar la comprobacion.
			createDTSolicitudesPendientes();
			$("#tmpNoCaja").val("");
			modalSolicitudes.show();
			muestraCamposSolicitud(true);						
		}else if(($("#tConcepto").val()=="CAC") && $("#DESTINO_GASTO").val() == "CCRE"){
			//Si es una comprobacion de gasto, muestra el div con las relaciones de gastos con remanente por comprobar para saber a quien restar la comprobacion.
			createDTSolicitudesPendientes();
			modalSolicitudes.show();
			muestraCamposSolicitud(true);						
		}else{
			muestraCamposSolicitud(false);
		}	
	
}	   

function concepto2(){
	querySelectPost("CatalogoObraTMovimendo2Read",{async: false });
}

function cargaConceptoMov() {
	$("#idDestinoGasto").val( $("#DESTINO_GASTO").val() );
	//------------------------SE ELIMINA EL FFM PARA LOS ESTADOS Y LAS UR QUE ESTAN EN tUnidadEjecutoraSinFFM------------------------
	querySelectPost({queryName: "CatalogoObraTConceptoRead", targetObjectId: "tConcepto", async: false, callback:function(){
		queryFormPost("leeBorrarFFM", {async: false });
		if( $("#cCentroContable").val() != '10' || $("#borrarFFM").val() == '1' || $.trim($("#cIdRFC_RelacionGasto").val()) != "BMN930209927"){
			var opts = document.getElementById("tConcepto").options;
			var indexFF = -1;
			for( cntFF = 0;cntFF < opts.length; cntFF++)
				if( "FFM" == opts[cntFF].value){
					indexFF = cntFF;
					break;
				}
			if(indexFF >= 0 )
				document.getElementById("tConcepto").remove( indexFF );
		}else if ($.trim($("#cIdRFC_RelacionGasto").val()) == "BMN930209927" && $("#DESTINO_GASTO").val() == "CSSU" ){
			var opts = document.getElementById("tConcepto").options;
			for( cntFF = 0;cntFF < opts.length; cntFF++ ){
				if( "FFM" != opts[cntFF].value && "" != opts[cntFF].value )
					document.getElementById("tConcepto").remove( cntFF );
			}
		}
	}});
	concepto();
	$("#DCD_CONCEPTO").val($("#cConcepto").val());
}

function enInicio(){
	queryFormPost("tEjercicioRead",{async: false });
	$("#cEjercicio").val( $("#aEjercicioFiscal").val() );
	if ("<%=cUR%>" == "A02") {
		querySelectPost("cUnidadEjecutoraRead", "UEjecutora",{async: false });
		$("#UEjecutora").val( "A02" );
		}
	else {
	   $("#divUE").hide();
	}
	$("#fAplicacion").val( "<%=today%>" );				
	$("#txtFolioFact").val($("#id_caso").val());
	$("#mImporteNeto2").val($("#mImporteNeto").val());
	$("#DCD_IMP_BRUTO").val($("#mImporteNeto2").val());
	
	$("#divAutorizar").hide();
	$("#divImprime").hide();
	$("#divImprimePoliza").hide();
	$("#divImprimeAnexo").hide();
	$("#divImprimeJustificacion").hide();
	$("#divImprimeInforme").hide();
	
	bCarga = true;
  	querySelectPost("CAT_TIPO_IVARead", "DESCRIPCION20",{async: false });
  	querySelectPost("CAT_TIPO_OPERACIONRead", "TIPO_OPERACION",{async: false });
  	querySelectPost("CatalogoDestinoGastoRelGastos", "DESTINO_GASTO",{async: true }); 	   	
  	querySelectPost("CatalogoObraTConceptoRead", "tConcepto",{async: false });
  	
  	if ($("#ID_DESTINO_GASTO").val()==" "){//URVP.14092014 EN CASO DE TRAER VALOR, NO SE BORRA
		$("#ID_DESTINO_GASTO").val( $("#DESTINO_GASTO").val() );
		$("#idDestinoGasto").val( $("#DESTINO_GASTO").val() );
	}else{
				
		if ($("#ID_DESTINO_GASTO").val()=="GCRG"){
			querySelectPost("CatCuentasBancarias", "cuentaBanc",{async: false });	
			$(".CtaBanc").show();
		}
		else
			$(".CtaBanc").hide();	
	}
	
  	cargaConceptoMov();
	
	querySelectPost("CatalogoAlmacenRead","ALM", {async: false });
	
	$("#cTipoPoliza").val("EG"); 
	
	queryFormPost("fRecepcionRead",{async: false });

	if ($("#aEjercicioFiscal").val() >= '2013'){
       $("#fAplicacion").val( "<%=today%>" );
		$("#fRecepcion").val( "<%=today%>" );

		if ( $("#fRecepcion").val().split("/")[2] != $("#aEjercicioFiscal").val() ) {
       	$("#fRecepcion").val( "31/12/" + $("#aEjercicioFiscal").val() );
       	$("#FechaAplicacion").val( $("#fRecepcion").val() );
       	$("#FECHA_CARGA").val( $("#fRecepcion").val() );
       	$("#fAplicacion").val( $("#fRecepcion").val() );					
       }
		if ($("#fAplicacion").val()!= "<%=today%>"){
			$("#fAplicacion").hide();
			$("#fAplicacion").attr("readonly","readonly");
		}
		$("#fRecepcion").attr( "readonly","readonly" );
	}

	if (<%=id_oper%> == 2 ){
		$("#divUE").hide();
	}
	
	 if (<%=id_oper%> == 3 ){
		$("#divUE").hide();
		$("#divAutorizar").hide();
		queryFormPost("consultaEstatusRG", {async: false});
		
		if( muestraDivImprimePoliza ){
	   		$("#divImprime").show();
	   		$("#divImprimePoliza").show();		 
 	   		$("#divImprimeAnexo").show();
 	   		
 	   		queryFormPost("tieneJustificacionRG", {async: false});
 	   		if ($("#cTieneJustificacion").val() > 0 ) {
 	   			$("#divImprimeJustificacion").show();
 	   		}
 	   	}	
		
		const tab = document.getElementById("L03");
		tab.parentNode.removeChild(tab);
		
		//document.getElementById("btnAceptarInforme").style.display = "none";
		
		 if($("#cInformeComision").val() != ""){
	   			$("#divImprimeInforme").show();
	   	}
		
 	   	$("#Agregar").attr('disabled', true);
	   	$("#Limpia").attr('disabled', true);
	   	$("#Borrar").attr('disabled', true);	   
	 }		   
	   
	   	concepto();
		setTimeout("confolio()",100);
		
		if (Number($("#nEnviadoSICOP").val()) > 0 ) {
			//Poner en readonly el detalle de la Agenda
			document.getElementById("mPasaje").readOnly = true;
			document.getElementById("mTaxi").readOnly = true;
			document.getElementById("mPeaje").readOnly = true;
			document.getElementById("mHotel").readOnly = true;
			document.getElementById("mPasaje").readOnly = true;
			document.getElementById("mConsumos").readOnly = true;
			document.getElementById("mOtros").readOnly = true;
			document.getElementById("mPasajeLocal").readOnly = true;
			document.getElementById("mTaxiLocal").readOnly = true;
			document.getElementById("mPeajeLocal").readOnly = true;
			document.getElementById("mGasolinaLocal").readOnly = true;
			document.getElementById("mMaritimoLocal").readOnly = true;
			document.getElementById("mAereoLocal").readOnly = true;
			document.getElementById("btnAceptarAgenda").style.display = "none";	
	   	}

	if ($("#numPaso").val()!="1"){ 
		$("#EditaFacturas").css('visibility', 'hidden');
		querySelectPost("readCtaBancxPago", "ctaBancaria",{async: false });
		$("#ctaBancaria").attr('disabled', true);
	}
	
}

	function onLoadPlantilla(){
	  	
		var bCaptura = false;
	  		
		if(<%=id_oper>1%>){
			$("#agrega2").attr("disabled",true);
			$("#chk_radicado").attr('disabled', true);
			$("#Borrar").hide();  			
		}
	enInicio(); 
		BuscaPoliza();  
		
	if(<%=id_oper==1%>){
		if("<%=cUR%>" != "A02" && $( "#cCentroContable" ).val() == "10"){
			$( "#txtFechaPago" ).hide();
			$("#txtFechaPago").attr("readonly","readonly");
		}
			
		if( parent.document.getElementById("pb_send") ) 
			parent.document.getElementById("pb_send").style.visibility='hidden';
		
		if( parent.document.getElementById("pb_cancel") )
			parent.document.getElementById("pb_cancel").disabled=false;
		
	}
	if(<%=id_oper==2%>){
		
		$("#grpAutorizar").val("Si");
		$("#Borrar").attr('disabled', true);
		$("#Borrar").hide();
		//$("#editar").hide();
		$("#nombreComision").attr("disabled",true);
		parent.document.getElementById("pb_send").style.visibility='hidden';
		
		if( parent.document.getElementById("pb_cancel") ){
			parent.document.getElementById("pb_cancel").style.visibility='hidden';
			parent.document.getElementById("pb_cancel").disabled=true;
			
		}			
					
	}

	if(<%=id_oper==3%>){
		
		queryFormPost("leeEstatusFolioCaja",{async: false });
		if ( $("#destinoRG").val()=="CERE" && ($("#estatusRG").val()=="EJERCIDO" || $("#estatusRG").val()=="PAGADO") )
			$("#capturaFolioCaja").css('visibility', 'visible');
		
		if( muestraDivImprimePoliza ){
			$("#EditaFirmas").css('visibility', 'visible');
		}
		
		if( parent.document.getElementById("pb_cancel") ){
			parent.document.getElementById("pb_cancel").style.visibility='hidden';
			parent.document.getElementById("pb_cancel").disabled=true;
		}		
		
	}	

}
function procesar(){
	
	breturnVal = false;
	
	/*Validar la suma de retenciones del detalle vs el encabezado*/			
	queryFormPost("validaRetencionENCvsDET", {async: false});
	var msgRF = $("#msgRF").val();
	
	if ( msgRF != "" ){
		alert(msgRF);
		return breturnVal; 			
	} 
    /*ARLA SI LAS FACTURAS ESTAN BORRADAS SE HACE EL INSERT A tPagoFactura Y SE BORRAN DE tPagoFactura_Borrada*/
    queryFormPost({
                queryName: "validaExisteFacturas", 
                async: false,
                callback:function(){
                    $( "#dialog-Procesando" ).dialog( "open" );
                    breturnVal = true;
                } 
    });
	    
    return breturnVal;
}


function creaCasoComsoc(tipoDocumento, folioDocumentoPago){
	
	$.ajax({
		url : '../servlet/ComsocAutorizacionServlet',
		dataType : 'json',
		type :"POST",
		data : {
			"h_TipoPago" : tipoDocumento,
			"hFolioPago": folioDocumentoPago
		},
		async : false,
		success : function(json) {
			var exito = json.success;
			if( exito == "true"){
				alert("El Pago está en Proceso de Autorización por parte del Área Normativa");
			}
		},
		error : function(xhr, textStatus, errorThrown) {
			alert("Advertencia: " + xhr.responseText + "\nEstatus: "
					+ textStatus + "\n" + errorThrown);
			return false;
		}
	});
	
}

	function actualizarConcepto(){
		queryFormPost("actualizarConcepto",{async: false });
	}
		   
	

	function retraso2(){
			
			if(<%=id_oper%> != 3){	
				parent.document.getElementById("pb_save").disabled = true; //para CONAFOR
			}
				
	 		queryFormPost("tRelacionGastDocumentRead","caNoContrarrecibo");				
			$("#cIdRFC_RelacionGasto2").val($("#cIdRFC_RelacionGasto").val());  
			queryFormPost("tRelacionGastClaveBeneficiarioRead","cIdRFC_RelacionGasto2");						  		   
			queryFormPost("tRelacionGastosCalendarioRead", {async: false});
			var total = Number($("#mMontoEjercer").val()) - Number(quitaFrmt($("#mMontoCalendarizado").val()));
			$("#mMontoPorEjercer").val(total.toFixed(2));
			
			claves();
				
			//setTimeout("validacionImportes()",1000);	
			setTimeout("retraso3()",400);		
		
	}
	
	function validacionImportes(){
		//URVP.15092014 Se cambia validacion a que el importe acumulado sea igual al importe neto a capturar y no que $("#cllave").val()!="" ya que con tener capturada una sola ep sin importar si no es por el importe total ya no permitia seguir capturando 
		if (Number(quitaFrmt($("#mMontoPorEjercer").val()))  == 0  && Number(quitaFrmt($("#mMontoRetPendientes").val())) == 0 ){ 
  			$(".paso9").attr('disabled', true);
			$(".pasoTres").show();
		} else {
			alert("No se ha terminado de capturar todo el importe a ejercer");
			return false;
		}
		return true;
	}
		
	function retraso3(){

		  if ( $("#caNoContrarrecibo").val() !="0"  && (<%=id_oper%> == 2 )  ){
			$("#cIdRFC_RelacionGasto2").val($("#cIdRFC_RelacionGasto").val());
			queryFormPost("tPagoApartadoEncabezadoRead", {async: false });
			queryFormPost("tRelacionGastClaveBeneficiarioRead","cIdRFC_RelacionGasto2",{async: false });						  
			$("#Agregar").attr('disabled', true);   
			$("#DCD_IMP_BRUTO").val($("#mImporteNeto").val());
			$("#DCD_NETO").val($("#DCD_IMP_BRUTO").val());
			$(".paso03").attr('disabled', true);   
			$("#divAutorizar").show();
			$("#numPaso").val('6');	
			parent.document.getElementById("pb_save").disabled=false;
		}else{
			$(".paso03").attr('disabled', false); 
		}
	}	

	function eliminaEPIngresos( epBuscar){
			
			var indice = -1;
			
			for( i = 0; i < relaciones.length; i++){
				if( relaciones[i] == epBuscar ){
					indice = i;
					break;
				}
			}
			
			if(indice >= 0 )
				relaciones[i].splice(indice,1);
			
			muestraJson();
		}

	
	function limpiaFirmante(postFijo){
		$("#cNombre" + postFijo).val( "" );
        $("#cPaterno" + postFijo).val( "" );
        $("#cMaterno" + postFijo).val( "" );
        $("#cPuesto" + postFijo).val( "" );
	}
	

	var tot = 0;

	//VGC 20140912 Funcion para el clic de la tabla de solicitudes pendientes
	function fnClickTblSolicitudes(event){
		$(tablaSolicitudes.fnSettings().aoData).each(
			function (){
				$(this.nTr).removeClass('row_selected');
			});
				
		$(event.target.parentNode).addClass('row_selected');
		var aPost = tablaSolicitudes.fnGetPosition(event.target.parentNode);		
		var aData = tablaSolicitudes.fnGetData(aPost);
		var noCajaSel = aData[0];	
		$("#tmpNoCaja").val(noCajaSel);
		var meta = aData[3];
		$("#cMeta").val(meta);
	}

	// VGC 20140916 Se valida que el monto de la RG no sobrepase el monto de la relacion de gastos.
	function validaRemanenteRG(){
		var noSol = $("#tmpNoCaja").val();
		$("#remanente").val("0");
		
		queryFormPost("remanenteSolicitudRead", {async:false});
		
		var montoRG = parseFloat( quitaFrmt( $("#mImporteNeto").val() ) );
		var montoRemanente = parseFloat( quitaFrmt( $("#remanente").val() ) );
		var resta = parseFloat( montoRemanente - montoRG );
		return resta >= 0;
	}

	function quitaFrmt(fld) {
		var valcol = fld.toString();
		valcol = valcol.replace(/[$]/g, "");
		valcol = valcol.replace(/,/g, "");
		return valcol;
	}

	function fnClickAddRowRetClave(A, B, C, D) {
		$('#grdRetClave').dataTable().fnAddData( [ A, B, C, D ]);
	}

	function documentacion(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O) {
		$('#grdFacturas').dataTable().fnAddData(
				[ A, B, C, D, E, F, G, H, I, J, K, L, M, N, O ]);
	}

	function fnClickAddRowComp() {
		$("#DCD_TIPO_OPE").attr('disabled', false);//URVP.18062014 se habilita para obtener su info y se habilita nuevamente en contrarecibo()
		$("#Agregar").attr('disabled', true);
		$('#grdFacturas').dataTable().fnAddData(
			[ $("#DCD_FACTURA").val(), 
				$("#DCD_FECHA_FACTURA").val(),
				$("#DCD_CBEN").val(),
				$("#DCD_TIPO_OPE").val(), 
				$("#DESCRIPCION20").val(),
				$("#DCD_NETO").val(),
				$("#DCD_IMP_BRUTO").val(), 
				$("#DCD_IVADES").val(),
				$("#DCD_IVA").val(), 
				$("#DCD_ISR").val(),
				$("#DCD_MIL5").val(), 
				$("#DCD_MIL2").val(),
				$("#DCD_OTRAS_RET").val(),
				$("#DCD_PENALIZACION").val() 
			]);
		$("#cIdRFC_RelacionGasto2").val($("#cIdRFC_RelacionGasto").val());
		$("#RFC").val($("#cIdRFC_RelacionGasto2").val());
		$("#fRecepcion2").val($("#fRecepcion").val());
		
		setTimeout("contrarecibo()", 1000);
									
	}

	function setSequenceVal(seqValue) {
		seqValue = "000000" + seqValue;
		seqValue = seqValue.substr(seqValue.length - 6);
		seqValue = "1" + seqValue.substr(seqValue.length - 5);
		seqValue = $("#cCentroContable").val() + cxpPrefijo + $("#aEjercicioFiscal").val() + seqValue;
		$("#caNoContrarrecibo").val( seqValue );
	}

	function contrarecibo() {
		$("#cDescripcionPoliza").val( "Apartado del pago: " + $("#caNoContrarrecibo").val() );
	   	$("#FechaAplAptd").val( $("#fAplicacion").val() );
	   	
		$("#DCD_TIPO_OPE").attr('disabled', true);
		$(".paso01").attr('disabled', true);
		$("#numPaso").val( '6' );
		parent.document.getElementById("pb_save").disabled=false;
		
	}
	
	function fnClickAddRowB(A, B, C) {
		$('#grdRetencion').dataTable().fnAddData( [ A, B, C ]);
	}
	function fnClickAddRowC(A, B, C) {
		$('#grdCompromisos').dataTable().fnAddData( [ A, B, C ]);
	}

	function fnClickAddRowZ(A, B, C, D, E, F, G, H, J) {
		$('#grdMovimientos').dataTable().fnAddData( [ A, B, C, D, E, F, G, H, J ]);
	}

	
	function fnClickAddRowA() {

		var table = document.getElementById('grdMovimientos');
		var rowCount = table.rows.length;
		var yaExiste = 0;
		for ( var i = 1; i < rowCount; i++) {
			var row = table.rows[i];
			var chkbox = '';
			try {
				var chkbox = row.cells[0].childNodes[0];
			} catch (e) {
				null;
			}
			if (null != chkbox) {
				if (chkbox.toString() == $("#EP").val()) {
					yaExiste = 1;
				}
			}
		}

		if (yaExiste == 1) {
			alert("Ya se ingresó un movimiento con esa clave");
			return;
		}

		var total3 = Number($("#mMovimiento").val());
		var total2 = Number($("#total1").val());
		var t4 = total3 + total2;

		if (t4 > Number($("#mImporteNeto").val())) {
			Swal.fire("Verique Importe", "Ha superado el Importe Neto", "error");
			$("#mMovimiento").val(" ");
			return;
		}
		Number($("#total1").val(t4));
		$(".pasoTres").show();

		$('#grdMovimientos').dataTable().fnAddData(
				[ $("#EP").val(), $("#nClaveCNA1").val(), $("#mMovimiento").val(),"", "", "", "", "" ]);
		
		$("#TIPO_MOVIMIENTO").attr('disabled', true);
		queryFormPost("tRelacionGastoDetalleCreate", { async : false });
		$("#acumuladoOperacion").val( parseFloat($("#acumuladoOperacion").val()) + parseFloat($("#mMovimiento").val()));
		
	}

	function reiniciaCompromisos() {
		$('#grdCompromisos').dataTable( {
			"iDisplayLength": 20,
			"bPaginate" : false,
			"bLengthChange" : false,
			"bFilter" : false,
			"bSort" : false,
			"bInfo" : false,
			"bAutoWidth" : false,
			"sScrollY" : 100,
			"bJQueryUI" : true,
			"bRetrive" : true,
			"bDestroy" : true,
			"sPaginationType" : "full_numbers"
		});
	}

	function generar() {
		try {
			var table = document.getElementById('grdCompromisos');
			//var table2 = document.getElementById('dt_paraEnvio')       		
			var rowCount = table.rows.length;
			
			for ( var i = 0; i < rowCount; i++) {
				var row = table.rows[i];
				
				var chkbox = '';
				try {
					var chkbox = row.cells[3].childNodes[0];
				} catch (e) {
					null;
				}
				if (null != chkbox) {
					if (chkbox.toString() == $("#EP").val()) {
						
						table.deleteRow(i);
						rowCount--;
						i--;
					}
				}

				// }		
			}
		} catch (e) {
			alert(e);
		}
	}

	function ChecaSiexisteCodSif() {
		var regresa = false;
		try {
			var table = document.getElementById('grdMovimientos');
			var rowCount = table.rows.length;
			var yaExiste = 0;
			for ( var i = 0; i < rowCount; i++) {
				var row = table.rows[i];
				var chkbox = '';
				try {
					var chkbox = row.cells[2].childNodes[0];
				} catch (e) {
					null;
				}
				if (null != chkbox) {
					if (chkbox.toString() == $("#EP").val()) {
						yaExiste = 1;
					}
				}
			}
		} catch (e) {
			alert(e);
		}
		if (yaExiste == 1) {
			Swal.fire("Verifique la clave!","Ya se ingresó un movimiento con esa clave", "warning");
			regresa = true;
			return regresa;
		}
		return regresa;

	}


	$(function() {
		$("#fechaFactura").datepicker( {
			showOn : "button",
			buttonImage : "images/calendar.gif",
			buttonImageOnly : true
		});
	});
	
	$(function() {
		$("#txtFechaOficio").datepicker( {
			showOn : "button",
			buttonImage : "images/calendar.gif",
			buttonImageOnly : true
		});
	});

	$(function() {
		$("#fElegibilidad").datepicker( {
			showOn : "button",
			buttonImage : "images/calendar.gif",
			buttonImageOnly : true
		});
	});

	function validaOlis() {
		var vEp = $("#epShowRG").val();
		var vCartera = vEp.substring(44, 55);
		var vCapitulo = vEp.substring(31, 32);
		if(vCartera != "00000000000" && ( vCapitulo == "5" || vCapitulo == "6" )){
			$( "#dialog-validaOLI" ).dialog( "open" );
		}
		
	}

	

	function validar3(e) {
		tecla = (document.all) ? e.keyCode : e.which;
		if (tecla == 8)
			return true;
		//patron = /[A-Za-z.\d\s\\-]/;
		patron = /^([0-9A-Za-zÑñáéíóúÁÉÍÓÚ ]+)$/g;
		te = String.fromCharCode(tecla);
		return patron.test(te);
	}

 
	function validar(event) {
		
		 var regex = new RegExp("^[a-zA-Z0-9 ]+$");
		  var key = String.fromCharCode(!event.charCode ? event.which : event.charCode);
		  if (!regex.test(key)) {
		    event.preventDefault();
		    return false;
		 }
	 
	}

	function validar2(e) {
		tecla = (document.all) ? e.keyCode : e.which;
		if (tecla == 8)
			return true;
		patron = /[.\d]/;
		te = String.fromCharCode(tecla);
		return patron.test(te);
	}

	function anexo(){
		queryFormPost("totalEpsRG",{async: false });
		queryFormPost("cantFacturas",{async: false });
		if (($("#cllave").val()>6 || ($("#totFacturas").val()>6)) //se cambia a EP's mayor a 7 o mas de 7 facturas
			  	&& pfmt == "PolizaPago"){   	
				let whereAnexo = "and caNoContrarrecibo ='" + $("#caNoContrarrecibo").val() + "'";
			  	$("#swhere").val(whereAnexo);
				//$("#formAnexo").submit();
			  	window.open("../admin/SeguridadCatalogos?" 
						+ "catalogo=ANEXO"
						+ "&accion=run" 
						+ "&rn=Anexo1.jasper"
						+ "&swhere=  and caNoContrarrecibo ='" + $("#caNoContrarrecibo").val() + "' "
						, "Anexo",
						"scrollbars=1, resizable=yes, width=1024, height=768");
		}
	}
	
	function imprimirJustificacion() {
		window.open("../admin/SeguridadCatalogos?" 
				+ "catalogo=ANEXO"
				+ "&accion=run" 
				+ "&rn=ReporteJustificacionesFirmas.jasper"
				+ "&sWhere=" + $("#id_caso").val()
				, "Justificacion",
				"scrollbars=1, resizable=yes, width=1024, height=768");
	}
	
	function imprimirInforme() {
		window.open("../admin/SeguridadCatalogos?" 
				+ "catalogo=ANEXO"
				+ "&accion=run" 
				+ "&rn=PolizaInformeComisionRG.jasper"
				+ "&whereFolio=" + $("#caNoContrarrecibo").val()
				, "Informe",
				"scrollbars=1, resizable=yes, width=1024, height=768");
	}
	
	function imprimir(elFormato) {
		var swhere = "&folio=" + $("#caNoContrarrecibo").val();
		if(elFormato == "ComprobanteRegistro" || elFormato == "NuevoContrarecibo"){
			swhere = "&whereFolio= and CR.caNocontrarrecibo = '" + $("#caNoContrarrecibo").val() + "'";
		} 
		
		if(<%=id_oper==3%>){
			queryFormPost("totalEpsRG",{async: false });
		}
		
		$("#totFacturas").val(0);
		queryFormPost("totFacturasPago", {async: false });
		
		
		//Imprimir Solicitud
		window.open("../admin/SeguridadCatalogos?" 
			    + "catalogo=CONTRARECIBO"
				+ "&accion=run" 
				+ "&rn=" + elFormato + ".jasper" 
				+ swhere,
				"popacuse", "scrollbars=1, resizable=yes, width=1024, height=768");

		
	}
	
	function cmdImprimir(elFormato) {
		var swhere = "&folio=" + $("#caNoContrarrecibo").val();
		if ($("#docAplicado").val() == "C" && $("#caNoContrarrecibo").val().substring(0,4) == $("#cCentroContable").val() + "CT" ) {
			var vUnidad = $("#cUnidadResponsable").val();
			vUnidad = vUnidad.substring(0,3);
			$("#noFolio").val( "RELG-" + vUnidad + "-" + $("#id_caso").val() );
			cmdImprimirRCH();
			return;
		}

		if(elFormato == "ComprobanteRegistro" || elFormato == "NuevoContrarecibo"){
			swhere = "&whereFolio= and CR.caNocontrarrecibo = '" + $("#caNoContrarrecibo").val() + "'";
		} 
		
		if(<%=id_oper==3%>){
			queryFormPost("totalEpsRG",{async: false });
		}
		
		$("#totFacturas").val(0);
		queryFormPost("totFacturasPago", {async: false });
		
		//Imprimir anexo
		if (($("#cllave").val()>6 || ($("#totFacturas").val()>6)) //se cambia a EP's mayor a 7 o mas de 7 facturas
			  	 && pfmt == "PolizaPago"){   	
				let whereAnexo = "and caNoContrarrecibo ='" + $("#caNoContrarrecibo").val() + "'";
			  	$("#swhere").val(whereAnexo);
				$("#formAnexo").submit();	
			
			}
		
		//Imprimir Solicitud
		window.open("../admin/SeguridadCatalogos?" 
			    + "catalogo=CONTRARECIBO"
				+ "&accion=run" 
				+ "&rn=" + elFormato + ".jasper" 
				+ swhere,
				"popacuse", "scrollbars=1, resizable=yes, width=1024, height=768");

		
	}

	function claves() {
		
		var contador = 0;
		var szWhere = "";
		szWhere = "";
		var elMonto = " tr.nFolioRELACIONGASTOS = " + $("#id_caso").val();
		var acumulado = Number(0.00);
		var importe = Number (0.00);
		var szTabla = "CLAVES_RELACION_GASTO";
		$.getJSON("../catalogos/SelectJson.jsp", {
			Tabla : szTabla,
			Param : szWhere,
			MaxReg : elMonto,
			ajax : 'false'
		}, function(j) {
			
			for ( var i = 0; i < j.length; i++) {
				importe = Number(parseFloat(j[i].Col5));
				fnClickAddRowZ(j[i].Col0, j[i].Col1, j[i].Col2, j[i].Col3, j[i].Col4, importe, j[i].Col7, j[i].Col8, j[i].Col9);
				acumulado += importe;
				contador = contador + 1;
				$("#cllave").val(contador);
				$("#numPaso").val("3"); // Conafor
				$("#L03").click();
			};
			if($("#numPaso").val() == "3" ){ // CONAFOR
				if(<%=id_oper%> != 3){
					parent.document.getElementById("pb_save").disabled = true; // CONAFOR
				}
				DOCUMENTACION(); // CONAFOR
			}
			
			$("#acumuladoOperacion").val(acumulado.toFixed(2));
			if (acumulado == parseFloat($("#mImporteNeto").val()) ){
				$("#numPaso").val("4");
				
				$("#L04").click();
				$("#Limpia").attr("disabled",true);
				$("#nIdClaveEgresos2").attr("disabled",true);
			}

		});

	}
	
	function DOCUMENTACION() {

		var szWhere = "";
		szWhere = " caNoContrarrecibo ='" + $("#caNoContrarrecibo").val() + "'";
		var elMonto = "";
		var szTabla = "DOCUMENTACION_RELACION_GASTO";
		$.getJSON("../catalogos/SelectJson.jsp", {
			Tabla : szTabla,
			Param : szWhere,
			MaxReg : elMonto,
			ajax : 'false'
		}, function(j) {
		
			for ( var i = 0; i < j.length; i++) {
				documentacion(j[i].Col0, j[i].Col1, j[i].Col2, j[i].Col3,
						j[i].Col4, j[i].Col5, j[i].Col6, j[i].Col7, j[i].Col8,
						j[i].Col9, j[i].Col10, j[i].Col11, j[i].Col12, j[i].Col13,
						j[i].Col14);
						$("#numPaso").val("4");
						$("#L04").click();
			};
			
			if($("#numPaso").val() == "4" ){
				
				$("#numPaso").val("6"); // Se agrega 6 Para Guardar el documento ultimo Paso
				$("#divImprime").show(); //para CONAFOR*/
				$("#Agregar").attr("disabled",true);
				
				if(<%=id_oper%> != 3){
					parent.document.getElementById("pb_save").disabled = false; // CONAFOR
				}
			}
			
		});
	}

	
	function Borrar1() {
		if ($("#docAplicado").val() == "S") {
			Swal.fire({
				  icon: 'error',
				  title: 'El documento ya fue Aplicado',
				  text: 'No es posible Borrar Relación de Gasto'
				})	
			return;
		}

		$("#elcontra").val($("#caNoContrarrecibo").val());
		parent.document.getElementById("pb_send").disabled = true;
		parent.document.getElementById("pb_cancel").disabled = false;
		parent.document.getElementById("pb_cancel").click();
		parent.document.getElementById("pb_cancel").disabled = true;
		//queryFormPost("tLayoutVuelosDetRGBorrarUpdate", {async: false });		
		//queryFormPost("borrarinfoBoletosRGDelete", {async: false });
		queryFormPost("borrarEdoCtaGreenMexRGDelete", {async: false });
		
		if ($("#docAplicado").val() != "S"){ 
			queryFormPost("borraFactRelacionPagoBorrado", {async: false });
		}
	}


	 	function Grid(){
			$("#cMeta").val();
			var esGreenMex = $("#idGreenMex").val();
			
	 		if( $("#tipo_fuente").val() == "IP" ){
	 			if(esGreenMex == "SI")
					window.open('AyudaEPsPagos.jsp?fuenteFinancimiento=4&esGreenMex=' + esGreenMex, 'AyudaEPsPagos', 'status=1, width=900px, height=680px, left=100px');
	 			else
	 				window.open('AyudaEPsPagos.jsp?fuenteFinancimiento=4', 'AyudaEPsPagos', 'status=1, width=900px, height=680px, left=100px');
	 		}
			else if( $("#tipo_fuente").val() == "CE" )
				window.open('AyudaEPsPagos.jsp?tipoGasto=2&fuenteFinancimiento=2', 'AyudaEPsPagos', 'status=1, width=900px, height=680px, left=100px');
			else
				window.open('AyudaEPsPagos.jsp?', 'AyudaEPsPagos', 'status=1, width=900px, height=680px, left=100px');
						
			return false;
		}

		function setSequenceValRCH(seqValue){
			seqValue = "000000" + seqValue;
			seqValue = seqValue.substr(seqValue.length - 6);
			seqValue = "<%=cCentroContable%>" + "RCH" + $("#aEjercicioFiscal").val() + seqValue;
			$("#folioDevolucion").val( seqValue );
		}
		  	
		  	
		function cmdImprimirRCH(){
			window.open(
				"../admin/SeguridadCatalogos?"
					+ "catalogo=CONTRARECIBO"
					+ "&accion=run"
					+ "&rn=VOLANTERECHAZO.jasper"
					+ "&NumeroFolio=" + $("#noFolio").val(),
				"popacuse",
				"scrollbars=1, resizable=yes, width=1024, height=768");
		}
		
		
		function borraDatos(){	
			var continuar = true;			
			queryFormPost({
				queryName: "destinoGastoSeleccionado",
				async:false,
				callback:function(){
					if( $("#destinoGastoElegido").val() != "" &&  $("#destinoGastoElegido").val() != $("#DESTINO_GASTO").val() ){
						$("#DESTINO_GASTO").val( $("#destinoGastoElegido").val() );
						Swal.fire("No puede cambiar de Destino de Gasto una vez que ha cargado la documentacion soporte.", "Si es necesario descarte este tramite e inicie nuevamente.", "error");
						continuar = false;
					} 
				}	
			});
			
			if(!continuar)
				return false;
			
			$("#cIdRFC_RelacionGasto").val("");
			$("#cIdRFC").val();
			$("#cnombre").val("");
			$("#cnombreRFC").val();
			
			//Para obtener unicamente los dos ultimos digitos--->tipoRelacion=tipoRelacion.substring(2,4);
			tipoRelacion=$("#DESTINO_GASTO").val(); 
			if (tipoRelacion==" "){
				$("#cTipoRfc").val("");
				Swal.fire("Seleccione","Favor de seleccionar el Tipo Destino","info");
				return;
			}
			if ($("#DESTINO_GASTO").val()=="GCRG"){
				querySelectPost("CatCuentasBancarias", "cuentaBanc",{async: false });	
				$(".CtaBanc").show();
			}
			else{
				$(".CtaBanc").hide();
			}
			
			//consultaSuficiencias();
			
			opcion = $("#DESTINO_GASTO option:selected").text();
			opcion = opcion.toUpperCase();
			if (parseInt(opcion.indexOf("COMPROBA"),10)>-1){//URVP.22102014 Se valida si el tipo destino es comprobacion, se oculta input y label de cta banca para encabezado
				$(".ctaBancComproba").hide();
				querySelectPost("cargaCtaBancariasRFCVacio", "ctaBancaria",{async: false });
			}else{
				$(".ctaBancComproba").show();
				querySelectPost("cargaCtaBancariasRFCVacio", "ctaBancaria",{async: false });
			}
			
			$("#chk_EditaImporte").prop("checked",false);
			$("#importeEdicionMaximo").val("0.00");
			$("#importeEdicion").val("0.00");
			$("#importeEdicion").attr('disabled', true);
			$("#editaimporte").hide();
			$("#mImporteNeto").val($("#importeAuxNeto").val());
				
			if ($("#DESTINO_GASTO").val()=="CERE" || $("#DESTINO_GASTO").val()=="GCRE" || $("#DESTINO_GASTO").val()=="CPRP" || $("#DESTINO_GASTO").val()=="RCRE"  || $("#DESTINO_GASTO").val()=="CCRE"){
				$("#editaimporte").show();
			}
				
			
		}
		
		function cat_beneficiario(){
			
			DestinoGasto=$("#DESTINO_GASTO").val();
			tipoRelacion=$("#DESTINO_GASTO").val();
			tipoRelacion=tipoRelacion.substring(2,4);
			if (((tipoRelacion=="RG" || tipoRelacion=="RP" || tipoRelacion=="SU") && ($("#DESTINO_GASTO").val()!="GCRG") && $("#DESTINO_GASTO").val()!="NARG" && $("#DESTINO_GASTO").val()!="2NRP" && $("#DESTINO_GASTO").val()!="2NFA") )
				$("#cTipoRfc").val("1,2");//PERSONA FISICA Y MORAL
			else if (tipoRelacion == "RE" && $("#DESTINO_GASTO").val() == "RCRE")
				$("#cTipoRfc").val("3"); //EMPLEADO CNF
			else if (tipoRelacion == "RE" && $("#DESTINO_GASTO").val()!="SPRE")
				$("#cTipoRfc").val("3"); //EMPLEADO CNF
			else if ($("#DESTINO_GASTO").val()=="GCRG")
				$("#cTipoRfc").val("0"); //CNF
			else if ($("#DESTINO_GASTO").val()=="NORN")
				$("#cTipoRfc").val("1,2,3"); //CNF
			else if ($("#DESTINO_GASTO").val()=="CBRB" || $("#DESTINO_GASTO").val()=="2NRP" || $("#DESTINO_GASTO").val()=="2NFA")/*2% / Nomina*/
				$("#cTipoRfc").val("7"); //CNF
			else if($("#DESTINO_GASTO").val()=="NARG")
				$("#cTipoRfc").val("1,2,3"); 
			else if($("#DESTINO_GASTO").val()=="SPRE")
				$("#cTipoRfc").val("6"); 
			
			
			window.open('CatalogoBeneficiariosRG.jsp?DESTINO_GASTO=' + DestinoGasto, 'Beneficiarios', 'status=1, width=900px, height=550px, left=0px, scrollbars=Yes,resizable=yes');
		}

		function cargaTipoDestino(){
			if ($("#tipo_fuente").val()=="IP"){
				if(muestraCBRadicado){
					$("#chk_radicado").prop("checked",false);
					$("#cEsRadicado").val("N");
				}
				$("#divGreenMex").show();
			}else{
				if(muestraCBRadicado){
					$("#chk_radicado").prop("checked",true);
					$("#cEsRadicado").val("S");
				}
			}
			
			if ($("#tipo_fuente").val()=="IP")  {
				$("#cTipoFuente").val("4");
				$("#divSuficiencia").hide();
				
			}	else if ($("#tipo_fuente").val()=="CE") {
				$("#cTipoFuente").val("1");
				$("#divGreenMex").hide();
				$("#divSuficiencia").show();
				
			} else {
				$("#cTipoFuente").val("1");
				$("#divSuficiencia").show();
			}
				
			
			if ($("#tipo_fuente").val()=="FF"){
				$("#divGreenMex").hide();
				$("#divSuficiencia").show();
			}
			querySelectPost("CatalogoDestinoGastoRelGastos", "DESTINO_GASTO",{async: true });
		}	
	 	
	 	//VGC 20140914 Crea la data table donde se mostraran las solicitudes pendientes
	 	var tablaSolicitudes;
		function createDTSolicitudesPendientes(){
			
			//poner la validacion si es deudor diverso o deudor viatico.
			var cDestino_Gasto = $("#DESTINO_GASTO").val();
			var cTipoConcepto = $("#tConcepto").val();	
			var sWhereGtoVto = " ";
			
			if ((cDestino_Gasto == "GCRE" && cTipoConcepto == "DDV")){		
				sWhereGtoVto = " AND nFolioCaja IN ( SELECT DISTINCT nFolioCaja FROM tcajadetalle WITH (NOLOCK) WHERE cEvento IN ('8_2_1') ) ";		
			}
			
			if ((cDestino_Gasto == "GCRE" && cTipoConcepto == "DD")){		
				sWhereGtoVto = " AND nFolioCaja IN ( SELECT DISTINCT nFolioCaja FROM tcajadetalle WITH (NOLOCK) WHERE cEvento NOT IN ('8_2_1','8_1_1') ) ";		
			}
			
			if ((cDestino_Gasto == "CCRE" && cTipoConcepto == "CAC")){		
				sWhereGtoVto = " AND nFolioCaja IN ( SELECT DISTINCT nFolioCaja FROM tcajadetalle WITH (NOLOCK) WHERE cEvento IN ('8_1_1') ) ";		
			}
			
			
		 	tablaSolicitudes = 
		 	     $('#tblSolicitudes').dataTable({
					        "bPaginate": false,
		        			"bLengthChange": true,
		        			"bFilter": true,
		        			"bSort": true,
		        			"bInfo": false,
		        			"bAutoWidth": false,
							"sScrollY": "100%",
							"bJQueryUI": true,
							"bRetrive" : true,
							"bDestroy" : true,
							"sPaginationType": "full_numbers",
							"sScrollX": "100%",
							"bScrollCollapse": true,	
							"bServerSide": true,   
							//ARLA se agrega a la vista v_Solicitudes_Pendientes columna de centro contable y se manda en el where para filtrar por centro contable
					        sAjaxSource : window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_Solicitudes_Pendientes&qw=cBeneficiario='" + $("#cIdRFC_RelacionGasto").val() + "'" + " AND cCentrocontable = '" + $("#cCentroContable").val() + "' " + sWhereGtoVto,
					        aoColumns   : [
								{ sName: "nFolioCaja" },
								{ sName: "cDescripcionSolicitud" },
								{ sName: "mMontoRemanente"},
								{ sName: "cMeta"}
							],
							oLanguage: {
								sProcessing: "Procesando...",
								sLengthMenu: "Mostrar _MENU_ registros",
								sZeroRecords: "No hay registros a mostrar",
								sEmptyTable: "No hay datos en la tabla",
								sLoadingRecords: "Cargando...",
								sInfo: "Registros _START_ al _END_ de _TOTAL_",
								sInfoEmpty: "Registro 0 al 0 de 0",
								sInfoFiltered: "(filtered from _MAX_ total entries)",
								sInfoPostFix: "",
								sInfoThousands: ",",
								sSearch: "Filtro:",
								oPaginate: {
									sFirst:    "Primero",
									sPrevious: "Ant.",
									sNext:     "Sigte.",
									sLast:     "&Uacute;ltimo"
								}
							}
				});
				
				$("#tblSolicitudes tbody").live("click", function(event){fnClickTblSolicitudes(event);} );
				$("#tblSolicitudes tbody").dblclick(function(event){dialogGastosPorComprobarAceptar(event);});
		 }

		
		function obtieneCuentaBanc(){
			$("#cCtaBanc").val($("#cuentaBanc option:selected").val());
			$("#dCtaBanc").val($("#cuentaBanc option:selected").text());
		}	
		
		var opcion="";
		function cargaCtaBancariaRFC(){
			opcion = $("#DESTINO_GASTO option:selected").text();
			opcion = opcion.toUpperCase();
			
			$("#campoRFC").val($("#cIdRFC_RelacionGasto").val());
			if (parseInt(opcion.indexOf("COMPROBA"),10)>-1){//URVP.22102014 Se valida si el tipo destino es comprobacion, llenar combo de cuenta bancaria de acuerdo al rfc seleccionado
				querySelectPost("cargaCtaBancariasRFCVacio", "ctaBancaria",{async: false });
			}else{
				querySelectPost("cargaCtaBancariasRFC", "ctaBancaria",{async: false });
			}
			queryFormPost("esProveedorExtranjero", {async:false});
			
			cargaConceptoMov();	
		}
		
		function aceptarFacturas() {
			$("#mImporteNeto").val( quitaFmt( $("#mTotalFacturaV").val() ) );
			validaEdicion();
			$("#mImporteBruto").val(parseFloat(Number(quitaFmt($("#importeEdicionMaximo").val()))+Number(quitaFmt($("#mImporteRetencion").val())) +Number(quitaFmt($("#importenoComprobable").val()))).toFixed(2));
			$("#importeAuxNeto").val($("#mImporteNeto").val());
			$(".subtotall").change();
			
			if ($("#importenoComprobable").val() =="") {
					$("#importenoComprobable").val("0.00");
			}
			
			revisaTipoFacturas();
			queryFormPost("RetencionesRead", {async:false});
			
			if ( revisaFacturaConRetenciones() ) {
				if (Number($("#mFacturaOtros").val()) > 0 ) {
					$("#divOtrasRete").show();
				} else {
					$("#divOtrasRete").hide();
				}
				parent.document.getElementById("pb_save").disabled=false;
			}else {
				parent.document.getElementById("pb_save").disabled=true;
			}
			
			modalFactura.hide();
		}
		
		function creaDiagloFacturas(abrir){
			if( abrir ){				
				if(   (  "" != $.trim( $( "#DESTINO_GASTO" ).val() )  ) && (  "" != $.trim( $("#cIdRFC_RelacionGasto").val() )  )   ) {
					
					queryFormPost("readMontoFacturasNetoRG", {async:false});
					creaDTFacturas();
					togleDivFacts(0);
					parent.document.getElementById("pb_save").disabled=true;
					
					modalFactura.show();
					
				} else if( "" == $.trim( $("#DESTINO_GASTO").val() ) )
					Swal.fire("Capturar", "Debe seleccionar antes el destino de gasto" ,"info");
				else if( "" == $.trim( $("#cIdRFC_RelacionGasto").val() ) )
					Swal.fire("Capturar", "Debe seleccionar antes el beneficiario/proveedor" ,"info");	
				
			}
			//consultaSuficiencias();
		}
		
		function validaEdicion(){
			if ($("#DESTINO_GASTO").val()=="CPRP"){
				queryFormPost("leeImporteFacturas", {async:false});
				$("#importenoComprobable").val("0.00");
				$("#mImporteNeto").val(Number(quitaFmt($("#importeEdicionMaximo").val()))+Number(quitaFmt($("#importenoComprobable").val())));
			}else if ($("#DESTINO_GASTO").val()=="CERE" || $("#DESTINO_GASTO").val()=="GCRE" || $("#DESTINO_GASTO").val()=="CCRE"|| $("#DESTINO_GASTO").val()=="RCRE"){
				queryFormPost("leeImporteFacturas", {async:false});
				queryFormPost("leeImportenoComprobable", {async:false});
				$("#mImporteNeto").val(parseFloat(Number(quitaFmt($("#importeEdicionMaximo").val()))+Number(quitaFmt($("#importenoComprobable").val()))).toFixed(2));
				//$("#importeEdicionMaximo").val($("#mImporteNeto").val());
			}
			
			queryFormPost("importeRetencionRG", {async:false});

			var rete = $("#mImporteRetencion").val();

			if (rete == ""){
				rete = "0.00";
				$("#mImporteRetencion").val(rete);
			}
				
			if ( rete != "0.00") {
				var importeNeto = $("#mImporteNeto").val();
				var importeBruto = parseFloat(Number(importeNeto) + Number(rete)).toFixed(2);
				$("#mImporteBruto").val(importeBruto);
				$("#mImporteRetencion").val(rete);
			}
		}
		
		function creaDTFacturas(){
			oTablevFact = $('#grdValidaFacturas').dataTable(
			{
				"bProcessing": true,
				"bServerSide": true,
				"bDestroy": true,
				"bSort": true, 
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vfacturaspagos&qw=folioPago=<%= c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)%> AND tipoPago='RELACIONGASTOS'" ,
				"bJQueryUI": true,
				"sScrollX": "100%",
				"sScrollXInner": "100%",
				"sScrollY": "100%",
				"bPaginate": false,
				//"bAutoWidth": true,
				"bInfo": true,
				aoColumns: [
							{ sName: "Factura" },
							{ sName: "mimporteconiva" } ]	
			});
		}
		
		function togleDivFacts(nIdDiv){
			
			var tipoDestion = $("#DESTINO_GASTO").val();
			var showCert = ( tipoDestion == "CERE" || tipoDestion == "GCRE" );
			var showOthers = !showCert;
			 		
			if( nIdDiv == 0){
				$("#uploadFacturasDiv").hide();
				$("#facturasCapturadasDiv").show();
				creaDTFacturas();
				//queryFormPost("readMontoFacturasNetoRG", {async:false});
			}else{
				if ($("#esExtranjero").val()=="1")
					$('#uploadFacturasFrm').attr('src', "UploadFacturasExtranjeros.jsp");
				else
					$('#uploadFacturasFrm').attr('src', "UploadFacturasRG.jsp?tipo_pago=RELACIONGASTOS&ALLOW_OTHERS="+showOthers+"&ALLOW_CERT="+showCert+"&DESTINO_GASTO="+$("#DESTINO_GASTO").val()+"&RFC="+ encodeURIComponent($("#cIdRFC_RelacionGasto").val()) );
				$("#uploadFacturasDiv").show();
				$("#facturasCapturadasDiv").hide();
			}		
			
			queryFormPost("readMontoFacturasNetoRG", {async:false});
		}
		
		function updateFolioCaja(){
			$("#dialog-folioCaja").dialog("open");
			if ($("#polManual").val()!="0"){
				$("#cFolioCaja").val($("#polManual").val());
				$("#cFolioCaja").attr("readonly","readonly");
				//cssReadOnly();
			}
		}
		
		function validaimportemaximo(){
		
			var importe = 0.00;
			importe = parseFloat(quitaFmt($("#importeEdicion").val()));
			importe = importe.toFixed(2);
			
			queryFormPost("validaEditaMontosRFC_Read",{async: false });	
			
			var importeEdicionMaximo = 0.00;
			
			if($("#editaMontoRFC").val() == "1"){
				importeEdicionMaximo = parseFloat(quitaFmt($("#importeEdicionMaximo").val())) + 1;
			}else{
				importeEdicionMaximo = parseFloat(quitaFmt($("#importeEdicionMaximo").val()));
			}
			
			importeEdicionMaximo = importeEdicionMaximo.toFixed(2);
			
			$("#importeEdicion").val(importe);
			
			$("#importeEdicionMaximo").val(importeEdicionMaximo);
			
			if (Number(quitaFmt($("#importeEdicion").val())) > Number(quitaFmt($("#importeEdicionMaximo").val()))){
				Swal.fire("Verifique","El importe editado no puede ser mayor a $"+$("#importeEdicionMaximo").val(),"info");			
				$("#importeEdicion").val("0.00");
			}else{
				$("#mImporteNeto").val(parseFloat(Number(importe)+Number(quitaFmt($("#importenoComprobable").val()))).toFixed(2));	
			}
			
			queryFormPost("leeImporteFacturas", {async:false});
			queryFormPost("importeRetencionRG", {async:false});
			
			var importeMaximo = parseFloat(quitaFmt($("#importeEdicionMaximo").val())) || 0;
			var importeRetencion = parseFloat(quitaFmt($("#mImporteRetencion").val())) || 0;
			var importeNoComprobable = parseFloat(quitaFmt($("#importenoComprobable").val())) || 0;

			if (Number($("#importeEdicion").val())==0){
				var total = importeMaximo + importeRetencion + importeNoComprobable;
				$("#mImporteBruto").val(total.toFixed(2));
			} else {
				importeMaximo = parseFloat(quitaFmt($("#importeEdicion").val())) || 0;
				var total = importeMaximo + importeRetencion + importeNoComprobable;
				$("#mImporteBruto").val(total.toFixed(2));	
			}
		}
		
		function regresaPasoCapturasEP(){
			document.getElementById("nIdClaveEgresos2").removeAttribute("disabled",false); //boton de las eps "...""
			document.getElementById("Limpia").removeAttribute("disabled",false);//boton de limpiar
			
			$(".pasoTres").hide();//se oculta paso de documentacion
			$("#grdMovimientos").dataTable().fnClearTable();//se limpia grid de movimientos
			claves();//se carga grid de movimientos en BD
			document.getElementById("mMovimiento").removeAttribute("readonly",false);
			$("#mMovimiento").removeClass("notEditable");
			//cssReadOnly();
		}
		
		function quitaPunto(){
			var folio = "";
			folio = $("#cFolioCaja").val();
			while (folio.lastIndexOf(".")>-1) {
				folio = folio.replace(".", "");	
			}
			$("#cFolioCaja").val(Number(folio));
		}
		

	/* ************************
	 * VGC20150930 Inicia cambios para FID
	 * *********************** */	
	function creaDialogoFID() {

		$("#capturaFID_DIV").dialog({
			title:"FID",
			autoOpen : false,
			height : 200,
			width : 430,
			modal : true,
			buttons : {
				"Aceptar" : function() {
					if( validaCapturaFID() ){
						$.blockUI({message: "Procesando espere ......"});
						$("#nIDPrograma").val($("#cProgramaDesc").val());
						$("#cSubPrograma").val($("#cSubProgramaDesc").val());

						queryFormPost({
							queryName : "actualizaDatosFIDRGUpdate",
							async : "false",
							callback : function() {
									queryFormPost({
									queryName : "actualizaFIDDetalle",
									async : "false",
									callback : function() {	
										return true;							
									}
								});
								fnClickAddRowComp();
								$.unblockUI();
								$("#capturaFID_DIV").dialog("close");
							}
						});	
					} 
				},
				"Cancelar" : function() {
					$(this).dialog("close");
				}
			}
		});

	}


	/**
	 * Valida si las EPs registradas en el pago son de un programa de FID Banorte.
	 * Si es asi, muestra al usuario la lista de subprogramas para que seleccione el
	 * correcto. En caso contrario, procede de manera normal.
	 */
	function validaFID() {

		$("#esPagoFID").val("0");
		
		queryFormPost({
			queryName : "esPagoRGFID",
			async : "false",
			callback : function() {
				var esPagoFid = parseInt($("#esPagoFID").val(), 10) > 0;
				if (esPagoFid && $.trim($("#cIdRFC_RelacionGasto").val()) == "BMN930209927" ){
					queryFormPost("programaRGRead", {async : false});
					querySelectPost( "catalogoFideicomisoRead", "cProgramaDesc", {async : false} );
					$("#capturaFID_DIV").dialog("open");
				}else{
					fnClickAddRowComp();
				}
				
			}
		});
	}

	/**
	 * Actualiza los subprogramas segun la seleccion del programa
	 */
	function cambiaPrograma() {
		$("#nIDPrograma").val($("#cProgramaDesc").val());
		clearSelect("cSubProgramaDesc");
		querySelectPost("catalogoSubprgRead", "cSubProgramaDesc", {
			async : false
		});
	}

	

	/**
	 * Valida que el usuario haya capturado correctamente los datos para FID
	 * @returns {Boolean} true si y solo si tanto el programa como el subprograma han sido seleccionados.
	 */
	function validaCapturaFID() {
		var msg = "";
		var token = "";
		
		if ($("#cProgramaDesc").val() == "-1") {
			msg = "Debe seleccionar el programa.";
			token = "\n";
		}
		if ($("#cSubProgramaDesc").val() == "-1" ) {
			msg +=  token + "Debe seleccionar el subprograma.";
		}

		if (msg != "") {
			Swal.fire("Seleccione", msg ,"info");
			return false;
		} else
			return true;

	}

	/**
	 * Crea dialogo para la captura de informacion de tipo de gastos de viaticos.
	 */
	

	 

	function showDivOficio(esUpdate){
		var cmpName = "oficioDelegatorio" + (esUpdate?"Update":"");
		var divName = "oficioDelegatorioCaptura" + (esUpdate?"Update":"");
		if( $("#" + cmpName ).is(":checked") )
			$("#"+divName).show();
		else
			$("#"+divName).hide();
	}

	
	/**
	** Funcion para crear un dialog.
	*/
	
	/*
	function creaDialogoInformeComision() {
	 
	     	$("#dglInformeComision").dialog({
	        title:"Informe de Comisión",
	        autoOpen : false,
	        height : 290,
	        width : 520,
	        modal : true,
	        buttons : {
	           	"Aceptar" : function() {      
	           			updateInformeComision();
	            	},
	             	"Cancelar" : function() {
	               		$(this).dialog("close");
	               }
	        	},
	        open: function(){
	        	Swal.fire(
	        			  'Capturar máximo 350 caracteres',
	        			  'DE ACUERDO AL ART. 24 DE LOS LINEAMIENTOS POR LOS QUE SE ESTABLECEN MEDIDAS DE AUSTERIDAD..., EL INFORME SERA UN RESUMEN DE LAS ACTIVIDADES REALIZADAS, CONCLUSIONES Y RESULTADOS OBTENIDOS',
	        			  'info'
	        			);
			}
	      });

	}	*/

	
	function consultaAgenda(){
		queryFormPost("readInfoAgendaComision", {async:false});		
		sumaDetalle();
		sumaTransporte();		
		sumaTotales();
		modalAgendaComision.show();
	}
	
	function actualizaAgendaComision(){
		let total = 0;
		let neto = 0;
		
		total = Number(quitaFmt($("#mTotal2").val()));
		neto = Number(quitaFmt($("#mImporteNeto").val()));
		
		if( total != neto ){
			Swal.fire({ icon: "error",
						text: "El total de la agenda debe ser igual el total neto de la solicitud."});			
		} else {			
			$("#mImpPasaje").val(Number(quitaFmt($("#mPasaje").val())));
			$("#mImpTaxi").val(Number(quitaFmt($("#mTaxi").val())));
			$("#mImpPeaje").val(Number(quitaFmt($("#mPeaje").val())));
			$("#mImpHotel").val(Number(quitaFmt($("#mHotel").val())));
			$("#mImpConsumos").val(Number(quitaFmt($("#mConsumos").val())));
			$("#mImpOtros").val(Number(quitaFmt($("#mOtros").val())));
			$("#mImpTotal1").val(Number(quitaFmt($("#mTotal1").val())));
			$("#mImpPasajeLocal").val(Number(quitaFmt($("#mPasajeLocal").val())));
			$("#mImpTaxiLocal").val(Number(quitaFmt($("#mTaxiLocal").val())));
			$("#mImpGasolinaLocal").val(Number(quitaFmt($("#mGasolinaLocal").val())));
			$("#mImpPeajeLocal").val(Number(quitaFmt($("#mPeajeLocal").val())));
			$("#mImpMaritimoLocal").val(Number(quitaFmt($("#mMaritimoLocal").val())));
			$("#mImpAereoLocal").val(Number(quitaFmt($("#mAereoLocal").val())));
			
			queryFormPost("actualizaAgenda", {async: false});
			Swal.fire({ icon: "success",
						text: "Importes actualizados correctamente."});
			
			modalAgendaComision.hide();
		}
	}
	
	function currencyFormatter({ currency, value}) {
		const formatter = new Intl.NumberFormat('en-US', {
			style: 'currency',
			minimumFractionDigits: 2,
			currency
		}) 
		return formatter.format(value)
	}
	
	function sumaDetalle() {
		let total = 0;
		
		total = Number(quitaFmt($("#mPasaje").val())) + Number(quitaFmt( $("#mTaxi").val())) + Number(quitaFmt($("#mPeaje").val())) 
			+ Number(quitaFmt($("#mHotel").val())) + Number(quitaFmt($("#mConsumos").val())) + Number(quitaFmt($("#mOtros").val()));
		
		total = currencyFormatter({currency:'USD', value: total });
		$("#mTotal1").val(total);	  

		let pasaje = currencyFormatter({currency:'USD', value: quitaFmt($("#mPasaje").val()) })
		$("#mPasaje").val(pasaje)
		
		let taxi = currencyFormatter({currency:'USD', value: quitaFmt($("#mTaxi").val()) })
		$("#mTaxi").val(taxi)
		
		let peaje = currencyFormatter({currency:'USD', value: quitaFmt($("#mPeaje").val()) })
		$("#mPeaje").val(peaje)
		
		let hotel = currencyFormatter({currency:'USD', value: quitaFmt($("#mHotel").val()) })
		$("#mHotel").val(hotel)
		
		let cons = currencyFormatter({currency:'USD', value: quitaFmt($("#mConsumos").val()) })
		$("#mConsumos").val(cons)
		
		let otros = currencyFormatter({currency:'USD', value: quitaFmt($("#mOtros").val()) })
		$("#mOtros").val(otros)		
	}
	
	function sumaTransporte() {
		let total = 0;
		total = Number(quitaFmt($("#mPasajeLocal").val())) + Number(quitaFmt( $("#mTaxiLocal").val())) + Number(quitaFmt($("#mGasolinaLocal").val())) 
			+ Number(quitaFmt($("#mPeajeLocal").val())) + Number(quitaFmt($("#mMaritimoLocal").val())) + Number(quitaFmt($("#mAereoLocal").val()));
		total = currencyFormatter({currency:'USD', value: total });
		$("#mTotalLocal").val(total);
		
		let pasaje = currencyFormatter({currency:'USD', value: quitaFmt($("#mPasajeLocal").val()) })
		$("#mPasajeLocal").val(pasaje);
		
		let taxi = currencyFormatter({currency:'USD', value: quitaFmt($("#mTaxiLocal").val()) })
		$("#mTaxiLocal").val(taxi);	
		
		let gas = currencyFormatter({currency:'USD', value: quitaFmt($("#mGasolinaLocal").val()) })
		$("#mGasolinaLocal").val(gas);
		
		let peaje = currencyFormatter({currency:'USD', value: quitaFmt($("#mPeajeLocal").val()) })
		$("#mPeajeLocal").val(peaje);
		
		let maritimo = currencyFormatter({currency:'USD', value: quitaFmt($("#mMaritimoLocal").val()) });
		$("#mMaritimoLocal").val(maritimo);

		let aereo = currencyFormatter({currency:'USD', value: quitaFmt($("#mAereoLocal").val()) });
		$("#mAereoLocal").val(aereo);
	}
	
	function sumaTotales(){
		let total = 0;
		
		total = Number(quitaFmt($("#mTotal1").val())) + Number(quitaFmt( $("#mTotalLocal").val()));				
		total = currencyFormatter({currency:'USD', value: total });
		$("#mTotal2").val(total);
	}

	
		/*vgc290916 Valida que la EP que se intenta agreagar no exista ya en el pago.*/
		function validaEPCapturada(epFind){
			var existe = false;
			var eps = $("#grdCompromisos").dataTable().fnGetData();
			for( i = 0; i < eps.length; i++ ){
				if( eps[i][1] == epFind ){
					existe = true;
					break;
				}
			}
			
			return existe;
		}
		
		function insertaObjeto( folioIngresoP, EPP, montoP, mesP){
																	
			if(!relaciones)
				relaciones = [];	
			
			var obj = { folioIngreso : folioIngresoP ,
						EP:EPP,
					    monto:montoP,
						mes:mesP
					  };
			
			relaciones[relaciones.length] =  obj ;
		}

		function validaPartidaGtosRepresentacion(){
			var bRegresa = true;
			
			$("#esPartidaViaticos").val("0");
			var cPartida = $("#EP").val();
			cPartida = cPartida.substring(31, 36);		
			$("#cPartida").val(cPartida);
			queryFormPost("esPartidaViaticosRead", {async : false});
			
			if(cPartida == "38501" || $("#esPartidaViaticos").val() == "1"){			
				Swal.fire("ATENCIÓN","Queda bajo su responsabilidad la información capturada en los conceptos e informes, misma que séra pública en " + 
						"seguimiento al articulo 70 de la Ley General de Transparencia y Acceso a la Información Pública.","info");
			}
			
			return bRegresa;
		}
		
		function showDivOficioVoBo(esUpdate){
			var cmpName = "oficioDeleVoBo" + (esUpdate?"Update":"");
			var divName = "oficioDelegatorioVoBo" + (esUpdate?"Update":"");
			if( $("#" + cmpName ).is(":checked") )
				$("#"+divName).show();
			else
				$("#"+divName).hide();
		}
		
		function validaEsIngresosPropios(){
			var bRegresa = false;		
			
			var cIngPropio = $("#EP").val();
			cIngPropio = cIngPropio.substring(39, 40);		
			
			if(cIngPropio == "4" ){			
				bRegresa = true;
			}
			
			return bRegresa;
		}
		
		function validaDispIngresoPropio(){
		
			var bRegresa = true;
					
			queryFormPost("validaSuficienciaRegIPRead", {async : false});
				
			if($("#nValidaSuficienciaIP").val() == "1"){
				bRegresa = false;
			}
			
			return bRegresa;
		}
		
		
		function referenciaBancaria(){
			var x = document.getElementById("ctaBancaria");
			var i=x.options.length;
			if ($("#chk_RB").prop("checked")){
			   var opcion = document.createElement('option');
				opcion.text = 'Pago Referenciado';
				opcion.value = 'Pago_Referenciado';
				x.appendChild(opcion);
			    x.selectedIndex = i;
			    $("#trReferenciaBancaria").css("visibility","visible");
			    $("#tdctaBancaria").css("visibility","hidden");
			    $("#cPagoReferenciado").val("S");
			}else{
				x.remove(i-1);
				x.selectedIndex = 0;
				$("#trReferenciaBancaria").css("visibility","hidden");
			    $("#tdctaBancaria").css("visibility","visible");
				$("#cReferenciaBancaria").val("");		
			}
		}
		
		function borraOfiDelegatorio(){

			if($("#firmanteOficioExiste").val() == "Existe"){
				queryFormPost({	queryName : "borrarOficioDelegatorio", async : false, callback : function(){																					
									msn = "Oficios borrados del pago " + $("#cTipoPago").val() + " no. " + $("#nFolioPago").val();
									} 
							});
				$("#cFolioOficioUpdate").val("");
				$("#dFechaOficioUpdate").val("");
				$("#cNombreTitularUpdate").val("");
				$("#cApellidoPaternoTitularUpdate").val("");
				$("#cApellidoMaternoTitularUpdate").val("");
				$("#cPuestoTitularUpdate").val("");
			}
			
			if($("#firmanteOficioVoBoExiste").val() == "Existe"){
				queryFormPost({	queryName : "borrarOficioDelegatorioVoBo", async : false, callback : function(){ 																						
									msn = "Oficios borrados del pago " + $("#cTipoPago").val() + " no. " + $("#nFolioPago").val();
									} 
							});
				$("#cFolioOficioVoBoUpdate").val("");
				$("#dFechaOficioVoBoUpdate").val("");
				$("#cNombreTitularVoBoUpdate").val("");
				$("#cApellidoPaternoTitularVoBoUpdate").val("");
				$("#cApellidoMaternoTitularVoBoUpdate").val("");
				$("#cPuestoTitularVoBoUpdate").val("");
			}							
			
			$("#firmanteOficioVoBoExiste").val("");
			$("#firmanteOficioExiste").val("");
			alert(msn);
		}
		
		function validarPartidaObligatoria(){
			
			var bRegresa = true;
			
			var cPartida = $("#EP").val();
			cPartida = cPartida.substring(31, 36);		
			
			$("#esPartidaObligatoria").val("0");
			queryFormPost("esPartidaObligatoria_RG_Read", {async:false});			
			
			if($("#esPartidaObligatoria").val() == "1" ){
				alert("No es posible la captura de la Partida: " + cPartida + ", debido a que esta marcada con retenciones Obligatorias.");
				bRegresa = false;		
			}	
					
			return bRegresa;		
		}
		
		function validaEntidadFederativa() {
			var bRegresa = true;
		
			var cEP = $("#EP").val();		
			var cEntidadFederativa = cEP.substring(41, 43);		
		
			queryFormPost("validaEntidadFederativaEP", {
				async : false
			});
			
			if ($("#cEntidadFederativaEP").val() != "0"){
				if ($("#cEntidadFederativaEP").val() != cEntidadFederativa) {
					alert("No se permite mezclar EP's de Entidades Federativas diferentes.");
					bRegresa = false;
				}		
			}
			return bRegresa;
		}
		
			
		
		function seleccionaAnticipoGreenMex(){
			if($("#idGreenMex").val() == "SI"){
				$("#mImporteComprobar").val($("#mImporteBruto").val());
				$("#mImporteComprobar").formatCurrency();				
				$("#sDataFoliosING").val("");
				$("#sDataRemanenteING").val("");
				$("#totalAcumulado").val("");		
				creaDTAnticipos();
				modalAnticipoGreenMex.show();
			}
			else 
				modalAnticipoGreenMex.hide();
		}
		
		function creaDTAnticipos(){
			oTableIng = $('#tblSolicitudesIngreso').dataTable({
				"bProcessing": true,
				"bServerSide": true,
				"bDestroy": true,
				"bSort": true, 		
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_SaldoGreenMex",
				"bJQueryUI": true,
				"bPaginate": false,
				"bAutoWidth": true,
				"bInfo": true,
				fnInitComplete: function(settings, json) {
					$(".ING_SEL").each(function(){
						$(this).click(function(){
							actualizaTotales();
						});
					});
				  },
				aoColumns: [
							{ sName: "id" },
							{ sName: "folio" },
							{ sName: "cdescripcionpoliza" },
							{ sName: "mMontoRemanente" },
							{ sName: "RFC"}]	
			});
		}
		
		function actualizaTotales(){
			var suma = Number( quitaFmt( $("#totalAcumulado").val() ) );
			var comprobar = Number( quitaFmt( $("#mImporteComprobar").val() ) );
			var diferencia;
			$(".ING_SEL").each(
				function(){
					 if( $(this).attr("checked") ){
					 	  var sumando = buscaTotal( $(this).attr('id') );
					 	  suma += sumando;
					 }
			});
			$("#totalAcumulado").val( suma.toFixed(2) );	
			
			diferencia = suma.toFixed(2) - comprobar.toFixed(2);
			if(diferencia < 0){
				Swal.fire({ icon: "warning",
							text: "El remanente del ingreso es menor al importe a ejercer, favor de agregar otro ingreso. \n Remanente: " + suma.toFixed(2) + "\n Ejercer: " + comprobar.toFixed(2) });		
				return;
			}
			
		}

		function buscaTotal( idInput ){
			var matrizVal = $("#tblSolicitudesIngreso").dataTable().fnGetData();
			var val = 0.00;
			
			for( cnt = 0; cnt < matrizVal.length; cnt++){
				if( matrizVal[cnt][1] == idInput ){
					val = parseFloat( quitaFrmt( matrizVal[cnt][3] ) );
				}
			}
			return val;
		}

		function quitaFrmt(fld) {
			var valcol = fld.toString();
			valcol = valcol.replace(/[$]/g, "");
			valcol = valcol.replace(/,/g, "");
			return valcol;
		}
		
		function abrirDialogSolicitudes() {
			$("#tmpNoCaja").val("");
			modalSolicitudes.show();
		}
		
		function aceptarDialogSolicitudes() {
			if ($("#DESTINO_GASTO").val()=="GCRE" && $("#tConcepto").val()=="DDV"){
				//capturaInformeComision();
				//$("#EditaInformeComision").css('visibility', 'visible');
				$("#VerDetalleAgenda").css('visibility','visible');
				//JGDS Actualizar datos de la comision y boton para editar
				queryFormPost("RGConsultaComisionComp",{async: false });
				$("#lblComision").text("Id. Comisión: " + $("#nIdComisionAux").val() + "     Concepto: " + $("#conceptoComision").val());
				$("#lblComision").show();
				
			}
			
			if( $("#tmpNoCaja").val() == ""  )
				Swal.fire("No ha seleccionado una solicitud a comprobar.","Por favor seleccionela dando click en la tabla","info");
			else{
				if( validaRemanenteRG() ){
					$("#noSolicitudCaja").val( $("#tmpNoCaja").val());
					$("#tmpNoCaja").val("");
					$(this).dialog("close");
				}else
					Swal.fire("Sin saldo","El monto de la relacion de gastos supera el monto remanente de la solicitud seleccionada.","info");
			}
			if($("#noSolicitudCaja").val() != '')
				actualizarConcepto();
			
			modalSolicitudes.hide();
		}
		
		function guardarAnticipoIngGreenMex(){
			var vacio = true;
			var impEjercer = $("#mImporteBruto").val();
			var impAcumulado = $("#totalAcumulado").val();
			var diferencia;
			var table = document.getElementById('tblSolicitudesIngreso');
				var aTrs = $('#tblSolicitudesIngreso').dataTable().fnGetNodes();		 				
				var fecha = $("#fRecepcion").val();
				
				for ( var i=1; i<=aTrs.length;  i++ ){ 								
					var row= table.rows[i];
					var chkbox = row.cells[0].childNodes[0];
					
					if(null != chkbox && true == chkbox.checked){
						var folioING = row.cells[1].innerHTML;
						var remanenteING = Number( quitaFmt( row.cells[3].innerHTML ) );		 							
						$('#sDataFoliosING').val($('#sDataFoliosING').val() + folioING + ",");
						$('#sDataRemanenteING').val($("#sDataRemanenteING").val() + remanenteING + ",");
						vacio = false;		 							
					}	
				}
				
				if(vacio){
					Swal.fire({ icon: "info",
					text: "Debes seleccionar al menos un ingreso que cubra el importe a ejercer." });
					return;
				}
				
			diferencia = impAcumulado - impEjercer;
			if(diferencia <= 0){
				Swal.fire({ icon: "warning",
							text: "El remanente del ingreso es menor al importe a ejercer, favor de seleccionar una solicitud de ingreso. \n Remanente: " + impAcumulado + "\n Ejercer: " + impEjercer });		
				return;
			} else {			
				var aaa = $("#id_caso").val();
				$.ajax({
					url : '../GREENMEX/greenmex',
					dataType : 'json',
					type :"POST",
					data : {
						"folioRG" : aaa,
						"impEjercer": impEjercer,
						"folioING" : $("#sDataFoliosING").val(),
						"remanenteING" : $("#sDataRemanenteING").val(),
						"fecha" : fecha
					},
					async : false,
					success : function(json) {
						var exito = json.success;
						if( exito == "true"){
							Swal.fire("Se actualizo exitosamente el estado de cuenta de GreenMex","success");										
						}
					},
					error : function(xhr, textStatus, errorThrown) {
						Swal.fire("Advertencia: " + xhr.responseText + "\nEstatus: " + textStatus + "\n" + errorThrown,"error");									
						return false;
					}
				});
			}
			modalAnticipoGreenMex.hide();
			$("#idGreenMex").attr('disabled', true);			
			 				
		}

		
				 
		function validaTipoFacturaFecha(){
			var folioRG = $("#id_caso").val();
			var bValida = false;
			$.ajax({
				url : '../cfdi/validacionesPreAutorizaRelacionGastos',
				dataType : 'json',
				type :"GET",
				data : {
					"folioRelacionGastos" : folioRG
				},
				async : false,
				success : function(json) {
					if(json.error != undefined && json.error != null && json.error != ""){
						alert("Error: " + json.error);
						bValida = false;
					}else{
						if(json.length > 0){
							var sMsg = "";
							
							for(var i=0; i<json.length; i++){
								try{
									var parts = (json[i]||"").split(";");
									if(parts.length >= 4){
										var linea = "La factura " + parts[1].trim() + " del proveedor " + parts[0].trim() + " No es valida, ya que la forma de pago es PUE y el mes es diferente. Mes Pago: " + parts[2].trim() + " Mes Factura: " + parts[3].trim();
										sMsg += linea + "\n";
									} else {
										
										sMsg += json[i] + "\n";
									}
								}catch(e){
									sMsg += json[i] + "\n";
								}
							}
	
							alert("Error en facturas cargadas: \n" + sMsg);
							bValida = false;
						}else{
							bValida = true;
						}
					}
				},
				error : function(xhr, textStatus, errorThrown) {
					alert("Advertencia: " + xhr.responseText + "\nEstatus: " + textStatus + "\n" + errorThrown);									
					bValida = false;
				}
			});
			return bValida;
		}

	</script>
	
</body>
</html>
