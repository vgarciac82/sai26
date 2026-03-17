<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%
	String cUR = "";
	String cRamo = "";
	String cCentroContable="";

	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	boolean mntoCuentas=false;
	if(usuario.getPropiedades()!=null&&usuario.getPropiedades().containsKey("CCENTROCONTABLE")){
		mntoCuentas = "10".equals(usuario.getPropiedad("CCENTROCONTABLE").getValor());
		cCentroContable= usuario.getPropiedad("CCENTROCONTABLE").getValor();
	}

	cUR = usuario.getU_UR();
	cRamo = usuario.getU_Ramo();

	
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<title>Consulta de Ingresos / Egresos</title>

		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">
		<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
		<meta http-equiv="description" content="This is my page">

		<link rel="shortcut icon" type="image/ico" href="imagenes/favicon.ico" />

		<style type="text/css" title="currentStyle">
			@import "themes/smoothness/jquery-ui-1.8.4.custom.css";
			@import "css/demo_table_jui.css";
			@import "css/demo_page.css";
		</style>
		
		<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
		<script type="text/javascript" src="js/jquery.dataTables.js"></script>
		<script type="text/javascript" src="js/jquery.form-2.94.js"></script>
		<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
		<script type="text/javascript" src="js/jquery.ui.datepicker.js"></script>
		<script type="text/javascript" src="js/jquery.ui.core.js"></script>
		<script type="text/javascript" src="js/jquery.ui.widget.js"></script>
		<script type="text/javascript" src="js/jquery.ui.tabs.js"></script>
		<script type="text/javascript" src="js/crud.js"></script>
		<script type="text/javascript" src="../js/catalogo/general.js"></script>
		<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
 		<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>

<script type="text/javascript" charset="utf-8">
		var oTable;
			
		$(document).ready(function() {
			
		    $("#cUnidadResponsable").val( "<%=cUR%>" );
		
			$("#tblIngresosEgresos tbody").click(function(event) {
					$(oTable.fnSettings().aoData).each(function (){
						$(this.nTr).removeClass('row_selected');
					});
					$(event.target.parentNode).addClass('row_selected');
				});


/*     * Insert a 'details' column to the table     */    
	  
	  var nCloneTh = document.createElement( 'th' );    
	  var nCloneTd = document.createElement( 'td' );    
	  nCloneTd.innerHTML = '<img src="../imagenes/icono_word.png">';    
	  nCloneTd.className = "center";        
	  
	  $('#tblIngresosEgresos thead tr').each( function () {        
	  		this.insertBefore( nCloneTh, this.childNodes[0] );    
	  } );         
	  
	  $('#tblIngresosEgresos tbody tr').each( function () {       
	  		 this.insertBefore(  nCloneTd.cloneNode( true ), this.childNodes[0] );    
	  } );         
	  
	  /*     * Initialse DataTables, with no sorting on the 'details' column     */    
	  
	  var oTable = $('#tblIngresosEgresos').dataTable({  
	  
	  		bSortClasses: false,
			bLengthChange: false,
        	bFilter: false,
        	bProcessing: true,
			bJQueryUI: true,
			bAutoWidth: false,
			bRetrive : true,
			bDestroy : true,
			bPaginate: false,
			sScrollY: "500px",
			sScrollX: "1000px",
			aoColumnDefs: [ { "bSortable": false, "aTargets": [ 1 ], "sClass":"center", "sWidth":"10" },  
		  					{ "bSortable": false, "aTargets": [ 2 ], "sClass":"center", "sWidth":"10" },
		  					{ "bSortable": false, "aTargets": [ 3 ], "sClass":"center", "sWidth":"50" },
		  					{ "bSortable": false, "aTargets": [ 4 ], "sClass":"center", "sWidth":"20" },
		  					{ "bSortable": false, "aTargets": [ 5 ], "sClass":"center", "sWidth":"20" },
		  					{ "bSortable": false, "aTargets": [ 6 ], "sClass":"center", "sWidth":"15","bVisible":false },
		  					{ "bSortable": false, "aTargets": [ 7 ], "sClass":"center", "sWidth":"15" }
		  					
		  					//{"sClass": "dt-right", "aTargets": ['dt-right']}
		  	],       
		  	aaSorting: [[1, 'asc']]   
	  
	  });   
	  
	  $('#tblIngresosEgresos tbody td img').live('click', function () {   
	
			var nTr = this.parentNode.parentNode; 
			var n = oTable.fnGetNodes();
			
			if ( this.src.match('flechaAzul1.png') ) {
			
				this.src = "../imagenes/flechaAzul2.png"; 
				mostrarDetalle(oTable, nTr) 
			
			}else if(this.src.match('flechaAzul2.png')){
				this.src = "../imagenes/flechaAzul1.png";   
				oTable.fnClose( nTr );  
			
			}else{
				
				imprimir(oTable, nTr);
			}
	});
	
			
			$(this).ajaxForm({
				dataType:  "json",
				success: formSubmited
			});
			
			$('#pbFiltrar').button().click( function() {  datosIngresoEgresos();  });
			
			$("#pbLimpiar").button().click(function() {

					$("#fFechaInicio").val(""); 
					$("#fFechaFin").val(""); 
					$("#szTemp").val( "" );
					$("#folioCierre").val( "" );
					
					$("#tblIngresosEgresos").dataTable().fnClearTable();
					
			});			
			
			$("#pbExcel")
				.button()
				.click(function() {
					if( $("#szTemp").val() != "" )
						document.ExportarForm.submit();

				});	
	
			$("#pbPdf")
				.button()
				.click(function() {
					if ( $("#szTemp").val() != "" ){
						window.open(
							"../admin/SeguridadCatalogos?"
								+ "catalogo=ANEXO"
								+ "&accion=run"
								+ "&rn=RepConsultaIngresosEgresos.jasper"
								+ "&whereFolio=" + $("#szTemp").val(),  
							"Anexo",
							"scrollbars=1, resizable=yes, width=1024, height=768");
					}

				});	
		});
		
		function fnFormatDetails ( oTable, nTr )
		{
			
		    var aData = oTable.fnGetData( nTr );
		    var sOut = '<table cellpadding="5" cellspacing="0" border="0" style="padding-left:50px;">';
		    sOut += '<tr><td>Rendering engine:</td><td>'+aData[1]+' '+aData[4]+'</td></tr>';
		    sOut += '<tr><td>Link to source:</td><td>Could provide a link here</td></tr>';
		    sOut += '<tr><td>Extra info:</td><td>And any further details here (images etc)</td></tr>';
		    sOut += '</table>';
		     
		    return sOut;
		}
		


		$(function() {
			$( "#fFechaInicio" ).datepicker({
				showOn: "button",
				buttonImage: "images/calendar.gif",
				buttonImageOnly: true
			});
		});
	
		
		$(function() {
			$( "#fFechaFin" ).datepicker({
				showOn: "button",
				buttonImage: "images/calendar.gif",
				buttonImageOnly: true
			});
		});
	
		
		function formSubmited() {
                alert("Beneficiario enviado!");
            }
		
		
		function fnGetSelected( oTableLocal )
			{
				var aReturn = new Array();
				var aTrs = oTableLocal.fnGetNodes();
				
				for ( var i=0 ; i<aTrs.length ; i++ )
				{
					if ( $(aTrs[i]).hasClass('gradeA') )
					{
						aReturn.push( aTrs[i] );
					}
				}
				return aReturn;
			}

			function foco(elemento) {
			 elemento.style.border = "1px solid #FF0000";
			 }
			
			 function no_foco(elemento) {
			 elemento.style.border = "1px solid #CCCCCC";
			 }
		
