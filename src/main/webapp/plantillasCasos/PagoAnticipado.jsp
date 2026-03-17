<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="com.syc.gestion.core.*"%>
<%@page import="com.syc.gestion.servlet.*"%>
<%@page import="com.syc.gestion.util.*"%>
<%@page import="java.util.*"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.text.DateFormat"%>
<%@page import="java.util.Calendar"%>


<%
	String cUnidadResponsableContable = "RHQ";
	String centroContable = null;
	Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
	Usuario usuario = (Usuario) session
			.getAttribute(GestionInterface.ATT_USER);
	String UR = usuario.getU_UR();

	AdecuacionBusinessLogic adecProy = new AdecuacionBusinessLogic(
			GestionInterface.ATT_CONEXION);
	String aEjercicioFiscal = adecProy.obtenEjercicioFiscal();

	int id_caso = c.getIdCaso();

	final int nFolioPagoAnticipado = new Integer(c.getFolio()
			.substring(c.getFolio().lastIndexOf('-') + 1)).intValue();
	final String folioPagoAnticipado = c.getFolio();

	if (usuario.getPropiedades() != null
			&& usuario.getPropiedades().containsKey("CCENTROCONTABLE")) {
		centroContable = usuario.getPropiedad("CCENTROCONTABLE")
				.getValor();
	}

	String U_Login = usuario.getLogin();
	usuario.getU_Ramo();
	String cUnidadResponsable = usuario.getU_UR();

	String DATE_FORMAT = "dd/MM/yyyy";
	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	Calendar c1 = Calendar.getInstance(); // today

	String today = sdf.format(c1.getTime());
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Formatos para la toma de decisión</title>

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

<style type="text/css" title="currentStyle">
#feedback {
	font-size: 1.4em;
}

#selectable .ui-selecting {
	background: #FECA40;
}

#selectable .ui-selected {
	background: #F39814;
	color: white;
}

#selectable {
	list-style-type: none;
	margin: 0;
	padding: 0;
	width: 60%;
}

#selectable li {
	margin: 3px;
	padding: 0.4em;
	font-size: 1.4em;
	height: 18px;
}

.notEditable {
	background-color: #CCCCCC;
}

.monto {
	text-align: right;
	border: 1px solid #aaaaaa;
	color: #222222;
}






</style>




<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js">
	
</script>

<script type="text/javascript" src="../Generador/js/crud.js">
	
</script>

<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js">
	
</script>

<script type="text/javascript" src="../Ayudas/js/autoCompleta.js">
	
</script>

<script type="text/javascript" src="../js/jsquery.js">
	
</script>

<script type="text/javascript"
	src="../Generador/js/jquery.dataTables.js">
	
</script>
<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js">
	
</script>
<script type="text/javascript"
	src="../Generador/js/jquery-ui-1.8.16.custom.min.js">
	
</script>
<script type="text/javascript"
	src="../Generador/js/jquery.ui.datepicker.js">
	
</script>
<script type="text/javascript" src="../Generador/js/jquery.ui.tabs.js">
	
</script>
<script type="text/javascript" src="../js/catalogo/general.js">
	
</script>

<script type="text/javascript" src="../js/catalogo/pagoAnticipado.js">
	
</script>

<script type="text/javascript"
	src="../Generador/js/jquery.formatCurrency.js">
	
</script>
<script type="text/javascript"
	src="../Generador/js/jquery.formatCurrency.all.js">
	
</script>
<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js">
	
