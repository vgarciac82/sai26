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
<title>Vista previa Pólizas</title>


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

var MatrizPolizas;
var nPolizaActual=1;
var nPolizas=1;
var camposWhere= new Array();
			   
<%
	String split[]=null;
	
	split=request.getParameter("condiciones").replaceAll(",''S''","").replaceAll(",''A''","").replace("(","").replace(")","").replaceAll("]'","]").split("\\[");
	
	System.out.println(split);
	
	for (int i=1;i<split.length;i++)
	out.println("camposWhere["+(i)+"]=\"'(["+split[i]+")'\";");
%>
 //Inicia llamado de pagina
	$(document).ready(function() {
	
    
		//Inicializa las ayudas. Simpre debe estar presente en el onLoad (en JQuery es el $(document).ready(function() {});)
		$("input.AyudaSyC").subIniciaDlg();
		//Inicializa el autocompletar. Simpre debe estar presente en el onLoad (en JQuery es el $(document).ready(function() {});)
		$("input.autoCompletaSyC").subIniciaAutoCompleta();
		
		 
		$("#dt_poliza").dataTable({
			bPaginate : false,
			bLengthChange :false,
			bInfo : false,
			sScrollX : "100%",
			sScrollY : "100%",
			bJQueryUI : true,
			bFilter : false,
			bSort : false,
			bAutoWidth : true,
			sWidth: "100%",
			
			
						
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
				sSearch : "Buscar:",
				oPaginate : {
					sFirst : "Primero",
					sPrevious : "Ant.",
					sNext : "Sigte.",
					sLast : "&Uacute;ltimo"
				}
			},
			aoColumns : [{
				sName : "CUENTA",
				bSortable : true
			}, {
				sName : "SUB-CTA",
				bSortable : false
			}, {
				sName : "SUB-SCTA",
				bSortable : false
			}, {
 				sName : "NOMBRE",
				bSortable : false
		    }, {
				sName : "DETALLE",
    			bSortable : false
	        }, {
				sName : "DEBE",
    			bSortable : false
	        }, {
				sName : "HABER",
    			bSortable : false
	        }
	        ]
		  
			});
			
		
		CargarDetalle();	
	
		}); //Finaliza llamado de pagina

		

function CargarDetalle()
{
			   $('#dt_poliza').dataTable().fnClearTable();
			   
			  
      	      // var camposWhere="'<%=request.getParameter("condiciones").replaceAll(",''S''","").replaceAll(",''A''","").replace("'(","(").replace(")'",")")%>'";                                                                     
				
				
				
				var zTabla = "BUSCA_POLIZA";
				
				nPolizas="<%=request.getParameter("items") %>";
		
				var param = "";
				var sOrder = " ";
			    var j;
			   
			  													
			$.getJSON("../catalogos/SelectJson.jsp",{Tabla:"BUSCA_POLIZA", Campos:camposWhere[nPolizaActual], Param:param, Order:sOrder, MaxReg: "5", ajax:"true"}, function(j){ 
					
			        
	   		         
	   		            MatrizPolizas=datatoArray(j);
	   		           
	   				    //nPolizas=numPolizas(MatrizPolizas);
	   				    
	   				    if(nPolizas==1)
	   				    {
	   				    document.getElementById("btSiguiente").disabled=true;
	   				    document.getElementById("btAnterior").disabled=true;
	   				    }
	   				      				
	   				   
	   					var k=getDataPoliza(MatrizPolizas,1);
	   					loadDatatable(k);
	   			    
					 
		        	}); //fin addTabla
	   
	   
	                     
	
}//termina carga de detalle en datatable