function datosIngresoEgresos(){

	$("#esperar").attr("style","visibility=visible");
	$("#tblIngresosEgresos").dataTable().fnClearTable();
	var szTemp = " cEvento != '800_50_27A' ";
					
	if($("#fFechaInicio").val() != "" && $("#fFechaFin").val() != ""){
						
			var fI = $("#fFechaInicio").val().split("/");
			var fIni = fI[2]+"-"+fI[1]+"-"+fI[0];
						
			var fF = $("#fFechaFin").val().split("/");
			var fFin = fF[2]+"-"+fF[1]+"-"+fF[0];
						
			szTemp += " fAplicacion BETWEEN '"+$("#fFechaInicio").val()+"' AND '"+$("#fFechaFin").val()+"'";
					
	}if($("#folioCierre").val() != ""){
						
			if(szTemp != ""){ szTemp += " AND "; }
			szTemp += " nFolioRDB = "+$("#folioCierre").val(); 
	}
	
	if(szTemp != ""){
		
		szTemp = " WHERE "+szTemp;
	}
	
	$("#szTemp").val(szTemp);
 	//if (szTemp != "") szTemp = "&qw=" + szTemp;
    		
 	//var camposWhere = szTemp +" GROUP BY fAplicacion, idRdbCargaArchivoRDBSaldo, cUnidadResponsable, aEjercicioFiscal ";
	var camposWhere = szTemp;
	
	 $.getJSON("../catalogos/SelectJson.jsp",{Tabla: "V_RDBCARGAARCHIVORDB", Campos:camposWhere, Param:"", MaxReg: "5", ajax: 'true'}, function(j){
			
				for (var i = 0; i < j.length; i++) {
												
					$("#tblIngresosEgresos").dataTable().fnAddData( [ '<td><img src="../imagenes/flechaAzul1.png"></td>', 
																	  
																		j[i].Col0, j[i].Col1, j[i].Col2, j[i].Col3, j[i].Col4, j[i].Col5,
																		'<img src="../images/imprimir.gif">'
																	] );
				}
				
				document.getElementById("esperar").style.visibility = 'hidden';
		});	

} 

