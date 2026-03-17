
	var nTotalObj = 0;						// Indice de Objetos SyCData existentes en la forma
	var objSyCData = new CSyCData(); 		// Para leer la estructura del XML, Clase de SyCData
	
	$(document).ready(
		function() 
		{
			$('#example').dataTable(
				{         
					"sScrollY": 200,         
					"bJQueryUI": true,    
					"bRetrive" : true,
					"bDestroy" : true,
					"sPaginationType": "full_numbers"    
				} );
			
			// buscamos los controles de la clase SyCData y que sean div  
			$("div.SyCData").each(function(i) 
			{   // Leemos definición de SyCData XML.
				$.ajax({type:"GET", 
						url: document.location.protocol + "//" + (document.location.host==""?"localhost":document.location.host) + "/gestion_conagua_sif/Generador/xml/"+ $(this).attr("id") +".xml", 
						dataType: "xml", 
						async: false, 
						success: readXMLIntoArray });  
				
				if (objSyCData[nTotalObj] != null)
				{
					switch ($.trim(objSyCData[nTotalObj].sEstilo.toUpperCase())) 
					{
						case 'TABULAR':					// llama función presentación en tabular
							var sReturn = fnTabular(nTotalObj, $(this).attr("id"));
							//$(this).attr('innerHTML',sReturn);
							
								
							break;
						case 'GRID':
							// llama función grid
							break;
						case 'CUSTOM':
							// llama función custom
							break;
						default:
							alert("Error...!\rTipo de presentación inválida " + objSyCData[nTotalObj].sTipoSD + "\rConstrucción cancelada.");
							return;
					}
					
					nTotalObj = nTotalObj + 1;
				}
				
			});  // fin de for each de "div.SyCData"
			

		
		});	// fin de ready JQuery 

	/** Procesa el XML crea arreglo de Objetos SyCData. **/   
	function readXMLIntoArray(pXML) 
	{  
		objSyCData[nTotalObj]=new CSyCData(pXML);		
	}
	
	/** Constructor de la Clase SyC Data. **/  
	function CSyCData(pXML) 
	{
		// llena las propiedades
		if (typeof(pXML) == "undefined") return;		// si el parametro es undefined salte
		
		this.sTitulo=$(pXML).find('titulo').text();		// Titulo del Objeto
		this.sSelect=$(pXML).find('select').text();		// Sentencia SQL para el Select para recuperar los datos
		this.sEstilo=$(pXML).find('estilo').text(); 	// Estilo de como se mostrara en el Browser
		this.sUpdate=$(pXML).find('update').text(); 	// Son modificables las propiedades o campos del Objeto
		
		var nNumCols = 0;
		var objSyCDataCol= new CSyCDataCol();
		
		$(pXML).find('columna').each(
		function()
		{
			objSyCDataCol[nNumCols]=new CSyCDataCol($(this));	// crea las columnas del Objeto
			nNumCols = nNumCols+1;
		}); 
		this.arrCols  = objSyCDataCol;
		
		/** Metodos del Objeto **/
		
		// Procesa el SQL para Select para recuperar los Datos de SyCData.
		this.fnSQLSelect = 	function (pIndexObj) 
			{  
				//var sQuery = objSyCData[pIndexObj].sSelect  + Sort de los campos
				// return fnSQLEjecuta(sQuery);
				// 
				return "falta por implemertarse " + pIndexObj;
			}	
	} 

	/** Constructor de la Clase SyC Data Columna. **/ 
	function CSyCDataCol(pCol)
	{
		if (typeof(pCol) == "undefined") return;				// si el parametro es undefined salte
		
		this.sNombreCol=$(pCol).find('nombre').text();			// propiedades de las columnas
		this.sTipoCol=$(pCol).find('tipoCol').text();
		this.sDescripcion=$(pCol).find('descripcion').text();
		this.sTop=$(pCol).find('top').text();
		this.sLeft=$(pCol).find('left').text();
		this.sAncho=$(pCol).find('ancho').text();
		this.sMaxlen=$(pCol).find('maxlen').text();
		this.sSort=$(pCol).find('sort').text();
		this.sRequerido=$(pCol).find('requerido').text();
		this.sVisible=$(pCol).find('visible').text();
		this.sEnabled=$(pCol).find('enabled').text();
		this.sOrigen=$(pCol).find('origen').text();
		this.sValidacionExpresion=$(pCol).find('validacionExpresion').text();
		this.sValidacionMensaje=$(pCol).find('validacionMensaje').text();
	}
	
	/** Ejecuta un SQL llamanda un WS. **/   
	function fnSQLEjecuta(pQuery) 
	{  
		// manda llamar al ws para traerse los datos
	}
	
	/** Presentación en modo Tabular **/  
	function fnTabular(pIndexObj, pCual) 
	{  
		var szResult = "";
		var iRow = 0;
		var iCol = 0;
		
		var arrContexto = document.location.pathname.split('/');
		szURL_AC = document.location.protocol + "//" + (document.location.host==""?"localhost":document.location.host) + "/" + arrContexto[1] + "/";
		
		$.getJSON( szURL_AC + "catalogos/ExecuteJson.jsp", {Descriptor: pCual, MaxReg: 100}, function( json ) {         laTabla= $('#example').dataTable(json);     } ); 
		return;
		
			$.getJSON(szURL_AC + "catalogos/ExecuteJson.jsp", {Descriptor: pCual, MaxReg: 100}, 
			function(datos)
			{

	        	
	        	alert(datos);
			}
		);
		
		
		szResult =  "<h1>Catálogo de Clientes</h1>\r";
		szResult += "<div id='demo2'>\r";
		szResult += "	<table cellpadding='0' cellspacing='0' border='0' class='display' id='tblSyCData" + pIndexObj + "'>\r";
		szResult += "		<thead>\r";
		szResult += "			<tr>\r";
		
		for (x in objSyCData[pIndexObj].arrCols)
		{
			szResult += "<th id='tdRow" + x + "Col" + iCol + "'>" + objSyCData[pIndexObj].arrCols[x].sDescripcion + "</th>\r";
			iCol = iCol + 1;
		}

		szResult += "			</tr>\r";
		szResult += "		</thead>\r";
		szResult += "		<tbody>\r";
		szResult += "			<tr class='odd gradeX'>\r";
		szResult += "				<td>Trident</td>\r";
		szResult += "				<td>Internet Explorer 4.0</td>\r";
		szResult += "			</tr>\r";
		szResult += "			<tr class='even gradeC'>\r";
		szResult += "				<td>Trident</td>\r";
		szResult += "				<td>Internet Explorer 5.0</td>\r";
		szResult += "			</tr>\r";
		szResult += "		</tbody>\r";
		szResult += "		<tfoot>\r";
		szResult += "			<tr>\r";
		szResult += "				<th>Rendering engine</th>\r";
		szResult += "				<th>Browser</th>\r";
		szResult += "			</tr>\r";
		szResult += "		</tfoot>\r";
		szResult += "	</table>\r";
		szResult += "</div>\r";
		
/*		
		szResult =  "<table id='tblSyCData" + pIndexObj + "' width='100%' height='100%' border='1'>\r";
		szResult += "<tr id='trRenglonHeader'>\r";
		for (x in objSyCData[pIndexObj].arrCols)
		{
			szResult += "<td id='tdRow" + x + "Col" + iCol + "'>&nbsp;" + objSyCData[pIndexObj].arrCols[x].sDescripcion +"&nbsp;</td>\r";
			iCol = iCol + 1;
		}
		szResult += "</tr>\r";
		szResult += "</table>\r";
*/
		//alert(szResult)
		return szResult;
	}
	

		
		
		
