<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.core.Caso"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@ page import="java.util.*"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%
	Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	String DATE_FORMAT = "dd/MM/yyyy";
	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	String uLogin = usuario.getLogin();
	String folio = c.getFolio();
	Calendar c1 = Calendar.getInstance();
	String today = sdf.format(c1.getTime());
	String msg;
	if (session.getAttribute("RESULT") != null) {
		msg = (String) session.getAttribute("RESULT");
		session.removeAttribute("RESULT");
	}

	int idCaso = c.getIdCaso();
	Map rol = usuario.getRoles();
	Iterator it1 = rol.entrySet().iterator();
	String roles = "";
	while (it1.hasNext()) {
		Map.Entry r = (Map.Entry) it1.next();
		roles += r.getKey().toString() + ",";
	}
	if (roles.length() > 0) {
		roles = roles.substring(0, roles.length() - 1);
	}
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Alta de Proveedores</title>
<!-- Estilos estandar para los controles JQuery -->
<link rel="stylesheet" type="text/css"
	href="../css/datepickercontrol_bluegray.css" />
<link rel="stylesheet" type="text/css"
	href="../Ayudas/css/autocompleta.css"></link>
<link rel="stylesheet" type="text/css"
	href="../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css"></link>
<link rel="stylesheet" type="text/css"
	href="../Generador/css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css"
	href="../Generador/css/demo_page.css"></link>
<style type="text/css">
.validateTips {
	border: 1px solid transparent;
	padding: 0.3em;
}

.notEditable {
	background-color: #CCCCCC;
}
</style>
<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="../Generador/js/crud.js"></script>
<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
<script type="text/javascript" src="../js/jsquery.js"></script>
<script type="text/javascript"
	src="../Generador/js/jquery.dataTables.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"></script>
<script type="text/javascript"
	src="../Generador/js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript"
	src="../Generador/js/jquery.ui.datepicker.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.ui.tabs.js"></script>
<script type="text/javascript" src="../js/catalogo/general.js"></script>
<script type="text/javascript"
	src="../Generador/js/jquery.formatCurrency.js"></script>
<script type="text/javascript"
	src="../Generador/js/jquery.formatCurrency.all.js"></script>
<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
<script type="text/javascript">
//--------INICIO Variables Globales---------------------------------------------------------------------------------------------------
	var cFolio, cIdTipoPersona, cIdRFC, cRazonSocial, cCURP, cApellidoPaterno, cApellidoMaterno, 
	cNombre, cGiro, cIdEntidadFederativa, cIdMunicipio,cPais,cExtranjero, cCalle, cNumeroExterno, cNumeroInterno, cColonia, cCodigoPostal, 
	cEmail, cUrl, nIdPyme, nIdTipoTelefono, cTelefono,cIdRFC1,cIdRFC2,cIdRFC3,
	tips = $( ".validateTips" ),
	devolucion=false,actualizacion=false,rechazoAutorizacion=false,actualizaCuentasBancarias=false;
	var daTable,aPos,idOper,cargado=false,sincambios;
	
	var operacionActual = <%=c.getCasoOperacion(0).getOperacion().getIdOperacion()%>;
//-------FIN Variables Globales-------------------------------------------------------------------------------------------------------------
	
//--------Inicia READY----------------------------------------------------------------------------------------------------------------------
	$(document).ready(function() {
		queryFormPost("tAltaProvedorRead", {async : false});
		actualizaVariables();
		if($("#cExtranjero").val()==1){
			document.getElementById("chk_extranjero").checked = true;
		}else
			document.getElementById("chk_extranjero").checked = false;
	    //Inicializa las ayudas. Siempre debe estar presente en el onLoad (en JQuery es el $(document).ready(function() {});)
	    $("input.AyudaSyC").subIniciaDlg();
	    //Inicializa el autocompletar. Simpre debe estar presente en el onLoad (en JQuery es el $(document).ready(function() {});)
	    $("input.autoCompletaSyC").subIniciaAutoCompleta();

	    //Data Table Cuentas Bancarias
	   daTable= $("#tblCuentasBancarias").dataTable({
	        bPaginate: false,
	        bLengthChange: false,
	        bInfo: false,
	        bAutoWidth: false,
	        sScrollY: "125",
	        sScrollX: "100",
	        bJQueryUI: true,
	        bFilter: false,
	        bSort: false,
	        bInfo: false,
	        bAutoWidth: true,
	        //bSearch : false,
	        //sScrollXInner: "100%",
	        oLanguage: {
	            sProcessing: "Procesando...",
	            sLengthMenu: "Mostrar _MENU_ registros",
	            sZeroRecords: "No hay registros a mostrar",
	            sEmptyTable: "No hay datos en la tabla",
	            sLoadingRecords: "Cargando...",
	            sInfo: "Registros _START_ al _END_ de _TOTAL_",
	            sInfoEmpty: "Registro 0 al 0 de 0",
	            sInfoFiltered: "(filtado de _MAX_ registros)",
	            sInfoPostFix: "",
	            sInfoThousands: ",",
	            sSearch: "Buscar:",
	            oPaginate: {
	                sFirst: "Primero",
	                sPrevious: "Ant.",
	                sNext: "Sigte.",
	                sLast: "&Uacute;ltimo"
	            }
	        },
	        aaSorting: [
	            [1, "asc"]
	        ],
	        aoColumns: [
	        	{sName: "cBanco",bSearchable: false,bSortable: false,bVisible: false},
	            {sName: "dBanco"},
	            {sName: "cPlaza"}, 
	            {sName: "dCuentaBancaria"}, 
	            {sName: "dDigitoVerificador"}, 
	            {sName: "dSucursal"}, 
	            {sName: "cStatusCuenta",bSearchable: false,bSortable: false,bVisible: false}, 
	            {sName: "dStatusCuenta"}, 
	            {sName: "nBCBEnviadoSICOP"},
	            {sName: "Autorizada"},
	            {sName: "Operaciones",bVisible: ((operacionActual==2||operacionActual==4)?true:false)}
	        ]

	    });
	    //Funciones para Actualizar Clabe
	    $("#cbBanco")
	        .change(function() {
	            var szTemp = CalculaCLABE();
	            $("#txtCLABE").val(szTemp);
	            $("#txtDigitoVerificador").val(szTemp.substr(17, 1));
	        });

	    $("#txtPlaza")
	        .change(function() {
	            var szTemp = CalculaCLABE();
	            $("#txtCLABE").val(szTemp);
	            $("#txtDigitoVerificador").val(szTemp.substr(17, 1));
	        });
	    $("#txtCuentaBancaria")
	        .change(function() {
	            var szTemp = CalculaCLABE();
	            $("#txtCLABE").val(szTemp);
	            $("#txtDigitoVerificador").val(szTemp.substr(17, 1));
	        });

	    $("#txtSucursal")
	        .change(function() {
	            var szTemp = CalculaCLABE();
	        });
		//llenado de datatable desde Tabla temporal de cuentas bancarias (capturadas  por el usuario)
	    var rfc= $("#cIdRFC").val().replace("-","");
	    rfc=rfc.replace("-","");
	    szWhere = " dRFC = '" +rfc + "' ";
	    szTabla = "BENEFICIARIOCUENTASBANCARIASTMP";
	    $.getJSON("../catalogos/SelectJson.jsp", {
	            Tabla: szTabla,
	            Param: szWhere,
	            MaxReg: 20,
	            ajax: 'false'
	        },
	        function(j) {
	            var x,x2;
	            for (var i = 0; i < j.length; i++) {
	                if(j[i].Col5 == 1)
	                	x="Activo";
	                else 
	                	x="Inactivo";
	                if(j[i].Col10 == 1)
	                	x2="SI";
	                else 
	                	x2="NO";
	                $('#tblCuentasBancarias').dataTable().fnAddData([
	                		j[i].Col1 ,
	                    	j[i].Col6 ,
	                    	j[i].Col2 ,
	                    	j[i].Col3 ,
	                    	j[i].Col4 ,
	                    	j[i].Col7 ,
	                    	j[i].Col5 ,
	                    	x,
	                    	x2,
	                    	"NO",
	                    	"<a border=\"0\" href=\"#\" onclick=\"eliminaRegistro('" + j[i].Col3 +"','"+j[i].Col6+"','"+j[i].Col2+"','"+j[i].Col4+"','"+j[i].Col7+"','"+x+"');return false;\"><img border=\"0\" class=\"btnEliminar\" src=\"../imagenes/iconos/rechazar.png\" title=\"Eliminar Registro\"></img></a>" 	                
	                     ]);
	            }
	        });
	       
		/* llenado de datatable desde tabla Existente de cuentas bancarias 
		(Ya Autorizadas anteriormente, solo para que el usuario las visualice) */
	   
	    szTabla = "BENEFICIARIOCUENTASBANCARIAS2";
	    $.getJSON("../catalogos/SelectJson.jsp", {
	            Tabla: szTabla,
	            Param: szWhere,
	            MaxReg: 20,
	            ajax: 'false'
	        },
	         function(j) {
	            var x,x2;
	            for (var i = 0; i < j.length; i++) {
	                if(j[i].Col5 == 1)
	                	x="Activo";
	                else 
	                	x="Inactivo";
	                if(j[i].Col10 == 1)
	                	x2="SI";
	                else 
	                	x2="NO";
	                $('#tblCuentasBancarias').dataTable().fnAddData([
	                		j[i].Col1 ,
	                    	j[i].Col6 ,
	                    	j[i].Col2 ,
	                    	j[i].Col3 ,
	                    	j[i].Col4 ,
	                    	j[i].Col7 ,
	                    	j[i].Col5 ,
	                    	x,
	                    	x2,
	                    	"SI",
	                    	"<a border=\"0\" href=\"#\" onclick=\"eliminaRegistro('" + j[i].Col3 +"','"+j[i].Col6+"','"+j[i].Col2+"','"+j[i].Col4+"','"+j[i].Col7+"','"+x+"');return false;\"><img border=\"0\" class=\"btnEliminar\" src=\"../imagenes/iconos/rechazar.png\" title=\"Eliminar Registro\"></img></a>" 	                
	                     ]);
	                //insertar Documentacion para Comprobante de pago en Cuentas Existentes en el sistema
	                queryFormPost({
    						queryName: "readID_GABINETE",
    						async: false,
    						callback: function() {

        						var id_gabinete = parseInt($('#ID_GABINETE').val(),10);
        						$('#ID_GABINETE').val(id_gabinete);
        						$('#cNombreCuenta').val(j[i].Col6 + '-'+j[i].Col3);
        						$('#cUsuarioModifico').val($.trim("<%=usuario.getLogin()%>"));
        						$('#ComprobanteBancario').val(0);
        						queryFormPost("readComprobanteBancario", {async : false});
                				
                				if ($("#ComprobanteBancario").val() != 'EXISTE')	// en caso de que ya cuente con Documentacion (Actualizacion de Datos)	
        						queryFormPost({
            						queryName: "readDocSiguiente",
            						async: false,
            						callback: function() {

                						var docsiguiente = parseInt($('#docSiguiente').val(),10);
                						$('#docSiguiente').val(docsiguiente);
                	
	                					queryFormPost("creaDocumentoComprobatorioCuentaB", {async: false});
	                					
	                				}
                        		});

                    		}
                	});			
	            }
	        });
	    $("#tblCuentasBancarias tbody").dblclick(
								function(event) {
									$(
											$("#tblCuentasBancarias").dataTable()
													.fnSettings().aoData).each(
											function() {
												$(this.nTr).removeClass(
														'row_selected');
											});

									$(event.target.parentNode).addClass(
											'row_selected');

									aPos = daTable.dataTable().fnGetPosition(event.target.parentNode);
									
									var aData = daTable.fnGetData(aPos);

									var ibanco=aData[1];
									var icuenta=aData[3];
									
									$("#iBanco").val(ibanco);
									$("#iCuenta").val(icuenta);
									
									if($("#idoper").val()==5)
										if(aData[9]=="SI"){
	
											$("#desactivaCuenta").dialog("open");
											
										}else
											alert("Para cambiar el estatus de una cuenta debe estar Autorizada");
									
									return;
								});
	    $("#chk_extranjero").change(function(){
			esExtranjero();			
		});
		$("#chk_edTipoPersona").change(function(){
			seEdita();
		});		
		$("#Motivos").dialog({
	        title:"Motivos de Modificacion",
	        autoOpen : false,
	        height : 260,
	        width : 420,
	        modal : true,
	        buttons : {
	           	"Aceptar" : function() {
	                      updatemotivos();   
	            	},
	             	"Cancelar" : function() {
	               		$(this).dialog("close");               		
	               }
	        	}
	      });
	});
//-------------Fin del READY----------------------------------------------------------------------------------------------------------------
	
