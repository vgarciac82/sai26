var gbPrimera = false;
var giTimer = 0;
var gclass;
var szTableCols = "";

var idCampo = "";
var szTabla = "";
var iMaxReg = 0;
var iColSort = 1;
var AyudaXML;
var arrColumnas;
var arrInsert;
var szAnchoDlg = "";
var szAltoDlg = "";
var szConca;

var szBorrar = "NO";
var szCambiar = "NO";
var szRegresaDatos = "NO";


$(document).ready(
	function() {

		idCampo = window.location.href;

		if (idCampo.indexOf("dlg2.0.jsp") > 0) {
			
			idCampo = idCampo.substr(idCampo.indexOf("?") + 4);

			var arrContexto = document.location.pathname.split('/');
			var URL = document.location.protocol + "//" + (document.location.host == "" ? "localhost" : document.location.host) + "/" + arrContexto[1] + "/Ayudas/xml-ayudas/";

			AyudaXML = loadXML(URL + idCampo + ".xml");
			
			if (AyudaXML) {
				spTitulo.innerText = AyudaXML.find("Ayuda>titulo").text();
				szTabla = AyudaXML.find("Ayuda>tabla").text();
				iMaxReg = parseInt(AyudaXML.find("Ayuda>maxRegistros").text(), 10);
				
				try {
					if( AyudaXML.find("Ayuda>ColSort").length > 0 )
						iColSort = parseInt( AyudaXML.find("Ayuda>ColSort").text(), 10);
				} catch (ex) { 
					iColSort = 1; 
				}
				arrColumnas = AyudaXML.find("columna");
				arrInsert = AyudaXML.find("campo");
				if (arrInsert.length == 0) document.getElementById("btnGridAgregar").src = "imagenes/agregar_d.png";
				szBorrar = AyudaXML.find("Ayuda>borraRegistro").text().toUpperCase();
				szCambiar = AyudaXML.find("Ayuda>CambiaRegistro").text().toUpperCase();
				szRegresaDatos = AyudaXML.find("Ayuda>regresaDatos").text().toUpperCase();
				szAnchoDlg = AyudaXML.find("Ayuda>anchoWin").text();
				szAltoDlg = AyudaXML.find("Ayuda>altoWin").text();
				var szAltoAreaCaptura = parseInt(AyudaXML.find("Ayuda>altoAreaCaptura").text(), 10);

				var altoWin = document.body.clientHeight - 65;
				$("#divTXTyTables").css('height', szAltoAreaCaptura);
				$("#divGrid").css('height', (altoWin - szAltoAreaCaptura));


				if (szRegresaDatos == "NO") {
					gbPrimera = true;
				}

				subGeneraInfoGrid();

				$("input.txtGridCol:first").focus();

				/*$("input.txtGridCol").each(function(i) 
					{  //alert($(this).attr("id")); 
					$(this).focus(); 
						
					});*/
			}
			else {
				alert("Error..!\rNo existe la definici\00f3n de la ayuda \r\r" + idCampo);
			}
			AyudaXML = null;

		}

		$("input.txtAyuda").keyup(
			function(e) {
				// elimina los caracteres no deseados
				var keyCode = e.keyCode;
				if (keyCode == 144 || keyCode == 91 || keyCode == 45 || keyCode == 44 || keyCode == 40 || keyCode == 39 || keyCode == 38 || keyCode == 37 || keyCode == 36 || keyCode == 35 || keyCode == 34 || keyCode == 33 || keyCode == 20 || keyCode == 19 || keyCode == 18 || keyCode == 17 || keyCode == 16 || keyCode == 13 || keyCode == 9) return;
				//alert(keyCode);

				window.clearInterval(giTimer);

				var szTemp = $(this).attr("tabla");
				var nClone = funGetCloneNumber($(this));
				var szTabla;

				$("#lb" + szTemp + "Desc" + nClone).css("backgroundColor", document.background-color);

				// limpia los controles de clase txtAyuda con dependencias de este control													
				$("input.txtAyuda").each(function(i) {
					if ($(this).attr("dependencias").indexOf(szTemp + nClone) >= 0) {
						var nClone2 = funGetCloneNumber($(this));
						szTabla = $(this).attr("tabla");
						$("#txt" + szTabla + nClone2).val("");
						$("#lb" + szTabla + "Desc" + nClone2).val("seleccionar...");
						$("#lb" + szTabla + "Desc" + nClone2).css("backgroundColor", document.background-color);
					}
				});
				$("input.txtAyudaDesc").each(function(i) {
					if ($(this).attr("dependencias").indexOf(szTemp + nClone) >= 0) {
						$(this).val("");
					}
				});

				// si modifican un text elimina cualquier cosa del grid de consulta
				if ($("#divGrid").attr('toggle') == "visible") {
					$("#divGrid").fadeOut("slow");
					$("#divGrid").attr('toggle', "hidden");
				}
				$("#divGrid").attr('origen', "");

				// limpia el label de este control si texto vacio
				szTabla = $(this).attr("tabla");
				if ($(this).val() == "") {
					$("#lb" + szTabla + "Desc" + nClone).val("seleccionar...");
					return;
				}
				$("#lb" + szTabla + "Desc" + nClone).val("capturando clave...");

				giTimer = window.setInterval("subTimerKeyUp(\"txt" + szTabla + nClone + "\")", 500);

			}
		); // fin de txtAyuda keyup

		$('input.txtGridCol').keyup(
			function(e) {
				var txtTyping = (e.target || e.srcElement);
				for (var i = 0; i < arrColumnas.length; i = i + 1) {
					if (txtTyping.id == "txtGrid" + (arrColumnas[i].getAttribute("control") == "" ? "Col" + i : arrColumnas[i].getAttribute("control"))) {
						//if (document.getElementById("btnGridAgregar").src.indexOf("agregar.png")>0)
						{
							str1 = arrColumnas[i].getAttribute("limpiaOnKeypress").split(",");
							for (var j = 0; j < str1.length; j = j + 1) {
								$("#" + str1[j]).val("");
							}
							txtTyping.style.backgroundColor = "#ffffff";
							break;
						}
					}
				}
			}
		);

		$('body')		// eventos del body
			.mouseover(
				function(e) {
					try {
						var objElemento = (e.target || e.srcElement);

						switch (objElemento.className) {
							case 'tdDetalle':
								gclass = objElemento.parentNode.className;
								objElemento.parentNode.className = "hover"
								break;
						}
					}
					catch (e) {
						//alert(e.description);
					}
				})
			.mouseout(
				function(e) {
					try {
						var objElemento = (e.target || e.srcElement);

						switch (objElemento.className) {
							case 'tdDetalle':
								objElemento.parentNode.className = gclass;
								break;
						}
					}
					catch (e) {
						//alert( e.description);
					}
				}
			)
			.click(
				function(e) {
					var objElemento = (e.target || e.srcElement);

					if (objElemento.className == "btnGrid")			// s boton de filtrar del grid
					{
						if (objElemento.id == 'btnGridFiltrar')
							subLlenaGridAyuda();
						if (objElemento.id == 'btnGridAgregar' && document.getElementById("btnGridAgregar").src.indexOf("agregar.png") > 0)
							subInsertaAyuda();
						if (objElemento.id == 'btnGridBorrar' && document.getElementById("btnGridBorrar").src.indexOf("borrar.png") > 0)
							subBorraAyuda();
						if (objElemento.id == 'btnGridCambiar' && document.getElementById("btnGridCambiar").src.indexOf("Cambiar.png") > 0)
							subCambiaAyuda();
						if (objElemento.id == 'btnGridLimpiar')
							subLimpiaAyuda();
					}

					if (objElemento.className == "tdDetalle")			// s seleccionaron un renglon del grid
					{
						if (szRegresaDatos == "NO") return;
						var szTR = objElemento.parentNode.innerHTML;
						var szTD;

						if (szBorrar == "SI" || szCambiar == "SI")
							szTR = szTR.substr(szTR.toLowerCase().indexOf( "</td>") + 5);

						for (var i = 0; i < arrColumnas.length; i = i + 1) {
							try {
								szControl = arrColumnas[i].getAttribute("control");
								szTD = szTR.substr(0, szTR.toLowerCase().indexOf( "</td>"));
								szTD = szTD.substr(szTD.indexOf(">") + 1);

								szTD = szTD.replace("&amp;", "&");//se reemplaza el &amp; por &

								szTR = szTR.substr(szTR.toLowerCase().indexOf("</td>") + 5);
								if (szControl != "") {
									if (szTD == "&nbsp;") szTD = "";
									//opener.document.getElementById(szControl).value = szTD;
									try {
										szConca = arrColumnas[i].getAttribute("concatena").toUpperCase();
										if (szConca == "S") {
											//alert(opener.document.getElementById(szControl).value.indexOf(szTD));
											if (opener.document.getElementById(szControl).value.indexOf(szTD) >= 0)
												szTD = opener.document.getElementById(szControl).value;
											else if (opener.document.getElementById(szControl).value != "")
												szTD = opener.document.getElementById(szControl).value + "; " + szTD;
										}
									}
									catch (ex) {
									}
									
									if(szTD != "undefined")
										opener.ReemplazaValor(szControl, szTD);
										
									opener.LimpiaCtrlsDep(szControl);

								}
							}
							catch (ex) {
								alert(ex.message)
							}

						}
						idCampo = window.location.href;
						idCampo = idCampo.substr(idCampo.indexOf("?") + 4);
						opener.$("#" + idCampo).trigger('change');
						window.close();
					}

					if (objElemento.className == "btnAyuda")		// s oprimieron el boton de mostrar grid
					{
						var idCampo = $(objElemento).attr('id');
						szAnchoDlg = ", width=400";
						szAltoDlg = ", height=400";

						idCampo = idCampo.substr(3);

						var arrContexto = document.location.pathname.split('/');
						var URL = document.location.protocol + "//" + (document.location.host == "" ? "localhost" : document.location.host) + "/" + arrContexto[1] + "/Ayudas/";

						AyudaXML = loadXML(URL + "xml-ayudas/" + idCampo + ".xml");
						
						if (AyudaXML) {
							szAnchoDlg = ",width=" + AyudaXML.find("Ayuda>anchoWin").text();
							szAltoDlg = ", height=" + AyudaXML.find("Ayuda>altoWin").text();
						}
						else {
							alert("Error..!\rNo existe la definici\00f3n de la ayuda \r\r" + idCampo);
							return;
						}
						AyudaXML = null;
						wndLCD = window.open(URL + "dlg2.0.jsp?id=" + idCampo, idCampo, "directories = no, resizable = no, menubar = no, titlebar =no, status=yes, scrollbars = no " + szAnchoDlg + szAltoDlg, false);
						wndLCD.focus();

					}

					if (objElemento.className == "btnClose")		// s oprimieron el boton de cerrar grid
					{
						$("#divGrid").fadeOut("slow");
						$("#divGrid").attr('toggle', "hidden");
					}
				}
			)

			.keyup(
				function(e) {

				}
			); // fin del body
		$(window).resize(function() {

			$("#divTXTyTables").width('100%');
			$("#divGrid").width('100%');

		});


	}); // fin de ready		

