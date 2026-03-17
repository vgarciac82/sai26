<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="com.syc.obrapublica.EjercicioFiscalBusinessLogic"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Caso"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%

String formName = request.getParameter("formName")==null || "".equals(request.getParameter("formName"))? "formPagos":request.getParameter("formName");
String idEmpleadoStr = StringUtils.trimToEmpty( request.getParameter("idEmpleado") );

%>
<!-- Estilos estandar para los controles JQuery -->
		<link rel="stylesheet" type="text/css"  href="css/demo_table_jui.css"></link>
		<link rel="stylesheet" type="text/css"	href="css/jquery-ui.min.css"></link>
		<link rel="stylesheet" type="text/css"	href="css/datatables.min.css"></link>
		<link rel="stylesheet" type="text/css"	href="css/bootstrap.min.css"></link>
		<link rel="stylesheet" type="text/css"	href="css/sweetalert2.min.css"></link>
		<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
		<script type="text/javascript" src="js/jquery.dataTables.min.js"></script>
		<script type="text/javascript" src="js/bootstrap.min.js"></script>
		<script type="text/javascript" src="js/Moment.js"></script>
		<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
		<script type="text/javascript" src="js/crud.js"></script>
		<script type="text/javascript" src="js/sweetalert2.all.min.js"></script>
		<script type="text/javascript">
			var formName ="<%=formName%>";	
			var idEmpleado = <%="".equals( idEmpleadoStr )?"0":idEmpleadoStr%>;
			
			$(document).ready(function () {
				
				if( idEmpleado == 0 ){
					var idEmp = "";
					eval( "idEmp = window.opener."  + formName + ".noEmpleadoRFC.value");
					$("#idEmpleado").val(idEmp); 
				}else{
					$("#idEmpleado").val(idEmpleado);
				}
				
				$("#btn_aceptar").button();
				$("#btn_cerrar").button();
				
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
				
				sWhere ="nIdEmpleado =" + $("#idEmpleado").val();
				oTable = $("#dt_generados").dataTable({
					"bPaginate" : false,
					"bFilter" : false,
					"bSort" : true,
					"bInfo" : false,
					"bJQueryUI" : true,
					"bRetrive" : true,
					"bDestroy" : true,
					"bServerSide" : true,
					sAjaxSource : window.location.protocol + "//"
						+ window.location.host + "/"
						+ window.location.pathname.split("/")[1]
						+ "/crud?rt=t&ql=v_listaComisiones&qw=" + sWhere,
					oLanguage: es_mx,
					aoColumns: [
						{ sName: "idAgenda",	bSearchable: false,	bSortable: false, bVisible: true },
						{ sName: "motivoComision" },
						{ sName: "fechaInicio" },
						{ sName: "fechaFin" },
						{ sName: "pais" },
						{ sName: "entidad" },
						{ sName: "municipio",	bSearchable: false,	bSortable: false, bVisible: true  }
						
					],
                	aaSorting: [[ 1, "asc" ]] 
	        	});
				
						
				$("#dt_generados tbody").click(function(event) {
						$(oTable.fnSettings().aoData).each(function (){

							$(this.nTr).removeClass('table-secondary');
						});
						$(event.target.parentNode).addClass('table-secondary');
						
						var aPos = oTable.fnGetPosition(event.target.parentNode);
			     		var aData = oTable.fnGetData(aPos);
						$("#campoIdAgenda").val(aData[0]);
						$("#campoMotivo").val(aData[1]);
						$("#campoFechaIni").val(aData[2]);
						$("#campoFechaFin").val(aData[3]);
						
						$("#campoPais").val(aData[4]);
						$("#campoEstado").val(aData[5]);
						$("#campoMunicipio").val(aData[6]);
						
				});
				
				//consultaAgenda();
				
			});
			
			
			function consultaAgenda() {
				$.ajax({
						url : '../viaticos/consultaAgenda',
						dataType : 'json',
						type :"GET",
						data : {
							"idEmpleado": $("#idEmpleado").val()
						},
						dataType:"json",
						async : false,
						success : function(agenda) {
							 let datos = agenda.agenda;
							 
							 if(datos){
					                var len = datos.length;
					                var txt = "";
					                if(len > 0){
					                    for(var i=0;i<len;i++){
					                    	$('#dt_generados').dataTable().fnAddData
					                    				([ 
					                    				datos[i].idAgenda, 
					                    				datos[i].motivoComision,
					                    				datos[i].fechaInicio,
					                    				datos[i].fechaFin,
					                    				datos[i].pais,
					                    				datos[i].entidad,
					                    				datos[i].municipio
					                    				]);			
					    				} 
					                }
					            }
						},
						error : function (err){
							Swal.fire("Ocurrio el siguiente error:", err.responseText, "error" );
								
							return false;}
					});
			}	
			
			function cerrar(){
				window.close();
			}
			
			function aceptar(){
				eval( 'window.opener.' + formName  + '.idAgenda.value=window.formAgenda.campoIdAgenda.value' );
				eval( 'window.opener.' + formName  + '.cConcepto.value=window.formAgenda.campoMotivo.value' );
				eval( 'window.opener.' + formName  + '.fechaIniAgenda.value=window.formAgenda.campoFechaIni.value' );
				eval( 'window.opener.' + formName  + '.fechaFinAgenda.value=window.formAgenda.campoFechaFin.value' );
				
				window.opener.$('#pais').val( $("#campoPais").val() );
				window.opener.$('#estado').val($("#campoEstado").val());
				window.opener.$('#municipio').val($("#campoMunicipio").val());
				
				window.close();
			}
		
		</script>
		<div id="container" style="width: 100%">
			<form id="formAgenda" name="formAgenda">
			<div id="tblComisiones" >
				<input type="hidden" value="" name="campoIdAgenda" id="campoIdAgenda"/>
				<input type="hidden" value="" name="campoMotivo" id="campoMotivo"/>
				<input type="hidden" value="" name="campoFechaIni" id="campoFechaIni"/>
				<input type="hidden" value="" name="campoFechaFin" id="campoFechaFin"/>
				<input type="hidden" value="" name="campoPais" id="campoPais"/>
				<input type="hidden" value="" name="campoEstado" id="campoEstado"/>
				<input type="hidden" value="" name="campoMunicipio" id="campoMunicipio"/>
				<input type="hidden" id="idEmpleado" name="idEmpleado" value=""/>
				<h4>Comisiones generadas</h4>
				<table id="dt_generados" class="table table-striped table-hover">
					<thead>
						<tr align="center">
								<th> #</th>
								<th> Nombre de la comisión </th>
								<th> F.Inicio </th>
								<th> F.Fin </th>
								<th> Pais </th>
								<th> Estado </th>
								<th> Municipio </th>
						</tr>
					</thead>
					<tbody id="respuesta"></tbody>
				</table>
		        <table align="center">
		        		<tr>
				        	<td>
				        		<input type="button" id="btn_aceptar" name="btn_aceptar" value="Aceptar" onclick="aceptar()" class="btn btn-secondary"/>
				        	</td>
				        	<td>
				        		<input type="button" id="btn_cerrar" name="btn_cerrar" value="Cancelar" onclick="cerrar()" class="btn btn-secondary"/>
				        	</td>
			        	</tr>
		        </table>
			</div>
		  </form>
		</div>