function loadDatatable(k)
{
var Cargos=0;
var Abonos=0;      
var tempCargos=0;
var tempAbonos=0;
	   					$('#cCentroContable').val(k[0][6]);
	   					$('#tipoPoliza').val(k[0][7]);
	   					$('#fPoliza').val(k[0][10]);
	   					$('#nPoliza').val(k[0][0]);
	   					$('#hechoPor').val(k[0][12]);
	   					$('#revisadoPor').val(k[0][13]);
	   					$('#autorizadoPor').val(k[0][11]);
	   					$('#cConcepto').val(k[0][1]);
	   					
	   					//var f=0;
	   					
	   					/*for (var i = 0; i < k.length; i++)  
	   					{	
	   				   alert(k[i][1]);
	   				   alert(k[i][2]);
	   				   alert(k[i][3]);
	   				   alert(k[i][4]);
	   				   alert(k[i][5]);
	   				   alert(k[i][6]);
	   				   alert(k[i][7]);
	   				   alert(k[i][8]);
	   				   alert(k[i][9]);
	   				   alert(k[i][10]);
	   				   alert(k[i][11]);
	   				   alert(k[i][12]);
	   				   alert(k[i][13]);
	   				   alert(k[i][14]);
	   				   alert(k[i][15]);
	   				   alert(k[i][16]);
	   				   alert(k[i][17]);
	   				   alert(k[i][18]);
	   				   alert(k[i][19]);
	   				   alert(k[i][20]);
	   				   alert(k[i][21]);
	   				   alert(k[i][22]);
	   				   alert(k[i][23]);
	   				   alert(k[i][23]);
	   				   }*/
	   					
	   					for (var i = 0; i < k.length; i++)  
	   					{	
	   				   
	   				    
	   				     
	   				    Cargos=parseFloat(k[i][8]);
	   				    Abonos=parseFloat(k[i][9]);
	   				 
	   					$('#dt_poliza').dataTable().dataTable().fnAddData( [
				           k[i][2]  , 
				          k[i][3]  , 
				         k[i][4] , 
				         k[i][5]  ,
				        k[i][15]  , 
				         Cargos.toFixed(2), 
				         Abonos.toFixed(2) 
				        ]);	
				        
				        
				        if( k[i][2]=='11301')
	   				    {
	   				  
	   				    $('#dt_poliza').dataTable().dataTable().fnAddData( [
				          '' , 
				          '' , 
				          '' , 
				          k[i][17]+'\n'+k[i][15]+'<br>'+k[i][16] ,
				          '' , 
				          '' , 
				          '' , 
				        ]);	
	   				    } 
	   				    
	   				    if( k[i][2]=='21203')
	   				    {
	   				   
	   				    $('#dt_poliza').dataTable().dataTable().fnAddData( [
				          '' , 
				          '' , 
				          '' , 
				          k[i][15] ,
				          '' , 
				          '' , 
				          '' , 
				        ]);	
	   				    } 
				        
				        
				         if( k[i][2]=='21204')
	   				    {
	   				   
	   				    $('#dt_poliza').dataTable().dataTable().fnAddData( [
				          '' , 
				          '' , 
				          '' , 
				          k[i][17]+'<br>'+k[i][18] ,
				          '' , 
				          '' , 
				          '' , 
				        ]);	
	   				    }
				        
				        
				        tempCargos+=parseFloat(Cargos);
				        tempAbonos+=parseFloat(Abonos);
				           				
	   	        		}
	   	        		
	   	        		
	   	        	
	   	        		
	   	        		
	   	        		
	   	        		$('#dt_poliza').dataTable().dataTable().fnAddData( [
				          '' , 
				          '' , 
				          '' , 
				          '' ,
				          'Total' , 
				          tempCargos.toFixed(2) , 
				          tempAbonos.toFixed(2), 
				        ]);	
	   	        		
	   	        		
	
}



function datatoArray(DataTable)
{

var arre=new Array();


for(var i=0;i<DataTable.length;i++)
{
arre[i]=new Array(19);

arre[i][0]=DataTable[i].Col1;
arre[i][1]=DataTable[i].Col4;
arre[i][2]=DataTable[i].Col9;
arre[i][3]=DataTable[i].Col10;
arre[i][4]=DataTable[i].Col11;
arre[i][5]=DataTable[i].Col12;
arre[i][6]=DataTable[i].Col17;
arre[i][7]=DataTable[i].Col20;
arre[i][8]=DataTable[i].Col28;
arre[i][9]=DataTable[i].Col29;
arre[i][10]=DataTable[i].Col34;
arre[i][11]=DataTable[i].Col44;
arre[i][12]=DataTable[i].Col45;
arre[i][13]=DataTable[i].Col46;
arre[i][14]=DataTable[i].Col47;
arre[i][15]=DataTable[i].Col38;
arre[i][16]=DataTable[i].Col39;
arre[i][17]=DataTable[i].Col13;
arre[i][18]=DataTable[i].Col14;
}


return arre;

}



function getDataPoliza( Matriz, nPolizaRequerida){

var arr = new Array();
var polizaActual=Matriz[0][0];
var polizaAnterior;
var posActual=1
var accept=false;
var fila=0;



if(posActual==nPolizaRequerida)
{
accept=true;
}

  for(var i=0; i < Matriz.length; i++)
   {  
    
      polizaAnterior=polizaActual;
      polizaActual=Matriz[i][0]; 
        
       if(polizaAnterior!=polizaActual)
       {
       
          posActual++;
          
          if(nPolizaRequerida==posActual)
          {
          accept=true;
           }
          else
          {
          accept=false;
          }
       }

      if(accept==true)
      {
      arr[fila]=new Array(19);
      for(var j=0; j < 19; j++)
        {
        arr[fila][j] = Matriz[i][j];
        
        }
     
      fila++;
      }
     }
    
   
return arr;
}