function subTimerKeyUp(pControl) {
	window.clearInterval(giTimer);
	// busca las dependencias de este control
	var szValor = funValidaDependencias("#" + pControl);
	if (szValor == "MAL") {
		return;
	}

	if (szValor != "")
		szValor += " AND ";

	var arrVsControl = $("#" + pControl).attr("tablavscontrol").split(',');
	var arrTipos = $("#" + pControl).attr("tablaTipos").split(',');
	var szTipo = "";
	var szTabla = $("#" + pControl).attr("tabla");
	var i = 0;

	szValor += $("#" + pControl).attr("campo") + " = ";
	szTipo = "";
	for (i = 0; i < arrVsControl.length; i = i + 1) {
		if (arrVsControl[i] == szTabla) {
			szTipo = arrTipos[i];
			break;
		}
	}
	if (szTipo == "numero") {
		szValor += $("#" + pControl).val();
	}
	if (szTipo == "string") {
		szValor += "'" + $("#" + pControl).val() + "'";
	}
	subWSAyudaTraeDesc(szTabla, szValor);

	for (i = 0; i < arrVsControl.length; i = i + 1) {
		if ($("#txt" + arrVsControl[i]).attr("className") == "txtAyudaDesc") {
			// busca las dependencias de txt .. Desc
			szValor = funValidaDependencias("#txt" + arrVsControl[i]);
			if (szValor == "MAL") {
				return;
			}

			subWSAyudaTraeTxTDesc($("#txt" + arrVsControl[i]).attr("tabla"), szValor);
		}
	}
}

// trae la descripcin de una clave
function subWSAyudaTraeDesc(pTabla, pValor) {

	$("#lb" + pTabla + "Desc").val("Buscando...");

	$.ajax
		({
			type: "POST",
			url: document.location.protocol + "//" + (document.location.host == "" ? "localhost" : document.location.host) + "/xxvisionwebser/wsalarmas.asmx/GetAyudaInfo",	// llamada al web service de traer la descripcin
			dataType: "html",
			data: "pTabla=" + pTabla + "&pValor=" + pValor,
			error:
				function(p1, p2, p3) {
					$("#lb" + pTabla + "Desc").val("Error...");
					$("#lb" + pTabla + "Desc").css("backgroundColor", "tomato");
				},
			success:
				function(responseText) {
					szTemp = responseText.funCambiaLlaves();
					var szTemp = szTemp.substr(szTemp.indexOf("<descripcion>") + 13);
					szTemp = szTemp.substr(0, szTemp.indexOf("</descripcion>"));

					$("#lb" + pTabla + "Desc").val(szTemp);
					if (szTemp.indexOf("Error") >= 0) {
						$("#lb" + pTabla + "Desc").css("backgroundColor", "tomato");
					}
				}
		});
}

// trae la descripcin de una clave
function subWSAyudaTraeTxTDesc(pTabla, pValor) {

	$("#txt" + pTabla).val("Buscando...");

	$.ajax
		({
			type: "POST",
			url: document.location.protocol + "//" + (document.location.host == "" ? "localhost" : document.location.host) + "/xxvisionwebser/wsalarmas.asmx/GetAyudaInfo",	// llamada al web service de traer la descripcin
			dataType: "html",
			data: "pTabla=" + pTabla + "&pValor=" + pValor,
			error:
				function(p1, p2, p3) {
					$("#txt" + pTabla).val("Error...");
				},
			success:
				function(responseText) {
					szTemp = responseText.funCambiaLlaves();
					var szTemp = szTemp.substr(szTemp.indexOf("<descripcion>") + 13);
					szTemp = szTemp.substr(0, szTemp.indexOf("</descripcion>"));

					$("#txt" + pTabla).val(szTemp);

				}
		});
}

