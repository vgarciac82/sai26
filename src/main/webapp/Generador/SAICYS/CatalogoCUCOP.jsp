<%@page import="com.syc.gestion.core.Role"%>
<%@page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="com.syc.admin.servlet.*"%>
<%@ page import="java.util.*" %>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	Map rol =usuario.getRoles();
	
	boolean mntoCuentas=false;
	if(usuario.getPropiedades()!=null&&usuario.getPropiedades().containsKey("CCENTROCONTABLE")){
		mntoCuentas = "10".equals(usuario.getPropiedad("CCENTROCONTABLE").getValor());
	}
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    
    
    <title>CatalogoCUCOP</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	
		<link rel="shortcut icon" type="image/ico" href="../imagenes/favicon.ico" />

		<style type="text/css" title="currentStyle">
			@import "../themes/smoothness/jquery-ui-1.8.4.custom.css";
			@import "../css/demo_table_jui.css";
			@import "../css/demo_page.css";
		</style>
		
		<script type="text/javascript" src="../js/jquery-1.6.2.min.js"></script>
		<script type="text/javascript" src="../js/jquery.dataTables.js"></script>
		<script type="text/javascript" src="../js/jquery.form-2.94.js"></script>
		<script type="text/javascript" src="../js/jquery-ui-1.8.16.custom.min.js"></script>
		<script type="text/javascript" src="../js/jquery.ui.datepicker.js"></script>
		<script type="text/javascript" src="../js/jquery.ui.core"></script>
		<script type="text/javascript" src="../js/jquery.ui.widget.js"></script>
		<script type="text/javascript" src="../js/jquery.ui.tabs.js"></script>
		<script type="text/javascript" src="../js/crud.js"></script>
		<script type="text/javascript" src="../../js/catalogo/general.js"></script>

		<script type="text/javascript" charset="utf-8">
		var oTable;
			
		$(document).ready(function() {
			//Obtengo el rol del usuario
			
			queryFormPost("checaRolUsuario",{async:false});
			queryFormPost("esAdminRecMat",{async:false});
			if( parseInt( $('#esAdminRecMat').val(), 10 ) == 0 ){
				document.A.btnAgregar.style.display ='none';
				document.A.btnBorrar.style.display ='none';
				document.A.btnCambiar.style.display ='none';
			}
			mostrar();			
			$("#tblcucop tbody").click(function(event) {
				$(oTable.fnSettings().aoData).each(function (){
						$(this.nTr).removeClass('gradeA');
					});
					
				$(event.target.parentNode).addClass('gradeA');
		 		
					
				var aTrs = $('#tblcucop').dataTable().fnGetNodes();
				//alert("Yaaaa");
				for ( var i=aTrs.length ; i>=0; i-- )     
					{  
						//alert("0");
						if ( $(aTrs[i]).hasClass('gradeA') )         
						{   
							var nTr = $('#tblcucop').dataTable().fnGetData(aTrs[i]);
							$("#cucop").val(nTr[0]);
							$("#descripcucop").val(nTr[1]);
							
							$("#partida").val(nTr[4]);
							$("#capitulo").val(nTr[3]);
							
							$("#um").val(nTr[2]);
							$("#tipoproceso").val(nTr[5]);
							
							$("#ccaop").val(nTr[6]);
							$("#cabm").val(nTr[7]);
							
							$("#cabmsop").val(nTr[8]);
							$("#descipcioncabmsop").val(nTr[9]);
							//llenar Hidens
							llenaHidens();
							$("#cIdUM").val(nTr[2]);
						}     
					}
			});
			
		});
		
	