</script>
<script type="text/javascript">
	var oTable;
	var oTableEp;
	var giRedraw = false;
	var catalogoEp;
	var pos;
	var posDescrip;
	var mensajeDescripcion="";
	var mensajeDescripcionEp="";
	var mensaje="";
	var sum;
	var dataEp;

	function validaciones(id_oper)
	{
		mensaje="";
		var revisa=true;
		var epElegida = oTableEp.fnGetData();
		var descripElegida = oTable.fnGetData();
		
		if($("#tjustificacion").val()==null || $("#tjustificacion").val()=="")
		{
			revisa=false;
			mensaje+="Justificación es un campo requerido \n";
		}
		
		if(($("#fInicio").val()=="")||($("#fFin").val()==""))
		{
			revisa=false;
			mensaje+="Debe registrar el periodo de solicitud \n";
		}
		
		
		if(epElegida.length<=0)
		{
			revisa=false;
			mensaje+="Debe elegir por lo menos una clave presupuestal \n";
		}
		
		
// 		if(descripElegida.length<=0)
// 		{
// 			revisa=false;
// 			mensaje+="Debe ingresar por lo menos una descripción \n";
// 		}
		
		if(id_oper==2 || id_oper==3)
			{
				if(!($("#checkbox").is(':checked')))
				{
					if($("#tmotivoRechazo").val()==null || $("#tmotivoRechazo").val()=="")
					{
						revisa=false;
						mensaje+="Debe ingresar el motivo del rechazo \n";
					}
				}
			}
		
		return revisa;
	}
	
	function onSubmit(id_oper) 
	{
		var p = window.parent;
		if(validaciones(id_oper)) 
		{
			if(validaDescripcionEp()) 
			{
			  if(comparaPresupuesto())
			  {
				if($("#tmotivoRechazo").val()=="")
				{
					$("#tmotivoRechazo").val(null);
				}
	
				
				
				//Detalle
				$("#nDocRenglon").val("0");
				var arrData = oTable.fnGetData();
				var arrDataEp = oTableEp.fnGetData();
				$("#existe").val("0");
				
			
				
				
	
	
				$.blockUI( {
						message : "Procesando espere ......"
					});
				
				queryFormPost({
					queryName : "readExistePagoAnticipado",
					async : false,
					callback : function() {
						if ($("#existe").val() == "1")
						{
						    // almacenamos el estatus
							$("#h_Select_Estatus").val($("#Select_Estatus").val());
			
							var actualizaEncabezado=false;
							queryFormPost({
								queryName : "upagoAnticipado",
								async : false,
								callback : function() {
									//alert("Información almacenado con éxito");
									actualizaEncabezado=true;
								}
							});
							
							
							//Borrar
							
							//queryFormPost('dPAnticipadoDescripcion', {async: false });
							
							//Borrar Ep
							queryFormPost('dPAnticipadoDetalle', {async: false });
							
							//actualizar
							var inserto=true;
							var insertoEp=false;
	 						
	 						//actualizarEp
							if(arrDataEp.length>0)
	 						{
								var Importe =0.00;
	 							for(var i=0; i<arrDataEp.length; i++)
	 							{
	 								
	 								$("#Ep").val(arrDataEp[i][0]);
	 								$("#nrenglonEp").val(i);
	 								
	 								 Importe = fn_quitaFormato(arrDataEp[i][1]);
									$("#importeTotal").val((parseFloat(Importe)).toFixed(2));
	 								//$("#importeTotal").val((parseFloat(arrDataEp[i][1])).toFixed(2));
									
	 								insertoEp=false;
									
	 									queryFormPost({
											queryName : "iPAnticipadoDetalle",
	 										async : false,
	 										callback : function() 
	 										{
	 											insertoEp=true;
											}
	 									});
									
	 							}
	 							
								
	 						}
	 						else
	 						{
	 							insertoEp=true;
	 						}
	 						if(insertoEp && inserto && actualizaEncabezado)
	 						{
	 							alert("Información almacenado con éxito");
	 						}
						}
						else
						{
							var insertoEncabezado=false;
	 						queryFormPost({
	 							queryName : "ipagoAnticipado",
	 							async : false,
	 							callback : function() {
	 								//alert("Información almacenado con éxito");
	 								insertoEncabezado=true;
	 							}
	 						});
							
							var inserto=true;
	//  						if(arrData.length>0)
	//  						{
	 							
								
	//  							for(var i=0; i<arrData.length; i++)
	//  							{
	 						
	// 								$("#descripcion").val(arrData[i][0]);
	// 								$("#importe").val(arrData[i][1]);
	// 								$("#nDocRenglon").val(i);
									
	//								insertar
	// 								inserto=false;
									
	// 									queryFormPost({
	// 										queryName : "iPAnticipadoDescripcion",
	// 										async : false,
	// 										callback : function() 
	// 										{
	// 											inserto=true;
	//											alert("Información almacenado con éxito");
	// 										}
	// 									});
									
	//  							}
	
	//  						}
	//  						else
	//  						{
	//  							inserto=true;
	//  						}
	 						
	 						//Eps
	 						var insertoEp=false;
	 						var Importe =0.00
	 						if(arrDataEp.length>0)
	 						{						
	 							for(var i=0; i<arrDataEp.length; i++)
	 							{
	 								
	 								$("#Ep").val(arrDataEp[i][0]);
	 								$("#nrenglonEp").val(i);
									Importe = fn_quitaFormato(arrDataEp[i][1]);
									$("#importeTotal").val((parseFloat(Importe)).toFixed(2));
	 								insertoEp=false;
									
	 									queryFormPost({
											queryName : "iPAnticipadoDetalle",
	 										async : false,
	 										callback : function() 
	 										{
	 											insertoEp=true;
											}
	 									});
									
	 							}
	//  							if(insertoEp)
	//  							{
	//  								alert("Información almacenado con éxito");
	//  							}
								
	 						}
	 						else
	 						{
	 							insertoEp=true;
	 						}
	 						
	 						if(inserto && insertoEncabezado && insertoEp)
	 						{
	 							alert("Información almacenado con éxito");
	 						}
						}
					}
				});
				
				if (id_oper==1){
			  	p.gestion.setFolio($("#FOLIO").val());
				p.gestion.setOperador($("#OPERADOR").val());
				p.gestion.setFechaDocumento($("#FECHA_SOLICITUD").val());
				p.gestion.setEjercicioFiscal($("#aEjercicioFiscal").val());
				p.gestion.setConceptoMov("Pago Anticipado");
				p.gestion.setMoneda("MXP");//el presupuesto siempre es en pesos
				p.gestion.setFechaApCont($("#FECHA_APLICACION_CONTABLE").val());
				}
				
				//parent.document.getElementById("pb_save").disabled=false;
				parent.document.getElementById("pb_send").disabled=false;
				
				
				
				$.unblockUI();
				return true;
				
			   }
			   else
			   {
					alert(mensajeDescripcion);
			   }
		   }
		   else
		   {
				alert(mensajeDescripcionEp);
		   }
		   
		}
		else
		{
			alert(mensaje);
		}
	}
	
	
	function fn_quitaFormato(fld) 
{
	var valcol = fld.toString();
	valcol = valcol.replace(/[$]/g, "");
	valcol = valcol.replace(/,/g, "");
	return valcol;
}
	
	function validaDescripcionEp()
	{
		var revisa=true;
		var arrDataEp = oTableEp.fnGetData();
		if(arrDataEp.length>0)
  		{
  			mensajeDescripcionEp="";
  			for(var i=0; i<arrDataEp.length; i++)
 			{
 				$("#Ep").val(arrDataEp[i][0]);
 				queryFormPost("existeDescripcion", {async : false});
 				
				if($("#registroDescripcion").val()=="0")
  				{	
					revisa=false;
  					mensajeDescripcionEp+="La clave presupuestal "+arrDataEp[i][0]+" no cuenta con descripcion \n";
  				}
		 	}
		}
		return revisa;
	}
	
	
	function comparaPresupuesto()
	{
		
		var revisaPresupuesto=true;
		var arrDataEp = oTableEp.fnGetData();
		var revisa=true;
		mensajeDescripcion="";
		if(arrDataEp.length>0)
  		{
  			for(var i=0; i<arrDataEp.length; i++)
 			{
 								
 				$("#epMonto").val(arrDataEp[i][0]);
				queryFormPost("montoTotalPagoAnti", {async : false});
// 				montoTotal
				$("#Ep").val(arrDataEp[i][0]);
				queryFormPost("descripcionTotalPagoAnti", {async : false});
				
				var montoT=parseFloat($("#montoTotal").val());
				var montoDescrip=parseFloat($("#importeTotalDescrip").val());

				if(montoDescrip>montoT)
				{
					mensajeDescripcion+="Las descripciones de la clave presupuestal "+arrDataEp[i][0]+" exceden el monto anual \n";
					revisa=false;
				}
// 			importeTotalDescrip
			}
		}
		return revisa;
	}



	function motivNoAutoriza()
	{
		queryFormPost({
		queryName : "existeMotivo",
		async : false,
		callback : function() 
		{
			if ($("#tmotivoRechazoLeyenda").val() !=null )
			{
				if ($("#tmotivoRechazoLeyenda").val() !="")
				{
					$("#leyendaMotivo").show();
				}
			}
			
		}
		});
	}

	function onPostDisplay(id_oper)
	 {
		if(id_oper==3)
			{
				if($("#checkbox").is(':checked'))
				{
				//	$.blockUI( {
				//		message : "Procesando espere ......"
				//	});
				//	parent.document.getElementById("frmSend").submit();
					
					//$.unblockUI();
					
				}
			}
	 	
		return true;
	}

	function ResponsableSiguiente(id_oper) 
	{
			if( id_oper == 1 )
			{
				return "REVISOR_P_ANT";
			}
			else if ( id_oper == 2)
			{
				if( $("#checkbox").is(':checked') ) //document.datosReintegro.autorizaRein[1].checked
				{
					return "AUTORIZADOR_P_ANT";
				}
				else
				{
					return "CAPTURISTA_P_ANTICIPADO";
				}
			}
			else if ( id_oper == 3)
			{
				if( $("#checkbox").is(':checked') )
				{
					return "CONSULTA_PAGOANTICIPADO";
				}
				else
				{
					return "REVISOR_P_ANT";
				}
			}
	
	}

	function OperacionSiguiente(id_oper) 
	{
		if( id_oper == 1 )
			{
				return "revisa_p_anti";
			}
			else if (id_oper == 2)
			{
				
				if($("#checkbox").is(':checked')) //document.datosReintegro.autorizaRein[1].checked
				{
					return "autoriza_p_anti";
				}
				else
				{
					return "captura_p_anticipado";
				}
			}
			else if ( id_oper == 3)
			{
				if($("#checkbox").is(':checked'))
				{
					return "consulta_pagoanticipado";
				}
				else
				{
					return "revisa_p_anti";
				}
			}
	}

	function onPostSubmit(id_oper) 
	{
		
		parent.document.getElementById("pb_send").disabled=true;
		parent.document.getElementById("pb_save").disabled=true;
		parent.document.getElementById("pb_cancel").disabled=true;
		parent.document.getElementById("pb_leave").disabled=true;
			$.blockUI( {
					message : "Procesando espere ......"
				});
				
		return true;
	}

	function onLoadPlantilla(id_oper) 
	{
		
		queryFormPost("sPagoAnticipado", {async : false});
		$("#motivoRechazo").hide();
		$("#leyendaMotivo").hide();
		$("#tmotivoRechazo").val(null);
		$("#tTotal").val("");
		
		 querySelectPost("catalogo_Estatus_PagoAnticipado", "Select_Estatus", {async : false});	

		
		$("#Select_Estatus").val($("#h_Select_Estatus").val());	
		document.getElementById("Select_Estatus").disabled=true;		
				
		if(id_oper== 3)
		document.getElementById("Select_Estatus").disabled=false;
		
		
		if(id_oper== 2 || id_oper== 3)
		{
			$("#autoriza").show();
			motivNoAutoriza();
		}	
		else
		{
			if(id_oper== 4)
			{
				fn_DeshabilitarCampos();
        	}
			else
			{
				if(id_oper== 1)
				{
					motivNoAutoriza();
				}
				
				$("#autoriza").hide();
			}
		}
		
		
	}

	$(document).ready(function() {
		//Inicializa las ayudas. Simpre debe estar presente en el onLoad (en JQuery es el $(document).ready(function() {});)
		$("input.AyudaSyC").subIniciaDlg();
		$("#motivoRechazo").hide();
		$("#leyendaMotivo").hide();
		//Inicializa el autocompletar. Simpre debe estar presente en el onLoad (en JQuery es el $(document).ready(function() {});)
		$("input.autoCompletaSyC").subIniciaAutoCompleta();

		querySelectPost("tCuentasBancariasURRead", "clavePresupuestal", {
			async : false
		});


		//Genera Tabs (Pestañas)
		$(".tabs").tabs();
	   	var mydate=new Date();
		var year=mydate.getYear();
		var month=mydate.getMonth()+1;
		var Fecha_i = '01/' + month + '/' + year;  
		var Fecha_f =  '31/' + 12 + '/' + year;  
		$(function() { 
		            $("#fInicio").datepicker( {
				    minDate: Fecha_i, maxDate: "+12M +0Y",  changeMonth: true, changeYear: false, showOn : "button",
					dateFormat : "dd/mm/yy",
					buttonImage : "../images/calendar.gif",
					buttonImageOnly : true
					});
			    });
			
				$(function() {
				$("#fFin").datepicker( {
					 minDate: Fecha_i, maxDate: Fecha_f,  changeMonth: true, changeYear: false, showOn : "button",
					dateFormat : "dd/mm/yy",
					buttonImage : "../images/calendar.gif",
					buttonImageOnly : true
					});
				});
			
			
	   	
			
		

		//Crea el DataTable con los campos de config. minimos. Simpre debe estar presente en el onLoad (en JQuery es el $(document).ready(function() {});)
		catalogoEp = $('#dt_catalogo').dataTable({
			"bPaginate" : true,
			"bLengthChange" : true,
			"bFilter" : true,
			"bSort" : true,
			"bInfo" : true,
			"bAutoWidth" : false,
			"sScrollY" : 270,
			"sScrollYInner" : "100%",
			"bJQueryUI" : true,
			"bRetrive" : true,
			"bDestroy" : true,
			"sPaginationType" : "full_numbers",
			"sScrollX" : "100%",
			"sScrollXInner" : "110%",
			"bScrollCollapse" : true,
			//"bServerSide": true,   
			//sAjaxSource : window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vEP_PROGRAMA&qw=cFuncion=3 AND cSubFuncion='04'",
			// 			        aoColumns   : [
			// 						{ sName: "relacion" },
			// 						{ sName: "cGrupoFuncional" },
			// 						{ sName: "cFuncion"},
			// 						{ sName: "cSubFuncion"},
			// 						{ sName: "cProgramaGeneral"},
			// 						{ sName: "cActividadInstitucional"},
			// 						{ sName: "cProgramaPresupuestario"}
			// 					],
			oLanguage : {
				sProcessing : "Procesando...",
				sLengthMenu : "Mostrar _MENU_ registros",
				sZeroRecords : "No hay registros a mostrar",
				sEmptyTable : "No hay datos en la tabla",
				sLoadingRecords : "Cargando...",
				sInfo : "Registros _START_ al _END_ de _TOTAL_",
				sInfoEmpty : "Registro 0 al 0 de 0",
				sInfoFiltered : "(filtered from _MAX_ total entries)",
				sInfoPostFix : "",
				sInfoThousands : ",",
				sSearch : "Filtro:",
				oPaginate : {
					sFirst : "Primero",
					sPrevious : "Ant.",
					sNext : "Sigte.",
					sLast : "&Uacute;ltimo"
				}
			}
		});

		//alert(window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_NominaCapituloMil&qw=" + " caNoContrarrecibo = '0' ");

		$('#dt_descripBien').dataTable({
			"bPaginate" : true,
			"bLengthChange" : true,
			"bFilter" : true,
			"bSort" : true,
			"bInfo" : true,
			"bAutoWidth" : false,
			"sScrollY" : 270,
			"sScrollYInner" : "100%",
			"bJQueryUI" : true,
			"bRetrive" : true,
			"bDestroy" : true,
			"sPaginationType" : "full_numbers",
			"sScrollX" : "100%",
			"sScrollXInner" : "110%",
			"bScrollCollapse" : true,
			
			oLanguage : {
				sProcessing : "Procesando...",
				sLengthMenu : "Mostrar _MENU_ registros",
				sZeroRecords : "No hay registros a mostrar",
				sEmptyTable : "No hay datos en la tabla",
				sLoadingRecords : "Cargando...",
				sInfo : "Registros _START_ al _END_ de _TOTAL_",
				sInfoEmpty : "Registro 0 al 0 de 0",
				sInfoFiltered : "(filtered from _MAX_ total entries)",
				sInfoPostFix : "",
				sInfoThousands : ",",
				sSearch : "Filtro:",
				oPaginate : {
					sFirst : "Primero",
					sPrevious : "Ant.",
					sNext : "Sigte.",
					sLast : "&Uacute;ltimo" 
				}
			},

			"aoColumns" : [ {
				"sSortDataType" : "dom-checkbox"
			}, {
				"sSortDataType" : "dom-checkbox"
			}, {
				"sSortDataType" : "dom-checkbox"
			}  ]
		});

		//miau
		
		oTable = $('#dt_descripBien').dataTable();
		oTableEp = $('#dt_catalogo').dataTable();

		$("#dt_descripBien tbody").click(function(event) 
		{
			$(oTable.fnSettings().aoData).each(function() 
			{
				$(this.nTr).removeClass('row_selected');
			});
			$(event.target.parentNode).addClass('row_selected');
			
			posDescrip=oTable.fnGetPosition($(event.target.parentNode)[0]);
			var data = oTable.fnGetData(posDescrip);
			$("#positionDescrip").val(data[2]);
				
			
		});

	

		$("#dt_catalogo tbody").click(function(event) 
		{ 
			$(oTableEp.fnSettings().aoData).each(function() {
			$(this.nTr).removeClass('row_selected');
			});
			$(event.target.parentNode).addClass('row_selected');
			
			pos=oTableEp.fnGetPosition($(event.target.parentNode)[0]);
			dataEp = oTableEp.fnGetData(pos);
			colocarEtiqueta(dataEp[0]);	
			borrarTablaSelect(dataEp[0]);
		});
		
		
		
		
		//miau
		
		//Detalle
		
		var camposWhere = " EP, mImporte";
		var elParametro =" nFolioPagoAnticipado = '"+$("#nFolioPagoAnticipado").val()+"'";
		var order = "";
		
		
	
		$.getJSON("../catalogos/SelectJson.jsp",{Tabla: "TPAGOANTICIPADODETALLE", Campos:camposWhere, Param:elParametro, Order:order, MaxReg: "10", ajax: 'true'}, function(j)
		{	
			for (var i = 0; i < j.length; i++)
 			{
 				var tipoPago = new Array(); 
 				tipoPago[i] = j[i].Col0;
 				var tipoPago2 = new Array(); 
 				tipoPago2[i] = j[i].Col1;
 				$("#h_temporal").val(j[i].Col1);
 				$("#h_temporal").formatCurrency();
 				//$('#dt_catalogo').dataTable().fnAddData([tipoPago[i], tipoPago2[i]]);
 				$('#dt_catalogo').dataTable().fnAddData([tipoPago[i], $("#h_temporal").val()]);
 			}	
 			
 			    
		});
		 
		
	
		
		//Descripcion
// 		var camposWhereDescripcion = " NDocRenglon, descripcion, importe ";
// 		var elParametro =" nFolioPagoAnticipado = '"+$("#nFolioPagoAnticipado").val()+"'";
// 		var order = "";
	
// 		$.getJSON("../catalogos/SelectJson.jsp",{Tabla: "TPAGOANTICIPADODESCRIPCION", Campos:camposWhereDescripcion , Param:elParametro, Order:order, MaxReg: "10", ajax: 'true'}, function(j)
// 		{		
// 			for (var i = 0; i < j.length; i++)
// 			{
// 				var tipoPago = j[i].Col1;
// 				var tipoPago2 = j[i].Col2;
// 				$('#dt_descripBien').dataTable().fnAddData([tipoPago, tipoPago2]);
// 			}	
// 		});
	});


	function borrarTablaSelect(epSeleccionada)
	{
		seleccionarDescripcion(epSeleccionada);	
			
	}
	
	
	function fn_DeshabilitarCampos(){
	
				document.getElementById("buscar").disabled=true;
				document.getElementById("buscarEliminar").disabled=true;
				document.getElementById("eliminarButton").disabled=true;
				document.getElementById("agregarButton").disabled=true;
				document.getElementById("tjustificacion").disabled=true;
				document.getElementById("fInicio").disabled=true;
 				document.getElementById("fFin").disabled=true;
 				document.getElementById("tdescripcion").disabled=true;
 				document.getElementById("timporte").disabled=true;
 		    	$('#fInicio').datepicker('disable');
 				$('#fFin').datepicker('disable');
    			document.getElementById("tjustificacion").setAttribute('readonly', 'readonly'); 
    			$("#autoriza").hide();
     }

	function seleccionarDescripcion(epSeleccionada)
	{
     
		//borrar dt_descripBien
		$("#dt_descripBien").dataTable().fnClearTable();
		
		
		var camposWhere = " WHERE nFolioPagoAnticipado = '"+$("#nFolioPagoAnticipado").val()+"' and Ep='"+epSeleccionada+"' ";
        var elParametro ="";
		var order = "";
	
	    //ORIGINal	$.getJSON("../catalogos/SelectJson.jsp",{Tabla: "TPAGOANTICIPADODESCRIPCION", Campos:camposWhereDescripcion , Param:elParametro, Order:order, MaxReg: "10", ajax: 'true'}, function(j)
	
		$.getJSON("../catalogos/SelectJson.jsp",{Tabla: "TPAGOANTICIPADODESCRIPCION", Campos:camposWhere , Param:elParametro, Order:order, MaxReg: "10", ajax: 'true'}, function(j)   
		{		
		
		    for (var i = 0; i < j.length; i++)
			{
				var tipoPago = j[i].Col2;
				//var tipoPago2 = j[i].Col3;
				$("#h_temporal").val(j[i].Col3);
				var tipoPago3 = j[i].Col1;
				$("#h_temporal").val(j[i].Col3);
 				$("#h_temporal").formatCurrency();
 				$('#dt_descripBien').dataTable().fnAddData([tipoPago, $("#h_temporal").val(), tipoPago3]);
			}	
		});
		
		agregarTotal();
		
	}

	function colocarEtiqueta(ep)
	{
		$("#epSeleccionada").text(ep);
	}

	function buscar() {

	}

	/* Funcion que selecciona un registro en una tabla especifica */
		
	function fnGetSelected(oTableLocal) {
		var aReturn = new Array();
		var aTrs = oTableLocal.fnGetNodes();

		for ( var i = 0; i < aTrs.length; i++) {
			if ($(aTrs[i]).hasClass('row_selected')) 
			{
				aReturn.push(aTrs[i]);
			}
		}

		return aReturn;
	}

	function borrar() 
	{
	    //Eliminar un registro con Descrpción
								

		if (fnGetSelected(oTable).length <= 0) 
		{
			alert("Se debe seleccionar un registro");
		}
		else 
		{
			var anSelected = fnGetSelected(oTable);
			oTable.fnDeleteRow(anSelected[0]);
 						
			$("#Ep").val($("#epSeleccionada").text());
 								
 			$("#nrenglonEpBorrar").val($("#positionDescrip").val());
			queryFormPost('dPAnticipadoDescripcionUno', {async: false });

			agregarTotal();
								
		}
	}
								
							
	function CambiarDesc() 
	{
	    if ($("#bEditar").val() != "Editado"){ 
			    //Eliminar un registro con Descripción
 						

		if (fnGetSelected(oTable).length <= 0) 
		{
			alert("Se debe seleccionar un registro");
					return;
		}
		else 
		{
					var RenglonSeleccionado = fnGetSelected(oTable);
					//oTable.fnDeleteRow(RenglonSeleccionado[0]);
					//alert(RenglonSeleccionado[0]);

					var AregloDatos = oTable.fnGetData(RenglonSeleccionado[0] );
		          	$("#nRenglonDesc").val(AregloDatos[2]); 
					$("#tdescripcion").val(AregloDatos[0]);
					$("#timporte").val(AregloDatos[1]);
				    $("#bEditar").val("Editado");
				    $("#ModificarButton").val("Cambiar");
				    $("#agregarButton").hide();
	                $("#eliminarButton").hide();
			
			     }
	       }//si diferente de editado	
	       else{
			        if ($("#tdescripcion").val() == "") {
						alert("Se debe capturar una descripcion");
						return false;
					}
				if ($("#timporte").val() == "") {
						alert("Se debe capturar un importe");
						return false; 
					}		       
		    borrar();
		    agregar();		       { 
			    $("#bEditar").val("");
			    $("#ModificarButton").val("Editar");
			    $("#agregarButton").show();
		        $("#eliminarButton").show();
	        }
			
		}
	}
	
	function borrarEp() 
	{
	     //Eliminar una sola EP
		if (fnGetSelected(oTableEp).length <= 0) 
		{
			alert("Se debe seleccionar un registro");
		} 
		else 
		{
			var anSelected = fnGetSelected(oTableEp);
			oTableEp.fnDeleteRow(anSelected[0]);
			$("#Ep").val(dataEp[0]);

			queryFormPost('dPAnticipadoDescripcionEp', {async: false });
		}
	}

	function validaFecha() {
		var startDt = document.getElementById("fInicio").value;
		var endDt = document.getElementById("fFin").value;

		if ((new Date(startDt).getTime() > new Date(endDt).getTime())) {
				("La fecha de inicio es mayor a la fecha final");
			return false;
		} else {
			return true;
		}

	}
	
	//cada vez que sea eliminar/guardar
	function agregarTotal() 
	{
		sum=parseFloat("0");
		
		var arrData = oTable.fnGetData();
		var arrData = oTable.fnGetData();
		var Importe =  0.00;
		if(arrData.length>0)
  		{
  			for(var i=0; i<arrData.length; i++)
  			{
 				//sum+=parseFloat(arrData[i][1]);
 				 Importe = fn_quitaFormato(arrData[i][1]);
 	       		     sum+= parseFloat(Importe);
			}
			
			$("#tTotal").val(sum);
			$("#tTotal").formatCurrency();
		}
		else
		{
			$("#tTotal").val(sum);
			$("#tTotal").formatCurrency();
		}
		
		
	}

	function agregar() 
	{
	
		if ($("#epSeleccionada").text() == "") {
			alert("Se debe elegir una clave presupuestal");
			return false;
		}
		
		if ($("#tdescripcion").val() == "") {
			alert("Se debe capturar una descripcion");
			return false;
		}
		if ($("#timporte").val() == "") {
			alert("Se debe capturar un importe");
			return false;
		}

		

		//insert
		
		var arrDataEp = oTable.fnGetData();
		
		
		
		$("#Ep").val($("#epSeleccionada").text());
		
				$("#maximoRenglon").val("");
				queryFormPost({
				queryName : "readMaximoRenglon",
				async : false,
				callback : function() 
				{
					if($("#maximoRenglon").val()=="")
					{
						$("#maximoRenglon").val("0");
					}
					
					var renglon=parseInt($("#maximoRenglon").val())+1;
					
					if ($("#bEditar").val() != "Editado"){ 
					$("#nRenglonDesc").val(renglon);
					}      
			 		$("#descripcion").val($("#tdescripcion").val());
			 		$("#importe").val($("#timporte").val());
			
			 		queryFormPost({
								queryName : "iPAnticipadoDescripcion",
			 					async : false,
			 					callback : function() 
			 					{
			 						
								}
			 		});
					$("#timporte").formatCurrency();
					$('#dt_descripBien').dataTable().fnAddData(
				[ $("#tdescripcion").val(), $("#timporte").val(), $("#nRenglonDesc").val() ]);
			
					$("#tdescripcion").val("");
					$("#timporte").val("");
					
					agregarTotal();
				}
			});
		
 		

	}

	function buscarEp() {
		window.open('epPagoAnticipado.jsp?id=gab', 'AyudaEPsPagos',
				'status=1, width=900px, height=680px, left=100px');
	}

	
	function fnClickAddRow() {
		$('#dt_descripBien').dataTable().fnAddData([ ".1", ".2", ".3" ]);
	}
	
	function formatoMoneda()
	{
		//$('#Monto_'+Arreglo[i]).formatCurrency();
	}
	
	
	
	
    function maxLen(text, maxLen,NameText) {
         if (text.value.length > maxLen) {
           alert ("El campo " + NameText + " debe tener máximo de " + maxLen + " caractéres");
           text.focus();
           return false;
     }
	}
	function validaCheck()
	{
		if($("#checkbox").is(':checked')) //document.datosReintegro.autorizaRein[1].checked
		{
			$("#motivoRechazo").hide();
		}
		else
		{
			$("#motivoRechazo").show();
		}
	}
	