function funValidaDependencias(pControl) {
	// busca las dependencias de este control
	if ($(pControl).attr("tablavscontrol") == undefined) return $(pControl).attr("tablavscontrol");
	var arrVsControl = $(pControl).attr("tablavscontrol").split(',');
	var arrTipos = $(pControl).attr("tablaTipos").split(',');

	var szValor = "";
	var szTipo = "";
	var szTemp = $(pControl).attr("dependencias");
	szTemp = $.trim(szTemp);
	if (szTemp != "ninguna") {
		szTemp += ",";
		do {
			szTabla = szTemp.substr(0, szTemp.indexOf(","))
			szVal = $("#txt" + szTabla).val();
			if (szVal != null) {
				if ($.trim(szVal) == "") {
					alert('Error...!\n\n\"' + $(pControl).attr("tabla") + '\" depende de \"' + szTabla + '\"\nseleccione primero un valor de \"' + szTabla + '\".');
					$(pControl).val("");
					$("#txt" + szTabla).focus();
					return "MAL";
				}
				if (szValor != "")
					szValor += " AND ";

				szValor += $("#txt" + szTabla).attr("campo") + " = ";

				szTipo = "";
				for (var i = 0; i < arrVsControl.length; i = i + 1) {
					if (arrVsControl[i] == szTabla) {
						szTipo = arrTipos[i];
						break
					}
				}
				if (szTipo == "numero") {
					szValor += $("#txt" + szTabla).val();
				}
				if (szTipo == "string") {
					szValor += "'" + $("#txt" + szTabla).val() + "'";
				}
			}
			szTemp = szTemp.substr(szTemp.indexOf(",") + 1);
		} while (szTemp.indexOf(",") >= 0)
	}
	else
		szValor = "";
	return szValor;
}

// construye el where del filtro
function funGeneraWhere() {
	var szWhere = "";
	$("input.txtGridCol").each(function(i) {
		i++;
		var szString = $(this).val();
		szString = $.trim(szString);
		if (szString != "") {
			if (szWhere.length > 0) {
				szWhere += " AND ";
			}
			szWhere += $(this).attr("campo");
			if ($(this).attr("tipo") == "numero") {
				szWhere += "=" + $(this).val();
			} else if ($(this).attr("tipo") == "fecha") {
				szWhere += " " + $(this).attr("opFecha") + " '" + szString + "'";
				if ($(this).attr("opFecha").toUpperCase() == "BETWEEN") {
					szWhere += " AND '" + $("#" + $(this).attr("id") + "Between").val() + "'";
				}
			}
			else {
				szString = $(this).val();
				/*					if (szString.indexOf("%") >= 0)
									{
										szWhere += " LIKE "
									}
									else
									{
										szWhere += "="
									}
				*/
				szWhere += " LIKE '%" + szString + "%'";
			}
		}
	});

	if (szWhere.length == 0) {
		szWhere = "TODO"
	}
	return szWhere;
}

function CreaTxtGridConTabla() {
	var szResult = "";
	var szTemp;
	var iRow = 0;
	var iCol = 0;

	try {
		// busca el maximo renglon y columna para crear la tabla
		for (var i = 0; i < arrColumnas.length; i = i + 1) {
			var arrRowCol = arrColumnas[i].getAttribute("rowCol").split(",");
			if (parseInt(arrRowCol[0], 10) > iRow) iRow = parseInt(arrRowCol[0], 10);
			if (parseInt(arrRowCol[1], 10) > iCol) iCol = parseInt(arrRowCol[1], 10);
		}

		szResult = "<table width='100%' border='0'>\r";
		for (var i = 1; i <= iRow; i = i + 1) {
			szResult += "<tr id='trRenglon" + i + "'>\r";
			for (var j = 1; j <= iCol; j = j + 1) {
				var iMaxCol = 0;
				for (var k = 0; k < arrColumnas.length; k = k + 1) {
					var arrRowCol = arrColumnas[k].getAttribute("rowCol").split(",");
					if (parseInt(arrRowCol[0], 10) == i) {
						if (parseInt(arrRowCol[1], 10) > iMaxCol) iMaxCol = parseInt(arrRowCol[1], 10);
					}
				}
				szResult += "<td id='tdRow" + i + "Col" + j + "'";
				if (iMaxCol == j && j < iCol) {
					szResult += " colspan='" + (iCol - iMaxCol + 1) + "'>&nbsp;</td>\r";
					break;
				}
				else
					szResult += ">&nbsp;</td>\r";

			}
			szResult += "</tr>\r";
		}
		szResult += "</table>\r";
		$("#divTXTyTables").attr('innerHTML', szResult);

		for (var i = 0; i < arrColumnas.length; i = i + 1) {
			szResult = "";

			if (arrColumnas[i].getAttribute("visible").toUpperCase() == "NO")	// si el campo no es visible
			{
				szResult += "<input id=" + (arrColumnas[i].getAttribute("control").indexOf("txtGrid") >= 0 ? "" : "txtGrid") + (arrColumnas[i].getAttribute("control") == "" ? "Col" + i : arrColumnas[i].getAttribute("control"));
				szResult += " name=txtGridCol" + (i) + " class='txtGridCol' campo=" + arrColumnas[i].getAttribute("campo") + " tipo=" + arrColumnas[i].getAttribute("tipo");
				if (arrColumnas[i].getAttribute("dependencia").toUpperCase() == "SI") {
					if (opener.location.href.indexOf("/dlg2.0.jsp") >= 0)
						szResult += " value='" + opener.DameValor(arrColumnas[i].getAttribute("control")) + "'";
					else
						szResult += " value='" + opener.document.getElementById(arrColumnas[i].getAttribute("control")).value + "'";
				}
				szResult += " type=text style='VISIBILITY:hidden; width:1'>";
			}
			else {
				szResult += "<span class='labelAyuda'>" + arrColumnas[i].getAttribute("header") + ":</span><br>";
				szResult += "<input id=" + (arrColumnas[i].getAttribute("control").indexOf("txtGrid") >= 0 ? "" : "txtGrid") + (arrColumnas[i].getAttribute("control") == "" ? "Col" + (i) : arrColumnas[i].getAttribute("control"));
				szResult += " name=txtGridCol" + (i) + " campo='" + arrColumnas[i].getAttribute("campo") + "' tipo=" + arrColumnas[i].getAttribute("tipo");
				if (arrColumnas[i].getAttribute("dependencia").toUpperCase() == "SI") {
					if (opener.location.href.indexOf("/dlg2.0.jsp") >= 0)
						szResult += " value='" + opener.DameValor(arrColumnas[i].getAttribute("control")) + "'";
					else
						szResult += " value='" + opener.document.getElementById(arrColumnas[i].getAttribute("control")).value + "'";
				} else if (arrColumnas[i].getAttribute("defaultValue") && arrColumnas[i].getAttribute("defaultValue").toUpperCase() != "") {
					szResult += " value='" + arrColumnas[i].getAttribute("defaultValue") + "'";

				} else if (arrColumnas[i].getAttribute("anioDinamic") && arrColumnas[i].getAttribute("anioDinamic").toUpperCase() != "") {
					var f = new Date();
					szResult += " value='" + f.getFullYear() + "'";

				}
				szResult += " type=text maxLength=" + arrColumnas[i].getAttribute("maxlen") + " size=" + arrColumnas[i].getAttribute("ancho");

				szTemp = arrColumnas[i].getAttribute("clase").toUpperCase();
				if (szTemp.indexOf("PROTEGIDO") >= 0) {
					szResult += " readOnly  style='BACKGROUND-COLOR: #eeeeee'";
				}
				else {
					for (var k = 0; k < arrInsert.length; k++) {
						if (arrColumnas[i].getAttribute("campo").toUpperCase() == arrInsert[k].getAttribute("field").toUpperCase() && arrInsert[k].getAttribute("llave").toUpperCase() == "SI") {
							szResult += " style='BACKGROUND-COLOR: AliceBlue'";
						}
					}

				}

				szResult += " class='txtGridCol"
				if (szTemp.indexOf("PROTEGIDO") < 0 && szTemp.indexOf("AYUDASYC") >= 0) {
					szResult += " " + arrColumnas[i].getAttribute("clase")
				}
				szResult += "' ";

				if (arrColumnas[i].getAttribute("tipo") == "fecha") {
					szResult += "datepicker_format='DD/MM/YYYY' datepicker='true' opFecha='" + arrColumnas[i].getAttribute("opFecha") + "' readOnly style='BACKGROUND-COLOR: #eeeeee' ";
				}
				szResult += " onkeypress=Validaciones(this," + (arrColumnas[i].getAttribute("tipo") == "numero" ? "2" : "4") + ") >";

				if (arrColumnas[i].getAttribute("tipo") == "fecha" && arrColumnas[i].getAttribute("opFecha").toUpperCase() == "BETWEEN") {
					szResult += "&nbsp;-&nbsp;<input id=" + (arrColumnas[i].getAttribute("control").indexOf("txtGrid") >= 0 ? "" : "txtGrid") + (arrColumnas[i].getAttribute("control") == "" ? "Col" + (i) : arrColumnas[i].getAttribute("control")) + "Between";
					szResult += " name=txtGridCol" + (i) + " campo='" + arrColumnas[i].getAttribute("campo") + "Between" + "' tipo=" + arrColumnas[i].getAttribute("tipo");
					szResult += " type=text maxLength=" + arrColumnas[i].getAttribute("maxlen") + " size=" + arrColumnas[i].getAttribute("ancho");
					szResult += " class='txtGridColBetween' "
					szResult += "datepicker_format='DD/MM/YYYY' datepicker='true' opFecha='" + arrColumnas[i].getAttribute("opFecha") + "' readOnly style='BACKGROUND-COLOR: #eeeeee' >";
				}

				if (szTemp.indexOf("AYUDASYC") >= 0) {
					var szClase = (arrColumnas[i].getAttribute("control").indexOf("txtGrid") >= 0 ? "" : "txtGrid") + (arrColumnas[i].getAttribute("control") == "" ? "Col" + (i) : arrColumnas[i].getAttribute("control"));
					//agregamos botn de consulta
					szResult += "<input id='btn" + szClase + "' name='btn" + szClase + "' type='button' class='btnAyuda' value='...' title='Consultar' tabindex=-1 hidefocus>";
				}

				szResult += "<br>";
			}
			//szTableCols += "<th nowrap=true id='thCol" + (i) + "' class='ColConsulta'>" + arrColumnas[i].getAttribute("header") + "</th>\r\n";
			if (arrColumnas[i].getAttribute("visible") != "No") {
				szTableCols += "<th nowrap=true id='thCol" + (i) + "' class='ColConsulta'>" + arrColumnas[i].getAttribute("header") + "</th>\r\n";
			}
			else {
				szTableCols += "<th nowrap=true id='thCol" + (i) + "' style='VISIBILITY:hidden; WIDTH:1px; '></th>\r\n";
			}

			var arrRowCol = arrColumnas[i].getAttribute("rowCol").split(",");
			$("#tdRow" + arrRowCol[0] + "Col" + arrRowCol[1]).attr('innerHTML', szResult);
		}


	}
	catch (ex) {
		CreaTxtGridSinTabla();
	}
}

