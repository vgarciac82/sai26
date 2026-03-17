<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	if(usuario == null ){
		response.sendRedirect( "../index.jsp" );
		return;
	}
	
	String employeeRegistration = usuario.getLogin();
	String employeeAdministrator = usuario.getNumeroEmpleado();
%>
<!DOCTYPE html>
<html>
<head>
 

<!-- Estilos estandar para los controles JQuery -->
<link rel="stylesheet" type="text/css" href="../css/datepickercontrol_bluegray.css" />
<link rel="stylesheet" type="text/css" href="../Ayudas/css/autocompleta.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_page.css"></link>

<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>

<script type="text/javascript" src="../Generador/js/crud.js"></script>
<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
<script type="text/javascript" src="../js/jsquery.js"></script>
<script type="text/javascript" src="../Generador/js/validaciones.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.dataTables.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"></script>
<script type="text/javascript" src="../Generador/js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.ui.datepicker.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.ui.tabs.js"></script>
<script type="text/javascript" src="../js/catalogo/general.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.all.js"></script>
<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
<script src="js/SICOVECommons.js"></script>
<script src="js/ContractCommons.js"></script>
<script type="text/javascript" src="js/FuelContract.js"></script>
<script type="text/javascript" src="js/WalletContractAsignation.js"></script>
<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>

<script type="text/javascript">
	let accountsDataTable;
	
	$(document).ready(function() {
		
		$("input.AyudaSyC").subIniciaDlg();
		
		$("#cIdContratoCompromiso").change(function(){getSAIContractInfo()});
		
		$("#saveContract").button().click(function(){
			saveContract($("#employeeRegistration").val());
		});
		
		$("#updateContract").button().click(function(){
			updateContract();
		});
		
		$("#addBtn").button().click(function(){
			addContractAccount($("#employeeRegistration").val());
		});

		$("#updateBtn").button().click(function(){
			updateFuelContractAccount();
		});
		
		
			
	});
	
	const updateContract = function(){
		if ($("#byLiters").is(":checked")) {
			$("#isByLiters").val("1"); 
		} else {
			$("#isByLiters").val("0");
		}
		
		if ($("#active").is(":checked")) {
			$("#contractActive").val("1"); 
		} else {
			$("#contractActive").val("0");
		}
		
		
		queryFormPost({
			queryName:"updateFuelContract",
			async:true,
			callback:function(){
				Swal.fire({
		            title: 'Operacion Exitosa',
		            text: 'El contrato se actualizo exitosamente.',
		            icon: 'success',
		            confirmButtonText: 'Aceptar'
		        });
			}
		});

	}
	
	const getSAIContractInfo = function (){
		
		queryFormPost({
			queryName:"fuelContractRead",
			async:true,
			callback: function(){
				cleanAccountInfo();
				if( $("#idFuelContract").val() != "0" ){
					fuelContract.id= $("#idFuelContract").val();
					$("#saveContract").hide();
					$("#updateContract").show();
					
					document.getElementById("byLiters").checked = ($("#isByLiters").val() == "1")
					document.getElementById("active").checked = ($("#contractActive").val() == "1");
					
					$(".captured").each(function(){
						$(this).show();
					});
					getDataTable($("#idFuelContract").val());
				}else{
					fuelContract.id = 0;
					$(".captured").each(function(){
						$(this).hide();
					});
					$("#saveContract").show();
					$("#updateContract").hide();
					document.getElementById("byLiters").checked = ($("#isByLiters").val() == "1")
					document.getElementById("active").checked = ($("#contractActive").val() == "1");
					
				}
			}
		});
	}
	
	const getDataTable = function(idContract){
		let condition = '1<>1';
		
		if( idContract )
			condition = "id="+idContract;
		
		accountsDataTable = null;	
		accountsDataTable = $("#tblCuentas").dataTable({

			fnServerData : function(sSource, aoData, fnCallback) {
				$.ajax({
					"dataType" : 'json',
					"type" : "POST",
					"url" : sSource,
					"data" : aoData,
					"success" : fnCallback
				});
			},
			"bRetrive" : true,
			"bDestroy" : true,
			bInfo:true,
			bJQueryUI:true,
			bServerSide: true,
			bAutoWidth:true,
			sAjaxSource : window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vFuelContracAccounts&qw=" + encodeURI(condition),
			"aoColumnDefs": [ 
				{
					"fnRender": function ( oObj ) {
						return '<a href="#" onclick="editFuelContractAccount(' + oObj.aData[4] + ');return false"><img src="../Generador/imagenes/edit.png" alt="Editar Cuenta"></a>';
					},
					"aTargets": [ 13 ]
				},
				{ "bVisible": false,  "aTargets": [ 0,1,2,3,4,5,6,7,8 ] } ,
				{ "bSortable": false,  "aTargets": [ 0,1,2,3,4,5,6,7,8,9,10,11,12,13 ] },
				{ "bSearchable": false, "aTargets": [ 0,1,2,3,4,5,6,7,8,13 ] }
			],
			aoColumns : [
				{sName : "id"},
				{sName : "contract_number"},
				{sName : "is_by_liters"},
				{sName : "active"},
				{sName : "id_account"} ,
				{sName : "employeeNumber"} ,
				{sName : "position"} ,
				{sName : "id_unit"} ,
				{sName : "executiveUnit"} ,
				{sName : "accountNumber"} ,
				{sName : "organizationUnitName"} ,
				{sName : "employeeResponsibleName"} ,
				{sName : "monthlyAsignation"},
				{sName : "'' AS cmd"}
			]		
		}) ;
	}
	
	const editFuelContractAccount = function(idAccount){
		cleanAccountInfo();
		$("#idAccount").val(idAccount);
		queryFormPost({
			queryName:"contractAccountRead",
			async:false,
			callback: function(){
				$("#updateBtn").show();
				$("#addBtn").hide();
			},
		})
	} 

	const updateFuelContractAccount = function(){
		queryFormPost({
			queryName:"updateFuelContractAccount",
			async:false,
			callback: function(){
				Swal.fire({
		            title: 'Operacion Exitosa',
		            text: 'La cuenta se actualizo exitosamente.',
		            icon: 'success',
		            confirmButtonText: 'Aceptar'
		        });
				getDataTable($("#idFuelContract").val());
				$("#updateBtn").hide();
				$("#addBtn").show();
				cleanAccountInfo();
			},
		});
	} 
	
