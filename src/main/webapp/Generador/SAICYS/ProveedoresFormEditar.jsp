<%@page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	boolean mntoCuentas=false;
	if(usuario.getPropiedades()!=null&&usuario.getPropiedades().containsKey("CUENTAS_BANCARIAS_BENEFICIARIOS")){
		mntoCuentas = "SI".equals(usuario.getPropiedad("CUENTAS_BANCARIAS_BENEFICIARIOS").getValor());
	}
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<title>Proveedores</title>

		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">
		<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
		<meta http-equiv="description" content="Catálogo de Beneficiarios">

		<link rel="shortcut icon" type="image/ico" href="../imagenes/favicon.ico" />

		<style type="text/css" title="currentStyle">
			@import "../themes/smoothness/jquery-ui-1.8.4.custom.css";
			@import "../css/demo_table_jui.css";
			@import "../css/demo_page.css";
		</style>

		<style>			// estilos del dialogo
			div#dialog-form fieldset { padding:0; border:0; margin-top:25px; }
			

			div#users-contain { width: 350px; margin: 20px 0; }
			div#users-contain table { margin: 1em 0; border-collapse: collapse; width: 100%; }
			div#users-contain table td, div#users-contain table th { border: 1px solid #eee; padding: .6em 10px; text-align: left; }
			.ui-dialog .ui-state-error { padding: .3em; }
			.validateTips { border: 1px solid transparent; padding: 0.3em; }
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
		
		$(document).ready(function() {
			$( "#aTab1" ).attr("disabled", true);
			if($("#editar_proveedor").val() != null &&$("#editar_proveedor").val() != "null"){				
				$("#editar_proveedor").val($("#editar_proveedor").val().replace('_=_', '&'));
				//alert($("#editar_proveedor").val());
				var h_rfc = $("#editar_proveedor").val();									
				var rfc1 = h_rfc.split("-");
				$("#cIdRFC1").val(rfc1[0]);
				$("#cIdRFC2").val(rfc1[1]);
				$("#cIdRFC3").val(rfc1[2]);
				cCURP = $( "#inputCurp" ),
				document.getElementById("cIdRFC1").disabled = true;
				document.getElementById("cIdRFC2").disabled = true;
				document.getElementById("cIdRFC3").disabled = true;
				if(rfc1[0].toString().replace(" ","").length == 3){					
					document.getElementById("tipoPersonaM").checked=true;
					$("#trCurp").hide();
					$("#isPersonaFisica").val(0);		
				}else if(rfc1[0].toString().replace(" ","").length== 4){					
					document.getElementById("tipoPersonaF").checked = true;	
					$("#trCurp").show();
					$("#isPersonaFisica").val(1);				
				}
				querySelectPost("mPymeRead", "nIdPyme", {async : false});
				queryFormPost("vProveedoresFormRead", "editar_proveedor", {async : false});					
			}
			$('#tblTelefonoProveedor').dataTable().fnClearTable();
			$('#tblTelefonoProveedor').dataTable(
		 		 {         
					 sScrollX: "100%",
					 sScrollXInner: "100%",
					 bScrollCollapse: true,
					 bDestroy: true,
					 oLanguage: {
						   //sProcessing: "Procesando...",
						  // sLengthMenu: "Mostrar _MENU_ registros",
						   //sZeroRecords: "No hay registros a mostrar",
						   //sEmptyTable: "No hay datos en la tabla",
						   //sLoadingRecords: "Cargando...",
						  // sInfo: "Registros _START_ al _END_ de _TOTAL_",
						  // sInfoEmpty: "Registro 0 al 0 de 0",
						   //sInfoFiltered: "(filtado de _MAX_ registros)",
						   //sInfoPostFix: "",
						   //sInfoThousands: ",",
						   //sSearch: "Buscar:",
						  /* oPaginate: {
										sFirst:    "Primero",
										sPrevious: "Ant.",
										sNext:     "Sigte.",
										sLast:     "&Uacute;ltimo"
										}*/
							},				
					bProcessing: true,					
					bPaginate: false,
					bLengthChange: false,
					bFilter: false,
					//bSort: false, 
					bInfo: false,
			        //bAutoWidth: false,					
					sPaginationType: "full_numbers",
					bJQueryUI: true,
					//aaSorting: [[ 1, "asc" ]] ,
					bAutoWidth: false,
					bServerSide: true,
					 sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vProveedoresTelefonoGrid&qw=cIdRFC='" + h_rfc + "'",
					 aaSorting: [[ 0, "asc" ]] ,
					 aoColumns: [
					{ sName: "nIdTelefono"   },
					{ sName: "cTipoTelefono"   },
					{ sName: "cTelefono"   },
					{ sName: "Eliminar"   }
					]
				});
			querySelectPost("mCatalogoEntidadFederativaRead", "cIdEntidadFederativa", {async : false});
			querySelectPost("mCatalogoTipoTelefonoRead", "cTipoTelefono", {async : false});						
			
			$( "#dialog:ui-dialog" ).dialog( "destroy" );
			var cIdRFC1 = $( "#cIdRFC1" ),
				cIdRFC2 = $( "#cIdRFC2" ),
				cIdRFC3 = $( "#cIdRFC3" ),
				cIdRFC = $( "#cIdRFC" ),
				cRazonSocial = $( "#cRazonSocial" ),
				cRepresentanteLegal = $("#cRepresentanteLegal"),
				cGiro = $("#cGiro"),								
				cMunicipio = $("#cMunicipio"),
				cCalle = $("#cCalle"),
				cNumeroExterno = $("#cNumeroExterno"),				
				cColonia = $( "#cColonia" ),
				cCodigoPostal = $("#cCodigoPostal"),
				cTelefono = $("#cTelefono"),	
				allFields = $( [] ).add( cIdRFC ).add( cRazonSocial ).add( cRepresentanteLegal ).add( cGiro ).add(cMunicipio).add(cCalle).add(cNumeroExterno).add( cColonia ).add( cCodigoPostal ).add( cTelefono ),
				tips = $( ".validateTips" );
			function updateTipsDlg( t ) {
				tips
					.text( t );
					alert(t);
				//	.addClass( "ui-state-highlight" );
				// setTimeout(function() { tips.removeClass( "ui-state-highlight", 1500 );}, 500 );
			}

			function checkLength( o, n, min, max ) {
				if ( o.val().length > max || o.val().length < min ) {
					o.addClass( "ui-state-error" );
					if (min == max)
						updateTipsDlg( "La longitud de " + n + " debe ser de " + min + " caracteres." );
					else
						updateTipsDlg( "La longitud de " + n + " debe estar entre " + min + " y " + max + "." );
					o.focus();
					return false;
				} else {
					return true;
				}
			}
			function checkRequerido( o, n) {
			   var vTemp=o.val();
				var sTemp = $.trim(vTemp);
				//o.val(sTemp);
				if ( sTemp.length == 0  ) {
					o.addClass( "ui-state-error" );
					updateTipsDlg(  n + " es un dato requerido." );
					o.focus();
					return false;
				} else {
					return true;
				}
			}

			function checkRegexp( o, regexp, n ) {
				if ( !( regexp.test( o.val() ) ) ) {
					o.addClass( "ui-state-error" );
					updateTipsDlg( n );
					return false;
				} else {
					return true;
				}
			}
			
			$("#btnGuardar")
				.button()
				.click(function() {
					$("#cIdRFC").val($("#cIdRFC1").val()+"-"+$("#cIdRFC2").val()+"-"+$("#cIdRFC3").val());
					
					var bValid = true;
						tips.text("");
						allFields.removeClass( "ui-state-error" );
	
						bValid = bValid && checkRequerido( cIdRFC1, "cIdRFC1" );
						bValid = bValid && checkLength( cIdRFC1, "cIdRFC1", 3, 4 );
						
						bValid = bValid && checkRequerido( cIdRFC2, "cIdRFC2" );
						bValid = bValid && checkLength( cIdRFC2, "cIdRFC2", 6,6 );
						
						bValid = bValid && checkRequerido( cIdRFC3, "cIdRFC3" );
						bValid = bValid && checkLength( cIdRFC3, "cIdRFC3", 3, 3 );
						
						if($("#isPersonaFisica").val()==1){
                        	bValid = bValid && checkRequerido( cCURP, "inputCurp" );
							bValid = bValid && checkLength( cCURP, "inputCurp", 18,18 );
                        }
                        
						bValid = bValid && checkRequerido( cRazonSocial, "cRazonSocial" );
						bValid = bValid && checkLength( cRazonSocial, "cRazonSocial", 1, 300 );
						
																		
// 						bValid = bValid && checkRequerido( cRepresentanteLegal, "cRepresentanteLegal" );
 						bValid = bValid && checkLength( cRepresentanteLegal, "cRepresentanteLegal", 0, 100 );
						
						
						bValid = bValid && checkRequerido( cGiro, "cGiro" );
						bValid = bValid && checkLength( cGiro, "cGiro", 1, 500 );
						
																						
						bValid = bValid && checkRequerido( cMunicipio, "cMunicipio" );
						bValid = bValid && checkLength( cMunicipio, "cMunicipio", 1, 50 );
						
						
						bValid = bValid && checkRequerido( cCalle, "cCalle" );
						bValid = bValid && checkLength( cCalle, "cCalle", 1, 100 );
						
						
						bValid = bValid && checkRequerido( cNumeroExterno, "cNumeroExterno" );
						bValid = bValid && checkLength( cNumeroExterno, "cNumeroExterno", 1, 50 );
						
						
						
						bValid = bValid && checkRequerido( cColonia, "cColonia" );
						bValid = bValid && checkLength( cColonia, "cColonia", 1, 100 );
						
						
						bValid = bValid && checkRequerido( cCodigoPostal, "cCodigoPostal" );
						bValid = bValid && checkLength( cCodigoPostal, "cCodigoPostal", 1, 5 );
						bValid = bValid && checkRegexp( cCodigoPostal, /^([0-9])+$/, "cCodigoPostal solo permite números : 0-9" );
						
						if($("#cTelefono").val()!= ''){
							bValid = bValid && checkLength( cTelefono, "cTelefono", 1, 20 );
							bValid = bValid && checkRegexp( cTelefono, /^([0-9])+$/, "cTelefono solo permite números : 0-9" );
							if (bValid){	
								queryFormPost("ConsecutivoNumTelProveedorRead", {async : false});												
								queryFormPost("mCatalogoProveedorTelefonoCreate",  {async : false});							
								$("#cTelefono").val('');
								$('#tblTelefonoProveedor').dataTable().fnClearTable();
							}
						}
					if (bValid){						
						$("#h_habilitado").val($("#lHabilitado").val()); 
						queryFormPost("mCatalogoProveedorUpdate",  {async : false});		
						alert("Registro Actualizado del Proveedor");
						location.href = 'Proveedores.jsp?tab=1&rfc='+$( "#cIdRFC" ).val();
					}
			});			
			
			$("#btnSalir")
				.button()
				.click(function() {
					location.href = 'Proveedores.jsp?tab=0';				
			});			
			
		setTimeout("cargarHabilitado()",1000);
		
		});
		function cambioLongitud(str_tipo){
			var tipo = str_tipo;
			
			if(tipo==1){				
				document.getElementById('cIdRFC1').maxLength= 4;				
			}
			if (tipo==2){					
				$("#cIdRFC1").val($("#cIdRFC1").val().substring(0,3));				
				document.getElementById('cIdRFC1').maxLength= 3;				
			}									
		}
		function cargarHabilitado(){			
			if(document.getElementById("lHabilitado").value==0){							
				document.getElementById("lHabilitado").checked = true;
				document.getElementById("lHabilitado").value=0;
			}else{							
				document.getElementById("lHabilitado").checked = false;
				document.getElementById("lHabilitado").value=1;
			}
		}	
			function habilitado(){			
			if(document.getElementById("lHabilitado").checked){
				document.getElementById("lHabilitado").checked=true;
				document.getElementById("lHabilitado").value=0;
			}else{
				document.getElementById("lHabilitado").checked=false;
				document.getElementById("lHabilitado").value=1;
			}	
			
		}
			
			function eliminaTelefono(tel_id){
				var telId = tel_id;
				$("#cIdRFC").val();
				$("#nIdTelefono").val(telId);
				//alert($("#nIdTelefono").val());
				//alert($("#cIdRFC").val());
				queryFormPost("sp_mEliminaTelefonoProveedor", {async : false});
				$('#tblTelefonoProveedor').dataTable().fnClearTable();	
				
			}
			
			function onlyNumbersAndLetters(evt) {
			var keyPressed = (evt.which) ? evt.which : event.keyCode
			var strCheck = " !@#$%^&*()´+=-[]\\';,./{}|\":<>?";
			var key = String.fromCharCode( keyPressed );
			if (strCheck.indexOf( key ) == -1)
			    //fue ingresado un caracter valido
				return true; // Valida que sea numero y punto decimal
		       	//fue ingresado un caracter especial
				return false; 
			}
			function ChangeCase(elem){
		        elem.value = elem.value.toUpperCase();
		    }
		 
	</script>
