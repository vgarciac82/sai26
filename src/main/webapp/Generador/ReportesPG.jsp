<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%> 
<%@page import="com.syc.gestion.core.Caso"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.text.DateFormat"%>
<%@page import="java.util.Calendar"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>




<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head> 
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Reportes Ejecutivos</title>


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

<script type="text/javascript" src="../Generador/js/jquery.dataTables.js"> </script>
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
<script type="text/javascript"
	src="../Generador/js/jquery.formatCurrency.js">
	
</script>
<script type="text/javascript"
	src="../Generador/js/jquery.formatCurrency.all.js">
	
</script><script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>

<link rel="stylesheet" type="text/css" 	href="../css/menuContabilidad.css"></link>


<script type="text/javascript">

		   
var action;
var descripcion;
var id_reporte;
 //Inicia llamado de pagina
	$(document).ready(function() {
	
	
	
    
		//Inicializa las ayudas. Simpre debe estar presente en el onLoad (en JQuery es el $(document).ready(function() {});)
		$("input.AyudaSyC").subIniciaDlg();
		//Inicializa el autocompletar. Simpre debe estar presente en el onLoad (en JQuery es el $(document).ready(function() {});)
		$("input.autoCompletaSyC").subIniciaAutoCompleta();
		
		 
		$("#dt_reporte").dataTable({
			bPaginate : false,
			bLengthChange :true,
			bInfo : false,
			sScrollX : "100%",
			sScrollY : "100%",
			bJQueryUI : true,
			bFilter : true,
			bSort : true,
			bAutoWidth : true,
			//sWidth: "100%",
			
			
						
			oLanguage : {
				sProcessing : "Procesando...",
				sLengthMenu : "Mostrar _MENU_ registros",
				sZeroRecords : "No hay registros a mostrar",
				sEmptyTable : "Por favor espere mientras se cargan los datos...",
				sLoadingRecords : "Cargando...",
				sInfo : "Registros _START_ al _END_ de _TOTAL_",
				sInfoEmpty : "Registro 0 al 0 de 0",
				sInfoFiltered : "(filtado de _MAX_ registros)",
				sInfoPostFix : "",
				sInfoThousands : ",",
				sSearch : "Filtrar:",
				oPaginate : {
					sFirst : "Primero",
					sPrevious : "Ant.",
					sNext : "Sigte.",
					sLast : "&Uacute;ltimo"
				}	
			},
			
				aoColumns: [
						{ sWidth: "2%" },
						{ sWidth: "25%" },
						{ sWidth: "71%" }
					]
			
			});
			
		
		CargarDetalle();
		
		
		
		
			
	
		}); //Finaliza llamado de pagina

		

function CargarDetalle()
{
			   $('#dt_reporte').dataTable().fnClearTable();
			   
				
				
				var zTabla = "REPORTES_CONTABLES";
				var camposWhere="";
				var param = "";
				var sOrder = " ";
			    var j;
			   
			   
			  													
			$.getJSON("../catalogos/SelectJson.jsp",{Tabla:zTabla, Campos:camposWhere, Param:param, Order:sOrder, MaxReg: "5", ajax:"true"}, function(j){ 
					
			        action= new Array(j.length);
			        descripcion= new Array(j.length);
	   		          
	                  for (var i = 0; i < j.length; i++) 
	   					{	
                             action[i]=j[i].Col3;
                             descripcion[i]=j[i].Col4;
	   						$('#dt_reporte').dataTable().fnAddData(
	   						
	   						[ j[i].Col0, j[i].Col1, j[i].Col2 ]
	   						
	   						
	   						);
						}
						
						accionFilas();
	   									 
		        	}); 
		        	
	   
	  
	   
	          }

function generarReporte()
{

var TipoReporte="";

if(id_reporte!=null)
   {
  
    var tipoSalida=$("input[name=treporte]:checked").val();

    if(tipoSalida==2)
     TipoReporte="&xls=SI";
  //  else if(tipoSalida==3)
    // TipoReporte="&html=SI";

    var url = "../admin/SeguridadCatalogos?catalogo=REPORTE&accion=run&rn="+action[id_reporte-1]+""+TipoReporte;
    var ventimp = window.open(url, "popacuse","scrollbars=1, resizable=yes, width=1024, height=768");
 }



}

function seleccionaRadio()
{


}


function accionFilas() {
    var table = document.getElementById("dt_reporte");
    var rows = table.getElementsByTagName("tr");
   
   
    
    for (i = 1; i < rows.length; i++) {
        row = table.rows[i];
        
        row.ondblclick = function(){
                          var cell = this.getElementsByTagName("td")[0];
                          var id = cell.innerHTML;
                          id_reporte=id;
                          generarReporte();                        
                      };  
        row.onmouseover = function(){
         var cell = this.getElementsByTagName("td")[0];
        
                          var id = cell.innerHTML;                
                          document.getElementById('cConcepto').value=descripcion[id-1]; 
                           
                          
                           
                                             
                      };  
         row.onmouseout = function(){
         var cell = this.getElementsByTagName("td")[0];
                          var id = cell.innerHTML;
                          document.getElementById('cConcepto').value='Descripción...';                       
                      };  
    }
    
    
    
    
    
    
}


   		



</script>

</head>
<body id="dt_example" bottomMargin="0" bgColor="red" leftMargin="0"
	topMargin="0" >
	
       
	
		<div id="container_poliza" class="container">
	
			<h1>Generaci&oacute;n de Reportes Ejecutivos</h1>
           <p>Haga doble click para Generar un reporte</p>




			<table id="dt_reporte" border="0">


				<thead>

					<tr>
						<th>
							
						</th>
						<th>
							Nombre
						</th>
						<th>
							Tipo Documento
						</th>
					</tr>
				</thead>
				<tbody>
				</tbody>
			</table>
			<br/>
           Tipo de Salida:
           <p>
           <input type="radio" name="treporte" value="1"  checked/>
           PDF
           <input type="radio" name="treporte" value="2" />
           EXCEL
          <!--  <input type="radio" name="treporte" value="3" />
           HTML  -->
           
           </p>
			<p>Coloque el puntero sobre algún reporte para ver la descripci&oacute;n.</p>

			<textarea name="cConcepto" id="cConcepto"> Descripción..</textarea>



		</div>
		
		
	


</body>
</html>