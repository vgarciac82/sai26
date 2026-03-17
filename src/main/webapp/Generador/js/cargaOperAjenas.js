
	function esIP(){
		if ($("#chk_IP").prop("checked")){
			$("#esIP").val("1");			
		} else {
			$("#esIP").val("0");
		} 
	}
	
	function ActivarRetSICOP(){
		if ($("#chk_LSICOP").prop("checked")){
			$("#RetSICOP").val("1");	
		} else {
			$("#RetSICOP").val("0");
		} 
	}

	function esEjercido(){
		if ($("#chk_Ejercido").prop("checked")){
			$("#esEjercido").val("1");
		} else {
			$("#esEjercido").val("0");
		} 
	}
	
	function grupoRetencion(){
		
		$("#cConcepto").val("");		
		queryFormPost("tGrupoOpAjenasOnclicRead", {async: false });		
		
		$("#cDescripcionPoliza").val($("#cConcepto").val());		
	
	}
	
	function cargarInformacion( archivo ) {
		var ext = new Array( ".csv" );
		var correcto = false;
		var extension = ( archivo.substring( archivo.lastIndexOf( "." ) ) ).toLowerCase();

		if( ext[ 0 ] == extension ) {
			correcto = true;
		}

		if( !correcto ) {
			Swal.fire({ icon: "error",
						text: "Comprueba la extensión del archivo a subir."});			
			return;
		}
		
		if ($("#chk_IP").prop("checked")){
			$("#esIP").val("1");			
		} else {
			$("#esIP").val("0");
		} 
		
		if ($("#chk_LSICOP").prop("checked")){
			$("#RetSICOP").val("1");	
		} else {
			$("#RetSICOP").val("0");
		} 
		
		if ($("#chk_Ejercido").prop("checked")){
			$("#esEjercido").val("1");
		} else {
			$("#esEjercido").val("0");
		} 
		
		if($("#cClc").val() == "") {
			Swal.fire({ icon: "warning",
						text: "Favor de capturar el campo Nombre para continuar con el proceso."});			
			return;
			
		} else if($("#cGrupo").val() == "") {
			Swal.fire({ icon: "warning",
						text: "Favor de seleccionar el tipo de retencion que se cargara."});			
			return;
			
		} else if($("#cIDRFC").val() == "") {
			Swal.fire({ icon: "warning",
						text: "Favor de seleccionar el RFC."});			
			return;
			
		} else {
					
			$(".captura").prop("readonly", true);
			document.getElementById("chk_IP").disabled = true;
			document.getElementById("chk_LSICOP").disabled = true;
			document.getElementById("chk_Ejercido").disabled = true;
			
			Swal.fire({
				 title: '¿Desea continuar?',
				 text: "Esta Seguro de Cargar Informacion.",
				 icon: 'warning',
				 showCancelButton: true,
				 confirmButtonColor: '#288BA8',
				 cancelButtonColor: '#e6e6e6',
				 confirmButtonText: 'Aceptar',
				 cancelButtonText: 'Cancelar'
			   }).then((result) => {
				 if (result.isConfirmed) {
					$( "#frmLayoutOA" ).attr( "action", "../gstnmngr/LayoutOperAjenaServlet" );
					$( "#frmLayoutOA" ).attr( "enctype", "multipart/form-data" );
					
					$( "#frmLayoutOA" ).submit();
				 } else if (result.dismiss === Swal.DismissReason.cancel) {
					return;
				 }
			   })
		}
				
	}

	function informacionCargadaTemp( folio ) {
		$( '#tblCargaDatos' ).dataTable().fnClearTable();
		var szTabla = "tOperAjenas_temp";
		var campos = " nFolioTemporal = " + folio;
		var elParametro = "";
		var order = "";

		$.getJSON( "../catalogos/SelectJson.jsp", {
		Tabla : szTabla,
		Campos : campos,
		Param : elParametro,
		MaxReg : "10",
		Order : order,
		ajax : 'true'
		}, function( j ) {

			for( var i = 0; i < j.length; i++ ) {
				$( "#tblCargaDatos" ).dataTable().fnAddData( [ j[ i ].Col0, j[ i ].Col1, j[ i ].Col2, j[ i ].Col3, j[ i ].Col4, j[ i ].Col5, j[ i ].Col6 ] );
			}
		} );

	}

	function limpiarTabla( ) {
		$( "#folioTempGral" ).val("");
		$( "#btnEnviarLayout" ).show();
		$( "#btnLimpiar" ).hide();
		$( "#btnAplicar" ).hide();
		mensaje = "";
		$( '#tblCargaDatos' ).dataTable().fnClearTable();
		var szTabla = "tOperAjenas_limpia";		
		var elParametro = "";
		var order = "";

		$.getJSON( "../catalogos/SelectJson.jsp", {
		Tabla : szTabla,
		Campos : campos,
		Param : elParametro,
		MaxReg : "10",
		Order : order,
		ajax : 'true'
		}, function( j ) {

			for( var i = 0; i < j.length; i++ ) {
				$( "#tblCargaDatos" ).dataTable().fnAddData( [ j[ i ].Col0, j[ i ].Col1, j[ i ].Col2, j[ i ].Col3, j[ i ].Col4, j[ i ].Col5, j[ i ].Col6 ] );
			}
		} );		
		$(".captura").prop("readonly", false);
			document.getElementById("chk_IP").disabled = fale;
			document.getElementById("chk_LSICOP").disabled = false;
			document.getElementById("chk_Ejercido").disabled = false;			
	}
	
	function aplicarInformacion() {
		if($("#folioTempGral").val() != 0){
			Swal.fire({
				 title: '¿Desea continuar?',
				 text: "Esta Seguro de Cargar Informacion.",
				 icon: 'warning',
				 showCancelButton: true,
				 confirmButtonColor: '#288BA8',
				 cancelButtonColor: '#e6e6e6',
				 confirmButtonText: 'Aceptar',
				 cancelButtonText: 'Cancelar'
			   }).then((result) => {
				 if (result.isConfirmed) {
					$( "#frmLayoutOA" ).attr( "action", "../gstnmngr/LayoutOperAjenaServlet" );
					$( "#frmLayoutOA" ).attr( "enctype", "multipart/form-data" );
	
					$( "#frmLayoutOA" ).submit();
				 } else if (result.dismiss === Swal.DismissReason.cancel) {
					return;
				 }
			   })
		}else{
			Swal.fire({ icon: "error",
						text: "Favor de cargar el lay out a aplicar."});			
		}		
	}
	