function mostrar() {
				var qw = " 1 = 1";
				
				if ($("#cucop").val() != "" )
					qw += " AND cIdCABM like '%25" + $("#cucop").val() + "%25'";
				if ($("#descripcucop").val() != "" )
					qw += " AND cCABM like '%25" + $("#descripcucop").val() + "%25'";
				if ($("#um").val() != "" )
					qw += " AND cIdUnidadMedida like '%25" + $("#um").val() + "%25'";
				if ($("#capitulo").val() != "" )
					qw += " AND cCapitulo like '%25" + $("#capitulo").val() + "%25'";
				if ($("#partida").val() != "" )
					qw += " AND cSubPartida like '%25" + $("#partida").val() + "%25'";
				
				oTable = $("#tblcucop").dataTable({
				sScrollX: "100%",
				sScrollXInner: "210%", 
				bScrollCollapse: true,
				bDestroy: true,
				bAutoWidth : false,
				//"iDisplayLength": 17, //Cuantos registros se despliegan
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
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=[v_mCatalogocucop]&qw="+qw,
				bProcessing: true,
				//Tablesorter:false,
				//bSortable: false
				sPaginationType: "full_numbers",
				bJQueryUI: true,
				aaSorting: [[ 0, "asc" ]] ,
				aoColumns: [
					
					{ sName: "cIdCABM" },
					{ sName: "cCABM" },
					{ sName: "cIdUnidadMedida"  },
					{ sName: "cCapitulo"	},
					{ sName: "cSubPartida" },
					{ sName: "cTipoProceso" },
					{ sName: "cCCAOP" },
					{ sName: "cIdCUCOP" },
					{ sName: "nIdCABMSOP" },
					{ sName: "cDescripcion" }
					
							
					]
        	});
				
				
		}
		function Buscar(){
			mostrar();
		}
		function Agregar(){
			//&& $("#descripcucop").val() != "" && $("#cIdCapitulo").val() != ""
			
			if($("#cucop").val() != ""){
				//alert($("#cucop").val());
				if($("#descripcucop").val() != ""){
					if($("#cIdSubPartida").val() != ""){
						if($("#cIdCapitulo").val() != ""){
							if($("#cIdUM").val() != ""){
								if($("#nIdTipoProceso").val() != ""){
									if($("#cIdCCAOP").val() != ""){
										queryFormPost("mCatalogoCABMExiste",  {async : false});
										//alert($("#existeCABM").val());
										if($("#existeCABM").val()!='EXISTE'){
											//llenaHidens();
											if($("#cabmsop").val() == "")
												$("#cabmsop").val('1');
											queryFormPost("mcatalogoCABMInsert",  {async : false});
											alert("Datos Guardados");
											Limpiar();
										}
										else{
											alert("El CABM ya existe, insertar otro");
											$("#existeCABM").val('');
										}
											
									}
									else
										alert("El  ccaop es Requerido, utilizar el boton ... para llenar el campo");
								}
								else
									alert("El tipo de proceso es Requerido, utilizar el boton ... para llenar el campo");
							}
							else
								alert("La unidad de meida es Requerida, utilizar el boton ... para llenar el campo");
						}
						else
							alert("El Capitulo es Requerido, utilizar el boton ... para llenar el campo");
					}
					else
						alert("La partida es Requerida, utilizar el boton ... para llenar el campo");
				}
				else
					alert("Descripcion del cucop Requerido");
				
			}
			else
				alert("Cucop Requerido");
			
			
		}
		function Borrar(){
			if($("#cucop").val() != ""){
				queryFormPost("mCatalogoCABMExiste",  {async : false});
				if($("#existeCABM").val()=='EXISTE'){
					if(window.confirm("Esta seguro de Borrar el cucop '"+$("#cucop").val()+"' del Catalogo?")){
						queryFormPost("mProgramaAnualDetalleExiste",  {async : false});
						//alert(parseInt($("#conteo").val()));
						if(parseInt($("#conteo").val(),10)==0){
							queryFormPost("mcatalogoCABMDelete",  {async : false});
							alert("Cucop Borrado");	
							Limpiar();
						}
						else
							alert("No se puede borrar por que ya se esta usando");
					}
					
				}
				else{
					LimpiaHidens();
					alert("No puedes Borrar algo que no existe");
				}
					
			}
			else
				alert("Selecciona el Cucop a borrar");
		}
		
		function Cambiar(){
			if($("#cucop").val() != ""){
				queryFormPost("mCatalogoCABMExiste",  {async : false});
				if($("#existeCABM").val()=='EXISTE'){
					if($("#cIdSubPartida").val() != ""){
						if($("#cIdCapitulo").val() != ""){
							if($("#cIdUM").val() != ""){
								if($("#nIdTipoProceso").val() != ""){
									if($("#cIdCCAOP").val() != ""){
										if($("#cabmsop").val() == "")
											$("#cabmsop").val('1');
										queryFormPost("mcatalogoCABMUpdate",  {async : false});
										alert("Cucop Actualizado");	
										Limpiar();
								
									}
									else
										alert("Utilizar el boton ... para llenar el campo");
								}
								else
									alert("Utilizar el boton ... para llenar el campo");
							}
							else
								alert("Utilizar el boton ... para llenar el campo");
						}
						else
							alert("Utilizar el boton ... para llenar el campo");
					}
					else
						alert("Utilizar el boton ... para llenar el campo");		
				}
				else{
					LimpiaHidens();
					alert("No puedes Actualizar algo que no existe");
				}
					
			}
			else
				alert("Selecciona el Cucop a Actualizar");
		}
		function Limpiar(){
			$("#cucop").val('');
			$("#descripcucop").val('');
			
			$("#partida").val('');
			$("#capitulo").val('');
			$("#um").val('');
			$("#tipoproceso").val('');
			
			$("#ccaop").val('');
			$("#cabm").val('');
			
			$("#cabmsop").val('');
			$("#descipcioncabmsop").val('');
			
			$("#existeCABM").val('');
			$("#cIdCapitulo").val('');
			$("#nIdTipoProceso").val('');
			$("#cIdSubPartida").val('');
			$("#cIdCCAOP").val('');
			LimpiaHidens();
			mostrar();
		}
		function bPartida(){
			window.open("../SAICYS/txtGridcSubPartida.jsp","Partida","status=1, width=600px, height=600px, left=150px,scrollbars=1 ");
			//src="../../Ayudas/xml-ayudas/txtGridcSubPartida.xml"
		}
		function bUM(){
			window.open("../SAICYS/UnidadMedida.jsp","Partida","status=1, width=600px, height=600px, left=150px,scrollbars=1 ");
			//src="../../Ayudas/xml-ayudas/txtGridcSubPartida.xml"
		}
		function bCCAOP(){
			window.open("../SAICYS/CCAOP.jsp","Partida","status=1, width=600px, height=600px, left=150px,scrollbars=1 ");
			
		}
		function bCABMSOP(){
			window.open("../SAICYS/CABMSOP.jsp","Partida","status=1, width=600px, height=600px, left=150px,scrollbars=1 ");
			
		}
		function bCAPITULO(){
			window.open("../SAICYS/capitulo.jsp","Partida","status=1, width=600px, height=600px, left=150px,scrollbars=1 ");
			
		}
		function bTPROCESO(){
			window.open("../SAICYS/TipoProceso.jsp","Partida","status=1, width=600px, height=600px, left=150px,scrollbars=1 ");
			
		}
		function onlyNumbers2(evt) {
			var keyPressed = (evt.which) ? evt.which : event.keyCode
			var strCheck = '0123456789';
		
			var key = String.fromCharCode( keyPressed );
			if (strCheck.indexOf( key ) == -1)
				return false; // Valida que sea numero y punto decimal
		
			return true 
			//!(keyPressed > 31 && (keyPressed < 48 || keyPressed > 57));
		}
		function llenaHidens(){
			if($("#cIdCapitulo").val()==''){
				//var i=$("#capitulo").val().indexOf(' ');
				var a=$("#capitulo").val().substring(0, 1);
				$("#cIdCapitulo").val(a);
				//alert($("#cIdCapitulo").val());
			}
			if($("#nIdTipoProceso").val()==''){
				var i=$("#tipoproceso").val().indexOf('-');
				var a=$("#tipoproceso").val().substring(0, i);
				$("#nIdTipoProceso").val(a);
				//alert($("#cIdCapitulo").val());
			}
			if($("#cIdSubPartida").val()==''){
				var i=$("#partida").val().indexOf('-');
				var a=$("#partida").val().substring(0, i);
				$("#cIdSubPartida").val(a);
				//alert($("#cIdCapitulo").val());
			}
			if($("#cIdCCAOP").val()==''){
				var i=$("#ccaop").val().indexOf('-');
				var a=$("#ccaop").val().substring(0, i);
				$("#cIdCCAOP").val(a);
				//alert($("#cIdCCAOP").val());
			}
		}
		function LimpiaHidens(){
			$("#cIdCapitulo").val('');
			$("#cIdCCAOP").val('');
			$("#cIdSubPartida").val('');
			$("#nIdTipoProceso").val('');
			$("#existeCABM").val('');
			$("#cIdUM").val('');
			$("#conteo").val('0');
		}