function mostrarDetalle(oTable, nTr){

		$("#esperar").attr("style","visibility=visible");
	
		var aData = oTable.fnGetData( nTr );
		
		var campos = " WHERE nFolioRDB = "+ aData[1] + " AND cEvento != '800_50_27A' ORDER BY tipoMovimiento DESC ";
		var unidadSicop = "";
		var partidaSicop = "";
		var mil = "";
		var dosMil = "";
		var tresMil = "";
		var cuatroMil = "";
		var cincoMil = "";
		var seisMil = "";
		var sOut = '<table border="1px" style="padding-left:100px;width:100%" >';
				
		$.getJSON("../catalogos/SelectJson.jsp",{Tabla: "V_RDBCARGAARCHIVORDB_DET", Campos:campos, Param:"", MaxReg: "5", ajax: 'true'}, function(j){
			
				sOut += '<tr><th width="60px">Registro</th><th width="50px">Folio Sai</th><th width="50px">Banco</th><th width="15px">-</th><th width="15px">Cuenta</th><th width="35px">Referencia</th><th width="20px">Movimiento</th><th width="45px">Concepto</th><th width="15px">Beneficiario</th><th width="25px">Importe</th><th width="20px">Tipo Movimiento</th></tr>';
			
				for (var i = 0; i < j.length; i++) {
										
					fRegistro = j[i].Col0;
					fSai = j[i].Col1;
					banco = j[i].Col2;
					tipoBancomer = j[i].Col3;
					cuenta = j[i].Col4;
					referencia = j[i].Col5;
					movimiento = j[i].Col6;
					concepto = j[i].Col7;
					beneficiario = j[i].Col8;
					importe = j[i].Col9;
					tipoMovimiento = j[i].Col10;
						
					sOut += '<tr><th width="60px">'+fRegistro+'</th><th width="50px">'+fSai+'</th><th width="50px">'+banco+'</th><th width="50px">'+tipoBancomer+'</th><th width="100px">'+cuenta+'</th><th width="100px">'+referencia+'</th><th width="100px">'+movimiento+'</th><th width="100px">'+concepto+'</th><th width="100px">'+beneficiario+'</th><th width="100px">'+importe+'</th><th width="100px">'+tipoMovimiento+'</th></tr>';
			}
				sOut += '</table>';
				oTable.fnOpen( nTr, sOut, 'details' );
				document.getElementById("esperar").style.visibility = 'hidden';
		});	
}

function imprimir(oTable, nTr){
	
	var aData = oTable.fnGetData( nTr );
	
	window.open(	"../admin/SeguridadCatalogos?"
					+ "catalogo=Rdb"
					+ "&accion=run"
					+ "&rn=RegistroDiarioBancoCierre.jasper"
					+ "&idRdbCargaArchivoRDBSaldo=" + aData[6],
					"popacuse",
					"scrollbars=1, resizable=yes, width=1024, height=768"
			   );
}


</script>
		

</head>

<body id="dt_example" >
	<form id="ExportarForm" name="ExportarForm" action="../gstnmngr/LayoutGeneral" method="post"> 
		<div id="container" style="width:1000px; padding-left:300px" class="SyCData">	
			<input type="hidden" name="szTemp" id="szTemp"/>
			<input type="hidden" name="cDocumento" id="cDocumento" value="TODOS"/>
			<input type="hidden" name="cBanco" id="cBanco" value=""/>
			<input type="hidden" name="cUnidadResponsable" id="cUnidadResponsable" value=""/>
			<input type="hidden" name="tipoConsulta" id="tipoConsulta" value="ConsultaIngresosEgreso"/>
															
			<h1>Consulta de Ingresos / Egresos</h1>	
			<fieldset>		
				<table id="clvcont" border="2" align="center">
				
					<tr>
						
						<td align="right">Fecha Inicio: </td>
						<td>
							<input type="text" maxlength="10" size="10" name="fFechaInicio" id="fFechaInicio" readonly >
						</td>
						<td align="right">Fecha Fin: </td>
						<td>
							<input type="text" maxlength="10" size="10" name="fFechaFin" id="fFechaFin" readonly >
						</td>
					
						<td valign="top" >Folio</td>				
						<td><input type="text" maxlength="10" size="15" name="folioCierre" id="folioCierre" ></td>
					</tr>
				</table>
			
				<table><tr><td>&nbsp;</td></tr></table>
				<table align="center">
					<tr>
						<td>
							<input type="button" id="pbFiltrar" value="Buscar"/>
						</td>
						<td>
							<input type="button" id="pbLimpiar" value="Nuevo"/>
						</td>
						
					</tr>
				</table>
			</fieldset>
			<table id="tblIngresosEgresos" class="display" style="font-size: 8pt">
			     <thead>
	                <tr>
	                	
	                	<th>Folio_Cierre</th>
						<th>Fecha_Aplicacion</th>	
						<th>Unidad_Responsable</th>	
						<th>Ejercicio_Fiscal</th>
						<th>Importe</th>
						<th></th>
						<th>Imprimir</th>
	                </tr>
	            </thead>
	        </table>
			<br/>
		</div>
	</form>
	</body>
</html>