//-----------Funciones de Caso Inicio ---------------------------------------------------------------------------------------------------
	
	function onLoadPlantilla(id_oper) {//Carga Plantilla
		$("#esperar").dialog({
			autoOpen : false,
			height : 150,
			width : 200,
			modal : true,
			close : function() {
			}
		});
		$("#desactivaCuenta").dialog({
			autoOpen : false,
			height : 180,
			width : 300,
			modal : true,
			close : function() {
			}
		});
		//querySelectPost("catalogoTipoPersonaRFCRead", "cIdTipoPersonaRFC", {async : false});
		idOper=id_oper;
		//parent.document.getElementById("pb_cancel").disabled = true;
		$("#idoper").val(id_oper);
		revisaCamposVacios();
		revisaComas();
		$("#btnObservaciones").hide();
		document.getElementById("lbl_Editar").style.visibility = "hidden";
		document.getElementById("chk_edTipoPersona").style.visibility = "hidden";
		document.getElementById("lbl_CorreccionDatos").style.visibility = "hidden";
		document.getElementById("chk_CorreccionDatos").style.visibility = "hidden";
			
		querySelectPost("mCatalogoTipoTelefonoRead", "cTipoTelefono", {async : false});
		querySelectPost("mPymeRead", "nIdPyme", {async : false});
		querySelectPost("EntidadFederativaRead", "cEstadoFiscal", {async : false});
		//querySelectPost("pCatalogoTipoRFCReadAll", "cIdTipoRFC", {async : false});
		queryFormPost("checaAltaProvedor", {async : false});
		querySelectPost("BancosRead", "cbBanco", {async: false});
		esActualizacion();
		if ($("#existe").val() == 'EXISTE') {
			$("#existe").val(" ");
			queryFormPost("tAltaProvedorRead", {async : false});
			actualizaVariables();
			seleccionados();
			tipoPersonaChange();
			actualizaValores();
			if($("#cExtranjero").val()==1){
				document.getElementById("chk_extranjero").checked = true;
			}else{
				document.getElementById("chk_extranjero").checked = false;
			}
			if($("#cTipoRegistro").val()=="PROVEEDOR"){
				$("#tipoPB").val(1);
				$("#trdescProv").show();
			 	$("#trdescBen").hide();
			}else if($("#cTipoRegistro").val()=="BENEFICIARIO"){
				$("#tipoPB").val(2);
				$("#trdescProv").hide();
			 	$("#trdescBen").show();
			}else{
				$("#cTipoRegistro").val("PROVEEDOR");
				$("#tipoPB").val(1);
			}
		}else{
			querySelectPost("MunicipiosRead", "cMunicipioFiscal", {async : false});
		}
		esExtranjero();
		if(id_oper==1){//captura
			parent.document.getElementById("pb_cancel").disabled = false;
			seEdita();
			document.getElementById("cuentasBancarias").style.display = "none";
			document.getElementById("bAgregarCuentasBancarias").style.display = "none";
			if($("#cDocumentoHaplicado").val() == 'I' || $("#cDocumentoHaplicado").val() == 'D'){
				alert("Devolucion de Alta de Proveedor\nObservaciones: "+$("#cObservaciones").val());
				document.getElementById("lbl_Editar").style.visibility = "visible";
				document.getElementById("chk_edTipoPersona").style.visibility = "visible";
				$("#btnObservaciones").show();
				if($("#cDocumentoHaplicado").val() == 'D')
					parent.document.getElementById("pb_cancel").disabled = true;
			}
			buscaEmpleado();
		}else if(id_oper==2){// Estatus de Captura Cuenta Bancaria
			parent.document.getElementById("pb_cancel").disabled = true;
			document.getElementById("cuentasBancarias").style.visibility = "visible";
			document.getElementById("bAgregarCuentasBancarias").style.visibility = "visible";
			//soloLectura(); Solicitud para que solo cuando sea rechazo esten inhabilitados los campos
			buscaEmpleado();
			if($("#cDocumentoHaplicado").val() == 'R'){
				alert("Devolucion de Alta de Proveedor\nObservaciones: "+$("#cObservaciones").val());
				$("#btnObservaciones").show();
				soloLectura();
				parent.document.getElementById("pb_cancel").disabled = true;
				rechazoAutorizacion =true;
			}else if(($("#cDocumentoHaplicado").val() == 'A')){
				soloLectura();
				actualizaCuentasBancarias=true;
				parent.document.getElementById("pb_leave").disabled = true;
				$("#botonLibera").show();
			}else if(($("#cDocumentoHaplicado").val() == 'D')){
				alert("Devolucion de Alta de Proveedor\nObservaciones: "+$("#cObservaciones").val());
				$("#btnObservaciones").show();
				soloLectura();
				parent.document.getElementById("pb_cancel").disabled = true;
			}
			
		}else if(id_oper==4){// /Autorizacion
			//buscaEmpleado();
			$("#cIdRFCB").val($("#cIdRFC1").val() +$("#cIdRFC2").val()+$("#cIdRFC3").val());
			queryFormPost("checaEmpleadoRFC", {async : false});
				if ($("#regreso").val() == 'EXISTE' ) {
					queryFormPost("extraeNumEmpleado", {async : false});
				}
			document.getElementById("NEmp").readOnly = true;
			document.getElementById("NEmp").className = "notEditable";
			parent.document.getElementById("pb_cancel").disabled = true;
			$("#fieldAutoriza").show();
			$("#botonExtrae").show();
			document.getElementById("bAgregarCuentasBancarias").style.display = "none";
			soloLectura();
			$("#cIdRFCB").val($("#cIdRFC1").val() +$("#cIdRFC2").val()+$("#cIdRFC3").val());
			queryFormPost("readcben", {async : false});
			$("#trCBEN").show();
			if($("#cDocumentoHaplicado").val() == 'R'|| $("#cDocumentoHaplicado").val() == 'D'){
				alert("Devolucion de Alta de Proveedor\nObservaciones: "+$("#cObservaciones").val());
				$("#btnObservaciones").show();
			}else if($("#cDocumentoHaplicado").val() == 'P'){
				alert("MODIFICACION DE TIPO PERSONA:"+$("#cObservaciones").val());
				$("#btnObservaciones").show();
			}
			document.getElementById("autorizaSi").checked = false;
			document.getElementById("autorizaNo").checked = false;
		}else if(id_oper==3){// Estatus de validacion
			buscaEmpleado();
			parent.document.getElementById("pb_cancel").disabled = true;
			$("#fieldAutoriza").show();
			$("#botonExtrae").show();
			document.getElementById("bAgregarCuentasBancarias").style.display = "none";
			//soloLectura();
			if($("#cDocumentoHaplicado").val() == 'I'|| $("#cDocumentoHaplicado").val() == 'D'){
				alert("Devolucion de Alta de Proveedor\nObservaciones: "+$("#cObservaciones").val());
				$("#btnObservaciones").show();
			}else if($("#cDocumentoHaplicado").val() == 'P'){
				alert("MODIFICACION DE TIPO PERSONA:"+$("#cObservaciones").val());
				$("#btnObservaciones").show();
			}
			document.getElementById("legendAutoriza").innerHTML="Validar";
			document.getElementById("autorizaSi").checked = false;
			document.getElementById("autorizaNo").checked = false;
		}else if(id_oper==5){// Estatus de Consulta
			buscaEmpleado();
			document.getElementById("bAgregarCuentasBancarias").style.display = "none";
			$("#cIdRFCB").val($("#cIdRFC1").val() +$("#cIdRFC2").val()+$("#cIdRFC3").val());
			queryFormPost("readcben", {async : false});
			$("#trCBEN").show();
			$("#botonExtrae").show();
			document.getElementById("CBEN").className = "notEditable";
			$("#trdescBen").hide();
			$("#trdescProv").hide();
			soloLectura();
		}else if(id_oper==6){// Estatus de Modificacion
			buscaEmpleado();
			parent.document.getElementById("pb_cancel").disabled = true;
			if(($("#cDocumentoHaplicado").val() == 'P')){
				soloLectura();
				document.getElementById("habilitaNo").disabled = true;
				document.getElementById("habilitaSi").disabled = true;
			}
			$("#cObservaciones").val("");
			parent.document.getElementById("pb_leave").disabled = true;
			$("#botonLibera").show();
			$("#tdObservacion").show();
			actualizacion=true;
			$("#fieldHabilita").show();
			document.getElementById("bAgregarCuentasBancarias").style.display = "none";
			queryFormPost("mCatalogoProveedorReadHabilitado", {async : false});
			if($("#cTipoRegistro").val()=="BENEFICIARIO"){
				$("#lHabilitado").val("0");
				document.getElementById("habilitaNo").checked = true;
				document.getElementById("habilitaSi").checked = false;
				document.getElementById("habilitaNo").disabled = true;
				document.getElementById("habilitaSi").disabled = true;
			}else if($("#lHabilitado").val()=="1"){
				document.getElementById("habilitaNo").checked = false;
				document.getElementById("habilitaSi").checked = true;
			}else{
				document.getElementById("habilitaNo").checked = true;
				document.getElementById("habilitaSi").checked = false;
			}
			document.getElementById("lbl_Editar").style.visibility = "visible";
			document.getElementById("chk_edTipoPersona").style.visibility = "visible";
			seEdita();
			$("#cActualizacion").val("1");
			queryFormPost("cActualizacionUpdate", {async : false});
			esActualizacion();
			$("#tdObservacion").show();
		}
	}// fin onLoadPlantilla
	function validaHomoclave(){
		var resp=true;
		var homoclave=$("#cIdRFC3").val();
		var msg="";
		var token="";
		
		if($("#cIdTipoPersonaRFC").val()!=3){
			if(homoclave=="" || homoclave=="   "){
				msg="La homoclave es un dato requerido.";
				resp=false;
				token="\n";
			}
			if(homoclave=="000"){
				msg+=token+"La homoclave no puede ser 000.";
				resp=false;
				token="\n";
			}
			if(homoclave.length<3){
				resp=false;
				msg+=token+"La homoclave deben ser 3 caracteres.";
			}
		}
		///
		if(!resp){
			alert(msg);
			
		}
		return resp;
	}
	function onSubmit(id_oper) {//clic boton Guardar
		switch (parseInt(id_oper,10)){
		case 1://Captura
		case 6://Modifica
		case 8://Guardar en Modificacion pero sin habilitar el boton enviar (al Agregar una cuenta Bancaria)
			var p = window.parent;
			revisaComas();
			document.getElementById("nIdPyme").disabled = false;
			p.gestion.setFolio($("#cFolio").val());
			p.gestion.setOperador("<%=usuario.getNombre()%>");
			p.gestion.setFechaDocumento("<%=today%>"); 
			p.gestion.setEjercicioFiscal("<%=today.substring(6)%>"); 
			ids();//inicializa variables
			tipoPBChange();
			if($("#cIdRFC1").val()==""){
				alert("El RFC no puede ir vacio");
				return false;
			}
			if(!validaHomoclave()){
				return false;
			}
			//if(!validacionDatos()){
			//	return false;
			//}
			if(id_oper==6){
					if($("#cDocumentoHaplicado").val()=='P'){
						$("#cIdRFCB").val($("#cIdRFC1").val() +$("#cIdRFC2").val()+$("#cIdRFC3").val());
						//inhabilita Proveedor
						$("#lHabilitado").val("0");
						queryFormPost({
							queryName: "proveedorHabilita",
							async: false,
							callback: function() {queryFormPost({
													queryName: "UpdateDesactivanBCBEnviadoSICOP",
													async: false,
													callback: function() {queryFormPost({
																			queryName: "UpdatetBeneficiarioNuevoCBEN",
																			async: false,
																			callback: function() {}});
																			}});
																		}});
						
					}
					else
						$("#cDocumentoHaplicado").val("S");
				
				if($("#cObservaciones").val()==""){
					creaDialogoMotivos();
					return false;
				}
				if($("#cTipoRegistro").val()=="BENEFICIARIO" && $("#chk_edTipoPersona").prop("checked")){
					$("#lHabilitado").val("0");
					document.getElementById("habilitaNo").checked = true;
					document.getElementById("habilitaSi").checked = false;
					document.getElementById("habilitaNo").disabled = true;
					document.getElementById("habilitaSi").disabled = true;
				}
				else if ($("#chk_edTipoPersona").prop("checked") && $("#cTipoRegistro").val()=="PROVEEDOR"){
					$("#lHabilitado").val("1");
					document.getElementById("habilitaNo").checked = false;
					document.getElementById("habilitaSi").checked = true;
					document.getElementById("habilitaNo").disabled = false;
					document.getElementById("habilitaSi").disabled = false;
				}
				queryFormPost({
					queryName: "tAltaProveedorUpdateUltimaModif",
					async: false,
					callback: function() {
						queryFormPost({
							queryName: "BitacoraModificacionAltaProveedor",
							async: false,
							callback: function() {
								if($("#cDocumentoHaplicado").val()=='P'){
									$("#cObservaciones").val(" "+$("#cObservaciones").val());
								}else{
									$("#cObservaciones").val("");
								}
								queryFormPost("tAltaProveedorUpdateObservaciones", {async:false});
								parent.document.getElementById("pb_save").disabled = true;
								queryFormPost({
									queryName: "tAltaProveedorUpdate",
									async: false,
									callback: function() {
										queryFormPost({
											queryName: "cActualizacionUpdate",
											async: false,
											callback: function() {
												return true;
											}
										});
									}
								});								
							}
						});
					}
				});
			}
			queryFormPost("checaAltaProvedor", {async : false});
			if ($("#existe").val() == 'EXISTE') {
				$("#existe").val(" ");
				queryFormPost("tAltaProveedorUpdate", {async : false});
			} else
				queryFormPost("tAltaProveedorCreate", {async : false});
			if(id_oper!=8)
				parent.document.getElementById("pb_send").disabled = false;
			revisaDocumentacion($("#cIdTipoPersonaRFC").val());
			if(actualizacion)
				queryFormPost("cActualizacionUpdate", {async : false});
			return true;
			break;
		case 2://Captura Cuenta Bancaria
			if(!validaHomoclave()){
				return false;
			}
			$("#cIdRFCB").val($("#cIdRFC1").val() +$("#cIdRFC2").val()+$("#cIdRFC3").val());
			if(!actualizaCuentasBancarias)	
				queryFormPost("UpdateDesactivanBCBEnviadoSICOP", {async : false});
			else{
				queryFormPost("checaCuentasNuevas", {async: false});//se valida si tieneCuentasRegistradas
				if($("#tieneCuentasRegistradas").val()=="EXISTE")
					sincambios=false;
				else
					sincambios=true;
			}
				
			if(revisaDocumentacionCuentaBancaria()){
				parent.document.getElementById("pb_send").disabled = false;
				return true;
			}
			
			break;
		case 3:// Validacion
			if(document.getElementById("autorizaNo").checked){
				$("#cDocumentoHaplicado").val("I");
				document.getElementById("cObservaciones").readOnly = true;
				document.getElementById("cObservaciones").className = "notEditable";
				devolucion=true;
			}else if(document.getElementById("autorizaSi").checked){
				if(!validaHomoclave()){
					return false;
				}
				if($("#cDocumentoHaplicado").val()!="R" && $("#cDocumentoHaplicado").val()!="D")
					$("#cDocumentoHaplicado").val("V");
				//$("#cObservaciones").val("");
			}else{
				alert("Debe seleccionar una opcion de Autorizacion");
				return false;
			}
			ids();
			queryFormPost({
        					queryName: "tAltaProveedorUpdateCampos",
        					async: false,
					        callback: function() {
					        	parent.document.getElementById("pb_send").disabled = false;
								document.getElementById("autorizaSi").disabled = true;
								document.getElementById("autorizaNo").disabled = true;
								}
							});
							return true;
			break;
		case 4://Autorizacion
			if(document.getElementById("autorizaNo").checked){
				$("#cDocumentoHaplicado").val("R");
				document.getElementById("cObservaciones").readOnly = true;
				document.getElementById("cObservaciones").className = "notEditable";
				devolucion=true;
			}else if(document.getElementById("autorizaSi").checked){
				$("#cDocumentoHaplicado").val("S");
				//$("#cObservaciones").val("");
			}else{
				alert("Debe seleccionar una opcion de Autorizacion");
				return false;
			}
			parent.document.getElementById("pb_send").disabled = false;
			document.getElementById("autorizaSi").disabled = true;
			document.getElementById("autorizaNo").disabled = true;
			return true;
			break;
		}// fin switch
	}// fin onSubmit
	
	function ResponsableSiguiente(id_oper) {
		switch (parseInt(id_oper,10)){
		case 1:// Captura
			return "CAPTURA_PROVEEDOR";
			break;
		case 2:// Captura Cuenta Bancaria
			
			if(rechazoAutorizacion||actualizaCuentasBancarias){
				if(sincambios)
					return "CONSULTA_PROVEEDOR";
				else
					return "AUTORIZA_PROVEEDOR";
			}else
				return "VALIDA_PROVEEDOR";
			break;
		case 3:// Validacion
			if(devolucion){
				return "CAPTURA_PROVEEDOR";
			}else;
				return "AUTORIZA_PROVEEDOR";
			break;
		case 4:// Autorizacion
			if(devolucion){
				return "CAPTURA_PROVEEDOR";
			}else;
				return "CONSULTA_PROVEEDOR";
			break;
		case 5: //Consulta
			return "MODIFICA_PROVEEDOR";
		case 6://mofidicaciones
			if($("#cDocumentoHaplicado").val()=='P')
				return "VALIDA_PROVEEDOR";
			else
				return "CONSULTA_PROVEEDOR";
		default:
			return "CONSULTA_PROVEEDOR";
			break;
		}// fin switch
	}// fin ResponsableSiguiente

	function OperacionSiguiente(id_oper) {
		switch (parseInt(id_oper,10)){
		case 1://Captura
			return "captura_cuentabancaria_proveedor";
			break;
		case 2: //Captura Cuenta Bancaria
			if(rechazoAutorizacion||actualizaCuentasBancarias){
				if(sincambios)
					return "consulta_proveedor";
				else
					return "autoriza_proveedor";
				
			}else
				return "valida_proveedor";
			break;
		case 3://Validacion
			if(devolucion){
				return "captura_proveedor";
			}else
				return "autoriza_proveedor";
			break;
		case 4://Autorizacion
			if(devolucion){
				if ($("#chk_CorreccionDatos").prop("checked")){
					return "captura_proveedor";
				}else{
					return "captura_cuentabancaria_proveedor";
				}
			}else
				return "consulta_proveedor";
			break;
		case 5://consulta
			return "modifica_proveedor";
		case 6://modificacion
			if($("#cDocumentoHaplicado").val()=='P')
				return "valida_proveedor";
			else
				return "consulta_proveedor";
		default:
			return "consulta_proveedor";
			break;
		}// fin switch
	}// fin OperacionSiguiente
	
	function onPostDisplay(id_oper) {
		if (id_oper==6){
			soloLectura();
			$("#botonLibera").hide();
		}
	}// fin onPostDisplay
	
	function onPostSubmit(id_oper) {// clic boton enviar
				var esValido = true;
				var tipo = $("#cIdTipoPersonaRFC").val();
				revisaComas();
				queryFormPost("sp_tAltaProveedorUltimoMovimiento", {async : false});
				if(id_oper==1||id_oper==6){//Captura-Modificacion
// 					tips.text("");
					remplazavariables();
					
					if( $("#cActualizacion").val() != 1 )
						existeProveedor();
					
					if(!validacionDatos()){
						return false;
					}
					
					if(esValido){
						onSubmit();
						esValido=esValido && revisaDocumentacion(tipo);
						if(esValido){
							if(id_oper==6){
								queryFormPost("proveedorHabilita", {async : false});
								queryFormPost("tAltaProveedorUpdateUltimaModif", {async: false});
								if($("#cActualizacion").val()==1){//UPDATE
									esValido = false;
									if($("#cTipoRegistro").val()=="PROVEEDOR"){
										preparaCamposInsert();
										queryFormPost("checarfc", {async : false});
										if($("#regreso").val() == 'EXISTE'){
										queryFormPost({
        									queryName: "mCatalogoProveedorAP",
        									async: false,
					        				callback: function() {
					        					esValido = registraCuentasBancarias();
					        					$("#esperar").dialog("open");
					        					//Falta Actualizar Telefonos
					        				}});
					        			}else{
					        					queryFormPost({
        										queryName: "mCatalogoProveedorAPCreate",
        										async: false,
					        					callback: function() {
            										queryFormPost({
                										queryName: "mCatalogoProveedorTelefonoAPCreate",
					                					async: false,
                										callback: function() {
                    										esValido = registraCuentasBancarias();
                    										$("#esperar").dialog("open");
                										}
            									});
       					 					}
  					  					});
					        		} 
					        	}else if($("#cTipoRegistro").val()=="BENEFICIARIO"){
					        			$("#cIdRFCB").val($("#cIdRFC1").val() +$("#cIdRFC2").val()+$("#cIdRFC3").val());
					        			if($("#cIdTipoPersonaRFC").val()==1){
					        					queryFormPost({
        											queryName: "mCatalogoProveedorUpdateMoral",
        											async: false,
					        						callback: function() {
					        							esValido = registraCuentasBancarias();
					        							$("#esperar").dialog("open");
					        						}
					        					});
					        			}else if($("#cIdTipoPersonaRFC").val()==2||$("#cIdTipoPersonaRFC").val()==3){
					        				queryFormPost({
        										queryName: "mCatalogoProveedorUpdateFisica",
        										async: false,
					        					callback: function() {
					        						esValido = registraCuentasBancarias();
					        						$("#esperar").dialog("open");
					        					}
					        				});
					        		}
					        	}
					        
						}return esValido;
							}else
								if(confirm("Esta seguro de registrar el RFC: "+$("#cIdRFC").val())){
									$("#esperar").dialog("open");
									return esValido;
								}else
									return false;
						}
					}else{
					parent.document.getElementById("pb_send").disabled = true;//deshabilita boton enviar para obligar al usuario en dar clic en Guardar
					}
					actualizaVariables();
				}else if(id_oper==2){//Cuenta Bancaria Solo avanza Caso
					if(sincambios&&actualizaCuentasBancarias)
						queryFormPost("tAltaProveedorUpdateAplicado", {async: false});
					if($("#cTipoRegistro").val()=="BENEFICIARIO"){
						queryFormPost("checaCuentasExitentes", {async: false});//se valida si tieneCuentasRegistradas
						if($("#tieneCuentasRegistradas").val()!="EXISTE"){
							queryFormPost("checaCuentasNuevas", {async: false});//se valida si tieneCuentasRegistradas
							if($("#tieneCuentasRegistradas").val()!="EXISTE"){
								alert("Debe registrar al menos una cuenta bancaria");
							}else{
								$("#esperar").dialog("open");
								return true;
							}
						}else{
							$("#esperar").dialog("open");
							return true;
						}
					}else{
						$("#esperar").dialog("open");
						return true;
					}
				}else if(id_oper==4){// /Autorizacion
					if(devolucion){
						alert("El Documento regresa a Captura para ser modificado");
							if ($("#chk_CorreccionDatos").prop("checked")){
								$("#lHabilitado").val("0");//inhabilitarProveedor
								queryFormPost("proveedorHabilita", {async: false});
								$("#cDocumentoHaplicado").val("D");//Update Estatus (docHaplicado)
							}
					}
					queryFormPost("tAltaProveedorUpdateObservaciones", {async: false});
					$("#cIdUsuarioAutoriza").val("<%=usuario.getLogin()%>");
					queryFormPost("tAltaProveedorUpdateAutoriza", {async: false});//Update Autoriza
					$("#esperar").dialog("open");
					if(document.getElementById("autorizaSi").checked){
						if(actualizaCuentasBancariasExistentes()){
							if( insertaRelCasoRFC() )
								return registraCuentasBancarias();
						}
						else{
							alert("Problema al registrar las cuentas bancarias");
						}
					} else return true;
						
				}else if(id_oper==3){//Valida
					$("#cIdUsuarioValida").val("<%=usuario.getLogin()%>");
					queryFormPost("tAltaProveedorUpdateValida", {async: false});//Update Usuario valida
					if($("#cTipoRegistro").val()=="PROVEEDOR"){
						queryFormPost("checarfc", {async : false});
					}else{
						queryFormPost("checarfcBeneficiarios", {async: false});
					}
					if($("#regreso").val() == 'EXISTE'){
						$("#cActualizacion").val("1");
						queryFormPost("cActualizacionUpdate", {async : false});
						$("#regreso").val("");
					}else{
						$("#cActualizacion").val("");
						queryFormPost("cActualizacionUpdate", {async : false});
					} 
					if(devolucion){
						alert("El Documento regresa a Captura para ser modificado");
						$("#esperar").dialog("open");
					}
					else if($("#cActualizacion").val()==1){//UPDATE
						esValido = false;
						if($("#cTipoRegistro").val()=="PROVEEDOR"){
							preparaCamposInsert();
							queryFormPost("checarfc", {async : false});
							if($("#regreso").val() == 'EXISTE'){
							queryFormPost({
        						queryName: "mCatalogoProveedorAP",
        						async: false,
					        	callback: function() {
					        		queryFormPost({
					        			queryName: "proveedorHabilita",
        								async: false,
					        			callback: function() {
					        			esValido = true;
					        			$("#esperar").dialog("open");
					        			}
					        		});
					        	}});
					        	}else{
					        		queryFormPost({
        							queryName: "mCatalogoProveedorAPCreate",
        							async: false,
					        		callback: function() {
            							queryFormPost({
                							queryName: "mCatalogoProveedorTelefonoAPCreate",
					                		async: false,
                							callback: function() {
                    							esValido = true;
                    							$("#esperar").dialog("open");
                						}
            						});
       					 		}
  					  		});
					        	} 
					        }else if($("#cTipoRegistro").val()=="BENEFICIARIO"){
					        	$("#cIdRFCB").val($("#cIdRFC1").val() +$("#cIdRFC2").val()+$("#cIdRFC3").val());
					        	queryFormPost("checarfcBeneficiarios", {async: false});
					        	if($("#cIdTipoPersonaRFC").val()==1){
					        		if($("#regreso").val() == 'EXISTE'){
						        		queryFormPost({
	        								queryName: "mCatalogoProveedorUpdateMoral",
	        								async: false,
						        			callback: function() {
						        				esValido = true;
						        				$("#esperar").dialog("open");
						        			}
						        		});
					        		}else{
					        			queryFormPost({
		  					          		queryName: "tBeneficiarioMoralCreate",
		  					          		async: false,
		    					       		callback: function() {
		      					        		queryFormPost("readcbeneneficiario", {async: false});
		      					        		$("#CBEN").val("C"+$("#cBeneficiario").val());
		      					        		queryFormPost({
		  					          				queryName: "UpdateCBEN",
		  					          				async: false,
		    					       				callback: function() {
		      					        				esValido = true;
		      					        				$("#esperar").dialog("open");
		      					        			}
		      					        		});
		         							}
		   					     		});
					        		}					        		
					        	}else if($("#cIdTipoPersonaRFC").val()==2||$("#cIdTipoPersonaRFC").val()==3){
					        		if($("#regreso").val() == 'EXISTE'){
						        		queryFormPost({
	        								queryName: "mCatalogoProveedorUpdateFisica",
	        								async: false,
						        			callback: function() {
						        				if($("#cIdTipoPersona").val()==3){
    					       						queryFormPost("UpdatetBeneficiarionIdEmpleado", {async: false});
    					       					}
						        				esValido = true;
						        				$("#esperar").dialog("open");
						        			}
						        		});
					        		}else{
					        			
						        		queryFormPost({
		  					          		queryName: "tBeneficiarioFisicaCreate",
		  					          		async: false,
		    					       		callback: function() {
		    					       			queryFormPost("readcbeneneficiario", {async: false});
		    					       			if($("#cIdTipoPersona").val()==3){
    					       						queryFormPost("UpdatetBeneficiarionIdEmpleado", {async: false});
		      					        			$("#CBEN").val("E"+$("#cBeneficiario").val());
		      					        			queryFormPost("UpdatetBeneficiarioEmpelado", {async: false});
		      					        		}else{
		      					        			$("#CBEN").val("C"+$("#cBeneficiario").val());
		      					        		}
		    					       			queryFormPost({
		  					          				queryName: "UpdateCBEN",
		  					          				async: false,
		    					       				callback: function() {
		      					        				esValido = true;
		      					        				$("#esperar").dialog("open");
		      					        			}
		      					        		});
		         							}
		   					     		});
	   					     		}
					        	}
					        }
					        
					}else { //insert
					if($("#cTipoRegistro").val()=="PROVEEDOR"){
					    	preparaCamposInsert();
					    	esValido = false;
    						queryFormPost({
        						queryName: "mCatalogoProveedorAPCreate",
        						async: false,
					        	callback: function() {
            						queryFormPost({
                						queryName: "mCatalogoProveedorTelefonoAPCreate",
					                	async: false,
                						callback: function() {
                    						esValido = true;
                    						$("#esperar").dialog("open");
                						}
            						});
       					 		}
  					  		});
  					  		if (tipo == 1 && esValido) {
  					      		esValido = false;
					        	queryFormPost({
  					          		queryName: "tBeneficiarioAP",
  					          		async: false,
    					       		callback: function() {
      					        		esValido = true;
      					        		$("#esperar").dialog("open");
         							}
   					     		});
  					  		}
						}else if($("#cTipoRegistro").val()=="BENEFICIARIO"){//InsertBeneficiario
							esValido = false;
							$("#cIdRFCB").val($("#cIdRFC1").val() +$("#cIdRFC2").val()+$("#cIdRFC3").val());
							if($("#cIdTipoPersonaRFC").val()==1){
								queryFormPost({
  					          		queryName: "tBeneficiarioMoralCreate",
  					          		async: false,
    					       		callback: function() {
      					        		queryFormPost("readcbeneneficiario", {async: false});
      					        		$("#CBEN").val("C"+$("#cBeneficiario").val());
      					        		queryFormPost({
  					          				queryName: "UpdateCBEN",
  					          				async: false,
    					       				callback: function() {
      					        				esValido = true;
      					        				$("#esperar").dialog("open");
      					        			}
      					        		});
         							}
   					     		});
							}else{
								
								
								queryFormPost({
  					          		queryName: "tBeneficiarioFisicaCreate",
  					          		async: false,
    					       		callback: function() {
    					       			queryFormPost("readcbeneneficiario", {async: false});
    					       			if($("#cIdTipoPersona").val()==3){
    					       				queryFormPost("UpdatetBeneficiarionIdEmpleado", {async: false});
      					        			$("#CBEN").val("E"+$("#cBeneficiario").val());
      					        			queryFormPost("UpdatetBeneficiarioEmpelado", {async: false});
      					        		}else{
      					        			$("#CBEN").val("C"+$("#cBeneficiario").val());
      					        		}
    					       			queryFormPost({
  					          				queryName: "UpdateCBEN",
  					          				async: false,
    					       				callback: function() {
      					        				esValido = true;
      					        				$("#esperar").dialog("open");
      					        			}
      					        		});
         							}
   					     		});
							}	
						}
					}
				queryFormPost("tAltaProveedorUpdateObservaciones", {async: false});
				if(devolucion){
					return esValido;
				}else{
					$("#cIdRFCB").val($("#cIdRFC1").val() +$("#cIdRFC2").val()+$("#cIdRFC3").val());
					if ($("#cExtranjero").val()==1)
						queryFormPost("tBeneficiarioUpdateExtranjero", {async: false});
					return esValido;
				}
			}
	}// fin  onPostSubmit
	
