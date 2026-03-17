var sWhere ="";
var es_mx = {
		sProcessing : "Procesando...",
		sLengthMenu : "Mostrar _MENU_ registros",
		sZeroRecords : "No hay registros a mostrar",
		sEmptyTable : "No hay datos en la tabla",
		sLoadingRecords : "Cargando...",
		sInfo : "Registros _START_ al _END_ de _TOTAL_",
		sInfoEmpty : "Registro 0 al 0 de 0",
		sInfoFiltered : "(filtered from _MAX_ total entries)",
		sInfoPostFix : "",
		sInfoThousands : ",",
		sSearch : "Filtro:",
		oPaginate : {
			sFirst : "Primero",
			sPrevious : "Ant.",
			sNext : "Sigte.",
			sLast : "&Uacute;ltimo"
		}	
	
	};
let modalDetalle;
	
$(document).ready(function() {
	sWhere = "nidEmpleado = 0";
	creaTableAgenda();
	creaTablaSolicitud();
	modalDetalle = new bootstrap.Modal(document.getElementById('dialog-Detalle'), 'data-bs-backdrop');
	
	$("#solicitudes").hide();
});
	

	
	function creaTableAgenda(){
		
		var oTable = $('#tablaAgenda').dataTable(
			{
				"bPaginate" : false,
				"bFilter" : false,
				"bSort" : true,
				"bInfo" : false,
				"bAutoWidth" : false,
				"bJQueryUI" : true,
				"bRetrive" : true,
				"bDestroy" : true,
				"bServerSide" : true,
				sAjaxSource : window.location.protocol + "//"
						+ window.location.host + "/"
						+ window.location.pathname.split("/")[1]
						+ "/crud?rt=t&ql=v_resumenAgendas&qw=" + sWhere ,
				aoColumns : [ {
					sName : "nidComision"
				}, {
					sName : "Nombre"
				}, {
					sName : "TotalComision"
				}, {
					sName : "anticipo"
				}, {
					sName : "pagos"
				}, {
					sName : "icon"
				}],
				oLanguage : es_mx
			});
			
		$("#tablaAgenda tbody").dblclick(
			function(event) {
				
				$(oTable.fnSettings().aoData).each(
					function() {
						$(this.nTr).removeClass('row_selected');
					}
				);

				$(event.target.parentNode).addClass('row_selected');
				aPos = oTable.dataTable().fnGetPosition(event.target.parentNode);
				
				var aData = oTable.fnGetData(aPos);
				f = aData[0];
				
				$("#idComision").val(f);
				$("#solicitudes").show();
				//creaTablaSolicitud();
		} )			
	}
		
	function consultarAgenda() {
		
		if ($("#cIdRFC").val()!="") {
			sWhere = "cRFC = '" + $("#cIdRFC").val()  + "'";
		} 
		
		creaTableAgenda();
	}		
	
	
function creaTablaSolicitud(){
		sWhere ="nidComisionModulo =" + $("#idComision").val();
		oTableBoleto = $('#tablaSolicitud').dataTable(
			{
				"bPaginate" : false,
				"bFilter" : false,
				"bSort" : true,
				"bInfo" : false,
				"bAutoWidth" : false,
				"bJQueryUI" : true,
				"bRetrive" : true,
				"bDestroy" : true,
				"bServerSide" : true,
				sAjaxSource : window.location.protocol + "//"
						+ window.location.host + "/"
						+ window.location.pathname.split("/")[1]
						+ "/crud?rt=t&ql=v_RelacionComision&qw=" + sWhere ,
				aoColumns : [ {
					sName : "id"
				},  {
					sName : "dEvento"
				}, {
					sName : "viaticos"
				}, {
					sName : "transporte"
				},{
					sName : "total"
				},{
					sName : "Anticipo"
				},{
					sName : "Devolucion"
				},{
					sName : "estatus"
				}],
				oLanguage : es_mx
			});			
			
	}	
	
	function cat_beneficiario(){
		$("#cTipoRfc").val("3");
		window.open('CatalogoBeneficiariosRG.jsp','Beneficiarios', 'status=1, width=900px, height=430px, left=100px, resizable=yes');
		
	}
	
	function cargaCtaBancariaRFC  () {
		$("#cIdRFC").val($("#cIdRFC_RelacionGasto").val());
	}
	
	function addCommas(nStr){
            nStr += '';
            x = nStr.split('.');
            x1 = x[0];
            x2 = x.length > 1 ? '.' + x[1] : '';
            var rgx = /(\d+)(\d{3})/;
            while (rgx.test(x1)) {
               x1 = x1.replace(rgx, '$1' + ',' + '$2');
            }
            return x1 + x2.substring(0,3);
     }
     
	
	function consultaSolicitud(idComision){

			$("#idComision").val(idComision);
			queryFormPost("leeValoresInicialesViaticos", {async: false});
			queryFormPost("leeTotalesAgenda", {async: false});
			queryFormPost("leeTotalesTransporte", {async: false});
			
			creaTableAgendaConsulta();
			creaTableTransporteConsulta();
			creaTableAutoriza();
			
			let totalAgenda = $("#totalAgenda").val();
			let totalTransporte = $("#totalTransporte").val();
			let totalGral = (parseFloat(totalAgenda) + parseFloat(totalTransporte)).toFixed(2);
			
			totalAgenda = addCommas(totalAgenda);
			$("#totalAgenda").val(totalAgenda);
			
			totalTransporte = addCommas(totalTransporte);
			$("#totalTransporte").val(totalTransporte);
			
			totalGral = addCommas(totalGral);
			$("#totalGeneral").val(totalGral);
			
			$("#nidComision").val($("#idComision").val());
			modalDetalle.show();
		
}	
