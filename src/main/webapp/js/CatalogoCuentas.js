/**
 * 
 */
var dTable;
var tableTemp= new Array(12);

var requeridos = {
	"dCuenta" : "Descripcion de la cuenta"
};
var lengParams = {
		sProcessing : "Procesando...",
		sLengthMenu : "Mostrar _MENU_ registros",
		sZeroRecords : "No hay registros a mostrar",
		sEmptyTable : "No hay datos en la tabla",
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
	};
function init() {

	$
			.blockUI({
				message : "<p><img src='../Generador/imagenes/wait24trans.gif' />&nbsp;&nbsp;Por favor espere...</p>"
			});
	$.fn.dataTableExt.oApi.fnReloadAjax = function(oSettings, sNewSource) {
		if (typeof sNewSource != 'undefined')
			oSettings.sAjaxSource = sNewSource;

		this.fnClearTable(this);
		this.oApi._fnProcessingDisplay(oSettings, true);
		var that = this;

		$.getJSON(oSettings.sAjaxSource, null, function(json) {
			/* Got the data - add it to the table */
			for ( var i = 0; i < json.aaData.length; i++) {
				that.oApi._fnAddData(oSettings, json.aaData[i]);
			}

			oSettings.aiDisplay = oSettings.aiDisplayMaster.slice();
			that.fnDraw(that);
			that.oApi._fnProcessingDisplay(oSettings, false);
		});
	};

	$("#Nivel1").focus();
	initInputAccount();
	createButtons();
	creaTabla();
	$.unblockUI();
}

function createButtons() {
	$("#Agregar").button().click(function() {
		var valid = validaRequeridos() && validaCuentaCapturada();
		if (valid)
			creaCuenta();
	});

	$("#Limpiar").button().click(function() {
		cleanForm();
	});
	$("#Buscar").button().click(function() {
		buscar();
	});
	$("#Actualizar").button().click(function() {
		actualizar();
	});
	$("#Eliminar").button().click(function() {
		eliminar();
	});
}

function creaTabla() {
	var nc = '';
	var token = '';

	for ( var i = 1; i <= 4; i++)
		if ($("#Nivel" + i).val().replace(/0/g, '') != '') {
			nc += token + $("#Nivel" + i).val();
			token = '-';
		}
	var reqQry = (nc != '' ? '&nCuenta=' + nc : '') + '&dCuenta='
			+ $("#dCuenta").val() + '&TipoCuenta=' + $("#TipoCuenta").val()
			+ '&TipoBalance=' + $("#TipoBalance").val() + '&VerificaSaldo=N'
			+ '&NaturalezaCuenta=' + $("#NaturalezaCuenta").val()
			+ '&NivelCuenta=' + $("#NivelCuenta").val() + '&AplicacionCuenta='
			+ $("#AplicacionCuenta").val() + '&cSubcuenta='
			+ $("#cSubcuenta").val() + '&nCuentaLike='
			+ $("#nCuentaLike").val() + '&nNivelBalanza='
			+ $("#nNivelBalanza").val() + '&nOrdenBalanza='
			+ $("#nOrdenBalanza").val();

	dTable = $("#dTbl")
			.dataTable(
					{
						bJQueryUI : true,
						sAjaxSource : '../CuentaContable/OperacionesCuenta?accion=BuscaCuentas'
								+ reqQry,
						oLanguage : lengParams
					});
	$("#dTbl tbody").dblclick(function(event) {
		var aPos = dTable.fnGetPosition(event.target.parentNode);

		$(dTable.fnSettings().aoData).each(function() {
			$(this.nTr).removeClass('row_selected');
			var aPos = dTable.fnGetPosition(this.nTr);
			var aData = dTable.fnGetData(aPos[0]);
		});

		$(event.target.parentNode).addClass('row_selected');

		
		var aData = dTable.fnGetData(aPos);
		fillFields(aData);
		$("#Actualizar").show();
		$("#Eliminar").show();
		editaCapturaCuentas(true);
		chargueData(aData);
	});
}