</script>
  </head>
  
  <body>

  <fieldset>
  
		<legend>Mantenimiento Catalogo Cucops</legend>
		  <form action="" name="A" id="A">
		    	<input title="Buscar" type="button" name="btnBuscar" id="btnBuscar" onclick="Buscar()" style="width: 80px;height:28px ;background-image: url('../../Ayudas/imagenes/buscar_d.png')" >
				<input title="Agregar" type="button" name="btnAgregar" id="btnAgregar" onclick="Agregar()" style="width: 85px;height:28px ;background-image: url('../../Ayudas/imagenes/agregar_d.png')" >
				<input title="Borrar" type="button" name="btnBorrar" id="btnBorrar" onclick="Borrar()" style="width: 80px;height:28px ;background-image: url('../../Ayudas/imagenes/borrar_d.png')">
				<input title="Actualizar" type="button" name="btnCambiar" id="btnCambiar" onclick="Cambiar()" style="width: 86px;height:28px ;background-image: url('../../Ayudas/imagenes/Cambiar_d.png')">
				<input title="Limpiar" type="button" name="btnLimpiar" id="btnLimpiar" onclick="Limpiar()" style="width: 80px;height:28px ;background-image: url('../../Ayudas/imagenes/Limpiar_d.png')">
				
				<input type="hidden" name="esAdminRecMat" id="esAdminRecMat" value="">
				<input type="hidden" name="usuarioRole" id="usuarioRole" value="">
				<input type="hidden" name="UE" id="UE" value="<%=usuario.getU_UR()%>">
				<input type="hidden" name="usuarioLogin" id="usuarioLogin" value="<%=usuario.getLogin()%>">
				
				<input type="hidden" name="existeCABM" id="existeCABM" value="" />
				<input type="hidden" name="cIdCapitulo" id="cIdCapitulo" value="" />
				<input type="hidden" name="nIdTipoProceso" id="nIdTipoProceso" value="" />
				<input type="hidden" name="cIdSubPartida" id="cIdSubPartida" value="" />
				<input type="hidden" name="cIdCCAOP" id="cIdCCAOP"  value=""/>
				<input type="hidden" name="cIdUM" id="cIdUM"  value=""/>
				<input type="hidden" name="conteo" id="conteo"  value="0"/>
				
				
		  </form>
		
	    <form action="" name="B" id="B">
	   		<table width="100%" border="0">
	   			<tr width="50%">
	   				<td >* Cucops:</td><td></td>
	   				<td >* Descripcion:</td>
	   				   				
	   			</tr>
	   			<tr width="50%">
	   				<td><input type="text" name="cucop" id="cucop" maxlength="11" onkeypress="return(onlyNumbers2(event));">
	   					
	   				</td>
	   				<td></td>
	   				<td width="50%"><input type="text" name="descripcucop" id="descripcucop" style="width: 80%;" maxlength="2000"></td>
	   				
	   			</tr>
	   			
	   			<tr width="50%">
	   				<td>* Partida:</td><td></td>
	   				<td>* Capitulo:</td>
	   				   				
	   			</tr>
	   			<tr width="50%">
	   				<td><input type="text" name="partida" id="partida" style="width: 80%;" maxlength="100">
	   					<input type="button" name="btnpartida" id="btnpartida" value="..." onclick="bPartida()" >
	   				</td>
	   				<td></td>
	   				<td><input type="text" name="capitulo" id="capitulo" style="width: 80%;" maxlength="110">
	   					<input type="button" name="btncapitulo" id="btncapitulo" value="..." onclick="bCAPITULO()">
	   				</td>
	   				
	   			</tr>
	   			
	   			<tr width="50%">
	   				<td>* Unidad de Medida:</td>
	   				<td></td>
	   				<td>* Tipo Proceso:</td>
	   				   				
	   			</tr>
	   			<tr width="50%">
	   				<td><input type="text" name="um" id="um" maxlength="5">
	   					<input type="button" name="btnum" id="btnum" value="..." onclick="bUM()">
	   				</td>
	   				<td></td>
	   				<td><input type="text" name="tipoproceso" id="tipoproceso" style="width: 80%;" maxlength="100">
	   					<input type="button" name="btntipoproceso" id="btntipoproceso" value="..." onclick="bTPROCESO()">
	   				</td>
	   				
	   			</tr>
	   			<tr width="50%">
	   				<td>* CCAOP:</td>
	   				<td></td>
	   				<td>CABM:</td>
	   				   				
	   			</tr>
	   			<tr width="50%">
	   				<td><input type="text" name="ccaop" id="ccaop" style="width: 80%;" maxlength="100" >
	   					<input type="button" name="btnccaop" id="btnccaop" value="..." onclick="bCCAOP()">
	   				</td>
	   				<td></td>
	   				<td><input type="text" name="cabm" id="cabm" maxlength="10"></td>
	   				
	   			</tr>
	   			<tr width="50%">	
	  				<td>CABMSOP:</td>
	  				<td></td>
	   				<td>Descripcion CABMSOP:</td>
	   			</tr>
	   			<tr width="50%">	
	   				<td><input type="text" name="cabmsop" id="cabmsop" maxlength="10" onkeypress="return(onlyNumbers2(event));">
	   					<input type="button" name="btncabmsop" id="btncabmsop" value="..." onclick="bCABMSOP()">
	   				</td>
	   				<td></td>
	   				<td><input type="text" name="descipcioncabmsop" id="descipcioncabmsop" style="width: 80%;" maxlength="80"></td>	
	   				
	   			</tr>
	   		</table>
	   		
	 		
	   </form>
   </fieldset>
   <table id="tblcucop" class="display">
	            <thead>
	                <tr>
	    				<th width="5%" >CUCOP</th>
	                	<th width="15%" >DESCRIPCION</th>
	                	<th width="5%">U.M.</th>	                	
	                   
	                    <th width="15%">CAPITULO</th>
	                   <th width="15%">PARTIDA</th>
	                   <th width="10%">TIPO DE PROCESO</th>
	                   
	                   <th width="5%">CCAOP</th>
	                   
	                   <th width="5%">CABM</th>
	                   <th width="5%">CABMSOP</th>
	                   <th width="15%">DESCRIPCION DEL CABMSOP</th>
	                  
	                </tr>
	            </thead>
	        </table>	
  </body>
</html>