function CreaTxtGridSinTabla() {
	var szResult = "";
	var szTemp;

	for (var i = 0; i < arrColumnas.length; i = i + 1) {
		if (arrColumnas[i].getAttribute("visible").toUpperCase() == "NO")	// si el campo no es visible
		{
			szResult += "<input id=" + (arrColumnas[i].getAttribute("control").indexOf("txtGrid") >= 0 ? "" : "txtGrid") + (arrColumnas[i].getAttribute("control") == "" ? "Col" + (i) : arrColumnas[i].getAttribute("control"));
			szResult += " name=txtGridCol" + (i) + " class='txtGridCol' campo=" + arrColumnas[i].getAttribute("campo") + " tipo=" + arrColumnas[i].getAttribute("tipo");
			if (arrColumnas[i].getAttribute("dependencia").toUpperCase() == "SI") {
				if (opener.location.href.indexOf("/dlg2.0.jsp") >= 0)
					szResult += " value='" + opener.DameValor(arrColumnas[i].getAttribute("control")) + "'";
				else
					szResult += " value='" + opener.document.getElementById(arrColumnas[i].getAttribute("control")).value + "'";
			}

			szResult += " type=text style='VISIBILITY:hidden; width:1'>";
		}
		else {
			szResult += "<span class='labelAyuda'>" + arrColumnas[i].getAttribute("header") + ":</span><br>";
			szResult += "<input id=" + (arrColumnas[i].getAttribute("control").indexOf("txtGrid") >= 0 ? "" : "txtGrid") + (arrColumnas[i].getAttribute("control") == "" ? "Col" + i : arrColumnas[i].getAttribute("control"));
			szResult += " name=txtGridCol" + (i) + " campo='" + arrColumnas[i].getAttribute("campo") + "' tipo=" + arrColumnas[i].getAttribute("tipo");
			if (arrColumnas[i].getAttribute("dependencia").toUpperCase() == "SI") {
				if (opener.location.href.indexOf("/dlg2.0.jsp") >= 0)
					szResult += " value='" + opener.DameValor(arrColumnas[i].getAttribute("control")) + "'";
				else
					szResult += " value='" + opener.document.getElementById(arrColumnas[i].getAttribute("control")).value + "'";
			} else if (arrColumnas[i].getAttribute("defaultValue") && arrColumnas[i].getAttribute("defaultValue").toUpperCase() != "") {
				szResult += " value='" + arrColumnas[i].getAttribute("defaultValue") + "'";
			} else if (arrColumnas[i].getAttribute("anioDinamic") && arrColumnas[i].getAttribute("anioDinamic").toUpperCase() != "") {
				var f = new Date();
				szResult += " value='" + f.getFullYear() + "'";

			}
			szResult += " type=text maxLength=" + arrColumnas[i].getAttribute("maxlen") + " size=" + arrColumnas[i].getAttribute("ancho");

			szTemp = arrColumnas[i].getAttribute("clase").toUpperCase();
			if (szTemp.indexOf("PROTEGIDO") >= 0) {
				szResult += " readOnly  style='BACKGROUND-COLOR: #eeeeee'";
			}
			else {
				for (var k = 0; k < arrInsert.length; k++) {
					if (arrColumnas[i].getAttribute("campo").toUpperCase() == arrInsert[k].getAttribute("field").toUpperCase() && arrInsert[k].getAttribute("llave").toUpperCase() == "SI") {
						szResult += " style='BACKGROUND-COLOR: AliceBlue'";
					}
				}

			}

			szResult += " class='txtGridCol"
			if (szTemp.indexOf("PROTEGIDO") < 0 && szTemp.indexOf("AYUDASYC") >= 0) {
				szResult += " " + arrColumnas[i].getAttribute("clase")
			}
			szResult += "' ";

			if (arrColumnas[i].getAttribute("tipo") == "fecha") {
				szResult += "datepicker_format='DD/MM/YYYY' datepicker='true' opFecha=" + arrColumnas[i].getAttribute("opFecha") + " readOnly style='BACKGROUND-COLOR: #eeeeee' ";

			}

			szResult += " onkeypress=Validaciones(this," + (arrColumnas[i].getAttribute("tipo") == "numero" ? "2" : "4") + ") >";

			if (szTemp.indexOf("AYUDASYC") >= 0) {
				var szClase = (arrColumnas[i].getAttribute("control").indexOf("txtGrid") >= 0 ? "" : "txtGrid") + (arrColumnas[i].getAttribute("control") == "" ? "Col" + (i) : arrColumnas[i].getAttribute("control"));
				//agregamos botn de consulta
				szResult += "<input id='btn" + szClase + "' name='btn" + szClase + "' type='button' class='btnAyuda' value='...' title='Consultar' tabindex=-1 hidefocus>";
			}

			szResult += "<br>";
		}
		//szTableCols += "<th nowrap=true id='thCol" + (i) + "' class='ColConsulta'>" + arrColumnas[i].getAttribute("header") + "</th>\r\n"
		if (arrColumnas[i].getAttribute("visible") != "No") {
			szTableCols += "<th nowrap=true id='thCol" + (i) + "' class='ColConsulta'>" + arrColumnas[i].getAttribute("header") + "</th>\r\n"
		}
		else {
			szTableCols += "<th nowrap=true id='thCol" + (i) + "' style='VISIBILITY:hidden; WIDTH:1px; '></th>\r\n";
		}
	}

	$("#divTXTyTables").attr('innerHTML', szResult);
}