</head>
<body id="dt_example" bottomMargin="0" bgColor="red" leftMargin="0" topMargin="0">
	<form id="proveedor" name="proveedor">			
		<input type="hidden" name="cIdUnidadEjecutora" id="cIdUnidadEjecutora"  value="<%=usuario.getU_UR() %>"/>	
		<input type="hidden" name="editar_proveedor" id="editar_proveedor"  value="<%=new String(request.getParameter("rfc").getBytes("ISO-8859-1"),"UTF-8")%>"/>
		<input type="hidden" name="h_habilitado" id="h_habilitado"  value=""/>
		<input type="hidden" name="consecutivoNum" id="consecutivoNum"  value=""/>
		<input type="hidden" name="nIdTelefono" id="nIdTelefono"  value=""/>
		
		<div id="container" class="container">				
		<fieldset>
			<legend>Datos Generales</legend>
			<table align="left">
				<tr align="left">
					<td>Tipo de Persona:</td>
					<td><input type="radio" id="tipoPersonaF" name="tipoPersona" value="1" disabled="disabled">F&iacute;sica
						<input type="radio" id="tipoPersonaM" name="tipoPersona" value="2"  disabled="disabled" > Moral					
						<input type="hidden" id="isPersonaFisica" name="isPersonaFisica" value="0" />
					</td>
				</tr>			
				<tr align="left">
					<td>Registro Federal de Contribuyentes [RFC]</td>
					<td><input type="text" name="cIdRFC1" id="cIdRFC1" maxLength="3" style="width: 4em;"  onKeyPress="return(onlyNumbersAndLetters(event));" onblur="ChangeCase(this);" />-
						<input type="text" name="cIdRFC2" id="cIdRFC2" style="width: 4em;" maxlength="6"  onKeyPress="return(onlyNumbersAndLetters(event));" onblur="ChangeCase(this);"/>-
						<input type="text" name="cIdRFC3" id="cIdRFC3" style="width: 4em;" maxlength="3"  onKeyPress="return(onlyNumbersAndLetters(event));" onblur="ChangeCase(this);"/>
						<input type="hidden" name="cIdRFC" id="cIdRFC" style="width: 4em;" /><font color="red">*</font></td>
				</tr>
				<tr align="left" id="trCurp" style="display: none;">
					<td>CURP</td>
					<td>
						<input type="text"  id="inputCurp" name="inputCurp" maxlength="18" onblur="ChangeCase(this);" onKeyPress="return(onlyNumbersAndLetters(event));" /><font color="red">*</font>
					</td>
				</tr>
				<tr align="left">
					<td>Razon Social</td>
					<td><input type="text" name="cRazonSocial" id="cRazonSocial" style="width: 30em;" maxlength="300"/><font color="red">*</font></td>
				</tr>
				<tr align="left">
					<td>Representante </td>
					<td><input type="text" name="cRepresentanteLegal" id="cRepresentanteLegal" style="width: 30em;" maxlength="100"/></td>
				</tr>
				<tr align="left">
					<td>Giro </td>
					<td><textarea rows="5" cols="60" id="cGiro" name="cGiro"></textarea><font color="red">*</font></td>
				</tr>
				<tr align="left">
					<td>Numero Interno de Registro </td>
					<td><input type="text" name="cNumeroRegistro" id="cNumeroRegistro" style="width: 30em;" maxlength="50"/></td>
				</tr>
				<tr align="left">
					<td>Pyme</td>
					<td><select id="nIdPyme" name=nIdPyme style="width: 30em;">
