<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	String UE=usuario.getU_UR();//UR comparar con unidad ejecutora
	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	boolean mntoCuentas=false;
	if(usuario.getPropiedades()!=null&&usuario.getPropiedades().containsKey("CCENTROCONTABLE")){
		mntoCuentas = "10".equals(usuario.getPropiedad("CCENTROCONTABLE").getValor());
	}
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<title>Catalogo de Estructura Programatica</title>
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">
		<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
		<meta http-equiv="description" content="This is my page">
		<link rel="shortcut icon" type="image/ico" href="imagenes/favicon.ico" />
		<style type="text/css" title="currentStyle">
			@import "../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css";
			@import "../Generador/css/demo_table_jui.css";
			@import "../Generador/css/demo_page.css";
		</style>
		<script type="text/javascript" src="../jq/js/jquery.js"></script>
		<script type="text/javascript" src="../jq/js/jquery.dataTables.js"></script>
		<script type="text/javascript" src="../Generador/js/crud.js"></script>
		<script type="text/javascript" src="../js/catalogo/general.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery-ui-1.8.16.custom.min.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.ui.datepicker.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.ui.core.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.ui.widget.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.ui.tabs.js"></script>
		<script type="text/javascript" src="js/jquery.formatCurrency.js"></script>
		<script type="text/javascript" src="js/jquery.formatCurrency.all.js"></script>
		<script type="text/javascript" src="../Generador/js/crud.js"></script>
		<script type="text/javascript" src="../js/catalogo/general.js"></script>
		<script type="text/javascript" charset="UTF-8">
		
		var oTable, oCurrentFocus;
		var asInitVals = new Array();
		var arrRet = null;
  	    var aSelected = [0];
		var UE = "<%= usuario.getU_UR()%>";
		var Bandera=""
		$(document).ready(function() {
			oTableSaldos = $("#tblSaldos").dataTable({
				bPaginate : false,
				bLengthChange : false,
				bInfo : false,
				bAutoWidth : false,				
				sScrollX: "100%",
				bJQueryUI: true,
				bFilter : false,
				bSort : false,
				bInfo : false,
				bAutoWidth : false,
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
					oPaginate: {sFirst:    "Primero", sPrevious: "Ant.", sNext:     "Sigte.", sLast:     "&Uacute;ltimo" }
				},
				aoColumns: [
					{ sName: "MontoEnero",		bSortable: false },
					{ sName: "MontoFebrero",		bSortable: false },
					{ sName: "MontoMarzo",		bSortable: false },
					{ sName: "MontoAbril",		bSortable: false },
					{ sName: "MontoMayo",		bSortable: false },
					{ sName: "MontoJunio",		bSortable: false },
					{ sName: "MontoJulio",		bSortable: false },
					{ sName: "MontoAgosto",		bSortable: false },
					{ sName: "MontoSeptiembre",		bSortable: false },
					{ sName: "MontoOctubre",		bSortable: false },
					{ sName: "MontoNoviembre",		bSortable: false },
					{ sName: "MontoDiciembre",		bSortable: false },
					{ sName: "MontoAnual",	bSortable: false }
				]
       		});
			
			//se inicializan queryes
			//lo primero es checar el rol del usurio
		   // $("#usuarioLoginReport").val('<%=usuario.getLogin()%>');
		    // queryFormPost("checaRolUsuarioMultiReport",{async:false});
			
			
		
			     for (var i = 1; i < 17; i++)
			    $("#col"+i+"_filter" ).keyup( function(evt) {
				
					oCurrentFocus = this;
					 fnFilterColumn(evt);
					//$("#pbAceptar").css("visibility","hidden");
					$('#tblSaldos').dataTable().fnClearTable(); 
				});
				
			oTable = createDataTable("");
		    	
			$("#tblEP tbody").click(function(event) {
				
				$(oTable.fnSettings().aoData).each(function (){
					$(this.nTr).removeClass('row_selected');
				});
				$(event.target.parentNode).addClass('row_selected');			
				$('#tblSaldos').dataTable().fnClearTable(); 
				
				var aPos = oTable.fnGetPosition(event.target.parentNode);

	            if( aPos instanceof Array )
	                currIndex = aPos[ 0 ];
	            else
	                currIndex = aPos;
 
	            var arrDatos = currIndex;
	            
				var anSelected = oTable.fnGetData(aPos);
				var sEP = 	$.trim( anSelected[0] )+ "."+
							$.trim( anSelected[1] ) + "."+
							$.trim( anSelected[2] ) + "."+
							$.trim( anSelected[3] ) + "."+
							$.trim( anSelected[4] ) + "."+
							$.trim( anSelected[5] ) + "."+
							$.trim( anSelected[6] ) + "."+
							$.trim( anSelected[7] ) + "."+
							$.trim( anSelected[8] )  + "."+
							$.trim( anSelected[9] ) + "."+
							$.trim( anSelected[10] ) + "."+
							$.trim( anSelected[11] ) + "."+
							$.trim( anSelected[12] ) + "."+
							$.trim( anSelected[13] ) + "."+
							$.trim( anSelected[14] ) + "." +
							$.trim( anSelected[15] ); 
								
				var szWhere = " EP = '" + sEP + "' ";
				var szTabla = "VSALDOSANUALES";                                                                                         
				$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, MaxReg: 10, ajax: 'false'}, 
					function(j)
					{                      
	   					for (var i = 0; i < j.length; i++) 
	   					{	
	   						$('#tblSaldos').dataTable().fnAddData(
	   						[ j[i].Col4, j[i].Col5, j[i].Col6, j[i].Col7, j[i].Col8,j[i].Col9, j[i].Col10, j[i].Col11, j[i].Col12,(j[i].Col13), (j[i].Col14), (j[i].Col15), (j[i].Col16) ]);
						}
	         		});   		
			});
			$(this).ajaxForm({
				dataType:  "json",
				success: formSubmited
			});
			

           
			$("#pbCancelar").button().click(function() {
					window.close();
				});	
		}); //fin del document ready
		
		
		
		
		function formSubmited() {
			alert("EP enviada");
		}
		function fnGetSelected( oTableLocal )
		{
			var aReturn = new Array();
			var aTrs = oTableLocal.fnGetNodes();
			for ( var i=0 ; i<aTrs.length ; i++ )
			{
				if ( $(aTrs[i]).hasClass('row_selected') )
				{
					aReturn.push( aTrs[i] );
				}
			}
			return aReturn;
		}
		//function fnFilterColumn (evt)
		function fnFilterColumn (evt)
		{	
		
	  				
			var value, myWhere, token;
			var column_name = [
				"EjercicioFiscal",
				"Ramo",
				"UnidadResponsable",
				"GrupoFuncional",
				"Funcion",
				"SubFuncion",
				"ProgramaGeneral",
				"ActividadInstitucional",
				"ProgramaPresupuestario",
				"Partida",
				"TipoGasto",
				"FuenteFinanciamiento",
				"EntidadFederativa",
				"Cartera",
				"UnidadEjecutora",
				"UnidadNorativa"
			];
			myWhere = "";
			token = "&qw=";
			
		 
		
			var charCode = evt.which ? evt.which : window.event.keyCode;
			if ((charCode != 8) && (charCode != 46)) { // No es backspace (8) y delete (46)?
				if (charCode <= 13) return true;

				if (charCode < 96 || charCode > 106) { // Es teclado numerico?
					var keyChar = String.fromCharCode(charCode);
					var re = /[a-zA-Z0-9.]/;
					if (!re.test(keyChar)) return true;
				}
			}
		
			 
		
			for (var i = 1; i < 17; i++) {
				value = $("#col"+i+"_filter" ).val();
				
				if (value !== "") {
					myWhere += token + column_name[i - 1] + " LIKE '%25" + value + "%25'";
					token = " AND ";
				}
			}
			
      
			oTable = createDataTable(myWhere);
				//oTable = createDataTable();
			return true;
		}
		
		function windowStatus( texto )
		{
			window.status=texto
		}
		function createDataTable(w) {
		
			var Pp = $('#Partida').val();
			var Pp2 = $('#Partida2').val();
			var Pp3 = $('#Partida3').val();
			
			/*
		    	UEt = UE.substring( UE.length - 2 );
			if (UEt >= 01 && UEt <= 14){
				Bandera=1;
			}
			else{
				Bandera=0;
			}
		   */
		   		
		   var where="((substring(Partida,1,1)='" + Pp + "') OR (substring(Partida,1,1)='" + Pp2 + "') OR (substring(Partida,1,1)='" + Pp3 + "'))" +w;
       
			return $("#tblEP").dataTable({
					"bDestroy": true,
					fnDrawCallback: function() {
						$(oCurrentFocus).focus(function() {
							if (this.createTextRange) {
								var r = this.createTextRange();
								r.collapse(false);
								r.select();
							}
							this.focus();
						});
						$(oCurrentFocus).focus();
					},
					bAutoWidth : false,
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
							sFirst:    "Primero",
							sPrevious: "Ant.",
							sNext:     "Sigte.",
							sLast:     "&Uacute;ltimo"
							}
					},
					bServerSide: true,
					sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vCatalogoEPGrid2&qw="+where,
					bProcessing: true,
					sPaginationType: "full_numbers",
					bJQueryUI: true,
					//aaSorting: [[ 1, "asc" ]] ,
					aoColumns: [
						{ sName: "EjercicioFiscal"   },
						{ sName: "Ramo" },
						{ sName: "UnidadResponsable"	},
						{ sName: "GrupoFuncional" },
						{ sName: "Funcion"   },
						{ sName: "SubFuncion" },
						{ sName: "ProgramaGeneral"	},
						{ sName: "ActividadInstitucional" },
						{ sName: "ProgramaPresupuestario"   },
						{ sName: "Partida" },
						{ sName: "TipoGasto"	},
						{ sName: "FuenteFinanciamiento" },
						{ sName: "EntidadFederativa"   },
						{ sName: "Cartera" },
						{ sName: "UnidadEjecutora"	},
						{ sName: "UnidadNorativa" }
					]
	        	});
		}
