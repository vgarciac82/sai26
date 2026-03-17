<%@page import="com.syc.sai.contabilidad.utils.db.CloseObject"%>
<%@page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" pageEncoding="utf-8"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@page import="com.syc.contable.core.Saldo"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.sql.Connection"%>
<%@page import="org.apache.log4j.Logger"%>
<%@page import="com.syc.sai.contabilidad.polizamanual.model.DocPolizaEncabezadoManager"%>
<%@page import="com.syc.sai.contabilidad.polizamanual.controller.DocPolizaEncabezadoBusinessLogic"%>
<%@page import="com.syc.sai.contabilidad.polizamanual.*"%>
<%!private final int POLIZA_POR_CUENTAS = 1;%>
<%!private final int POLIZA_POR_EVENTOS = 2;%>
<%!private Logger log = Logger.getLogger(getClass());%>
<%
	//boolean bAplicadoCont = false;
	String DATE_FORMAT = "dd/MM/yyyy";
	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	
	Calendar c1 = Calendar.getInstance();
	Calendar c2 = Calendar.getInstance(); // today
	String fechaMaxima ="";
	String fechaMinima ="";
	String today = sdf.format(c1.getTime());
	String cCentroContable = "";
	String cUR = "";
	String cRamo = "";
	String nomUsuario = "";
	String nomLargoUsuario = "";
	String folio_Resp="";
	String folioDoc="";
	Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
	String Meses[] = { "ENERO", "FEBRERO", "MARZO", "ABRIL", "MAYO",
					"JUNIO", "JULIO", "AGOSTO", "SEPTIEMBRE", "OCTUBRE",
					"NOVIEMBRE", "DICIEMBRE" };
	String Control[] = {"EjercicioFiscal","RamoEP",
			"UnidadResponsableEP",
			"GrupoFuncional","Funcion","SubFuncion","ProgramaGeneral","ActividadInstitucional",
			"ProgramaPresupuestario","Partida",
			"TipoGasto","FuenteFinanciamiento","EntidadFederativa","Cartera","UnidadNormativa",
			"cUnidadEjecutora"
			};
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	if (usuario.getPropiedades() != null&& usuario.getPropiedades().containsKey("CCENTROCONTABLE")) {
		cCentroContable = usuario.getPropiedad("CCENTROCONTABLE").getValor();
	}

	//VGC20150702 cuando es un usuario de admin, valida contra los beneficiarios tanto de financieros como de RRHH
	boolean esAdminContabilidad = usuario.getPropiedad("ROL_CONTABLE") != null && "ADMIN_CONTABILIDAD".equalsIgnoreCase(usuario.getPropiedad("ROL_CONTABLE").getValor());
	/*VGC20160104 Se cambia la fecha de aplicacion segun el EF activo*/
	AdecuacionBusinessLogic adbl = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);
	int efa =  Integer.parseInt( adbl.obtenEjercicioFiscal() );
	
	boolean cPolizaManualCuenta = false;
	boolean cPolizaManualEventos = false;
	
	cPolizaManualCuenta =(usuario.getPropiedades().containsKey("CPOLIZAMANUALCUENTA") && usuario.getPropiedad("CPOLIZAMANUALCUENTA").getValor().contentEquals("SI"));
	cPolizaManualEventos =(usuario.getPropiedades().containsKey("CPOLIZAMANUALEVENTOS") && usuario.getPropiedad("CPOLIZAMANUALEVENTOS").getValor().contentEquals("SI"));
	

	if (  c == null || cCentroContable==null )
	{
		response.sendRedirect("../index.jsp");
		return;
	}

	int id_oper = -1;
	
	if (request.getParameter("id_oper") != null)
		id_oper = new Integer(request.getParameter("id_oper")).intValue();
	else
		id_oper = c.getCasoOperacion(0).getIdOperacion();
	
	CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
	cUR = usuario.getU_UR();
	cRamo = usuario.getU_Ramo();
	nomUsuario = usuario.getLogin();
	nomLargoUsuario = usuario.getNombre();
	try {
		Connection conn = cbl.getConnection();
		CasoDato cd = new CasoDato();
		cd.setIdCaso(c.getIdCaso());
		c.setCasoDato(CasoDatoManager.select(conn, cd));
		conn.close();
		conn = null;
	} catch (Exception expropcte) {
		log.error("Error leyendo CasoDato: ", expropcte);
	}
	

	/*****  FOLIO DE RECARGA DESPEUS DE CARGAR PORLIZA POR LAYAOUT  ******/
	if(session.getAttribute("FOLIO_POLIZA")!=null )
	{
	
	System.out.println("entra por que regreso de cargar poliza ");
	 folio_Resp=session.getAttribute("FOLIO_POLIZA").toString();
	 System.out.println("folio respuesta: "+folio_Resp);
	 folioDoc=folio_Resp;
	 session.removeAttribute("FOLIO_POLIZA");
	}
	else
	folioDoc=request.getParameter("folio");
	
	/*  -------------------------------------------      */

	/****************DISPATCHER*****************/
	
	String folio = null;	
	
	if (c.getCasoDato("FOLIO") != null) 
		folio = c.getFolio();
	else
		folio=folio_Resp;
		

	int nFolioDocPoliza = new Integer(folio.split("-")[2]);
		String restrictions = "nFolioDocPoliza=" + nFolioDocPoliza;
		
	boolean capxCuentas=false;	
	boolean polNueva=true;	
	int nmesDocumento=0;
	int mesAbierto=DocPolizaEncabezadoManager.readnMesAbierto(new DocPolizaEncabezadoBusinessLogic().getConnection(), cCentroContable, cUR);
	String fechaAplicacion="";
	String fCaptura="";
	
	
	List<DocPolizaEncabezado> l = DocPolizaEncabezadoManager.readDocPolizaEncabezadoBy(new DocPolizaEncabezadoBusinessLogic().getConnection(), restrictions);
	
  	boolean UAUTORIZADO=false;/*temporal para saber si tiene permisos de crear/abrir algun tipo de poliza*/
  	boolean POLIZANUEVA=true;
  	int     FORMATOPOLIZA=1;	
  	if( efa != c2.get(Calendar.YEAR) ){
  		c2.set(Calendar.YEAR, efa);
  		c2.set(Calendar.MONTH, 11);
  		c2.set(Calendar.DAY_OF_MONTH, 31);
  	}
    //Rango de Fechas Permitidas
	c2.set(c2.get(Calendar.YEAR), ( mesAbierto == 13 ? 11 : mesAbierto - 1) ,1);
	fechaMinima = efa+","+(c2.get(Calendar.MONTH))+","+c2.get(Calendar.DAY_OF_MONTH);
	fechaMaxima = efa+","+(c2.get(Calendar.MONTH))+","+c2.getActualMaximum(Calendar.DAY_OF_MONTH);
    
if (!l.isEmpty()) 
	{  		
  		DocPolizaEncabezado dpe = l.get(0);
  		
  		if(dpe.getnFormatoPoliza() != null  )
  		{
  			System.out.println("Formato de polizaEncabezado: "+dpe.getnFormatoPoliza() );
  			switch (dpe.getnFormatoPoliza())
				{			
					case POLIZA_POR_CUENTAS:				
						if(cPolizaManualCuenta)
						{
							UAUTORIZADO=true;	
							cPolizaManualEventos=false;					
						}
						break;	
					case POLIZA_POR_EVENTOS:						
						if(cPolizaManualEventos )
						{
							FORMATOPOLIZA=POLIZA_POR_EVENTOS;
							UAUTORIZADO=true;						
						}
						break;
				}  	
  			
  			if(UAUTORIZADO)
	  			{
		  			nmesDocumento=l.get(0).getNmes();
					fechaAplicacion=l.get(0).getFaplicacion();
					fCaptura=l.get(0).getFcarga();
				    String split=(fechaAplicacion.contains("/")?"/":"-");
					//*verificar el codigo de abajo, esta muy feo*//
		  			fechaAplicacion=fechaAplicacion.split(split)[2]+"/"+fechaAplicacion.split(split)[1]+"/"+fechaAplicacion.split(split)[0];
		  			fCaptura=fCaptura.split(split)[2]+"/"+fCaptura.split(split)[1]+"/"+fCaptura.split(split)[0];		  					
	  			}  			
  		}
  		
  		POLIZANUEVA=false;  	  		
  	}
  	else
  	{ 	// sin registros  y id_oper=1
  		if(cPolizaManualCuenta || cPolizaManualEventos)
  		{
  			UAUTORIZADO=true;
  			int nmesAplicacion=0;  			
  			
  			fCaptura=sdf.format(c1.getTime());
			int mesActual=c1.get(Calendar.MONTH)+1;	
			System.out.println("Mes Actual:"+mesActual);		
			if(mesAbierto!=mesActual)
			{
				c1.set(Calendar.MONTH, ( mesAbierto == 13 ? 11 : mesAbierto-1 )  );	
				if(mesAbierto==2)// trae el ultimo dia de febrero
					c1.set(Calendar.DAY_OF_MONTH, c1.getLeastMaximum(Calendar.DAY_OF_MONTH));
				else		
					c1.set(Calendar.DAY_OF_MONTH, c1.getActualMaximum(Calendar.DAY_OF_MONTH));
			}
			
			/*VGC20160104 Se cambia la fecha de aplicacion segun el EF activo*/	
			c1.set(Calendar.YEAR, efa);
			
			fechaAplicacion = sdf.format(c1.getTime()); 			
  		} 		
  	}
	
	try
	{
	System.out.println("Mes de Aplicacion:"+fechaAplicacion);
	System.out.println("Mes Abierto:"+mesAbierto);

	System.out.println("Mes del documento:"+nmesDocumento);
	System.out.println("Fecha de captura:"+fCaptura);
	//System.out.println("FormatoPoliza:"+FORMATOPOLIZA);
	//System.out.println("Poliza_POR_cuentas :"+POLIZA_POR_CUENTAS);
	System.out.println("Poliza Nueva :"+POLIZANUEVA);
	//System.out.println("cpolizamanualcuenta :"+cPolizaManualCuenta);
	//	System.out.println("Poliza_por_eventos :"+POLIZA_POR_EVENTOS);
	//System.out.println("cpolizamanualevento :"+cPolizaManualEventos);
	System.out.println("FOLIO RESP :"+folioDoc);
	System.out.println("Minimo:"+fechaMinima);
	System.out.println("Maximo:"+fechaMaxima);
	}
	catch(Exception r){r.printStackTrace();
	System.out.println(r.toString());
	}
	
	//validaciones de excel.
	String mensajeRetorno = (String) session.getAttribute("MENSAJE_CARGA");
	String band = (String) session.getAttribute("FLAG");
	
	if (mensajeRetorno != null) {
		session.removeAttribute("MENSAJE_CARGA");		
	}
	
	System.out.println("Mensaje de Retorno: "+mensajeRetorno);
	
	/*VGCFIEL*/
	String nNumEmpleado = usuario.getNumeroEmpleado();	
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<title>P&oacute;lizas</title>
		
		<link rel="stylesheet" type="text/css" href="../Ayudas/css/autocompleta.css"></link>
		<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
		<link rel="stylesheet" type="text/css" href="css/jquery-ui.min.css"></link>
		<link rel="stylesheet" type="text/css" href="css/datatables.min.css"></link>
		<link rel="stylesheet" type="text/css" href="css/bootstrap.min.css"></link>
		<link rel="stylesheet" type="text/css" href="css/sweetalert2.min.css"></link>
		<link rel="stylesheet" type="text/css" href="../Bootstrap/bootstrap-icons-1.9.1/bootstrap-icons.css">
			
		<script type="text/javascript" src="../Generador/js/bootstrap.bundle.min.js"></script>
		<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
		<script type="text/javascript" src="js/jquery.dataTables.js"></script>
		<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
		<script type="text/javascript" src="js/jquery.ui.core.js"></script>
		<script type="text/javascript" src="js/jquery.ui.widget.js"></script>
		<script type="text/javascript" src="js/jquery.ui.tabs.js"></script>	
		<script type="text/javascript" src="js/crud.js"></script>
		<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
		<script type="text/javascript" src="../Generador/js/Moment.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.all.js"></script>
		<script type="text/javascript" src="js/sweetalert2.all.min.js"></script>

		<script type="text/javascript" src="js/validaciones.js"></script>
		<script type="text/javascript" src="../js/ContabilidadCentroContable.js"></script>
		<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
		<script type="text/javascript" src="js/crud.js"></script>
		<script type="text/javascript" src="../js/catalogo/general.js"></script>
		<script type="text/javascript" src="js/PolizaEventos.js"></script>

