<%@page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" pageEncoding="utf-8" %>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@page import="com.syc.contable.core.Saldo"%>
<%@page import="java.text.DecimalFormat"%>

<%
Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);

if (usuario == null) {
	response.sendRedirect("../index.jsp");
	return;
}
	String DATE_FORMAT = "dd-MM-yyyy";
	SimpleDateFormat fe = new SimpleDateFormat(DATE_FORMAT);
	Calendar c1 = Calendar.getInstance(); // today
	String today = fe.format(c1.getTime());
	
	Calendar calendario = new GregorianCalendar();
	int hora = calendario.get(Calendar.HOUR_OF_DAY);
	int min = calendario.get(Calendar.MINUTE);
	int seg = calendario.get(Calendar.SECOND);
	
	String FechHora = today+ " " +hora+":"+min;
	

%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
   
    <link rel="shortcut icon" type="image/ico" href="imagenes/favicon.ico" />
	<style type="text/css" title="currentStyle">
		@import "themes/smoothness/jquery-ui-1.8.4.custom.css";
		@import "css/demo_table_jui.css";
		@import "css/demo_page.css";
		table.borde th{border: 1px dotted #d4d9de;}
	</style>
	
	<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
	<script type="text/javascript" src="js/jquery.dataTables.js"></script>
	<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
	<script type="text/javascript" src="js/jquery.ui.datepicker.js"></script>
	<script type="text/javascript" src="js/jquery.ui.core.js"></script>
	<script type="text/javascript" src="js/jquery.ui.widget.js"></script>
	<script type="text/javascript" src="js/jquery.ui.tabs.js"></script>
	<script type="text/javascript" src="js/jquery.form-2.94.js"></script>
	
	<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
	<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
	<script type="text/javascript" src="js/crud.js"></script>
	<script type="text/javascript" src="js/jquery.formatCurrency.js"></script>
	<script type="text/javascript" src="js/jquery.formatCurrency.all.js"></script>	
	
<script type="text/javascript">
$(document).ready(function () {

	$("#btnExportaExcel").click(function() { $("#frmConsultaCapitulo").submit(); limpiar(); } );
	/*
	$('#tablaComparativo').dataTable({         
			//iDisplayLength: 20,
			bSortClasses: false,
			ScrollY: "500px",
			sScrollX: "1300px",
			bPaginate: false,
        	bLengthChange: false,
        	bFilter: false,
        	bSort: true,
        	bInfo: false,
        	bAutoWidth: false,
			bJQueryUI: true,
			bRetrive : true,
			bDestroy : true,
			sPaginationType: "full_numbers",
			bScrollCollapse: true,
			//sScrollXInner: "100%",
			aaSorting: [[ 1, "asc" ]] ,
			bRetrive: true,
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
						sSearch: "Buscar:",
						oPaginate: {
							sFirst:    "Primero",
							sPrevious: "Ant.",
							sNext:     "Sigte.",
							sLast:     "&Uacute;ltimo"
						}
				}
				
		});
	*/
	 /*     * Insert a 'details' column to the table     */    
	  
	  var nCloneTh = document.createElement( 'th' );    
	  var nCloneTd = document.createElement( 'td' );    
	  nCloneTd.innerHTML = '<img src="../imagenes/icono_word.png">';    
	  nCloneTd.className = "center";        
	  
	  $('#tablaComparativo thead tr').each( function () {        
	  		this.insertBefore( nCloneTh, this.childNodes[0] );    
	  } );         
	  
	  $('#tablaComparativo tbody tr').each( function () {       
	  		 this.insertBefore(  nCloneTd.cloneNode( true ), this.childNodes[0] );    
	  } );         
	  
	  /*     * Initialse DataTables, with no sorting on the 'details' column     */    
	  
	  var oTable = $('#tablaComparativo').dataTable({  
	  
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
			sScrollX: "1700px",
						
			/*bProcessing: true,
			bJQueryUI: true,
			bAutoWidth : true,
			bRetrive: true,
			bDestroy: true,
        	bPaginate: false,
			iDisplayLength: 10,
		    sScrollY: "450px", 
		    sScrollX: "1200px",*/
			//sScrollXInner: "100%",
			//aaSorting: [[ 1, "asc" ]] ,
			/*oLanguage: {
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
						sSearch: "Buscar:",
						oPaginate: {
							sFirst:    "Primero",
							sPrevious: "Ant.",
							sNext:     "Sigte.",
							sLast:     "&Uacute;ltimo"
						}
				},*/
			/*oLanguage: {
				sProcessing: "Procesando...",
				sInfo: "<th>olaaa</th><th>adios</th>",
				sSearch: ""
			},*/
		  	aoColumnDefs: [ { "bSortable": false, "aTargets": [ 1 ], "sClass":"center", "sWidth":"10%" },  
		  					{ "bSortable": false, "aTargets": [ 2 ], "sClass":"center", "sWidth":"10%" },
		  					{ "bSortable": false, "aTargets": [ 3 ], "sClass":"center", "sWidth":"10%" },
		  					{ "bSortable": false, "aTargets": [ 4 ], "sClass":"center", "sWidth":"10%" },
		  					{ "bSortable": false, "aTargets": [ 5 ], "sClass":"center", "sWidth":"10%" },
		  					{ "bSortable": false, "aTargets": [ 6 ], "sClass":"center", "sWidth":"10%" },
		  					{ "bSortable": false, "aTargets": [ 7 ], "sClass":"center", "sWidth":"10%" },
		  					{ "bSortable": false, "aTargets": [ 8 ], "sClass":"center", "sWidth":"10%" },
		  					{ "bSortable": false, "aTargets": [ 9 ], "sClass":"center", "sWidth":"10%" },
		  					{ "bSortable": false, "aTargets": [ 10 ], "sClass":"center", "sWidth":"10%" },
		  					{ "bSortable": false, "aTargets": [ 11 ], "sClass":"center", "sWidth":"10%" },
		  					{ "bSortable": false, "aTargets": [ 12 ], "sClass":"center", "sWidth":"10%" },
		  					{ "bSortable": false, "aTargets": [ 13 ], "sClass":"center", "sWidth":"10%" },
		  					{ "bSortable": false, "aTargets": [ 14 ], "sClass":"center", "sWidth":"10%" },
		  					{ "bSortable": false, "aTargets": [ 15 ], "sClass":"center", "sWidth":"10%" },
		  					{ "bSortable": false, "aTargets": [ 16 ], "sClass":"center", "sWidth":"10%" },
		  					{ "bSortable": false, "aTargets": [ 17 ], "sClass":"center", "sWidth":"10%" }
		  					
		  					//{"sClass": "dt-right", "aTargets": ['dt-right']}
		  	],       
		  	aaSorting: [[1, 'asc']]   
	  
	  });      
	  	
	  
	  /* Add event listener for opening and closing details    
		 * Note that the indicator for showing which row is open is not controlled by DataTables,     
		* rather it is done here     
	  */    
	  
	  /*
	  $('tbody td img', oTable.fnGetNodes() ).live( function () { 
	  		
	  		$(this).click( function () {
			
			alert("ja");
			var nTr = this.parentNode.parentNode;
			
				if ( this.src.match('details_close') ){
					this.src = "../imagenes/icono_word.png";
					 oTable.fnClose( nTr );  
					//var nRemove = $(nTr).next()[0];
					//nRemove.parentNode.removeChild( nRemove );
				
				}else if(this.src.match('details_open') ){
					this.src = "../imagenes/icono_refresh.jpg";
					oTable.fnOpen( nTr, fnFormatDetails(oTable, nTr), 'details' );
				}
			});
		});
			*/
	  
	
	$('#tablaComparativo tbody td img').live('click', function () {   
	
			var nTr = this.parentNode.parentNode; 
			var n = oTable.fnGetNodes();
			//var aPos = oTable.fnGetPosition( this.parentNode.parentNode );
     		//var aData = oTable.fnGetData( nTr );
			//var dat = aData[0];
			
			if ( this.src.match('flechaAzul1.png') ) {
			
				this.src = "../imagenes/flechaAzul2.png"; 
				mostrarDetalle(oTable, nTr) 
			
			}else{
				this.src = "../imagenes/flechaAzul1.png";   
				oTable.fnClose( nTr );  
			
			}
			
			
			
			//oTable.fnOpen( nTr, mostrarDetalle(oTable, nTr), 'details' );   
			
			
			
			/*var nTr = this.parentNode.parentNode;  
			      
			if ( this.src.match('details_close') ) {
			alert("aki");
			     This row is already open - close it            
			    this.src = "../imagenes/icono_word.png";           
			    oTable.fnClose( nTr );  
			          
			} else {           
				alert("else");
				  Open this row   
				 this.src = "../imagenes/icono_refresh.jpg";         
				 oTable.fnOpen( nTr, mostrarDetalle(oTable, nTr), 'details' );       
			}*/
			    
	});
	
	datosComparativo();  
	//tablaComparativo.$('tr:odd').css('backgroundColor', 'blue');
	
	
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
function datosComparativo(){

	$("#esperar").attr("style","visibility=visible");
	
	$.getJSON("../catalogos/SelectJson.jsp",{Tabla: "ejercidoComparativoCapitulo", Campos:"", Param:"", MaxReg: "5", ajax: 'true'}, function(j){
			
				for (var i = 0; i < j.length; i++) {
												
					$("#tablaComparativo").dataTable().fnAddData( [ '<td><img src="../imagenes/flechaAzul1.png"></td>', 
																	j[i].Col0, 
																	/*'<td><input type="text" value="'+j[i].Col1+'" size="15"  style="background-color:#E6E6FA; border:1px solid #E6E6FA; text-align:right" readonly="readonly"></td>',
																	'<td><input type="text" value="'+j[i].Col2+'" size="15"  style="background-color:#E6E6FA; border:1px solid #E6E6FA; text-align:right" readonly="readonly"></td>',
																	'<td><input type="text" value="'+j[i].Col3+'" size="15"  style="background-color:#E6E6FA; border:1px solid #E6E6FA; text-align:right" readonly="readonly"></td>',
																	'<td><input type="text" value="'+j[i].Col4+'" size="15"  style="background-color:#E6E6FA; border:1px solid #E6E6FA; text-align:right" readonly="readonly"></td>',
																	'<td><input type="text" value="'+j[i].Col5+'" size="15"  style="background-color:#E6E6FA; border:1px solid #E6E6FA; text-align:right" readonly="readonly"></td>',
																	'<td><input type="text" value="'+j[i].Col6+'" size="15"  style="background-color:#E6E6FA; border:1px solid #E6E6FA; text-align:right" readonly="readonly"></td>',
																	'<td><input type="text" value="'+j[i].Col7+'" size="15"  style="background-color:#E6E6FA; border:1px solid #E6E6FA; text-align:right" readonly="readonly"></td>',
																	j[i].Col8,
																	'<td><input type="text" value="'+j[i].Col9+'" size="15"  style="background-color:#E6E6FA; border:1px solid #E6E6FA; text-align:right" readonly="readonly"></td>',
																	'<td><input type="text" value="'+j[i].Col10+'" size="15"  style="background-color:#E6E6FA; border:1px solid #E6E6FA; text-align:right" readonly="readonly"></td>',
																	'<td><input type="text" value="'+j[i].Col11+'" size="15"  style="background-color:#E6E6FA; border:1px solid #E6E6FA; text-align:right" readonly="readonly"></td>',
																	'<td><input type="text" value="'+j[i].Col12+'" size="15"  style="background-color:#E6E6FA; border:1px solid #E6E6FA; text-align:right" readonly="readonly"></td>',
																	'<td><input type="text" value="'+j[i].Col13+'" size="15"  style="background-color:#E6E6FA; border:1px solid #E6E6FA; text-align:right" readonly="readonly"></td>',
																	'<td><input type="text" value="'+j[i].Col14+'" size="15"  style="background-color:#E6E6FA; border:1px solid #E6E6FA; text-align:right" readonly="readonly"></td>',
																	'<td><input type="text" value="'+j[i].Col15+'" size="15"  style="background-color:#E6E6FA; border:1px solid #E6E6FA; text-align:right" readonly="readonly"></td>'
																*/
																	j[i].Col1, j[i].Col2, j[i].Col3, j[i].Col4, j[i].Col5, j[i].Col6, j[i].Col7, j[i].Col8, j[i].Col9, j[i].Col10, j[i].Col11, j[i].Col12, j[i].Col13, j[i].Col14, j[i].Col15, j[i].Col16
																	
																	] );
				}
				totales();
				document.getElementById("esperar").style.visibility = 'hidden';
		});	
} 

function mostrarDetalle(oTable, nTr){

		$("#esperar").attr("style","visibility=visible");
	
		var aData = oTable.fnGetData( nTr );
		
		var campos = aData[1];
		var unidadSicop = "";
		var partidaSicop = "";
		var mil = "";
		var dosMil = "";
		var tresMil = "";
		var cuatroMil = "";
		var cincoMil = "";
		var seisMil = "";
		var sOut = '<table border="0" style="padding-left:100px;width:100%" class="borde">';
		
		$.getJSON("../catalogos/SelectJson.jsp",{Tabla: "ejercidoComparativoCapituloDetalle", Campos:campos, Param:"", MaxReg: "5", ajax: 'true'}, function(j){
			
				for (var i = 0; i < j.length; i++) {
										
					partidaSicop = j[i].Col1;
					mil = j[i].Col2;
					dosMil = j[i].Col3;
					tresMil = j[i].Col4;
					cuatroMil = j[i].Col5;
					cincoMil = j[i].Col6;
					seisMil = j[i].Col7;
					totalGeneral = j[i].Col8;		
					
					partidaSAI = j[i].Col10;
					milSAI = j[i].Col11;
					dosMilSAI = j[i].Col12;
					tresMilSAI = j[i].Col13;
					cuatroMilSAI = j[i].Col14;
					cincoMilSAI = j[i].Col15;
					seisMilSAI = j[i].Col16;
					totalGeneralSAI = j[i].Col17;
					diferencia = j[i].Col18;
					
					sOut += '<tr><th width="12px"> - </th><th width="60px">'+partidaSicop+'</th><th width="100px">'+mil+'</th><th width="100px">'+dosMil+'</th><th width="100px">'+tresMil+'</th><th width="100px">'+cuatroMil+'</th><th width="100px">'+cincoMil+'</th><th width="100px">'+seisMil+'</th><th width="100px">'+totalGeneral+'</th>';
					sOut += '<th width="10px"> - </th><th width="60px">'+partidaSAI+'</th><th width="100px">'+milSAI+'</th><th width="100px">'+dosMilSAI+'</th><th width="100px">'+tresMilSAI+'</th><th width="100px">'+cuatroMilSAI+'</th><th width="100px">'+cincoMilSAI+'</th><th width="100px">'+seisMilSAI+'</th><th width="100px">'+totalGeneralSAI+'</th><th width="100px">'+diferencia+'</th></tr>';
				}
				sOut += '</table>';
				oTable.fnOpen( nTr, sOut, 'details' );
				document.getElementById("esperar").style.visibility = 'hidden';
		});	
}

function totales(){

	$.getJSON("../catalogos/SelectJson.jsp",{Tabla: "ejercidoComparativoTotalCapitulo", Campos:"", Param:"", MaxReg: "5", ajax: 'true'}, function(j){
			
				for (var i = 0; i < j.length; i++) {
												
					$("#tablaComparativo").dataTable().fnAddData( [ 
																	/*'-','<th>Total</th>', 
																	'<th>'+j[i].Col0+'</th>','<th>'+j[i].Col1+'</th>','<th>'+j[i].Col2+'</th>','<th>'+j[i].Col3+'</th>','<th>'+j[i].Col4+'</th>','<th>'+j[i].Col5+'</th>','<th>'+j[i].Col6+'</th>', 
																	'<th>Total</th>',
																	'<th>'+j[i].Col7+'<th>','<th>'+j[i].Col8+'</th>','<th>'+j[i].Col9+'</th>','<th>'+j[i].Col10+'</th>','<th>'+j[i].Col11+'</th>','<th>'+j[i].Col12+'</th>','<th>'+j[i].Col13+'</th>','<th>'+j[i].Col14+'</th>'
																	*/
																	'-','TOTAL',
																	j[i].Col0,j[i].Col1, j[i].Col2, j[i].Col3, j[i].Col4, j[i].Col5, j[i].Col6, 
																	'TOTAL', j[i].Col7, j[i].Col8, j[i].Col9, j[i].Col10, j[i].Col11, j[i].Col12, j[i].Col13, j[i].Col14
																	
																	] );
				}
				
		});	
}
/*
function tablaTotales(){

	$.getJSON("../catalogos/SelectJson.jsp",{Tabla: "V_EJERCIDOCOMPARATIVO", Campos:"", Param:"", Order:"", MaxReg: "10", ajax: 'true'}, function(j){
	
		var sicop = 0.00;
		var totalEjercidoSICOP = 0.00;
		var cxp = 0.00;
		var totalEjercidoSAI = 0.00;
		var totalDiferencia = 0.00;
		
		for (var i = 0; i < j.length; i++) {
		    
		    sicop = j[i].Col0;
		    totalEjercidoSICOP = j[i].Col1;
		    cxp = j[i].Col2;
		    totalEjercidoSAI = j[i].Col3;
		    totalDiferencia = j[i].Col4;
		    		    
		}
		$("#sicop").html(sicop);
		$("#totalEjercidoSICOP").html(totalEjercidoSICOP);
		$("#cxp").html(cxp);
		$("#totalEjercidoSAI").html(totalEjercidoSAI);
		$("#totalDiferencia").html(totalDiferencia);
		
		
	});	

}

*/


</script>
</head>
<body id="dt_example">
  	<form id="frmConsultaCapitulo" name="frmConsultaCapitulo" method="post" action="../gstnmngr/LayoutEjercidoComparativoServlet" target="_blank">
		<div   id="container" class="container SyCData" style="width:80%">
			<h1>Consulta Ejercido Comparativo<label style="font-size: 7.5pt"></label></h1>		
			
			<div id="tabsl">
					<label id="esperar" style="visibility: hidden">
									<div align="center">Espere por favor....
									  <img border="0" src="../imagenes/espera.gif" height="30">
									</div>
					</label>    
				<fieldset>
						<legend>Comision Nacional Forestal Comparativo Del Ejercido</legend>
						<table align="right" >
							<td><%=FechHora%></td>
							<td><input type="button" id="btnExportaExcel" name="btnExportaExcel" value="Exportar Excel" /></td>
							<td><input type="hidden" name="fecha" id="fecha" value="<%=FechHora%>"/></td>
							<td><input type="hidden" name="tipoConsulta" id="tipoConsulta" value="consultaEjercidoComparativoCapitulo"/></td>
						</table>
						</table>
						<table id="tablaComparativo" class="display" style="font-size: 8pt">
							<tbody>
								<thead>
									<tr align="center">
										<th>Unidad Sicop</th>
										<th>1000</th>
										<th>2000</th>
										<th>3000</th>
										<th>4000</th>
										<th>5000</th>
										<th>6000</th>
										<th>Total General</th>
										
										<th>Unidad SAI</th>
										<th>1000</th>
										<th>2000</th>
										<th>3000</th>
										<th>4000</th>
										<th>5000</th>
										<th>6000</th>
										<th>Total General</th>
										<th>Diferencia</th>
									</tr>
								</thead>
							</tbody>
						</table>	
						<tr>&nbsp;</tr><tr>&nbsp;</tr>
				</fieldset>
				
			</div>
		</div>
	</form>	
</body>
</html>