//-------------Funciones de Caso Fin --------------------------------------------------------------------------------------------------------



//------------Funciones Para Documentacion--------------------------------------------------------------------------------------------------
	function revisaDocumentacion(tipo) {//funcion para revisar la documentacion adjunta
		var x, txt = "";
		switch (parseInt(tipo,10)) {	
			case 1://PERSONA MORAL
				querySelectPost("documentosMoralRead", "cIdTipoDoc", {async : false});
				document.getElementById("cIdTipoDoc").selectedIndex = "0";
				x = $("#cIdTipoDoc").val();
				if (x < 1) {
					txt = txt + "*Acta Constitutiva-Poder Notarial \n";
				}
				/*document.getElementById("cIdTipoDoc").selectedIndex = "1";
				x = $("#cIdTipoDoc").val();
				if (x < 1) {
					txt = txt + "*Poder Notarial \n";
				}
				document.getElementById("cIdTipoDoc").selectedIndex = "2";
				x = $("#cIdTipoDoc").val();
				if (x < 1) {
					txt = txt + "*IdentificacionRepresetanteLegal \n";
				}*/
				if($("#nIdPyme").val()!=4){
				document.getElementById("cIdTipoDoc").selectedIndex = "1";
				x = $("#cIdTipoDoc").val();
				if (x < 1) {
					txt = txt + "*Formato del sector MIPYME \n";
					}
				}
				break;
			case 2 ://PERSONA FISICA
			case 3 ://EMPLEADO CNF
				querySelectPost("documentosFisicaRead", "cIdTipoDoc", {async : false});
				document.getElementById("cIdTipoDoc").selectedIndex = "0";
				x = $("#cIdTipoDoc").val();
				if (x < 1) {
					txt = txt + "*Acta de Nacimiento \n";
				}
				document.getElementById("cIdTipoDoc").selectedIndex = "1";
				x = $("#cIdTipoDoc").val();
				if (x < 1) {
					txt = txt + "*CURP \n";
				}
				if($("#nIdPyme").val()!=4){
				document.getElementById("cIdTipoDoc").selectedIndex = "2";
				x = $("#cIdTipoDoc").val();
				if (x < 1) {
					txt = txt + "*Formato del sector MIPYME \n";
				}
				}
				break;		
		}//fin switch
			//Documentos en General
		querySelectPost("documentosGralRead", "cIdTipoDoc", {async : false});
		document.getElementById("cIdTipoDoc").selectedIndex = "0";
		x = $("#cIdTipoDoc").val();
		if (x < 1) {
			txt = txt + "*Cedula de Registro(RFC) \n";
		}
		document.getElementById("cIdTipoDoc").selectedIndex = "2";
		x = $("#cIdTipoDoc").val();
		if (x < 1) {
			txt = txt + "*Identificacion \n";
		}
		document.getElementById("cIdTipoDoc").selectedIndex = "1";
		x = $("#cIdTipoDoc").val();
		if (x < 1) {
			txt = txt + "*Comprobante de Domicilio \n";
		}
		
		if($("#cActualizacion").val()==1&&parseInt(tipo,10)==3)
			txt="";
			
		if (txt != ""){// Se manda mensaje con la Documentacion Faltante
			alert("Falta Adjuntar Documentacion Comprobatoria:\n" + txt);
			parent.document.getElementById("pb_send").disabled = true;
			return false;
		}else 
			return true;
	}// fin revisaDocumentacion
	
	function revisaDocumentacionCuentaBancaria(){//funcion para la documentacion de las Cuentas Bancarias
		queryFormPost("tieneDocumentosBancarios", {async : false});
		var x,doc,txt="Falta Documentacion Comprobatoria de las cuentas:\n";
		querySelectPost("documentosBancarios", "cDocBanca", {async : false});
		x = parseInt($("#tiene").val(),10);
		if(x>0){
			for(y=0;y<x;y++){
				document.getElementById("cDocBanca").selectedIndex = y;
				txt =txt + "* "+$("#cDocBanca").val()+"\n";
			}
			alert(txt);
			return false;
		}else
		return true;
	}// fin revisaDocumentacionCuentaBancaria
	