// subrutina que genera el contenido del div de ayudas
function subGeneraInfoGrid() {
	var nNumCols = arrColumnas.length;


	if (szBorrar == "SI" || szCambiar == "SI")	// => aparece la accion de Borrar o Cambiar agrega columna de seleccion
		szTableCols = "<th nowrap=true id='thCol00' class='ColConsulta'>Sel</th>\r\n";
	else
		szTableCols = "";

	CreaTxtGridConTabla();

	subLlenaGridAyuda();

	$("input.autoCompletaSyC").subIniciaAutoCompleta();


	DPC_autoInit();
	//DatePickerControl.onWindowResize();		
	DPC_onWindowResize();
}


function subLlenaGridAyuda() {
	var szWhere = funGeneraWhere();
	var szResponse = "";
	var szTemp = "";

	try {
		for (var i = 0; i < arrInsert.length; i = i + 1) {
			if (arrInsert[i].getAttribute("llave").toUpperCase() != "SI")
				document.getElementById(arrInsert[i].getAttribute("requeridoFocus")).style.backgroundColor = "#ffffff";
			else
				document.getElementById(arrInsert[i].getAttribute("requeridoFocus")).style.backgroundColor = "AliceBlue";
		}
	}
	catch (ex) {
	}

	if (gbPrimera) {
		szWhere = "1=2";
		gbPrimera = false;
	}
	//alert(szWhere);
	$.getJSON("../catalogos/SelectJson.jsp", { Tabla: szTabla, Param: szWhere, MaxReg: iMaxReg },
		function(datos) {
			szResponse += "<table id='tblDetalle' cellpadding='0' width='100%' cellspacing='0' class='tblGrid'>\r";
			szResponse += "<thead id='fixedHeader'>\r";
			szResponse += "<tr>\r";
			szResponse += szTableCols;
			szResponse += "</tr>\r";
			szResponse += "</thead>\r";
			szResponse += "<tbody id='tableBody'>\r";

			if (datos.length > 0) {
				for (var i = 0; i < datos.length; i++) {
					if (i >= iMaxReg) break;
					szTemp += "<tr class='trRow " + (i % 2 == 0 ? "odd" : "even") + "' id='trRowDub" + i + "'>\r";

					if (szBorrar == "SI" || szCambiar == "SI")	// => aparece accion de Borrar o Cambiar agrega columna de seleccion
					{
						szTemp += "<td name='thSele' nowrap=true class='tdDetalle'><img onclick=funSeleccionarRow(" + i + ") src='imagenes/seleccion.png' style='CURSOR: hand;' title='seleccionar registro'></td>\r"
					}
					// if (datos.length == 3)
					// {
					//	alert(szTemp)
					// }
					for (var m = 0; m < arrColumnas.length; m++) {
						szTemp += "<td nowrap=true " + (arrColumnas[m].getAttribute("visible") == "No" ? " class='nada' style='WIDTH:1px; VISIBILITY:hidden; FONT-SIZE:1pt;'" : "class='tdDetalle'") + ">"
						szTemp += (eval("datos[" + i + "].Col" + m) == " " || eval("datos[" + i + "].Col" + m) == "" || eval("datos[" + i + "].Col" + m) == "NULL" ? "&nbsp;" : eval("datos[" + i + "].Col" + m));
						szTemp += "</td>\r"
					}
					szTemp += "</tr>\r"
				}
			}

			szResponse += szTemp;
			szResponse += "</tbody>\r";
			if (datos.length > iMaxReg) {
				szResponse += "<tfoot><tr><td colspan=" + arrColumnas.length + ">* solo se muestran los primeros " + iMaxReg + " registros</td></tr></tfoot>";
			}
			if (datos.length == 0) {
				szResponse += "<tfoot><tr><td colspan=" + arrColumnas.length + ">* no existen registros con ese filtro de b\00e1squeda.</td></tr></tfoot>";
			}
			szResponse += "</table>";
			$("#divTableContainer").attr('innerHTML', szResponse);


			var iAncho = 0;
			var iCuantos = 0;
			/*				$("th.ColConsulta").each(function(i) {
								objss = $("input[@name=txtGridCol" + i + "]");					
								//$(this).width($(objss).width() - ($.browser.mozilla?2:0));  // 22= +padding + bordes
								iAncho += $(objss).width();		alert(	"i="+ i + " iCuantos=" + iCuantos + "  " + 	iAncho+ "  " + $("input[@name=txtGridCol" + i + "]").attr('name'));
								i=i+1;
								iCuantos = iCuantos+1;						 
							});											
			*/
			$("input.txtGridCol").each(function(i) {
				//alert($(this).attr("style").indexOf("hidden") + "    " + $(this).width())
				if ($(this).attr("style") && $(this).attr("style").indexOf("hidden") < 0)
					iAncho += $(this).width();

				//alert(	"i="+ i + " iCuantos=" + iCuantos + "  " + 	iAncho+ "  " + $("input[@name=txtGridCol" + i + "]").attr('name'));
				i = i + 1;
				iCuantos = iCuantos + 1;
			});

			iAncho = iAncho + (6 * iCuantos) + 16 - ($.browser.mozilla > 0 ? 5 : 0);
			
			if (iAncho > document.body.clientWidth) iAncho = document.body.clientWidth - 6;

			if ($.browser.msie) $("#fixedHeader").addClass("fixedHeader");

			$("#divGrid").css("width", "100%");
			$("#divTXTyTables").css("width", "100%");

			$("#divTableContainer").css("width", "100%");

			if (szBorrar == "SI" || szCambiar == "SI")	// => aparece accion de Borrar o Cambiar agrega columna de seleccion
			{
				$("#tblDetalle").tablesorter({
					sortColumn: 1,						// nmero entero del ndice o nombre de la columna en minsculas
					sortClassAsc: 'headerSortUp',		// clase css de la cabecera de la columna cuando aplicamos un orden ascendente
					sortClassDesc: 'headerSortDown',	// clase css de la cabecera de la columna cuando aplicamos un orden descendente
					headerClass: 'header',				// clase genrica de las cabeceras (th's)
					stripingRowClass: ['even', 'odd'],	// Estilos css para las lneas pares y para las impares.
					stripeRowsOnStartUp: true			// Dibujamos las lneas de las tablas con distintos css indicados en strinpingRowClass.

				});

			}
			else
				$("#tblDetalle").tablesorter({
					sortColumn: 0,						// nmero entero del ndice o nombre de la columna en minsculas
					sortClassAsc: 'headerSortUp',		// clase css de la cabecera de la columna cuando aplicamos un orden ascendente
					sortClassDesc: 'headerSortDown',	// clase css de la cabecera de la columna cuando aplicamos un orden descendente
					headerClass: 'header',				// clase genrica de las cabeceras (th's)
					stripingRowClass: ['even', 'odd'],	// Estilos css para las lneas pares y para las impares.
					stripeRowsOnStartUp: true			// Dibujamos las lneas de las tablas con distintos css indicados en strinpingRowClass.

				});

			$("#thCol" + iColSort).click();

			$("#txtGrid" + szTabla).focus();
		}
	)
}

