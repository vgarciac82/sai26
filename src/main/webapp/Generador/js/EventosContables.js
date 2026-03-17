
	var oTableCRC;
	var oTablePM;
	var oTablaGrupoGuia;
	var modalGrupoGuia;
	var oTablaSubGrupoGuia;
	var modalSubGrupoGuia;
	var oTablaConsecutivo;
	var modalConsecutivo;
	var oTablaCuentas;
	var modalCuentas;
	var oTablaPartida;
	var modalPartida;
	
	$(document).ready(function() {	
		inittable();		
		navEvento();
		$("#btnCatGrupoGuia").attr('disabled','disabled');
		$("#btnCatSubGrupoGuia").attr('disabled','disabled');
		$("#btnCatConsecutivo").attr('disabled','disabled');
		
		querySelectPost("CAT_UNIDAD_EJECUTORARead", "cIdUnidadEjecutora", {async: false });			
		querySelectPost("readEjercicioFiscalActivo", "nEjercicioFiscal", {async: false });	
		
		modalGrupoGuia = new bootstrap.Modal(document.getElementById('dialog-formGrupo'), 'data-bs-backdrop');
		modalSubGrupoGuia = new bootstrap.Modal(document.getElementById('dialog-formSubGrupo'), 'data-bs-backdrop');
		modalConsecutivo = new bootstrap.Modal(document.getElementById('dialog-formConsecutivo'), 'data-bs-backdrop');
		modalCuentas = new bootstrap.Modal(document.getElementById('dialog-formCuentas'), 'data-bs-backdrop');
		modalPartida = new bootstrap.Modal(document.getElementById('dialog-formPartida'), 'data-bs-backdrop');
		
		$( "#btnCatGrupoGuia" )
			.button()
			.click(function() {
				
			modalGrupoGuia.show();
			
			oTablaGrupoGuia = $("#tblGrupoGuia").dataTable({
				oLanguage: {
					sProcessing: "Procesando...",
					sLengthMenu: "Mostrar _MENU_ registros",
					sZeroRecords: "No hay registros a mostrar",
					sEmptyTable: "No hay datos en la tabla",
					sLoadingRecords: "Cargando...",
					sInfo: "Registros _START_ al _END_ de _TOTAL_",
					sInfoEmpty: "Registro 0 al 0 de 0",
					sInfoFiltered: "(filtered from _MAX_ total entries)",
					sInfoPostFix: "",
					sInfoThousands: ",",
					sSearch: "Buscar:",
					oPaginate: {
						sFirst:    "Primero",
						sPrevious: "Ant.",
						sNext:     "Sigte.",
						sLast:     "&Uacute;ltimo"
					}
				},
				bServerSide: true,	
				sAjaxSource : window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=tCatGuia&qw=" + "1 = 1",
				"bLengthChange" : true,
	            "bFilter" : true,
	            "bSort" : true,
	            "bInfo" : true,
	            "bPaginate" : false,
	            "bAutoWidth" : false,
	            "bScrollCollapse" : true,   		            
	            "sPaginationType" : "full_numbers",
	            "bJQueryUI" : true,
	            "bRetrive" : true,
	            "bDestroy" : true,
	            "bServerSide": true,                   
				"iDisplayLength": 100,	
				aaSorting: [[0, "asc" ]] ,
				aoColumns: [
					{ sName: "id_Guia" },
					{ sName: "cDescripcion" }					
				]			
	        });
			
			/*Alta de funcionalidad de doble clic*/
			$("#tblGrupoGuia tbody").unbind('dblclick'); 
		    $("#tblGrupoGuia tbody").dblclick( function( e ) {                	                	
		         $(oTablaGrupoGuia.fnSettings().aoData).each(
		                function (){
		                      $(this.nTr).removeClass('table-primary');
		         });
		         $(e.target.parentNode).addClass('table-primary');
		         tblDblClickG(e);	                 
		    });
		
		});
		
		$( "#btnCatSubGrupoGuia" )
			.button()
			.click(function() {
				
			modalSubGrupoGuia.show();			
			var grupoGuia = $("#nGrupo").val();
			
			if(grupoGuia == ''){
				modalSubGrupoGuia.hide();
				Swal.fire({ icon: "warning",
							text: "Debes seleccionar primero el Grupo de la Guia." });
				return;				
			}
			
			oTablaSubGrupoGuia = $("#tblSubGrupoGuia").dataTable({
				oLanguage: {
					sProcessing: "Procesando...",
					sLengthMenu: "Mostrar _MENU_ registros",
					sZeroRecords: "No hay registros a mostrar",
					sEmptyTable: "No hay datos en la tabla",
					sLoadingRecords: "Cargando...",
					sInfo: "Registros _START_ al _END_ de _TOTAL_",
					sInfoEmpty: "Registro 0 al 0 de 0",
					sInfoFiltered: "(filtered from _MAX_ total entries)",
					sInfoPostFix: "",
					sInfoThousands: ",",
					sSearch: "Buscar:",
					oPaginate: {
						sFirst:    "Primero",
						sPrevious: "Ant.",
						sNext:     "Sigte.",
						sLast:     "&Uacute;ltimo"
					}
				},
				bServerSide: true,	
				sAjaxSource : window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_catSubGrupoGuia&qw=" + "id_Guia = " + grupoGuia,
				"bLengthChange" : true,
	            "bFilter" : true,
	            "bSort" : true,
	            "bInfo" : true,
	            "bPaginate" : false,
	            "bAutoWidth" : false,
	            "bScrollCollapse" : true,   		            
	            "sPaginationType" : "full_numbers",
	            "bJQueryUI" : true,
	            "bRetrive" : true,
	            "bDestroy" : true,
	            "bServerSide": true,                   
				"iDisplayLength": 100,	
				aaSorting: [[1, "asc" ]] ,
				aoColumns: [
					{ sName: "id_Guia" },
					{ sName: "nIdGrupoEvento" },
					{ sName: "cDescripcion" }					
				]			
	        });
			
			/*Alta de funcionalidad de doble clic*/
			$("#tblSubGrupoGuia tbody").unbind('dblclick'); 
		    $("#tblSubGrupoGuia tbody").dblclick( function( e ) {                	                	
		         $(oTablaSubGrupoGuia.fnSettings().aoData).each(
		                function (){
		                      $(this.nTr).removeClass('table-primary');
		         });
		         $(e.target.parentNode).addClass('table-primary');
		         tblDblClickSG(e);	                 
		    });
		
		});
		
		$( "#btnCatConsecutivo" )
			.button()
			.click(function() {
				
			modalConsecutivo.show();			
			var grupoGuia = $("#nGrupo").val();
			var subGrupoGuia = $("#nSubGrupo").val();
			
			if(grupoGuia == '' || subGrupoGuia == ''){
				modalConsecutivo.hide();
				Swal.fire({ icon: "warning",
							text: "Debes seleccionar primero el Grupo y el Sub-Grupo de la Guia." });
				return;				
			}
			
			oTablaConsecutivo = $("#tblConsecutivo").dataTable({
				oLanguage: {
					sProcessing: "Procesando...",
					sLengthMenu: "Mostrar _MENU_ registros",
					sZeroRecords: "No hay registros a mostrar",
					sEmptyTable: "No hay datos en la tabla",
					sLoadingRecords: "Cargando...",
					sInfo: "Registros _START_ al _END_ de _TOTAL_",
					sInfoEmpty: "Registro 0 al 0 de 0",
					sInfoFiltered: "(filtered from _MAX_ total entries)",
					sInfoPostFix: "",
					sInfoThousands: ",",
					sSearch: "Buscar:",
					oPaginate: {
						sFirst:    "Primero",
						sPrevious: "Ant.",
						sNext:     "Sigte.",
						sLast:     "&Uacute;ltimo"
					}
				},
				bServerSide: true,	
				sAjaxSource : window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_catConsecutivo&qw=" + " id_Guia = " + grupoGuia + " AND nIdGrupoEvento = '" + subGrupoGuia + "'",
				"bLengthChange" : true,
	            "bFilter" : true,
	            "bSort" : true,
	            "bInfo" : true,
	            "bPaginate" : false,
	            "bAutoWidth" : false,
	            "bScrollCollapse" : true,   		            
	            "sPaginationType" : "full_numbers",
	            "bJQueryUI" : true,
	            "bRetrive" : true,
	            "bDestroy" : true,
	            "bServerSide": true,                   
				"iDisplayLength": 100,	
				aaSorting: [[1, "asc" ]] ,
				aoColumns: [
					{ sName: "id_Guia" },					
					{ sName: "nIdGrupoEvento" },
					{ sName: "nConsecutivo" },
					{ sName: "dConcepto" },
					{ sName: "dDocumentoFuente" },
					{ sName: "dPeriodicidad" }					
				]			
	        });
			
			/*Alta de funcionalidad de doble clic*/
			$("#tblConsecutivo tbody").unbind('dblclick'); 
		    $("#tblConsecutivo tbody").dblclick( function( e ) {                	                	
		         $(oTablaConsecutivo.fnSettings().aoData).each(
		                function (){
		                      $(this.nTr).removeClass('table-primary');
		         });
		         $(e.target.parentNode).addClass('table-primary');
		         tblDblClickC(e);	                 
		    });
		
		});
		
		$( "#btnCatCuentas" )
			.button()
			.click(function() {
				
			modalCuentas.show();			
			
			oTablaCuentas = $("#tblCuentas").dataTable({
				oLanguage: {
					sProcessing: "Procesando...",
					sLengthMenu: "Mostrar _MENU_ registros",
					sZeroRecords: "No hay registros a mostrar",
					sEmptyTable: "No hay datos en la tabla",
					sLoadingRecords: "Cargando...",
					sInfo: "Registros _START_ al _END_ de _TOTAL_",
					sInfoEmpty: "Registro 0 al 0 de 0",
					sInfoFiltered: "(filtered from _MAX_ total entries)",
					sInfoPostFix: "",
					sInfoThousands: ",",
					sSearch: "Buscar:",
					oPaginate: {
						sFirst:    "Primero",
						sPrevious: "Ant.",
						sNext:     "Sigte.",
						sLast:     "&Uacute;ltimo"
					}
				},
				bServerSide: true,	
				sAjaxSource : window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vCuentasEventos&qw=" + " 1 = 1 ",
				"bLengthChange" : true,
	            "bFilter" : true,
	            "bSort" : true,
	            "bInfo" : true,
	            "bPaginate" : false,
	            "bAutoWidth" : false,
	            "bScrollCollapse" : true,   		            
	            "sPaginationType" : "full_numbers",
	            "bJQueryUI" : true,
	            "bRetrive" : true,
	            "bDestroy" : true,
	            "bServerSide": true,                   
				"iDisplayLength": 100,	
				aaSorting: [[1, "asc" ]] ,
				aoColumns: [
					{ sName: "ncuenta" },					
					{ sName: "dcuenta" },
					{ sName: "cSubcuenta" }					
				]			
	        });
			
			/*Alta de funcionalidad de doble clic*/
			$("#tblCuentas tbody").unbind('dblclick'); 
		    $("#tblCuentas tbody").dblclick( function( e ) {                	                	
		         $(oTablaCuentas.fnSettings().aoData).each(
		                function (){
		                      $(this.nTr).removeClass('table-primary');
		         });
		         $(e.target.parentNode).addClass('table-primary');
		         tblDblClickCta(e);	                 
		    });
		
		});
		
		$( "#btnCatPartidas" )
			.button()
			.click(function() {
				
			modalPartida.show();
			
			oTablaPartida = $("#tblPartida").dataTable({
				oLanguage: {
					sProcessing: "Procesando...",
					sLengthMenu: "Mostrar _MENU_ registros",
					sZeroRecords: "No hay registros a mostrar",
					sEmptyTable: "No hay datos en la tabla",
					sLoadingRecords: "Cargando...",
					sInfo: "Registros _START_ al _END_ de _TOTAL_",
					sInfoEmpty: "Registro 0 al 0 de 0",
					sInfoFiltered: "(filtered from _MAX_ total entries)",
					sInfoPostFix: "",
					sInfoThousands: ",",
					sSearch: "Buscar:",
					oPaginate: {
						sFirst:    "Primero",
						sPrevious: "Ant.",
						sNext:     "Sigte.",
						sLast:     "&Uacute;ltimo"
					}
				},
				bServerSide: true,	
				sAjaxSource : window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=tCatalogoPartida&qw=" + "1 = 1",
				"bLengthChange" : true,
	            "bFilter" : true,
	            "bSort" : true,
	            "bInfo" : true,
	            "bPaginate" : false,
	            "bAutoWidth" : false,
	            "bScrollCollapse" : true,   		            
	            "sPaginationType" : "full_numbers",
	            "bJQueryUI" : true,
	            "bRetrive" : true,
	            "bDestroy" : true,
	            "bServerSide": true,                   
				"iDisplayLength": 100,	
				aaSorting: [[0, "asc" ]] ,
				aoColumns: [
					{ sName: "cPartida" },
					{ sName: "dPartida" }					
				]			
	        });
			
			/*Alta de funcionalidad de doble clic*/
			$("#tblPartida tbody").unbind('dblclick'); 
		    $("#tblPartida tbody").dblclick( function( e ) {                	                	
		         $(oTablaPartida.fnSettings().aoData).each(
		                function (){
		                      $(this.nTr).removeClass('table-primary');
		         });
		         $(e.target.parentNode).addClass('table-primary');
		         tblDblClickP(e);	                 
		    });
		
		});
		
	});
	
	function inittable(){
		/*Inicializar tabla*/
		oTableCRC= $("#tblCuentasCRC").dataTable({
              	"bLengthChange" : true,
	            "bFilter" : false,
	            "bSort" : true,
	            "bInfo" : true,
	            "bPaginate" : false,
	            "bAutoWidth" : false,
	            "bScrollCollapse" : true,   		            
	            "sPaginationType" : "full_numbers",
	            "bJQueryUI" : true,
	            "bRetrive" : true,
	            "bDestroy" : true,
	            "bServerSide": true, 
       			"fnInitComplete": function() {   
       				oTableCRC.fnAdjustColumnSizing();
    			},
               oLanguage : {
               	    sProcessing: "Procesando...",
          			sLengthMenu: "Mostrar _MENU_ registros",
          			sZeroRecords: "No hay registros a mostrar",
          			sEmptyTable: "No hay datos en la tabla",
          			sLoadingRecords: "Cargando...",
          			sInfo: "Registros _START_ al _END_ de _TOTAL_",
          			sInfoEmpty: "Registro 0 al 0 de 0",
          			sInfoFiltered: "(filtado de _MAX_ registros)",
          			sInfoPostFix: "",
          			sInfoThousands: ",",
          			sSearch: "Buscar:",
          			oPaginate: {
          				sFirst:    "Primero",
          				sPrevious: "Ant.",
          				sNext:     "Sigte.",
          				sLast:     "&Uacute;ltimo"
                      }
               }
        });    

		/*Inicializar tabla*/
		oTablePM= $("#tblCuentasPM").dataTable({
              	"bLengthChange" : true,
	            "bFilter" : false,
	            "bSort" : true,
	            "bInfo" : true,
	            "bPaginate" : false,
	            "bAutoWidth" : false,
	            "bScrollCollapse" : true,   		            
	            "sPaginationType" : "full_numbers",
	            "bJQueryUI" : true,
	            "bRetrive" : true,
	            "bDestroy" : true,
	            "bServerSide": true, 
       			"fnInitComplete": function() {   
       				oTablePM.fnAdjustColumnSizing();
    			},
               oLanguage : {
               	    sProcessing: "Procesando...",
          			sLengthMenu: "Mostrar _MENU_ registros",
          			sZeroRecords: "No hay registros a mostrar",
          			sEmptyTable: "No hay datos en la tabla",
          			sLoadingRecords: "Cargando...",
          			sInfo: "Registros _START_ al _END_ de _TOTAL_",
          			sInfoEmpty: "Registro 0 al 0 de 0",
          			sInfoFiltered: "(filtado de _MAX_ registros)",
          			sInfoPostFix: "",
          			sInfoThousands: ",",
          			sSearch: "Buscar:",
          			oPaginate: {
          				sFirst:    "Primero",
          				sPrevious: "Ant.",
          				sNext:     "Sigte.",
          				sLast:     "&Uacute;ltimo"
                      }
               }
        });  

		/*Inicializar tabla*/
		oTablaGrupoGuia= $("#tblGrupoGuia").dataTable({
              	"bLengthChange" : true,
	            "bFilter" : false,
	            "bSort" : true,
	            "bInfo" : true,
	            "bPaginate" : false,
	            "bAutoWidth" : false,
	            "bScrollCollapse" : true,   		            
	            "sPaginationType" : "full_numbers",
	            "bJQueryUI" : true,
	            "bRetrive" : true,
	            "bDestroy" : true,
	            "bServerSide": true, 
       			"fnInitComplete": function() {   
       				oTablaGrupoGuia.fnAdjustColumnSizing();
    			},
               oLanguage : {
               	    sProcessing: "Procesando...",
          			sLengthMenu: "Mostrar _MENU_ registros",
          			sZeroRecords: "No hay registros a mostrar",
          			sEmptyTable: "No hay datos en la tabla",
          			sLoadingRecords: "Cargando...",
          			sInfo: "Registros _START_ al _END_ de _TOTAL_",
          			sInfoEmpty: "Registro 0 al 0 de 0",
          			sInfoFiltered: "(filtado de _MAX_ registros)",
          			sInfoPostFix: "",
          			sInfoThousands: ",",
          			sSearch: "Buscar:",
          			oPaginate: {
          				sFirst:    "Primero",
          				sPrevious: "Ant.",
          				sNext:     "Sigte.",
          				sLast:     "&Uacute;ltimo"
                      }
               }
        });

		/*Inicializar tabla*/
		oTablaSubGrupoGuia= $("#tblSubGrupoGuia").dataTable({
              	"bLengthChange" : true,
	            "bFilter" : false,
	            "bSort" : true,
	            "bInfo" : true,
	            "bPaginate" : false,
	            "bAutoWidth" : false,
	            "bScrollCollapse" : true,   		            
	            "sPaginationType" : "full_numbers",
	            "bJQueryUI" : true,
	            "bRetrive" : true,
	            "bDestroy" : true,
	            "bServerSide": true, 
       			"fnInitComplete": function() {   
       				oTablaGrupoGuia.fnAdjustColumnSizing();
    			},
               oLanguage : {
               	    sProcessing: "Procesando...",
          			sLengthMenu: "Mostrar _MENU_ registros",
          			sZeroRecords: "No hay registros a mostrar",
          			sEmptyTable: "No hay datos en la tabla",
          			sLoadingRecords: "Cargando...",
          			sInfo: "Registros _START_ al _END_ de _TOTAL_",
          			sInfoEmpty: "Registro 0 al 0 de 0",
          			sInfoFiltered: "(filtado de _MAX_ registros)",
          			sInfoPostFix: "",
          			sInfoThousands: ",",
          			sSearch: "Buscar:",
          			oPaginate: {
          				sFirst:    "Primero",
          				sPrevious: "Ant.",
          				sNext:     "Sigte.",
          				sLast:     "&Uacute;ltimo"
                      }
               }
        });

		/*Inicializar tabla*/
		oTablaConsecutivo= $("#tblConsecutivo").dataTable({
              	"bLengthChange" : true,
	            "bFilter" : false,
	            "bSort" : true,
	            "bInfo" : true,
	            "bPaginate" : false,
	            "bAutoWidth" : false,
	            "bScrollCollapse" : true,   		            
	            "sPaginationType" : "full_numbers",
	            "bJQueryUI" : true,
	            "bRetrive" : true,
	            "bDestroy" : true,
	            "bServerSide": true, 
       			"fnInitComplete": function() {   
       				oTablaGrupoGuia.fnAdjustColumnSizing();
    			},
               oLanguage : {
               	    sProcessing: "Procesando...",
          			sLengthMenu: "Mostrar _MENU_ registros",
          			sZeroRecords: "No hay registros a mostrar",
          			sEmptyTable: "No hay datos en la tabla",
          			sLoadingRecords: "Cargando...",
          			sInfo: "Registros _START_ al _END_ de _TOTAL_",
          			sInfoEmpty: "Registro 0 al 0 de 0",
          			sInfoFiltered: "(filtado de _MAX_ registros)",
          			sInfoPostFix: "",
          			sInfoThousands: ",",
          			sSearch: "Buscar:",
          			oPaginate: {
          				sFirst:    "Primero",
          				sPrevious: "Ant.",
          				sNext:     "Sigte.",
          				sLast:     "&Uacute;ltimo"
                      }
               }
        });  

		/*Inicializar tabla*/
		oTablaCuentas= $("#tblCuentas").dataTable({
              	"bLengthChange" : true,
	            "bFilter" : false,
	            "bSort" : true,
	            "bInfo" : true,
	            "bPaginate" : false,
	            "bAutoWidth" : false,
	            "bScrollCollapse" : true,   		            
	            "sPaginationType" : "full_numbers",
	            "bJQueryUI" : true,
	            "bRetrive" : true,
	            "bDestroy" : true,
	            "bServerSide": true, 
       			"fnInitComplete": function() {   
       				oTablaGrupoGuia.fnAdjustColumnSizing();
    			},
               oLanguage : {
               	    sProcessing: "Procesando...",
          			sLengthMenu: "Mostrar _MENU_ registros",
          			sZeroRecords: "No hay registros a mostrar",
          			sEmptyTable: "No hay datos en la tabla",
          			sLoadingRecords: "Cargando...",
          			sInfo: "Registros _START_ al _END_ de _TOTAL_",
          			sInfoEmpty: "Registro 0 al 0 de 0",
          			sInfoFiltered: "(filtado de _MAX_ registros)",
          			sInfoPostFix: "",
          			sInfoThousands: ",",
          			sSearch: "Buscar:",
          			oPaginate: {
          				sFirst:    "Primero",
          				sPrevious: "Ant.",
          				sNext:     "Sigte.",
          				sLast:     "&Uacute;ltimo"
                      }
               }
        });  

		/*Inicializar tabla*/
		oTablaPartida= $("#tblPartida").dataTable({
              	"bLengthChange" : true,
	            "bFilter" : false,
	            "bSort" : true,
	            "bInfo" : true,
	            "bPaginate" : false,
	            "bAutoWidth" : false,
	            "bScrollCollapse" : true,   		            
	            "sPaginationType" : "full_numbers",
	            "bJQueryUI" : true,
	            "bRetrive" : true,
	            "bDestroy" : true,
	            "bServerSide": true, 
       			"fnInitComplete": function() {   
       				oTablaGrupoGuia.fnAdjustColumnSizing();
    			},
               oLanguage : {
               	    sProcessing: "Procesando...",
          			sLengthMenu: "Mostrar _MENU_ registros",
          			sZeroRecords: "No hay registros a mostrar",
          			sEmptyTable: "No hay datos en la tabla",
          			sLoadingRecords: "Cargando...",
          			sInfo: "Registros _START_ al _END_ de _TOTAL_",
          			sInfoEmpty: "Registro 0 al 0 de 0",
          			sInfoFiltered: "(filtado de _MAX_ registros)",
          			sInfoPostFix: "",
          			sInfoThousands: ",",
          			sSearch: "Buscar:",
          			oPaginate: {
          				sFirst:    "Primero",
          				sPrevious: "Ant.",
          				sNext:     "Sigte.",
          				sLast:     "&Uacute;ltimo"
                      }
               }
        });  
	}
	
	function navEvento(){
		$("#divAltaEvento").show();
		$("#divConfiguracionGuia").hide();
		$("#divConfiguracionCuentas").hide();
	}
	
	function navGuia(){
		$("#divAltaEvento").hide();
		$("#divConfiguracionGuia").show();
		$("#divConfiguracionCuentas").hide();
	}
	
	function navCuentas(){
		$("#divAltaEvento").hide();
		$("#divConfiguracionGuia").hide();
		$("#divConfiguracionCuentas").show();
		$("#divPolManual").hide();
	}
	
	function reload(){
		$("#btnGuardaEvento").button();
		$("#btnGuardaGuia").button();
		$("#btnGuardaCuentas").button();	
		$("#AgregarCuentasCRC").button();	
		$("#AgregarCuentasPM").button();		
				
		$("#TIPO_CONFIGURACION1").removeAttr('disabled','disabled');
		$("#TIPO_CONFIGURACION2").removeAttr('disabled','disabled');
		$("#TIPO_CONFIGURACION3").removeAttr('disabled','disabled');
		$("#ExisteGuia").removeAttr('disabled','disabled');
		
		$("#tabs-1").click();
		
		navEvento();
	}
		
	function selecciona(elemento){		
		if (elemento.value == "2"){
			$("#nIdGrupoEvento").attr('disabled','disabled');
			$("#cNombreGrupo").attr('disabled','disabled');
			$("#nIdSubGrupoEvento").removeAttr('disabled','disabled');
			$("#cNombreSubGrupo").removeAttr('disabled','disabled');
		} 
		else if(elemento.value == "3"){
			$("#nIdGrupoEvento").attr('disabled','disabled');
			$("#cNombreGrupo").attr('disabled','disabled');
			$("#nIdSubGrupoEvento").attr('disabled','disabled');
			$("#cNombreSubGrupo").attr('disabled','disabled');
		}
		else if(elemento.value == "1"){
			$("#nIdGrupoEvento").removeAttr('disabled','disabled');
			$("#cNombreGrupo").removeAttr('disabled','disabled');
			$("#nIdSubGrupoEvento").removeAttr('disabled','disabled');
			$("#cNombreSubGrupo").removeAttr('disabled','disabled');
		}		
	}
	
	function seleccionaModulo(elemento){		
		if (elemento.value == "1"){
			$("#modC").text("(Caja)");
			$("#modRC").text("");
			$("#modPM").text("");
			$("#divCajaRC").show();
			$("#divPolManual").hide();			
		}
		else if (elemento.value == "2"){
			$("#modC").text("");
			$("#modRC").text("(Reintegros Caja)");			
			$("#modPM").text("");
			$("#divCajaRC").show();
			$("#divPolManual").hide();
		} 
		else if (elemento.value == "3"){
			$("#modC").text("");
			$("#modRC").text("");
			$("#modPM").text("(Poliza Manual)");
			$("#divCajaRC").hide();
			$("#divPolManual").show();
		} 
	}
			
	function GuardaEvento(){	
		var okEvto = 1;
						
		if($("#TIPO_CONFIGURACION1").attr("checked")){
			if ($("#nIdGrupoEvento").val() == ""){
				Swal.fire({ icon: "warning",
							text: "Campo ID Grupo es obligatorio, favor de capturarlo."});			
				okEvto = 0;
			} else if ($("#cNombreGrupo").val() == ""){
				Swal.fire({ icon: "warning",
							text: "Campo Nombe Grupo es obligatorio, favor de capturarlo."});			
				okEvto = 0;
			} else if ($("#nIdSubGrupoEvento").val() == ""){
				Swal.fire({ icon: "warning",
							text: "Campo ID Sub-Grupo es obligatorio, favor de capturarlo."});			
				okEvto = 0;
			}  else if ($("#cNombreSubGrupo").val() == ""){
				Swal.fire({ icon: "warning",
							text: "Campo Nombre Sub-Grupo es obligatorio, favor de capturarlo."});			
				okEvto = 0;
			} else if ($("#cEvento").val() == ""){
				Swal.fire({ icon: "warning",
							text: "Campo ID Evento es obligatorio, favor de capturarlo."});			
				okEvto = 0;
			}  else if ($("#dEvento").val() == ""){
				Swal.fire({ icon: "warning",
							text: "Campo Nombre Evento es obligatorio, favor de capturarlo."});			
				okEvto = 0;
			} 	
		} else if($("#TIPO_CONFIGURACION2").attr("checked")){
			if ($("#nIdSubGrupoEvento").val() == ""){
				Swal.fire({ icon: "warning",
							text: "Campo ID Sub-Grupo es obligatorio, favor de capturarlo."});			
				okEvto = 0;
			}  else if ($("#cNombreSubGrupo").val() == ""){
				Swal.fire({ icon: "warning",
							text: "Campo Nombre Sub-Grupo es obligatorio, favor de capturarlo."});			
				okEvto = 0;
			} else if ($("#cEvento").val() == ""){
				Swal.fire({ icon: "warning",
							text: "Campo ID Evento es obligatorio, favor de capturarlo."});			
				okEvto = 0;
			}  else if ($("#dEvento").val() == ""){
				Swal.fire({ icon: "warning",
							text: "Campo Nombre Evento es obligatorio, favor de capturarlo."});			
				okEvto = 0;
			} 	
		} else if($("#TIPO_CONFIGURACION3").attr("checked")){
			if ($("#cEvento").val() == ""){
				Swal.fire({ icon: "warning",
							text: "Campo ID Evento es obligatorio, favor de capturarlo."});			
				okEvto = 0;
			}  else if ($("#dEvento").val() == ""){
				Swal.fire({ icon: "warning",
							text: "Campo Nombre Evento es obligatorio, favor de capturarlo."});			
				okEvto = 0;
			} 	
		}
		
		
		if(okEvto == 1){
			$("#tabs-2").click();		
			$("#divAltaEvento").hide();
			$("#divConfiguracionGuia").show();
			$("#divConfiguracionCuentas").hide();
			$("#TIPO_CONFIGURACION1").attr('disabled','disabled');
			$("#TIPO_CONFIGURACION2").attr('disabled','disabled');
			$("#TIPO_CONFIGURACION3").attr('disabled','disabled');
			$("#btnGuardaEvento").attr('disabled','disabled');
			
			/*
			GUARDA EVENTO				
			 */
			/*Desactuvar los campos para escritura*/
			$("#nIdGrupoEvento").prop('readonly', true);
			$("#cNombreGrupo").prop('readonly', true);
			$("#nIdSubGrupoEvento").prop('readonly', true);
			$("#cNombreSubGrupo").prop('readonly', true);
			$("#cEvento").prop('readonly', true);
			$("#dEvento").prop('readonly', true);
			$("#cIdUnidadEjecutora").prop('disabled','disabled');
		} 
	}
	
	function GuardaGuia(){
		var okGuia = 1;
		
		if ($("#nGrupo").val() == ""){
			Swal.fire({ icon: "warning",
						text: "Campo Grupo Guia es obligatorio, favor de capturarlo."});			
			okGuia = 0;
		} else if ($("#dGrupoGuia").val() == ""){
			Swal.fire({ icon: "warning",
						text: "Campo Descripcion Grupo Guia es obligatorio, favor de capturarlo."});			
			okGuia = 0;
		} else if ($("#nSubGrupo").val() == ""){
			Swal.fire({ icon: "warning",
						text: "Campo Sub-Grupo Guia es obligatorio, favor de capturarlo."});			
			okGuia = 0;
		} else if ($("#cDescripcion").val() == ""){
			Swal.fire({ icon: "warning",
						text: "Campo Descripcion Sub-Grupo Guia es obligatorio, favor de capturarlo."});			
			okGuia = 0;
		} else if ($("#nConsecutivo").val() == ""){
			Swal.fire({ icon: "warning",
						text: "Campo Consecutivo es obligatorio, favor de capturarlo."});			
			okGuia = 0;
		} else if ($("#dConcepto").val() == ""){
			Swal.fire({ icon: "warning",
						text: "Campo Concepto es obligatorio, favor de capturarlo."});			
			okGuia = 0;
		} else if ($("#dDocumentoFuente").val() == ""){
			Swal.fire({ icon: "warning",
						text: "Campo Doc Fuente es obligatorio, favor de capturarlo."});			
			okGuia = 0;
		} else if ($("#dPeriodicidad").val() == ""){
			Swal.fire({ icon: "warning",
						text: "Campo Periodicidad es obligatorio, favor de capturarlo."});			
			okGuia = 0;
		}
		
		if(okGuia == 1){
			$("#tabs-3").click();
			$("#divAltaEvento").hide();
			$("#divConfiguracionGuia").hide();
			$("#divConfiguracionCuentas").show();
			$("#divCajaRC").show();
			$("#divPolManual").hide();
			$("#btnGuardaGuia").attr('disabled','disabled');
			
			/*
			GUARDAR GUIA
			
			 */
		
			/*Desactuvar los campos para escritura*/
			$("#nGrupo").prop('readonly', true);
			$("#dGrupoGuia").prop('readonly', true);
			$("#nSubGrupo").prop('readonly', true);
			$("#cDescripcion").prop('readonly', true);
			$("#nConsecutivo").prop('readonly', true);
			$("#dConcepto").prop('readonly', true);
			$("#dDocumentoFuente").prop('readonly', true);
			$("#dPeriodicidad").prop('readonly', true);
		}
	}
	
	function GuardaCuentas(){ 
		Swal.fire({ icon: "success",
					text: "Configuración del evento completada" });		
		reload();		
	}
	
	function guiaExistente(){
		if($("#ExisteGuia").prop("checked")){			
		//Si se activa el check se borran los datos para seleccionar una guia existente. Y se desctivan los campos para escritura.
			$("#nGrupo").val("");
			$("#dGrupoGuia").val("");
			$("#nSubGrupo").val("");
			$("#cDescripcion").val("");
											
			$("#nGrupo").prop('readonly', true);
			$("#dGrupoGuia").prop('readonly', true);
			$("#nSubGrupo").prop('readonly', true);
			$("#cDescripcion").prop('readonly', true);
									
			$("#btnCatGrupoGuia").removeAttr('disabled','disabled');
			$("#btnCatSubGrupoGuia").removeAttr('disabled','disabled');
			
		} else {			
			$("#nGrupo").val("");
			$("#dGrupoGuia").val("");
			$("#nSubGrupo").val("");
			$("#cDescripcion").val("");
			
			if($("#ExisteConsecutivo").prop("checked")){
				$("#ExisteConsecutivo").prop("checked", false);
				
				$("#nConsecutivo").val("");
				$("#dConcepto").val("");
				$("#dDocumentoFuente").val("");
				$("#dPeriodicidad").val("");
				
				$("#nConsecutivo").prop('readonly', false);
				$("#dConcepto").prop('readonly', false);
				$("#dDocumentoFuente").prop('readonly', false);
				$("#dPeriodicidad").prop('readonly', false);
				
				$("#btnCatConsecutivo").removeAttr('disabled','disabled');
			}
			
			$("#nGrupo").prop('readonly', false);
			$("#dGrupoGuia").prop('readonly', false);
			$("#nSubGrupo").prop('readonly', false);
			$("#cDescripcion").prop('readonly', false);					
			
			$("#btnCatGrupoGuia").attr('disabled','disabled');
			$("#btnCatSubGrupoGuia").attr('disabled','disabled');
			$("#btnCatConsecutivo").attr('disabled','disabled');
						
		}	
	}
	
	function consecutivoExistente(){
		if($("#ExisteConsecutivo").prop("checked")){
			if(!$("#ExisteGuia").prop("checked")){
				Swal.fire({ icon: "warning",
					text: "Debes Activar la casilla de evento existente para poder seleccionar un consecutivo existente." });
				$("#ExisteConsecutivo").prop("checked", false);		
			} else {
				$("#nConsecutivo").val("");
				$("#dConcepto").val("");
				$("#dDocumentoFuente").val("");
				$("#dPeriodicidad").val("");
				
				$("#nConsecutivo").prop('readonly', true);
				$("#dConcepto").prop('readonly', true);
				$("#dDocumentoFuente").prop('readonly', true);
				$("#dPeriodicidad").prop('readonly', true);
				
				$("#btnCatConsecutivo").removeAttr('disabled','disabled');
			}
		} else {
			$("#nConsecutivo").val("");
			$("#dConcepto").val("");
			$("#dDocumentoFuente").val("");
			$("#dPeriodicidad").val("");
			
			$("#nConsecutivo").prop('readonly', false);
			$("#dConcepto").prop('readonly', false);
			$("#dDocumentoFuente").prop('readonly', false);
			$("#dPeriodicidad").prop('readonly', false);
			
			$("#btnCatConsecutivo").attr('disabled','disabled');
		}			
	}

	function tblDblClickG(event){
        var aPos;
        var aData;
        aPos = oTablaGrupoGuia.fnGetPosition(event.target.parentNode); /*Toma la posición del renglón doble click*/
        aData = oTablaGrupoGuia.fnGetData(aPos); /*Toma los datos de acuerdo a la posición y genera arreglo*/
        
        $("#nGrupo"   	).val(aData[0]);
        $("#dGrupoGuia" ).val(aData[1]);        	
        
		modalGrupoGuia.hide();		
   	}

	function tblDblClickSG(event){
        var aPos;
        var aData;
        aPos = oTablaSubGrupoGuia.fnGetPosition(event.target.parentNode); /*Toma la posición del renglón doble click*/
        aData = oTablaSubGrupoGuia.fnGetData(aPos); /*Toma los datos de acuerdo a la posición y genera arreglo*/
        
        $("#nSubGrupo"    ).val(aData[1]);
        $("#cDescripcion" ).val(aData[2]);        	
        
		modalSubGrupoGuia.hide();		
   	}

	function tblDblClickC(event){
        var aPos;
        var aData;
        aPos = oTablaConsecutivo.fnGetPosition(event.target.parentNode); /*Toma la posición del renglón doble click*/
        aData = oTablaConsecutivo.fnGetData(aPos); /*Toma los datos de acuerdo a la posición y genera arreglo*/
        
        $("#nConsecutivo"     ).val(aData[2]);
        $("#dConcepto" 		  ).val(aData[3]);        	
		$("#dDocumentoFuente" ).val(aData[4]);
		$("#dPeriodicidad"    ).val(aData[5]);
        
		modalConsecutivo.hide();		
   	}

	function tblDblClickCta(event){
        var aPos;
        var aData;
        aPos = oTablaCuentas.fnGetPosition(event.target.parentNode); /*Toma la posición del renglón doble click*/
        aData = oTablaCuentas.fnGetData(aPos); /*Toma los datos de acuerdo a la posición y genera arreglo*/
        
        $("#nCuenta" ).val(aData[0]);
        $("#dCuenta" ).val(aData[1]);        	
		        
		modalCuentas.hide();		
   	}

	function tblDblClickP(event){
        var aPos;
        var aData;
        aPos = oTablaPartida.fnGetPosition(event.target.parentNode); /*Toma la posición del renglón doble click*/
        aData = oTablaPartida.fnGetData(aPos); /*Toma los datos de acuerdo a la posición y genera arreglo*/
        
        $("#cPartida" ).val(aData[0]);
        $("#dPartida" ).val(aData[1]);        	
		        
		modalPartida.hide();		
   	}

	function cambioTipoPoliza(){
		$("#cTipoPoliza").attr('disabled', 'disabled');
	}
	
	function AgregarCuentasCRC(){		
		$("#CAJA").attr('disabled','disabled');
		$("#REINTEGROCAJA").attr('disabled','disabled');
		$("#POLIZAMANUAL").attr('disabled','disabled');
		
		var okCtaCRC = 1;
				
		if ($("#cTipoPoliza").val() == "-1"){
			Swal.fire({ icon: "warning",
						text: "Favor de seleccionar un Tipo de Poliza."});			
			okCtaCRC = 0;
		} else if ($("#nCuenta").val() == ""){
			Swal.fire({ icon: "warning",
						text: "Campo Cuenta es obligatorio, favor de seleccionar una."});			
			okCtaCRC = 0;
		} else if ($("#cTipoMovimiento").val() == "-1"){
			Swal.fire({ icon: "warning",
						text: "Favor de seleccionar un Tipo Movimiento."});			
			okCtaCRC = 0;
		}		
		
		if(okCtaCRC == "1"){
			Swal.fire({ icon: "succes",
						text: "Cuenta Agregada para guardar"});	
		}
	}
	
	function AgregarCuentasPM(){
		$("#CAJA").attr('disabled','disabled');
		$("#REINTEGROCAJA").attr('disabled','disabled');
		$("#POLIZAMANUAL").attr('disabled','disabled');	
		
		var okCtaPM = 1;
		
		if ($("#cTipoPoliza").val() == "-1"){
			Swal.fire({ icon: "warning",
						text: "Favor de seleccionar un Tipo de Poliza."});			
			okCtaPM = 0;
		} else if ($("#nCuenta").val() == ""){
			Swal.fire({ icon: "warning",
						text: "Campo Cuenta es obligatorio, favor de seleccionar una."});			
			okCtaPM = 0;
		} else if ($("#cTipoMovimiento").val() == "-1"){
			Swal.fire({ icon: "warning",
						text: "Favor de seleccionar un Tipo Movimiento."});			
			okCtaPM = 0;
		} else if ($("#cPartida").val() == ""){
			Swal.fire({ icon: "warning",
						text: "Campo Partida es obligatorio, favor de seleccionar una."});			
			okCtaPM = 0;
		}
		
		if(okCtaPM == "1"){
			Swal.fire({ icon: "succes",
						text: "Cuenta Agregada para guardar"});	
		}		
	}