</script>

</head>

<body id="dt_example" bgColor="red">
	<form action="FuelContract" method="post" id="mainFrm">
		<input type="hidden" name ="employeeAdministrator" id="employeeAdministrator" value="<%=employeeAdministrator%>">
		<input type="hidden" name ="employeeRegistration" id="employeeRegistration" value="<%=employeeRegistration%>">
		<input type="hidden" name ="isByLiters" id="isByLiters" value="">
		<input type="hidden" name ="contractActive" id="contractActive" value="">
		<div id="container" class="container" style="width: 75%">
		
			<h1> Asignacion de contratos</h1>
			<div id="generales-contrato">
				<fieldset>
					<legend>Contrato</legend>
					<table align="center">
						<tr>
							<td align="right">
								Contrato N&uacute;mero: 
							</td>
							<td align="left" colspan="3">
								<input type="text" size="64" readonly="readonly" class="readonly AyudaSyC" name="cIdContratoCompromiso" id="cIdContratoCompromiso">
								<input type="hidden" name="idFuelContract" id="idFuelContract" value="0">
							</td>	
						</tr>
						<tr>
							<td align="right">
								Beneficiario: 
							</td>
							<td align="left">
								<input type="text" size="25" readonly="readonly" class="readonly" name="beneficiario" id="beneficiario">
							</td>
							<td align="right">
								Monto Total: 
							</td>
							<td align="left">
								<input type="text" size="12" readonly="readonly" class="readonly" name="totalMaxAmount" id="totalMaxAmount">
							</td>
									
						</tr>
						<tr>
							<td align="right">
								Fecha Inicio: 
							</td>
							<td align="left">
								<input type="text" size="12" readonly="readonly" class="readonly" name="startDate" id="startDate">
							</td>
							<td align="right">
								Fecha Fin: 
							</td>
							<td align="left">
								<input type="text" size="12" readonly="readonly" class="readonly" name="endDate" id="endDate">
							</td>
									
						</tr>
						
						<tr>
							<td align="left" colspan="4">
								Concepto: 
							</td>
						</tr>
						<tr>
							<td align="left" colspan="4">
								<textarea rows="6" cols="80" readonly="readonly" id="contractConcept"></textarea>
							</td>
						</tr>
						
						<tr>
							<td align="left" colspan="2">
								<input type="checkbox" id="byLiters" name="byLiters" ><label for="byLiters">Contrato medido en Litros</label>
							</td>
							<td align="left" colspan="2">
								<input type="checkbox" id="active" name="active" value="true"><label for="active">Contrato Activo</label>
							</td>
						</tr>
						
						<tr>
							<td align="center" colspan="4">
								<input type="button" id="saveContract" value="Guardar">
								<input type="button" id="updateContract" style="display: none" value="Actualizar">
							</td>
						</tr>
					</table>
				</fieldset>
			</div>
			
			
			<div id="asignacion" class="captured" style="display: none">
				<fieldset>
					<legend>Asignación de Cuentas</legend>
					
					<input type="hidden"   name="idAccount" id="idAccount" class="accountInfo">
					
					<table align="center">
						<tr>
							<td align="right">
								Unidad Responsable: 
							</td>
							<td align="left" colspan="3">
								<input type="text" size="64" readonly="readonly" class="readonly AyudaSyC accountInfo" name="organizationUnitName" id="organizationUnitName">
								<input type="hidden" size="10" name="idUnit" id="idUnit" class="accountInfo">
								<input type="hidden" size="10" name="executiveUnit" id="executiveUnit" class="accountInfo">
							</td>	
						</tr>
						<tr>
							<td align="right">
								Servidor Publico Responsable: 
							</td>
							<td align="left" colspan="3">
								<input type="text" size="64" readonly="readonly" class="readonly AyudaSyC accountInfo" name="employeeResponsibleName" id="employeeResponsibleName">
								<input type="hidden" name="employeeNumber" id="employeeNumber">
							</td>
						</tr>
						
						<tr>
							<td align="right">
								Cargo: 
							</td>
							<td align="left" colspan="3">
								<input type="text" size="64" readonly="readonly"  name="position" id="position" class="accountInfo">
							</td>
						</tr>
						
						<tr>
							<td align="right">
								Numero de Cuenta: 
							</td>
							<td align="left">
								<input type="text" size="18" name="accountNumber" id="accountNumber"  class="accountInfo">
							</td>
							
							<td align="right">
								Asignacion Mensual: 
							</td>
							<td align="left">
								<input type="text" size="12"  name="monthlyAsignation" id="monthlyAsignation" class="accountInfo">
							</td>
						</tr>
						
						<tr>
							<td align="center" colspan="4">
								<input type="button" value="Agregar" id="addBtn">
								<input type="button" value="Actualizar" id="updateBtn" style="display: none">
							</td>
									
						</tr>
					</table>
				</fieldset>
			</div>
			
			<div id="container" class="captured" style="display: none">
				<fieldset>
					<legend>Cuentas Asignadas</legend>
					
					<table id="tblCuentas" align="center" width="100%">
						<thead>
							<tr>
								<th>id</th>
								<th>contractNumber</th>
								<th>isByLiters</th>
								<th>active</th>
								<th>idAccount</th>
								<th>employeeNumber</th>
								<th>position</th>
								<th>idUnit</th>
								<th>executiveUnit</th>
								<th width="18%">
									Cuenta
								</th>
								<th width="29%">
									Unidad
								</th>
								<th  width="32%">
									Responsable
								</th>
								<th  width="17%">
									Asignacion Mensual
								</th>
								<th  width="4%">
									&nbsp;
								</th>
							</tr>
						</thead>
						<tbody></tbody>
					</table>
				</fieldset>
				
			</div>
			
		</div>
	</form>
</body>

</html>