//------------Funciones Para Documentacion Fin--------------------------------------------------------------------------------------------------


	function preparaCamposInsert(){
		var tipo = $("#cIdTipoPersonaRFC").val();
		switch (parseInt(tipo,10)) {
			case 0://COMISIÓN NACIONAL FORESTAL
			break;
			case 1://PERSONA MORAL
				cRepresentanteLegal=$("#cNombre").val()+" "+$("#cApellidoPaterno").val()+" "+$("#cApellidoMaterno").val();
				$("#cRepresentanteLegal").val(cRepresentanteLegal);
			break;
			case 2://PERSONA FISICA
				cRazonSocial=$("#cNombre").val()+" "+$("#cApellidoPaterno").val()+" "+$("#cApellidoMaterno").val();
				$("#cRazonSocial").val(cRazonSocial);
			break;
			case 3://EMPLEADO CNF
			break;
			case 4://AREA CNF
			break;
			case 5://QUINCENAS
			break;
		}
		$("#cIdRFC").val($("#cIdRFC1").val() + "-" + $("#cIdRFC2").val() + "-"+
		$("#cIdRFC3").val());
		queryFormPost("nombreMunicipioRead", {async : false});
		$("#nPyme").val(parseInt(nIdPyme,10));
		if($("#cIdEntidadFederativa").val().length==1){
			$("#cIdEntidadFederativa").val("0"+$("#cIdEntidadFederativa").val());
		}
	}
	
	function actualizaCuentasBancariasExistentes(){
		queryFormPost("readNvoCBEN", {async: false});
			rtn=false;
		if($("#cNvoCBEN").val()==1){
			queryFormPost({
						queryName: "UpdatetBeneficiarioNuevoCBEN",
							async: false,
							callback: function() {
								rtn= true;
							}
					});
				}else
				queryFormPost({
						queryName: "UpdatenHabilitaBCBEnviadoSICOP",
						async: false,
						callback: function() {
							rtn= true;
						}
					});
		return rtn;		
	}
	
	function registraCuentasBancarias() { //funcion para Pasar Cuentas Bancarias de la Tabla temporal a la definitiva
		queryFormPost("nCuentasBancarias", {async: false});
		var x,i=0;
		querySelectPost("readCuentasBancariasTmp", "ctaBanca", {async: false});
		x = parseInt($("#ncuentas").val(),10);
		if(x > 0) {
			for(y = 0; y < x; y++) {
				document.getElementById("ctaBanca").selectedIndex = y;
				queryFormPost({
					queryName: "CuentasBancariasUpdate",
					async: false,
					callback: function() {
						queryFormPost({
							queryName: "CtaBancariaTmpDelete",
							async: false,
							callback: function() {
								if($("#cIdTipoPersona").val()==3){
									$("#cIdRFCB").val($("#cIdRFC1").val()+$("#cIdRFC2").val()+$("#cIdRFC3").val());
									queryFormPost({
										queryName: "UpdatenBCBEnviadoSICOP",
										async: false,
										callback: function() {
										i++;
										}
									});
								}else{
									i++;
								}
							}
						});
					}
				});
			}
			if(x==i)
				return true;
			else{
				alert("Hubo algun error al momento de agregar las cuentas bancarias");
				return false;
			}
		}else 
			return true;
	}
	
	function onlyNumbersAndLetters(evt, elem) {
		var keyPressed = (evt.which) ? evt.which : event.keyCode;
		var strCheck = "!@#$%^&*()´+=-[]\\';,./{}|\":<>?";
		var key = String.fromCharCode(keyPressed);
		if (strCheck.indexOf(key) == -1) {
			//fue ingresado un caracter valido
			if (elem.name == "cIdRFC2" || elem.name == "NEmp") {//Solo numeros para esta parte del RFC
				strCheck = '0123456789';
				if (strCheck.indexOf(key) == -1)
					return false;
				else
					return true;
			} else
				return true;
		}
		return false;
	}

	function tipoPersonaChange() {//muestra opciones segun sea el tipo de persona
		document.getElementById("chk_extranjero").checked = false;
		$("#trNoEmpleado").hide();
		esExtranjero();
		$("#lbl_provExtra").show();
		$("#chk_extranjero").show();
		$("#cGiro").val("");
		//$("#tipoPB").val(1);
    	//$("#cTipoRegistro").val("PROVEEDOR");
    	$("#trdescProv").show();
    	$("#trdescBen").hide();
		var tipo = $("#cIdTipoPersonaRFC").val();
		if (tipo == 2 || tipo == 3) {//FISICA/EMPLEADO
			document.getElementById('cIdRFC1').maxLength = 4;
			$("#trCurp").show();
			$("#pMoralRepresentante").hide();
			$("#pMoralRazon").hide();
			if(tipo==3){//solo Empleado CNF
				$("#tipoPB").hide();
				//$("#trGiro").hide();
				$("#trPyme").hide();
				$("#nIdPyme").val("4");
				//$("#cIdTipoRFC").val("1");
				$("#tipoPB").val(2);
    			$("#cTipoRegistro").val("BENEFICIARIO");
    			$("#trdescProv").hide();
			 	$("#trdescBen").show();
    			$("#cRazonSocial").val('');	
    			$("#lbl_provExtra").hide();
				$("#chk_extranjero").hide();
				$("#labelCalle").hide();
				$("#labelNumero").hide();
				$("#labelColonia").hide();
				$("#labelCP").hide();
				$("#labelCorreo").hide();
				$("#labelTel").hide();
				$("#cGiro").val("EMPLEADO CONAFOR");
				$("#trGiro").hide();
				$("#trNoEmpleado").show();			
			}else{
				$("#tipoPB").show();
				$("#trGiro").show();
				$("#trPyme").show();
				$("#nIdPyme").val(1);
				$("#tipoPB").val(1);
				$("#labelCalle").show();
				$("#labelNumero").show();
				$("#labelColonia").show();
				$("#labelCP").show();
				$("#labelCorreo").show();
				$("#labelTel").show();
				//$("#cIdTipoRFC").val(1);
			}
		} else {//MORAL
			$("#cIdRFC1").val($("#cIdRFC1").val().substring(0, 3));
			document.getElementById('cIdRFC1').maxLength = 3;
			$("#trCurp").hide();
			$("#pMoralRepresentante").show();
			$("#tipoPB").show();
			$("#trGiro").show();
			$("#trPyme").show();
			$("#nIdPyme").val(1);
			//$("#cIdTipoRFC").val(1);
			$("#pMoralRazon").show();
			if(!actualizacion){
				$("#cIdRFC2").val('');
				$("#cIdRFC3").val('');
			}
			$("#cCURP").val('');
			$("#labelNumero").show();
			$("#labelColonia").show();
			$("#labelCP").show();
			$("#labelCorreo").show();
			$("#labelTel").show();
		}
		
		

	}

	function existeProveedor() {//valida en mCatalogoProveedor
		cargado=true;
		if(idOper==6){ 
			return;
		}
		
		parent.document.getElementById("pb_save").disabled = false;
		if($("#cActualizacion").val()!=1){
		
			var rfc=$("#cIdRFC").val($("#cIdRFC1").val() + "-" + $("#cIdRFC2").val() + "-"+ $("#cIdRFC3").val());
			$("#cIdRFCB").val($("#cIdRFC1").val() +$("#cIdRFC2").val()+$("#cIdRFC3").val());
							
			queryFormPost("checarfc", {async : false});	
			queryFormPost("checaAltaProvedorRFC", {async : false});
			if($("#existeFolio").val()!="" && $("#existeFolio").val()!=$("#cFolio").val()){
				parent.document.getElementById("pb_save").disabled = true;
				alert("Este RFC ya esta Capturado en el Documento: "+$("#existeFolio").val());
				$("#cIdRFC1").addClass( "ui-state-error" );
				$("#cIdRFC2").addClass( "ui-state-error" );
				$("#cIdRFC3").addClass( "ui-state-error" );
				$("#existeFolio").val("");
			}else if ($("#regreso").val() == 'EXISTE') {//existe m
				$("#regreso").val(" ");	
				if(confirm("Este RFC ya existe, desea Actualizarlo")){
					cargaProveedor();
					tipoPersonaChange();
				}else{
					$("#cIdRFC1").addClass( "ui-state-error" );
					$("#cIdRFC2").addClass( "ui-state-error" );
					$("#cIdRFC3").addClass( "ui-state-error" );
				}
				return;
			}else {
				queryFormPost("checarfcBeneficiarios", {async : false});
				queryFormPost("checarfcBeneficiariosSinH", {async : false});
				if ($("#regreso").val() == 'EXISTE') {
					$("#regreso").val(" ");
					if(confirm("Este RFC ya existe, desea Actualizarlo")){
						cargaBeneficiario();
						tipoPersonaChange();
					}else{
						$("#cIdRFC1").addClass( "ui-state-error" );
						$("#cIdRFC2").addClass( "ui-state-error" );
						$("#cIdRFC3").addClass( "ui-state-error" );
					}
					return;
				}
				else{			
							
					$("#cIdRFC1").removeClass( "ui-state-error" );
					$("#cIdRFC2").removeClass( "ui-state-error" );
					$("#cIdRFC3").removeClass( "ui-state-error" );
				}
		}
	}
	}
	
	function cargaBeneficiario(){
		document.getElementById("lbl_Editar").style.visibility = "visible";
		document.getElementById("chk_edTipoPersona").style.visibility = "visible";
		document.getElementById("cIdTipoPersonaRFC").disabled = true;
		
	
		document.getElementById("cIdRFC1").readOnly = true;
		document.getElementById("cIdRFC1").className = "notEditable";
		document.getElementById("cIdRFC2").readOnly = true;
		document.getElementById("cIdRFC2").className = "notEditable";
		document.getElementById("cIdRFC3").readOnly = true;
		document.getElementById("cIdRFC3").className = "notEditable";
		$("#cActualizacion").val("1");
		actualizacion=true;
		//document.getElementById("cRazonSocial").readOnly = true;
		//document.getElementById("cRazonSocial").className = "notEditable";
		//document.getElementById("cCURP").readOnly = true;
		//document.getElementById("cCURP").className = "notEditable";
		
		
		document.getElementById("tipoPB").disabled = true;
		queryFormPost({
  			queryName: "tBeneficiarioTipoPersonaRead",
  			async: false,
    		callback: function() {
         	}
   		});

   		if($("#tipoPersona").val()==1){
    				queryFormPost({
  						queryName: "tBeneficiarioMoralRead",
  						async: false,
    					callback: function() {
    						actualizaMunicipio();
    						queryFormPost("tBeneficiarioMunicipioRead", {async : false});
    						$("#cMunicipioFiscal").val($("#cIdMunicipio").val());
    						$("#cIdTipoPersonaRFC").val(1);
    						$("#tipoPB").val(2);
    						$("#cTipoRegistro").val("BENEFICIARIO");
    					}
    				});
    			}else if($("#tipoPersona").val()==2){
    				queryFormPost({
  						queryName: "tBeneficiarioFisicaRead",
  						async: false,
    					callback: function() {
    						actualizaMunicipio();
    						queryFormPost("tBeneficiarioMunicipioRead", {async : false});
    						$("#cMunicipioFiscal").val($("#cIdMunicipio").val());
    						$("#cIdTipoPersonaRFC").val(2);
    						$("#tipoPB").val(2);
    						$("#cTipoRegistro").val("BENEFICIARIO");
    					}
    				});
    			}else if($("#tipoPersona").val()==3){
    				queryFormPost({
  						queryName: "tBeneficiarioFisicaRead",
  						async: false,
    					callback: function() {
    						actualizaMunicipio();
    						queryFormPost("tBeneficiarioMunicipioRead", {async : false});
    						$("#cMunicipioFiscal").val($("#cIdMunicipio").val());
    						$("#cIdTipoPersonaRFC").val(3);
    						$("#tipoPB").hide();
    						$("#cTipoRegistro").val("BENEFICIARIO");
    						//$("#trGiro").hide();
							$("#trPyme").hide();
							$("#nIdPyme").val("4");
							//$("#cIdTipoRFC").val("1");
							$("#tipoPB").val(2);
    						$("#cTipoRegistro").val("BENEFICIARIO");
    					}
    				});
    				buscaEmpleado();
    			}
    	revisaCamposVacios();
    	revisaComas();
	}
	
	function consultaBeneficiario(){
		$("#cIdRFCsinH").val( $("#cIdRFC1").val() + $("#cIdRFC2").val());
			
		queryFormPost({
  			queryName: "tBeneficiarioTipoPersonaReadsinH",
  			async: false,
    		callback: function() {
         	}
   		});

   		if($("#tipoPersona").val()==1){
    				queryFormPost({
  						queryName: "tBeneficiarioMoralReadsinH",
  						async: false,
    					callback: function() {
    						actualizaMunicipio();
    						queryFormPost("tBeneficiarioMunicipioReadsinH", {async : false});
    						$("#cMunicipioFiscal").val($("#cIdMunicipio").val());
    						$("#cIdTipoPersonaRFC").val(1);
    						$("#tipoPB").val(2);
    						$("#cTipoRegistro").val("BENEFICIARIO");
    					}
    				});
    			}else if($("#tipoPersona").val()==2){
    				queryFormPost({
  						queryName: "tBeneficiarioFisicaReadsinH",
  						async: false,
    					callback: function() {
    						actualizaMunicipio();
    						queryFormPost("tBeneficiarioMunicipioReadsinH", {async : false});
    						$("#cMunicipioFiscal").val($("#cIdMunicipio").val());
    						$("#cIdTipoPersonaRFC").val(2);
    						$("#tipoPB").val(2);
    						$("#cTipoRegistro").val("BENEFICIARIO");
    					}
    				});
    			}else if($("#tipoPersona").val()==3){
    				queryFormPost({
  						queryName: "tBeneficiarioFisicaReadsinH",
  						async: false,
    					callback: function() {
    						actualizaMunicipio();
    						queryFormPost("tBeneficiarioMunicipioReadsinH", {async : false});
    						$("#cMunicipioFiscal").val($("#cIdMunicipio").val());
    						$("#cIdTipoPersonaRFC").val(3);
    						$("#tipoPB").hide();
    						$("#cTipoRegistro").val("BENEFICIARIO");
    						//$("#trGiro").hide();
							$("#trPyme").hide();
							$("#nIdPyme").val("4");
							//$("#cIdTipoRFC").val("1");
							$("#tipoPB").val(2);
    						$("#cTipoRegistro").val("BENEFICIARIO");
    					}
    				});
    				//buscaEmpleado();
    			}
    
	}
	
	function cargaProveedor(){
		document.getElementById("lbl_Editar").style.visibility = "visible";
		document.getElementById("chk_edTipoPersona").style.visibility = "visible";
		document.getElementById("cIdTipoPersonaRFC").disabled = true;
		document.getElementById("cIdRFC1").readOnly = true;
		document.getElementById("cIdRFC1").className = "notEditable";
		document.getElementById("cIdRFC2").readOnly = true;
		document.getElementById("cIdRFC2").className = "notEditable";
		document.getElementById("cIdRFC3").readOnly = true;
		document.getElementById("cIdRFC3").className = "notEditable";
		$("#cActualizacion").val("1");
		actualizacion=true;
		//document.getElementById("cRazonSocial").readOnly = true;
		//document.getElementById("cRazonSocial").className = "notEditable";
		//document.getElementById("cCURP").readOnly = true;
		//document.getElementById("cCURP").className = "notEditable";
		document.getElementById("tipoPB").disabled = true;
		queryFormPost("mCatalogoProveedorCURPRead", {async : false});
		queryFormPost({
  			queryName: "mCatalogoProveedorRead",
  			async: false,
    		callback: function() {

         	}
   		});
		cIdEntidadFederativa=$("#cEstadoFiscal").val();
		//$("#cEstadoFiscal").val(parseInt(cIdEntidadFederativa,10));
		if($("#cEstadoFiscal").val().length==1){
			$("#cEstadoFiscal").val("0"+$("#cEstadoFiscal").val());
		}
		queryFormPost("idMunicipioRead", {async : false});
		actualizaMunicipio();
		$("#cMunicipioFiscal").val($("#cIdMunicipio").val());
		$("#tipoPB").val(1);
		$("#cTipoRegistro").val("PROVEEDOR");
		queryFormPost("tipoPersona", {async : false});
		if($("#cIdTipoPersonaRFC").val()==1){
			//se pidio cambio ya que si tiene 2 nombres cargaba mal.
			/*var nombreCompleto=$("#cRepresentanteLegal").val();
			var res = nombreCompleto.split(" ");
			$("#cNombre").val(res[0]);
			$("#cApellidoMaterno").val(res[1]);
			$("#cApellidoPaterno").val(res[2]);*/
			// ahora se carga todo en el nombre y el usuario se encarga de dividir.
				$("#cNombre").val($("#cRepresentanteLegal").val());
				$("#cApellidoMaterno").val("");
				$("#cApellidoPaterno").val("");
		}else{
			//se pidio cambio ya que si tiene 2 nombres cargaba mal.
			/*var nombreCompleto=$("#cRazonSocial").val();
			var res = nombreCompleto.split(" ");
			$("#cNombre").val(res[0]);
			$("#cApellidoMaterno").val(res[1]);
			$("#cApellidoPaterno").val(res[2]);*/
			// ahora se carga todo en el nombre y el usuario se encarga de dividir.
			$("#cNombre").val($("#cRazonSocial").val());
			$("#cApellidoMaterno").val("");
			$("#cApellidoPaterno").val("");
			
			$("#cCURP").val($("#CURP").val());
			$("#cRazonSocial").val("");
			
		}
		if($("#cIdTipoPersonaRFC").val()==3){
			document.getElementById("lbl_Editar").style.visibility = "visible";
			document.getElementById("chk_edTipoPersona").style.visibility = "visible";
		}
		revisaCamposVacios();
		revisaComas();
	}
	
	function revisaCamposVacios() {//Evitar campos con espacios
		if($("#cGiro").val().indexOf(' ') == 0)
			$("#cGiro").val("");
		
		if($("#cCalle").val().indexOf(' ') == 0)
			$("#cCalle").val("");
			
		if($("#cNumeroExterno").val().indexOf(' ') == 0)
			$("#cNumeroExterno").val("");
			
		if($("#cNumeroInterno").val().indexOf(' ') == 0)
			$("#cNumeroInterno").val("");
			
		if($("#cColonia").val().indexOf(' ') == 0)
			$("#cColonia").val("");
			
		if($("#cCodigoPostal").val().indexOf(' ') == 0)
			$("#cCodigoPostal").val("");
			
		if($("#cEmail").val().indexOf(' ') == 0)
			$("#cEmail").val("");
			
		if($("#cTelefono").val().indexOf(' ') == 0)
			$("#cTelefono").val("");
	}
	
	function revisaComas() {//Quitar comas de campos
		cRazonSocial=$("#cRazonSocial").val().replace(/,/g, " ");
		$("#cRazonSocial").val(cRazonSocial);
		cGiro=$("#cGiro").val().replace(/,/g, " ");
		$("#cGiro").val(cGiro);
		cCalle=$("#cCalle").val().replace(/,/g, " ");
		$("#cCalle").val(cCalle);
		cNumeroExterno=$("#cNumeroExterno").val().replace(/,/g, " ");
		$("#cNumeroExterno").val(cNumeroExterno);
		cNumeroInterno=$("#cNumeroInterno").val().replace(/,/g, " ");
		$("#cNumeroInterno").val(cNumeroInterno);
		cColonia=$("#cColonia").val().replace(/,/g, " ");
		$("#cColonia").val(cColonia);
		cCodigoPostal=$("#cCodigoPostal").val().replace(/,/g, " ");
		$("#cCodigoPostal").val(cCodigoPostal);
		cEmail=$("#cEmail").val().replace(/,/g, " ");
		$("#cEmail").val(cEmail);
		cTelefono=$("#cTelefono").val().replace(/,/g, " ");
		$("#cTelefono").val(cTelefono);
	}
	
	function ChangeCase(elem) {//Cambia a Mayusculas
		elem.value = elem.value.toUpperCase();
	}

	function Change(elem,evt) {//Pasa al siguiente campo al escribir
		var rfc = elem.name;
		if (elem.value.length == elem.maxLength) {
			if (rfc == "cIdRFC1") {
				if(onlyNumbers(evt))
				$("#cIdRFC2").select();
			} else
				$("#cIdRFC3").select();
		}
	}

	function onlyNumbers(evt) {
		var keyPressed = (evt.which) ? evt.which : event.keyCode;
		var strCheck = '0123456789';
		var key = String.fromCharCode(keyPressed);
		if (strCheck.indexOf(key) == -1)
			return false; 
		return true;
	}

	function actualizaMunicipio() {
		querySelectPost("MunicipiosRead", "cMunicipioFiscal", {async : false});
	}

	function validarEmail() {
		var email = $("#cEmail").val();
		expr = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
		if (!expr.test(email)){
			alert("Error: La dirección de correo " + email + " es incorrecta.");
				return false;
			}else
				return true;
	}
	
	function ids() {//Pasa Valores de ID's para su Actualizacion
		$("#cIdTipoPersona").val($("#cIdTipoPersonaRFC").val());
		$("#cIdEntidadFederativa").val($("#cEstadoFiscal").val());
		$("#cIdMunicipio").val($("#cMunicipioFiscal").val());
		$("#nIdTipoTelefono").val($("#cTipoTelefono").val());
		$("#idDoc").val($("#cIdTipoDoc").val());
	}
	
	function seleccionados() { //Se guardan los valores Seleccionados
		$("#cIdTipoPersonaRFC").val($("#cIdTipoPersona").val());
		$("#cEstadoFiscal").val($("#cIdEntidadFederativa").val());
		$("#cMunicipioFiscal").val($("#cIdMunicipio").val());
		$("#cTipoTelefono").val($("#nIdTipoTelefono").val());
	}
	
	function actualizaVariables(){//obtener Valores para Variables Globales
		cFolio = $("#cFolio").val();
		cIdTipoPersona = $("#cIdTipoPersona").val();
		cIdRFC = $("#cIdRFC").val();
		cRazonSocial = $("#cRazonSocial").val();
		cCURP = $("#cCURP").val();
		cApellidoPaterno = $("#cApellidoPaterno").val();
		cApellidoMaterno = $("#cApellidoMaterno").val();
		cNombre = $("#cNombre").val();
		cGiro = $("#cGiro").val();
		cIdEntidadFederativa = $("#cIdEntidadFederativa").val();
		cIdMunicipio = $("#cIdMunicipio").val();
		cCalle = $("#cCalle").val();
		cPais = $("#cPais").val();
		cExtranjero = $("#cExtranjero").val();
		cNumeroExterno = $("#cNumeroExterno").val();
		cNumeroInterno = $("#cNumeroInterno").val();
		cColonia = $("#cColonia").val();
		cCodigoPostal = $("#cCodigoPostal").val();
		cEmail = $("#cEmail").val();
		cUrl = $("#cUrl").val();
		nIdPyme = $("#nIdPyme").val();
		nIdTipoTelefono = $("#nIdTipoTelefono").val();
		cTelefono = $("#cTelefono").val();
		cIdRFC1 = $("#cIdRFC1").val();
		cIdRFC2 = $("#cIdRFC2").val();
		cIdRFC3 = $("#cIdRFC3").val();
	}
	
	function remplazavariables(){//Variables Globales para Validaciones 
		cFolio = $("#cFolio"),
		cIdTipoPersona = $("#cIdTipoPersona"),
		cIdRFC = $("#cIdRFC"),
		cRazonSocial = $("#cRazonSocial"),
		cCURP = $("#cCURP"),
		cApellidoPaterno = $("#cApellidoPaterno"),
		cApellidoMaterno = $("#cApellidoMaterno"),
		cNombre = $("#cNombre"),
		cGiro = $("#cGiro"),
		cIdEntidadFederativa = $("#cIdEntidadFederativa"),
		cIdMunicipio = $("#cIdMunicipio"),
		cCalle = $("#cCalle"),
		cPais = $("#cPais"),
		cExtranjero = $("#cExtranjero"),
		cNumeroExterno = $("#cNumeroExterno"),
		cNumeroInterno = $("#cNumeroInterno"),
		cColonia = $("#cColonia"),
		cCodigoPostal = $("#cCodigoPostal"),
		cEmail = $("#cEmail"),
		cUrl = $("#cUrl"),
		//nIdPyme = $("#nIdPyme"),
		nIdTipoTelefono = $("#nIdTipoTelefono"),
		cTelefono = $("#cTelefono"),
		cIdRFC1 = $("#cIdRFC1"),
		cIdRFC2 = $("#cIdRFC2"),
		cIdRFC3 = $("#cIdRFC3");
	}
	
	function actualizaValores(){//Se actualizan los valores apartir de los Valores Globales
		$("#cIdEntidadFederativa").val(parseInt(cIdEntidadFederativa,10));
		$("#cEstadoFiscal").val(parseInt(cIdEntidadFederativa,10));
		actualizaMunicipio();	
		$("#cFolio").val(cFolio);	
		$("#cIdTipoPersona").val(cIdTipoPersona);
		$("#cIdTipoPersonaRFC").val(cIdTipoPersona);		
		$("#cRazonSocial").val(cRazonSocial);	
		$("#cCURP").val(cCURP);	
		$("#cApellidoPaterno").val(cApellidoPaterno);	
		$("#cApellidoMaterno").val(cApellidoMaterno);	
		$("#cNombre").val(cNombre);	
		$("#cGiro").val(cGiro);		
		$("#cCalle").val(cCalle);
		$("#cPais").val(cPais);	
		$("#cExtranjero").val(cExtranjero);
		$("#cNumeroExterno").val(cNumeroExterno);	
		$("#cNumeroInterno").val(cNumeroInterno);	
		$("#cColonia").val(cColonia);	
		$("#cCodigoPostal").val(cCodigoPostal);	
		$("#cEmail").val(cEmail);	
		$("#cUrl").val(cUrl);	
		$("#nIdPyme").val(nIdPyme);	
		$("#nIdTipoTelefono").val(nIdTipoTelefono);	
		$("#cTipoTelefono").val(nIdTipoTelefono);
		$("#cTelefono").val(cTelefono);
		$("#cIdMunicipio").val(parseInt(cIdMunicipio,10));
		$("#cMunicipioFiscal").val(parseInt(cIdMunicipio,10));
		if(cIdTipoPersona==2||cIdTipoPersona==3){
			$("#cIdRFC1").val(cIdRFC.substr(0, 4));
			$("#cIdRFC2").val(cIdRFC.substr(5, 6));
			$("#cIdRFC3").val(cIdRFC.substr(12,3));
		}else{
			$("#cIdRFC1").val(cIdRFC.substr(0, 3));
			$("#cIdRFC2").val(cIdRFC.substr(4, 6));
			$("#cIdRFC3").val(cIdRFC.substr(11,3));
		}
	}
	function actMunicipio(){
		$("#cIdMunicipio").val(parseInt(cIdMunicipio,10));
		$("#cMunicipioFiscal").val(parseInt(cIdMunicipio,10));
		}
	function validacionDatos(){
		var esValido = true;
		//Validar Primera parte del RFC y CURP/RazonSocial
		esValido=esValido && esRequerido(cIdRFC1,"RFC");
		var tipo = $("#cIdTipoPersonaRFC").val();
					if (tipo == 2 || tipo == 3){//Fisica/ Empleado
						esValido=esValido && checkLength(cIdRFC1,"RFC",4,4);
						esValido=esValido && esRequerido(cCURP,"cCURP");
						esValido=esValido && checkLength(cCURP,"CURP",18,18);
					}else{
						esValido=esValido && checkLength(cIdRFC1,"RFC",3,3);
						esValido=esValido && esRequerido(cRazonSocial,"Razon Social");
						esValido=esValido && checkLength(cRazonSocial,"Razon Social",1,300);
					}
		
				//Validar Segunda parte del RFC
					esValido=esValido && esRequerido(cIdRFC2,"RFC");
					esValido=esValido && checkLength(cIdRFC2,"RFC",6,6);
				//Validar Tercera parte del RFC
					esValido=esValido && esRequerido(cIdRFC3,"RFC");
					esValido=esValido && checkLength(cIdRFC3,"RFC",3,3);
				//Validar Nombre y Apellidos
					if($("#cExtranjero").val()=="0"){
						esValido=esValido && esRequerido(cApellidoPaterno,"Apellido Paterno");
						esValido=esValido && checkLength(cApellidoPaterno,"Apellido Paterno",1,60);
						esValido=esValido && esRequerido(cApellidoMaterno,"Apellido Materno");
						esValido=esValido && checkLength(cApellidoMaterno,"Apellido Materno",1,60);
						esValido=esValido && esRequerido(cNombre,"Nombre");
						esValido=esValido && checkLength(cNombre,"Nombre",1,60);
				//Validar Domicilio y telefono
						if($("#cIdTipoPersonaRFC").val()!=3){//excepto empleados
							esValido=esValido && esRequerido(cCalle,"Calle");
							esValido=esValido && checkLength(cCalle,"Calle",1,100);
							esValido=esValido && esRequerido(cNumeroExterno,"Numero Externo");
							esValido=esValido && checkLength(cNumeroExterno,"Numero Externo",1,50);
							esValido=esValido && esRequerido(cColonia,"Colonia");
							esValido=esValido && checkLength(cColonia,"Colonia",1,60);
							esValido=esValido && esRequerido(cCodigoPostal,"Codigo Postal");
							esValido=esValido && checkLength(cCodigoPostal,"Codigo Postal",1,5);
							esValido=esValido && esRequerido(cGiro,"Giro");
							esValido=esValido && checkLength(cGiro,"Giro",1,500);
						//Se solicita como obligatorio para subir a SICOP
							esValido=esValido && esRequerido(cTelefono,"Telefono o Correo Electronico");
							esValido=esValido && checkLength(cTelefono,"Telefono con Lada",10,25);
						}
					}else{
						esValido=esValido && esRequerido(cPais,"Pais");
						esValido=esValido && checkLength(cPais,"Pais",1,100);
					}
					if($("#cEmail").val()!="")
						esValido=esValido &&validarEmail();
						
					return esValido
}
	function esRequerido( o, n) {//Funcion para validar si un campo es requerido
				var sTemp = $.trim(o.val());
				o.val(sTemp);
				if ( sTemp.length == 0  ) {
					o.addClass( "ui-state-error" );
					updateTipsDlg(  n + " es un dato requerido." );
					parent.document.getElementById("pb_send").disabled = true;
					o[0].readOnly=false;
					parent.document.getElementById("pb_save").disabled = false;
					o.focus();
					return false;
				} else {
					o.removeClass( "ui-state-error" );
					//o.focus();
					return true;
				}
	}
	
	function checkLength( o, n, min, max ) {//Funcion para Validar la longitud de un campo (max-Min)
				if ( o.val().length > max || o.val().length < min ) {
					o.addClass( "ui-state-error" );
					if (min == max){
						updateTipsDlg( "La longitud de " + n + " debe ser de " + min + " caracteres." );
						parent.document.getElementById("pb_send").disabled = true;
					}else{
						updateTipsDlg( "La longitud de " + n + " debe estar entre " + min + " y " + max + "." );
						parent.document.getElementById("pb_send").disabled = true;
						}
					o.focus();
					return false;
				} else {
					return true;
				}
	}
	
	function updateTipsDlg( t ) {//Manda mensaje de Error
				tips
					.text( t );
					alert(t);
			}
	
	function checkRegexp( o, regexp, n ) {//Validar expresiones regulares
				if ( !( regexp.test( o.val() ) ) ) {
					o.addClass( "ui-state-error" );
					updateTipsDlg( n );
					return false;
				} else {
					return true;
				}
			}
			
	function habilitaObservaciones(opc){// en caso de no Autorizar Habilita para escribir observaciones
		if(opc==1){
			$("#tdObservacion").hide();
			$("#lObservacion").hide();
			document.getElementById("autorizaNo").checked = false;
			document.getElementById("chk_CorreccionDatos").checked = false;
			document.getElementById("lbl_CorreccionDatos").style.visibility = "hidden";
			document.getElementById("chk_CorreccionDatos").style.visibility = "hidden";
		}else if(opc==2){
			$("#tdObservacion").show();
			$("#lObservacion").show();
			if(idOper==4){
				document.getElementById("lbl_CorreccionDatos").style.visibility = "visible";
				document.getElementById("chk_CorreccionDatos").style.visibility = "visible";
			}
			document.getElementById("autorizaSi").checked = false;
		}	
	}
	
	function inhabilitaProveedor(opc){// en caso de no Autorizar Habilita para escribir observaciones
		if(opc==1){
			document.getElementById("habilitaNo").checked = false;
			$("#lHabilitado").val("1");
		}else if(opc==2){
			document.getElementById("habilitaSi").checked = false;
			$("#lHabilitado").val("0");
		}	
	}
	
	function soloLectura(){
		document.getElementById("cIdTipoPersonaRFC").disabled = true;
		document.getElementById("tipoPB").disabled = true;
		document.getElementById("cRazonSocial").readOnly = true;
		document.getElementById("cRazonSocial").className = "notEditable";
		document.getElementById("cCURP").readOnly = true;
		document.getElementById("cCURP").className = "notEditable";
		document.getElementById("cApellidoPaterno").readOnly = true;
		document.getElementById("cApellidoPaterno").className = "notEditable";
		document.getElementById("cApellidoMaterno").readOnly = true;
		document.getElementById("cApellidoMaterno").className = "notEditable";
		document.getElementById("cNombre").readOnly = true;
		document.getElementById("cNombre").className = "notEditable";
		document.getElementById("cGiro").readOnly = true;
		document.getElementById("cGiro").className = "notEditable";
		document.getElementById("cEstadoFiscal").disabled = true;
		document.getElementById("cMunicipioFiscal").disabled = true;
		document.getElementById("cCalle").readOnly = true;
		document.getElementById("cCalle").className = "notEditable";
		document.getElementById("cPais").readOnly = true;
		document.getElementById("cPais").className = "notEditable";
		document.getElementById("cNumeroExterno").readOnly = true;
		document.getElementById("cNumeroExterno").className = "notEditable";
		document.getElementById("cNumeroInterno").readOnly = true;
		document.getElementById("cNumeroInterno").className = "notEditable";
		document.getElementById("cColonia").readOnly = true;
		document.getElementById("cColonia").className = "notEditable";
		document.getElementById("cCodigoPostal").readOnly = true;
		document.getElementById("cCodigoPostal").className = "notEditable";
		document.getElementById("cEmail").readOnly = true;
		document.getElementById("cEmail").className = "notEditable";
		document.getElementById("cUrl").readOnly = true;
		document.getElementById("cUrl").className = "notEditable";
		document.getElementById("nIdPyme").disabled = true;
		document.getElementById("cTipoTelefono").disabled = true;
		document.getElementById("cTelefono").readOnly = true;
		document.getElementById("cTelefono").className = "notEditable";
		document.getElementById("cIdRFC1").readOnly = true;
		document.getElementById("cIdRFC1").className = "notEditable";
		document.getElementById("cIdRFC2").readOnly = true;
		document.getElementById("cIdRFC2").className = "notEditable";
		document.getElementById("cIdRFC3").readOnly = true;
		document.getElementById("cIdRFC3").className = "notEditable";		
		document.getElementById("chk_extranjero").disabled = true;
		document.getElementById("CBEN").readOnly = true;
		document.getElementById("CBEN").className = "notEditable";
	}
	function tipoPBChange(){
		var tipo = $("#tipoPB").val();
		if(tipo==1){//Proveedor
			 $("#cTipoRegistro").val("PROVEEDOR");
			 $("#trdescProv").show();
			 $("#trdescBen").hide();
		}else{//Beneficiario
			 $("#cTipoRegistro").val("BENEFICIARIO");
			 $("#trdescBen").show();
			 $("#trdescProv").hide();
		}
	}
	function esActualizacion(){
		if($("#cActualizacion").val()!=""){
			document.getElementById("cIdTipoPersonaRFC").disabled = true;
			document.getElementById("tipoPB").disabled = true;
			document.getElementById("cIdRFC1").readOnly = true;
			document.getElementById("cIdRFC1").className = "notEditable";
			document.getElementById("cIdRFC2").readOnly = true;
			document.getElementById("cIdRFC2").className = "notEditable";
			document.getElementById("cIdRFC3").readOnly = true;
			document.getElementById("cIdRFC3").className = "notEditable";
			document.getElementById("chk_extranjero").disabled = true;
			//Se solicito que tanto la razon social como el CURP se puedan modificar
			/*document.getElementById("cRazonSocial").readOnly = true;
			document.getElementById("cRazonSocial").className = "notEditable";
			document.getElementById("cCURP").readOnly = true;
			document.getElementById("cCURP").className = "notEditable";*/
			return true;
		}else
			return false;
	}
	function OpenDialogObservaciones(){
		if($("#cObservaciones").val()!="")
			alert ($("#cObservaciones").val());
		else
			alert ("No Hay Observaciones");	
	}
	//Cuentas Bancarias

	// funciones del dialogo de Cuentas Bancarias
		
		$(function() {

			$( "#dialog:ui-dialog" ).dialog( "destroy" );

			var IdRFC1 = $("#cIdRFC1"),
				IdRFC2 = $("#cIdRFC2"),
				IdRFC3 = $("#cIdRFC3"),
				ApellidoPaterno = $("#cApellidoPaterno"),
				ApellidoMaterno = $("#cApellidoMaterno"),

								
				Plaza =  $("#txtPlaza"),
				CuentaBancaria =  $("#txtCuentaBancaria"),
				Sucursal =  $("#txtSucursal"),
				EntidadSiaff =  $("#txtEntidadSiaff");					
				


			$( "#dialog-form" ).dialog({
				autoOpen: false,
				height: 250,
				width: 800,
				modal: true,
				buttons: {
					"Aceptar": function() {

						if($("#archivoZip").val()==""){
							alert ("Debe cargar el Comprobante Bancario");
							return false;
						}
						var bValid = true;
// 						tips.text("");
						
	
						bValid = bValid && esRequerido( Plaza, "Plaza" );
						bValid = bValid && checkLength( Plaza, "Plaza", 3, 3 );
						bValid = bValid && checkRegexp( Plaza, /^([0-9])+$/, "Plaza solo permite números : 0-9" );
						
						bValid = bValid && esRequerido( CuentaBancaria, "No.Cuenta" );
						bValid = bValid && checkLength( CuentaBancaria, "No.Cuenta", 11, 11 );
						bValid = bValid && checkRegexp( CuentaBancaria, /^([0-9])+$/, "No.Cuenta solo permite números : 0-9" );
						
						bValid = bValid && esRequerido( Sucursal, "Sucursal" );
						bValid = bValid && checkLength( Sucursal, "Sucursal", 1, 4 );
						bValid = bValid && checkRegexp( Sucursal, /^([0-9])+$/, "Sucursal solo permite números : 0-9" );
						
						
						if (!bValid) return;
						
						var szTemp = CalculaCLABE();		
									
						$("#txtCLABE").val(szTemp);
						$("#txtDigitoVerificador").val(szTemp.substr(17,1));

                		var docsiguiente = parseInt($('#docSiguiente').val(),10);
                		$('#docSiguiente').val(docsiguiente);
                		$('#cNombreCuenta').val($('#cbBanco option:selected').text() + '-'+$('#txtCuentaBancaria').val());
                		$('#cUsuarioModifico').val($.trim("<%=usuario.getLogin()%>"));
                		var dHoy = new Date();
                		$('#fCuentaModifico').val(dHoy.getFullYear() + "" + cerosIzq("" + (dHoy.getMonth() + 1), 2) + "" + cerosIzq("" + dHoy.getDate(), 2) + " " + cerosIzq("" + dHoy.getHours(), 2) + ":" + cerosIzq("" + dHoy.getMinutes(), 2));
                		$('#cBancoH2').val($('#cBancoH').val());
                		$('#cIdRFCB2').val($('#cIdRFCB').val());
                		$('#dBancoH2').val($('#dBancoH').val());
                		$('#cFolio2').val($('#cFolio').val());
                		$('#FormProveedor').submit();

                        $('#tblCuentasBancarias').dataTable().fnAddData([
                        	$('#cbBanco').val(),
                            $('#cbBanco option:selected').text(),
                            $('#txtPlaza').val() ,
                            $('#txtCuentaBancaria').val(),
                            $('#txtDigitoVerificador').val(),
                            $('#txtSucursal').val(),
                            "0",
                            "Inactivo",
                            "NO",
                            "NO",
                            " "
                          ]);
						$( this ).dialog( "close" );
					},
					Cancelar: function() {
						$( this ).dialog( "close" );
					}
				},
				close: function() {
					
// 					tips.text( "" );
				}
			});

			$( "#pbCuentasBancarias" )
				.button()
				.click(function() {
					
					if($("#idoper").val()==5)
						onSubmit(8);
						
					$("#txtNombreProveerdor").val( (IdRFC1.val() + "-"+ IdRFC2.val() + "-" + IdRFC3.val()).toUpperCase() + " / "+($.trim($("#cApellidoPaterno").val() + " " + $("#cApellidoMaterno").val() + " " + $("#cNombre").val())).toUpperCase());
					
					$("#cbBanco").removeAttr('disabled');
					$("#cbBanco").css("background","white");
					$("#txtPlaza").removeAttr("disabled");
					$("#txtPlaza").css("background", "white");					
					$("#txtCuentaBancaria").removeAttr("disabled");
					$("#txtCuentaBancaria").css("background", "white");
					$("#txtSucursal").removeAttr("disabled");
					$("#txtSucursal").css("background", "white");					
					$("#chkEntidad").removeAttr("disabled");
					$("#chkEntidad").css("background","white");
					
					$( "#dialog-form" ).dialog( "open" );				
				});
		});
		
		function CalculaCLABE(){
			$("#cBancoH").attr("disabled", false);
			$("#cPlazaH").attr("disabled", false);
			$("#dCuentaBancariaH").attr("disabled", false);
			$("#dDigitoVerificadorH").attr("disabled", false);
			$("#cStatusCuentaH").attr("disabled", false);
			$("#dBancoH").attr("disabled", false);
			$("#dSucursalH").attr("disabled", false);
			$("#nBCBEnviadoSICOPH").attr("disabled", false);
					
			$('#cBancoH').val($('#cbBanco').val());
			$('#cPlazaH').val($('#txtPlaza').val());
			$('#dCuentaBancariaH').val($('#txtCuentaBancaria').val());
			$('#dDigitoVerificadorH').val($('#txtDigitoVerificador').val());
			$('#dBancoH').val($('#cbBanco option:selected').text());
			$('#dSucursalH').val($('#txtSucursal').val());
						
			var  n=0;
			var iRes=0;
			var arrFactor=new Array(3,7,1,3,7,1,3,7,1,3,7,1,3,7,1,3,7); 
			var arrResult=new Array(-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1);
			var szCLABE = $("#cbBanco").val();
			
			$("#txtPlaza").val(cerosIzq($("#txtPlaza").val(),3 ));
			$("#txtCuentaBancaria").val(cerosIzq($("#txtCuentaBancaria").val(),11 ));
			szCLABE += $("#txtPlaza").val() + $("#txtCuentaBancaria").val();
			
			for (n=0 ;n<arrFactor.length ; n++)
			{
				arrResult[n] = (arrFactor[n] * parseInt(szCLABE.charAt(n),10)) % 10;
				iRes += arrResult[n];
			}
			iRes = iRes % 10;
			iRes = (10 - iRes) % 10;
			szCLABE = szCLABE + iRes;
			return szCLABE;
		}
		
		function esExtranjero(){
			if ($("#chk_extranjero").prop("checked")){
				$("#cExtranjero").val("1");
				$("#cPais").val(cPais);
				document.getElementById("Pais").style.display = "block";
				$("#cEstadoFiscal").val(33);
				actualizaMunicipio();
				document.getElementById("cEstadoFiscal").disabled = true;
				document.getElementById("cMunicipioFiscal").disabled = true;
				$("#nIdPyme").val("4");
				$("#trPyme").hide();
				$("#labelNombre").hide();
				$("#labelPaterno").hide();
				$("#labelMaterno").hide();
				$("#labelCalle").hide();
				$("#labelNumero").hide();
				$("#labelColonia").hide();
				$("#labelCP").hide();
				$("#labelCorreo").hide();
				$("#labelTel").hide();
			}else{
				$("#labelNombre").show();
				$("#labelPaterno").show();
				$("#labelMaterno").show();
				$("#labelCalle").show();
				$("#labelNumero").show();
				$("#labelColonia").show();
				$("#labelCP").show();
				$("#labelCorreo").show();
				$("#labelTel").show();
				$("#cExtranjero").val("0");
				$("#cPais").val("Mexico");
				document.getElementById("Pais").style.display = "none";
				$("#cEstadoFiscal").val(cIdEntidadFederativa);
				document.getElementById("cEstadoFiscal").disabled = false;
				document.getElementById("cMunicipioFiscal").disabled = false;
				actualizaMunicipio();
				$("#trPyme").show();
			}
		}
		function seEdita(){
			if ($("#chk_edTipoPersona").prop("checked")){
				document.getElementById("cIdTipoPersonaRFC").disabled = false;
				document.getElementById("tipoPB").disabled = false;
    			$("#tipoPB").show();
			}else{
				existeProveedor();
			}
		}
		function cambiaEstatusCuenta(){
			$("#cIdRFCB").val($("#cIdRFC1").val()+$("#cIdRFC2").val()+$("#cIdRFC3").val());
			$("#cStatusCuentaH").val($("#actInact").val());
			$("#cBancoH").val($("#iBanco").val());
			$("#dCuentaBancariaH").val($("#iCuenta").val());
			queryFormPost("StatusCuentaBancariaUpdate", {async : false});
			var x;
			if($("#actInact").val() == 1)
				x="Activo";
			else
				x="Inactivo";
			daTable.fnUpdate(x , aPos, 7);
			$("#desactivaCuenta").dialog( "close" );
		}
		function enviaConsulta() {
			document.ExportarForm.submit();
		}
		function liberaCaso() {
			//tAltaProveedorUpdateAplicado
			//LiberaCasoAltaProveedor
			$("#esperar").dialog("open");
			var liberado=false;
			queryFormPost({
				queryName: "tAltaProveedorUpdateAplicado",
				async: false,
				callback: function() {
					queryFormPost({
						queryName: "LiberaCasoAltaProveedor",
						async: false,
						callback: function() {
							parent.document.getElementById("pb_leave").disabled = false;
							parent.document.getElementById("pb_leave").click();
							liberado=true;
						}
					});
				}
			});
			if(!liberado)
				alert ("Hubo un problema al liberar. Favor de Reportar al administrador");
		}

		function insertaRelCasoRFC(){
			var exito = false;
			
			queryFormPost(
				{
					queryName:"relCasoRFCCreate",
					async:false,
					callback:function(){
						exito = true;
					}
				}
			);
			
			return exito;
		}
		
		function eliminaRegistro(cuenta,banco,plaza,digito,sucursal,status){
			if(confirm("Esta seguro de Eliminar el siguiente registro:\n Cuenta: "+ cuenta+"\n Banco: "+banco+"\n Plaza: "+plaza+"\n Digito: "+digito+"\n Sucursal: "+sucursal)){
			
				$("#cIdRFCB").val($("#cIdRFC1").val() +$("#cIdRFC2").val()+$("#cIdRFC3").val());  
				$("#dCuentaBancariaH").val(cuenta);
				$("#cBancoH").val(banco);
				$("#cPlazaH").val(plaza);
				$("#dDigitoVerificadorH").val(digito);
				$("#dSucursalH").val(sucursal);
								
				if(status=="Inactivo"){
					  queryFormPost(
						{
							queryName:"CtaBancariaTmpDeleteIcono",
							async:false,
							callback:function(){
								$('#tblCuentasBancarias').dataTable().fnClearTable();
								cargaDataTable();
							}
						}
					);
				}else {
					queryFormPost(
						{
							queryName:"CuentasBancariasEliminadasUpdate",
							async:false,
							callback:function(){
								$("#nombreDcto").val(banco+"-"+cuenta);
 								queryFormPost(
 									{
 										queryName:"CtaBancariaDELETEDocto",
 										async:false,
 										callback:function(){
											queryFormPost(
			 									{
			 										queryName:"CtaBancariaDeleteIcono",
			 										async:false,
			 										callback:function(){
														$('#tblCuentasBancarias').dataTable().fnClearTable();
														cargaDataTable();
														}
													}
												);
 										}
								}
 								);
							}
						}
					);
				}
			}
		}
		
		function cargaDataTable(){
			var rfc= $("#cIdRFC").val().replace("-","");
		    rfc=rfc.replace("-","");
		    szWhere = " dRFC = '" +rfc + "' ";
		    szTabla = "BENEFICIARIOCUENTASBANCARIASTMP";
		    $.getJSON("../catalogos/SelectJson.jsp", {
		            Tabla: szTabla,
		            Param: szWhere,
		            MaxReg: 20,
		            ajax: 'false'
		        },
		        function(j) {
		            var x,x2;
		            for (var i = 0; i < j.length; i++) {
		                if(j[i].Col5 == 1)
		                	x="Activo";
		                else 
		                	x="Inactivo";
		                if(j[i].Col10 == 1)
		                	x2="SI";
		                else 
		                	x2="NO";
		                $('#tblCuentasBancarias').dataTable().fnAddData([
		                		j[i].Col1 ,
		                    	j[i].Col6 ,
		                    	j[i].Col2 ,
		                    	j[i].Col3 ,
		                    	j[i].Col4 ,
		                    	j[i].Col7 ,
		                    	j[i].Col5 ,
		                    	x,
		                    	x2,
		                    	"NO",
		                    	"<a border=\"0\" href=\"#\" onclick=\"eliminaRegistro('" + j[i].Col3 +"','"+j[i].Col6+"','"+j[i].Col2+"','"+j[i].Col4+"','"+j[i].Col7+"','"+x+"');return false;\"><img border=\"0\" class=\"btnEliminar\" src=\"../imagenes/iconos/rechazar.png\" title=\"Eliminar Registro\"></img></a>" 	                
		                     ]);
		            }
		        });		   
		    szTabla = "BENEFICIARIOCUENTASBANCARIAS2";
		    $.getJSON("../catalogos/SelectJson.jsp", {
		            Tabla: szTabla,
		            Param: szWhere,
		            MaxReg: 20,
		            ajax: 'false'
		        },
		         function(j) {
		            var x,x2;
		            for (var i = 0; i < j.length; i++) {
		                if(j[i].Col5 == 1)
		                	x="Activo";
		                else 
		                	x="Inactivo";
		                if(j[i].Col10 == 1)
		                	x2="SI";
		                else 
		                	x2="NO";
		                $('#tblCuentasBancarias').dataTable().fnAddData([
		                		j[i].Col1 ,
		                    	j[i].Col6 ,
		                    	j[i].Col2 ,
		                    	j[i].Col3 ,
		                    	j[i].Col4 ,
		                    	j[i].Col7 ,
		                    	j[i].Col5 ,
		                    	x,
		                    	x2,
		                    	"SI",
		                    	"<a border=\"0\" href=\"#\" onclick=\"eliminaRegistro('" + j[i].Col3 +"','"+j[i].Col6+"','"+j[i].Col2+"','"+j[i].Col4+"','"+j[i].Col7+"','"+x+"');return false;\"><img border=\"0\" class=\"btnEliminar\" src=\"../imagenes/iconos/rechazar.png\" title=\"Eliminar Registro\"></img></a>" 	                
		                     ]);		
		            }
		        });
		} 
		function creaDialogoMotivos() { 
	      $("#Motivos").dialog("open");
	}
	function updatemotivos(){	
		if($("#motivoModifica").val()==""){
			alert("Favor de capturar los motivos de la modificacion");
			return;			
		}
	
		$("#cObservaciones").val( $("#motivoModifica").val() );
		queryFormPost("tAltaProveedorUpdateObservaciones", {async:false});
		$("#Motivos").dialog("close");
		parent.document.getElementById("pb_save").click();		
		
	}
	function buscaEmpleado(){
		if($("#cIdTipoPersonaRFC").val()==3){
			$("#regreso").val(0);
			queryFormPost("checaEmpleado", {async : false});
			if ($("#regreso").val() == 'EXISTE' ) {
				queryFormPost("extraeRFCEmpleado", {async : false});
				var rfcEmpleado = $("#rfcEmpleado").val();
				$("#cIdRFC1").val(rfcEmpleado.substring(0,4));
				$("#cIdRFC2").val(rfcEmpleado.substring(4,10));
				$("#cIdRFC3").val(rfcEmpleado.substring(10,13));
				$("#regreso").val(0);
				existeProveedor();	
			}else{
				queryFormPost("checaEmpleadoRFC", {async : false});
				if ($("#regreso").val() == 'EXISTE' ) {
					queryFormPost("extraeNumEmpleado", {async : false});
				}
			}
			document.getElementById("NEmp").readOnly = true;
			document.getElementById("NEmp").className = "notEditable";
		}else
			return;
		
	}
	function validaOrganismoPublico(){
		var roles="<%=roles%>";
		//alert(roles);
		if(parseInt($("#nIdPyme").val(),10)==6 &&!(roles.indexOf("ADMIN_RECMAT")>=0 || roles.indexOf("ANALISTA")>=0 || roles.indexOf("JEFES")>=0)){
			alert("Los organismos p\u00fablicos solo se pueden dar de alta en el area de adquisiciones de oficinas centrales.");
			parent.document.getElementById("pb_send").disabled = true;
			parent.document.getElementById("pb_save").disabled = true;
			$("#trLegenOrgPub").show();
		}else{
			parent.document.getElementById("pb_save").disabled = false;
			$("#trLegenOrgPub").hide();
		}
	}
	</script>