</script>

</head>
<body id="dt_example" bottomMargin="0" bgColor="red" leftMargin="0"
	topMargin="0">
	<form id="FormContrato" name="FormContrato">
		<input type="hidden" name="positionDescrip" id="positionDescrip" />
		<input type="hidden" name="h_temporal" id="h_temporal" />
		<input type="hidden" Id="h_Select_Estatus" name="h_Select_Estatus">
		 <input	type="hidden" name="maximoRenglon" id="maximoRenglon" /> <input
			type="hidden" name="Ep" id="Ep" /> <input type="hidden"
			name="nRenglonDesc" id="nRenglonDesc" /> <input type="hidden"
			name="epMonto" id="epMonto" /> <input type="hidden"
			name="importeTotal" id="importeTotal" /> 
			<input type="hidden" name="importeTotalDescrip" id="importeTotalDescrip" /> 
			<input type="hidden" name="StrFiltro" id="StrFiltro" value="3" /> 
			<input type="hidden" name="registroDescripcion" id="registroDescripcion" />
		<input type="hidden" name="montoTotal" id="montoTotal" /> <input
			type="hidden" name="nrenglonEp" id="nrenglonEp" /> <input
			type="hidden" name="nrenglonEpBorrar" id="nrenglonEpBorrar" /> <input
			type="hidden" name="nDocRenglon" id="nDocRenglon" /> <input
			type="hidden" name="existe" id="existe" value="0" /> <input
			type="hidden" name="OPERADOR" id="OPERADOR"
			value="<%=c.getCasoOperacion(0) == null ? "caso operacion nulo" : c
					.getCasoOperacion(0).getResponsable()%>" />
		<input type="hidden" name="FECHA_SOLICITUD" id="FECHA_SOLICITUD"
			value="<%=today%>" /> <input type="hidden"
			name="FECHA_APLICACION_CONTABLE" id="FECHA_APLICACION_CONTABLE"
			value="<%=today%>" readonly="readonly" maxlength="10" size="10" /> <input
			type="hidden" name="FECHA_DOCUMENTO" id="FECHA_DOCUMENTO"
			value="<%=today%>" /> <input type="hidden" name="FOLIO" id="FOLIO"
			value="<%=folioPagoAnticipado%>" /> <input type="hidden"
			name="nFolioPagoAnticipado" id="nFolioPagoAnticipado"
			value="<%=nFolioPagoAnticipado%>" /> <input type="hidden"
			name="aEjercicioFiscal" id="aEjercicioFiscal"
			value="<%=aEjercicioFiscal%>" /> <input type="hidden"
			name="cUnidadResponsableContable" id="cUnidadResponsableContable"
			value="<%=cUnidadResponsableContable%>" /> <input type="hidden"
			name="centroContable" id="centroContable" value="<%=centroContable%>" />
		<input type="hidden" name="id_caso" id="id_caso" value="<%=id_caso%>" />
		<input type="hidden" name="U_Login" id="U_Login" value="<%=U_Login%>" />
		<input type="hidden" name="cUnidadResponsable" id="cUnidadResponsable"
			value="<%=cUnidadResponsable%>" /> <input type="hidden"
			name="cUnidadResponsable" id="cUnidadResponsable" value="<%=UR%>" />
		<input type="hidden" value="" id="buscaVal" name="buscaVal"> <input
			type="hidden" value="" id="existe" name="existe"> <input
			type="hidden" name="descripcion" id="descripcion" /> <input
			type="hidden" name="importe" id="importe" />
        <input type="hidden" value="" id="bEditar" name="bEditar">
		<div id="container" class="container">
			<h1>Solicitud de Autorizacion de pagos anticipados</h1>

			<div class="tabs">

				<ul>
					<li><a id="Link01" href="#tabs-0">General</a></li>
					<li><a id="Link02" href="#tabs-1">Claves presupuestales</a></li>
					<li><a id="Link03" href="#tabs-2" onclick="agregarTotal()">Descripción
							del bien</a></li>
				</ul>
				<div id="tabs-0">
					<table align="center" >
						<tr>
							<td align="center" colspan="6">Numero de Folio: <input
								readonly type="text" id="numFolio" name="numFolio"  readonly="readonly"
							class="notEditable" value="<%=folioPagoAnticipado%>"></td>
							<td width="20%"></td>
							
							<td align="right"> Estatus:</td>
							<td><select id="Select_Estatus" name="Select_Estatus">
						</tr>
					</table>

					
						<table align="center">
							<tr>
								<td><textarea id="tjustificacion" name="tjustificacion" onBlur="maxLen(this,4000,'Justificación');"
										rows="4" cols="100"></textarea>
								</td>
							</tr>
						</table>
				
					<br></br>
					<fieldset>
						<legend>Periodo de solicitud de autorizacion</legend>
						<table border="0" align="center">
							<tr>
								<td style="padding-top: 3px"><input
									title="Se captura la Fecha Inicio" type="text" id="fInicio"
									name="fInicio" size="10">
								</td>
								<p>
								<td style="padding-top: 3px"><input
									title="Se captura la Fecha fin" type="text" id="fFin"
									name="fFin" size="10">
								</td>	
							</tr>
						</table>
					</fieldset>
					<div id="leyendaMotivo">
						Motivo del rechazo: <input readonly style="width: 400px;" type="text"
							id="tmotivoRechazoLeyenda" name="tmotivoRechazoLeyenda">
					</div>

					<div id="autoriza">
						Autoriza: <input name="checkbox" id="checkbox" type="checkbox"
							value="1" checked="checked" onclick="validaCheck();" />
					</div>

					<div id="motivoRechazo">
						Motivo del rechazo:
						<textarea id="tmotivoRechazo" name="tmotivoRechazo" rows="3"
							cols="50"></textarea>
					</div>

				</div>
				<div id="tabs-1">
					<fieldset>
						<legend>Clave Presupuestal</legend>
						<div id="dv">
							<table align="right">
								<tr>
									<td align="right"><input type="button" id="buscar"
										value="Buscar" onclick="buscarEp()"></td>
									<td><input type="button" value="Eliminar"
										id="buscarEliminar" onclick="borrarEp()"></td>
								</tr>
							</table>
							<br></br>
							<table id="dt_catalogo" class="display" cellspacing="0"
								cellpadding="2" align="center">
								<thead>
									<tr>
										<th>Clave presupuestal</th>
										<th>Monto anual</th>
									</tr>
								</thead>
							</table>
						</div>
					</fieldset>
				</div>
				<div id="tabs-2">
					<fieldset>
						Clave Presupuestal seleccionada: <label style="font-weight: bold;"
							id="epSeleccionada"></label> <br></br>
						<table align="center" border=0  >
							<tr>
							   
								<td>Descripcion:</td>
								<td>Importe</td>
								<td><input type="button" id="agregarButton" value="Agregar"   onclick="agregar()"><br>
								
							</tr>
							<tr>	
								<td><textarea style="width: 400px; height: 60px"
										id="tdescripcion" name="tdescripcion"  onBlur="maxLen(this,200,'Descripción');"></textarea></td>

								
								<!-- 							<td><input type="text" id="timporte" name="timporte" -->
								<!-- 								onkeypress="return(onlyNumbers(event));"></td> -->
								<td><input type="text" id="timporte" name="timporte"
									onkeypress="return(onlyNumbers(event));">
								</td>
								<td><input type="button" id="eliminarButton" value="Eliminar" onclick="borrar()"><br>
								    <input type="button" id="ModificarButton" value="Editar"  onclick="CambiarDesc()"> 
								</td>
							</tr>
						</table>
						<table id="dt_descripBien" class="display" cellspacing="0"
							cellpadding="2" align="center">
							<thead>
								<tr>
									<th>Descripción</th>
									<th >Importe</th>
									<th>Renglon</th>
								</tr>
							</thead>
						</table>
						<table align="right">
							<tr>
								<td>Total:</td>
								<td><input type="text" id="tTotal" name="tTotal"></td>
							</tr>
						</table>
					</fieldset>
				</div>
			</div>
			<!-- 			<table align="center"> -->
			<!-- 				<tr> -->

			<!-- 					<td colspan="6" align="right"><input type="button" value="Aceptar" id="aceptarButton" onclick="guardar()">  -->
			<!-- 					</td> -->
			<!-- 				</tr> -->
			<!-- 			</table> -->

		</div>

	</form>
</body>
</html>