function subInsertaAyuda() {
	var szCampos = "";
	var szTemp = "";


	for (var i = 0; i < arrInsert.length; i = i + 1) {
		szTemp = $("#" + arrInsert[i].getAttribute("control")).val();
		szTemp = $.trim(szTemp);
		if (arrInsert[i].getAttribute("requerido") != "" && szTemp == "")
			if (arrInsert[i].getAttribute("requeridoFocus") != "") document.getElementById(arrInsert[i].getAttribute("requeridoFocus")).style.backgroundColor = "#ffffbb";
			else {
				if (arrInsert[i].getAttribute("llave").toUpperCase() != "SI")
					if (arrInsert[i].getAttribute("requeridoFocus") != "") document.getElementById(arrInsert[i].getAttribute("requeridoFocus")).style.backgroundColor = "#ffffff";
					else
						if (arrInsert[i].getAttribute("requeridoFocus") != "") document.getElementById(arrInsert[i].getAttribute("requeridoFocus")).style.backgroundColor = "AliceBlue";

			}
	}

	for (var i = 0; i < arrInsert.length; i = i + 1) {
		szTemp = $("#" + arrInsert[i].getAttribute("control")).val();
		szTemp = $.trim(szTemp);
		$("#" + arrInsert[i].getAttribute("control")).val(szTemp);
		if (arrInsert[i].getAttribute("requerido") != "" && szTemp == "") {
			$("#" + arrInsert[i].getAttribute("requeridoFocus")).focus();
			alert(arrInsert[i].getAttribute("requerido"));
			return;
		}
		if (arrInsert[i].getAttribute("identity").toUpperCase() != "SI") {
			if (arrInsert[i].getAttribute("tipo").toUpperCase() == "STRING") {
				if (szTemp == "")
					szCampos += null;
				else
					szCampos += "'" + szTemp + "'";
			}
			else {
				if (szTemp == "")
					szCampos += null;
				else
					szCampos += szTemp;
			}
			szCampos += " , ";
		}
	}
	szCampos = szCampos.substr(0, szCampos.length - 3);

	$.getJSON("../catalogos/InsertJson.jsp", { Tabla: szTabla, Param: szCampos },
		function(datos) {
			if (datos[0].Col1 == "S") {
				alert("Registro dado de Alta.");
				subLlenaGridAyuda();
			}
			else {
				alert("Error..!\r" + datos[0].Col1);
			}
		})
}

function subBorraAyuda() {
	var szCampos = "";
	var szTemp = "";
	var szWhere = "";


	for (var i = 0; i < arrInsert.length; i = i + 1) {
		szTemp = $("#" + arrInsert[i].getAttribute("control")).val();
		szTemp = $.trim(szTemp);
		if (arrInsert[i].getAttribute("requerido") != "" && szTemp == "")
			if (arrInsert[i].getAttribute("requeridoFocus") != "") document.getElementById(arrInsert[i].getAttribute("requeridoFocus")).style.backgroundColor = "#ffffbb";
			else {
				if (arrInsert[i].getAttribute("llave").toUpperCase() != "SI")
					if (arrInsert[i].getAttribute("requeridoFocus") != "") document.getElementById(arrInsert[i].getAttribute("requeridoFocus")).style.backgroundColor = "#ffffff";
					else
						if (arrInsert[i].getAttribute("requeridoFocus") != "") document.getElementById(arrInsert[i].getAttribute("requeridoFocus")).style.backgroundColor = "AliceBlue";
			}
	}

	for (var i = 0; i < arrInsert.length; i = i + 1) {
		szTemp = $("#" + arrInsert[i].getAttribute("control")).val();
		szTemp = $.trim(szTemp);
		$("#" + arrInsert[i].getAttribute("control")).val(szTemp);
		if (arrInsert[i].getAttribute("requerido") != "" && szTemp == "") {
			document.getElementById(arrInsert[i].getAttribute("requeridoFocus")).focus();
			alert(arrInsert[i].getAttribute("requerido"));
			return;
		}
		if (arrInsert[i].getAttribute("llave").toUpperCase() == "SI") {
			szWhere += arrInsert[i].getAttribute("field") + "=";
			if (arrInsert[i].getAttribute("tipo").toUpperCase() == "STRING")
				szWhere += "'" + szTemp + "'";
			else
				szWhere += szTemp;
			szWhere += " AND ";
		}
		if (szTabla == "CATALOGOCABM") {
			szCampos += arrInsert[i].getAttribute("field") + "=";
			if (arrInsert[i].getAttribute("tipo").toUpperCase() == "STRING")
				szCampos += "'" + szTemp + "'";
			else
				szCampos += szTemp;
			szCampos += " , ";
		}
	}

	szWhere = szWhere.substr(0, szWhere.length - 5);
	szCampos = szCampos.substr(0, szCampos.length - 3);
	if (confirm("¿Esta Ud seguro de querer borrar el registro? ")) {
		$.getJSON("../catalogos/DeleteJson.jsp", { Tabla: szTabla, Param: szWhere, SetParam: szCampos },
			function(datos) {
				
				if (datos.arrResponse[0].Col1 == "S") {
					alert("Registro borrado.");
					subLlenaGridAyuda();
					subLimpiaAyuda();
				}
				else {
					alert("Error..!\r" + datos.arrResponse[0].Col1);
				}
				return;
			})
	}

}

function subCambiaAyuda() {
	var szCampos = "";
	var szTemp = "";
	var szWhere = "";


	for (var i = 0; i < arrInsert.length; i = i + 1) {
		szTemp = $("#" + arrInsert[i].getAttribute("control")).val();
		szTemp = $.trim(szTemp);
		if (arrInsert[i].getAttribute("requerido") != "" && szTemp == "")
			if (arrInsert[i].getAttribute("requeridoFocus") != "") document.getElementById(arrInsert[i].getAttribute("requeridoFocus")).style.backgroundColor = "#ffffbb";
			else {
				if (arrInsert[i].getAttribute("llave").toUpperCase() != "SI")
					if (arrInsert[i].getAttribute("requeridoFocus") != "") document.getElementById(arrInsert[i].getAttribute("requeridoFocus")).style.backgroundColor = "#ffffff";
					else
						if (arrInsert[i].getAttribute("requeridoFocus") != "") document.getElementById(arrInsert[i].getAttribute("requeridoFocus")).style.backgroundColor = "AliceBlue";
			}
	}
	var aux = "";
	for (var i = 0; i < arrInsert.length; i = i + 1) {
		szTemp = $("#" + arrInsert[i].getAttribute("control")).val();
		szTemp = $.trim(szTemp);
		if (arrInsert[i].getAttribute("requerido") != "" && szTemp == "") {
			szTemp = (szTemp == "" ? "NULLREQ" : szTemp); // GJCG: Correccion para considerar Nulos
			$("#" + arrInsert[i].getAttribute("control")).val(szTemp);
		}
		if (szTemp == "NULLREQ") {
			document.getElementById(arrInsert[i].getAttribute("requeridoFocus")).focus();
			$("#" + arrInsert[i].getAttribute("control")).val(aux);
			alert(arrInsert[i].getAttribute("requerido"));
			return;
		}

		if (arrInsert[i].getAttribute("llave").toUpperCase() == "SI") {
			szWhere += arrInsert[i].getAttribute("field") + "=";
			if (arrInsert[i].getAttribute("tipo").toUpperCase() == "STRING")
				szWhere += "'" + szTemp + "'";
			else
				szWhere += szTemp;
			szWhere += " AND ";
		} else {
			szCampos += arrInsert[i].getAttribute("field") + "=";
			if (arrInsert[i].getAttribute("tipo").toUpperCase() == "STRING")
				szCampos += "'" + szTemp + "'";
			else
				szCampos += szTemp;
			szCampos += " , ";
		}
	}

	szWhere = szWhere.substr(0, szWhere.length - 5);
	szCampos = szCampos.substr(0, szCampos.length - 3);
	$.getJSON("../catalogos/UpdateJson.jsp", { Tabla: szTabla, Param: szWhere, SetParam: szCampos },
		function(datos) {
			
			if (datos.arrResponse[0].Col1 == "S") {
				alert("Registro cambiado.");
				subLlenaGridAyuda();
			}
			else {
				alert("Error..!\r" + datos.arrResponse[0].Col1);
			}
			return;
		})

}