function numPolizas(Matriz)
{
var temp;
var nPol=1;

temp=Matriz[0][0]

for(var i=1;i<Matriz.length;i++)
{
if(temp!=Matriz[i][0])
{
nPol++;
temp=Matriz[i][0];
}

}

return nPol;
}
	   		

function siguiente()
{

var k;
nPolizaActual+=1;


if((nPolizaActual)==nPolizas)
{
document.getElementById("btSiguiente").disabled=true;
}

if(nPolizaActual>1)
{
document.getElementById("btAnterior").disabled=false;
}

if(nPolizaActual<=nPolizas)
{
document.getElementById("btAnterior").disabled=false;

$('#dt_poliza').dataTable().fnClearTable();
k=getDataPoliza(MatrizPolizas,nPolizaActual);

//loadDatatable(k);
CargarDetalle();
}

}


function anterior()
{

var k;
nPolizaActual-=1;

if(nPolizaActual==1)
{
document.getElementById("btAnterior").disabled=true;

}
if(nPolizas>1)
{
document.getElementById("btSiguiente").disabled=false;

}



k=getDataPoliza(MatrizPolizas,nPolizaActual);
$('#dt_poliza').dataTable().fnClearTable();
//loadDatatable(k);

CargarDetalle();



}



</script>

</head>
<body id="dt_example" bottomMargin="0" bgColor="red" leftMargin="0"
	topMargin="0" >
	
       
		

		<div id="container_poliza" class="container">
	
			<h1>Vista previa Pólizas</h1>

			
<table id="encabezado" width="100%" border="1">
  <tr>
    <td colspan="2"><img src="../imagenes/logotipo-usuario.png" /></td>
    <td colspan="2"><div align="center">GERENCIA DE RECURSOS FINANCIEROS<br />
      SUBGERENCIA DE CONTABILIDAD<br />
    COMISION NACIONAL FORESTAL</div></td>
    <td colspan="2">&nbsp;</td>
  </tr>
  <tr>
    <td></td>
    <td></td>
    <td><div align="right">Centro Contable: </div></td>
    <td><div align="left"><input type="text" name="cCentroContable" id="cCentroContable"  class="typesEnc"></div></td>
    <td></td>
    <td>
       
       
    </td>
  </tr>
  <tr>
    <td><div align="right">Poliza de:</div></td>
    <td><input type="text" name="tipoPoliza" id="tipoPoliza"  class="typesEnc"/></td>
    <td><div align="right">Fecha:</div></td>
    <td><div align="left"><input type="text" name="fPoliza" id="fPoliza"  class="typesEnc"/></div></td>
    <td><div align="right">Poliza:</div></td>
    <td>
    <div id="navegador">
    
     <input name="btAnterior" type="submit" id="btAnterior" value="" onclick="anterior();" class="navegar"/>
     <input type="text" name="nPoliza" id="nPoliza" class="typesEnc"/> 
    
     <input name="btSiguiente" type="submit" id="btSiguiente" value="" onclick="siguiente();" class="navegar" />
   
     </div></td>
  </tr>
</table>
			
			<table id="dt_poliza" border="0" >
			
									
								<thead>
								
										<tr><th>Cuenta</th>
										    <th>Subcuenta</th>
										    <th>Subsubcuenta</th>
											<th>Nombre </th>
											<th>Detalle</th>
											<th>Debe</th>
											<th>Haber</th>																						
										</tr>
									</thead>
									<tbody>
									</tbody>
								</table>
			
	
		<table width="100%" border="0">
		<tr>
    <td colspan="5">
      Concepto:
       </td>
  </tr>
  <tr>
    <td colspan="5">
      
      <textarea name="cConcepto" id="cConcepto"  class="types"></textarea>
       </td>
  </tr>
  <tr>
    <td rowspan="2">CONTROL</td>
    <td>HECHO POR </td>
    <td>REVISADO</td>
    <td>AUTORIZADO</td>
   <!--  <td>PRESUPUESTOS</td>  -->
  </tr>
  <tr>
    <td><input type="text" name="hechoPor" id="hechoPor"  class="types" /></td>
    <td><input type="text" name="revisadoPor" id="revisadoPor"  class="types" /></td>
    <td><input type="text" name="autorizadoPor" id="autorizadoPor"  class="types"/></td>
    <td></td>
  </tr>
</table>	
			
			
			
			
		</div>
	


</body>
</html>