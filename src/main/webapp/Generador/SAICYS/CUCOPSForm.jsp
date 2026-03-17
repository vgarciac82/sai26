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
		<title>Agregar CUCOP´s</title>

		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">
		<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
		<meta http-equiv="description" content="Catálogo de Beneficiarios">

		<link rel="shortcut icon" type="image/ico" href="imagenes/favicon.ico" />

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
		<script type="text/javascript" src="../js/catalogo/general.js"></script>

		<script type="text/javascript" charset="utf-8">
	
		var giTimer=0;
		var gsOperacion ='<%=request.getParameter("Op")%>';

		$(document).ready(function() {			
			     $('#tblTipoRFCDisp').dataTable(
				{
					"bPaginate": false,
        			"bLengthChange": false,
        			"bFilter": false,
        			"bSort": false,
        			"bInfo": false,
        			"bAutoWidth": false,
					"sScrollY": 100,
					"bJQueryUI": true,
					"bRetrive" : true,
					"bDestroy" : true,
					"sPaginationType": "full_numbers"

				} );
			oTable = $("#tblTipoRFC").dataTable({
				"bPaginate": false,
        			"bLengthChange": false,
        			"bFilter": false,
        			"bSort": false,
        			"bInfo": false,
        			"bAutoWidth": false,
					"sScrollY": 100,
					"bJQueryUI": true,
					"bRetrive" : true,
					"bDestroy" : true,
					"sPaginationType": "full_numbers"				
        	});			
						
			$(".tabs").tabs();
			$(this).ajaxForm({
				dataType:  "json",
				success: formSubmited
			});

			function fnGetSelected( oTableLocal ) {     
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

			$('#tblTipoRFCDisp tr').live('click', function() {         
				if ( $(this).hasClass('row_selected') )             
					$(this).removeClass('row_selected');         
				else            
					$(this).addClass('row_selected');     
				} );
			
			$('#tblTipoRFC tr').live('click', function() {         
				if ( $(this).hasClass('row_selected') )             
					$(this).removeClass('row_selected');         
				else            
					$(this).addClass('row_selected');     
				} );
			
			$("#pbPalla" )
				.button()
				.click(function() {

					var aTrs = $('#tblTipoRFCDisp').dataTable().fnGetNodes();           
					for ( var i=aTrs.length ; i>=0; i-- )     
					{         
						if ( $(aTrs[i]).hasClass('row_selected') )         
						{             
							var nTr = $('#tblTipoRFCDisp').dataTable().fnGetData(aTrs[i]);   
//							nTr[0] = nTr[0].replace("nombre","name");
							$('#tblTipoRFC').dataTable().fnAddData( nTr );
							$('#tblTipoRFCDisp').dataTable().fnDeleteRow( i ); 
						}     
					} 
				});
			
			$("#pbPaca" )
				.button()
				.click(function() {

					var aTrs = $('#tblTipoRFC').dataTable().fnGetNodes();           
					for ( var i=aTrs.length ; i>=0; i-- )     
					{         
						if ( $(aTrs[i]).hasClass('row_selected') )         
						{             
							var nTr = $('#tblTipoRFC').dataTable().fnGetData(aTrs[i]);  
							nTr[0] = nTr[0].replace("name","nombre");
							$('#tblTipoRFCDisp').dataTable().fnAddData( nTr );
							$('#tblTipoRFC').dataTable().fnDeleteRow( i ); 
						}     
					} 
				});
			
			
		});
		function formSubmited() {
                alert("CUCOPS enviados!");
            }				
		</script>

	<script type="text/javascript" charset="utf-8">		// funciones del dialogo de Cuentas Bancarias
		

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
				var sTemp = $.trim(o.val());
				o.val(sTemp);
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


			$("#pbAceptar")
				.button()
				.click(function() {

				var bValid = true;
				tips.text("");
				allFields.removeClass( "ui-state-error" );
				
				});
				$("input").each(function (){
					var szStyle = "" + $(this).attr('style');
					if (szStyle.indexOf("uppercase")>1)
					{$(this).val($(this).val().toUpperCase());  
					}
				});
				$("input").each(function (){
					var szStyle = "" + $(this).attr('style');
					if (szStyle.indexOf("lowercase")>1)
					{
						$(this).val($(this).val().toLowerCase());  
					}
				});
				


		function fnEsperaIns()
		{
			var oSettings = oTable.fnSettings();
			var aTrs = oTable.fnGetNodes();
			oSettings.sAjaxSource = null;
			
			for ( var i=0 ; i<aTrs.length ; i++ )
			{
				oTable.fnUpdate( $("#cBeneficiario").val(), i, 4); 
			}
			
		}
		function cargarDatos(){
			
				var szWhere = "";// cBeneficiario = " + $("#cBeneficiario").val() + " ";
				var szTabla = "CUCOPS_PROGANUAL";                                                                                         
				$.getJSON("../../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, MaxReg: 20, ajax: 'false'}, 
					function(j)
					{                       							
    					for (var i = 0; i < j.length; i++) 
    					{  
    					    $('#tblTipoRFCDisp').dataTable().fnAddData( [
								j[i].Col0,j[i].Col1,j[i].Col2,j[i].Col3,j[i].Col4,j[i].Col5
							]);    					 
						}
		         })  	
		} 
		
		
		function agregarCUCOP(){
			var table = document.getElementById('tblTipoRFC');
            var rowCount = table.rows.length;
            for(var i=1; i<rowCount; i++) {
            	var row = table.rows[i];                		
				var piezaGrd = '';
				var elMontoGrd = '';
				try
				{
	  				piezaGrd = row.cells[0].childNodes[0].nodeValue;
	  			//	elMontoGrd = row.cells[2].childNodes[0].nodeValue;
	 			} catch(e) {
					null;
				}
				if(null != piezaGrd ) {
					if(piezaGrd.toString() != ""){

						$("#pieza").val('2011');
			       		queryFormPost("tNCreate", {async: false });

				    	table.deleteRow(i);
	            	  	rowCount--;
	            	  	i--;
					}
				}
            }
			
			
			
		}
	</script>
</head>

<body id="dt_example" bottomMargin="0" bgColor="red" leftMargin="0" topMargin="0" onload="cargarDatos();" >
	<form>
		
		<input id="cUsuario" name="cUsuario" type="hidden" size="10">
		<input id="pieza" name="pieza" type="text" size="10">		
		
		<div id="container" >
			<h1><img id="imgPlayStop" src="imagenes/wait24trans.gif">Agregar Cucop´s <label id="lbOperacion" style="font-size: 8pt"></label></h1>
			

			<br/>

			<div class="tabs">
				<ul>
					<li><a id="aTab0" href="#tabs-0">Agregar</a></li>					
				</ul>
				<div id="tabs-0" >
					<table >
						<tr>
							<td width="330px">
								<table id="tblTipoRFCDisp" class="display"  >
						            <thead>
						                <tr>
						                	<th>CABMS</th>
						                    <th>Descripción</th>
						                    <th>Partida</th>
						                    <th>Descripción Partida</th>
						                    <th>Unidad Medida</th>						                        
						                    <th>Tipo Proceso</th>
						                </tr>
						            </thead>
						               <tbody>
                  </tbody>
                  <tfoot>
                  </tfoot>
						        </table>
							</td>
							<td>
								&nbsp;<button id="pbPalla"   >>></button>&nbsp;<br><br>
								&nbsp;<button id="pbPaca" ><<</button>&nbsp;
							</td>
							<td  width="330px">
								<table id="tblTipoRFC" class="display"  >
						            <thead>
						               <tr>
						                	<th>CABMS</th>
						                    <th>Descripción</th>
						                    <th>Partida</th>
						                    <th>Descripción Partida</th>
						                    <th>Unidad Medida</th>						                        
						                    <th>Tipo Proceso</th>
						                </tr>
						            </thead>
						             <tbody>
                  </tbody>
                  <tfoot>
                  </tfoot>
						        </table>
							</td>
						</tr>
					</table>
					<label style="POSITION: relative; TOP:-195px; LEFT:20px">Disponibles</label>
					<label style="POSITION: relative; TOP:-195px; LEFT:365px">Seleccionados</label>
					
				</div>

				
				
				
			</div>
			<label class="validateTips ui-state-error" ></label></br>
			<table width="100%" border="0" >
				<tr>
					<td width="33%">&nbsp;</td>
					<td width="33%" align="center">
						<button id="pbAceptar" onclick="agregarCUCOP();">Agregar CUCOPS</button>&nbsp;&nbsp;&nbsp;
						<input type="button" id="pbCancelar" value="Cancelar"/>
					</td>
					<td width="33%" align="right">
						&nbsp;
					</td>
				</tr>
			</table>
		</div>





	</form>
	</body>
</html>