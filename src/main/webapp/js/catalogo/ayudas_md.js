	var giTimer=0;
	var gclass;
	var szTableCols="";
		
	$(document).ready(
		function() 
		{				
			$("input.txtAyuda").keyup(
				function(e)
				{
					// elimina los caracteres no deseados
					var keyCode = e.keyCode 							
					if (keyCode == 144 || keyCode == 91 || keyCode == 45 || keyCode == 44 || keyCode == 40 || keyCode == 39 || keyCode == 38 || keyCode == 37 || keyCode == 36 || keyCode == 35 || keyCode == 34 || keyCode == 33 || keyCode == 20 || keyCode == 19 || keyCode == 18 || keyCode == 17 || keyCode == 16 || keyCode == 13 || keyCode == 9) return;
					//alert(keyCode);
					
					window.clearInterval(giTimer); alert("antes");
					
					var szTemp = $(this).attr("tabla");	
					var nClone = funGetCloneNumber($(this));
					var szTabla;
					
					$("#lb" + szTemp + "Desc" + nClone).css("backgroundColor",document.bgColor);
				
					// limpia los controles de clase txtAyuda con dependencias de este control													
					$("input.txtAyuda").each(function(i) {
						if ($(this).attr("dependencias").indexOf(szTemp + nClone)>= 0 )
						{									
							var nClone2 = funGetCloneNumber($(this));
							szTabla = $(this).attr("tabla");
							$("#txt" + szTabla + nClone2).val("");
							$("#lb" + szTabla + "Desc" + nClone2).val("seleccionar...");
							$("#lb" + szTabla + "Desc" + nClone2).css("backgroundColor",document.bgColor);
						}												
					});					
					$("input.txtAyudaDesc").each(function(i) {
						if ($(this).attr("dependencias").indexOf(szTemp + nClone)>= 0 )
						{									
							$(this).val("");
						}												
					});					
												
					// si modifican un text elimina cualquier cosa del grid de consulta
					if ($("#divGrid").attr('toggle') == "visible")
					{
						$("#divGrid").fadeOut("slow");
						$("#divGrid").attr('toggle',"hidden");
					}							
					$("#divGrid").attr('origen',"");							
					
					// limpia el label de este control si texto vacio
					szTabla = $(this).attr("tabla");
					if ($(this).val() == "")
					{							
						$("#lb" + szTabla + "Desc" + nClone).val("seleccionar...");
						return;
					}
					$("#lb" + szTabla + "Desc" + nClone).val("capturando clave...");
					
					giTimer=window.setInterval("subTimerKeyUp(\"txt" + szTabla + nClone + "\")",500);
					
				}
			); // fin de txtAyuda keyup
			
			$('body')		// eventos del body
				.mouseover(
					function(e) 
					{ 
						try
						{
							var objElemento = (e.target || e.srcElement);
				
							switch (objElemento.className)
							{
							case 'tdDetalle':
								gclass = objElemento.parentNode.className;
								objElemento.parentNode.className = "hover"
								break;
							case 'btnGrid':
								$(objElemento).css('top',parseInt($(objElemento).css('top')) -1);
								$(objElemento).css('left',parseInt($(objElemento).css('left')) -1);
								$(objElemento).css('border',"gray 1px solid");
								break;
							}	
						}
						catch(e)
						{
							//alert(e.description);
						}
					})
				.mouseout(
					function(e) 
					{
						try
						{
							var objElemento = (e.target || e.srcElement);
							
							switch (objElemento.className)
							{
							case 'tdDetalle':
								objElemento.parentNode.className = gclass;
								break;
							case 'btnGrid':
								$(objElemento).css('top',parseInt($(objElemento).css('top')) +1);
								$(objElemento).css('left',parseInt($(objElemento).css('left')) +1);
								$(objElemento).css('border',"");
								break;
							}
						}
						catch(e)
						{
							//alert( e.description);
						}
					}
				)
				.click(
					function(e) 
					{
						var objElemento = (e.target || e.srcElement); 
						
						if (objElemento.className=="btnGrid")			// s boton de filtrar del grid
						{
							subLlenaGridAyuda();
						}
						
						if (objElemento.className=="tdDetalle")			// s seleccionaron un renglon del grid
						{
							var szTemp = $("#divGrid").attr('origen');
							var szTabla;
							// limpia los controles de clase txtAyuda con dependencias de este control	
							var arrDependencias = $("#txt"+szTemp).attr("dependencias").split(",");
											 							
							var szTR = objElemento.parentNode.innerHTML;
							var szTD;
							$("input.txtGridCol").each(function(i) {
								i++;
								szTD = szTR.substr(0,szTR.indexOf(($.browser.mozilla?"</td>":"</TD>")));
								szTD = szTD.substr(szTD.indexOf(">")+1);
								szTR = szTR.substr(szTR.indexOf(($.browser.mozilla?"</td>":"</TD>"))+5);
								if ($(this).attr("id") != "txtGridCol" + i )
								{
									szTemp = $(this).attr("id").substr(7);
									if ($("#txt" + szTemp).val()!=szTD)
									{
										$("input.txtAyuda").each(function(j) {
											if ($(this).attr("dependencias").indexOf(szTemp)>= 0 )
											{
												$(this).val(""); 
												$("#lb" + $(this).attr("tabla") + "Desc").val("seleccionar...");
												$("#lb" + $(this).attr("tabla") + "Desc").css("backgroundColor",document.bgColor);
											}
										});
										
										$("#txt" + szTemp).val(szTD); 
									
										var szValor = funValidaDependencias("#txt" + szTemp);
										if (szValor == undefined)
										{
											return;
										}	
										if (szValor == "MAL")
										{
											return;
										}																
										
										if (szValor != "") 
											szValor += " AND ";
										 
										
										var arrVsControl = $("#txt" + szTemp).attr("tablavscontrol").split(',');
										var arrTipos     = $("#txt" + szTemp).attr("tablaTipos").split(',');
										var szTipo = "";

										szValor += $("#txt"+szTemp).attr("campo") + " = ";
										szTipo = "";
										for (var i=0; i<arrVsControl.length; i++)
										{
											if (arrVsControl[i]==szTemp)
											{
												szTipo = arrTipos[i];
												break
											}
										}
										if (szTipo == "numero")
										{
											szValor += $("#txt" + szTemp).val();
										}
										if (szTipo == "string")
										{
											szValor += "'" + $("#txt" + szTemp).val() + "'";
										}
										
										subWSAyudaTraeDesc(szTemp,szValor);
									}
								}												
							});
								
							// elimina cualquier condicion del grid de consulta
							$("#divGrid").fadeOut("slow");
							$("#divGrid").attr('toggle',"hidden");
							$("#divGrid").attr('origen',"");
						}
						
						if (objElemento.className=="btnAyuda")		// s oprimieron el boton de mostrar grid
						{
							var szTabla = $(objElemento).attr('id');
							szTabla = szTabla.substr(3);
							
							// posicionamos el grid			
							iArrPos = $("#txt"+szTabla).findPos(); 
							$("#divGrid").css('left',iArrPos.x);
							$("#divGrid").css('top',iArrPos.y + 22);
														
							if ($("#divGrid").attr('origen') != szTabla)
							{
								$("#divGrid").attr('origen',szTabla);
								$("#divGrid").attr('toggle',"hidden");
								subGeneraInfoGrid();
							}
							if ($("#divGrid").attr('toggle') == "hidden")
							{
								$("#divGrid").attr('toggle',"visible");																
								$("#divGrid").fadeIn("slow"); 
								//$("#lbGridTitulo").attr("innerHTML","Consultando " + $("#lb"+szTabla).attr("innerHTML"));
							}
							else
							{
								$("#divGrid").fadeOut("slow");
								$("#divGrid").attr('toggle',"hidden");
							}					
						}
						if (objElemento.className=="btnClose")		// s oprimieron el boton de cerrar grid
						{
							$("#divGrid").fadeOut("slow");
							$("#divGrid").attr('toggle',"hidden");
						}
					}
				)
				.keyup(
					function(e) 
					{
						var txtTyping = (e.target || e.srcElement);
						if (txtTyping.className=="txtGridCol")
						{
							txtTyping.title="";
						}
					}
				); // fin del body
			
	}); // fin de ready		
			
	function subTimerKeyUp(pControl)
	{
		window.clearInterval(giTimer); alert("hello");
		// busca las dependencias de este control
		var szValor = funValidaDependencias("#"+pControl);	
		if (szValor == "MAL")
		{
			return;
		}													
		
		if (szValor != "") 
			szValor += " AND "; 
			
		var arrVsControl = $("#"+pControl).attr("tablavscontrol").split(',');
		var arrTipos     = $("#"+pControl).attr("tablaTipos").split(',');
		var szTipo = "";
		var szTabla = $("#"+pControl).attr("tabla");
		var i=0;

		szValor += $("#"+pControl).attr("campo") + " = "; 
		szTipo = "";
		for (i=0; i<arrVsControl.length; i++)
		{
			if (arrVsControl[i]==szTabla)
			{
				szTipo = arrTipos[i];
				break;
			}
		}
		if (szTipo == "numero")
		{
			szValor += $("#"+pControl).val();
		}
		if (szTipo == "string")
		{
			szValor += "'" + $("#"+pControl).val() + "'";
		}
		subWSAyudaTraeDesc(szTabla ,szValor);
				
		for (i=0; i<arrVsControl.length; i++)
		{
			if ($("#txt" + arrVsControl[i]).attr("className") == "txtAyudaDesc")
			{
				// busca las dependencias de txt .. Desc
				szValor = funValidaDependencias("#txt" + arrVsControl[i]);	
				if (szValor == "MAL")
				{
					return;
				}
				
				subWSAyudaTraeTxTDesc($("#txt" + arrVsControl[i]).attr("tabla") ,szValor);
				}
		}	
	}
			
	// trae la descripcin de una clave
	function subWSAyudaTraeDesc(pTabla,pValor)
	{	
		
		$("#lb" + pTabla + "Desc").val("Buscando...");

		$.ajax
		({
			type: "POST",
			//url: document.location.protocol + "//" + (document.location.host==""?"localhost":document.location.host) + "/xxvisionwebser/wsalarmas.asmx/GetAyudaInfo",	// llamada al web service de traer la descripcin
			url: document.location.protocol + "//" + (document.location.host==""?"localhost":document.location.host) + "/gestion/services/Catalogo",	// llamada al web service de traer la descripcin
			dataType: "html",
			data: "pTabla=" + pTabla + "&pValor=" + pValor,
			error: 
				function(p1,p2,p3)
				{
					$("#lb" + pTabla + "Desc").val("Error...");
					$("#lb" + pTabla + "Desc").css("backgroundColor","tomato");
				},
			success: 
				function(responseText)
				{	
					szTemp = responseText.funCambiaLlaves();
					var szTemp = szTemp.substr(szTemp.indexOf("<descripcion>")+13);
					szTemp = szTemp.substr(0,szTemp.indexOf("</descripcion>"));
										
					$("#lb" + pTabla + "Desc").val(szTemp);
					if (szTemp.indexOf("Error") >= 0)
					{
						$("#lb" + pTabla + "Desc").css("backgroundColor","tomato");
					}
				}
		});
	}
	
		// trae la descripcin de una clave
	function subWSAyudaTraeTxTDesc(pTabla,pValor)
	{	
		
		$("#txt" + pTabla ).val("Buscando...");

		$.ajax
		({
			type: "POST",
			//url: document.location.protocol + "//" + (document.location.host==""?"localhost":document.location.host) + "/xxvisionwebser/wsalarmas.asmx/GetAyudaInfo",	// llamada al web service de traer la descripcin
			url: document.location.protocol + "//" + (document.location.host==""?"localhost":document.location.host) + "/gestion/services/Catalogo",	// llamada al web service de traer la descripcin
			dataType: "html",
			data: "pTabla=" + pTabla + "&pValor=" + pValor,
			error: 
				function(p1,p2,p3)
				{
					$("#txt" + pTabla ).val("Error...");
				},
			success: 
				function(responseText)
				{	
					szTemp = responseText.funCambiaLlaves();
					var szTemp = szTemp.substr(szTemp.indexOf("<descripcion>")+13);
					szTemp = szTemp.substr(0,szTemp.indexOf("</descripcion>"));
										
					$("#txt" + pTabla ).val(szTemp);

				}
		});
	}
	
	function funValidaDependencias(pControl)
	{
		// busca las dependencias de este control
		if ($(pControl).attr("tablavscontrol")==undefined) return $(pControl).attr("tablavscontrol");
		var arrVsControl = $(pControl).attr("tablavscontrol").split(',');
		var arrTipos     = $(pControl).attr("tablaTipos").split(',');

		var szValor = "";
		var szTipo = "";							
		var szTemp = $(pControl).attr("dependencias");
		szTemp = $.trim(szTemp); 		
		if (szTemp != "ninguna")
		{					
			szTemp += ","; 
			do 
			{
				szTabla = szTemp.substr(0,szTemp.indexOf(","))
				szVal = $("#txt"+szTabla).val(); 
				if (szVal != null)
				{
					if ($.trim(szVal) == "")
					{
						alert('Error...!\n\n\"' + $(pControl).attr("tabla") + '\" depende de \"' + szTabla + '\"\nseleccione primero un valor de \"' + szTabla + '\".');
						$(pControl).val("");
						$("#txt"+szTabla).focus();
						return "MAL";
					}
					if (szValor != "") 
						szValor += " AND ";
					
					szValor += $("#txt"+szTabla).attr("campo") + " = ";
					
					szTipo = "";
					for (var i=0; i<arrVsControl.length; i++)
					{
						if (arrVsControl[i]==szTabla)
						{
							szTipo = arrTipos[i];
							break
						}
					}
					if (szTipo == "numero")
					{
						szValor += $("#txt"+szTabla).val();
					}
					if (szTipo == "string")
					{
						szValor += "'" + $("#txt"+szTabla).val() + "'";
					}
				}
				szTemp = szTemp.substr(szTemp.indexOf(",")+1);
			}while (szTemp.indexOf(",")>=0)
		}
		else
			szValor = "";
		return szValor;
	}
	
	// construye el where del filtro
	function funGeneraWhere()
	{
		var szWhere="";
		$("input.txtGridCol").each(function(i) {	
			i++;
			var szString = $(this).val();
			szString = $.trim(szString);
			if (szString != "")
			{
				if (szWhere.length > 0)
				{
					szWhere += " AND ";
				}
				szWhere += $(this).attr("campo");
				if ($(this).attr("tipo") == "numero")
				{
					szWhere += "=" + $(this).val();	
				}
				else
				{
					szString = $(this).val();
					if (szString.indexOf("%") >= 0)
					{
						szWhere += " LIKE "
					}
					else
					{
						szWhere += "="
					}
					szWhere += "'" + szString + "'";
				}											
			}																	 
		}); 
		
		if (szWhere.length == 0)
		{
			szWhere= "TODO"
		}
		return szWhere;
	}
	
	// subrutina que genera el contenido del Grid
	function subGeneraInfoGrid()
	{
		var i;
		var szTemp="";
		var szTabla = $("#divGrid").attr('origen');
		var szResult="";
		var arrCampos    = $("#txt" + szTabla).attr("tablacampos").split(',');
		var arrVsControl = $("#txt" + szTabla).attr("tablavscontrol").split(',');
		var arrHeaders   = $("#txt" + szTabla).attr("tablaHeaders").split(',');
		var arrAnchos    = $("#txt" + szTabla).attr("tablaAnchos").split(',');
		var arrTipos     = $("#txt" + szTabla).attr("tablaTipos").split(',');
		
		$("#divTXTyTables").attr('innerHTML',"");
		$("#lbGridTitulo").attr("innerHTML","Buscando ... ");
		$("#divGrid").width(200);
		$("#btnClose").css('left',200-10);
		$("#divGridToolbar").width(200);
		szTableCols = "";
		
		for (i=0; i<arrCampos.length; i++)
		{
			szResult += "<input id=txtGrid";
			if (arrVsControl[i]=="") 
			{
				szResult += "Col" + (i+1);
			}
			else
			{
				szResult += arrVsControl[i];  	
				if ($("#txt" + arrVsControl[i]).attr("className") == "txtAyuda")
				{
					if (arrVsControl[i] != szTabla)
						szResult += " style='BACKGROUND-COLOR: #eeeeee' "
				}
				if ($("#txt" + arrVsControl[i]).attr("className") == "txtAyudaDesc")
				{
					if ($("#txt" + arrVsControl[i]).attr("dependencias").indexOf(szTabla)==-1)
						szResult += " style='BACKGROUND-COLOR: #eeeeee' "
				}
			}
			szResult += " name=txtGridCol" + (i+1) + " class=txtGridCol campo=" + arrCampos[i] + " tipo=" + arrTipos[i]
			szResult += " type=text maxLength=" + arrAnchos[i] + " size=" + arrAnchos[i] + ">"
			szTableCols += "<th id='thCol" + (i+1) + "' class='ColConsulta'>" + arrHeaders[i] + "</th>\r\n"
		}
		
        szResult += "<div id=divTableContainer class=divTableContainer style='OVERFLOW: auto; HEIGHT: 288px; BACKGROUND-COLOR: white'></div>"
		
		$("#divTXTyTables").attr('innerHTML',szResult);
		
		$("input.txtGridCol").each(function(i) {
			i++;
			if ($(this).attr("id") != "txtGridCol" + i )
			{
				szTemp = $(this).attr("id").substr(7);													
				if (szTabla != szTemp)
				{
					if ($("#txt" + szTemp).attr("className") == "txtAyuda")
					{
						$(this).val($("#txt" + szTemp).val());
						if ($("#lb" + szTemp + "Desc").val()!= "seleccionar...")
						{
							$(this).attr("title",$("#lb" + szTemp + "Desc" ).val());
						}
					}
					if ($("#txt" + szTemp).attr("className") == "txtAyudaDesc")
					{
						if ($("#txt" + szTemp).attr("dependencias").indexOf(szTabla)==-1)
						{
							$(this).val($("#txt" + szTemp).val());
						}
					}
				}
			}												
		});
		
		subLlenaGridAyuda();	
									
	}
	
	function subLlenaGridAyuda()
	{					
		var szTabla = $("#divGrid").attr('origen');		
		szTabla = $("#txt" + szTabla).attr("tabla");
		$.ajax
		({
			type: "POST",
			//url: document.location.protocol + "//" + (document.location.host==""?"localhost":document.location.host) + "/xxvisionwebser/wsalarmas.asmx/GetAyudaDatosGrid", // llamada al web service de datos tabla
			url: document.location.protocol + "//" + (document.location.host==""?"localhost":document.location.host) + "/gestion/services/Catalogo",	// llamada al web service de traer la descripcin
			dataType: "html",
			data: "pTabla=" + szTabla + "&pCondicion=" + funGeneraWhere(),
			error: 
				function(p1,p2,p3)
				{
					//alert("Error\n" + p3.description);
					$("#lb" + szTabla + "Desc").val("Error...");
					$("#lb" + szTabla + "Desc").css("backgroundColor","tomato");
				},
			success: 
				function(responseText)
				{	
					var szResponse = responseText.funCambiaLlaves();
					
					szResponse = szResponse.substr(szResponse.indexOf("<table"));
					szResponse = szResponse.substr(0,szResponse.indexOf("</string>"));
					
					szResponse = szResponse.replace("<ENCABEZADO>",szTableCols);
					
					$("#divTableContainer").attr('innerHTML',szResponse);
							
					var iAncho=0;
					var iCuantos=0;
					$("th.ColConsulta").each(function(i) {
						i++;
						iCuantos++;
						objss = $("input[@name=txtGridCol" + i + "]");						
						$(this).width($(objss).width()-20 - ($.browser.mozilla?2:0));  // 22= +padding + bordes
						iAncho += $(objss).width();										 
					});											
					
					iAncho = iAncho + (6*iCuantos) + 16 -($.browser.mozilla>0?5:0);
					
					$("#divTXTyTables").width(iAncho);
alert($("#divTXTyTables").attr('innerHTML'));
					if (iAncho > 800) iAncho = 800;
					if ($.browser.msie) $("#fixedHeader").addClass("fixedHeader");
					
					$("#divGrid").width(iAncho);	 
					
					$("#btnClose").css('left',iAncho-10);
					$("#divGridToolbar").width(iAncho);
					//$("#divTableContainer").width(iAncho);
					
					$("#tblDetalle").tableSorter({
						sortColumn: 0,						// nmero entero del ndice o nombre de la columna en minsculas
						sortClassAsc: 'headerSortUp',		// clase css de la cabecera de la columna cuando aplicamos un orden ascendente
						sortClassDesc: 'headerSortDown',	// clase css de la cabecera de la columna cuando aplicamos un orden descendente
						headerClass: 'header',				// clase genrica de las cabeceras (th's)
						stripingRowClass: ['even','odd'],	// Estilos css para las lneas pares y para las impares.
						stripeRowsOnStartUp: true			// Dibujamos las lneas de las tablas con distintos css indicados en strinpingRowClass.

					});	
					$("#lbGridTitulo").attr("innerHTML","Consultando " + $("#lb"+szTabla).attr("innerHTML"));
					$("#txtGrid"+szTabla).focus();								
				}
		});
	}
	
	// Creacin del grid de ayuda dinamicamente
	$.subCreaAyudaGrid = function()
	{
		var objDiv;
		var objDiv2;
		var objDiv3;
		var objTemp;
		
		objDiv = document.createElement('div'); //("<div id='divGrid' class='divGrid' style='overflow:auto; LEFT:00px; TOP:0px; WIDTH: 0px; HEIGHT: 385px' origen='' toggle='hidden' >");
		objDiv.id = 'divGrid';
		objDiv.className = 'divGrid';
		objDiv.style.overflow = 'auto'; objDiv.style.left = '0px'; objDiv.style.top = '0px'; objDiv.style.width = '0px'; objDiv.style.height = '385px';
		objDiv.setAttribute('origen','')
		objDiv.setAttribute('toggle','hidden')
		document.body.appendChild(objDiv);				
		
		$(objDiv).append("<img id='btnClose' name='btnClose' class='btnClose' src='Imagenes/pbClose.bmp' title=' Cerrar' style='POSITION: relative; LEFT: 110px; TOP: 0px'>");
		
		$(objDiv).append("<label id='lbGridTitulo' class='lbGridTitulo'>");
		
		$(objDiv).append("<br>");
		$(objDiv).append("<br>");
		
		$(objDiv).append("<div id='divGridToolbar' style='POSITION: relative; LEFT: 0px; TOP: 0px; WIDTH: 100%; HEIGHT: 27px; BACKGROUND-COLOR: #dddddd'>");
		
		$("#divGridToolbar").append("<img id='btnGridFiltrar' name='btnGridFiltrar' class='btnGrid' src='Imagenes/boton-filter.jpg' title='Buscar registros que cumplan la condicin' style='POSITION: relative; LEFT: 5px; TOP: 5px'>");
		
		$("#divGridToolbar").append("<div id='divConte' style='overflow:auto; POSITION: relative; HEIGHT: 331px; TOP: 8px'>");
		
		$("#divConte").append("<div id='divTXTyTables' style='OVERFLOW: auto; HEIGHT: 314px;'>");
				
		$("#divGrid").hide();
	};
	
		//clona los elementos de ayudas por control
	$.subClonaAyudaElementos = function(pTabla) 
	{
		var szTabla="";
		var nIndex = $("input.txtAyuda").length;
		var iCuantos=0;
		
		$("input.txtAyuda").each(function(i) {
			if ($(this).attr("id") == "txt"+pTabla) 
			{
				if (iCuantos > 0)
				{
					szTabla = $(this).attr("tabla"); 
						
					$(this).attr("clone", nIndex);					// asinamos nmero de clone
					$(this).attr("id", "txt" + szTabla + nIndex);	// reid el control
					$(this).attr("name", "txt" + szTabla + nIndex);	// rename el control
					$(this).addClass("txtAyuda");
					var arrVsControl = $("#txt" + szTabla).attr("tablavscontrol").split(',');
					var szTemp=""; 
					for (var k=0; k<arrVsControl.length; k++)
					{
						szTemp += (k>0?",":"");  
						if (arrVsControl[k]==szTabla)
							szTemp = szTabla + nIndex;
						else
							szTemp += arrVsControl[k];	
					}
					$(this).attr("tablavscontrol",szTemp); 
					var arrDependencias = $("#txt"+szTabla).attr("dependencias").split(",");
					szTemp="";
					for (var k=0; k<arrDependencias.length; k++)
					{
						szTemp += (k>0?",":"");
						if (arrDependencias[k]==szTabla)
							szTemp = szTabla + nIndex;
						else
							szTemp += arrDependencias[k];					
					}
					$(this).attr("dependencias",szTemp);
				}
				iCuantos++;
			}
			//alert($(this).attr("id") + "   " + $(this).attr("clone"));
		});	
		
		if (szTabla != "")
		{
			$("input.btnAyuda").each(function(j) {			// hacemos lo mismo para la clase btnAyuda	
				if ($(this).attr("id") == "btn"+szTabla) 
				{
					if (j>0)
					{
						if ($(this).attr("clone")==undefined)
						{
							$(this).attr("clone", nIndex);					// asinamos nmero de clone
							$(this).attr("id", "btn" + szTabla + nIndex);	// reid el control
							$(this).attr("name", "btn" + szTabla + nIndex);	// rename el control
						}
					}
				}
				//alert($(this).attr("id") + "   " + $(this).attr("clone"));
			});		
			
			$("input.lbAyudaDesc").each(function(k) {			// hacemos lo mismo para la clase lbAyudaDesc	
				if ($(this).attr("id") == "lb" + szTabla + "Desc") 
				{
					if (k>0)
					{
						if ($(this).attr("clone")==undefined)
						{
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


	
	
	//Agrega los elementos de ayudas por control
	$.subAddAyudaElementos = function(pControl) 
	{
		var szTabla = $(pControl).attr("tabla");
		
		//agregamos botn y etiqueta de consulta
		$(pControl).after("<input id='btn" + szTabla + "' name='btn" + szTabla + "' type='button' class='btnAyuda' value='...' title='Consultar' tabindex=-1 hidefocus>");
		//$("#btn" + szTabla).after("&nbsp;<input id='lb" + szTabla + "Desc' name='lb" + szTabla + "Desc' class='lbAyudaDesc' value='seleccionar...' disabled>");
	};

	// procedimiento que inicializa los controles de ayuda en linea	
	$.fn.subInicia = function() 
	{
		$.subCreaAyudaGrid();
				
		this.each(
			function() 
			{
				$.subAddAyudaElementos(this);
			}
		);
		
		$("input.txtAyuda").each(function(i) {
			if ($.browser.msie)
			{									
				$(this).css("top","4");
				$("#btn" + $(this).attr("tabla")).css("top","3");
				$("#btn" + $(this).attr("tabla")).css("height","22");
				$("#lb" + $(this).attr("tabla") + "Desc").css("top","4");
			}			
			$("#lb" + $(this).attr("tabla") + "Desc").css("backgroundColor",document.bgColor);									
		});	
		
		$.subAtributos();
	}

/*	// funciones Trim
	String.prototype.funTrim = function() {return this.replace(/^\s+|\s+$/g,"");}
	String.prototype.funLTrim = function() {return this.replace(/^\s+/g,"");}
	String.prototype.funRTrim = function() {return this.replace(/\s+$/g,"");} 
*/	
	// funcin que cambia las llaves a & < > de &lt; y &gt; del regreso de los web services
	String.prototype.funCambiaLlaves = function () {return this.replace(/&amp;/g, "&").replace(/&lt;/g,"<").replace(/&gt;/g, ">");}
	
	// funcin que regresa la posicin real de un control
	function findPos(obj) 
	{ 
		var curleft = 0;
		var curtop = 0;
		if (obj.offsetParent)
		{
			while (obj.offsetParent)
			{
				curleft += obj.offsetLeft
				curtop += obj.offsetTop   
				obj = obj.offsetParent;
			}
		}
		return { x: curleft, y: curtop };
	}

	$.fn.findPos = function() 
	{
	return findPos(this.get(0));
	} 

	function funGetCloneNumber(pControl)
	{
		if ($(pControl).attr("clone")==undefined)
			return "";
		else
			return $(pControl).attr("clone");	
	}


