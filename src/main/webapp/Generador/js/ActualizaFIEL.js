/**
 * 
 */

let esRevisionFIEL = false;

const openDialog = function(){
	$("#dlgAvanzaSICOP").dialog("open");
}

$(document).ready(function() {



	if (document.getElementById("revisionFIEL"))
		esRevisionFIEL = true;
	
	if( esRevisionFIEL ){
		$("#container").append(
			  '<div class= "card"> '
			+ ' <div class="card-body"> ' 
			+ ' 	<h6>Opciones</h6> '
			+ ' 	<div class="row"> '
			+ ' 		<div class="col-12 col-lg-12 col-md-12 col-sm-12"> <a href="#" onclick="openDialog();return false" >Autorizar/Rechazar envío a SICOP</a> '
			+ ' 		</div> '
			+ ' 	</div> '
			+ ' </div> '
		    + '</div>' );
	 
	}
		
			
	$(document.body).append("<div id='dlgAvanzaSICOP'></div>");

	$("#dlgAvanzaSICOP").append(
		"<fieldset>"
		+ "	<form id='sicopFrm' method='POST' action='../FIEL/autorizaEnvioSICOP' target='content-iframe'>"
		+ "		<legend>Autorizacion de envio a SICOP</legend>"
		+ "		<div id='autInfoDiv'><table id='autInfoTable'></table></div>"
		+ "		<div id='contentDiv'>"
		+ "			<input type='hidden' id='VoBoNombre'> "
		+ "			<input type='hidden' id='VoBoFecha'> "
		+ "			<input type='hidden' id='AutNombre'> "
		+ "			<input type='hidden' id='AutFecha'>"
		+ "			<input type='hidden' id='operacionActual'>"
		+ "			<input type='hidden' id='cDocumentoAplicado'>"
		+ "      </div>"
		+ "	</form>"
		+ "</fieldset>"
		+ "<fieldset>\n" 
		+ (esRevisionFIEL?""
			:( "\t<legend>Documentos</legend>\n"
			 + "\t<div id='DoctosDiv'><ul id='doctosList'></ul></div>\n"
			 )
		 )
		+ "</fieldset>"
	);

	$("#contentDiv").append("<table id='tblContent'>"
		+ "<tr>"
		+ "<td colspan='2'>Desea autorizar el envio del siguiente pago a SICOP para su procesamiento?</td>"
		+ "</tr>"
		+ "<tr>"
		+ "<td colspan='2' align='center'>"
		+ "<input type='radio' value='S' id='yEnvioSicop' name='envioSicop' checked>Si"
		+ "<input type='radio' value='N' id='nEnvioSicop' name='envioSicop' >No"
		+ "</td>"
		+ "</tr>"
		+ "</table>");

	
	
	queryFormPost({
		queryName : "infoVoBoRead",
		async : false,
		callback : function() {
			$("#autInfoTable tbody").append("<tr>" +
				"	<td align='right'>" +
				"		<label> Visto Bueno Por: </label>" +
				"	</td>" +
				"	<td align='left'>" +
				"		<b>" + $("#VoBoNombre").val() + "</b>" +
				"	</td>" +
				"	<td align='right'> " +
				"		el dia: " +
				"</td>" +
				"	<td align='left'>" +
				"		<b>" + $("#VoBoFecha").val() + "</b>" +
				"	</td>" +
				"</tr>");
		}
	});

	queryFormPost({
		queryName : "infoAutRead",
		async : false,
		callback : function() {
			$("#autInfoTable tbody").append("<tr>" +
				"		<td align='right'>" +
				"			<label> Autorizado Por: </label> " +
				"		</td>" +
				"		<td align='left'>" +
				"			<b>" + $("#AutNombre").val() + "</b> " +
				"		</td>" +
				"		<td align='right'>" +
				"			<label> el dia: </label> " +
				"		</td>" +
				"		<td align='left'>" +
				"			<b>" + $("#AutFecha").val() + "</b>" +
				"		</td> " +
				"	</tr> ");
		}
	});

	if( !esRevisionFIEL ){
		$.ajax({
			url : '../fortimax/documents',
			dataType : 'json',
			type : "GET",
			data : {
				"accion" : "send_tree"
			},
			async : true,
			success : function(objResp) {
				var nodos = objResp.nodos;
				for( var i in nodos){
					nodo = nodos[i];
					//'" +   + "');return false;>"+  + "
					$("#doctosList").append("<li><a style='color:black;' href=\"#\" onclick=\"muestraDocumento('" + nodo.fortimax + "')\">" + nodo.path + "</a></li>");
				}
			},
			error : function(xhr, textStatus, errorThrown) {
				try{
					var obj = eval(xhr.responseText);
					var msg = obj.errCause;
					alert("No fue posible consultar los documentos debido al error: " + msg);
				}catch (e) {
					alert("Advertencia: " + xhr.responseText + "\nEstatus: " + textStatus + "\n" + errorThrown);
				}
				
				$.unblockUI();
			}
		});
	}
	
	var muestraDialogo = false;
	$("#operacionActual").val("");


	if (document.getElementById("TipoAutorizacion"))
		$("#TipoAutorizacion").val("");
	else
		$("#contentDiv").append("<input type='hidden' id='TipoAutorizacion'>");

	queryFormPost("tipoAutorizacionRead", {
		async : false
	});

	var usuarioPermitido = esUsuarioAutSICOP();
	queryFormPost({
		queryName : "operacionTramiteRead",
		async : false,
		callback : function() {
			var oper = ( parseInt($("#operacionActual").val() == "" ? "3" : $("#operacionActual").val(), 10)  );
			
			muestraDialogo = !esRevisionFIEL && usuarioPermitido && ( ( $("#TipoAutorizacion").val() == "S")  &&  ( $("#cDocumentoAplicado").val() == "S") && oper == 0 ? true : false);
		}
	});

	$("#dlgAvanzaSICOP").dialog({
		autoOpen : muestraDialogo,
		height : 500,
		width : 750,
		modal : true,
		buttons : {
			"Aceptar" : function() {
				var msg = "Esta seguro que desea " + ($('input[name=envioSicop]:checked').val() == "S" ? "Autorizar" : "Rechazar") + " el tramite? Esta operacion no se puede deshacer."
				if (confirm(msg))
					$("#sicopFrm").submit();
			}
		},
		open : function() {},
		beforeClose : function() {}
	});
});


