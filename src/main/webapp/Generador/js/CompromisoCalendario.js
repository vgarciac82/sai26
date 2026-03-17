
			let vista = "v_saldoDisponibleAcc";
			
			$(document).ready(function() {
				
				queryFormPost("tEjercicioRead",{async: false });
				$("#cEjercicio").val( $("#aEjercicioFiscal").val() );
				
				const options = {
						bRetrieve: true,
				        language: es_mx,
				        paging: false,
				        searching: false,
				        autoWidth: false,
				         "bInfo" : false
				    };
				modalFirmantes = new bootstrap.Modal(document.getElementById('firmantesModal'), 'data-bs-backdrop');
				 $('#tablaReducciones').DataTable(options);
				 $('#tablaAmpliaciones').DataTable(options);
				 $('#tablaCalendario').DataTable(options);
				 $('#tablaValidacion').DataTable(options);
				  
				 
				  $('#tablaReducciones tbody').on('dblclick', 'tr', function () {
					  const ep = $(this).find('td:first').text();
					  var ene = $(this).find('td:eq(2)').html();
					  var feb = $(this).find('td:eq(3)').html();
					  var mar = $(this).find('td:eq(4)').html();
					  var abr = $(this).find('td:eq(5)').html();
					  var may = $(this).find('td:eq(6)').html();
					  var jun = $(this).find('td:eq(7)').html();
					  var jul = $(this).find('td:eq(8)').html();
					  var ago = $(this).find('td:eq(9)').html();
					  var sep = $(this).find('td:eq(10)').html();
					  var oct = $(this).find('td:eq(11)').html();
					  var nov = $(this).find('td:eq(12)').html();
					  var dic = $(this).find('td:eq(13)').html();
					  console.log(ep, ene);
				    abrirModalDetalle(ep, 'reduccion', ene, feb, mar, abr, may, jun, jul, ago, sep, oct, nov, dic);
				    
				  });

				  $('#tablaAmpliaciones tbody').on('dblclick', 'tr', function () {
				    const ep = $(this).find('td:first').text();
				      var ene = $(this).find('td:eq(2)').html();
				      var feb = $(this).find('td:eq(3)').html();
					  var mar = $(this).find('td:eq(4)').html();
					  var abr = $(this).find('td:eq(5)').html();
					  var may = $(this).find('td:eq(6)').html();
					  var jun = $(this).find('td:eq(7)').html();
					  var jul = $(this).find('td:eq(8)').html();
					  var ago = $(this).find('td:eq(9)').html();
					  var sep = $(this).find('td:eq(10)').html();
					  var oct = $(this).find('td:eq(11)').html();
					  var nov = $(this).find('td:eq(12)').html();
					  var dic = $(this).find('td:eq(13)').html();
				    abrirModalDetalle(ep, 'ampliacion', ene, feb, mar, abr, may, jun, jul, ago, sep, oct, nov, dic);
				  });
				  
				  queryFormPost("existeCompromisoCalendario", {async: false});
				  
				  if ($('#existeComp').val() > 0) {
					  queryFormPost("consultaCompromisoCalendario", {async: false});
					  cargarEpReduccion();
					  cargarEpAmpliacion();
					  cargarMovimientos();
					  validacionCalendario();
					 // $('#btnContrato').hide(); 
				  } else {
					  $('#btnContrato').show();
				  }
				  
				  $('#cIdContrato').change(function() {
					  cargarEpReduccion();
					  cargarEpAmpliacion();
					  cargarMovimientos();
					  validacionCalendario();
				   });
			});
			
			function cambiarVista() {
				var tipoVista=$("input[name='checkClave']:checked").val();
				
				  if (tipoVista=="INTERNA" ) {
				    vista = "v_saldoDisponibleCuentaCorta";
				    cargarEpAmpliacion();
				  } else {
				     vista = "v_saldoDisponibleAcc";
				     cargarEpAmpliacion();
				  }
				
			}
			function abrirModalDetalle(ep, tipo, ene, feb, mar, abr, may, jun, jul, ago, sep, oct, nov, dic) {
				  
				$('#epSeleccionado').text(ep);

				  const tipoLabel = tipo === 'reduccion' ? 'Reducción' : 'Ampliación';
				  const tipoBase = tipo === 'reduccion' ? 'Compromiso' : 'Disponible';

				  $('#columnaComp').text(tipoBase);
				  $('#columnaCaptura').text(tipoLabel);

				  const meses = ['Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio',
				                 'Julio', 'Agosto', 'Septiembre', 'Octubre', 'Noviembre', 'Diciembre'];

				  const tbody = $('#tablaDetalleMensual tbody');
				  tbody.empty();

				  meses.forEach((mes, index) => {
				    const fila = `
				      <tr>
				        <td>${mes}</td>
				        <td>
				          <input type="number" readonly class="form-control baseValor" id="base_${index}" value="0">
				        </td>
				        <td>
				          <input type="number" class="form-control capturaValor" id="captura_${index}" value="0">
				        </td>
				      </tr>`;
				    tbody.append(fila);
				  });
				  
				  const total = `
				      <tr>
				        <td>Total</td>
				        <td></td>
				        <td><input type="number" class="form-control capturaValor" id="captura_total" value = "0" readonly></td>
				      </tr>`;
				  tbody.append(total);
				  
				  $('#base_0').val(ene);
				  $('#base_1').val(feb);
				  $('#base_2').val(mar);
				  $('#base_3').val(abr);
				  $('#base_4').val(may);
				  $('#base_5').val(jun);
				  $('#base_6').val(jul);
				  $('#base_7').val(ago);
				  $('#base_8').val(sep);
				  $('#base_9').val(oct);
				  $('#base_10').val(nov);
				  $('#base_11').val(dic);

				  tbody.on('input', '.capturaValor', function () {
				    const row = $(this).closest('tr');
				    const base = parseFloat(row.find('.baseValor').val()) || 0;
				    const valor = parseFloat($(this).val()) || 0;
				    var esValido;
				    if (tipo === 'reduccion') {
				    	esValido = (valor <= 0) && (Math.abs(valor) <= base);	
				    } else {
				    	esValido = (valor => 0) && (Math.abs(valor) <= base);
				    }
				    
				    $(this).toggleClass('is-invalid', !esValido);
				    
				  });

				  tbody.on('blur', '.capturaValor', function () {
					  	const row = $(this).closest('tr');
					    const base = parseFloat(row.find('.baseValor').val()) || 0;
					    const valor = parseFloat($(this).val()) || 0;
					    var esValido;
					    
					    $('#captura_total').val( Number($('#captura_0').val() ) 
					    						+ Number($('#captura_1').val()) 
					    						+ Number($('#captura_2').val())
					    						+ Number($('#captura_3').val())
					    						+ Number($('#captura_4').val())
					    						+ Number($('#captura_5').val())
					    						+ Number($('#captura_6').val())
					    						+ Number($('#captura_7').val())
					    						+ Number($('#captura_8').val())
					    						+ Number($('#captura_9').val())
					    						+ Number($('#captura_10').val())
					    						+ Number($('#captura_11').val()) 
					    						);
					    
					    if ($('#columnaComp').text() == 'Reducción') {
					    	esValido = (valor <= 0) && (Math.abs(valor) <= base);	
					    } else {
					    	esValido = (valor => 0) && (Math.abs(valor) <= base);
					    }

					  if (!esValido)   {
					    	Swal.fire('Modifique el importe', 'El importe capturado NO puede ser mayor al COMPROMISO / DISPONIBLE' , 'error');
					  } 
					});
				  
				  $('#modalDetalle').modal('show');
			}

			function consultaContratos(){
				
				window.open('ConsultaContratos.jsp','Contratos', 'status=1, width=900px, height=430px, left=100px, resizable=yes');
				
			}
			
			function guardarEncabezado() {
				queryFormPost("existeCompromisoCalendario", {async: false});
				
				if ($('#existeComp').val() == 0 ) {
					queryFormPost("cargarEncabezadoCalendario", {async: false});	
				} else {
					queryFormPost("eliminaCompromisoCalendario", {async: false});
					queryFormPost("cargarEncabezadoCalendario", {async: false});
					
				}
				
			}
			
			function guardarDetalle() {
				  const tipo = $('#columnaCaptura').text().toLowerCase().includes('reducción') ? 'reduccion' : 'ampliacion';
				  const ep = $('#epSeleccionado').text();
				  const datos = [];
				  const folio = $('#nFolioPago').val();

				  $('#tablaDetalleMensual tbody tr').each(function (index) {
				    const mes = index + 1; 
				    const base = parseFloat($(this).find('.baseValor').val()) || 0;
				    const captura = parseFloat($(this).find('.capturaValor').val()) || 0;

				    if (mes <= 12) {
					    if (captura !== 0) {
					      datos.push({
					        mes,
					        base,
					        captura
					      });
					    }
				    }
				  });

				  if (datos.length === 0) {
				    Swal.fire('Error', 'Debes capturar al menos un valor válido.', 'error');
				    return;
				  }

				  $.ajax({
					    url: '../compromiso/guardarCalendarioServlet',
					    method: 'post',
					    contentType: 'application/json',
					    data: JSON.stringify({folio, ep, tipo, datos }),
					    success : function() {
								Swal.fire('OK!', 'Datos guardados correctamente.', 'success');
							      $('#modalDetalle').modal('hide');
							      cargarMovimientos();
							      validacionCalendario();
						},
						error : function(err) {
							let mensaje = 'Ocurrió un error inesperado.';
						    if (err.responseJSON && err.responseJSON.error) {
						        mensaje = err.responseJSON.error;
						    }
						    Swal.fire('Error', mensaje, 'error');
						}
				  });
				}

			const SYSTEM_URL =  window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1]; 
			function cargarEpReduccion() {
				let contrato = $("#cIdContrato").val().trim();
		    	var oTable = new DataTable('#tablaReducciones', {

		    		ajax: {
		    			url: SYSTEM_URL + "/crud?rt=nt&ql=V_SALDOCOMPROMISOS&qw=SUBSTRING(EP,40,1) !=4 AND cIdContrato ='" + contrato + "'",
		    			type: 'POST'
		    		},
		    		columns: [
		    			{ data: "ep", 					visible: true, searchable: true, orderable: true },
		    			{ data: "mAnual", 				visible: true, searchable: false, orderable: false },
		    			{ data: "mCompromisoEnero", 	visible: true, searchable: false, orderable: false },
		    			{ data: "mCompromisoFebrero", 	visible: true, searchable: false, orderable: false },
		    			{ data: "mCompromisoMarzo", 	visible: true, searchable: false, orderable: false },
		    			{ data: "mCompromisoAbril", 	visible: true, searchable: false, orderable: false },
		    			{ data: "mCompromisoMayo", 		visible: true, searchable: false, orderable: false },
		    			{ data: "mCompromisoJunio", 	visible: true, searchable: false, orderable: false },
		    			{ data: "mCompromisoJulio", 	visible: true, searchable: false, orderable: false },
		    			{ data: "mCompromisoAgosto", 	visible: true, searchable: false, orderable: false },
		    			{ data: "mCompromisoSeptiembre", visible: true, searchable: false, orderable: false },
		    			{ data: "mCompromisoOctubre", 	visible: true, searchable: false, orderable: false },
		    			{ data: "mCompromisoNoviembre", visible: true, searchable: false, orderable: false },
		    			{ data: "mCompromisoDiciembre", visible: true, searchable: false, orderable: false }
		    		],
		    		order: [[1, 'asc']],
		    		rowId: 'ep',
		    		processing: true,
		    		serverSide: true,
		    		scrollCollapse: true,
		    		scrollY: '200px',
		    		language: es_mx,
		    		paging: false,
		    		searching: true,
		    		bDestroy: true,
		    		"bInfo" : false
		    	});
			}
			
			function cargarEpAmpliacion() {
				let contrato = $("#cIdContrato").val().trim();
				
		    	var oTable = new DataTable('#tablaAmpliaciones', {

		    		ajax: {
		    			url: SYSTEM_URL + "/crud?rt=nt&ql=" + vista + "&qw=cIdContrato = '" + contrato + "'",
		    			type: 'POST'
		    		},
		    		columns: [
		    			{data: "ep", 		visible: true, searchable: true, orderable: true},
		                {data: "total", 	visible: true, searchable: false, orderable: true},
		                { data: "Enero", 	visible: true, searchable: false, orderable: false },
		    			{ data: "Febrero", 	visible: true, searchable: false, orderable: false },
		    			{ data: "Marzo", 	visible: true, searchable: false, orderable: false },
		    			{ data: "Abril", 	visible: true, searchable: false, orderable: false },
		    			{ data: "Mayo", 	visible: true, searchable: false, orderable: false },
		    			{ data: "Junio", 	visible: true, searchable: false, orderable: false },
		    			{ data: "Julio", 	visible: true, searchable: false, orderable: false },
		    			{ data: "Agosto", 	visible: true, searchable: false, orderable: false },
		    			{ data: "Septiembre", visible: true, searchable: false, orderable: false },
		    			{ data: "Octubre", 	visible: true, searchable: false, orderable: false },
		    			{ data: "Noviembre", visible: true, searchable: false, orderable: false },
		    			{ data: "Diciembre", visible: true, searchable: false, orderable: false }
		    		],
		    		order: [[1, 'asc']],
		    		rowId: 'ep',
		    		processing: true,
		    		serverSide: true,
		    		scrollCollapse: true,
		    		scrollY: '200px',
		    		language: es_mx,
		    		paging: false,
		    		searching: true,
		    		bDestroy: true,
		    		"bInfo" : false
		    	});
			}
			
			function cargarMovimientos() {
				queryFormPost("sumaClavesCalendario", {async: false});
				
				let folioComp = $("#nFolioPago").val().trim();
		    	var oTable = new DataTable('#tablaCalendario', {

		    		ajax: {
		    			url: SYSTEM_URL + "/crud?rt=nt&ql=vCompromisoCambioCalendario&qw=cTipoPago = 'COMPROMISO' AND nFolioPago =" + folioComp ,
		    			type: 'POST'
		    		},
		    		columns: [
		    			{ data: "EP", 	visible: true, searchable: true, orderable: false },
		    			{ data: "nMes", visible: true, searchable: true, orderable: false },
		    			{ data: "mImporteBruto", visible: true, searchable: false, orderable: false },
		    			{ data: "borrar", visible: mostrarBorrar, searchable: false, orderable: false }
		    		],
		    		order: [[1, 'asc']],
		    		rowId: 'ep',
		    		processing: true,
		    		serverSide: true,
		    		scrollCollapse: true,
		    		scrollY: '200px',
		    		language: es_mx,
		    		paging: false,
		    		searching: false,
		    		bDestroy: true,
		    		info : false
		    	});
			}
			
			function validacionCalendario() {
				
				let folioComp = $("#nFolioPago").val().trim();
		    	var oTable = new DataTable('#tablaValidacion', {

		    		ajax: {
		    			url: SYSTEM_URL + "/crud?rt=nt&ql=vCompromisoCalendarioValidar&qw=nFolioPago =" + folioComp ,
		    			type: 'POST'
		    		},
		    		columns: [
		    			{ data: "clavecorta", visible: true, searchable: true, orderable: false },
		    			{ data: "nMes", visible: true, searchable: false, orderable: false },
		    			{ data: "sumaImporte", visible: true, searchable: false, orderable: false }
		    		],
		    		order: [[1, 'asc']],
		    		rowId: 'ep',
		    		processing: true,
		    		serverSide: true,
		    		scrollCollapse: true,
		    		scrollY: '200px',
		    		language: es_mx,
		    		paging: false,
		    		searching: false,
		    		bDestroy: true,
		    		info : false,
		    	});
			}
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
			 
			 
			    function infoEmpleado( tipoFirmante ){
			    	var postFijo;
					if( "AUT" == tipoFirmante){
						numeroEmpleado = $("#cboAutoriza").val();
						postFijo = "Aut";
					}else if( "SUPAUT" == tipoFirmante){
						numeroEmpleado = $("#cboSuplenteAut").val();
						postFijo = "Titular";
					}
					
					limpiaFirmante(postFijo);
					
					if( parseInt( numeroEmpleado, 10 ) > 0 ){
						$("#nNumEmpleadoBusqueda").val( numeroEmpleado );		
						queryFormPost({ queryName:"infoComplementariaFirmanteRead", 
						                    async:false,
						              });
					} 
				}
				
			    var breturnVal = false;
			    function procesar(){
					const folioPago =$('#nFolioPago').val();
					const cxp = $('#caNoCompromiso').val();
					const folio =$('#folio').val();
			    	 $.ajax({
						    url: '../compromiso/guardarCompromiso',
						    method: 'post',
						    dataType : 'json',
						    data: { folioPago, cxp , folio},
						    success : function() {
									Swal.fire('OK!', 'Datos guardados correctamente.', 'success');
								      $('#modalDetalle').modal('hide');
								      cargarMovimientos();
								      validacionCalendario();
								      breturnVal= true;
							},
							error : function(err) {
								let mensaje = 'Ocurrio un error inesperado.';
							    if (err.responseText !="") {
							        mensaje = err.responseText;
							    }
							    Swal.fire('Verifique!', mensaje, 'error');
							}
					  });
			    	 
					return breturnVal;
				}
			    
			    function borrarRegistro(folio, ep, mes){
					
			    	 $.ajax({
						    url: '../compromiso/eliminarRenglon',
						    method: 'post',
						    dataType : 'json',
						    data: {folio, ep, mes },
						    success : function() {
									Swal.fire('Ok!', 'Datos eliminados correctamente.', 'success');
								      cargarMovimientos();
								      validacionCalendario();
							},
							error : function(err) {
								let mensaje = 'Ocurrio un error inesperado.';
							    if (err.responseJSON && err.responseJSON.error) {
							        mensaje = err.responseJSON.error; 
							    }
							    Swal.fire('Error', mensaje, 'error');
							}
					  });
			    	 
					
				}
			    
			    function limpiaFirmante(postFijo){
					$("#cNombreEmpleado").val( "" );
			        $("#cPaternoEmpleado").val( "" );
			        $("#cMaternoEmpleado").val( "" );
			        $("#cPuestoEmpleado").val( "" );
				}
				
			    
				function abrirModalFirmantes(contrato, id, esIntegrada){
					$("#cContrato").val(contrato)
					$("#idCompromiso").val(id)
					$('#esIntegrado').val(esIntegrada);
					modalFirmantes.show();
					
				}
				
				function imprimirNotaNuevo(){
					queryFormPost("existeFirmanteCompromisoRead", {async: false});
					
					if ($("#tieneFirmante").val() == 0) {
						queryFormPost("guardarFirmanteCompromiso", {async: false});	
					} else {
						queryFormPost("actualizarFirmanteCompromiso", {async: false});	
					}
					
					window.open("../admin/SeguridadCatalogos?" 
						    + "catalogo=CONTRARECIBO"
							+ "&accion=run" 
							+ "&rn=NotaInformativaContratoNuevo.jasper" 
							+ "&where2=" + $("#cContrato").val()
							+ "&esIntegrada=" + $("#esIntegrado").val()
							+ "&nombre=" + $("#cNombreEmpleado").val() + ' ' +  $("#cPaternoEmpleado").val() + ' ' +  $("#cMaternoEmpleado").val()
							+ "&puesto=" +  $("#cPuestoEmpleado").val(),
							"popacuse", "scrollbars=1, resizable=yes, width=1024, height=768");
				}
			
				
		