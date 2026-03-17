	var szURL_AC="";
	var giTimer_AC=0;
	var gclass_AC;
	var szTableCols_AC="";
	var szIDCtrl ="";
	
	var arrCtrls = new Array();
		
	var bTieneControl = false;
	$(document).ready(
		function() 
		{				
			$("input.autoCompletaSyC").keyup(
				function(e)
				{
					// elimina los caracteres no deseados
					var keyCode = e.keyCode; 							
					if (keyCode == 144 || keyCode == 91 || keyCode == 45 || keyCode == 44 || keyCode == 40 || keyCode == 39 || keyCode == 38 || keyCode == 37 || keyCode == 36 || keyCode == 35 || keyCode == 34 || keyCode == 33 || keyCode == 20 || keyCode == 19 || keyCode == 18 || keyCode == 17 || keyCode == 16 || keyCode == 13 || keyCode == 9) return;
					//alert(keyCode);
					
					window.clearInterval(giTimer_AC); 
					
					szIDCtrl = $(this).attr("id");
					
					$("#divAC" + szIDCtrl ).attr('origen',szIDCtrl);
					
					var pControl = funGetObjAutoComplete(szIDCtrl);
					
					var arrColumnas = pControl.getArrCols();
					
					for (var i = 0; i < arrColumnas.length; i=i+1)						
					{	
						if ($(this).attr("id") == arrColumnas[i].getAttribute("control"))
						{
							str1 = arrColumnas[i].getAttribute("ctrlsDependientes").split(",");
							for (var j = 0; j < str1.length; j=j+1)
							{
								$("#"+str1[j]).val("");
							}
							break;
						}
					}
					
				
					giTimer_AC=window.setInterval("subTimerKeyUp_AC()",500);
					
				}
			); // fin de autoCompleta keyup
			
			
			$('body')		// eventos del body
				.mouseover(
					function(e) 
					{ 
						try
						{
							var objElemento = (e.target || e.srcElement);
				
							switch (objElemento.className)
							{
							case 'tdDetalle_AC':
								gclass_AC = objElemento.parentNode.className;
								objElemento.parentNode.className = "hover"
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
							case 'tdDetalle_AC':
								objElemento.parentNode.className = gclass_AC;
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
						
						if (objElemento.className=="tdDetalle_AC")			// s seleccionaron un renglon del grid
						{			
							var k=0;
							var obj = objElemento;			
							while (obj.offsetParent)
							{
								obj = obj.offsetParent;
								if (obj.tagName == "DIV") k=k+1;
								if (k==2) break;
							}
							szIDCtrl = obj.origen
						
							var pControl = funGetObjAutoComplete(szIDCtrl);

							var arrColumnas = pControl.getArrCols();
							
							var szTR = objElemento.parentNode.innerHTML;
							var szTD;
							
							for (var i = 0; i < arrColumnas.length; i=i+1)						
							{		
								try
								{	
									szControl = arrColumnas[i].getAttribute("control");
									szTD = szTR.substr(0,szTR.indexOf(($.browser.mozilla?"</td>":"</TD>")));
									szTD = szTD.substr(szTD.indexOf(">")+1);
									szTR = szTR.substr(szTR.indexOf(($.browser.mozilla?"</td>":"</TD>"))+5);
									if (szControl != "")
									{
										if (szTD == "&nbsp;") szTD = ""; 
										$("#"+szControl).val(szTD);
									}
								}
								catch (ex)
								{
								}
									
							}	
							
							$("#"+$("#divAC" + szIDCtrl).attr('origen')).focus();
							$("#divAC" + szIDCtrl).fadeOut("slow"); 
							$("#divAC" + szIDCtrl).attr('origen',"");
						}

						if (objElemento.className=="btnCloseAC")		// s oprimieron el boton de cerrar grid
						{
							$("#divAC" + szIDCtrl).fadeOut("slow");
						}

					}
				)

				; // fin del body
			
	}); // fin de ready		
			
	function subTimerKeyUp_AC()
	{		
		window.clearInterval(giTimer_AC);    
		
		$("#divAC" + szIDCtrl).width(125);	
		$("#divAC" + szIDCtrl).height(47);
		$("#divTablaAC" + szIDCtrl).height($("#divAC" + szIDCtrl).height()-10);
		$("#imgClose" + szIDCtrl).css('left',$("#divAC" + szIDCtrl).width()-10);
		$("#divTablaAC" + szIDCtrl).attr('innerHTML',"Buscando...");
		
/*		
		$("div.calendarbutton").each(function(i) 
		{  
			//alert($(this).css('top')); 
			$(this).css('top',parseInt($(this).css('top'))+$("#divAC" + szIDCtrl).height());
			
		});
		
			// Set the style and position:
	var nTop               = getObject.getSize("offsetTop", input);
	var nLeft              = getObject.getSize("offsetLeft", input);
	calButton.className    = "calendarbutton";
	calButton.style.zIndex = 10000;
	calButton.style.cursor = "pointer";
	calButton.style.top    = (nTop + Math.floor((input.offsetHeight-calButton.offsetHeight)/2) + this.buttonOffsetY) + "px";
	var btnOffX            = Math.floor((input.offsetHeight - calButton.offsetHeight) / 2);
	if (this.buttonPosition == "in"){
		calButton.style.left = (nLeft + input.offsetWidth - calButton.offsetWidth - btnOffX + this.buttonOffsetX) + "px";
	}
	else{ // "out"
		calButton.style.left = (nLeft + input.offsetWidth + btnOffX + this.buttonOffsetX) + "px";
	}
*/		
		
		subLlenaGridAyuda_AC();	
		
		return;
		
		// busca las dependencias de este control
		var szValor = subGeneraInfoGrid_AC(pControl);	
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
		for (i=0; i<arrVsControl.length; i=i+1)
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
		subTraeDesc_AC(szTabla ,szValor);
				
		for (i=0; i<arrVsControl.length; i=i+1)
		{
			if ($("#txt" + arrVsControl[i]).attr("className") == "txtAyudaDesc")
			{
				// busca las dependencias de txt .. Desc
				szValor = subGeneraInfoGrid_AC("#txt" + arrVsControl[i]);	
				if (szValor == "MAL")
				{
					return;
				}
				
				subWSAyudaTraeTxTDesc_AC($("#txt" + arrVsControl[i]).attr("tabla") ,szValor);
				}
		}	
	}
			
	function subTraeDesc_AC(pTabla,pValor)
	{	
		
		$("#lb" + pTabla + "Desc").val("Buscando...");

		$.ajax
		({
			type: "POST",
			url: document.location.protocol + "//" + (document.location.host==""?"localhost":document.location.host) + "/xxvisionwebser/wsalarmas.asmx/GetAyudaInfo",	// llamada al web service de traer la descripcin
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
					szTemp = responseText.funCambiaChars();
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
	function subWSAyudaTraeTxTDesc_AC(pTabla,pValor)
	{	
		
		$("#txt" + pTabla ).val("Buscando...");

		$.ajax
		({
			type: "POST",
			url: document.location.protocol + "//" + (document.location.host==""?"localhost":document.location.host) + "/xxvisionwebser/wsalarmas.asmx/GetAyudaInfo",	// llamada al web service de traer la descripcin
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
					szTemp = responseText.funCambiaChars();
					var szTemp = szTemp.substr(szTemp.indexOf("<descripcion>")+13);
					szTemp = szTemp.substr(0,szTemp.indexOf("</descripcion>"));
										
					$("#txt" + pTabla ).val(szTemp);

				}
		});
	}
	
	function funValidaDependencias_AC(pControl)
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
					for (var i=0; i<arrVsControl.length; i=i+1)
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
	function funGeneraWhere_AC()
	{
		var szWhere="";
		$("input.txtGridCol").each(function(i) {	
			i=i+1;
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
	function subGeneraInfoGrid_AC()
	{
		var i;
		var szTemp="";
		var szTabla = $("#divAC" + szIDCtrl).attr('origen');
		var szResult="";
		var arrCampos    = $("#txt" + szTabla).attr("tablacampos").split(',');
		var arrVsControl = $("#txt" + szTabla).attr("tablavscontrol").split(',');
		var arrHeaders   = $("#txt" + szTabla).attr("tablaHeaders").split(',');
		var arrAnchos    = $("#txt" + szTabla).attr("tablaAnchos").split(',');
		var arrTipos     = $("#txt" + szTabla).attr("tablaTipos").split(',');
		

		$("#divTablaAC" + szIDCtrl).attr("innerHTML","Buscando ... ");

		szTableCols_AC = "";
		
		for (i=0; i<arrCampos.length; i=i+1)
		{
			szResult += "<input id=txtGrid";
			if (arrVsControl[i]=="") 
			{
				szResult += "Col" + (i+1);
			}
			else
			{
				szResult += arrVsControl[i];  	
				if ($("#txt" + arrVsControl[i]).attr("className") == "autoCompletaSyC")
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
			szTableCols_AC += "<th id='thCol" + (i+1) + "' class='ColConsulta'>" + arrHeaders[i] + "</th>\r\n"
		}
		
        szResult = "<div id=divTableContainer class=divTableContainer style='OVERFLOW: auto; BACKGROUND-COLOR: white'></div>"
		
		$("#divTablaAC" + szIDCtrl).attr('innerHTML',szResult);
		
		subLlenaGridAyuda_AC();	
									
	}
	
	function subLlenaGridAyuda_AC()
	{					
		var pID = $("#divAC" + szIDCtrl).attr('origen');
		
		var pControl = funGetObjAutoComplete(pID);
		
		var szTabla = pControl.getTabla();
		var iMaxReg = pControl.getMaxReg();
		var arrColumnas = pControl.getArrCols();
		var szWhere = "";
		var szTemp;
		var szResponse="";
		var szTDOne="";
		
		for (var i = 0; i < arrColumnas.length; i++)
		{
			szTemp = $("#"+arrColumnas[i].getAttribute("control")).val();
			szTemp = $.trim(szTemp); 
			
			if (szTemp != "")
			{
				if (arrColumnas[i].getAttribute("tipo").toUpperCase()=="STRING")
					szWhere += arrColumnas[i].getAttribute("campo") + " like '" + szTemp + "%' AND ";
				else
					szWhere += " CAST(" + arrColumnas[i].getAttribute("campo") + " AS char(300)) like '" + szTemp + "%' AND ";
			}
		}
		szWhere = szWhere.substr(0,szWhere.length-5);
		
		$.getJSON(szURL_AC + "catalogos/SelectJson.jsp", {Tabla: szTabla, Param: szWhere, MaxReg: iMaxReg}, 
			function(datos)
			{
				szTableCols_AC = ""
				for (var i = 0; i < arrColumnas.length; i++)						
				{			
					szTableCols_AC += "<th nowrap=true id='thCol" + (i+1) + "' class='ColConsultaAC'>" + arrColumnas[i].getAttribute("header") + "</th>\r\n"
				}	
			
				szResponse += "<table id='tblDetalle_AC' class='tblAcompleta' width='100%' cellpadding='0' cellspacing='0'>\r";
        		szResponse += "<thead id='fixedHeader'>\r";
        		szResponse += "<tr>\r";
        		szResponse += szTableCols_AC;
        		szResponse += "</tr>\r";
        		szResponse += "</thead>\r";
        		szResponse += "<tbody id='tableBody'>\r"; 
        		szTemp = "";
        		
        		if (datos.length > 0)
				{				
					for (var i = 0; i < datos.length; i=i+1)					
					{				
						if (i >= iMaxReg) break;	
						szTemp += "<tr class='trRow " + (i%2==0?"odd":"even") + "' id='trRow" + i + "'>\r";						
						for (var m = 0; m < arrColumnas.length; m=m+1)						
						{			
							szTemp += "<TD nowrap=true class='tdDetalle_AC'>" 				
							szTemp += (eval("datos[" + i + "].Col" + m)== " "?"&nbsp;":eval("datos[" + i + "].Col" + m));	
							szTemp += "</TD>\r"	
							szTDOne += "<TD nowrap=true class='tdDetalle_AC'>" + (eval("datos[" + i + "].Col" + m)== " "?"&nbsp;":eval("datos[" + i + "].Col" + m)) + "</TD>\r"	;
						}	
						szTemp += "</tr>\r"				
					}				
				}      				
        				
				szResponse += szTemp;
	        	szResponse += "</tbody>\r";
	        	if (datos.length > iMaxReg) 
	        	{
	            	szResponse += "<tfoot><tr><td colspan=" + arrColumnas.length + ">* solo se muestran los primeros " + iMaxReg + " registros</td></tr></tfoot>";
	        	}
	        	if (datos.length == 0 )
	        	{
	            	szResponse += "<tfoot><tr><td colspan=" + arrColumnas.length + ">* no existen registros con ese filtro de búsqueda.</td></tr></tfoot>";
	        	}
	        	szResponse += "</table>";
	        	$("#divTablaAC" + szIDCtrl).attr('innerHTML',szResponse);
	        	
	        	$("#divAC" + szIDCtrl).height((datos.length+2)*22);
	        	
	        	if (datos.length > 10)
				{		
					$("#divAC" + szIDCtrl).height(232);	
				}
				if (datos.length == 0)
				{		
					$("#divAC" + szIDCtrl).height(88);	
				}
				
				$("#divAC" + szIDCtrl).width(600);	
				
				$("#divTablaAC" + szIDCtrl).height($("#divAC" + szIDCtrl).height()-10);
				$("#imgClose" + szIDCtrl).css('left',$("#divAC" + szIDCtrl).width()-10);
				
	        	if (datos.length == 1)
	        	{
	        		szTR = szTDOne;
					for (var i = 0; i < arrColumnas.length; i=i+1)						
					{		
						try
						{	
							szControl = arrColumnas[i].getAttribute("control");
							szTD = szTR.substr(0,szTR.indexOf(($.browser.mozilla?"</td>":"</TD>")));
							szTD = szTD.substr(szTD.indexOf(">")+1);
							szTR = szTR.substr(szTR.indexOf(($.browser.mozilla?"</td>":"</TD>"))+5);
							if (szControl != "")
							{
								if (szTD == "&nbsp;") szTD = ""; 
								$("#"+szControl).val(szTD); 
							}
						}
						catch (ex)
						{
						}	
					}	
					$("#"+$("#divAC" + szIDCtrl).attr('origen')).focus();
					$("#divAC" + szIDCtrl).fadeOut("slow"); 
					$("#divAC" + szIDCtrl).attr('origen',"");
					return;
	        	}
	        	
			}
		)
		
		// posicionamos el grid			
		iArrPos = $("#"+pID).whereXY(); 
		$("#divAC" + szIDCtrl).css('left',iArrPos.x);
		$("#divAC" + szIDCtrl).css('top',iArrPos.y + 22);
							
		$("#divAC" + szIDCtrl).fadeIn("slow"); 
	}

	// procedimiento que inicializa los controles de autoCompleta
	$.fn.subIniciaAutoCompleta = function() 
	{
		var arrContexto = document.location.pathname.split('/');
		szURL_AC = document.location.protocol + "//" + (document.location.host==""?"localhost":document.location.host) + "/" + arrContexto[1] + "/";

/*		var headID = document.getElementsByTagName("head")[0];         
		var cssNode = document.createElement('link');
		cssNode.type = 'text/css';
		cssNode.rel = 'stylesheet';
		cssNode.href = szURL_AC + 'Ayudas/css/autoCompleta.css';
		cssNode.media = 'screen';
		headID.appendChild(cssNode);
*/		

		
		//objDiv = document.createElement('div'); 
		//objDiv.id = 'divAutoCompleta';
		//objDiv.className = 'divAutoCompleta';
		//objDiv.style.overflow = 'auto'; objDiv.style.left = '0px'; objDiv.style.top = '0px'; 
		//objDiv.setAttribute('origen','')
		//objDiv.setAttribute('toggle','hidden')
		//document.body.appendChild(objDiv);				
		
		//$("#divAutoCompleta").append("<div id='divTXTyTablesAC' style='OVERFLOW: auto; WIDTH:100%; height:100%'>");
		//$("#divAutoCompleta").append("<img class='btnCloseAC' src='" + szURL_AC + "Ayudas/imagenes/pbClose.bmp' title=' Cerrar' style='Z-INDEX: 9000; POSITION: absolute; LEFT: 550px; TOP: 6px'; CURSOR: hand;>");
		//$("#divAutoCompleta").hide();
				
		var i=0;
		this.each(		//Agrega y busca los elementos de autocompleta		
			function() 
			{	
				$(this).before("<img id='imgTri" + $(this).attr("id") + "' src='" + szURL_AC + "Ayudas/imagenes/autocomplet.png' style='Z-INDEX: 9999; POSITION: relative; LEFT: 10px; TOP:-8px; CURSOR: hand;'  title='Este texto tiene Autocompletar.' hidefocus  >");	
				
				if ($(this).attr("className").indexOf("AyudaSyC")>=0)
				{
					$("#btn" + $(this).attr("id")).after("<div id='divAC" + $(this).attr("id") + "' class='divAutoCompleta' style='Z-INDEX: 10001; overflow:auto; LEFT:0px; TOP:0px;' origen='' toggle='hidden' >");
				}
				else
				{				
					$(this).after("<div id='divAC" + $(this).attr("id") + "' class='divAutoCompleta' style='Z-INDEX: 10001; overflow:auto; LEFT:0px; TOP:0px; ' origen='' toggle='hidden' >");
				}
				$("#divAC" + $(this).attr("id")).append("<img id='imgClose" + $(this).attr("id") + "' class='btnCloseAC' src='" + szURL_AC + "Ayudas/imagenes/pbClose.bmp' title=' Cerrar' style='POSITION: absolute; LEFT: 0px; TOP:0px; CURSOR: hand;'>");
				
				$("#divAC" + $(this).attr("id")).append("<div id='divTablaAC" + $(this).attr("id") + "' style='OVERFLOW: auto; POSITION: absolute; LEFT: 0px; TOP:10px; WIDTH:100%; height:100%'>");
				$("#divAC" + $(this).attr("id")).hide();			
				
				var URL = document.location.protocol + "//" + (document.location.host == "" ? "localhost" : document.location.host) + "/" + arrContexto[1] + "/Ayudas/xml-ayudas/";

				AyudaXML = loadXML(URL + idCampo + ".xml");
				//var AyudaXML = new ActiveXObject("Microsoft.XMLDOM");
				szID = $(this).attr("id"); 
				AyudaXML.async="false"; 	
				if (AyudaXML)
				{
					var szTabla = AyudaXML.find("Ayuda>tabla").text; 
					var iMaxReg = parseInt(AyudaXML.find("Ayuda>maxRegistros").text,10);
					var arrColumnas = AyudaXML.find("columna");
					var p = new CtrlsAutoComplete(szID, szTabla, iMaxReg, arrColumnas);
					arrCtrls[i] = p;
					i=i+1;
				}
				else
				{
					alert("Error..!\rNo existe la definición de la ayuda \r\r" + szID);
				}
				AyudaXML = null;
			
			}
		);
	}


	// funcin que cambia las llaves a & < > de &lt; y &gt; del regreso de los web services
	String.prototype.funCambiaChars = function () {return this.replace(/&amp;/g, "&").replace(/&lt;/g,"<").replace(/&gt;/g, ">");}
	
	// funcin que regresa la posicin real de un control
	function whereXY(obj) 
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
				if (obj.tagName == "DIV") break;
			}
		}
		return { x: curleft, y: curtop };
	}

	$.fn.whereXY = function() 
	{
		return whereXY(this.get(0));
	} 

	function funGetObjAutoComplete(pId)
	{
		for (var i=0; i<arrCtrls.length; i=i+1)
		{
			if (arrCtrls[i].getID()==pId) return arrCtrls[i];
		}
	}

	function CtrlsAutoComplete(pId, pTabla, pMaxRegs, pArrCols) 
	{
	    this._id = pId;
	    this._tabla = pTabla;
	    this._maxreg = pMaxRegs;
	    this._arrcols = pArrCols;
	}
	
	CtrlsAutoComplete.prototype._id;
	CtrlsAutoComplete.prototype._tabla;
	CtrlsAutoComplete.prototype._maxreg;
	CtrlsAutoComplete.prototype._arrcols;
	
	CtrlsAutoComplete.prototype.getID = function() 
		{
	    	return this._id;
		}
	CtrlsAutoComplete.prototype.getTabla = function() 
		{
	    	return this._tabla;
		}
	CtrlsAutoComplete.prototype.getMaxReg = function() 
		{
	    	return this._maxreg;
		}
	CtrlsAutoComplete.prototype.getArrCols = function() 
		{
	    	return this._arrcols;
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