function esUsuarioAutSICOP() {
	var exito = false;
	var usuarioPermitido = false;

	$.ajax({
		url : '../FIEL/validaUsuarioAutorizador',
		type : "GET",
		dataType : 'json',
//		data : $("form").serialize(),
		async : false,
		success : function(json) {
			exito = json.success == "true";
			if (exito) {
				usuarioPermitido = json.data_1.result == "true";
			} else {
				alert("ERRO: " + json.data_1.result);
			}
		},
		error : function(xhr, textStatus, errorThrown) {
			msgError = "Advertencia: " + xhr.responseText + "\nEstatus: " + textStatus + "\n" + errorThrown;
		}
	});
	
	return usuarioPermitido;
}

function muestraDocumento(fortimaxNode){
	openCenteredWindow("../filestore?select=" + fortimaxNode, "_blank", 800, 1024);
}

function openCenteredWindow(url, name, height, width, parms) {
	var left = Math.floor((screen.width - width) / 2);
	var top = Math.floor((screen.height - height) / 2);
	var winParms = "top=" + top + ",left=" + left + ",height=" + height + ",width=" + width + ",scrollbar=yes,status=yes,resizable=1";
	if (parms) {
		winParms += "," + parms;
	}
	var win = window.open(url, name, winParms);
	if (parseInt(navigator.appVersion) >= 4) {
		win.window.focus();
	}
	return win;
}