<script type="text/javascript" charset="utf-8">
		
			var oTableCuen;
			var oper = 0; // se inicializa en 0 por que lo usamos para abrir el dialogo de procesando sin que haga nada en el open
			var seleccionado = -1; // renglón seleccionado del datatable
			var usuarioCapturaPoliza= <%=usuario.getGrupo("CAPTURA_POLIZA") != null%>;
			var usuarioRevisionPoliza = <%=usuario.getGrupo("REVISION_POLIZA") != null%>;
			var usuarioAutorizaPoliza = <%=usuario.getGrupo("AUTORIZA_POLIZA") != null%>;
			var usuarioCCentroContable = '<%=cCentroContable%>';			
			var totalRenglon=0;
			var detalleC=false;					
			var id_oper=1;
			var id_oper_Next=0;
			var polizaCancel=false;			
			var done=false;
			var ERROPER="Reintente nuevamente en un momento";	
			var renglon = 0;
			var ultimoTipoMovimiento = '';			
			var cuentas;			
			var numEvento = 0;			
			var capxCuentas=true;//=<%=((POLIZANUEVA==false && FORMATOPOLIZA==POLIZA_POR_CUENTAS) || (POLIZANUEVA && cPolizaManualCuenta))%>;		
			var EVTOCONF; //array que guardará la configuración de los eventos
			var PUNTERO = 0; //posición actual en la configuración del evento que se agrega					
			var FILASEVENTO=0; //filas totales del nuevo evento
			var NUMEVENTO_CARGAR=0; //el número de evento actual
			var ESCARGO=true; //si la fila del evento es cargo es verdadero, falso será abono
			var REGISTROSDT=0; //filas del dt actual
			var datosPoliza;
			var editaMov=false;
			var posEditar=-1;
			var valorTemp="$0.0";//almacena el monto ingresado por el usuario, de inicio vale 0	
			var Parcialestmp="";//Guarda de manera temporal el texto a repetir en caso de editar algun movimiento
			var maxChars=799; //caracteres maximo permitidos en los textarea
			var charTemp=""; //texto actual en el textarea
			var guardadoParcial=false;
			let mensajeRetorno='<%=mensajeRetorno%>';
			var Cancela_Firmante=false; //Bandera para avanzar en firmantes 
			
			//VGC20150702 cuando es un usuario de admin, valida contra los beneficiarios tanto de financieros como de RRHH
			var esAdminContabilidad = <%=esAdminContabilidad%>; 
			
			/*VGCFIEL*/
			var nNumEmpleado = "<%=nNumEmpleado%>";
			
			$("#pb_cancel", parent.window.document).hide();
			$("#pb_leave", parent.window.document).hide();
			$("#pb_save", parent.window.document).hide();
			$("#pb_send", parent.window.document).hide();
					
			function showCuentas(){
				var param = 'vCuentasContables';
				if( $("#reclasifica").attr("checked") )
					param = 'vCuentasPresupuestales';
					
				window.open('../catalogos/AyudaCuentas.jsp?vista='+param, 'AyudaCuentas', 'status=1, width=900px, height=600px, resizable=1');
			}
			
			$(document).ready(function(	)
			 {		
			 	/*VGCFIEL*/
				$("#nNumEmpleadoElab").val(nNumEmpleado);
			
				$("#bCargar").click(function(){ cargarInformacion( $("#archivoPol").val() ); });				
			 	
			 	$("#pCuentas tbody").dblclick(function(event) 
			 	{
			 		try
			 		{					
				 		var rows = oTableCuen.fnGetData();
						var rowCount = rows.length;	
						if(rowCount == 0) return;										
							
						if(posEditar>=0 || PUNTERO!=0 || (!capxCuentas && FILASEVENTO!=0))
						{
							alert("Debe terminar de editar el movimiento antes de editar otro diferente.");
							return;
						}
						
						
						if(id_oper==2 || id_oper==3)
						{
							return;
						}
						
							
						posEditar= oTableCuen.fnGetPosition(event.target.parentNode);					
						$(oTableCuen.fnSettings().aoData).each(function() 
						{
							$(this.nTr).removeClass('row_selected');
							
						});
	                      
						$(event.target.parentNode).addClass('row_selected');
	
						var aData = oTableCuen.fnGetData(posEditar);
						numEvento=aData[14];
						editaMov=true;
						Parcialestmp=$("#Parciales").val();
						

						if(quitaFmt(aData[5]) != 0.0)		
						{
							$("#tdlabelCargo").show();
							$("#Cargos").show();
							ESCARGO=true;	
							valorTemp=quitaFmt(aData[5]);
							$("#tdlabelAbono").hide();
							$("#Abonos").hide();
						}
						else
						{
							$("#tdlabelCargo").hide();
							$("#Cargos").hide();
							ESCARGO=false;
							valorTemp=quitaFmt(aData[6]);
							$("#tdlabelAbono").show();
							$("#Abonos").show();
						}
											
						cambioTotales(aData[0], aData[1], aData[2], aData[3], aData[4], aData[5], aData[6], '', aData[7], aData[11], aData[12], aData[13], aData[15]);

						
						deshabilitabtnCuentas();
						$("#divCuenta").show();
						$("#tdButtons").show();	
						$("#Modificar").show();
						$(".elementosCuenta").show();
						$("#Limpia1").show();  
						
					}
					catch(ex)
					{
						alert("Error 0001js.\r\r" + ex.message + "\r\rFavor de reportarlo al Administrador del Sistema.");
					}	
				});
			 	
			 	$("#cConcepto,#txtObservaciones").bind('paste', function (e) 
			 		{   
			 		    var elemento=(e.target.id);    		
						charTemp=$("#"+elemento).val().replace("'","").replace("´","");
						
						if(elemento=='txtObservaciones')
			 			maxChars=299;
						
						setTimeout(function () 
							{ 
								if($("#"+elemento).val().length > maxChars)
      					   		{
									alert("El texto excede el número máximo de caracteres permitidos.");
									$("#"+elemento).text(charTemp);									
      					   		}
								charTemp="";
					    	});							
	    			});			 	
			 	
			 	$("#cConcepto,#txtObservaciones").keydown(function(e)
			 	{ 
			 		var elemento=(e.target.id); 
			 		var car=e.which;
			 		
			 		if(elemento=='txtObservaciones')
			 			maxChars=299;
			 		
			 		if(car==219)
			 			e.preventDefault();			 		
    				else if(teclasEspeciales(car) && $("#"+elemento).val().length > maxChars)
        			{
    					alert("Ha llegado al número máximo de caracteres permitidos.");
    					$("#"+elemento).val($("#"+elemento).val().substr(0, maxChars));
        			}    				
				});	
			 	
			 	function teclasEspeciales(car)
			 	{
			 		var cars=[8,46,37,38,39,40,13];//borrados,flechas y enter
			 		var permitido=true;
			 		
			 		for (var i=0;i<cars.length;i++)
			 			if(cars[i]==car)
			 				permitido=false;
			 		
			 		return permitido;
			 	}
			 	
			 	$("#Cargos,#Abonos").keyup(function(e)
			 	{ 
			 		var elemento=(e.target.id); 
			 		
			 		if(!EsNumero($("#"+elemento).val(),"") && $(this).val()!='-' && $(this).val()!='.')
			 			{
			 			$("#"+elemento).val($("#"+elemento).val().substr(0,  $("#"+elemento).val().length-1));
			 			}			 		
					
				});	
			 	
			 	$("#pCuentas tbody tr").live('click', function(evt1) 
			 	{		
			 		try
			 		{									
						var rows = oTableCuen.fnGetData();
						var rowCount = rows.length;						
						
						if(rowCount == 0 || posEditar>=0 || id_oper==2 || id_oper==3 || (!capxCuentas && FILASEVENTO!=0) || PUNTERO!=0) return;	
						
						
						if ($(this).hasClass('row_selected')) {
							$(this).removeClass('row_selected');
							seleccionado = -1;
						} else {
							$('tr.row_selected').removeClass('row_selected');
							$(this).addClass('row_selected');
							seleccionado = oTableCuen.fnGetPosition(evt1.target.parentNode);
						}
					}
					catch(ex)
					{
						alert("Error 0002js.\r\r" + ex.message + "\r\rFavor de reportarlo al Administrador del Sistema.");
					}	
				});
				
				
	
				
			 	oTableCuen=$("#pCuentas").dataTable({
					oLanguage: {
						sProcessing: "Procesando...",
						sLengthMenu: "Mostrar _MENU_ registros",
						sZeroRecords: "No hay registros a mostrar",
						sEmptyTable: "No hay datos en la tabla",
						sLoadingRecords: "Cargando...",
						sInfo: "Registros _START_ al _END_ de _TOTAL_",
						sInfoEmpty: "Registro 0 al 0 de 0",
						sInfoFiltered: "(filtrado de _MAX_ registros)",
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
					bProcessing: true,					
		            bAutoWidth : true,
					bRetrive: true,
					bDestroy: true,
		    		bPaginate: true,
		    		sScrollX: "100%",		      		
          			bLengthChange : true,
					bInfo : true,
					bFilter : true,
					bSort : true,
					sAjaxSource:"../export/GeneraJsonTxt",
					"fnServerParams":  function ( aoData ) {
                     aoData.push( { "name": "more_data", "value": <%=folioDoc%> } );
                      },
					left : true,
					sPaginationType: "full_numbers",
					bJQueryUI: true,
					"aLengthMenu": [
							            [25, 50, 100, 200, -1],
							            [25, 50, 100, 200, "Todo"]
							        ], 
					"iDisplayLength" : 25,
					"aoColumnDefs" : [
										{ "bVisible": false,  "aTargets": [ 15 ] }
									 ]
	        	});		 	
			
				$('#dtGuardaDetalle').dataTable({
					"iDisplayLength": 500,
			      	"bPaginate": false,
			      	"bFilter": false,
			      	"bSort": false,
			      	"bInfo": false } );
			      	
				$( "#dialog-Procesando" ).dialog({
					autoOpen: true,
					height: 250,
					width: 600,
					modal: true,
					show: "blind",
		   			hide: "scale",
		   			resizable: false,
		    		closeOnEscape: false,
		    		overlay: { backgroundColor: '#FFF',opacity: 1.0 },
					open: function() {
				
						try
						{        
						   $(".ui-dialog-titlebar-close").css("visibility","hidden");
						   	<%if((POLIZANUEVA && !capxCuentas)|| !POLIZANUEVA){%>
						   $("span.ui-dialog-title").text('Procesando');
							<%}%>
						   						   
				   		
					       if((id_oper==1 || id_oper==0) && id_oper_Next==2)
						   {
						        setTimeout("avanzaRevision()",2000);
						   }
						   else if(id_oper==2 && id_oper_Next==3)
						   {		   
					        	setTimeout("avanzaAutorizacion()",2000);
						   }else if(id_oper==3 && id_oper_Next==2)
						   {
					            setTimeout("regresaRevision()",2000);
					       }
						   else if(id_oper==2 && id_oper_Next==1)
						   {
								setTimeout("regresaCaptura()",500);
						   }
						}
						catch(ex)
						{
							alert("Error 0003js.\r\r" + ex.message + "\r\rFavor de reportarlo al Administrador del Sistema.");
						}	
	
					},
					close: function() {										
					}				
				});	

				$("#nCuenta").bind( "change", function( event ) {
	               selectCuenta();
	            });
				
				$("#movParcialidad").bind( "change", function( event ) {
					if(!$(this).is(":checked")) 
	              	{
						$("#Parciales").val("");
						Parcialestmp="";						
	              	}
	            });
				    
				$("#nCuenta").bind( 
					"keydown", 
					function( event ) {
	                	return catchTab(event, "nCuenta");
	            	}).autocomplete({
						delay:100,
						minLength: 0,
						source: "../CuentaContable/AutoCompletaCuenta",
						select:function(event, ui){					
							$("#nCuenta").val(ui.item.value);
							$("#nCuentaAplicacion").val(ui.item.value);
							selectCuenta();
						}
				});
					
				$("#nGrupo").bind( "blur", function( event ) {				
		                return catchTab(event, "nGrupo");
		            }).bind( "change", function( event ) {				
		                $.ajax({
								url:"../CuentaContable/autoCompletaGrupoEvento",
								data:"term="+$("#nGrupo").val(),
								dataType:"json",
								success: function(json){		
									if(json.length!=0){											
										document.getElementById("Label2").innerHTML = json[0].descripcion; 
										$("#nSubGrupo").val("");
										document.getElementById("Label3").innerHTML = "";
										$("#dSubGrupo").val("");
										document.getElementById("Label4").innerHTML = "";
										$("#nEvento").val("");
										document.getElementById("Label5").innerHTML = "";					
										document.getElementById("Label6").innerHTML = "";
										$.ajax({
											url:"../CuentaContable/selected",
											data:"key=nGrupo&value="+json[0].value
										});
									}
								}
							});	
	            });
			
				$("#nSubGrupo").bind( "blur", function( event ) {				
	                return catchTab(event, "nSubGrupo");
	            }).bind( "change", function( event ) {				
	                $.ajax({
							url:"../CuentaContable/autoCompletaSubGrupoEvento",
							data:"term="+$("#nSubGrupo").val(),
							dataType:"json",
							success: function(json){
								if(json.length!=0){												
									document.getElementById("Label3").innerHTML = json[0].descripcion; 
									$("#nEvento").val("");
									document.getElementById("Label4").innerHTML = "";
									$("#dEvento").val("");
									document.getElementById("Label5").innerHTML = "";
									document.getElementById("Label6").innerHTML = "";
									$.ajax({
										url:"../CuentaContable/selected",
										data:"key=nSubGrupo&value="+json[0].value
									});			
								}
							}
						});	
	            });
						
				$("#nEvento").bind( "blur", function( event ) {				
	                return catchTab(event, "nEvento");
	            }).bind( "change", function( event ) {				
	                $.ajax({
							url:"../CuentaContable/autoCompletaEventoRelacion",
							data:"term="+$("#nEvento").val(),
							dataType:"json",
							success: function(json){	
								if(json.length!=0){												
									document.getElementById("Label4").innerHTML = json[0].descripcion; 
									$.ajax({
										url:"../CuentaContable/selected",
										data:"key=nEvento&value="+json[0].value
									});
								}			
							}
						});	
	            });
	         
	     		
				$("input.AyudaSyC").subIniciaDlg();
	    		//$("input.autoCompletaSyC").subIniciaAutoCompleta();
	
				$('.currency').blur(function()
				{
					$('.currency').formatCurrency();
				});		
				
				
				
//***********************
					
		$( "#dialog-firmantes" ).dialog({
				
				autoOpen: false,
				height: 400,
				width: 480,
				modal: true,
				buttons: {
						"Aceptar": function() 
						{	
							if($("#cNombreCaptura").val() == ""){ alert("Falta Ingresar Nombre del Capturista "); return; } 
							else if($("#cPaternoCaptura").val() == ""){ alert("Falta Ingresar Apellido Paterno del Capturista"); return; }
							else if($("#cPuestoCaptura").val() == ""){ alert("Falta Ingresar Puesto del Capturista"); return; }
							
							if($("#cNombreRevisa").val() == ""){ alert("Falta Ingresar Nombre del Revisor "); return; } 
							else if($("#cPaternoRevisa").val() == ""){ alert("Falta Ingresar Apellido del Revisor"); return; }
							else if($("#cPuestoRevisa").val() == ""){ alert("Falta Ingresar Puesto del Revisor"); return; }
							
							if($("#cNombreAutoriza").val() == ""){ alert("Falta Ingresar Nombre del Autorizador"); return; } 
							else if($("#cPaternoAutoriza").val() == ""){ alert("Falta Ingresar Apellido Paterno del Autorizador"); return; }
							else if($("#cPuestoAutoriza").val() == ""){ alert("Falta Ingresar Puesto del Autorizador "); return; }
							
							
							$("#cNombreCap").val($("#cNombreCaptura").val());
							$("#cPaternoCap").val($("#cPaternoCaptura").val());
							$("#cMaternoCap").val($("#cMaternoCaptura").val());
							$("#cPuestoCap").val($("#cPuestoCaptura").val());
							
							$("#cNombreRev").val($("#cNombreRevisa").val());
							$("#cPaternoRev").val($("#cPaternoRevisa").val());
							$("#cMaternoRev").val($("#cMaternoRevisa").val());
							$("#cPuestoRev").val($("#cPuestoRevisa").val());
							
							$("#cNombreAut").val($("#cNombreAutoriza").val());
							$("#cPaternoAut").val($("#cPaternoAutoriza").val());
							$("#cMaternoAut").val($("#cMaternoAutoriza").val());
							$("#cPuestoAut").val($("#cPuestoAutoriza").val());
							
							$("#firmanteCap").val($("#cNombreCap").val()+" "+$("#cPaternoCap").val()+" "+$("#cMaternoCap").val() );
							$("#firmanteRev").val($("#cNombreRev").val()+" "+$("#cPaternoRev").val()+" "+$("#cMaternoRev").val());
							$("#firmanteAut").val($("#cNombreAut").val()+" "+$("#cPaternoAut").val()+" "+$("#cMaternoAut").val());
							
							var msn = "No Se Guardo Correctamente Informacion de Firmantes"; 
								
							if($("#firmanteExiste").val() == "Existe"){
								
								// Actualiza informacion
								queryFormPost({	queryName : "guardaFirmanteCaptura_Update,guardaFirmanteRevisa_Update,, guardaFirmanteAutoriza_Update", async : false, callback : function(){ 
															
															msn = "Actualizado Correctamente Firmantes";
														} 
										  	});
							}else{
								
								queryFormPost({	queryName : "guardaFirmanteCaptura_Create,guardaFirmanteRevisa_Create,guardaFirmanteAutoriza_Create", async : false, callback : function(){
															
															msn = "Guardado Correctamente Firmantes";
														} 
										  });
							}
							
							queryFormPost("tDocPolizaEncFirmante_Update", {async: false });	
							
							
								
							queryFormPost("tDocPolizafAplicacion_Update", {async: false });					
							//cont=true;	
							//firmante=true;
							//reImprime="SI";						
							//$("#guardar").click();
							$( this ).dialog( "close" );
						},
						
						"Cancelar": function()
							 {	
							 	Cancela_Firmante=true;						
								$( this ).dialog( "close" );							
											}
							},
				close: function() {						
						        if (Cancela_Firmante)
						           {	
						        	 return;
						           }
						        else{
						        		$("#tOperacion").val("Avanzando de autorizacion a consulta");			
							    		queryFormPost("tDocPolizaBitacora", {	async : false});
										id_oper_Next=4;
										$("#pb_save", parent.window.document).click();
				                	}
				                
				                }							
	});
///*************				
			if(usuarioCCentroContable!="10" && !esAdminContabilidad)
			$("#file1").hide();
											   
	
			});		// fin del onready
			
			
			
			
		   	$(function() {		   		
				$( "#fAplicacionLbl" ).datepicker({
					dateFormat: "dd/mm/yy",
					autoclose: true,
					changeYear: true,
					changeMonth: true,
					minDate:new Date(<%=fechaMinima%>),
			  		maxDate:new Date(<%=fechaMaxima%>)
				});
			});	
					
		   function asignaTipoPol()
			{ 
				if($("#grpformato").val()==1)
					capxCuentas=true;
				else
					capxCuentas=false;	
				
				$("#grpformato").remove();
				$("#eligeFormato").remove();
				$("#ProcMsg").show();	
				$("#marcoformatoPoliza").remove();	
				$("span.ui-dialog-title").text('Procesando');
				
				setTimeout("fnLoad();",2000);					
			}
				
			function Grid()
		{
		window.open('../admin/MultiReporteGrid.jsp?id='+$("#cCentroContable").val()+'', 'MultiReporteGrid', 'status=1, width=900px, height=500px');
		if ($.trim(document.getElementById("ep").value)!=""){
			//window.alert($.trim(document.getElementById("ep").value));
			rellenaCampos();
		}
		
	 	return false;
		} //FIN DEL GRID
		
		function rellenaCampos() {
//					querySelectPost("catalogoClaveCNA2Read", "ClaveCNA");
					<%for (int i = 0; i < Control.length; i++) {%>
						$('#nOrden').val('<%=i+1%>');
						<%if ((i+1)!=3 && (i+1)!=9 && (i+1)!=10 && (i+1)!=14 && (i+1)!=15 && (i+1)!=16){%>
							querySelectPost("catalogoEPRead", "<%=Control[i]%>");
							$("#<%=Control[i]%>").attr("disabled", true);
						<%}else{%>
							$("#h<%=Control[i]%>").attr("disabled", true);
						<%}%>
					<%}%>
					//$("#hClaveCNA").attr("disabled", true);
            }

			
					
			function creacionDetalle( sCrudFrm ) 
			{
				try
				{
						cargaDataTable();
						queryFormPost( sCrudFrm + "tPolizaDetalleEventoCreate", { async : false, 
							callback : function() {
								//if(id_oper==0)
								//	id_oper = 1;
								detalleC=true;	
								guardadoParcial=true;	
								} 
							});
					
				}
				catch(ex)
				{
					alert("Error 0005js.\r\r" + ex.message + "\r\rFavor de reportarlo al Administrador del Sistema.");
				}
			}				// fin de creacionDetalle( sCrudFrm )
				
			function cargaDataTable() {
					var rows = oTableCuen.fnGetData();
					var rowCount = rows.length;
					var bGuardar = false;
					var cEvento="";
					var mImporte=0;
					
					$('#dtGuardaDetalle').dataTable().fnClearTable();
		
					for ( var i = 0; i < rowCount; i++) 
					{
						if(quitaFmt(rows[i][5])==0)
							{
							cEvento="ABONO";
							mImporte=quitaFmt(rows[i][6]);
							}	
						else
							{
							cEvento="CARGO";
							mImporte=quitaFmt(rows[i][5]);
							}
														
								
						$('#dtGuardaDetalle').dataTable().fnAddData([
						           '<td><input type="text" size="1" id="nDocRenglonCrud" name="nDocRenglonCrud" value="' + rows[i][0] + '"></td>',
						           '<td><input type="text" size="1" id="nCuentaCrud" name="nCuentaCrud" value="' + rows[i][15] + '"></td>',
						           '<td><input type="text" size="1" id="valSubCuentaCrud" name="valSubCuentaCrud" value="' + rows[i][3] + '"></td>',
						           '<td><input type="text" size="1" id="ParcialesCrud" name="ParcialesCrud" value="' + rows[i][4] + '"></td>',
						           '<td><input type="text" size="1" id="Parcial" name="Parcial" value="' + rows[i][7]+ '"></td>',
						           '<td><input type="text" size="1" id="cEventoCrud" name="cEventoCrud" value="' + cEvento + '"></td>',
						           '<td><input type="text" size="1" id="mImporte" name="mImporte" value="' + mImporte  + '"></td>',
						           '<td><input type="text" size="1" id="dataCABMS" name="dataCABMS" value="' + rows[i][12] + '"></td>',
						           '<td><input type="text" size="1" id="dataCUCOP" name="dataCUCOP" value="' + rows[i][13] + '"></td>',
						           '<td><input type="text" size="1" id="dataPartida" name="dataPartida" value="' + rows[i][11] + '"></td>',
						           '<td><input type="text" size="1" id="nIdGrupoEvento" name="nIdGrupoEvento" value="' + rows[i][8] + '"></td>',
						           '<td><input type="text" size="1" id="nIdSubGrupoEvento" name="nIdSubGrupoEvento" value="' + rows[i][9] + '"></td>',
						           '<td><input type="text" size="1" id="cIdEventoManual" name="cIdEventoManual" value="' + rows[i][10] + '"></td>',
						           '<td><input type="text" size="1" id="nNumeroEvento" name="nNumeroEvento" value="' + rows[i][14] + '"></td>'
						           ]);
						
						
					} 
					}	
					
		function creaEvento()
		{
			try
			{
				$("#pCuentas tbody tr").each(function(evt1) {	$(this).removeClass('row_selected'); });				
							
				datosPoliza = oTableCuen.fnGetData();
				REGISTROSDT = datosPoliza.length;					
				
				if (REGISTROSDT==0) 
					NUMEVENTO_CARGAR=1;
				else
					NUMEVENTO_CARGAR=parseInt(datosPoliza[REGISTROSDT-1][14])+1;								
				
								
						var params = "";					
						params += "cIdGrupoEvento="+$("#nGrupo").val();
						params += "&cIdSubGrupoEvento="+$("#nSubGrupo").val();
						params += "&cIdeventoManual="+$("#nEvento").val();
						params += "&cPartida="+$("#nCOG").val();					
						
						$.ajax({
								url:"../PolizaManual/getCuentasByPartida",
								data:params,
								dataType:"json",
								success : function(result){
								
									try
									{
										if(result[0].value == 'error')
										{
											alert(result[0].descripcion);
											$("#nCOG").val("");
											return false;	
										}
										else
										{								
											FILASEVENTO=result.length;										
											EVTOCONF= result;
											cargaRegistroEvento();
											$("#buttonLimpiaEvento").hide();
											numEvento=NUMEVENTO_CARGAR;
										}		
									}
									catch(ex)
									{
										alert("Error 0002js.\r\r" + ex.message + "\r\rFavor de reportarlo al Administrador del Sistema.");
									}					
								}
							});	
					
			}
			catch(ex)
			{
				alert("Error 0006js.\r\r" + ex.message + "\r\rFavor de reportarlo al Administrador del Sistema.");
			}	
		}	
			
			function cargaRegistroEvento()
			{
				try
				{						
					$("#nCuenta").val(EVTOCONF[PUNTERO].value);	
					$("#nCuentaAplicacion").val(EVTOCONF[PUNTERO].value);
					
					selectCuenta();					
					
					$("#divCuenta").show();	
					$("#tdButtons").show();	
					$("#Limpia1").show();
					
					if(ESCARGO)
						valorTemp=$("#Cargos").val();
					else
						valorTemp=$("#Abonos").val();
							
				    moneyFrmt("Cargos", "0");
					moneyFrmt("Abonos", "0");
					
					if(EVTOCONF[PUNTERO].cEvento == "CARGO")
					{
						$("#tdlabelCargo").show();
						$("#Cargos").show();							
						$("#tdlabelAbono").hide();
						$("#Abonos").hide();
						$("#Cargos").val(valorTemp);
						ESCARGO=true;						
					}
					else
					{
						$("#tdlabelCargo").hide();
						$("#Cargos").hide();							
						$("#tdlabelAbono").show();
						$("#Abonos").show();
						$("#Abonos").val(valorTemp);
						ESCARGO=false;
					}	
										
					deshabilitarbtEvento();
					
					
				}
				catch(ex)
				{
					alert("Error 0007js.\r\r" + ex.message + "\r\rFavor de reportarlo al Administrador del Sistema.");
				}				
				
			}
			
			function deshabilitarbtEvento()
			{
				try
				{
					$("#nGrupo").attr("disabled", "disabled");
					$("#nSubGrupo").attr("disabled", "disabled");
					$("#nEvento").attr("disabled", "disabled");
					
					$("#btnnGrupo").hide();				
					$("#btnnSubGrupo").hide();						
					$("#btnnEvento").hide();
					$("#btnnCuenta").hide();
					
					$("#nCOG").attr("disabled", "disabled");
					$("#nCABMS").attr("disabled", "disabled");
					$("#nCUCOP").attr("disabled", "disabled");
					
					$("#btnnCOG").hide();
					$("#btnnCABMS").hide();
					
				}	
				catch(ex)
				{
					alert("Error 0008js.\r\r" + ex.message + "\r\rFavor de reportarlo al Administrador del Sistema.");
				}
			}				// fin de deshabilitarBotones()		
			
			function habilitarbtEvento()
			{
				try
				{
					$("#nGrupo").removeAttr("disabled");
					$("#nSubGrupo").removeAttr("disabled");
					$("#nEvento").removeAttr("disabled");
					$("#btnnGrupo").removeAttr("disabled");
					$("#btnnSubGrupo").removeAttr("disabled");
					$("#btnnEvento").removeAttr("disabled");
					$("#btnnCuenta").removeAttr("disabled");
					
					$("#btnnGrupo").show();			
					$("#btnnSubGrupo").show();						
					$("#btnnEvento").show();
					$("#btnnCuenta").show();
					
					$("#nCOG").val("");
					$("#nCABMS").val("");
					$("#nCUCOP").val("");					
				}	
				catch(ex)
				{
					alert("Error 0009js.\r\r" + ex.message + "\r\rFavor de reportarlo al Administrador del Sistema.");
				}
			}		// fin de habilitarBotones()
			
			function muestrabtnsPartida()
			{
				$("#nCOG").removeAttr("disabled");
				$("#nCABMS").removeAttr("disabled");
				$("#nCUCOP").removeAttr("disabled");
				$("#buttonCreateEvento").removeAttr("disabled");
				$("#btnnCOG").show();						
				$("#btnnCABMS").show();
				$("#nCOG").val("");
				$("#nCABMS").val("");
				$("#nCUCOP").val("");	
			}
			
			function limpiadatosEvento()
			{				
				$("#nGrupo").val("");
				$("#nSubGrupo").val("");
				$("#nEvento").val("");
				$("#nCOG").val("");
				$("#nCABMS").val("");
				$("#nCUCOP").val("");
				
			}
			
			function limpiaCapCuentas()
			{	
				$(".elementosCuenta").hide();
				$("#btnnCuenta").show();
				$("#nCuenta").val("");
				$("#nCuentaAplicacion").val("");
				$("#tdlabelCargo").show();
				$("#Cargos").show();
				$("#tdlabelAbono").show();
				$("#Abonos").show();
				$("#Cargos").val("$0.0");
				$("#Abonos").val("$0.0");				
			}
			function deshabilitabtnCuentas()
			{
				if(!capxCuentas)
					{
						$("#nCuenta").attr("disabled",true);
						$("#btnnCuenta").hide();	
					}
				
				$("#dCuenta").attr("disabled",true);
				$("#movParcialidad").attr("disabled",true);					
				$("#movParcialidad").hide();
				$("#repetirTexto").hide();
							
			}
			
			function habilitabtnCuentas()
			{
				if(!capxCuentas)
					{
						$("#nCuenta").removeAttr("disabled");
						$("#btnnCuenta").show();
					}					
				
				$("#dCuenta").removeAttr("disabled");
				$("#movParcialidad").removeAttr("disabled");				
				$("#movParcialidad").show();
				$("#repetirTexto").show();					
							
			}
			
			function agregaRegistroEvento()
			{
				try
				{	
					if ($("#nCuenta").val() == '') {
						alert("Es Necesario que indique una cuenta");
						return;
					}
				
					if ($("#Cargos").val() == '')
						$("#Cargos").val(0);
				
					if ($("#Abonos").val() == '')
						$("#Abonos").val(0);
					
					Parcialestmp=$("#Parciales").val();//Se guarda concepto chico desde aqui por si el usuario elimina el evento
				
					if (parseFloat(quitaFmt($("#Cargos").val())) == 0 && parseFloat(quitaFmt($("#Abonos").val())) == 0) 
					{
						alert("Es Necesario que capture al menos un monto de cargo o abono.");							
						return;
					}
					
					if(capxCuentas)//Captura de poliza por cuentas se incrementa el evento y se da valor de 1 a registros x eventos
						{ 
							if (parseFloat(quitaFmt($("#Cargos").val())) != 0 && parseFloat(quitaFmt($("#Abonos").val()))!= 0) 
							{
								alert("Solo se puede capturar cargo o abono.");
								$("#Abonos").val("$0.0");
								$("#Cargos").val("$0.0");
								return;
							}
				
							if(parseFloat(quitaFmt($("#Cargos").val())) != 0)
								ESCARGO=true;
							else
								ESCARGO=false;							
								FILASEVENTO=1;
								datosPoliza = oTableCuen.fnGetData();
								REGISTROSDT = datosPoliza.length;
								NUMEVENTO_CARGAR=parseInt(datosPoliza.length)+1;						
						}				
					
					queryFormPost("ComparaPolizaCuentaRead", {async : false});
				
					if ($("#nCuenta").val() != $("#valSubCuentaCompar").val()) {
						alert("El numero de cuenta [" + $("#nCuenta").val()+ "] no existe o no es una cuenta de aplicacion. ");
						limpiaCuentas();
						ocultaSubCuentas();
						return;
					}			
					
					var partfech = $("#fAplicacion").val().split("/");
					var mesAplicar = partfech[1];
					
				
					if (validaCuenta($("#nCuenta").val(), mesAplicar, $("#cCentroContable").val(), (ESCARGO)?"C":"A"))
						return;
				
					if ($("#nSubCuenta").val() == "RFC") 
					{
						
						$("#valSubCuenta").val($("#cIDRFC").val());
						$("#valSubCuentaCompar").val("");
				
						if ($("#valSubCuenta").val() == '') {
							alert("Es obligatorio indicar un RFC");
							$("#cIDRFC").focus();
							return;
						}
				
						
						//VGC20150702 cuando es un usuario de admin, valida contra los beneficiarios tanto de financieros como de RRHH
						if( esAdminContabilidad )
							queryFormPost("ComparaPolizaRFCAdminRead", {
								async : false
							});
						else
							queryFormPost("ComparaPolizaRFCRead", {
								async : false
							});
				
						if ($("#cIDRFC").val() != $("#valSubCuentaCompar").val()) {
							alert("El RFC [" + $("#cIDRFC").val() + "] no existe. ");
							$("#valSubCuenta").val(0);
							$("#valSubCuentaCompar").val("");
							$("#cIDRFC").val("");
							return;
						}
					} 
					else if ($("#nSubCuenta").val() == "CTAB") 
					{
						$("#valSubCuenta").val($("#CTABAN").val());
						$("#valSubCuentaCompar").val("");
						if ($("#valSubCuenta").val() == '') {
							alert("Es obligatorio indicar una cuenta bancaria");
							$("#CTABAN").focus();
							return;
						}
						$("#cCentroContable").val(<%=cCentroContable %>);
						queryFormPost("ComparaPolizaCuentaBanRead", {
							async : false
						});
						if ($("#CTABAN").val() != $("#valSubCuentaCompar").val()) {
							alert("La cuenta Bancaria [" + $("#CTABAN").val() + "] no existe. ");
							$("#valSubCuenta").val(0);
							$("#valSubCuentaCompar").val("");
							$("#CTABAN").val("");
							return;
						}
					} 
					else if ($("#nSubCuenta").val() == "ALM") {
						$("#valSubCuenta").val($("#ALM").val());
					} 
					else if ($("#nSubCuenta").val() == "EP") {
					$("#valSubCuenta").val($("#ep").val());
					$("#valSubCuentaCompar").val("");
						if ($("#valSubCuenta").val() == '') {
							alert("Es obligatorio indicar una EP");
							$("#ep").focus();
							return;
							}
					$("#valSubCuenta").val($("#ep").val());
					} 
					else if ($("#nSubCuenta").val() == "OBGT") {
						if(capxCuentas==true){
							$("#valSubCuenta").val($("#hPartidaPol").val());
							$("#valSubCuentaCompar").val("");
								if ($("#valSubCuenta").val() == '') {
									alert("Es obligatorio indicar una Partida");
									$("#hPartidaPol").focus();
									return;
									}
								$("#valSubCuenta").val($("#hPartidaPol").val());
						}
						else{
							$("#valSubCuenta").val($("#nCOG").val());
						}
						} 
						/*ARLA*/
					else if ($("#nSubCuenta").val() == "cProyectoFonden") {
						if(capxCuentas==true){
							$("#valSubCuenta").val($("#hProyectoPol").val());
							$("#valSubCuentaCompar").val("");
								if ($("#valSubCuenta").val() == '') {
									alert("Es obligatorio indicar un Proyecto");
									$("#hProyectoPol").focus();
									return;
									}
								$("#valSubCuenta").val($("#hProyectoPol").val());
						}
						else{
							$("#valSubCuenta").val($("#nCOG").val());
						}
					}else if ($("#nSubCuenta").val() == "CRI") {
						$("#valSubCuenta").val($("#cCRI").val());
					} 
					
					
					else {
						$("#valSubCuenta").val("");
					}
									
					$('#pCuentas').dataTable().fnAddData(
										[
										REGISTROSDT+1,										//00 
										$("#nCuenta").val(),								//01
										$("#dCuenta").val(), 								//02
										$("#valSubCuenta").val(),							//03
										$("#Parciales").val(),								//04
										formatCurrency($("#Cargos").val()),					//05
										formatCurrency($("#Abonos").val()),					//06										
										"N",												//07
										$("#nGrupo").val(),									//08
										$("#nSubGrupo").val(),								//09
										$("#nEvento").val(),								//10
										$("#nCOG").val(),									//11
										$("#nCABMS").val(),									//12
										$("#nCUCOP").val(),									//13
										NUMEVENTO_CARGAR,									//14
										$("#nCuentaAplicacion").val()						//15
										]);
										
					$("#Tcargos").val( (Number(quitaFmt($("#Tcargos").val())) + Number(quitaFmt($("#Cargos").val()))).toFixed(2) );					
					$("#TcargosLbl").text( formatCurrency($("#Tcargos").val()) );				
					$("#Tabonos").val( (Number(quitaFmt($("#Tabonos").val())) + Number(quitaFmt($("#Abonos").val()))).toFixed(2) );
					$("#TabonosLbl").text( formatCurrency($("#Tabonos").val()) );
								
					
					
					
					if(!$("#movParcialidad").attr("checked"))
						$("#Parciales").val("");
					
					if((PUNTERO+1)==FILASEVENTO)
						{   
							if(capxCuentas)
							{
								REGISTROSDT++;
								$(".elementosCuenta").hide();
								$("#nCuenta").val("");
								$("#nCuentaAplicacion").val("");
							}
							else
							{
								$("#divCuenta").hide();
								$("#tdButtons").hide();								
								muestrabtnsPartida();
								FILASEVENTO=0;									
							}
							
							moneyFrmt("Cargos", "0");
							moneyFrmt("Abonos", "0");
							PUNTERO=0;
							$("#Agregar").hide();
							$("#Limpia1").hide();
							
							
						}
					else
						{	//La carga del evento aun no ha sido completada
							PUNTERO++;
							REGISTROSDT++;
							cargaRegistroEvento();							
							
						}					
				}
				catch(ex)
				{
					alert("Error 0010js.\r\r" + ex.message + "\r\rFavor de reportarlo al Administrador del Sistema.");
				}				
			}
			
		function editaRegistroEvento()
			{
				try
				{
					var acumuladoActual=0;
					var montoNuevo=0;

					
					if(ESCARGO)					
						{ 
							oTableCuen.fnUpdate($("#Cargos").val(), posEditar, 5);
							oTableCuen.fnUpdate("", posEditar, 6);
							valorActual=quitaFmt($("#Tcargos").val()); 
							montoNuevo=quitaFmt($("#Cargos").val());
							$("#Tcargos").val(parseFloat(valorActual)+Number(montoNuevo));
							$("#TcargosLbl").text(formatCurrency($("#Tcargos").val()));
						}	
					else
						{
							oTableCuen.fnUpdate($("#Abonos").val(), posEditar, 6);
							oTableCuen.fnUpdate("", posEditar, 5);
							valorActual=quitaFmt($("#Tabonos").val());
							montoNuevo=quitaFmt($("#Abonos").val());
							$("#Tabonos").val(parseFloat(valorActual)+Number(montoNuevo));
							$("#TabonosLbl").text(formatCurrency($("#Tabonos").val()));							
						}
					
					if(capxCuentas)
						{						
							oTableCuen.fnUpdate($("#nCuenta").val(), posEditar, 1);
							oTableCuen.fnUpdate($("#dCuenta").val(), posEditar, 2);
							oTableCuen.fnUpdate($("#nCuentaAplicacion").val(), posEditar, 15);
							limpiaCapCuentas();
						}
					else
						$("#divCuenta").hide();
					
					
					muestrabtnsPartida();
					
					if($.trim($("#Parciales").val())!="")
						oTableCuen.fnUpdate($("#Parciales").val(), posEditar, 4);
					if ($("#nSubCuenta").val() == "ALM")
						oTableCuen.fnUpdate($("#ALM").val(), posEditar, 3);
					else if ($("#nSubCuenta").val() == "RFC") 
						oTableCuen.fnUpdate($("#cIDRFC").val(), posEditar, 3);
					else if ($("#nSubCuenta").val() == "CTAB")
						oTableCuen.fnUpdate($("#CTABAN").val(), posEditar, 3);
					else if ($("#nSubCuenta").val() == "EP")
						oTableCuen.fnUpdate($("#ep").val(), posEditar, 3);
					else if ($("#nSubCuenta").val() == "OBGT")
						oTableCuen.fnUpdate($("#hPartidaPol").val(), posEditar, 3);
					else if ($("#nSubCuenta").val() == "cProyectoFonden")
						oTableCuen.fnUpdate($("#hProyectoPol").val(), posEditar, 3);
					else
						oTableCuen.fnUpdate("", posEditar, 3);
						
					
					habilitabtnCuentas();					
					$("#Modificar").hide();
					$("#Limpia1").hide();					
					$("#Cargos").val("$0.0");
					$("#Abonos").val("$0.0");					
					
					posEditar=-1;
					
					if(Parcialestmp!="")
					{
						$("#Parciales").val(Parcialestmp);
						$("#movParcialidad").attr("checked", true);
						Parcialestmp="";
					}
				}
				catch(ex)
				{
					alert("Error 0011js.\r\r" + ex.message + "\r\rFavor de reportarlo al Administrador del Sistema.");
				}				
			}
		function renumeraEventoCuentas()
		{
			var rows = oTableCuen.fnGetData();
			var rowCount = rows.length;
			var evtoManual=1;
			
			for(i=0;i<rowCount;i++)
				{
				oTableCuen.fnUpdate(evtoManual, i,14);
				oTableCuen.fnUpdate("", i,13);
				oTableCuen.fnUpdate("", i,8);
				oTableCuen.fnUpdate("", i,9);
				evtoManual++;
				}
			
		}
			
		function eliminaRegistroEvento()
			{	
				try
				{				
					var aData= oTableCuen.fnGetData();					
					var temp=new Array();
					var renglon=1;
					var evtoRenumerado=0;
					var evtoAnterior=0;						
						
					for (var i = 0; i < aData.length; i++) 
					{  					
						if(aData[i][14]!=numEvento)//se agregan los registros con el evento distinto al que se encuentra en memoria
							{								   
								temp.push(aData[i]);
								temp[renglon-1][0]=renglon;
								
								if(aData[i][14]==evtoAnterior)
										temp[renglon-1][14]=evtoRenumerado;
								else
									{
										evtoAnterior=temp[renglon-1][14];
										++evtoRenumerado;
										temp[renglon-1][14]=evtoRenumerado;
									}								
								
								renglon++;								
							}
						else
							{   						
								if(posEditar!=-1 && (aData[posEditar][0]!=aData[i][0]) )//si la posicion actual se esta editando no se resta del total
								{														//posEditar indica que no es una edicion 
									if(Number(quitaFmt(aData[i][5]))==0)				//no se busca por evento porque el evto en edicion sigue en el dt, se descarta el renglon en edición					
										{
										$("#Tabonos").val(quitaFmt($("#Tabonos").val())-Number(quitaFmt(aData[i][6])));	
										$("#TabonosLbl").text(formatCurrency($("#Tabonos").val()));
										}								
									else
										{
										$("#Tcargos").val(quitaFmt($("#Tcargos").val())-Number(quitaFmt(aData[i][5])));
										$("#TcargosLbl").text(formatCurrency($("#Tcargos").val()));
										}
								}
								else if(posEditar==-1 && aData[i][14]==numEvento )//Se borran del dt los renglones del evento actual (no es edicion)
								{												  //se busca por número de evto
									if(Number(quitaFmt(aData[i][5]))==0)									
										{
										$("#Tabonos").val(quitaFmt($("#Tabonos").val())-Number(quitaFmt(aData[i][6])));	
										$("#TabonosLbl").text(formatCurrency($("#Tabonos").val()));
										}								
									else
										{
										$("#Tcargos").val(quitaFmt($("#Tcargos").val())-Number(quitaFmt(aData[i][5])));
										$("#TcargosLbl").text(formatCurrency($("#Tcargos").val()));
										}									
								}
								
							}
					}				
	   					
						$("#pCuentas").dataTable().fnClearTable();
						$("#pCuentas").dataTable().fnAddData(temp);
						
						muestrabtnsPartida();
						$("#Limpia1").hide(); 
						$("#Modificar").hide();
						$("#Agregar").hide();					
						$("#Cargos").val("$0.0");
						$("#Abonos").val("$0.0");
						$("#Parciales").val(Parcialestmp);					
						
						habilitabtnCuentas();
						
						posEditar=-1;
						PUNTERO=0;
						FILASEVENTO=0;
						 
						if(temp.length==0 && !capxCuentas)
							{							
								$("#buttonLimpiaEvento").removeAttr("disabled",true);
								$("#buttonLimpiaEvento").show();
								$("#buttonCreateEvento").show();							
								habilitarbtEvento();								
								limpiadatosEvento();						
							}
						if(capxCuentas)
							{							
								limpiaCapCuentas();
							}
						else
							$("#divCuenta").hide();	
						
					
				}
				catch(ex)
				{
					alert("Error 0012js.\r\r" + ex.message + "\r\rFavor de reportarlo al Administrador del Sistema.");
				}
				
				
			}
			
		
			function cmdGuardarParcial() 
			{	
				try
				{
					$( "#dialog-Procesando" ).dialog( "open" );	
					var rows = oTableCuen.fnGetData();
					var rowCount = rows.length;
					
					if(rowCount==0)
					{
						alert("Para poder guardar es necesario haber capturado por lo menos un evento");
						$( "#dialog-Procesando" ).dialog( "close" );	
						return;
					}
					
					if($("#cxp").val()=="" && $("#reclasifica").is(":checked")){
						alert("La cuenta por pagar es obigatoria, favor de capturarla");
						$( "#dialog-Procesando" ).dialog( "close" );	
						return;
					}	
					
					if($("#polManual").val()=="" && $("#reclasificaV").is(":checked")){
						alert("Favor de seleccionar el numero de solicitud del anticipo.");
						$( "#dialog-Procesando" ).dialog( "close" );	
						return;
					}			    
				     				    
			        if(!capxCuentas)
						if(PUNTERO!=0)
						{
							alert("Debe terminar la captura del evento para guardar.");
							$( "#dialog-Procesando" ).dialog( "close" );
							return;
						}
				
				   $("#hPolCtroContable").attr("disabled", false);
				   $("#cCentroContable").val(<%=cCentroContable %>);
					
					if (posEditar >= 0) 
					{
						alert("Debe terminar de editar el movimiento antes de continuar");
						$( "#dialog-Procesando" ).dialog( "close" );
						return;
					}
			
					moneyFrmt("Cargos", 0);
					moneyFrmt("Abonos", 0);
					
					$("#Status").val(1);
					$("#resp").val("CAPTURA_POLIZA");
					$("#btnsCtrl").css("visibility","hidden");					
					
					$("#pb_save", parent.window.document).click();	
					
					$("#Tcargos").val(Number(quitaFmt($("#Tcargos").val())));
					$("#Tabonos").val(Number(quitaFmt($("#Tcargos").val())));
					
					$("#tOperacion").val("Guardado Parcial ");
					
					queryFormPost("tDocPolizaBitacora", {async : false});
					
					$("#PolTipo").removeAttr("disabled");
				  
					if(<%=POLIZANUEVA%> && !guardadoParcial){
						queryFormPost("validaCXP", {async : false});		
						if($("#cxp").val() != $("#comparacxp").val()){
							alert("La cuenta por pagar " + $("#cxp").val() +" no es valida.");
						}
						else{		
							creacionDetalle("tPolizaEncabezadoCreate,ActualizaRelGastos,");
						}
					
					}else{
						creacionDetalle("tDocPolizaDetalleDelete,tPolizaEncabezadoUpdate,actualizaTPOLConcepto,");
					}
		
					if (detalleC) 
					{
						alert("Póliza Guardada Correctamente");
					} 
					else 
					{
						alert("Error al guardar la Póliza. Reintente en un momento");
						$( "#dialog-Procesando" ).dialog( "close" );
					}
									
					
					$("#hPolCtroContable").attr("disabled", true);					
					$("#dialog-Procesando" ).dialog( "close" );		
					$("#btnsCtrl").css("visibility","visible");	
								
					
					return;
				}
				catch(ex)
				{
					alert("Error 0013js.\r\r" + ex.message + "\r\rFavor de reportarlo al Administrador del Sistema.");
				}
			}			// fin de cmdGuardarParcial() 
			
					
					
			function cmdGuardar() 
			{
				try
				{
					$("#divRevision").css("visibility","hidden");
					$("#hPolCtroContable").removeAttr('disabled');
					
					$("#cReferenciaPolizaRev").val($("#cReferenciaPoliza").val());
					$("#cReferenciaPolizaAut").val($("#cReferenciaPoliza").val());
	
					if (posEditar >= 0) 
					{
						alert("Debe terminar de editar el movimiento antes de continuar.");
						$("#divRevision").css("visibility","visible");
						return;
					}
					
					if($("#cxp").val()=="" && $("#reclasifica").is(":checked")){
						alert("La cuenta por pagar es obigatoria, favor de capturarla");
						$( "#dialog-Procesando" ).dialog( "close" );	
						return;
					}	
					
					if($("#polManual").val()=="" && $("#reclasificaV").is(":checked")){
						alert("Favor de seleccionar el numero de solicitud del anticipo.");
						$( "#dialog-Procesando" ).dialog( "close" );	
						return;
					}			    
					
					if (id_oper==0 || id_oper==1) 
					{
						try
						{
							var arrCtasBloq = validaCuentasBloqueadas();
							if (arrCtasBloq.length > 0) 
							{
								var ctas = '';
								for ( var i = 0; i < arrCtasBloq.length; i++)
									ctas = arrCtasBloq[i] + "\r";
				
								alert("No se puede avanzar la póliza ya que las siguientes cuentas se encuentran bloqueadas:\r\r" + ctas);
								$("#divRevision").css("visibility","visible");
								return;
							}
							if (!validaPolizaCuadra()) 
							{
								alert("La póliza no Cuadra.\rPor favor valide cargos y abonos.");
								$("#divRevision").css("visibility","visible");
								return;
							}
			
							if (!validaPolizaRegistros()) 
							{
								alert("La póliza debe contener registros.");
								$("#divRevision").css("visibility","visible");
								$("#dialog-Procesando" ).dialog( "close" );	
								return;
							}
		
 							if ($.trim($("#cConcepto").val()) == '') 
							{	
								alert("El Concepto es un dato requerido.");
								$('#cConcepto').focus();
								
								$("#divRevision").css("visibility","visible");
								return;
							}
												
							$("#fAplicacion").removeAttr("disabled");
							$("#hPolCtroContable").removeAttr("disabled");
							$("#PolTipo").removeAttr("disabled");
							
							$("#Tcargos").val(Number(quitaFmt($("#Tcargos").val())));
							$("#Tabonos").val(Number(quitaFmt($("#Tcargos").val())));
							queryFormPost("validaCXP", {async : false});
							
							if(<%=POLIZANUEVA%> && !guardadoParcial){
								queryFormPost("validaCXP", {async : false});
								if($("#cxp").val() != $("#comparacxp").val()){
									//alert("La cuenta por pagar " + $("#cxp").val() +" no es valida.");
								}
								else{								
									creacionDetalle("tPolizaEncabezadoCreate,ActualizaRelGastos,");
								}
							}else{
								creacionDetalle("tDocPolizaDetalleDelete,tPolizaEncabezadoUpdate,actualizaTPOLConcepto,");
							}
							
							if($("#cxp").val() != $("#comparacxp").val()){
								alert("La cuenta por pagar " + $("#cxp").val() +" no es valida.");
							}
							else {	
							if ($.trim($("#cDocumentoHaplicado").val()) == "") 
							{ 
								$("#btnsCtrl").css("visibility","hidden");
								$("#tOperacion").val("Avanzando a revision");	
								queryFormPost("tDocPolizaBitacora", {	async : true  });					     
								
								id_oper_Next=2;
								
								$("#pb_save", parent.window.document).click();						
								$("#btnsCtrl").css("visibility","visible");
							}
							}
						}
						catch(ex)
						{
							alert("Error 0004js.\r\r" + ex.message + "\r\rFavor de reportarlo al Administrador del Sistema.");
						}
					} 
					else if (id_oper == 2)			// revisión 
					{
						try
						{							
							if (Number($("#evelvar").val()) == 1) 					// radio botton = 1, avanza a revisión
							{				
							    $("#tOperacion").val("Avanzando a autorizacion");			
							    queryFormPost("tDocPolizaBitacora", {	async : false  });							    
								 
								id_oper_Next=3;
								
								$("#pb_save", parent.window.document).click();	
							} 
							else 
							{
								$("#tOperacion").val("Regresando de revision a Captura");			
							    queryFormPost("tDocPolizaBitacora", {	async : false  });						
								
								id_oper_Next=1;
								
								$("#pb_save", parent.window.document).click();			
							}
						}
						catch(ex)
						{
							alert("Error 0005js.\r\r" + ex.message + "\r\rFavor de reportarlo al Administrador del Sistema.");
						}
					} 
					else if (id_oper == 3) 		// autorización
					{
						try
						{
							$("#divAutorizar").css("visibility","hidden");
							
							if($("#fComprobacion").val()!="0"){			
								 $("#fComprobacion").removeAttr("disabled");													
								queryFormPost("actualizaViaticos,insertaDetalleViaticos", {async: false});
							}
							//actualizaViaticos,
							
							if ($("#evelvar").val() == 1)
							{
								//****aqui se abre el dialog de firmantes  **--
								
								
								queryFormPost("mCatalogoFirmantesCap_READ,mCatalogoFirmantesRev_READ, mCatalogoFirmantesAutoriza_READ", {async: false }); // voBo - autoriza
								$( "#dialog-firmantes" ).dialog( "open" );	
									
								/**     
								$("#tOperacion").val("Avanzando de autorizacion a consulta");			
							    queryFormPost("tDocPolizaBitacora", {	async : false});
		
								id_oper_Next=4;
								
								$("#pb_save", parent.window.document).click();*/
							}
							else 
							{
								
								$("#tOperacion").val("Regresando de autorizacion a revision");			
							    queryFormPost("tDocPolizaBitacora", {	async : false});
		
								id_oper_Next=2;
								
								$("#pb_save", parent.window.document).click();				
							}
						}
						catch(ex)
						{
							alert("Error 0006js.\r\r" + ex.message + "\r\rFavor de reportarlo al Administrador del Sistema.");
						}
					} 
					else  
					{
						document.liberardocumento.submit();
					}
					
					$("#hPolCtroContable").attr("disabled", true);
				}
				catch(ex)
				{
					alert("Error 0014js.\r\r" + ex.message + "\r\rFavor de reportarlo al Administrador del Sistema.");
				}
			}		// fin de cmdGuardar()
			
			function cambioTotales(contador, cuenta, subcuenta, auxiliar, dmov, cargo, abono, referencia, esParcial,Partida,Cabms,Cucop, cuentaAplicacion) 
			{
				try
				{
					$("#nDato").val(contador);
					$("#Tcargos").val(Number(quitaFmt($("#Tcargos").val())) - Number(quitaFmt(cargo)));
					$("#TcargosLbl").text(formatCurrency($("#Tcargos").val()));
					$("#Tabonos").val(Number(quitaFmt($("#Tabonos").val())) - Number(quitaFmt(abono)));
					$("#TabonosLbl").text(formatCurrency($("#Tabonos").val()));
					$("#Cargos").val(quitaFmt(cargo));
					$("#Abonos").val(quitaFmt(abono));
					$("#nCuenta").val(cuenta);
					$("#dCuenta").val(subcuenta);
					$("#Parciales").val(dmov);
					$("#nCOG").val(Partida);
					$("#nCABMS").val(Cabms);
					$("#nCUCOP").val(Cucop);					
					$("#nCuentaAplicacion").val(cuentaAplicacion);			
					
					$("#nCOG").attr('disabled', 'disabled');
					$("#nCABMS").attr('disabled', 'disabled');
					$("#nCUCOP").attr('disabled', 'disabled');
					$("#buttonCreateEvento").attr('disabled', 'disabled');
					$("#buttonLimpiaEvento").attr('disabled', 'disabled');						
					$("#btnnCOG").hide();						
					$("#btnnCABMS").hide();
					
						
						
					queryFormPost("CONSULTAPOLIZARead", { async : false });
				     tipoSubCuenta();
					if (esParcial=='S')
						$("#movParcialidad").attr("checked", true);
					else
						$("#movParcialidad").removeAttr("checked");
				
					if ($("#nSubCuenta").val() == "RFC") 
					{
						$("#divRFC").show();
				
						$("#lblAuxiliar").text("RFC:");
						$("#sinAuxiliar").hide();
				
						$("#divCTAB").hide();
						$("#divAML").hide();
						$("#divEP").hide();
						$("#divOBGT").hide();
						$("#cIDRFC").val(auxiliar);
						$("#divProy").hide();
						$("#divCRI").hide();
					} 
					
					
					else if ($("#nSubCuenta").val() == "EP") 
					{
						$("#divEP").show();
				
						$("#lblAuxiliar").text("EP:");
						$("#sinAuxiliar").hide();
				
						$("#divCTAB").hide();
						$("#divAML").hide();
						$("#divRFC").hide();
						$("#divOBGT").hide();
						$("#ep").val(auxiliar);
						$("#divProy").hide();
						$("#divCRI").hide();
					} 
					
					else if ($("#nSubCuenta").val() == "OBGT") 
					{
						$("#divOBGT").show();
				
						$("#lblAuxiliar").text("Partida:");
						$("#sinAuxiliar").hide();
				
						$("#divCTAB").hide();
						$("#divEP").hide();
						$("#divRFC").hide();
						$("#divAML").hide();
						$("#hPartidaPol").val(auxiliar);
						$("#divProy").hide();
						$("#divCRI").hide();
					} 
					else if ($("#nSubCuenta").val() == "CTAB") 
					{
						$("#divRFC").hide();
				
						$("#divCTAB").show();
						$("#lblAuxiliar").text("Cta. Ban:");
						$("#sinAuxiliar").hide();
						$("#divEP").hide();
						$("#divOBGT").hide();
						$("#divAML").hide();
						$("#CTABAN").val(auxiliar);
						$("#divProy").hide();
						$("#divCRI").hide();
					} 
					else if ($("#nSubCuenta").val() == "ALM") 
					{ 
						$("#divAML").show();
						$("#lblAuxiliar").text("Almacen:");
						$("#sinAuxiliar").hide();
						$("#divEP").hide();
						$("#divOBGT").hide();
						$("#divCTAB").hide();
						$("#divRFC").hide();
						$("#ALM").val(auxiliar);
						$("#divCRI").hide();
						
					}
					else if ($("#nSubCuenta").val() == "cProyectoFonden") 
					{
						$("#divProy").show();
				
						$("#lblAuxiliar").text("Proyecto:");
						$("#sinAuxiliar").hide();
						$("#divOBGT").hide();
						$("#divCTAB").hide();
						$("#divEP").hide();
						$("#divRFC").hide();
						$("#divAML").hide();
						$("#hProyectoPol").val(auxiliar);
						$("#divCRI").hide();
					}
					else if ($("#nSubCuenta").val() == "CRI") 
					{
						$("#divCRI").show();
						
						$("#divRFC").hide();
						$("#lblAuxiliar").text("CRI:");
						$("#sinAuxiliar").hide();
				
						$("#divCTAB").hide();
						$("#divAML").hide();
						$("#divEP").hide();
						$("#divOBGT").hide();
						$("#cCRI").val(auxiliar);
						$("#divProy").hide();
						
					} 
				}
				catch(ex)
				{
					alert("Error 0015js.\r\r" + ex.message + "\r\rFavor de reportarlo al Administrador del Sistema.");
				}
			}			// fin de cambioTotales()
			
			function folio() 			//   Carga informacion de la poliza. según el paso en  el que se encuentra la poliza.
			{		
				try
				{
					var drenglon = 1;
					var Concepto;					
					var cTipoPoliza = "";
				
															
						queryFormPost("datosPolizaManual", {async:false});							
						
						$("#id_caso").val("<%=c.getIdCaso()%>");
						$("#usuario").val("<%=nomUsuario%>");
					    $("#nFolioPolizatmp").val($("#nFolioPoliza").val());
					    $("#TcargosLbl").text( formatCurrency( $("#Tcargos").val() ) );
						$("#TabonosLbl").text( formatCurrency( $("#Tabonos").val() ) );
						$("#dCuenta").attr("disabled","disabled");
						
						renglon = $("#ndocrenglon").val();			
				        id_oper=<%=id_oper%>; //parseInt($("#Id_Caso_Oper").val(), 10);		 
						cTipoPoliza = $('#PolTipo').val();						
						totalRenglon=renglon;						
				  		
					if ( $("#cDocumentoHaplicado").val() == "P" )
					{
				    	alert("El documento se encuentra en Proceso de Aplicación Contable.\r\rFavor de esperar.");
				    	document.liberardocumento.submit();
				    	return;
					} else if ( $("#cDocumentoHaplicado").val() == "S" && id_oper==1 )
					{
				    	alert("El documento se encuentra Aplicado Contablemente se avanzará a Revisión.\r\rFavor de esperar.");
				    	queryFormPost("tdocPolizaUsuarioRevision", {async:false});	
				    	cargaDataTable();
				    	avanzaRevision();
				    	document.liberardocumento.submit();
				    	return;
					} 
					
					/*
					ESTA PARTE SE VA A IMPLEMENTAR EN EL DIALOG CON EL VALOR DE UAUTORIZADO
					if(formatoPol==1 && !capxCuentas )
						{
						alert("No cuenta con permisos para modificar pólizas por Cuentas.");
						document.liberardocumento.submit();
						return;
						}*/
				//	alert(capxCuentas);
					if(capxCuentas) 
						$("#nomFormatoPol").text("(por cuentas)");
						
					if(id_oper==1 || id_oper==0)
					{
						$("#tblRevisionAutirzación").remove();	
						$("#tblImpresionDePoliza").remove();
						$('<input>').attr({
						    type: 'hidden',
						    id: 'nFolioPoliza',
						    name: 'nFolioPoliza'
						}).appendTo('form');
						
						//$("#txtObservaciones").remove();				
						$("#lblguardarEstatus").remove();
						$("#tdObservaciones").remove();
						$("#tdDescripcionEvento").attr("colspan",3); 
						$("#Agregar").hide();
						$("#Limpia1").hide();  
				       
						if($("#nFolioPolizatmp").val()>0)
						{        		
							document.getElementById('cancelar').style.visibility='hidden';
							$("#PolTipo").attr("disabled", "disabled");							
						}	
						
					    if(renglon>1)   deshabilitarBotones();
					    
					    
					    if(capxCuentas) 
						{					    	
					     	$("#lgtipoPoliza").text("Cuenta");					    	
					     	$(".elementosCuenta").hide();	    	
					    	$("#divGrupo1").hide();
					    	$("#botoneraEventos").hide();
					    	$("#divCuenta").show();
					    	$("#divReclasifica").show();
					    	$("#tdButtons").show();		    	 	
					    	$("#nFormatoPoliza").val(1);
					    	$("#nCuenta").prop("readonly",false);
					    	
					    	//renumeraEventoCuentas();
					    	$("#nGrupo").val("");
							$("#nSubGrupo").val("");
							$("#Limpia1").val("Eliminar Cuenta");
						}else
					    {
					    	if(id_oper==1)
					    		{ 	
					    			$("#buttonLimpiaEvento").hide();					    			
					    		}
					    	
					    }
// 						$("input.autoCompletaSyC").subIniciaAutoCompleta();	
					   }
					
					else if(id_oper==2)
					   {		
						
						try
						{								
							$("#lbSubTitulo").text(" - Revisión Póliza");	
							$("#cConcepto").attr("disabled", "disabled");
							$("#EF").attr("disabled", "disabled");
							$("#PolTipo").attr("disabled", "disabled");
							
							$("#divGrupo1").remove();
							$("#botoneraEventos").remove();
							$("#btnsCtrl").remove();
							$("#tdDescripcionEvento").remove();
							$("#tdObservaciones").attr("colspan",3);
							$("#lbObservaciones").css("visibility","visible");								
							$("#tblCaptura").css("background-color","WhiteSmoke");
							$("#txtObservaciones").text("");
							$("#divReclasifica").hide();	
							
							$("#tblImpresionDePoliza").remove();						
						}
						catch(ex)
						{
							alert("Error 00016js.\r\r" + ex.message + "\r\rFavor de reportarlo al Administrador del Sistema.");
						}
						 
					   }
					else if(id_oper==3)
					   {
						try
						{									
							$("#lbSubTitulo").text(" - Autorización Póliza");	
							$("#lblguardarEstatus").text("Autorización Póliza - ");
							$("#cConcepto").attr("disabled", "disabled");
							$("#EF").attr("disabled", "disabled");
							$("#PolTipo").attr("disabled", "disabled");
							
							$("#divGrupo1").remove();
							$("#botoneraEventos").remove();
							$("#btnsCtrl").remove();
							$("#tdDescripcionEvento").remove();
							$("#tdObservaciones").attr("colspan",3);
							$("#lbObservaciones").css("visibility","visible");								
							$("#tblCaptura").css("background-color","WhiteSmoke");
							$("#txtObservaciones").text("");
							$("#divReclasifica").hide();
							
							$("#tblImpresionDePoliza").remove();
						}
						catch(ex)
						{
							alert("Error 00013js.\r\r" + ex.message + "\r\rFavor de reportarlo al Administrador del Sistema.");
						}
					    
					   }		
					   else if(id_oper==4){
					   		$("#divImpirme").css("disabled","true");
					   	
					   }			
															
					if ($.trim($("#cComentarios").val()) != '') alert("Observaciones:  "+$("#cComentarios").val());							
					
					$("#dialog-Procesando" ).dialog( "close" );	
					
					$("#container").css("visibility","visible");					
					
				}
				catch(ex)
				{
					alert("Error 00014js.\r\r" + ex.message + "\r\rFavor de reportarlo al Administrador del Sistema.");
				}
			}			// fin de folio()
			
				
			function fnLoad() 
			{	
				try
				{														
					querySelectPost("catalogoPolTipo", "PolTipo", {async: false});
					querySelectPost("catalogoEjercicioFiscalRead", "EF", {async : false});					
					querySelectPost("CCentroContablePolizaRead","hPolCtroContable", {async : false});	
					querySelectPost("catalogoPolizaReclasificarRead", "polManual", {async : false});
									
					$("#PolTipo option[value='']").remove();
					$("span.ui-dialog-title").text('Procesando');																		
							
					$("#cUnidadResponsable").val("<%=cUR%>");					
					$("#fAplicacion").val("<%=fechaAplicacion%>");
					$("#fCaptura").val("<%=fCaptura%>");					
					$("#nMes").val(<%=(nmesDocumento==0)?mesAbierto:nmesDocumento%>);
					$("#dMes").val(<%=mesAbierto%>);
					
					
					$("#fAplicacionLbl").val($("#fAplicacion").val());		
					$("#fCatpuraLbl").text($("#fCaptura").val());
					
					//$("#divReclasificacionV").hide();
					//$("#divReclasificacion").hide();
					
					if(  $("#nMes").val() != $("#dMes").val() && parseInt(  $("#dMes").val(), 10) != 13  )
					{											
						var nombreMes=["ENERO","FEBRERO","MARZO","ABRIL","MAYO","JUNIO","JULIO","AGOSTO","SEPTIEMBRE","OCTUBRE","NOVIEMBRE","DICIEMBRE"];
								
						alert("El mes de "  + nombreMes[$("#nMes").val()-1] + " no esta abierto. El mes actual abierto es " +nombreMes[$("#dMes").val()-1] + 
								"\nNo podra realizar alguna operación en esta póliza hasta que el mes " + nombreMes[$("#nMes").val()-1] + " este abierto"); +
															
						$(":input").each(function()
							{
								if( $(this).attr("type")== 'text' || $(this).attr("type")== 'hidden' || $(this).attr("type")== 'textarea')
									$(this).attr('readonly', 'readonly');
								else
									$(this).attr('disabled', 'disabled');
							});
						$("#cerrarCaso").removeAttr("disabled");
						$("#imprimePoliza").removeAttr("disabled");
						
					}					
					
					if( $("#nMes").val() != 13 )
					{
						$("#Periodo13Div").hide();								
						$("#AjusteCapt").hide();							
						$("#mes13").val(0);								
						$("#periodo13").val("N");
						
					}else
					{
						$("#Periodo13Div").show();								
						$("#periodo13").val("S");
						$("#AjusteCapt").show();							
						$("#mes13").val(1);
					}						
							
							
					moneyFrmt("Cargos", "0");
					moneyFrmt("Abonos", "0");
			     
					folio();					
					
					$("#Agregar").button();
					$("#Limpia1").button();
					$("#cancelar").button();
					$("#Guarda2").button();
					$("#Guarda").button();
					$("#cerrarCaso").button();
					$("#Modificar").button();									
					
					$("#hPolCtroContable").attr("disabled","disabled");						
					$("#nomLargoUsuario").val("<%out.print(usuario.getNombre());%>");
					$("#OPERADOR").val("<%out.print(usuario.getNombre());%>"); 
					$("#cadFolio").val("POLI-C" + $("#cCentroContable").val() + "-"+ $("#folioDocumento").val());
				 }
				catch(ex)
				{
					alert("Error 0017js.\r\r" + ex.message + "\r\rFavor de reportarlo al Administrador del Sistema.");
				}
			}			// fin de fnLoad() 
			
			function onLoadPlantilla(id_oper)
			{
				try
				{				
					$("#pCuentas").attr("class","display");
					$("#salir").button();
					<%
					if( (!POLIZANUEVA && UAUTORIZADO) || (POLIZANUEVA &&(cPolizaManualEventos ^ cPolizaManualCuenta )))
					{
						out.println("setTimeout(\"fnLoad();\",2000);");					
						if(cPolizaManualEventos)
						out.println("capxCuentas=false;");						
					}
					%>								
				}
				catch(ex)
				{
					alert("Error 0018js.\r\r" + ex.message + "\r\rFavor de reportarlo al Administrador del Sistema.");
				}
			}			// fin de onLoadPlantilla()
			
			function ResponsableSiguiente(id_oper){}	
			function OperacionSiguiente(id_oper) {}	
			function onPostDisplay(id_oper){}

			function onSubmit()				//validaciones del boton guardar
			{	
				try
				{
			  		var p = window.parent;
			  		var valida_campos = true;
					
					try{
						
						$( "#dialog-Procesando" ).dialog( "open" );		
						
						p.gestion.setOperador( $("#OPERADOR").val() );	
						
						if (id_oper==0 || id_oper==1 )
						{					
							p.gestion.setFolio( $("#FOLIO").val() );
							p.gestion.setFechaDocumento('<%=today%>');
							p.gestion.setEjercicioFiscal( $("#EF").val() );
							p.gestion.setConceptoMov("Aplicación Poliza");
							p.gestion.setMoneda("MXP");							
						}				
						
					$("#btnsCtrl").css("visibility","hidden");
					
			
					} catch (e) {
						window.alert("onSubmit: Error: " + e.message);
						return false;
					}
					return valida_campos;
				}
				catch(ex)
				{
					alert("Error 0019js.\r\r" + ex.message + "\r\rFavor de reportarlo al Administrador del Sistema.");
				}
			}				// fin de onSubmit()
			
			function limpiaEvento()
			{	
				try
				{
					$("#nGrupo").val("");
					$("#nSubGrupo").val("");
					$("#nEvento").val("");
					$("#nCABMS").val("");
					$("#nCUCOP").val("");
					$("#nCOG").val("");
					document.getElementById("Label2").innerHTML = ""; 	
					document.getElementById("Label3").innerHTML = "";					
					document.getElementById("Label4").innerHTML = "";
					document.getElementById("Label5").innerHTML = "";					
					document.getElementById("Label6").innerHTML = "";
				}
				catch(ex)
				{
					alert("Error 0020js.\r\r" + ex.message + "\r\rFavor de reportarlo al Administrador del Sistema.");
				}
			}				// fin de limpiaEvento()
			
			function selectCuenta()
			{		
				try
				{
					queryFormPost({
					queryName : "leeAttrCuenta",				
							async : false,
							callback : function() {
								if ($("#dCuenta").val() == "") {
									alert("La cuenta " + $("#nCuenta").val() + " no existe o no es una cuenta de aplicación.");
									$("#nCuenta").val('');
									$("#nCuentaAplicacion").val("");
									$("#nCuenta").focus();
									return false;
								}
								tipoSubCuenta();								
								
								if(posEditar<0)
									{
									$("#Agregar").show();
									$("#Limpia1").show();		
									}
								
								return true;
							}
						});
				}
				catch(ex)
				{
					alert("Error 0021js.\r\r" + ex.message + "\r\rFavor de reportarlo al Administrador del Sistema.");
				}
			}				// fin de selectCuenta()
			
			function cambioALM() 
			{		
				try
				{
					querySelectPost("CatalogoAlmacenPolizaRead", "ALM", {async : false});
					
					$("#nIdAlmacen").val($("#ALM").val());
					
					if ($("#nIdAlmacen").val() == '')	
					{
						querySelectPost("CatalogoAlmacenVacioRead", "ALM", {async : false});
					}
				}
				catch(ex)
				{
					alert("Error 0022js.\r\r" + ex.message + "\r\rFavor de reportarlo al Administrador del Sistema.");
				}
			}				// fin de cambioALM()
			
			
			function cmdBorrar() 
			{	
				try
				{
					$("#tOperacion").val("Póliza descartada");
					queryFormPost("descartaTdocPoliza,tDocPolizaBitacora,ActualizaRelGastos0", {async : false});				
					
					$("#pb_cancel", parent.window.document).click();					
				}
				catch(ex)
				{
					alert("Error 0023js.\r\r" + ex.message + "\r\rFavor de reportarlo al Administrador del Sistema.");
				}
			}				// fin de cmdBorrar()
			
			function cmdCierraCaso() 
			{	
				try
				{
					$( "#dialog-Procesando" ).dialog( "open" );	
					$("#btnsCtrl").css("visibility","hidden");
					$("#cerrarCaso").css("visibility","hidden");											
					$("#btnsCtrl").css("visibility","visible");
					$("#cerrarCaso").css("visibility","visible");	
					
					if (confirm("Al cerrar el documento podrá ser trabajado por otro usuario, ¿desea continuar?"))
					{			
						$("#tOperacion").val("Póliza liberada");
						queryFormPost("tDocPolizaBitacora", {async : false}); 
						liberardocumento.submit();
					}
					else 
					{
						$("#dialog-Procesando" ).dialog( "close" );					
					}											
				}
				catch(ex)
				{
					alert("Error 0024.1js.\r\r" + ex.message + "\r\rFavor de reportarlo al Administrador del Sistema.");
				}
			}				// fin de cmdCierraCaso()
			
			function formatCurrency(txt) 
			{
				try
				{
					$("#tmpInpt").val(txt);
					$("#tmpInpt").formatCurrency();
					return $("#tmpInpt").val();
				}
				catch(ex)
				{
					alert("Error 0025js.\r\r" + ex.message + "\r\rFavor de reportarlo al Administrador del Sistema.");
				}
			}				// fin de formatCurrency(txt)
			
			
			
			
			function avanzaAutorizacion()
			{
				try
				{
					$("#Status").val(3);  		
					$("#resp").val("AUTORIZA_POLIZA"); 
										  
					queryFormPost("ActualizaCDATOPolizaUpdate,ActualizaCOPERPolizaUpdate2,tdocPolizaUsuarioRevision", 
					{
						async : false, 
						callback : function() 
						{		  		     	
					  		alert("Documento avanzado a estatus de Autorización correctamente.");
					  		document.liberardocumento.submit();
					  		done=true;
					  		return;
							  		
							     	
							if (usuarioAutorizaPoliza) 
							{											
								folio();											
							} 
							else 
							{	  
								document.liberardocumento.submit();
							}
						}
					});					
					if(!done)
					{
						alert(ERROPER);   	  
					   	 cerrar();
					   	 return;
					}					
				}
				catch(ex)
				{
					alert("Error 0027js.\r\r" + ex.message + "\r\rFavor de reportarlo al Administrador del Sistema.");
				}				   
			}				 
			function reseteaCaptura()
			{
				try
				{
					queryFormPost("tDocPolizaCancelDetalleDelete,tDocPolizaCancelEncabezadoDelete,tPolizaCancelDelete,tdocpolizaCancelEncabezadoCreate,tdocpolizaCancelDetalleCreate,tPolizaCancelCreate,RestauraCasoDatos", 
					{
						async : false, 
						callback : function() 
						{
							done=true; 
							polizaCancel=true; 
						}
					});
					
					if(!done)
					{
						alert(ERROPER);
						//	cerrar();
						return;
					}
					done=false;
				}
				catch(ex)
				{
					alert("Error 0030js.\r\r" + ex.message + "\r\rFavor de reportarlo al Administrador del Sistema.");
				}
			}				// fin de reseteaCaptura()			
			
		function avanzaRevision()
			{
				try
				{
					$("#Status").val(2);  		
		   		   	$("#resp").val("REVISION_POLIZA"); 
		   						        
				    queryFormPost("ActualizaCOPERPolizaUpdate2",//,copiaDescripcionMovimiento",ActualizaCDATOPolizaUpdate,  --se cometa este crud por que aplica contablemente en la autorizacion y para este caso aun no hay movimientos
					{
					async : false, 
					callback : function() 
			            {
				         //comprobacionPoliza();  						 
						 done=true;
						 document.liberardocumento.submit();
						 
						 return;
						 
						 
									        
						if (usuarioRevisionPoliza) {
							  folio();

							$("#nCuenta").val("");
							$("#nCuentaAplicacion").val("");
							$("#Cargos").val("");
							$("#Abonos").val("");
							$("#valSubCuenta").val("");
							$("#cComentariosRev").val("");
					
						} else {
							
							document.liberardocumento.submit();
						}
				        }
					});					
					
					if(!done)
  		 			{
						alert(ERROPER);   	  
      					cerrar();
      					return;
					}
					//else cerrar();
				}
				catch(ex)
				{
					alert("Error 0030js.\r\r" + ex.message + "\r\rFavor de reportarlo al Administrador del Sistema.");
				}		
			}			// fin de avanzaRevision()
			
			function regresaCaptura()
			{
				try
				{
					$("#Status").val(1);  		
			  		$("#resp").val("CAPTURA_POLIZA"); 
			  		$("#usuario").val(""); 
			  						      
					//queryFormPost("Restauratpoliza,RestauraEncabezado,ActualizaCOPERPolizaUpdate2,tmovimientoDocPolDelete,tmovimientoDocPolCancelDelete,tDocPolizaCancelDetalleDelete,tDocPolizaCancelEncabezadoDelete,tPolizaCancelDelete,tdocPolizaComentarioCaptUpdate", 
					queryFormPost("RestauraEncabezado,ActualizaCOPERPolizaUpdate2,tdocPolizaComentarioCaptUpdate", 
					{
					async : false, 
					callback : function() 
						{			   	  
							alert("Documento regresado a estatus de Captura correctamente.");
							done=true;
							document.liberardocumento.submit();
							return;			     
							       
							if (usuarioCapturaPoliza)
							{			    	
								$('#pCuentas').dataTable().fnClearTable();			
								
								folio(); 
								polizaCancel=false; 							
							
								$("#cComentarios").val($("#cComentariosRev").val());
								$("#cancelar").attr("disabled", true);
								$("#cDocumentoHaplicado").val("");						
							} 
							else 
							{		
								document.liberardocumento.submit();		
							}			         
						}
					});    			        
							
					if(!done)
				 	{
						queryFormPost("RestauraCasoDatosAplicado", { async : false});						
						alert(ERROPER);   	  
			   					cerrar();
			   					return;
					}		 		
				}
				catch(ex)
				{
					alert("Error 0031js.\r\r" + ex.message + "\r\rFavor de reportarlo al Administrador del Sistema.");
				}
			}					// fin de regresaCaptura()		
			
			function cerrar()
			{
				try
				{
					$( "#dialog-Procesando" ).dialog( "close" );
				}
				catch(ex)
				{
					alert("Error 0032js.\r\r" + ex.message + "\r\rFavor de reportarlo al Administrador del Sistema.");
				}
			}					// fin de cerrar()
			
			
			function onFocusMoney(input)
			{
				try
				{
					if (Number(input.value) == 0) {
						input.value = '';
					} 
					else if (input.value > 0) 
					{
						input.select();
					}
				}	
				catch(ex)
				{
					alert("Error 0034js.\r\r" + ex.message + "\r\rFavor de reportarlo al Administrador del Sistema.");
				}
			}				// fin de onFocusMoney()
			
			function onBlurMoney(input) 
			{
				try
				{
					if (input.value == 0 || input.value == '') 	input.value = '0.0';
				}	
				catch(ex)
				{
					alert("Error 0035js.\r\r" + ex.message + "\r\rFavor de reportarlo al Administrador del Sistema.");
				}
			}				// fin de  onBlurMoney()
			
			function Sinfrmt(fld) 
			{
				try
				{
					var valcol = fld.value;
					valcol = valcol.replace("$", "");
					valcol = valcol.replace(/,/g, "");
					$("#" + fld.id).val(valcol);
				}	
				catch(ex)
				{
					alert("Error 0036js.\r\r" + ex.message + "\r\rFavor de reportarlo al Administrador del Sistema.");
				}
			}				// fin de Sinfrmt(fld)
			
			function cambiafrmt(fld) 
			{
				try
				{
					$("#" + fld.id).val(formatCurrency(quitaFmt($("#" + fld.id).val())));
				}	
				catch(ex)
				{
					alert("Error 0037js.\r\r" + ex.message + "\r\rFavor de reportarlo al Administrador del Sistema.");
				}
			}				// fin de cambiafrmt(fld)
			
			function moneyFrmt(id, val) 
			{
				try
				{
					val = quitaFmt(val);
					$("#" + id).val(val);
					$("#" + id).formatCurrency();
				}	
				catch(ex)
				{
					alert("Error 0038js.\r\r" + ex.message + "\r\rFavor de reportarlo al Administrador del Sistema.");
				}
			}				// fin de moneyFrmt(id, val) 
			
			function quitaFmt(val)
			{
				try
				{
					val = String(val);
					val = val.replace("$", "");
					val = val.replace(/,/g, "");
				
					if (val.indexOf("(") >= 0) 
					{
						val = val.replace("(", "");
						val = val.replace(")", "");
						val = "-" + val;
					}
					return val;
				}	
				catch(ex)
				{
					alert("Error 0039js.\r\r" + ex.message + "\r\rFavor de reportarlo al Administrador del Sistema.");
				}
			}				// fin de quitaFmt(val)	
			
			function limpiaCuentas()
			{
				try
				{
					$("#valSubCuentaCompar").val("");
					$("#dCuenta").val("");
					$("#nCuenta").val("");
					$("#nCuentaAplicacion").val("");
				}	
				catch(ex)
				{
					alert("Error 0040js.\r\r" + ex.message + "\r\rFavor de reportarlo al Administrador del Sistema.");
				}
			}				// fin de limpiaCuentas()
			
			function ocultaSubCuentas() 
			{
				try
				{
					$("#cIDRFC").val("");
					$("#ep").val("");
					$("#CTABAN").val("");
					$("#ALM").val("");
					$("#divAML").hide();
					$("#divCTAB").hide();
					$("#divRFC").hide();
					$("#divEP").hide();
					$("#divOBGT").hide();
					$("#sinAuxiliar").show();
					$("#lblAuxiliar").text("Auxiliar:");
					$("#divCRI").hide();
				}	
				catch(ex)
				{
					alert("Error 0041js.\r\r" + ex.message + "\r\rFavor de reportarlo al Administrador del Sistema.");
				}
			}				// fin de ocultaSubCuentas()
			
			function deshabilitarBotones()
			{
				try
				{					
					$('#nGrupo').attr("disabled", "disabled");
					$('#nSubGrupo').attr("disabled", "disabled");
					$('#nEvento').attr("disabled", "disabled");
					
					$('#btnnGrupo').attr("disabled", "disabled");
					$('#btnnGrupo').attr("class", "");
					$('#btnnSubGrupo').attr("disabled", true);
					$('#btnnSubGrupo').attr("class", "");
					$('#btnnEvento').attr("disabled", true);
					$('#btnnEvento').attr("class", "");		
				}	
				catch(ex)
				{
					alert("Error 0042js.\r\r" + ex.message + "\r\rFavor de reportarlo al Administrador del Sistema.");
				}
			}					// fin de deshabilitarBotones()
			
			function updateAjuste() 
			{
				try
				{
					$("#nTipoAjusteRev").val($("#nTipoAjuste").val());
					$("#nTipoAjusteAut").val($("#nTipoAjuste").val());
				}	
				catch(ex)
				{
					alert("Error 0043js.\r\r" + ex.message + "\r\rFavor de reportarlo al Administrador del Sistema.");
				}
			}				// fin de updateAjuste() 
			
			function cierraAyuda() 
			{
				try
				{
					$("#divACnCuenta").hide();
				}	
				catch(ex)
				{
					alert("Error 0044js.\r\r" + ex.message + "\r\rFavor de reportarlo al Administrador del Sistema.");
				}
			}				// fin de cierraAyuda() 
			
			function blurNCta() 
			{
				try
				{
					$("#divACnCuenta").hide();
					var nCta = $("#nCuenta").val();
					var arrNCta = [ '00000', '00000', '00000', '00000' ];
					var ceros = '00000';
					if (nCta.indexOf("-") > 0) 
					{
						var quintetos = nCta.split("-");
						for ( var i = 0; i < quintetos.length; i++) 
						{
							var ctaElem = ceros.substring(quintetos[i].length) + quintetos[i];
							arrNCta[i] = ctaElem;
						}
					} else
						arrNCta[0] = ceros.substring(nCta.length) + nCta;
				
					nCta = arrNCta.join("-");
					$("#nCuenta").val(nCta);
				
					if ($("#nCuenta").val().length == 23) 
					{
						queryFormPost({
							queryName : "leeAttrCuenta",
							async : false,
							callback : function() {
								if ($("#dCuenta").val() == "") {
									alert("La cuenta " + $("#nCuenta").val()
											+ " no existe o no es una cuenta de aplicacion.");
									$("#nCuenta").val('');
									$("#nCuentaAplicacion").val("");
									$("#nCuenta").focus();
									return false;
								}
								tipoSubCuenta();
								$("#divACnCuenta").hide();
								return true;
							}
						});
				
					}
					return false;
				}	
				catch(ex)
				{
					alert("Error 0045js.\r\r" + ex.message + "\r\rFavor de reportarlo al Administrador del Sistema.");
				}
			}				// fin de blurNCta()
			
			function tipoSubCuenta() 
			{
				try
				{
					$("#file1").hide();
				
					if ($("#nSubCuenta").val() == "RFC") 
					{
						$("#lblAuxiliar").text("RFC:");
						$("#sinAuxiliar").hide();
						$("#divRFC").show();
						$("#cIDRFC").focus();
						$("#divEP").hide();
						$("#divOBGT").hide();
						$("#divCTAB").hide();
						$("#divAML").hide();
						$("#divProy").hide();
						$("#divCRI").hide();
					} else if  ($("#nSubCuenta").val() == "EP") 
					{
						$("#lblAuxiliar").text("EP:");
						$("#sinAuxiliar").hide();
						$("#divEP").show();
						$("#ep").focus();
						$("#ep").val("");
						$("#divRFC").hide();
						$("#divOBGT").hide();
						$("#divCTAB").hide();
						$("#divAML").hide();
						$("#divProy").hide();
						$("#divCRI").hide();
					}  else if  (($("#nSubCuenta").val() == "OBGT") &&  (capxCuentas==true)  ) 
					{
						$("#lblAuxiliar").text("Partida:");
						$("#sinAuxiliar").hide();
						$("#divOBGT").show();
						$("#hPartidaPol").focus();
						$("#divRFC").hide();
						$("#divCTAB").hide();
						$("#divEP").hide();
						$("#divAML").hide();
						$("#divProy").hide();
						$("#divCRI").hide();
					}else if ($("#nSubCuenta").val() == "CTAB") 
					{
						$("#divRFC").hide();
				
						$("#divCTAB").show();
						$("#lblAuxiliar").text("Cta. Ban:");
						$("#sinAuxiliar").hide();
						$("#idCuenta").focus();
						$("#divEP").hide();
						$("#divOBGT").hide();
						$("#divAML").hide();
						$("#divProy").hide();
						$("#divCRI").hide();
					} else if ($("#nSubCuenta").val() == "ALM") 
					{
						$("#hPolCtroContable").attr("disabled", false);
						cambioALM();
						$("#hPolCtroContable").attr("disabled", true);
						$("#divAML").show();
						$("#ALM").focus();
						$("#lblAuxiliar").text("Almacen:");
						$("#sinAuxiliar").hide();
						$("#divEP").hide();
						$("#divCTAB").hide();
						$("#divRFC").hide();
						$("#divProy").hide();
						$("#divCRI").hide();
					}else if ($("#nSubCuenta").val() == "cProyectoFonden") 
					{
						$("#divRFC").hide();
						$("#divCTAB").hide();
						$("#divProy").show();
						$("#lblAuxiliar").text("Proyecto:");
						$("#sinAuxiliar").hide();
						//$("#idCuenta").focus();
						$("#divEP").hide();
						$("#divOBGT").hide();
						$("#divAML").hide();
						$("#divCRI").hide();
					} else if ($("#nSubCuenta").val() == "CRI") 
					{
						$("#sinAuxiliar").hide();
						$("#divRFC").hide();
						$("#cIDRFC").hide();
						$("#divEP").hide();
						$("#divOBGT").hide();
						$("#divCTAB").hide();
						$("#divAML").hide();
						$("#divProy").hide();
						$("#divCRI").show();
					} else 
					{
						$("#valSubCuenta").val("") ;
						ocultaSubCuentas();
						$("#Cargos").focus();
					}
					$(".elementosCuenta").show();
				}	
				catch(ex)
				{
					alert("Error 0046js.\r\r" + ex.message + "\r\rFavor de reportarlo al Administrador del Sistema.");
				}
			}				// fin de tipoSubCuenta()
				
			function blurCtaBanc() 
			{
				try
				{
					if ($("#idCuenta").val() != '')
						queryFormPost({
							queryName : "leeCtaBan",
							async : false,
							callback : function() {
								if ($("#CTABAN").val() == '') {
									alert("No se encontro la cuenta con indice "
											+ $("#idCuenta").val());
									return false;
								} else {
									if (ultimoTipoMovimiento == 'A')
										$("#Abonos").focus();
									else
										$("#Cargos").focus();
									return false;
								}
							}
						});
				}	
				catch(ex)
				{
					alert("Error 0047js.\r\r" + ex.message + "\r\rFavor de reportarlo al Administrador del Sistema.");
				}
			}				// fin de blurCtaBanc()			
		
			
			
			function validaCuentasBloqueadas() 
			{
				try
				{
					var rows = oTableCuen.fnGetData();
					var rowCount = rows.length;
				
					var cargoGrd = '';
					var abonoGrd = '';
					var cuentaGrd = '';
					var cuentasDeCargo = '';
					var cuentasDeAbono = '';
					var tokenCargos = '';
					var tokenAbonos = '';
					var arrRes = Array();
				
					for ( var i = 0; i < rowCount; i++) {
						var row = rows[i];
						var cuentaGrd = row[1];
						var cargoGrd = row[5];
						var abonoGrd = row[6];
						var op;
				
						if (Number(quitaFmt(cargoGrd)) != 0) {
							cuentasDeCargo += tokenCargos + "'" + cuentaGrd + "'";
							tokenCargos = ",";
						} else if (Number(quitaFmt(abonoGrd)) != 0) {
							cuentasDeAbono += tokenAbonos + "'" + cuentaGrd + "'";
							tokenAbonos = ',';
						}
					}
					var nMes = $("#nMes").val();
					var cCentroContable = $("#cCentroContable").val();
				
					if (rowCount>0)  
					$.ajax({
						type: "POST",
						url : '../CuentaContable/VerificaBloqueo',
						dataType : 'json',
						data : {
							"accion" : "VerificaCuentas",
							"cuentasCargo" : cuentasDeCargo,
							"cuentasAbono" : cuentasDeAbono,
							"nMes" : nMes,
							"cCentroContable" : cCentroContable
				
						},
						async : false,
						success : function(RS) {
							var index, data, col;
							if (RS.success == "true") {
								index = 1;
								while (true) {
									data = eval("RS.data_" + index++);
									if (!data)
										break;
									for ( var i = 0; i < data.length; i++) {
										col = data[i];
										for ( var name in col) {
											arrRes.push(eval("col." + name));
										}
									}
								}
							}
						},
						error : function(xhr, textStatus, errorThrown) {
							alert("Advertencia: " + xhr.responseText + "\nEstatus: "
									+ textStatus + "\n" + errorThrown);
							r = true;
						}
					});
				
					return arrRes;
				}	
				catch(ex)
				{
					alert("Error 0048js.\r\r" + ex.message + "\r\rFavor de reportarlo al Administrador del Sistema.");
				}
			}				// fin de validaCuentasBloqueadas()
			
			function validaCuenta(nCuenta, nMes, cCentroContable, operacion) 
			{
				try
				{  
					var r = false;
				
					$.ajax({
						url : '../CuentaContable/VerificaBloqueo',
						dataType : 'json',
						data : {
							"accion" : "VericaBloqueoCuenta",
							"nCuenta" : nCuenta,
							"nMes" : nMes,
							"cCentroContable" : cCentroContable,
							"operacion" : operacion
				
						},
						async : false,
						success : function(json) {
							r = json.data_1.result == 'true';
							if (r) {
								alert("La cuenta " + nCuenta
										+ " est\u00E1 bloqueada en el mes " + nMes + " para "
										+ ("C" == operacion ? "cargos" : "abonos")
										+ "\nPor favor seleccione otra cuenta.");
								$("#nCuenta").focus();
							}
						},
						error : function(xhr, textStatus, errorThrown) {
							alert("Advertencia: " + xhr.responseText + "\nEstatus: "
									+ textStatus + "\n" + errorThrown);
							r = true;
						}
					});
					return r;
				}	
				catch(ex)
				{
					alert("Error 0049js.\r\r" + ex.message + "\r\rFavor de reportarlo al Administrador del Sistema.");
				}
			}				// fin de validaCuenta()		
			
			function limpiaCambsCucop()
			{
				try
				{
					document.getElementById("nCABMS").value="";
					document.getElementById("nCUCOP").value="";
				}	
				catch(ex)
				{
					alert("Error 0050js.\r\r" + ex.message + "\r\rFavor de reportarlo al Administrador del Sistema.");
				}
			}				// fin de limpiaCambsCucop()	
		
			function ctaKeyDwn(e, id) 
			{
				try
				{
					var tecla = ((document.all) ? e.keyCode : e.which);
					if (tecla != 9) {
						if (id == 'idCuenta') {
							$("#CTABAN").val('');
							return true;
						} else if (id == 'CTABAN') {
							$("#idCuenta").val('');
							return true;
						} else {
							return true;
						}
					}
				}	
				catch(ex)
				{
					alert("Error 0051js.\r\r" + ex.message + "\r\rFavor de reportarlo al Administrador del Sistema.");
				}
			}				// fin de ctaKeyDwn()	
			
			function catchTab(e, id) 
			{
				try
				{
					var tecla = ((document.all) ? e.keyCode : e.which);
					if (tecla == 9) {
						if (id == "Cargos") {
							if ($("#Cargos").val() == "" || quitaFmt($("#Cargos").val()) == 0) {
								$("#Abonos").focus();
							} else
								$("#Parciales").focus();
							return false;
						} else if (id == 'Abonos') {
							$("#Parciales").focus();
							return false;
						} else if (id == 'nCuenta') {
							blurNCta();
							return false;
						}
					}
					return true;
				}	
				catch(ex)
				{
					alert("Error 0052js.\r\r" + ex.message + "\r\rFavor de reportarlo al Administrador del Sistema.");
				}
			}				// fin de catchTab(e, id) 
			
					
			function validaPolizaCuadra() 
			{
				try
				{
					return Number(quitaFmt($("#Tcargos").val())) == Number(quitaFmt($("#Tabonos").val()));
				}	
				catch(ex)
				{
					alert("Error 0054js.\r\r" + ex.message + "\r\rFavor de reportarlo al Administrador del Sistema.");
				}
			}				// fin de validaPolizaCuadra() 
			
			function validaPolizaRegistros() 
			{	
				try
				{
					return oTableCuen.fnGetData()!="";
				}	
				catch(ex)
				{
					alert("Error 0055js.\r\r" + ex.message + "\r\rFavor de reportarlo al Administrador del Sistema.");
				}
			}				// fin de validaPolizaRegistros()
			
			
			function limpiaPartida()
			{
				try
				{	
					$('#btnnCOG').attr("disabled", false);
					$('#btnnCOG').attr("class", "btnAyuda");
					$('#btnnCABMS').attr("disabled", false);
					$('#btnnCABMS').attr("class", "btnAyuda");			
					$("#nCABMS").attr("disabled", false);
					$("#nCOG").attr("disabled", false);
					$("#nCUCOP").attr("disabled", false);
					$("#buttonCreateEvento").attr("disabled", false);
					$("#buttonLimpiaEvento").attr("disabled", false);
					$("#nCABMS").val("");
					$("#nCOG").val("");	
					$("#nCUCOP").val("");
				}	
				catch(ex)
				{
					alert("Error 0056js.\r\r" + ex.message + "\r\rFavor de reportarlo al Administrador del Sistema.");
				}
			}	// fin de limpiaPartida()		
			
				
			
			function cargarInformacion(archivo){
				var ext = new Array(".xls");
				var correcto = false;
				var extension = (archivo.substring(archivo.lastIndexOf("."))).toLowerCase();
				if (ext[0] == extension){
						correcto = true;
				}
				
				if (!correcto){
					alert("Solo se permiten archivos con extension xls");
					return;
				} else {
					if(confirm("Esta Seguro de Cargar Informacion")){
						
						var prm = "cCentroContable="+ $("#cCentroContable").val()
						+ "&folioDocumento="+ $("#folioDocumento").val()
						+ "&Usuario="+$("#usuario").val()
						+ "&Operacion="+$("#tOperacion").val()
						+ "&fechaCaptura="+$("#fCaptura").val()
						+ "&fechaAplicacion="+$("#fAplicacionLbl").val()
						+ "&Ramo="+$("#cRamo").val()
						+ "&UnidadResponsable="+$("#cUnidadResponsable").val()
						+ "&FolioPolizaTemp="+$("#nFolioPolizatmp").val()
						+ "&Mes="+$("#dMes").val()
						+ "&EjerFisc="+$("#EF").val()
						+ "&ConceptoPolizas="+$("#cConcepto").val()
						+ "&Id_Caso="+$("#id_caso").val();
						
						$("#form1").attr("action","../gstnmngr/CargaPoliza/CargaMasiva?"+prm);
						$("#form1").attr("enctype","multipart/form-data");					
						$.blockUI({message : "Cargando Archivo. Por favor espere ......"});
						$("#form1").submit();							
				}			
			}		
		} /// FIN CARGA ARCHIVO 

function seleccionarpoliza(){
        if ($("#reclasificaV").is(":checked")) {
            $("#divReclasificacionV").show();   
            $("#divReclasificacion").hide();            
        }
        else if ($("#reclasifica").is(":checked")) {
             $("#divReclasificacion").show();
             $("#divReclasificacionV").hide();
        }		
	} // FIN seleccionarpoliza()		
	
function imprimirPoliza(){
	document.form1.action="../polizamanual/ImprimePoliza";	
	document.form1.method="GET";
	document.form1.target="_blank";
	document.form1.submit();	
} //FIN imprimirPoliza()		
	
$.fn.dataTableExt.oApi.fnReloadAjax = function(oSettings, sNewSource) 
	{
		$.blockUI({message : "<p><img src='../Generador/imagenes/wait24trans.gif' />&nbsp;&nbsp;Por favor espere...</p>"});
						
		if (typeof sNewSource != 'undefined')
			oSettings.sAjaxSource = sNewSource;
	
			this.fnClearTable(this);
			this.oApi._fnProcessingDisplay(oSettings, true);
			var that = this;
	
			$.getJSON(oSettings.sAjaxSource,null,function(json)
				{
								
					for ( var i = 0; i < json.aaData.length; i++) 
					{
						that.oApi._fnAddData(oSettings,json.aaData[i]);
					}
					
					oSettings.aiDisplay = oSettings.aiDisplayMaster.slice();
					that.fnDraw(that);
					that.oApi._fnProcessingDisplay(
							oSettings, false);
					$.unblockUI();
				});
		};
							
	</script>
</head>
<br/>
	<body id="dt_example" bottomMargin="0" rightMargin="0" leftMargin="0" topMargin="0" bgcolor="white">

		<form id="form1" name="form1" method="post">
			<!--VGCFIEL-->
			<input type="hidden" name="nNumEmpleadoElab" id="nNumEmpleadoElab" value="-1"/>			 
			<input type="hidden" name="TIPO_REPORTE" name="TIPO_REPORTE" value="PolizaManual.jasper"/>
			
			<div id="container" class="container" class="ms-5" style="width: 80%; visibility: hidden;">
				
					<div class="card-header"> 
						<h3>P&oacute;lizas 
							<label style="font-size: 10pt" id="nomFormatoPol">
								 (por evento)						
							</label>
							<label id="lbSubTitulo" style="font-size: 12pt">						
								- Captura P&oacute;liza
							</label>
						</h3> 
					</div>													
				
				<br/>
				
				<div id="divCaptura">
					<div id="tblRevisionAutirzación">
						<div class="row d-flex justify-content">
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
							</div>
							<div class="col-12 col-lg-6 col-md-6 col-sm-12 p-1 justify-content-center">
								<label id="lblguardarEstatus"> Revisi&oacute;n - </label>&nbsp;&nbsp;&nbsp;&nbsp;
								<label for="autorizaSi" class="form-check-label">Si: </label> &nbsp;&nbsp;
								<input name="grp" id="grp" onclick='$("#evelvar").val(1);' type="radio" class="form-check-input" checked value="Si" />&nbsp;&nbsp;&nbsp;&nbsp;
								
								<label for="autorizaNo" class="form-check-label">No: </label>&nbsp;&nbsp;
								<input name="grp" id="grp" onclick='$("#evelvar").val(2);' type="radio" class="form-check-input" value="No" />&nbsp;&nbsp;&nbsp;&nbsp;
								
								<input name="GuardaEle" id="GuardaEle" onclick="cmdGuardar()" type="button" class="btn btn-secondary btn-sm" value="Guardar" />								
							</div>											
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1 justify-content-center">
								<label for="nFolioPoliza"> Folio Póliza: </label>
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1 justify-content-center">								
								<input type="text" id="nFolioPoliza" name="nFolioPoliza" value="-1" class="form-control form-control-sm" readonly/>								
							</div>																		
						</div>											
					</div>
					
					<br/>
					
					<div id="divImpirme" style="width: 1000px;">									
						<table id="tblImpresionDePoliza" border="0" width="100%" style="font-weight: bold">
							<tr>
								<td>
									<input name="imprimePoliza" id="imprimePoliza" onclick="imprimirPoliza()" type="button" value="Imprimir Poliza" />
								</td>	
							</tr>
						</table>					
					</div>		
					
					<div id="tblCaptura" width="100%" border="0" cellspacing="3" cellpadding="0" style="background-color: White">
						<div class="row d-flex justify-content">
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
								<label for="folioDocumento"style="font-weight: bold;"> Folio Documento: <%=folioDoc%> </label>															
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
								<label for="fCatpuraLbl"> F. Captura: </label>
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
								<label id="fCatpuraLbl">&nbsp;</label>
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
								<label for="fAplicacionLbl"> F. Aplicación: </label>
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
								<div class="input-group">
									<span class="input-group-text"><i class="bi bi-calendar"></i></span>
									<input name="fAplicacionLbl" type="text" id="fAplicacionLbl" class="form-control form-control-sm" value="" size="10">
								</div>
							</div>
						</div>		
						
						<div class="row d-flex justify-content">
							<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">
							</div>							
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
								<label for="EF"> E. Fiscal: </label>
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
								<select id="EF" name="EF" class="form-select form-select-sm"></select>
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
								<label for="PolTipo"> Tipo P&oacute;liza: </label>
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
								<select id="PolTipo" name="PolTipo" class="form-select form-select-sm"></select>								
							</div>
						</div>		
						
						<div class="row d-flex justify-content">
							<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">
							</div>							
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
								<label for="hPolCtroContable"> Centro Contable: </label>
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
								<select id="hPolCtroContable" name="hPolCtroContable" class="form-select form-select-sm" onChange="cambioALM()">
								</select>
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
								<div id="Periodo13Div">									
									<input type="hidden" name="periodo13" id="periodo13" value="N">
									<input type="hidden" id="CtroContable" name="CtroContable" />
									<input type="checkbox" name="periodo13Cap" id="periodo13Cap" class="form-check-input" checked="checked" disabled="disabled" value="1">
									<label for="periodo13Cap"> Periodo 13 </label>
								</div>
							</div>
						</div>
						
						<div id="AjusteCapt">
							<div class="row d-flex justify-content">
								<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
								</div>			
								<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
									<label for="PolTipo"> Ajuste: </label>
								</div>
								<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
									<select id="nTipoAjuste" name="nTipoAjuste" onchange="updateAjuste()" class="form-select form-select-sm">
										<option value=""></option>
										<option value="1">
											Ajuste Previo
										</option>
										<option value="2">
											Ajuste Presupuestario
										</option>
										<option value="3">
											Ajuste de Resultados
										</option>
									</select>							
								</div>
							</div>
						</div>		
						
						<div class="row d-flex justify-content">
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
							</div>
							<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">
								<label for="encabezado"> Concepto: </label>
								<textarea name="cConcepto" id="cConcepto" rows="5" cols="63" class="form-control form-control-sm" ></textarea>					
								<input type="hidden" id="txtObservaciones" name="txtObservaciones"></input>
							</div>			
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">								
							</div>
							<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1" >		
								<div id="tdDescripcionEvento">						
									<p class="text-end"><em><label id="Label2"></label></em></p>								
									<p class="text-end"><em><label id="Label3"></label></em></p>								
									<p class="text-end"><em><label id="Label4"></label></em></p>							
									<p class="text-end"><em><label id="Label5"></label></em></p>
									<p class="text-end"><em><label id="Label6"></label></em></p>
								</div>															
							</div>
						</div>
						
						<div class="row d-flex justify-content">
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
								<label for="fComprobacion"> Folio Anticipo </label>															
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
								<input id="fComprobacion" name="fComprobacion" disabled class="form-control form-control-sm"/>
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
								<label for="rCxp"> CxP: </label>
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
								<input id="rCxp" name="rCxp" disabled class="form-control form-control-sm"/>
							</div>
						</div>
					</div>
					
					<div id="divGrupo1">
						<div class="row d-flex justify-content">
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
								<label for="nGrupo"> <p class="fw-bold"> Grupo </p> </label>															
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
								<label for="nSubGrupo"> <p class="fw-bold"> SubGrupo </p> </label>							
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
								<label for="nEvento"> <p class="fw-bold"> Evento </p> </label>
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
								<label for="nCOG"> <p class="fw-bold"> OBGT </p> </label>
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
								<!-- <label for="nCABMS"> <p class="fw-bold"> CABMS </p> </label> -->							
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
								<!-- <label for="nCOG"> <p class="fw-bold"> CUCOP </p> </label> -->							
							</div>
						</div>
						
						<div class="row d-flex justify-content">
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
								<div class="input-group">
									<input type="text" name="nGrupo" id="nGrupo" class="form-control form-control-sm AyudaSyC" onkeypress="Validaciones(this,2)" />
								</div>											
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
								<div class="input-group">				
									<input type="text" name="nSubGrupo" id="nSubGrupo" class="form-control form-control-sm AyudaSyC" onkeypress="Validaciones(this,2)" />			
								</div>
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
								<div class="input-group">							
									<input type="text" name="nEvento" id="nEvento" class="form-control form-control-sm AyudaSyC" onkeypress="Validaciones(this,2)" />
								</div>
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
								<div class="input-group">						
									<input type="text" name="nCOG" id="nCOG" class="form-control form-control-sm AyudaSyC" onchange="limpiaCambsCucop();" onkeypress="Validaciones(this,2)" />	
								</div>
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
								<div class="input-group">
									<input type="hidden" name="nCABMS" id="nCABMS" class="form-control form-control-sm" onkeypress="Validaciones(this,2)" />
								</div>							
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
								<div class="input-group">
									<input type="hidden" name="nCUCOP" id="nCUCOP" class="form-control form-control-sm" onkeypress="Validaciones(this,2)" />
								</div>							
							</div>
						</div>
					</div>
					
					<div id="botoneraEventos">
						<div id="botonespolizaEventos">
							<div class="row d-flex justify-content">
								<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
								</div>
								<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">									
									<input id="buttonCreateEvento" type="button" onclick="creaEvento()" class="btn btn-secondary btn-sm" value="Agregar evento" />																			
								</div>
								<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">									
									<input id="buttonLimpiaEvento" type="button" onclick="limpiaEvento()" class="btn btn-secondary btn-sm" value="Limpiar" />																			
								</div>
								<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">									
									<input id="buttonDescartarEvento" type="button" onclick="dropEvento()" value="Descartar evento" class="btn btn-secondary btn-sm" style="display: none" />																			
								</div>
							</div>
						</div>
					</div>
					
					<div id="divReclasifica" style="display: none">
						<div class="row d-flex justify-content">
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">									
								Reclasificacion de viaticos <input type="radio" name="reclasifica" id="reclasificaV" class="form-check-input" value="viaticos" onclick="seleccionarpoliza()"/>																			
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">									
								Reclasificacion <input type="radio" name="reclasifica" id="reclasifica" class="form-check-input" value="presupuestal" onclick="seleccionarpoliza()"/>																			
							</div>
						</div>
						
						<div id ="divReclasificacionV" style="display: none">																			
							<div class="row d-flex justify-content">
								<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
								</div>
								<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">									
									Solicitud de anticipo origen: <select id="polManual" name="polManual" class="form-select form-select-sm"></select>																			
								</div>
							</div>							
						</div>
						
						<div id ="divReclasificacion" style="display: none">																			
							<div class="row d-flex justify-content">
								<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
								</div>
								<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">									
									Captura la cuenta por pagar: <input type="text" name="cxp" id="cxp" class="form-control form-control-sm" />																			
								</div>
							</div>							
						</div>
					</div>
			
					<br/>
					
					<div id="divCuenta" style="display: none">								
						<h5>Movimientos por							
							<label id="lgtipoPoliza">						
								Cuenta
							</label>
						</h5> 
						<hr class="mt-3">		
									
						<div id ="t1">
							<div class="row d-flex justify-content">
								<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
								</div>
								<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
									<label for="nCuenta"> Cuenta: </label>															
								</div>
								<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
									<div class="input-group">
										<input type="text" name="nCuenta" id="nCuenta" readonly="readonly" class="form-control form-control-sm" onchange="selectCuenta()" />
										<input type="button" class="btn btn-secondary btn-sm" value="..." onclick="showCuentas();">
									</div>
										<input type="hidden" id="nCuentaAplicacion" name="nCuentaAplicacion" value="">
								</div>
								<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
								</div>
								<div class="col-12 col-lg-6 col-md-6 col-sm-12 p-1">
									<div id="file1">
										<div class="row d-flex justify-content">								
											<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
												<label for="archivoPol"> Cargar Archivo: </label>
											</div>
											<div class="col-12 col-lg-6 col-md-6 col-sm-12 p-1">
												<input type="file" id="archivoPol" name="archivoPol" class="form-control form-control-sm"/>								
											</div>
											<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
												<input type="button" id="bCargar" name="bCargar" value="Cargar" class="btn btn-secondary"/>
											</div>
										</div>
									</div>
								</div>
							</div>	
							
							<div class="row d-flex justify-content">
								<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
								</div>
								<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
									<label for="dCuenta"> Descripción: </label>															
								</div>
								<div class="col-12 col-lg-9 col-md-9 col-sm-12 p-1">
									<input name="dCuenta" type="text" id="dCuenta" class="form-control form-control-sm" onChange="tipoSubCuenta()" />
								</div>
							</div>
							
							<div class="row d-flex justify-content">
								<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
								</div>
								<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
									<label id="lblAuxiliar"> Auxiliar: </label>															
								</div>
								<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
									<div id="divAML" style="display: none">
										<select id="ALM" name="ALM" onfocus="cierraAyuda()" onkeypress="return toNext(event,this.id)" class="form-select form-select-sm"> </select>
									</div>
									<div id="divRFC" style="display: none">
										<div class="input-group">
											<input type="text" name="cIDRFC" id="cIDRFC" onfocus="cierraAyuda()" onblur="" class="form-control form-sontrol-sm AyudaSyC autoCompletaSyC" />
										</div>
									</div>									
									<div id="divOBGT" style="display: none">
										<div class="input-group">
											<input type="text" name="hPartidaPol" id="hPartidaPol" onfocus="cierraAyuda()" onblur="" class="form-control form-control-sm AyudaSyC autoCompletaSyC" />
										</div>
									</div>
									<div id="divEP" style="display: none">
										<div class="input-group">
											<input id="ep" name="ep" type="text"   value="" class="form-contro form-control-sm"/>
											<input type="button" value="..." onclick="Grid()" class="btn btn-secondary btn-sm" />
										</div>
									</div>
									<div id="divCTAB" style="display: none">
										<div class="input-group">
											&nbsp;&nbsp;&nbsp;ID:&nbsp;
											<input type="text" name="idCuenta" id="idCuenta" value="" class="form-control form-control-sm AyudaSyC" onblur="blurCtaBanc()" onkeydown="return ctaKeyDwn(event, this.id)" />
										</div>
										<div class="input-group">
											Clabe:
											<input type="text" name="CTABAN" id="CTABAN" value="" size="17" onkeydown="return ctaKeyDwn(event, this.id)" onfocus="cierraAyuda()" class="form-control form-control-sm autoCompletaSyC AyudaSyC" />
										</div>	
									</div>
									<div id="divProy" style="display: none">
										<div class="input-group">
											<input type="text" name="hProyectoPol" id="hProyectoPol" onfocus="cierraAyuda()" onblur="" class="form-control form-control-sm AyudaSyC autoCompletaSyC" />
										</div>
									</div>
									<div id="sinAuxiliar" style="display: none">
										<label> Cuenta sin auxiliar: </label>
									</div>
									<div id="divCRI" style="display: none">
										<div class="input-group">
											<input type="text" name="cCRI" id="cCRI" onfocus="cierraAyuda()" onblur="" class="form-control form-sontrol-sm AyudaSyC autoCompletaSyC" />
										</div>
									</div>	
								</div>
							</div>
							
							<div id="repetirTexto">
								<div class="row d-flex justify-content">
									<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
									</div>
									<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
										<label for="repetirTexto"> Repetir Desc.: </label>															
									</div>
									<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
										<input type="checkbox" id="movParcialidad" name="movParcialidad" class="form-check-input" value="S">									
									</div>
								</div>
							</div>
							
							<div class="row d-flex justify-content">
								<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
								</div>
								<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
									<label for="Parciales"> Descripción: </label>															
								</div>
								<div class="col-12 col-lg-7 col-md-7 col-sm-12 p-1">
									<input type="text" name="Parciales" id="Parciales" value="" maxlength="120" class="form-control form-control-sm" onkeypress="return toNext(event,this.id)" />
								</div>
							</div>
							
							<div class="row d-flex justify-content">
								<div class="col-12 col-lg-5 col-md-5 col-sm-12 p-1">
								</div>
								<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
									<div id="tdlabelCargo" align="right">
										<label for="Parciales"> Cargo: </label>
									</div>															
								</div>
								<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
									<div id="tdInputCargo" align="left">
										<input type="text" name="Cargos" id="Cargos"
											onfocus="Sinfrmt(this);onFocusMoney(this);"
											onblur="onBlurMoney(this);cambiafrmt(this);"
											onkeypress="return toNext(event,this.id)"
											onkeydown="return catchTab(event,this.id)" size="10"
											style="text-align: right" 
											class="form-control form-control-sm"/>
										<input type="hidden" name="auxCargos" id="auxCargos" size="10" />
									</div>
								</div>
								<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
									<div id="tdlabelAbono" align="right">
										<label for="Parciales"> Abono: </label>
									</div>															
								</div>
								<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
									<div id="tdInputAbono" align="left">
										<input type="text" name="Abonos" id="Abonos"
											onfocus="Sinfrmt(this);onFocusMoney(this);"
											onblur="onBlurMoney(this);cambiafrmt(this);"
											onkeypress="return toNext(event,this.id)" size="10"
											style="text-align: right"
											class="form-control form-control-sm"/>
										<input type="hidden" name="auxAbonos" id="auxAbonos" size="10" />
										<input type="hidden" name="auxAbonos1" id="auxAbonos1" size="10" />
									</div>
								</div>
							</div>
								
						</div> <!-- id="t1" -->
					</div> <!-- id="divCuenta" -->
							
					<div id="tdButtons" style="display: none">			
						<div class="row d-flex justify-content">
							<div class="col-12 col-lg-5 col-md-5 col-sm-12 p-1">
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
								<input type="button" name="Agregar"   id="Agregar" value="Agregar Movimiento" onClick="agregaRegistroEvento()" class="btn btn-secondary btn-sm">							
							</div>						
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
								<input type="button" name="Limpia1"   id="Limpia1" value="Eliminar Evento"     onClick="eliminaRegistroEvento()" class="btn btn-outline-danger btn-sm">
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
								<input type="button" name="Modificar" id="Modificar" value="Agregar Cambios"  onClick="editaRegistroEvento()" class="btn btn-secondary btn-sm" style="display: none">							
							</div>						
						</div>	
					</div>
					
					
					<br/>

					<div class="mt-2 col-12">
						<table id="pCuentas" class="table table-striped">
							<thead>
								<tr align="center">
									<th> <!-- 0 --> Rengl&oacute;n </th>
									<th> <!-- 1 --> Cuenta </th>
									<th> <!-- 2 --> Desc. Cuenta </th>
									<th> <!-- 3 --> Auxiliar </th>
									<th> <!-- 4 --> Descripci&oacute;n Mov. </th>
									<th> <!-- 5 --> Cargos </th>
									<th> <!-- 6 --> Abonos </th>
									<th> <!-- 7 --> Parcial </th>
									<th> <!-- 8 --> Grupo </th>
									<th> <!-- 9 --> SubGrupo </th>
									<th> <!-- 10 --> Evento </th>
									<th> <!-- 11 --> Partida </th>
									<th> <!-- 12 --> CABMS </th>
									<th> <!-- 13 --> CUCOP </th>
									<th> <!-- 14 --> No. Evento </th>
									<th> <!-- 15 --> Cta Aplicacion </th>
								</tr>
							</thead>
						</table>
					</div>

					<table width="100%">
						<tr>
							<td align="right" nowrap="nowrap">
								<label style="font-style: italic;">
									Total Cargos :
								</label>
								<label id="TcargosLbl" style="font-weight: bolder;">
								</label>								
							</td>
							<td align="right" nowrap="nowrap" width="10%">
								&nbsp;
								<label style="font-style: italic;">
									Total Abonos:
								</label>
								<label id="TabonosLbl" style="font-weight: bolder;">
								</label>								
							</td>
						</tr>						
					</table>
					
					<div id="btnsCtrl" align="center">
						<div class="row d-flex justify-content">				
							<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
							</div>			
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
								<input type="button" value="Guardar Cambios" class="btn btn-secondary btn-sm" id="Guarda2" name="Guardar" onClick="cmdGuardarParcial()" />
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
								<input type="button" value="Guardar Enviar" class="btn btn-primary btn-sm" id="Guarda" name="Guardar" onClick="cmdGuardar()" />
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
								<input type="button" value="Descartar Captura" class="btn btn-dark btn-sm" id="cancelar" name="cancelar" onClick="cmdBorrar()" />
							</div>
							<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
							</div>			
						</div>
					</div>
										
					<div class="row d-flex justify-content">				
						<div class="col-12 col-lg-10 col-md-10 col-sm-12 p-1">
						</div>			
						<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
							<input type="button" value="Cerrar" name="cerrarCaso" id="cerrarCaso" onClick="cmdCierraCaso()" class="btn btn-secondary btn-sm" />
						</div>
					</div>
					
				</div>
				<!-- Fin del div de Captura -->								
				<div id="divGrabaDetalle" style="width:100px; visibility: hidden">
			     <table id="dtGuardaDetalle" style="width:100px">
			      <thead>
			       <tr>
			        <th>A</th>  <!-- nDocRenglonCrud  -->
			        <th>B</th>  <!-- nCuentaCrud  -->
			        <th>C</th>  <!-- valSubCuentaCrud  -->
			        <th>D</th>  <!-- ParcialesCrud  -->
			        <th>E</th>  <!-- Parcial  -->
			        <th>F</th>  <!-- mImporte  -->
			        <th>G</th>  <!-- dataCABMS  -->
			        <th>H</th>  <!-- dataCUCOP  -->
			        <th>I</th>  <!-- dataPartida  -->
			        <th>J</th>  <!-- nIdGrupoEvento  -->
			        <th>K</th>  <!-- nIdSubGrupoEvento  -->
			        <th>L</th>  <!-- cIdEventoManual  -->
			        <th>M</th>  <!-- nNumeroEvento  -->
			        <th>N</th>  <!-- nNumeroEvento  -->
			       </tr>
			      </thead>
			     </table>
			    </div>   <!--  Fin del div de divGrabaDetalle -->
									
			</div>
			<!-- Fin del div del Container -->						

			<div id="dialog-Procesando" title="Seleccionar" style="width: 50%; margin: 0px auto 0px auto; text-align: center" class="container">
				
				<%				
				if(POLIZANUEVA &&  cPolizaManualCuenta && cPolizaManualEventos)//Poliza Nueva - Captura Ambas
					{
				%>				
					<fieldset id="marcoformatoPoliza">
						<legend >Formato de P&oacute;liza</legend>
						
							<select id="grpformato" name="grpformato" style="width:200px" class="form-select form-select-sm">
								<option value="2">Por Eventos</option>
								<option value="1">Por Cuentas</option>								
							</select>
						<br/><br/>
						<input id="eligeFormato" onclick="asignaTipoPol();"    type="button" value="Aceptar" class="btn btn-secondary btn-sm"/>
						<input id="descartar" onclick="$('#pb_cancel', parent.window.document).click();"    type="button" value="Regresar" class="btn btn-secondary btn-sm"/>
					</fieldset>		
								
					<div id="ProcMsg" align="center" style="display:none">
						Espere por favor....
						<div id="ProcMsg" align="center" style="font-size: 10pt">Procesando...</div>
						<img border="0" src="../imagenes/espera.gif" height="30">
					</div>	
				<%
					}
				else if(!UAUTORIZADO)
				{
					%>					
					<div id="divEsperaProcesando" align="center" >						
						<%
						if(!UAUTORIZADO && POLIZANUEVA)
							out.println("No cuenta con el permiso para generar algun tipo de póliza.");
						else
							out.println("No cuenta con el permiso para abrir este tipo de pólizas.");
						%>
						<div id="ProcMsg" align="center" style="font-size: 10pt"><br/><input id="salir" onclick="document.liberardocumento.submit();"    type="button" value="Aceptar" /></div>
						</div>
					<% 					
				}
				else{ //si ya existe la poliza o tiene permiso de capturar solo alguno de las dos formatos.									
				%>	
					<div id="divEsperaProcesando" align="center" >
						Espere por favor....
						<div id="ProcMsg" align="center" style="font-size: 10pt">Procesando...</div>
						<img border="0" src="../imagenes/espera.gif" height="30">
					</div>
				<%}%>				
			</div>
			

			<!-- ******************************************************************************************************************************************************* -->

			<input type="hidden" name="Id_Caso_Oper" id="Id_Caso_Oper" />			
			<input type="hidden" name="nSubCuenta" id="nSubCuenta" />
			<input type="hidden" name="mes13" id="mes13" value="0" />
			<input type="hidden" name="cComentarios" id="cComentarios" />
			<input type="hidden" id="dMes" name="dMes" value="" />			
			<input type="hidden" id="cReferenciaPoliza" name="cReferenciaPoliza" value="" />
			<input type="hidden" id="cReferenciaPolizaRev" name="cReferenciaPolizaRev" value="" />
			<input type="hidden" id="cReferenciaPolizaAut" 	name="cReferenciaPolizaAut" value="" />			
			<input type="hidden" id="cEvento" name="cEvento" />			
			<input type="hidden" id="cDocumentoHaplicado" name="cDocumentoHaplicado" value="" />
			<input type="hidden" id="tmpInpt" name="tmpInpt" value="" />
			<input type="hidden" id="cCentroContable" name="cCentroContable" value="<%=cCentroContable%>" />
			<input type="hidden" id="cRamo" name="cRamo" value="<%=cRamo%>" />
			<input type="hidden" id="cUnidadResponsable" name="cUnidadResponsable" value="" />
			<input type="hidden" id="usuario" name="usuario" value="<%=nomUsuario%>" />
			<input type="hidden" id="nomLargoUsuario" name="nomLargoUsuario" value="<%=nomLargoUsuario%>" />
			<input type="hidden" id="evelvar" name="evelvar" value="1" />			
			<input type="hidden" id="nIdAlmacen" name="nIdAlmacen" value="" />
			<input type="hidden" id="nDato" name="nDato" value="0" />			
			<input type="hidden" id="mesAbierto" name="mesAbierto" value="0" />
			<input type="hidden" id="cTipoPoliza" name="cTipoPoliza" />
			<input type="hidden" id="Status" name="Status" value="0" />			
			<input type="hidden" id="cadFolio" name="cadFolio" value=" " />
			<input type="hidden" id="resp" name="resp" value=" " />
			<input type="hidden" id="nMes" name="nMes" />
			<input type="hidden" id="valSubCuenta" name="valSubCuenta" />
			<input type="hidden" id="valSubCuentaCompar" name="valSubCuentaCompar" />			
			<input type="hidden" name="FOLIO" id="FOLIO" value="<%=c.getFolio()%>" />
			<input type="hidden" name="OPERADOR" id="OPERADOR" value="<%=nomLargoUsuario%>" />						
			<input type="hidden" id="nFolioPolizatmp" name="nFolioPolizatmp" value="-1" />			
			<input type="hidden" id="nFormatoPoliza" name="nFormatoPoliza" value="2" />			
			<input type="hidden" name="id_caso" id="id_caso" value="<%=c.getIdCaso()%>" />
			<input type="hidden" name="folioDocumento" id="folioDocumento" value="<%=folioDoc%>">
			<input type="hidden" name="fCaptura" id="fCaptura" >
			<input type="hidden" name="fAplicacion" id="fAplicacion" >
			<input type="hidden" name="ndocrenglon" id="ndocrenglon" />
			<input type="hidden" name="formatoPoliza" id="formatoPoliza" />				
			<input type="hidden" id="PolAutomatica" name="PolAutomatica" value="0" readonly />
			<input type="hidden" name="tOperacion" id="tOperacion" />
			<input type="hidden" name="ADEFAS" id="ADEFAS" value="N" />
			<input type="hidden" name="Movs" id="Movs" value="0" />
			<input id="Tcargos" name="Tcargos" type="hidden" />
			<input id="Tabonos" name="Tabonos" type="hidden" />
			<input type="hidden" id="comparacxp" name="comparacxp" />
						
	</form>
	
	<form id="liberardocumento" name="liberardocumento"
		action="../gstnmngr/gestion?cmd=22" target="content-iframe"
		method="post">
	</form>

	</body>
</html>