/*CARGAR LOS DATOS EN LAS VARIABLES PARA VERIFRICAR SI SOLO CAMBIA LA DESCRIPCIONC */
function chargueData(adata) {
    
	var nc = adata[0].split("-");
	tableTemp[0]=nc[0];
	tableTemp[1]=nc[1];
	tableTemp[2]=nc[2];
	tableTemp[3]=nc[3];
	tableTemp[4]=adata[1];
	tableTemp[5]=adata[2];
	tableTemp[6]=adata[4];
	tableTemp[7]=adata[5];
	tableTemp[8]=adata[6];
    tableTemp[9]=adata[7];
	tableTemp[10]=adata[8];
	tableTemp[11]=adata[9];
	
}

function fillFields(adata) {

	var nc = adata[0].split("-");
	$("#Nivel1").val(nc[0]);
	$("#Nivel2").val(nc[1]);
	$("#Nivel3").val(nc[2]);
	$("#Nivel4").val(nc[3]);

	$("#dCuenta").val(adata[1]);
	$("#TipoCuenta").val(adata[2]);
	$("#TipoBalance").val(adata[4]);
	$("#NaturalezaCuenta").val(adata[5]);
	$("#NivelCuenta").val(adata[6]);

	$("#AplicacionCuenta").val(adata[7]);
	$("#cSubcuenta").val(adata[8]);
	$("#nCuentaLike").val(adata[9]);
	// $("#nNivelBalanza").val(adata[0]);
	// $("#nOrdenBalanza").val();
}

function initInputAccount() {

	var idx;
	$("#NivelCuenta").val("0");
	$('input[name^="Nivel"]').each(
			function() {
				idx = this.id.substring(this.id.length - 1);
				if (idx && idx > 0) {
					$(this).val("00000");
					$(this).focus(function() {
						this.select();
					});
					$(this)
							.blur(
									function() {
										if ($(this).val() == ''
												|| $(this).val().replace(/0/g,
														'') == '')
											$(this).val("00000");
										else {
											var ceros = '00000';
											var val = ceros.substring($(this)
													.val().length)
													+ $(this).val();
											$(this).val(val);
										}

										calcAccountLevel();
										genSimAccount();
									});
					$(this).keypress(function(e) {
						return noChars(e);
					});
				}
			});
	$("#nNivelBalanza").keypress(function(e) {
		return noChars(e);
	});
	$("#nOrdenBalanza").keypress(function(e) {
		return noChars(e);
	});

}

function noChars(e) {
	tecla = (document.all) ? e.keyCode : e.which;
	if (tecla == 8)
		return true;
	patron = /[\d]/g;
	te = String.fromCharCode(tecla);
	return patron.test(te);
}

function calcAccountLevel() {
	var continuar = true;
	var prefix = "Nivel";
	var nivel = 1;
	for ( var i = 1; i <= 4 && continuar; i++) {
		var elID = prefix + i;
		if ($("#" + elID).val() != ''
				&& $("#" + elID).val().replace(/0/g, '') != '') {
			$("#NivelCuenta").val(nivel++);
		} else
			continuar = false;
	}
}

function genSimAccount() {
	var prefix = $("#Nivel1").val() + "-" + $("#Nivel2").val() + "-"
			+ $("#Nivel3").val() + "-" + $("#Nivel4").val();
	var cLike = "";
	var idx = 0;
	for ( var i = prefix.length; i > 0; i--) {
		if (prefix.charAt(i - 1) != '0' && prefix.charAt(i - 1) != '-') {
			idx = i;
			break;
		}
	}
	if (idx != 0) {
		if (prefix.length - 1 > Number(idx))
			$("#nCuentaLike").val(prefix.substring(0, idx) + '%');
		else
			$("#nCuentaLike").val(prefix.substring(0, idx));

	} else {
		$("#nCuentaLike").val('');
	}
}

