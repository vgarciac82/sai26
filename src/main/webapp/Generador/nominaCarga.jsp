

<% 
	String msg = "";
	if(request.getParameter("mensaje") != null)
		msg = request.getParameter("mensaje"); 
%>

	<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
	<script type="text/javascript" src="js/jquery.dataTables.js"></script>
	<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
	<script type="text/javascript" src="js/jquery.ui.datepicker-es.js"></script>
 	<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
    <script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
			<script type="text/javascript" src="js/jquery.formatCurrency.js"></script>
		<script type="text/javascript" src="js/jquery.formatCurrency.all.js"></script>
	
	
	<script type="text/javascript" src="js/jquery.form-2.94.js"></script>
	<script type="text/javascript" src="js/crud.js"></script>
 	
	
	
	<script type="text/javascript" charset="utf-8">
		function LimitAttach(tField,iType) { 
			var file=tField.value;
			if (iType==1) { 
				var extArray = new Array(".csv"); 
			} 
			
			var allowSubmit = false; 
			
			if (!file){ 
				return; 
			}
		
			while (file.indexOf("\\") != -1){ 
				file = file.slice(file.indexOf("\\") + 1); 
			}
			var ext = file.slice(file.indexOf(".")).toLowerCase(); 
			for (var i = 0; i < extArray.length; i++) { 
				if (extArray[i] == ext) { 	    
					allowSubmit = true; 
					break; 
				} 
			} 
			
			if (allowSubmit) {
			
			
			} else { 
				tField.value=""; 
				document.getElementById("limpiar").click();
				alert("Usted sólo puede subir archivos con extensiones " + (extArray.join(" ")) + "\nPor favor seleccione un nuevo archivo");
            } 
		}
		
		function validarArchivo(cargarCSV, archivo1){
$("#cArchivo").attr('disabled', true);
$("#Validar1").attr('disabled', true);
$("#Borrar").attr('disabled', true);
$("#Limpia").attr('disabled', true);
alert("Cargando Archivo");
document.getElementById("esperar").style.visibility="visible";
			extensiones = new Array(".csv");
        	if(!archivo1){
        		alert("No se ha cargado el archivo"); 
        	}
        	else{ 
        		permitida = false; 
            	extension1 = (archivo1.substring(archivo1.lastIndexOf("."))).toLowerCase();
         
            	if (extensiones[0] == extension1){
               		permitida = true;
               	} 
           		
           		if (!permitida) { 
            		alert("Comprueba la extensión de los archivos a subir. \nSólo se pueden subir archivos con extensiones: " + extensiones.join()); 
      	    		document.getElementById("limpiar").click();
      	    	}
      	    	
      	    	else{
      	    		document.forms('cargarCSVNomina').submit();  
      	    		 		
      	    	}
      	    } 
        	return 0; 
    	}
    	</script>
  			<div align="center">
  				<table cellspacing="2" cellpadding="2" border="0"> 
  					<tr> 
  						<td align="right"> 
  							<font class="LabelSalida">Nomina (*.csv):</font> 
  						</td> 
  						<td align="left">
  							<input type="file" id="archivoNomina" name="archivoNomina" onblur="LimitAttach(this,1);">
  							<input type="button" value="Cargar Archivo" id="cArchivo" name="cArchivo" onclick="javaScript:validarArchivo(this.form, this.form.archivoNomina.value);" class="btnInterfaceBG"> 
  						</td> 
						<td>
						<input type="hidden" id="mensaje" name="mensaje" value="<%= msg%>">
						<input type="hidden" id="id_caso2" name="id_caso2" value="0" >
						</td>
  					</tr> 
  				</table>	
  			</div>