function subLimpiaAyuda() {
	var szCtrl = "";

	document.getElementById("btnGridBorrar").src = "imagenes/borrar_d.png";
	document.getElementById("btnGridCambiar").src = "imagenes/Cambiar_d.png";
	if (arrInsert.length == 0)
		document.getElementById("btnGridAgregar").src = "imagenes/agregar_d.png";
	else
		document.getElementById("btnGridAgregar").src = "imagenes/agregar.png";

	for (var i = 0; i < arrColumnas.length; i++) {
		szCtrl = (arrColumnas[i].getAttribute("control").indexOf("txtGrid") >= 0 ? "" : "txtGrid") + (arrColumnas[i].getAttribute("control") == "" ? "Col" + (i) : arrColumnas[i].getAttribute("control"));
		if (arrColumnas[i].getAttribute("dependencia").toUpperCase() != "SI")
			$("#" + szCtrl).val("");
		//document.getElementById(szCtrl).style.backgroundColor="#ffffff";
		if (arrColumnas[i].getAttribute("tipo") == "fecha" && arrColumnas[i].getAttribute("opFecha").toUpperCase() == "BETWEEN") {
			$("#" + szCtrl + "Between").val("");
		}
		strTemp = arrColumnas[i].getAttribute("clase").toUpperCase();
		if (strTemp.indexOf("PROTEGIDO") < 0) {
			document.getElementById(szCtrl).readOnly = false;
		}
	}

	if (szRegresaDatos == "NO") {
		gbPrimera = true;
	}
	subLlenaGridAyuda();

}

function funSeleccionarRow(pRow) {
	document.getElementById("btnGridAgregar").src = "imagenes/agregar_d.png";;
	if (szBorrar == "SI")	// => aparece el boton de Borrar
		document.getElementById("btnGridBorrar").src = "imagenes/borrar.png";;
	if (szCambiar == "SI")	// => aparece el boton de Borrar
		document.getElementById("btnGridCambiar").src = "imagenes/Cambiar.png";;

	var szTR = $("#trRowDub" + pRow).attr('innerHTML'); // alert(pRow + "\r  " + szTR)
	var szTD;

	szTR = szTR.substr(szTR.toLowerCase().indexOf( "</td>") + 5);

	for (var i = 0; i < arrColumnas.length; i = i + 1) {
		szControl = arrColumnas[i].getAttribute("control");

		szTD = szTR.substr(0, szTR.toLowerCase().indexOf( "</td>"));
		szTD = szTD.substr(szTD.indexOf(">") + 1);
		szTR = szTR.substr(szTR.toLowerCase().indexOf( "</td>" ) + 5 );

		if (szTD == "&nbsp;") szTD = "";


		if (szControl != "") {
			ReemplazaValor((szControl.indexOf("txtGrid") >= 0 ? "" : "txtGrid") + szControl, szTD);
			//				if (szControl.substring(0, 7)  == "txtGrid")
			//				{
			//					ReemplazaValor(szControl, szTD); 
			//				}	
			//				else
			//				{
			//					ReemplazaValor("txtGrid"+szControl, szTD); 
			//				}	
		}
		else {
			ReemplazaValor("txtGridCol" + i, szTD);
		}


	}
	for (var i = 0; i < arrInsert.length; i = i + 1) {
		if (arrInsert[i].getAttribute("llave").toUpperCase() == "SI") {
			document.getElementById(arrInsert[i].getAttribute("control")).readOnly = true;
		}
	}

}

// Creacin del grid de ayuda dinamicamente
$.subCreaAyudaGrid = function() {
	var objDiv;
	var objDiv2;
	var objDiv3;
	var objTemp;

	objDiv = document.createElement('div'); //("<div id='divGrid' class='divGrid' style='overflow:auto; LEFT:00px; TOP:0px; WIDTH: 0px; HEIGHT: 385px' origen='' toggle='hidden' >");
	objDiv.id = 'divGrid';
	objDiv.className = 'divGrid';
	objDiv.style.overflow = 'auto'; objDiv.style.left = '0px'; objDiv.style.top = '0px'; objDiv.style.width = '100%'; objDiv.style.height = '385px';
	objDiv.setAttribute('origen', '')
	objDiv.setAttribute('toggle', 'hidden')
	document.body.appendChild(objDiv);

	$(objDiv).append("<img id='btnClose' name='btnClose' class='btnClose' src='Imagenes/pbClose.bmp' title=' Cerrar' style='POSITION: relative; LEFT: 110px; TOP: 0px'>");

	$(objDiv).append("<label id='lbGridTitulo' class='lbGridTitulo'>");

	$(objDiv).append("<br>");
	$(objDiv).append("<br>");

	$(objDiv).append("<div id='divGridToolbar' style='POSITION: relative; LEFT: 0px; TOP: 0px; WIDTH: 100%; HEIGHT: 27px; BACKGROUND-COLOR: #dddddd'>");

	$("#divGridToolbar").append("<img id='btnGridFiltrar' name='btnGridFiltrar' class='btnGrid' src='Imagenes/boton-filter.jpg' title='Buscar registros que cumplan la condici\00f3n' style='POSITION: relative; LEFT: 5px; TOP: 5px'>");

	$("#divGridToolbar").append("<div id='divConte' style='overflow:auto; POSITION: relative; HEIGHT: 331px; TOP: 8px'>");

	$("#divConte").append("<div id='divTXTyTables' style='OVERFLOW: auto; HEIGHT: 314px; width:\"100%\"'>");

	$("#divGrid").hide();
};

//clona los elementos de ayudas por control
$.subClonaAyudaElementos = function(pTabla) {
	var szTabla = "";
	var nIndex = $("input.txtAyuda").length;
	var iCuantos = 0;

	$("input.txtAyuda").each(function(i) {
		if ($(this).attr("id") == "txt" + pTabla) {
			if (iCuantos > 0) {
				szTabla = $(this).attr("tabla");

				$(this).attr("clone", nIndex);					// asinamos nmero de clone
				$(this).attr("id", "txt" + szTabla + nIndex);	// reid el control
				$(this).attr("name", "txt" + szTabla + nIndex);	// rename el control
				$(this).addClass("txtAyuda");
				var arrVsControl = $("#txt" + szTabla).attr("tablavscontrol").split(',');
				var szTemp = "";
				for (var k = 0; k < arrVsControl.length; k++) {
					szTemp += (k > 0 ? "," : "");
					if (arrVsControl[k] == szTabla)
						szTemp = szTabla + nIndex;
					else
						szTemp += arrVsControl[k];
				}
				$(this).attr("tablavscontrol", szTemp);
				var arrDependencias = $("#txt" + szTabla).attr("dependencias").split(",");
				szTemp = "";
				for (var k = 0; k < arrDependencias.length; k++) {
					szTemp += (k > 0 ? "," : "");
					if (arrDependencias[k] == szTabla)
						szTemp = szTabla + nIndex;
					else
						szTemp += arrDependencias[k];
				}
				$(this).attr("dependencias", szTemp);
			}
			iCuantos++;
		}
		//alert($(this).attr("id") + "   " + $(this).attr("clone"));
	});

	if (szTabla != "") {
		$("input.btnAyuda").each(function(j) {			// hacemos lo mismo para la clase btnAyuda	
			if ($(this).attr("id") == "btn" + szTabla) {
				if (j > 0) {
					if ($(this).attr("clone") == undefined) {
						$(this).attr("clone", nIndex);					// asinamos nmero de clone
						$(this).attr("id", "btn" + szTabla + nIndex);	// reid el control
						$(this).attr("name", "btn" + szTabla + nIndex);	// rename el control
					}
				}
			}
			//alert($(this).attr("id") + "   " + $(this).attr("clone"));
		});

		$("input.lbAyudaDesc").each(function(k) {			// hacemos lo mismo para la clase lbAyudaDesc	
			if ($(this).attr("id") == "lb" + szTabla + "Desc") {
				if (k > 0) {
					if ($(this).attr("clone") == undefined) {
						$(this).attr("clone", nIndex);					// asinamos nmero de clone
						$(this).attr("id", "lb" + szTabla + "Desc" + nIndex);	// reid el control
						$(this).attr("name", "lb" + szTabla + "Desc" + nIndex);	// rename el control
					}
				}
			}
			//alert($(this).attr("id") + "   " + $(this).attr("clone"));
		});
	}
};

