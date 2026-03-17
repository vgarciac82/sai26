<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page import="java.util.*" %>
<%
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	String UE=usuario.getU_UR();//UR comparar con unidad ejecutora
	boolean mntoCuentas=false;
	if(usuario.getPropiedades()!=null&&usuario.getPropiedades().containsKey("CCENTROCONTABLE")){
		mntoCuentas = "10".equals(usuario.getPropiedad("CCENTROCONTABLE").getValor());
	}
	Map rol =usuario.getRoles();
	//String partida=request.getParameter("partida");
	String contratoPedido=request.getParameter("idCntPedido");
	String unidadEjecutora=request.getParameter("ue");
	
	
	System.out.println("contratopedido" +contratoPedido);
	System.out.println("unidadejecutora" +unidadEjecutora);
	
	String cIdUsuarioCreacion = request.getParameter("cIdUsuarioCreacion"); 
	String cIdDocumento = request.getParameter("cIdDocumento"); 
	String isPasivo=request.getParameter("isPasivo")!=null? request.getParameter("isPasivo"):"0";
	String isPlurianual=request.getParameter("isPlurianual")!=null? request.getParameter("isPlurianual"):"0";
	String isContratoCap4=request.getParameter("isContratoCap4")!=null? request.getParameter("isContratoCap4"):"0";
	String cuentaDisp=request.getParameter("cuentaDisponible")!=null? request.getParameter("cuentaDisponible"):"82106";
	System.out.println(" es Pasivo : "+isPasivo+" es plurianual :"+isPlurianual);
	
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<title>Catálogo de Estructura Programática</title>
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
			@import "../css/interfaz.css";
			@import "css/demo_table.css"; 
		</style>
		
		<script type="text/javascript" src="../js/jquery-1.6.2.min.js"></script>
		<script type="text/javascript" src="../js/jquery.dataTables.js"></script>
		<script type="text/javascript" src="js/jquery.form-2.94.js"></script>
		<script type="text/javascript" src="../js/jquery-ui-1.8.16.custom.min.js"></script>
		<script type="text/javascript" src="js/jquery.ui.datepicker.js"></script>
		<script type="text/javascript" src="js/jquery.ui.core.js"></script>
		<script type="text/javascript" src="js/jquery.ui.widget.js"></script>
		<script type="text/javascript" src="js/jquery.ui.tabs.js"></script>
		<script type="text/javascript" src="js/crud.js"></script>
		<script type="text/javascript" src="js/funciones.js"></script>
		<script type="text/javascript" src="../js/catalogo/general.js"></script>
		<script type="text/javascript" src="../js/sweetalert/sw/sweetalert.min.js"></script>
		<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
		<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
	  	<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
	  	<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.all.js"></script>
		<script type="text/javascript" charset="UTF-8">
		
		var oTable,oTableEP, oCurrentFocus;
		var asInitVals = new Array();
		var arrRet = null;
  	    var aSelected = [0];
		var UE = "<%= usuario.getU_UR()%>";
		var Bandera="";
		var roles='';
		
		$(document).ready(function() {
			<%
			    String roles="";
				//botones
				
				Iterator it1 = rol.entrySet().iterator();
				while (it1.hasNext()) {
					Map.Entry r = (Map.Entry)it1.next();
					roles += r.getKey().toString()+",";
				}
				if(roles.length()>0){
					roles = roles.substring(0,roles.length()-1);
				}
				

				%> 
			roles="<%=roles%>";
			if (roles.indexOf("ADMIN_RECMAT") >= 0){
				$("#isAdmin").val(0);
			}
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
		    $("#usuarioLoginReport").val('<%=usuario.getLogin()%>');
		     queryFormPost("checaRolUsuarioMultiReport",{async:false});
			
			
			
			for (var i = 1; i < 17; i++)
				$("#col"+i+"_filter" ).keyup( function(evt) { 
					oCurrentFocus = this; fnFilterColumn(evt);
					$("#pbAceptar").css("visibility","hidden");
					$('#tblSaldos').dataTable().fnClearTable(); 
				});
			oTable = createDataTable("");
			$("#tblEP tbody").click(function(event) {
				
				$(oTable.fnSettings().aoData).each(function (){
					$(this.nTr).removeClass('row_selected');
				});
				$(event.target.parentNode).addClass('row_selected');
				$("#pbAceptar").css("visibility","visible");			
				$('#tblSaldos').dataTable().fnClearTable(); 
				var anSelected = fnGetSelected( oTable );
				var aData = oTable.fnGetData(anSelected[0]);
				var sEP = 	aData[0]+"." +
					aData[1]+"." +
					aData[2]+"." +
					aData[3]+"." +
					aData[4]+"." +
					aData[5]+"." +
					aData[6]+"." +
					aData[7]+"." +
					aData[8]+"." +
					aData[9]+"." +
					aData[10]+"." +
					aData[11]+"." +
					aData[12]+"." +
					aData[13]+"." +
					aData[14]+"." +aData[15];				
				var szWhere = " EP = '" + sEP + "' ";				
				var szTabla = "VSALDOSANUALES"; 
				if($("#cuentaDisponible").val()=='82109'){
					szTabla='VSALDOSANUALES_DISP_RAD';
				}
				                                                                                        
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
			$("#pbAceptar").button().click(function() {
					var anSelected = fnGetSelected( oTable );
					var aData = oTable.fnGetData(anSelected[0]);
	 				var sEP = 	aData[0]+"." +
						aData[1]+"." +
						aData[2]+"." +
						aData[3]+"." +
						aData[4]+"." +
						aData[5]+"." +
						aData[6]+"." +
						aData[7]+"." +
						aData[8]+"." +
						aData[9]+"." +
						aData[10]+"." +
						aData[11]+"." +
						aData[12]+"." +
						aData[13]+"." +
						aData[14]+"." +aData[15];
	 				$("#ep").val(sEP);
					window.opener.document.forms[0].elements["ep"].value=window.MultiReporteHija.ep.value ;
					window.close();
			});		
			$("#pbCancelar").button().click(function() {
				window.close();
			});
			
			$(window).bind('resize', function (){
				if($('#tblEP >tbody >tr').length>0){
					oTableEP.fnAdjustColumnSizing();
				}
			});
		});
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
			return true;
		}
		function windowStatus( texto )
		{
			window.status=texto
		}
		function createDataTable(w) {
			window.MultiReporteHija.Partida.value=window.opener.document.forms[0].elements["Partida"].value;
			window.MultiReporteHija.Partida2.value=window.opener.document.forms[0].elements["Partida2"].value;
			window.MultiReporteHija.Partida3.value=window.opener.document.forms[0].elements["Partida3"].value;
			window.MultiReporteHija.Partida1.value=window.opener.document.forms[0].elements["Partida1"].value;
			
			var Pp = $('#Partida').val();
			var Pp2 = $('#Partida2').val();
			var Pp3 = $('#Partida3').val();
			var Pp4 = $('#Partida1').val();
		  	var qw="1=1";
		   	queryFormPost("tCatalogoUnidadEjecutoraReadVistasCadena",{async:false});
		   	if($("#UES_USUARIO").val()!=''){
		   		qw=qw+" and UnidadEjecutora in("+$("#UES_USUARIO").val()+")";	
		   	}
			//para filtrar dependiendo del rol	y la unidad ejecutora			
			$('#usuarioUEReport').val('<%=usuario.getU_UR()%>');
			//var where="((substring(Partida,1,1)='" + Pp + "') OR (substring(Partida,1,1)='" + Pp2 + "') OR (substring(Partida,1,1)='" + Pp3 + "'))"+ w;
			 
			var where = ' 1 = 1 ' + w + ' ';	
			var uePartida=$("#uePartidasVisibles").val().split('.');
			var funcion="";
			//alert($("#isPasivo").val());
			if($("#isPasivo").val()=='0'){
				if($("#isPlurianual").val()=='0'){
					if($("#isContratoCap4").val()=='0'){
						funcion="fn_EpsConMontoAnualMod2";
					}else{
						funcion="fn_EpsConCap4MontoAnualMod2";
					}
				}else{
					funcion="fn_EpsConMontoAnualMod2Plurianual";
				}
				
			}else{
				funcion="fn_EpsConMontoAnualMod2Pasivo";
			}
			return oTableEP= $("#tblEP").dataTable({
					
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
					bScrollCollapse: true,
	        		bInfo: false,
					sScrollX: "100%",
					bAutoWith: true,
					bJQueryUI: true,
					bRetrive : true,
					bDestroy : true,
					sPaginationType: "full_numbers",
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
					
					sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql="+funcion+"('"
					+$('#isAdmin').val()+"','"+$('#cIdUnidadEjecutoraUsuario').val()
 					+"','"+$('#U_LOGIN').val()+"','"+$('#modulo').val()+"','"+$('#cIdDocumento').val()+"','"+$('#cuentaDisponible').val()
					+"')&qw="+where,
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
		<div id="container" class="container" style="width: 95%">
			<h1>Estructura Programática</h1>
				<input id="ep" name="ep" type="hidden"/>
				<input type="hidden" id="Partida" name="Partida" value="" class=""/>
				<input type="hidden" id="Partida2" name="Partida2" value="" class=""/>
				<input type="hidden" id="Partida3" name="Partida3" value="" class=""/>
				<input type="hidden" id="Partida1" name="Partida1" value="" class=""/>
				<input type="hidden" id="UE" name="UE" value="" class=""/>
				<input type="hidden" id="usuarioLoginReport" name="usuarioLoginReport" value="" />
				<input type="hidden" id="usuarioRoleReport" name="usuarioRoleReport" size="10" /> 
				<input type="hidden" id="usuarioUEReport" name="usuarioUEReport" size="10" /> 
				
				<input type="hidden" id="cIdDocumento" name="cIdDocumento" size="10" value="<%=cIdDocumento%>" /> 
				<input type="hidden" id="uePartidasVisibles" name="uePartidasVisibles" /> 
				<input type="hidden" id="cIdUsuarioCreacion" name="cIdUsuarioCreacion" size="10" value="<%=cIdUsuarioCreacion%>" /> 
				<input type="hidden" name="cIdUnidadEjecutoraUsuario" id="cIdUnidadEjecutoraUsuario" value="<%= usuario.getU_UR() %>" />
				<input type="hidden" name="modulo" id="modulo" value="MATERIALES" />
				<input type="hidden" name="isAdmin" id="isAdmin" value="1" />
				<input type="hidden" name="U_LOGIN" id="U_LOGIN" value="<%= usuario.getLogin() %>"/>
				<input type="hidden" name="UES_USUARIO" id="UES_USUARIO" value="" />
				<input type="hidden" name="isPasivo" id="isPasivo" value="<%=isPasivo %>" />
				<input type="hidden" name="isPlurianual" id="isPlurianual" value="<%=isPlurianual %>" />
				<input type="hidden" name="isContratoCap4" id="isContratoCap4" value="<%=isContratoCap4 %>" />
				<input type="hidden" name="cuentaDisponible" id="cuentaDisponible" value="<%=cuentaDisp %>" />
			<br />	
			<label style="POSITION: relative; TOP:-2px; LEFT:15px;color: blue;">Saldos Disponibles</label>
	        <br>
	        <div style="height: 100px;">
			<table id="tblSaldos" class="display" style="width: 100%;height: 50%" >
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
	        </div>
	        <label style="POSITION: relative; TOP:-2px; LEFT:15px; color: blue;">Estructuras programaticas disponibles</label>
	        <br>
			<table id="tblEP" class="display"  style="width: 100%">
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
				<input type="button" id="pbAceptar" name="pbAceptar" style="visibility: hidden" value ="Aceptar"  class="btnInterfaceBG ui-button ui-corner-all"  />&nbsp;&nbsp;&nbsp;
				<input type="button" id="pbCancelar" name="pbCancelar"  style="visibility: hidden" value ="Cancelar"  class="btnInterfaceBG ui-button ui-corner-all"  />
			</center>
		</div>
	</form>
	</body>
</html>