</script>
</head>
<body id="dt_example" >
	<form id="MultiReporteHija" name="MultiReporteHija">
		<div id="container" class="container">
			<h1>Estructura Programática</h1>
				<input id="ep" name="ep" type="hidden"/>
				<input type="hidden" id="Partida" name="Partida" value="2" class=""/>
				<input type="hidden" id="Partida2" name="Partida2" value="3" class=""/>
				<input type="hidden" id="Partida3" name="Partida3" value="5" class=""/>
				<input type="hidden" id="UE" name="UE" value="" class=""/>
				<input type="hidden" id="usuarioLoginReport" name="usuarioLoginReport" value="" />
				<input type="hidden" id="usuarioRoleReport" name="usuarioRoleReport" size="10" /> 
				<input type="hidden" id="usuarioUEReport" name="usuarioUEReport" size="10" /> 
				
				
				
			<label>Saldos Disponibles</label>
			<table id="tblSaldos" class="display"  >
	            <thead>
	                <tr>
	                	<th>Enero</th>
	                    <th>Febrero</th>
	                    <th>Marzo</th>
	                    <th>Abril</th>
	                    <th>Mayo</th>
	                    <th>Junio</th>
	                    <th>Julio</th>
	                    <th>Agosto</th>
	                    <th>Septiembre</th>
	                    <th>Octubre</th>
	                    <th>Noviembre</th>
	                    <th>Diciembre</th>
	                    <th>Anual</th>
	                </tr>
	            </thead>
	        </table>
	        
	        <br>
			<table id="tblEP" class="display"    class="display" >
	            <thead>
	                <tr>
					      <th>EF</th>
					      <th>Ra</th>
					      <th>UR</th>
					      <th>GF</th>
					      <th>Fu</th>
					      <th>SF</th>
					      <th>PG</th>
					      <th>AI</th>
					      <th>PP</th>
					      <th>Pa</th>
					      <th>TG</th>
					      <th>FF</th>
					      <th>EF</th>
					      <th>Ca</th>
					      <th>UE</th>
					      <th>UN</th>
	                </tr>
	            </thead>
	            	<tfoot>
						<tr>
							<th><input type="text" name="col1_filter" id="col1_filter"  onMouseOver="windowStatus('Ejercicio Fiscal');" /></th>
							<th><input type="text" name="col2_filter" id="col2_filter"  onMouseOver="windowStatus('Ramo');" /></th>
							<th><input type="text" name="col3_filter" id="col3_filter"  onMouseOver="windowStatus('Unidad Responsable');" /></th>
							<th><input type="text" name="col4_filter" id="col4_filter"  onMouseOver="windowStatus('Grupo Funcional');" /></th>
							<th><input type="text" name="col5_filter" id="col5_filter"  onMouseOver="windowStatus('Funcion');" /></th>
							<th><input type="text" name="col6_filter" id="col6_filter"  onMouseOver="windowStatus('SubFuncion');" /></th>
							<th><input type="text" name="col7_filter" id="col7_filter"  onMouseOver="windowStatus('Programa General');" /></th>
							<th><input type="text" name="col8_filter" id="col8_filter"   onMouseOver="windowStatus('Actividad Institucional');" /></th>
							<th><input type="text" name="col9_filter" id="col9_filter"  onMouseOver="windowStatus('Programa Presupuestario');" /></th>
							<th><input type="text" name="col10_filter" id="col10_filter"   onMouseOver="windowStatus('Partida');" /></th>
							<th><input type="text" name="col11_filter" id="col11_filter"   onMouseOver="windowStatus('Tipo de Gasto');" /></th>
							<th><input type="text" name="col12_filter" id="col12_filter"   onMouseOver="windowStatus('Fuente Financiamiento');" /></th>
							<th><input type="text" name="col13_filter" id="col13_filter"   onMouseOver="windowStatus('Entidad Federativa');" /></th>
							<th><input type="text" name="col14_filter" id="col14_filter"   onMouseOver="windowStatus('Cartera');" /></th>
							<th><input type="text" name="col15_filter" id="col15_filter"  onMouseOver="windowStatus('Unidad Ejecutora');"/></th>
							<th><input type="text" name="col16_filter" id="col16_filter"  onMouseOver="windowStatus('Unidad Norativa');"/></th>
						</tr>
					</tfoot>
	        </table>
			<br/>
			<center>
				<input type="button" id="pbAceptar" style="visibility: hidden" value ="Aceptar"/>&nbsp;&nbsp;&nbsp;
				<input type="button" id="pbCancelar" style="visibility: hidden" value ="Cancelar"/>
			</center>
		</div>
	</form>
	</body>
</html>