function validaRequeridos() {

	var msg = "";
	for ( var elem in requeridos) {
		if ($("#" + elem).val() == '')
			msg += "\n" + eval("requeridos." + elem);
	}
	if (msg != "") {
		msg = "La captura de los siguientes campos es obligatoria:" + msg;
		alert(msg);
		return false;
	} else
		return true;
}

function validaCuentaCapturada() {
	var cta = $("#Nivel1").val() + $("#Nivel2").val() + $("#Nivel3").val()
			+ $("#Nivel4").val();
	if (cta.replace(/0/g, '') == '') {
		alert("Debe capturar la cuenta contable");
		return false;
	}
	if ($("#Nivel1").val().replace(/0/g, '') == '') {
		alert("El primer nivel siempre debe ser diferente de 00000");
		return false;
	}
	var continueLoop = true;
	for ( var cnt = 4; cnt > 1 && continueLoop; cnt--) {
		var elemName = "Nivel" + cnt;
		if ($("#" + elemName).val().replace(/0/g, '') != '') {
			var elemPrevName = "Nivel" + Number(cnt - 1);
			if ($("#" + elemPrevName).val().replace(/0/g, '') == '') {
				alert("El nivel previo de la subcuenta "
						+ $("#" + elemName).val()
						+ " debe ser diferente de 00000");
				continueLoop = false;
				return false;
			}
		}
	}
	return true;
}

function creaCuenta() {
	$
			.blockUI({
				message : "<p><img src='../Generador/imagenes/wait24trans.gif' />&nbsp;&nbsp;Por favor espere...</p>"
			});
	$.ajax({
		url : '../CuentaContable/OperacionesCuenta',
		dataType : 'json',
		data : {
			"accion" : "AltaCuentas",
			"nCuenta" : $("#Nivel1").val() + "-" + $("#Nivel2").val() + "-"
					+ $("#Nivel3").val() + "-" + $("#Nivel4").val(),
			"dCuenta" : $("#dCuenta").val(),
			"TipoCuenta" : $("#TipoCuenta").val(),
			"TipoBalance" : $("#TipoBalance").val(),
			"VerificaSaldo" : "N",
			"NaturalezaCuenta" : $("#NaturalezaCuenta").val(),
			"NivelCuenta" : $("#NivelCuenta").val(),
			"AplicacionCuenta" : $("#AplicacionCuenta").val(),
			"cSubcuenta" : $("#cSubcuenta").val(),
			"nCuentaLike" : $("#nCuentaLike").val(),
			"nOrdenBalanza" : $("#nOrdenBalanza").val(),
			"nNivelBalanza" : $("#nNivelBalanza").val()
		},
		async : false,
		success : function(RS) {
			$.unblockUI();
			$("#question").dialog({
				modal : true,
				buttons : {
					Ok : function() {
						$(this).dialog("close");
					}
				}
			});
			var arrRes = new Array();
			var index, data, col;
			if (RS.success == "true") {
				index = 1;
				while (true) {
					data = eval("RS.data_" + index++);
					if (!data)
						break;
					for ( var i = 0; i < data.length; i++) {
						col = data[i];
						for ( var name in col) {
							arrRes.push(eval("col." + name));
						}
					}
				}
			}

			var msgRes = arrRes.join("<br/>");
			$("#titulo").text("");
			$("#resultMsg").html(msgRes);

		},
		error : function(xhr, textStatus, errorThrown) {
			alert("Advertencia: " + xhr.responseText + "\nEstatus: "
					+ textStatus + "\n" + errorThrown);
			r = true;
		}
	});
}

function cleanForm() {
	$('[type=text]').each(
			function() {
				if (this.id.indexOf('Nivel') >= 0
						&& !("NivelCuenta" == this.id)
						&& !("nNivelBalanza" == this.id))
					$(this).val('00000');
				else if (this.id == "NivelCuenta")
					$(this).val('0');
				else
					$(this).val('');
				$(".sel option:first-child").attr("selected", true);
			});
	$("#Actualizar").hide();
	$("#Eliminar").hide();
	editaCapturaCuentas(false);
}

