/**
 * 
 */
jQuery.browser = {};
(function () {
    jQuery.browser.msie = false;
    jQuery.browser.version = 0;
    if (navigator.userAgent.match(/MSIE ([0-9]+)\./)) {
        jQuery.browser.msie = true;
        jQuery.browser.version = RegExp.$1;
    }
})();
	
 
 $(document).ready(function() {
			$("#genera").button();
			$("#pbStatusInicial").button();

			inicio();
			 datos();
	});
	
	var es_mx = {
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
	
			};
		
		function inicio() {
			$('#sDataHCompromiso').val("");
			$('#sDataHContrato').val("");
			$('#sDataFR').val("");
			
			$("#rdoOriginal").prop("checked", true);
			$("#cMovto").val("O");
			$("#rdoModificado").prop("checked", false);
			
			$("#rdoOriginal").change(function() {
				if ($("#rdoOriginal").prop("checked")) {
					$("#cMovto").val("O");
					$("#rdoModificado").prop("checked", false);
					buscaComprommisosMovto();					
				}		
			});
			
			$("#rdoModificado").change(function() {
				if ($("#rdoModificado").prop("checked")) {
					$("#cMovto").val("M");
					$("#rdoOriginal").prop("checked", false);
					buscaComprommisosMovto();						
				}		
			});
			
			datosUnidadEjecutora();	
				
				$("#chkTodos").change(function() {
					if ($("#chkTodos").prop("checked")) {
						$("input:checkbox").attr('checked', 'checked');
					}else{
						$("input:checkbox").removeAttr('checked');
					}	
				});			
		}
		
		function fnClickAddRow(row) {
			
			 $('#dt_paraEnvio').dataTable().fnAddData( [					
						row.find('td:eq(1)').html()!=null?row.find('td:eq(1)').html():"",
						row.find('td:eq(2)').html()!=null?row.find('td:eq(2)').html():"",
						row.find('td:eq(3)').html()!=null?row.find('td:eq(3)').html():"",
						row.find('td:eq(4)').html()!=null?row.find('td:eq(4)').html():"",
						row.find('td:eq(5)').html()!=null?row.find('td:eq(5)').html():"",
						row.find('td:eq(6)').html()!=null?row.find('td:eq(6)').html():"",
						row.find('td:eq(7)').html()!=null?row.find('td:eq(7)').html():"",
						row.find('td:eq(8)').html()!=null?row.find('td:eq(8)').html():"",
						row.find('td:eq(9)').html()!=null?row.find('td:eq(9)').html():"",
						row.find('td:eq(10)').html()!=null?row.find('td:eq(10)').html():""
			] );
		}

		function fnClickDellRows(){
			$('#sDataHCompromiso').val("");
			$('#sDataHContrato').val("");
			$('#dt_paraEnvio').dataTable().fnClearTable();
		}

		function enviar(){
			try {
				fnClickDellRows();
				$('#dt_generados tbody tr input:checked').each(function(idx, elm){
					var caNoCompromiso = $(this).parent('td').parent('tr').find('td:eq(2)').html();
					var cIdContrato = $(this).parent('td').parent('tr').find('td:eq(3)').html();
					$('#sDataHCompromiso').val($('#sDataHCompromiso').val() + "'" + caNoCompromiso + "',");
					$('#sDataHContrato').val($('#sDataHContrato').val() + "'" + cIdContrato + "',");
					fnClickAddRow($(this).parent('td').parent('tr'));
				});

				if($('#sDataHCompromiso').val() == "" || $('#sDataHContrato').val() == ""){
					alert("No se ha seleccionado ningún registro...");
					location.reload(true);
				}

			}catch(e) {
				alert(e);
				location.reload(true);
			}
 		}

		function generar(){
			try {
				$('#dt_paraEnvio').dataTable().fnClearTable();
				$('#envioSICOP').submit();
		
			}catch(e) {
				alert(e);
			}
		}

		function cancelarCompromiso(){
			$('#dt_rechazados tbody tr input[type=button] ').click(function(event){
				var cFolioRechazado = $(this).parent('td').parent('tr').find('td:eq(1)').html();
				var caNoCompromisoR = $(this).parent('td').parent('tr').find('td:eq(2)').html();
				document.location.href='../gstnmngr/CancelaCompromisos?cF=' + cFolioRechazado + '&comp='+ caNoCompromisoR;
			});	
		}

	function Inicializa(){
		
		var vacio = true;
		
			try {
								
    		var table = document.getElementById('dt_enviados');
			var aTrs = $('#dt_enviados').dataTable().fnGetNodes();
			var cCOLUMNALLAVE = 2;
			
			$('#comp').val("");
			var token = "";
			var idList= "";
			
			for ( var i=1; i<=aTrs.length;  i++ )     
			{   
				var row= table.rows[i];
				var chkbox = row.cells[0].childNodes[0];
				
				if(null != chkbox && true == chkbox.checked)
				{ 	
					var caNoFolio = row.cells[cCOLUMNALLAVE].innerHTML;
					idList = idList + token + caNoFolio;
					token = ",";
					vacio = false;
			    } 
			}
				
			$('#comp').val( idList );
			
			if(!vacio){
				$.ajax({
					  type: "POST",
					  beforeSend : function() {
			               $.blockUI({ message: 'Procesando ...' });
			          }, 
			          complete: function () {
			           	$.unblockUI();
			          },
					  url: "../gstnmngr/CancelaCompromisos",
					  data: $('#conLayout').serialize(),
					  success: function(data) {
						  Swal.fire("OK!","Se Iniciaron los Estatus correctamente","success");
						 location.reload(true);
					  },
					  dataType: "json"
					});
			}
			else 
				Swal.fire("Seleccione","Debe marcar al menos una fila...","warning");
       	
    	}catch(e) {
     		alert(e);
		}

		
		}
	
	function datosUnidadEjecutora(){
		
		queryFormPost("validaUsuarioCentralesMAT_Read", {async: false});
		querySelectPost("tCatalogoUnidadEjecutoraReadVistas", "cboUnidadEjecutora", {async: false});
		$("#cboUnidadEjecutora").val($("#cIdUnidadEjecutoraUsuario").val());
		$("#cUR").val($("#cboUnidadEjecutora").val());
		
		if($("#isAdmin").val() == "0"){
			$("#cboUnidadEjecutora").prepend("<option value='*'> * - OFICINAS CENTRALES</option>");
			$("#cboUnidadEjecutora").prepend("<option value='**'> ** - GERENCIAS ESTATALES</option>");
		}
		buscaComprommisosMovto();
	}
	
	
	function buscaComprommisosMovto(){
		$("#cUR").val($("#cboUnidadEjecutora").val());
		llenaCompromisos();
		compromisosEnviadosSICOP();
		listaCompromisosDevueltos();
		listaCompromisosRechazados();
		compromisosIncompletos();
	}
	
			
	function compromisosEnviadosSICOP(){
				var cCentroContable = $("#cCC").val();
				var cUR = $("#cUR").val();
				var cWhereUR = "";
				var esAdmin = $("#isAdmin").val();
				var cUE_Usuario = $("#cIdUnidadEjecutoraUsuario").val();
				
				if (cUR != "*" && cUR != "**"){
					cWhereUR = " AND cUnidadResponsable = '" + cUR + "'";
				}else{
					if(cUR == "*"){
						cWhereUR =" AND cCentroContable = '"+ cCentroContable +"'";
					}else{
						cWhereUR =" AND cCentroContable <> '"+ cCentroContable +"'";
					}
				}
				
				var cMovto =  $("#cMovto").val();
				var cWhereMovto = " AND MOVTO = '" + cMovto + "'";
				var complemento = "";
				
				var tipoContrato = "";
				
				if(cUE_Usuario == "A02"){
					tipoContrato = " AND ( cTipoContrato = 'FE' OR TipoDocumento = 'Convenio de Colaboracion')";
				}else if(cUE_Usuario == "A04"){
					tipoContrato = " AND ( cTipoContrato <> 'FE' AND TipoDocumento <> 'Convenio de Colaboracion')";
				}	
				
				complemento = cWhereUR + cWhereMovto + tipoContrato;
				
				$('#dt_enviados').dataTable({
					bRetrive: true,
					bDestroy: true,
					oLanguage: es_mx,
					bServerSide: true,
					sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vListaCompromisos&qw=estadoCompromiso = 'ENVIADO A SICOP'" + encodeURIComponent(complemento),
					bProcessing: true,
					sPaginationType: "full_numbers",
					bJQueryUI: true,
					aoColumns: [
						{ sName: "idCompromiso"		},
						{ sName: "cCentroContable"		},
						{ sName: "caNoCompromiso"		},
						{ sName: "cIdContrato"			},
						{ sName: "cTipoDocumento"			},
						{ sName: "estadoCompromiso"		},
						{ sName: "fCarga"					},
						{ sName: "cRamo"					},
						{ sName: "cUnidadResponsable"		},
						{ sName: "cIdRFC"					}
					]
				});
				
				document.getElementById("dt_enviados").style.width ="98%";
			}


			function llenaCompromisos(){
				var cCentroContable = $("#cCC").val();
				var cUR = $("#cUR").val();
				var cWhereUR = "";
				var esAdmin = $("#isAdmin").val();
				var cUE_Usuario = $("#cIdUnidadEjecutoraUsuario").val();
				
				if (cUR != "*" && cUR != "**"){
					cWhereUR = " AND cUnidadResponsable = '" + cUR + "'";
				}else{
					if(cUR == "*"){
						cWhereUR =" AND cCentroContable = '"+ cCentroContable +"'";
					}else{
						cWhereUR =" AND cCentroContable <> '"+ cCentroContable +"'";
					}
				}
				var cMovto =  $("#cMovto").val();
				var cWhereMovto = " AND MOVTO = '" + cMovto + "'";
				var complemento = "";
				
				var tipoContrato = " ";
				
				if(cUE_Usuario == "A02"){
					tipoContrato = " AND (cTipoContrato = 'FE' OR TipoDocumento in ( 'Convenio de Colaboracion', 'CONVENIO DE PAGO BENEFICIARIOS'))";
				}else if(cUE_Usuario == "A04"){
					tipoContrato = " AND cTipoContrato <> 'FE' AND TipoDocumento not in ( 'Convenio de Colaboracion', 'CONVENIO DE PAGO BENEFICIARIOS')";
				}	
				
				complemento = cWhereUR + cWhereMovto + tipoContrato;
				
				$('#dt_generados').dataTable({
					bRetrive: true,
					bDestroy: true,
					oLanguage: es_mx,
					bServerSide: true,
					sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vListaCompromisos&qw=estadoCompromiso = 'CREADO'"+encodeURIComponent(complemento),
					bProcessing: true,
					sPaginationType: "full_numbers",
					bJQueryUI: true,
					aoColumns: [
						{ sName: "idCompromiso",		bSearchable: false, bSortable: false, bVisible: true},
						{ sName: "cCentroContable",		bSearchable: true, bSortable: false, bVisible: true},
						{ sName: "caNoCompromiso",		bSearchable: true, bSortable: false, bVisible: true},
						{ sName: "cIdContrato",			bSearchable: true, bSortable: false, bVisible: true},
						{ sName: "cTipoDocumento",		bSearchable: true, bSortable: false, bVisible: true},
						{ sName: "estadoCompromiso",	bSearchable: true, bSortable: false, bVisible: true},
						{ sName: "fCarga",				bSearchable: true, bSortable: false, bVisible: true},
						{ sName: "fAplicacion",			bSearchable: true, bSortable: false, bVisible: true},
						{ sName: "cRamo",				bSearchable: true, bSortable: false, bVisible: true},
						{ sName: "cUnidadResponsable",	bSearchable: true, bSortable: false, bVisible: true},
						{ sName: "cIdRFC",				bSearchable: true, bSortable: false, bVisible: true}
					]
				});
				
			}
			
			function listaCompromisosDevueltos(){
 				var cCentroContable = $("#cCC").val();
 				var cUR = $("#cUR").val();
				var cWhereUR = "";
				var cUE_Usuario = $("#cIdUnidadEjecutoraUsuario").val();
				
				if (cUR != "*" && cUR != "**"){
					cWhereUR = " AND cUnidadResponsable = '" + cUR + "'";
				}else{
					if(cUR == "*"){
						cWhereUR =" AND cCentroContable = '"+ cCentroContable +"'";
					}else{
						cWhereUR =" AND cCentroContable <> '"+ cCentroContable +"'";
					}
				}
				var cMovto =  $("#cMovto").val();
				var cWhereMovto = " AND MOVTO = '" + cMovto + "'";
				var complemento = "";
				
				var tipoContrato = "";
				
				if(cUE_Usuario == "A02"){
					tipoContrato =  " AND ( cTipoContrato = 'FE' OR TipoDocumento = 'Convenio de Colaboracion') ";
				}else if(cUE_Usuario == "A04"){
					tipoContrato = tipoContrato = " AND ( cTipoContrato <> 'FE' AND TipoDocumento <> 'Convenio de Colaboracion')";
				}	
				
				complemento = cWhereUR + cWhereMovto + tipoContrato;
				
    			$('#dt_devueltos').dataTable({
					"bRetrive" : true,
					"bDestroy" : true,
					"bPaginate" : true,
					"bLengthChange" : true,
					"bServerSide" : true,
					sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vListaCompromisos&qw=estadoCompromiso = 'ACEPTADO'" +encodeURIComponent(complemento),
					bProcessing: true,
					sPaginationType: "full_numbers",
					bJQueryUI: true,
					aoColumns: [
						{ sName: "cCentroContable",			bSearchable: false,	bSortable: false, bVisible: true},
						{ sName: "caNoCompromiso",			bSearchable: true,	bSortable: true, bVisible: true},
						{ sName: "cIdContrato",				bSearchable: true,	bSortable: true, bVisible: true},
						{ sName: "cTipoDocumento",			bSearchable: false,	bSortable: false, bVisible: true},
						{ sName: "estadoCompromiso",		bSearchable: false,	bSortable: false, bVisible: true},
						{ sName: "fCarga",					bSearchable: false,	bSortable: false, bVisible: true},
						{ sName: "cRamo",					bSearchable: false,	bSortable: false, bVisible: true},
						{ sName: "cUnidadResponsable",		bSearchable: false,	bSortable: false, bVisible: true},
						{ sName: "cIdRFC",					bSearchable: true,	bSortable: true, bVisible: true},
						{ sName: "nfolioautSICOP",			bSearchable: false,	bSortable: false, bVisible: true}
					],
					 oLanguage: es_mx,
 				});
 				
 				document.getElementById("dt_devueltos").style.width = "98%";
 			}
 			
 			function listaCompromisosRechazados(){
				var cCentroContable = $("#cCC").val();
				var cUR = $("#cUR").val();
				var cWhereUR = "";
				//var esAdmin = $("#isAdmin").val();
				var cUE_Usuario = $("#cIdUnidadEjecutoraUsuario").val();
				
				if (cUR != "*" && cUR != "**"){
					cWhereUR = " AND cUnidadResponsable = '" + cUR + "'";
				}else{
					if(cUR == "*"){
						cWhereUR =" AND cCentroContable = '"+ cCentroContable +"'";
					}else{
						cWhereUR =" AND cCentroContable <> '"+ cCentroContable +"'";
					}
				}
				var cMovto =  $("#cMovto").val();
				var cWhereMovto = " AND MOVTO = '" + cMovto + "'";
				var complemento = "";
				
				var tipoContrato = "";
				
				if(cUE_Usuario == "A02"){
					tipoContrato =  " AND ( cTipoContrato = 'FE' OR TipoDocumento = 'Convenio de Colaboracion') ";
				}else if(cUE_Usuario == "A04"){
					tipoContrato =  " AND ( cTipoContrato <> 'FE' AND TipoDocumento <> 'Convenio de Colaboracion')";
				}	
				
				complemento = cWhereUR + cWhereMovto + tipoContrato;
				
				$('#dt_rechazados thead tr td:eq(0)').click();
				$('#dt_rechazados').dataTable({
					bRetrive: true,
					bDestroy: true,
					oLanguage:es_mx,
					bServerSide: true,
					sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vListaCompromisos&qw=estadoCompromiso = 'RECHAZADO'" + complemento,
					bProcessing: true,
					sPaginationType: "full_numbers",
					bJQueryUI: true,
					aoColumns: [
						{ sName: "cCentroContable",			bSearchable: false,	bSortable: false, bVisible: true},
						{ sName: "cFolio",					bSearchable: false,	bSortable: false, bVisible: true},
						{ sName: "caNoCompromiso",			bSearchable: true,	bSortable: true, bVisible: true},
						{ sName: "cIdContrato",				bSearchable: true,	bSortable: true, bVisible: true},
						{ sName: "cTipoDocumento",			bSearchable: false,	bSortable: false, bVisible: true},
						{ sName: "estadoCompromiso",		bSearchable: false,	bSortable: false, bVisible: true},
						{ sName: "fCarga",					bSearchable: false,	bSortable: false, bVisible: true},
						{ sName: "cRamo",					bSearchable: false,	bSortable: false, bVisible: true},
						{ sName: "cUnidadResponsable",		bSearchable: false,	bSortable: false, bVisible: true},
						{ sName: "cIdRFC",					bSearchable: false,	bSortable: false, bVisible: true},
						{ sName: "cancelar",				bSearchable: false,	bSortable: false, bVisible: true}
					]
				});
				
				document.getElementById("dt_rechazados").style.width = "98%";
			}
			
			function compromisosIncompletos(){
				var cCentroContable = $("#cCC").val();
				var cUR = $("#cUR").val();
				var cWhereUR = "";
				//var esAdmin = $("#isAdmin").val();
				
				if (cUR != "*" && cUR != "**"){
					cWhereUR = " AND cUnidadResponsable = '" + cUR + "'";
				}else{
					if(cUR == "*"){
						cWhereUR =" AND cCentroContable = '"+ cCentroContable +"'";
					}else{
						cWhereUR =" AND cCentroContable <> '"+ cCentroContable +"'";
					}
				}
				var cMovto =  $("#cMovto").val();
				var cWhereMovto = " AND MOVTO = '" + cMovto + "'";
				var complemento = "";	
				
				complemento = cWhereUR + cWhereMovto;
				
				$('#dt_incompletos').dataTable({
					bRetrive: true,
					bDestroy: true,
					oLanguage: es_mx,
					bServerSide: true,					
					sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vListaCompromisosIncompletos&qw=estadoCompromiso = 'CREADO'" + complemento,
					bProcessing: true,
					bJQueryUI: true,
					aoColumns: [
						{ sName: "cFolio",		bSearchable: false,	bSortable: false, bVisible: true},						
						{ sName: "caNoCompromiso",			bSearchable: false,	bSortable: false, bVisible: true},
						{ sName: "cIdContrato",				bSearchable: false,	bSortable: false, bVisible: true},						
						{ sName: "cUnidadResponsable",		bSearchable: false,	bSortable: false, bVisible: true},
						{ sName: "cIdRFC",					bSearchable: false,	bSortable: false, bVisible: true},
						{ sName: "NoProcedimientoCNET",		bSearchable: false,	bSortable: false, bVisible: true},
						{ sName: "CodContratoCNET",			bSearchable: false,	bSortable: false, bVisible: true},
						{ sName: "CodExpedienteCNET",		bSearchable: false,	bSortable: false, bVisible: true}
					]
				});
				
				document.getElementById("dt_incompletos").style.width = "98%";
			}
			
			
    	function checkSeleccionaTodos(){
				
				$("#chkSelTodos").change(function() {
					if ($("#chkSelTodos").prop("checked")) {
						$("input:checkbox").attr('checked', 'checked');
					}else{
						$("input:checkbox").removeAttr('checked');
					}	
				});
			}
		function datos(){
			 $('#dt_paraEnvio').dataTable(
			{
				"bPaginate" : false,
				"bLengthChange" : true,
				"bAutoWidth" : true,
				"bJQueryUI" : true,
				"bRetrive" : true,
				"bDestroy" : true,
				"bServerSide" : false,
				oLanguage : es_mx
			});
		}