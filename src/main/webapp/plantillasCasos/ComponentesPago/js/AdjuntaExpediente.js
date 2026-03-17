

	function inittable(){
		/*Inicializar tabla*/
		oTable= $("#dtAdjuntos").dataTable({
           "bLengthChange" : true,
           "bFilter" : true,
           "bSort" : true,
           "bInfo" : true,
           "bPaginate" : true,
           "bAutoWidth" : false,
           "bScrollCollapse" : true,
         
           "sPaginationType" : "full_numbers",
           "bJQueryUI" : true,
           "bRetrive" : true,
           "bDestroy" : true,
           "bServerSide": true,
           oLanguage : {
                  sProcessing : "Procesando...",
                  sLengthMenu : "Mostrar _MENU_ registros",
                  sZeroRecords : "No hay registros a mostrar",
                  sEmptyTable : "No se encontraron resultados", 
                  sLoadingRecords : "Cargando...",
                  sInfo : "Registros _START_ al _END_ de _TOTAL_",
                  sInfoEmpty : "Registro 0 al 0 de 0",
                  sInfoFiltered : "(filtered from _MAX_ total entries)",
                  sInfoPostFix : "",
                  sInfoThousands : ",",
                  sSearch : "Buscar:",
                  oPaginate : {
                        sFirst : "Primero",
                        sPrevious : "Ant.",
                        sNext : "Sigte.",
                        sLast : "&Uacute;ltimo"
                  }
           }
    	});                    
	}
	
	function cargaDataTable(){
		var cond = "FOLIO = '" + $("#FOLIO").val() + "' AND TITULO_APLICACION = '" + $("#TITULO_APLICACION").val() + "'";
		
		oTable = $('#dtAdjuntos').dataTable({
			"bPaginate" : false,
			"bLengthChange" : true,
			"bFilter" : true,
			"bSort" : true,
			"bInfo" : true,
			"bAutoWidth" : true,
			"sScrollY" : 500,
			"sScrollX" : 1600,
			"bJQueryUI" : true,
			"bRetrive" : true,
			"bDestroy" : true,
			"sPaginationType" : "full_numbers",
			"bScrollCollapse" : true,
			"bServerSide" : true,
			oLanguage : {
                  sProcessing : "Procesando...",
                  sLengthMenu : "Mostrar _MENU_ registros",
                  sZeroRecords : "No hay registros a mostrar",
                  sEmptyTable : "No se encontraron resultados", 
                  sLoadingRecords : "Cargando...",
                  sInfo : "Registros _START_ al _END_ de _TOTAL_",
                  sInfoEmpty : "Registro 0 al 0 de 0",
                  sInfoFiltered : "(filtered from _MAX_ total entries)",
                  sInfoPostFix : "",
                  sInfoThousands : ",",
                  sSearch : "Buscar:",
                  oPaginate : {
                        sFirst : "Primero",
                        sPrevious : "Ant.",
                        sNext : "Sigte.",
                        sLast : "&Uacute;ltimo"
                  }
           },
			sAjaxSource : window.location.protocol + "//"
					+ window.location.host + "/"
					+ window.location.pathname.split("/")[1]
					+ "/crud?rt=t&ql=vDocumentosAdjuntos&qw=" + cond,
			aaSorting: [[ 3, "asc" ]] ,
			aoColumns : [
						{sName : "FOLIO", bVisible : false }, 
						{sName : "nombre_documento"}, 
						{sName : "Fortimax"}
						]
		});			 
	}