function buscar() {
	var nc = '';
	var token = '';

	for ( var i = 1; i <= 4; i++)
		if ($("#Nivel" + i).val().replace(/0/g, '') != '') {
			nc += token + $("#Nivel" + i).val();
			token = '-';
		}

	var reqQry = (nc != '' ? '&nCuenta=' + nc : '') + '&dCuenta='
			+ $("#dCuenta").val() + '&TipoCuenta=' + $("#TipoCuenta").val()
			+ '&TipoBalance=' + $("#TipoBalance").val() + '&VerificaSaldo=N'
			+ '&NaturalezaCuenta=' + $("#NaturalezaCuenta").val()
			+ '&NivelCuenta=' + $("#NivelCuenta").val() + '&AplicacionCuenta='
			+ $("#AplicacionCuenta").val() + '&cSubcuenta='
			+ $("#cSubcuenta").val() + '&nCuentaLike='
			+ $("#nCuentaLike").val() + '&nNivelBalanza='
			+ $("#nNivelBalanza").val() + '&nOrdenBalanza='
			+ $("#nOrdenBalanza").val();
	var ajxSource = '../CuentaContable/OperacionesCuenta?accion=BuscaCuentas'
			+ reqQry;
	dTable.fnSettings().sAjaxSource = ajxSource;
	dTable.fnReloadAjax();

}

function actualizar() {
	
  var newData= new Array(12);
	newData[0]=$("#Nivel1").val(); 
	newData[1]=$("#Nivel2").val();
 	newData[2]=$("#Nivel3").val(); 
 	newData[3]=$("#Nivel4").val();
 	newData[4]=$("#dCuenta").val();
 	newData[5]=$("#TipoCuenta").val();
 	newData[6]=$("#TipoBalance").val();
 	newData[7]=$("#NaturalezaCuenta").val();
	newData[8]=$("#NivelCuenta").val();
 	newData[9]=$("#AplicacionCuenta").val();
	newData[10]=$("#cSubcuenta").val();
 	newData[11]=$("#nCuentaLike").val();   
	
	
	
	
	
	var onlyDetail=true;
	
	for(var i=0;i<tableTemp.length;i++)
		{
		if(i!=4 && tableTemp[i]!=newData[i])
			{
			onlyDetail=false;
			
			}		
		
		}
	
		
	$
			.blockUI({
				message : "<p><img src='../Generador/imagenes/wait24trans.gif' />&nbsp;&nbsp;Por favor espere...</p>"
			});

	$.ajax({
		url : '../CuentaContable/OperacionesCuenta',
		dataType : 'json',
		data : {
			"accion" : "ActualizaCuentas",
			"nCuenta" : $("#Nivel1").val() + "-" + $("#Nivel2").val() + "-"
					+ $("#Nivel3").val() + "-" + $("#Nivel4").val(),
			"dCuenta" : $("#dCuenta").val(),
			"TipoCuenta" : $("#TipoCuenta").val(),
			"TipoBalance" : $("#TipoBalance").val(),
			"VerificaSaldo" : "N",
			"NaturalezaCuenta" : $("#NaturalezaCuenta").val(),
			"NivelCuenta" : $("#NivelCuenta").val(),
			"AplicacionCuenta" : $("#AplicacionCuenta").val(),
			"cSubcuenta" : $("#cSubcuenta").val(),
			"nCuentaLike" : $("#nCuentaLike").val(),
			"nOrdenBalanza" : $("#nOrdenBalanza").val(),
			"nNivelBalanza" : $("#nNivelBalanza").val(),
			"onlyDetail" : onlyDetail
		},
		async : false,
		success : function(RS) {
			$.unblockUI();
			$("#question").dialog({
				modal : true,
				buttons : {
					Ok : function() {
						$(this).dialog("close");
					}
				}
			});
			var arrRes = new Array();
			var index, data, col;
			if (RS.success == "true") {
				index = 1;
				while (true) {
					data = eval("RS.data_" + index++);
					if (!data)
						break;
					for ( var i = 0; i < data.length; i++) {
						col = data[i];
						for ( var name in col) {
							arrRes.push(eval("col." + name));
						}
					}
				}
			}

			var msgRes = arrRes.join("<br/>");
			$("#titulo").text("");
			$("#resultMsg").html(msgRes);

		},
		error : function(xhr, textStatus, errorThrown) {
			alert("Advertencia: " + xhr.responseText + "\nEstatus: "
					+ textStatus + "\n" + errorThrown);
			r = true;
		}
	});

}