function ReemplazaValor(pControl, pValor) {
	$("#" + pControl).val(pValor);
}
function DameValor(pControl) {
	return $("#" + pControl).val();
}

function LimpiaCtrlsDep(pControl) {
	if (typeof (arrColumnas) == "undefined") return;
	for (var i = 0; i < arrColumnas.length; i = i + 1) {
		if ($("#" + pControl).attr("id") == "txtGrid" + arrColumnas[i].getAttribute("control")) {
			if (arrColumnas[i].getAttribute("ctrlsDependientes") != null) {
				str1 = arrColumnas[i].getAttribute("ctrlsDependientes").split(",");
				for (var j = 0; j < str1.length; j = j + 1) {
					$("#" + str1[j]).val("");
				}
			}
			break;
		}
	}
}



//Agrega los elementos de ayudas por control
$.subAddAyudaElementos = function(pControl) {
	var szClase = $(pControl).attr("id");

	//agregamos botn de consulta
	$(pControl).after("<input id='btn" + szClase + "' name='btn" + szClase + "' type='button' class='btnAyuda' value='...' title='Consultar' tabindex=-1 hidefocus>");
};

// procedimiento que inicializa los controles de ayuda en linea	
$.fn.subIniciaDlg = function() {
	this.each(
		function() {
			$.subAddAyudaElementos(this);
		}
	);

}

const subIniciaDlgBootsprap = function() {

	$('input.helper').attr({
		'placeholder': 'Click para buscar',
		'onclick': 'openHelper(event)',
		'readonly': true
	});

}

// funcin que cambia las llaves a & < > de &lt; y &gt; del regreso de los web services
String.prototype.funCambiaLlaves = function() { return this.replace(/&amp;/g, "&").replace(/&lt;/g, "<").replace(/&gt;/g, ">"); }

// funcin que regresa la posicin real de un control
function findPos(obj) {
	var curleft = 0;
	var curtop = 0;
	if (obj.offsetParent) {
		while (obj.offsetParent) {
			curleft += obj.offsetLeft
			curtop += obj.offsetTop
			obj = obj.offsetParent;
		}
	}
	return { x: curleft, y: curtop };
}

$.fn.findPos = function() {
	return findPos(this.get(0));
}

function funGetCloneNumber(pControl) {
	if ($(pControl).attr("clone") == undefined)
		return "";
	else
		return $(pControl).attr("clone");
}


function Validaciones(Objeto, Tipo) {
	/*
	::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	:: Verifica los caracteres capturados                                   
	   ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	*/
	var LongitudValor = Objeto.value.length + 1
	var SubCadena = String.fromCharCode(window.event.keyCode);	
	var Cadena = ""
	var LetrasMin = String.fromCharCode(225, 233, 237, 243, 250, 241);
	var LetrasMay = String.fromCharCode(193, 201, 205, 211, 218, 209);


	//alert(SubCadena);
	switch (Tipo) {
		case 0:  //Letras solo sin espacios
			var cadStr = 'AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz' + LetrasMay;
			break;
		case 1:  //Letras
			var cadStr = 'ABCDEFGHIJKLMNOPQRSTUVWXYZÁÉÍÓÚÜ-_/() ' + LetrasMay;
			break;
		case 2: //Números
			var cadStr = '0123456789'
			break;
		case 3:  //Letras y Números
			var cadStr = ' 0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ.-_ÁáÉéÍíÓóÚúÜü/()' + LetrasMay;
			break;
		case 4:  //Letras mayúsculas, minúsculas y Números
			var cadStr = ' @0123456789AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz.-_ÁáÉéÍíÓóÚúÜü/()%#' + LetrasMay + LetrasMin;
			break;
		case 5:  //HoraÐ
			var cadStr = '0123456789:'
			break;
		case 6:  //Calendario de carga
			var cadStr = '0123456789,-* '
			PintarCar209 = false;
			break;
		case 7: //Fechas
			var cadStr = '0123456789/'
			break;
		case 8: //Números telefonicos
			var cadStr = '0123456789-()EXT. '
			break;
		case 9: //Números Decimales
			var cadStr = '0123456789.'
			break;
		// Tipos de datos BEPM27FEB2004
		case 10: //Tipo Boolean ó Bit
			var cadStr = '01'
			break;
		case 11:  //Letras y Números sin espacio
			var cadStr = '0123456789AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz.-_ÁáÉéÍíÓóÚúÜü/()' + LetrasMay + LetrasMin;
			break;
		case 12:  //No editable
			var cadStr = '';
			break;
		case 13:  //Letras y Números sin espacio
			var cadStr = '0123456789AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz';
			break;
		case 14:  //Letras mayusculas y Números sin espacio
			var cadStr = '0123456789AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz' + LetrasMay;
			break;
		case 15:  //para mail
			var cadStr = '@0123456789AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz.-_' + LetrasMin;
			break;

	}

	if (LongitudValor > 0) {
		for (i = 1; i <= cadStr.length; i++) {
			//alert(cadStr.substring(i,i-1) + '   code=' + cadStr.charCodeAt(i-1) + '   ' + SubCadena + ' Subcadena=' + SubCadena.charCodeAt(0));
			if (cadStr.charCodeAt(i - 1) == SubCadena.charCodeAt(0)) {
				Cadena = cadStr.substring(i, i - 1);
				i = cadStr.length;
			}
		}

		if (Cadena.length == 0) {
			//Objeto.value=Objeto.value.substring(0, Objeto.value.length-1);
			window.event.keyCode = 0;
			//Objeto.focus();
		}

	}
}


function loadXML(urlSource){
	var xml = "";
	
	$.ajax({
		    type :"GET",
		    url : urlSource,
			async: false,
		    success : function(dataXML){
				 xml = $(dataXML);
			}
	});
	
	return xml;
}

const openHelper = function (e){
	
	let urlContext = document.location.pathname.split('/');
	
	let target = (e.target || e.srcElement);
	let idTarget = $(target).attr('id');
	
	let dialogWidth = ", width=400";
	let dialogHeight = ", height=400";

	var URL = document.location.protocol + "//" + (document.location.host == "" ? "localhost" : document.location.host) + "/" + urlContext[1] + "/Ayudas/";

	let xmlHelper = loadXML(URL + "xml-ayudas/" + idTarget + ".xml");
	
	if (xmlHelper) {
		dialogWidth = ",width=" + xmlHelper.find("Ayuda>anchoWin").text();
		dialogHeight = ", height=" + xmlHelper.find("Ayuda>altoWin").text();
	}
	else {
		alert("Error..!\rNo existe la definición de la ayuda \r\r" + idTarget);
		return;
	}
	
	xmlHelper = null;
	const wndLCD = window.open(URL + "dlg2.0.jsp?id=" + idTarget, idTarget, "directories = no, resizable = no, menubar = no, titlebar =no, status=yes, scrollbars = no " + dialogWidth + dialogHeight, false);
	wndLCD.focus();
}