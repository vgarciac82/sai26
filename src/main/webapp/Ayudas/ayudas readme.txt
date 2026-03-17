

agregar los archivos de javascript de las ayudas

ejemplo:

	<script type="text/javascript" src="Ayudas/js/jquery.js"></script>
	<script type="text/javascript" src="Ayudas/js/ayudasDlg2.0.js"></script>
	
	
agregamos los controles que queremos que tengan la ayuda y le agregamos la clase de  "class='AyudaSyC'"
ejemplo

	<input id="DetalleInstruccion" name="DetalleInstruccion" type="text" class="AyudaSyC" size="100"/>
	
en la inicializacion del JQuery hay que inicalizar la clase de AyudaSyC de la siguiente manera

	$(document).ready(
				function() 
				{
					$("input.AyudaSyC").subIniciaDlg();	
				}
			);

debe exitir un xml con el nombre del "id" del control de la clase AyudaSyC dentro del subdirectorio mensionado abajo

	ejemplo 
			..\Ayudas\xml-ayudas\DetalleInstruccion.xml 