function editaCapturaCuentas(ro) {
	if (ro) {
		$('input[name^="Nivel"]').each(function() {
			if (!("NivelCuenta" == this.id) && !("nNivelBalanza" == this.id)) {
				$(this).attr('readOnly', true);
			}
		});
	} else {
		document.getElementById("Nivel1").readOnly = "";
		document.getElementById("Nivel2").readOnly = "";
		document.getElementById("Nivel3").readOnly = "";
		document.getElementById("Nivel4").readOnly = "";
		/*
		 * $("#Nivel1").attr("readonly",false);
		 * $("#Nivel2").attr("readonly",false);
		 * $("#Nivel3").attr("readonly",false);
		 * $("#Nivel4").attr("readonly",false);
		 */

		/*
		 * $("#Nivel1").attr("readonly",''); $("#Nivel2").attr("readonly",'');
		 * $("#Nivel3").attr("readonly",''); $("#Nivel4").attr("readonly",'');
		 */
		/*
		 * $("#Nivel1").removeAttr('readonly');
		 * $("#Nivel2").removeAttr('readonly');
		 * $("#Nivel3").removeAttr('readonly');
		 * $("#Nivel4").removeAttr('readonly');
		 * 
		 * $("#Nivel1").removeAttr('disabled','');
		 * $("#Nivel2").removeAttr('disabled','');
		 * $("#Nivel3").removeAttr('disabled','');
		 * $("#Nivel4").removeAttr('disabled','');
		 */
		/*
		 * $('input[name^="Nivel"]').each(function() { if (!("NivelCuenta" ==
		 * this.id) && !("nNivelBalanza" == this.id)) {
		 * //$(this).removeAttr('readOnly'); //$("#" + this.id
		 * ).attr('readOnly',false); $("#" + this.id ).attr('readonly',""); }
		 * });
		 */
	}
}

function eliminar() {
	$.blockUI({
				message : "<p><img src='../Generador/imagenes/wait24trans.gif' />&nbsp;&nbsp;Por favor espere...</p>"
			});

	$.ajax({
		url : '../CuentaContable/OperacionesCuenta',
		dataType : 'json',
		data : {
			"accion" : "EliminaCuenta",
			"nCuenta" : $("#Nivel1").val() + "-" + $("#Nivel2").val() + "-"
					+ $("#Nivel3").val() + "-" + $("#Nivel4").val()
		},
		async : false,
		success : function(RS) {
			$.unblockUI();
			$("#question").dialog({
				modal : true,
				buttons : {
					Ok : function() {
						$(this).dialog("close");
					}
				}
			});
			var arrRes = new Array();
			var index, data, col;
			if (RS.success == "true") {
				index = 1;
				while (true) {
					data = eval("RS.data_" + index++);
					if (!data)
						break;
					for ( var i = 0; i < data.length; i++) {
						col = data[i];
						for ( var name in col) {
							arrRes.push(eval("col." + name));
						}
					}
				}
			}

			var msgRes = arrRes.join("<br/>");
			$("#titulo").text("");
			$("#resultMsg").html(msgRes);

		},
		error : function(xhr, textStatus, errorThrown) {
			alert("Advertencia: " + xhr.responseText + "\nEstatus: "
					+ textStatus + "\n" + errorThrown);
			r = true;
		}
	});
}