</head>

<body id="dt_example" bottomMargin="0" bgColor="red" leftMargin="0"
	topMargin="0">
	<form id="ExportarForm" name="ExportarForm"
		action="../reportes/FormatoAltaProveedor" method="POST"
		target="_blank">
		<input type="hidden" name="idCaso" id="idCaso" value="<%=idCaso%>" />
		<input type="hidden" name="regreso" id="regreso" value=" " /> 
		<input type="hidden" name="regresoSinH" id="regresoSinH" value=" " /> 
		<input type="hidden" name="tieneCuentasRegistradas" id="tieneCuentasRegistradas" value=" " /> 
		<input type="hidden" name="existe" id="existe" value=" " /> 
		<input type="hidden" name="cValor" id="cValor" value=" " /> 
		<input type="hidden" name="cNvoCBEN" id="cNvoCBEN" value=" " /> 
		<input type="hidden" name="rfc" id="rfc" value=" " /> 
		<input type="hidden" name="cBeneficiario" id="cBeneficiario" value=" " /> 
		<input type="hidden" name="cIdTipoPersona" id="cIdTipoPersona" value=" " />
		<input type="hidden" name="lHabilitado" id="lHabilitado" value="1" />
		<input type="hidden" name="nIdTipoTelefono" id="nIdTipoTelefono" value=" " /> 
		<input type="hidden" name="cIdMunicipio" id="cIdMunicipio" value="" /> 
		<input type="hidden" name="cIdMunicipioNombre" id="cIdMunicipioNombre" value="" /> 
		<input type="hidden" name="idDoc" id="documentacion" value="4" /> 
		<input type="hidden" name="cIdEntidadFederativa" id="cIdEntidadFederativa" value=" " /> 
		<input type="hidden" name="cIdUsuarioCaptura" id="cIdUsuarioCaptura" value="<%=usuario.getLogin()%>" /> 
		<input type="hidden" name="cIdUsuarioAutoriza" id="cIdUsuarioAutoriza" value=" " /> 
		<input type="hidden" name="cIdUsuarioValida" id="cIdUsuarioValida" value="<%=usuario.getLogin()%>" /> 
		<input type="hidden" name="cIdUsuaUltModif" id="cIdUsuaUltModif" value="<%=usuario.getLogin()%>" /> 
		<input type="hidden" name="OPERADOR" id="OPERADOR" value="<%=c.getCasoOperacion(0).getResponsable()%>" /> 
		<input type="hidden" name="FECHA_DOCUMENTO" id="FECHA_DOCUMENTO" value="<%=today%>" /> 
		<input type="hidden" name="cDocumentoHaplicado" id="cDocumentoHaplicado" value="" /> 
		<input type="hidden" name="cIdRFCB" id="cIdRFCB" value="" /> 
		<input type="hidden" name="cIdRFCsinH" id="cIdRFCsinH" value="" /> 
		<input name="cDocumento" type="hidden" id="cDocumento" value="<%=c.getTipoCaso().getGavetaAsociada()%>"> 
		<input type="hidden" name="cIdUnidadEjecutora" id="cIdUnidadEjecutora"	value="<%=usuario.getU_UR()%>" /> 
		<input type="hidden" name="cRepresentanteLegal" id="cRepresentanteLegal" value="" /> 
		<input type="hidden" name="cTipoGiro" id="cTipoGiro" value="" />
		<input type="hidden" name="nPyme" id="nPyme" value="" /> 
		<input type="hidden" name="CURP" id="CURP" value="" /> 
		<input type="hidden" name="cActualizacion" id="cActualizacion" value="" /> 
		<input type="hidden" name="tipoPersona" id="tipoPersona" value="" /> 
		<input type="hidden" name="cTipoRegistro" id="cTipoRegistro" value="" /> 
		<input type="hidden" name="existeFolio" id="existeFolio" value="" /> 
		<input type="hidden" name="ComprobanteBancario" id="ComprobanteBancario" value="" /> 
		<input type="hidden" name="cNombreCuenta" id="cNombreCuenta" value="" />
		<!-- Variables Para Cuentas Bancarias -->
		<input id="cUsuarioModifico" name="cUsuarioModifico" type="hidden" size="10"> 
		<input id="fCuentaModifico" name="fCuentaModifico" type="hidden" size="10"> 
		<input type="hidden"	name="ID_GABINETE" id="ID_GABINETE" value="" /> 
		<input type="hidden" name="nombreDcto" id="nombreDcto" value="" /> 
		<input type="hidden" name="docSiguiente" id="docSiguiente" value="" /> 
		<input type='hidden' id='cBancoH' name='cBancoH' value=''> 
		<input type='hidden' id='cPlazaH' name='cPlazaH' value=''> 
		<input type='hidden' id='dCuentaBancariaH' name='dCuentaBancariaH' value=''>
		<input type='hidden' id='dDigitoVerificadorH' name='dDigitoVerificadorH' value=''> 
		<input type='hidden' id='cStatusCuentaH' name='cStatusCuentaH' value='1'> 
		<input type='hidden' id='dBancoH' name='dBancoH' value=''> 
		<input type='hidden' id='dSucursalH' name='dSucursalH' value=''> 
		<input type='hidden' id='nBCBEnviadoSICOPH' name='nBCBEnviadoSICOPH' value='0'> 
		<input type='hidden' id='tiene' name='tiene' value='0'> 
		<input type='hidden' id='ncuentas' name='ncuentas' value='0'> 
		<input type='hidden' id='cExtranjero' name='cExtranjero'> 
		<input type='hidden' id='idoper' name='idoper' value='0'> 
		<input type='hidden' id='rfcEmpleado' name='rfcEmpleado' value=''>
		<div id="container" class="container">
			<h1>Alta de Proveedores</h1>
			<fieldset>
				Folio SAI: <input title="Número de Folio, se asigna automáticamente"
					type="text" readonly="readonly" id="cFolio" name="cFolio"
					value="<%=c.getFolio()%>" class="notEditable"> 
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
					<span id="btnObservaciones">
						<a href="#" onclick="OpenDialogObservaciones();">MOSTRAR OBSERVACIONES</a> 
					</span> 
					<input id="botonExtrae" name="botonExtrae" type="button" value="Extraer"
					onclick="enviaConsulta()" style="display: none;" /> 
					<input id="botonLibera" name="botonLibera" type="button" value="Liberar"
					onclick="liberaCaso()" style="display: none;" />
				<div id="esperar" align="center" title="Espera">
					<fieldset>
						<table>
							<tr>
								<td>Espere por favor.... <img border="0"
									src="../imagenes/espera.gif" height="30">
								</td>
							</tr>
						</table>
					</fieldset>
				</div>
				<div id="desactivaCuenta" align="center"
					title="Activa/Inactiva Cuenta Bancaria">
					<fieldset>
						<table>
							<tr>
								<td>Banco : <input type="text" id='iBanco' name='iBanco'
									value='' readonly style="background: #f0f0f0">
								</td>
							</tr>
							<tr>
								<td>Cuenta: <input type="text" id='iCuenta' name='iCuenta'
									value='' readonly style="background: #f0f0f0">
								</td>
							</tr>
							<tr>
								<td><select id="actInact" name="actInact"
									style="width: 10em;">
										<option id="0" value="0">Inactiva</option>
										<option id="1" value="1">Activa</option>
								</select></td>
							</tr>
							<tr>
								<td>
									<button id="pbActInact" onclick="cambiaEstatusCuenta();">Aceptar</button>
								</td>
							</tr>
						</table>
					</fieldset>
				</div>
				<fieldset id="fieldAutoriza" style="display: none;">
					<legend id="legendAutoriza">Autorizar</legend>
					<table align="left">
						<tr align="left">
							<td><input type="radio" id="autorizaSi" name="autorizaSi"
								onClick="habilitaObservaciones(1)" value="Si"> Si 
								<input type="radio" id="autorizaNo" name="autorizaNo"
								onClick="habilitaObservaciones(2)" value="No"> No
								&nbsp;&nbsp;&nbsp;&nbsp; 
								<label id="lObservacion" style="display: none;">Observaciones:</label>
							</td>
							<td id="tdObservacion" style="display: none;"><textarea
									rows="5" cols="60" id="cObservaciones" name="cObservaciones"></textarea>
							</td>
						</tr>
						<tr>
							<td><label id="lbl_CorreccionDatos">Correccion de Datos</label> 
									<input type="checkbox" id="chk_CorreccionDatos" name="chk_CorreccionDatos" value="0">
							</td>
						</tr>
					</table>
				</fieldset>
				<fieldset id="fieldHabilita" style="display: none;">
					<legend>Proveedor Habilitado</legend>
					<table align="left">
						<tr align="left">
							<td><input type="radio" id="habilitaSi" name="habilitaSi"
								onClick="inhabilitaProveedor(1)" value="Si"> Si 
								<input type="radio" id="habilitaNo" name="habilitaNo"
								onClick="inhabilitaProveedor(2)" value="No"> No
							</td>
						</tr>
					</table>
				</fieldset>
				<legend>Captura Datos Generales</legend>
				<table>
					<tr align="left" id="trdescProv">
						<td><font color="blue">Proveedor: Prestador de
								servicios o Persona Moral o Física para pago por el cap. 2000,
								3000, 5000 y 6000</font></td>
					</tr>
					<tr align="left" id="trdescBen" style="display: none;">
						<td><font color="blue">Beneficiario: Empleado CNF,
								Persona Moral o Física para pago de subsidios cap. 4000</font></td>
					</tr>
				</table>
				<table align="left">
					<tr align="left" id="trCBEN" style="display: none;">
						<td>CBEN:&nbsp;&nbsp;&nbsp;&nbsp;<input type="text"
							name="CBEN" id="CBEN" readonly="readonly" style="width: 4em;" />
						</td>
					</tr>
					<tr align="left">
						<td>Tipo de Persona:</td>
						<td><select id="cIdTipoPersonaRFC" name="cIdTipoPersonaRFC"
							onChange="tipoPersonaChange(this)" style="width: 15em;">
								<option id="1" value="1" selected>PERSONA MORAL</option>
								<option id="2" value="2">PERSONA FISICA</option>
								<option id="3" value="3">EMPLEADO CNF</option>
						</select> <select id="tipoPB" name="tipoPB" onChange="tipoPBChange()"
							style="width: 15em;">
								<option id="1" value="1" selected>PROVEEDOR</option>
								<option id="2" value="2">BENEFICIARIO</option>
						</select> <select id="cIdTipoDoc" name="cIdTipoDoc" style="display: none;"></select>
							<select id="cDocBanca" name="cDocBanca" style="display: none;"></select>
							<select id="ctaBanca" name="ctaBanca" style="display: none;"></select>
							<label id="lbl_Editar">editar</label> <input type="checkbox"
							id="chk_edTipoPersona" name="chk_edTipoPersona" value="0">
						</td>
					</tr>
					<tr align="left">
					<tr align="left" id="trNoEmpleado" style="display: none;">
						<td>No. Empleado:</td>
						<td><input type="text" name="NEmp" id="NEmp"
							onKeyPress="return(onlyNumbersAndLetters(event,this));"
							onblur="buscaEmpleado();" style="width: 4em;"/>
						</td>
					</tr>
					<tr align="left">
						<td>Registro Federal de Contribuyentes [RFC]</td>
						<td><input type="text" name="cIdRFC1" id="cIdRFC1" maxLength="3" style="width: 4em;"
							onKeyPress="Change(this,event);return(onlyNumbersAndLetters(event,this));"
							onblur="ChangeCase(this);" />- 
							<input type="text" name="cIdRFC2" id="cIdRFC2" style="width: 4em;" maxlength="6"
							onKeyPress="Change(this,event);return(onlyNumbersAndLetters(event,this));"
							onblur="ChangeCase(this);" />- 
							<input type="text" name="cIdRFC3" id="cIdRFC3" style="width: 4em;" maxlength="3"
							onKeyPress="return(onlyNumbersAndLetters(event,this));"
							onblur="ChangeCase(this);existeProveedor();consultaBeneficiario();" /> 
							<input type="hidden" name="cIdRFC" id="cIdRFC" style="width: 4em;" />
							<font color="red"></font> 
							<label id="lbl_provExtra">Extranjero</label>
							<input type="checkbox" id="chk_extranjero" name="chk_extranjero" value="0"></td>
					</tr>
					<tr align="left" id="trCurp" style="display: none;">
						<td>CURP</td>
						<td><input type="text" id="cCURP" name="cCURP" maxlength="18" onblur="ChangeCase(this);"
							onKeyPress="return(onlyNumbersAndLetters(event,this));" style="width: 30em;" /> 
							<label id="labelCURP"><font color="red">*</font></label>
						</td>
					</tr>
					<tr align="left" id="pMoralRazon">
						<td>Raz&oacute;n Social</td>
						<td><input type="text" name="cRazonSocial" id="cRazonSocial"
							style="width: 30em;" maxlength="300" onblur="ChangeCase(this);" />
							<label id="labelRazon"><font color="red">*</font></label></td>
					</tr>
					<tr align="left" id="pMoralRepresentante">
						<td>Nombre del Representante</td>
					</tr>
					<tr align="left" id="aPat">
						<td>Apellido Paterno</td>
						<td><input type="text" name="cApellidoPaterno" id="cApellidoPaterno" 
							style="width: 30em;" maxlength="300" onblur="ChangeCase(this);" /> 
							<label id="labelPaterno"><font color="red">*</font></label></td>
					</tr>
					<tr align="left" id="aMat">
						<td>Apellido Materno</td>
						<td><input type="text" name="cApellidoMaterno" id="cApellidoMaterno" 
							style="width: 30em;" maxlength="300" onblur="ChangeCase(this);" /> 
							<label id="labelMaterno"><font color="red">*</font></label></td>
					</tr>
					<tr align="left" id="aNom">
						<td>Nombre</td>
						<td><input type="text" name="cNombre" id="cNombre"
							style="width: 30em;" maxlength="300" onblur="ChangeCase(this);" />
							<label id="labelNombre"><font color="red">*</font></label>
					</tr>
					<tr align="left" id=trGiro>
						<td>Giro</td>
						<td><input type="text" name="cGiro" id="cGiro"
							style="width: 30em;" maxlength="499" onblur="ChangeCase(this);" />
							<label id="labelGiro"><font color="red">*</font></label></td>
					</tr>
					<!--<tr align="left">
					<td>N&uacute;mero Interno de Registro </td>
					<td><input type="text" name="cNumeroRegistro" id="cNumeroRegistro" style="width: 30em;" maxlength="50"/></td>
				</tr>-->
					<tr align="left" id=trPyme>
						<td>Pyme</td>
						<td><select id="nIdPyme" name="nIdPyme" style="width: 30em;"
							onchange="validaOrganismoPublico()">
								<!-- 						<option id="0" value="0">No aplica</option> -->
								<!-- 						<option id="1" value="1">Micro Empresa</option> -->
								<!-- 						<option id="2" value="2">Pequeña Empresa</option> -->
								<!-- 						<option id="3" value="3">Mediana Empresa</option> -->
						</select></td>
					</tr>
					<tr id="trLegenOrgPub" style="display: none;">
						<td colspan="2"><span style="color: red">Los
								organismos p&uacute;blicos solo se pueden dar de alta en el area
								de adquisiciones de oficinas centrales. </span></td>
					</tr>
				</table>
			</fieldset>
			<fieldset>
				<legend>Domicilio Fiscal </legend>
				<table align="left">
					<tr align="left" id="Pais">
						<td>Pais</td>
						<td><input type="text" name="cPais" id="cPais"
							style="width: 30em;" maxlength="100" onblur="ChangeCase(this);" />
							<font color="red">*</font>
						</td>
					</tr>
					<tr align="left">
						<td>Entidad Federativa</td>
						<td><select id="cEstadoFiscal" name="cEstadoFiscal"
							onChange="actualizaMunicipio();" style="width: 30em;">
							</select>
						</td>
					</tr>
					<tr align="left">
						<td>Municipio</td>
						<td><select id="cMunicipioFiscal" name="cMunicipioFiscal"
							style="width: 30em;"></select></td>
					</tr>
					<tr align="left">
						<td>Calle</td>
						<td><input type="text" name="cCalle" id="cCalle" style="width: 30em;" maxlength="100"
							onKeyPress="return(onlyNumbersAndLetters(event,this));" onblur="ChangeCase(this);" /> 
							<label id="labelCalle"><font color="red">*</font></label></td>
					</tr>
					<tr align="left">
						<td>N&uacute;mero Externo</td>
						<td><input type="text" name="cNumeroExterno"
							id="cNumeroExterno" style="width: 30em;" maxlength="20"
							onKeyPress="return(onlyNumbersAndLetters(event,this));" /> 
							<label id="labelNumero"><font color="red">*</font></label></td>
					</tr>
					<tr align="left">
						<td>N&uacute;mero Interno</td>
						<td><input type="text" name="cNumeroInterno"
							id="cNumeroInterno" style="width: 30em;" maxlength="20"
							onKeyPress="return(onlyNumbersAndLetters(event,this));" />
						</td>
					</tr>
					<tr align="left">
						<td>Colonia</td>
						<td><input type="text" name="cColonia" id="cColonia"
							style="width: 30em;" maxlength="100" onblur="ChangeCase(this);"
							onKeyPress="return(onlyNumbersAndLetters(event,this));" /> 
							<label id="labelColonia"><font color="red">*</font></label>
						</td>
					</tr>
					<tr align="left">
						<td>C&oacute;digo Postal</td>
						<td><input type="text" name="cCodigoPostal" id="cCodigoPostal" style="width: 30em;"
							onKeyPress="return onlyNumbers(event);" maxlength="5" /> 
							<label id="labelCP"><font color="red">*</font></label>
						</td>
					</tr>
				</table>
			</fieldset>
			<fieldset>
				<legend>Contacto </legend>
				<table align="left">
					<tr align="left">
						<td>Pagina Web</td>
						<td><input type="text" name="cUrl" id="cUrl" style="width: 30em;" maxlength="75" /></td>
					</tr>
					<tr align="left">
						<td>Correo Electr&oacute;nico</td>
						<td><input type="text" name="cEmail" id="cEmail" style="width: 30em;" maxlength="75" /> 
							<label id="labelCorreo"><font color="red">*</font></label>
						</td>
					</tr>
					<tr align="left">
						<td>Tel&eacute;fono</td>
						<td><select id="cTipoTelefono" name="cTipoTelefono" style="width: 8em;"></select>&nbsp; 
							<input type="text" name="cTelefono" id="cTelefono" style="width: 8em;"
							maxlength="25" onKeyPress="return onlyNumbers(event);" /> 
							<label id="labelTel"><font color="red">*</font></label>
						</td>
					</tr>
					<tr align="left">
						<td colspan="2"><label class="validateTips ui-state-error"></label>
						</td>
						<td id="bAgregarCuentasBancarias">
							<input type="button" id="pbCuentasBancarias" value="Agregar Cuenta Bancaria"/>
						</td>
					</tr>
				</table>
			</fieldset>
			<fieldset id="cuentasBancarias">
				<table id="tblCuentasBancarias" width="750px">
					<thead>
						<tr>
							<th width="50px">cBanco</th>
							<th width="120px">Banco</th>
							<th width="40px">Plaza</th>
							<th width="115px">No.Cuenta</th>
							<th width="35px">Dígito</th>
							<th width="60px">Sucursal</th>
							<th width="60px">cStatus</th>
							<th width="70px">Estatus</th>
							<th width="30px">Enviado</th>
							<th width="30px">Autorizada</th>
							<th width="30px">&nbsp;</th>
						</tr>
					</thead>
				</table>
			</fieldset>
		</div>
	</form>
	<div id="dialog-form" title="Captura de Cuentas Bancarias">
		<form id="FormProveedor" name="FormProveedor"
			action="../UploadDocBancario" method="post"
			enctype="multipart/form-data">
			<input type='hidden' id='cBancoH2' name='cBancoH2' value=''>
			<input type='hidden' id='cIdRFCB2' name='cIdRFCB2' value=''>
			<input type='hidden' id='cStatusCuentaH2' name='cStatusCuentaH2' value='0'> 
			<input type='hidden' id='dBancoH2' name='dBancoH2' value=''> 
			<input type='hidden' id='nBCBEnviadoSICOPH2' name='nBCBEnviadoSICOPH2' value='0'>
			<input type='hidden' id='cFolio2' name='cFolio2'>
			<fieldset>
				<table cellpadding="2" cellspacing="0" border="0" width="100%">
					<tr>
						<td align="right">Beneficiario:</td>
						<td colspan=3>
							<input type="text" name="txtNombreProveerdor" id="txtNombreProveerdor" readonly
							style=" width:100%; background: #f0f0f0" />
						</td>
					</tr>
					<tr>
						<td align="right">Banco:</td>
						<td colspan="3">
							<select id="cbBanco" name="cbBanco">
							</select>&nbsp;&nbsp;
							Plaza: <input type="text" name="txtPlaza" id="txtPlaza" size="3" maxlength="3"
							onkeypress="Validaciones(this,2)" /> &nbsp;&nbsp;
							No.Cuenta: <input type="text" name="txtCuentaBancaria" id="txtCuentaBancaria"
							size="12" maxlength="11" onkeypress="Validaciones(this,2)" /> &nbsp;&nbsp;
							Dígito de Control: <input type="text" name="txtDigitoVerificador" id="txtDigitoVerificador"
							align="middle" size="1" readonly style="background: #f0f0f0" />
						</td>
					</tr>
					<tr>
						<td align="right">Sucursal:</td>
						<td width="200px"><input type="text" name="txtSucursal"
							id="txtSucursal" align="middle" size="4" maxlength="4"
							onkeypress="Validaciones(this,2)" /></td>
						<td align="right" width="130px">CLABE:</td>
						<td width="250px"><input type="text" name="txtCLABE"
							id="txtCLABE" readonly style="WIDTH:100%; background: #f0f0f0" />
						</td>
					</tr>
				</table>
				<table>
					<tr>
						<td>Comprobante Bancario: <input type="file" value=""
							id="archivoZip" name="archivoZip">
						</td>
					</tr>
				</table>
			</fieldset>
		</form>
	</div>
	<div id="Motivos">
		<fieldset>
			<legend>Capture los motivos de la modificacion</legend>
			<table>
				<tr>
					<td align="left">Motivos:</td>
				</tr>
				<tr>
					<td align="right"><textarea cols=50 rows=6 id="motivoModifica"
							name="motivoModifica"></textarea></td>
				</tr>
			</table>
		</fieldset>
	</div>
</body>

</html>