<!-- 						<option id="0" value="0">No aplica</option> -->
<!-- 						<option id="1" value="1">Micro Empresa</option> -->
<!-- 						<option id="2" value="2">Pequeña Empresa</option> -->
<!-- 						<option id="3" value="3">Mediana Empresa</option> -->
						</select></td>
				</tr>
				<tr align="left">
					<td>Inhabilitar</td>
					<td><input type="checkbox" name="lHabilitado" id="lHabilitado" onclick="habilitado()" /> 
					</td>
				</tr>
								
			</table>
		</fieldset>
		<fieldset>
			<legend>Domicilio Fiscal </legend>
			<table align="left">
				<tr align="left">
					<td>Entidad Federativa</td>
					<td><select id="cIdEntidadFederativa" name="cIdEntidadFederativa" style="width: 30em;"></select></td>
				</tr>			
				<tr align="left">
					<td>Delegacion o Municipio </td>
					<td><input type="text" name="cMunicipio" id="cMunicipio" style="width: 30em;" maxlength="50"/><font color="red">*</font></td>
				</tr>
				<tr align="left">
					<td>Calle</td>
					<td><input type="text" name="cCalle" id="cCalle" style="width: 30em;" maxlength="100"/><font color="red">*</font></td>
				</tr>
				<tr align="left">
					<td>Numero Externo </td>
					<td><input type="text" name="cNumeroExterno" id="cNumeroExterno" style="width: 30em;" maxlength="50"/><font color="red">*</font></td>
				</tr>
				<tr align="left">
					<td>Numero Interno </td>
					<td><input type="text" name="cNumeroInterno" id="cNumeroInterno" style="width: 30em;" maxlength="50"/></td>
				</tr>
				<tr align="left">
					<td>Colonia  </td>
					<td><input type="text" name="cColonia" id="cColonia" style="width: 30em;" maxlength="100"/><font color="red">*</font></td>
				</tr>
				<tr align="left">
					<td>Código Postal  </td>
					<td><input type="text" name="cCodigoPostal" id="cCodigoPostal" style="width: 30em;" maxlength="5"/><font color="red">*</font></td>
				</tr>				
			</table>
		</fieldset>
		<fieldset>
			<legend>Contacto </legend>
			<table align="left">
				<tr align="left">
					<td>Pagina Web </td>
					<td><input type="text" name="cUrl" id="cUrl" style="width: 30em;" maxlength="75"/></td>
				</tr>			
				<tr align="left">
					<td>Correo Electronico </td>
					<td><input type="text" name="cEmail" id="cEmail" style="width: 30em;" maxlength="75"/></td>
				</tr>
				
				<tr align="left">
					<td>Telefono </td>
					<td><select id="cTipoTelefono" name="cTipoTelefono" style="width: 10em;"></select>&nbsp;
					<input type="text" name="cTelefono" id="cTelefono" style="width: 20em;" maxlength="30"/>
					</td>
				</tr>
				<tr align="center">
					<td colspan="2">
					<table id="tblTelefonoProveedor" class="display" align="center">
									            <thead>
									                <tr>									                
									                	<th>Num. Telefono</th>
									                    <th>Tipo</th>
									                    <th>Telefono</th>
									                     <th>Eliminar</th>
									                </tr>
									            </thead>
									        </table>
					</td>
				</tr>
				<tr align="left">
					<td colspan="2"><label class="validateTips ui-state-error" ></label></br></td>
				</tr>
				<tr align="center">
					<td colspan="2"><input type="button" name="btnGuardar" id="btnGuardar" value="Guardar"  />&nbsp;&nbsp;
						<input type="button" name="btnSalir" id="btnSalir" value="Salir"  /></td>
				</tr>
				
				
															
			</table>
		</fieldset>
		</div>
	</form>	
</body>
</html>






<update id="mCatalogoProveedorUpdate" setList="cRazonSocial,cNumeroRegistro,cGiro,cRepresentanteLegal,cIdEntidadFederativa,cCalle,cNumeroExterno,cNumeroInterno,cColonia, cMunicipio,cCodigoPostal,cEmail,cUrl, cIdUnidadEjecutora,nIdPyme, h_habilitado" whereList="cIdRFC"><![CDATA[update mCatalogoProveedor set cRazonSocial= ? ,   cNumeroRegistro=? ,cGiro=? , cRepresentanteLegal=? ,cIdEntidadFederativa=? ,cCalle = ?  ,cNumeroExterno = ? ,cNumeroInterno= ?  ,cColonia= ? ,cMunicipio = ?  ,cCodigoPostal = ? ,cEmail = ? ,cUrl = ? ,cIdUnidadEjecutora = ? ,nIdPyme = ? , lHabilitado = ? WHERE cIdRFC = ?]]></update>