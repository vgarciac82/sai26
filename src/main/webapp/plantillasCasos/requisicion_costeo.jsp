<%@page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.syc.gestion.core.Caso"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.UsuarioPropiedades"%>
<%@page import="com.syc.gestion.core.UsuarioPropiedadesManager"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="com.syc.gestion.UsuarioPropiedadesBusinessLogic"%>
<%@page import="com.syc.fortimax.core.DescripcionManager"%>
<%@page import="com.syc.fortimax.core.Descripcion"%>
<%@page import="com.syc.fortimax.core.ListaImaxfile"%>
<%@page import="com.syc.fortimax.core.DocumentoManager"%>
<%@page import="com.syc.fortimax.core.Documento"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="java.util.concurrent.Delayed"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="com.syc.gestion.core.FortimaxFile"%>
<%@page import="com.syc.gestion.core.UsuarioGrupoManager"%>
<%@page import="com.syc.gestion.UsuarioGrupoBusinessLogic"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
    String DATE_FORMAT = "dd/MM/yyyy";
    SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
    Calendar c1 = Calendar.getInstance(); // today
    String today= sdf.format(c1.getTime());
    Caso objCaso = (Caso)session.getAttribute(GestionInterface.ATT_CASE);
    Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);

    int anp_rows_table=1;

    String folio=objCaso.getFolio();
    int id_oper = objCaso.getCasoOperacion(0).getIdOperacion();
    String titulo_aplicacion = objCaso.getTipoCaso().getGavetaAsociada();
    String solicitante=null;
    String categoria=null;
    String comprador=null;
    Map compradores = new Hashtable();;
    boolean vigencia_landed_cost=true;
    boolean vigencia_gross_margin=true;
    ListaImaxfile[] lstimx_gerencia_ventas = null;
    ListaImaxfile[] lstimx_empaque = null;
    ListaImaxfile[] lstimx_status = null;

    if(id_oper==1){
    	//para llenado automatico del solicitante
    	solicitante=objCaso.getCasoOperacion(0).getResponsable();
    	//para llenado automatico de la categoria en funcion del solicitante
    	UsuarioPropiedades up = new UsuarioPropiedades();
    	up.setLogin(usuario.getLogin());
    	up.setNombre("p_categoria");
    	UsuarioPropiedadesBusinessLogic upbl= new UsuarioPropiedadesBusinessLogic("jdbc/gestion");
    	up=upbl.select(up);
		categoria=up!=null?upbl.select(up).getValor():null;
		up = new UsuarioPropiedades();
		up.setLogin(usuario.getLogin());
		up.setNombre("p_comprador");
		up=upbl.select(up);
		comprador=up!=null?upbl.select(up).getValor():null;
		UsuarioGrupoBusinessLogic ugbl = new UsuarioGrupoBusinessLogic("jdbc/gestion");
		compradores = ugbl.selectUsuarios(comprador);
	}

	//if(id_oper<19){
		//para llenado automatico de la gerencia de ventas
		DescripcionManager dm = new DescripcionManager();
		lstimx_gerencia_ventas = dm.selectListaImaxfile(titulo_aplicacion, "GERENCIA_VENTAS");
		lstimx_empaque = dm.selectLista("MXEMPAQUE");
		//System.out.println(lstimx_empaque);
	//}
	if(id_oper>=19){
		lstimx_status = dm.selectLista("MXESTATUS_PRODUCTO");
		if(objCaso.getCasoDato("ANP_ROWS").getValor()!=null&&!"".equals(objCaso.getCasoDato("ANP_ROWS").getValor()))
			anp_rows_table=new Integer(objCaso.getCasoDato("ANP_ROWS").getValor()).intValue();
		anp_rows_table=(anp_rows_table==0?1:anp_rows_table);
	}
	//VIGENCIA LANDED COST
	if(id_oper>=6&&id_oper<=18){
		DocumentoManager dman=new DocumentoManager();
		Documento d=dman.buscaDocumento("ANP",objCaso.getIdGabinete(),7,1);//EL landed cost esta en la carpeta 7
		Calendar calendar = new GregorianCalendar();
		if(d!=null){
			if(d.getFh_vigencia()!=null&&!"".equals(d.getFh_vigencia())){
				if(d.getFh_vigencia().getTime()<calendar.getTimeInMillis()){
					vigencia_landed_cost=false;
				}
			}
		}
		//VIGENCIA DEL GROSS (depende de la del landed)
		if(id_oper==8||id_oper==9){
			d=dman.buscaDocumento("ANP",objCaso.getIdGabinete(),8,1);//leemos la fecha de vigencia del gross que a su vez es la del landed anterior
			if(d!=null){
				if(d.getFh_vigencia()!=null&&!"".equals(d.getFh_vigencia())){
					if(d.getFh_vigencia().getTime()<calendar.getTimeInMillis()){
						vigencia_gross_margin=false;
					}
				}
			}
		}

	}


%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">

    <title>My JSP 'registra.jsp' starting page</title>

	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<link rel="stylesheet" type="text/css" href="./css/datepickercontrol_bluegray.css" />
	<link rel="stylesheet" type="text/css" href="./css/vasconia.css">

	<script type="text/javascript" src="./js/datepickercontrol.js"></script>
	<script type="text/javascript" src="./js/jquery-1.2.6.js"></script>
	<script type="text/javascript" src="./js/jsquery.js"></script>
	<script type="text/javascript" src="./js/jsquery-fetch.js"></script>
	<script type="text/javascript" src="./js/utils/syctools.js"></script>
	<script type="text/javascript">


	function get(name) {
		return document.getElementById(name).value;
	}

	function inicioOnLoad(){

		<%if(!vigencia_landed_cost&&(id_oper>=6&&id_oper<=18)){%>
			//alert(parent.parent.document.getElementById('reload').value);
			if(parent.parent.document.getElementById('reload').value=="no"){
				parent.parent.document.getElementById('reload').value='si';
				var complemento_msg="<%=(id_oper==6?"Favor de sustituirlo.":(id_oper==8||id_oper==9||id_oper==17?"Favor de Guardar y Enviar para regresar el caso.":""))%>";
				alert("Vigencia vencida en Landed Cost.\n\n"+ complemento_msg);
			}
		<%}%>
	}
	function oPostDisplay(id_oper){
		if(id_oper==19){
			<%for(int i=1;i<=anp_rows_table;i++){%>
				if(document.getElementById("estatus_vigencia_<%=i%>").value==4){
					document.getElementById("DPC_fecha_oferta_<%=i%>").disabled=false;
					document.getElementById("DPC_fecha_oferta_fin_<%=i%>").disabled=false;
				}
			<%} %>
		}
	}

	//Validaciones de campos de la requisicion de costeo y formato anp
	function inicioOnSubmit(id_oper,addRow) {
	var p = window.parent;
	var valida_campos = true;
		try {

				if(id_oper==1){
					var mxg="";
					p.gestion.setFolio(get("FOLIO"));

					//llenar el campo GERENCIA_VENTAS con las gerencias que hayan sido checadas
					var gv = document.getElementById("GERENCIA_VENTAS").value="";
					<%	for(int i=0;i<lstimx_gerencia_ventas.length;i++){ %>
							if(document.getElementById("GERENCIA_VENTAS_<%=lstimx_gerencia_ventas[i].getConsecutivo()%>").checked)
								gv+=(gv!=""?"|":"")+"<%=lstimx_gerencia_ventas[i].getConsecutivo()%>";
					<%	}%>
					document.getElementById("GERENCIA_VENTAS").value=gv;
					p.gestion.setGerenciaVentas(get("GERENCIA_VENTAS"));

					p.gestion.setCategoria(get("CATEGORIA"));
					p.gestion.setFechaSolicitud(get("FECHA_SOLICITUD"));
					p.gestion.setGerenteProducto(get("SOLICITANTE"));
					<%if(objCaso.getCasoDato("GERENTE_PRODUCTO").getValor()!=null&&!"".equals(objCaso.getCasoDato("GERENTE_PRODUCTO").getValor())){%>
						p.gestion.setGerenteProducto("<%=objCaso.getCasoDato("GERENTE_PRODUCTO").getValor()%>");
					<%}%>
					p.gestion.setComprador(get("NOMBRE_COMPRADOR")); //<%=comprador%>
					p.gestion.setEstimacionPof(get("ESTIMACION_POF"));
					if(document.req_costeo.ESTIMACION_COMP[0].checked){
						p.gestion.setEstimacionComp("INVENTARIO");//ojo, esto debe ser igual al valor que esta por default en el campo
						p.gestion.setEstimacionClie("&nbsp;");
					}
					else{
						p.gestion.setEstimacionComp("CLIENTE");//ojo, esto debe ser igual al valor que esta por default en el campo
						p.gestion.setEstimacionClie(get("ESTIMACION_CLIE"));
					}
					p.gestion.setEstimacionVenta(get("ESTIMACION_VENTA"));
					p.gestion.setNombreProducto(get("NOMBRE_PRODUCTO"));

					if(get("DPC_FECHA_LANZAMIENTO")==""||get("DPC_FECHA_LANZAMIENTO")==null){
						mxg+="Fecha de lanzamiento es requerido\n";
						valida_campos = false;
					}
					//alert(get("NOMBRE_PRODUCTO").length);
					//var chale=get("NOMBRE_PRODUCTO").length;
					//for (i=0;i<chale;i++){
					//	alert(get("NOMBRE_PRODUCTO").charCodeAt(i));
					//}
					if(get("NOMBRE_PRODUCTO")==""||get("NOMBRE_PRODUCTO")==null){
						mxg+="Nombre del producto es requerido\n";
						valida_campos = false;
					}
					if(!document.getElementById("FABRICACION_IMP").checked && !document.getElementById("FABRICACION_NAL").checked && !document.getElementById("COMPRA_NAL").checked){
						mxg+="Fabricaci�n es requerido\n";
						valida_campos = false;
					}
					if(get("GERENCIA_VENTAS")==""||get("GERENCIA_VENTAS")==null){
						mxg+="Gerencia de ventas es requerido\n";
						valida_campos = false;
					}
					if(get("DESCRIPCION")==""||get("DESCRIPCION")==null){
						mxg+="Descripci�n es requerido\n";
						valida_campos = false;
					}
					//if(get("HERRAMIENTA")==""||get("HERRAMIENTA")==null){
					//	mxg+="Herramienta es requerido\n";
					//	valida_campos = false;
					//}
					if((get("EVALUAR_HERRAMIENTA")==""||get("EVALUAR_HERRAMIENTA")==null)&& document.getElementById("HERRAMIENTA").checked){
						mxg+="Evaluar Herramienta es requerido\n";
						valida_campos = false;
					}
					if(get("EMPAQUE")==-1||get("EMPAQUE")==null){
						mxg+="Empaque es requerido\n";
						valida_campos = false;
					}
					if((get("OTRO_EMPAQUE")==""||get("OTRO_EMPAQUE")==null)&&get("EMPAQUE")==0){
						mxg+="Otro Empaque es requerido\n";
						valida_campos = false;
					}
					if(get("PIEZAS_CAJA")==""||get("PIEZAS_CAJA")==null){
						mxg+="Piezas Caja es requerido\n";
						valida_campos = false;
					}
					if(get("VOLUMEN_ESTIMADO")==""||get("VOLUMEN_ESTIMADO")==null){
						mxg+="Volumen Estimado es requerido\n";
						valida_campos = false;
					}
					if(get("DPC_FECHA_REQ_COTIZACION")==""||get("DPC_FECHA_REQ_COTIZACION")==null){
						mxg+="Fecha Req. Cotizaci�n es requerido\n";
						valida_campos = false;
					}
					//if((get("PRESUPUESTO")==""||get("PRESUPUESTO")==null)){
					//	mxg+="PRESUPUESTO es requerido\n";
					//	valida_campos = false;
					//}
					//if(get("CANTIDAD")==""||get("CANTIDAD")==null){
					//	mxg+="Cantidad es requerido\n";
					//	valida_campos = false;
					//}
					if(get("PRECIO_PROMEDIO")==""||get("PRECIO_PROMEDIO")==null){
						mxg+="Precio promedio es requerido\n";
						valida_campos = false;
					}
					//if(get("CAJA")==""||get("CAJA")==null){
					//	mxg+="Caja es requerido\n";
					//	valida_campos = false;
					//}
					if(get("PALET")==""||get("PALET")==null){
						mxg+="Palet es requerido\n";
						valida_campos = false;
					}
					if(get("ESTIBAS")==""||get("ESTIBAS")==null){
						mxg+="Estibas es requerido\n";
						valida_campos = false;
					}
					if(get("CAJA_MASTER")==""||get("CAJA_MASTER")==null){
						mxg+="Caja Master es requerido\n";
						valida_campos = false;
					}
					if(get("CAJA_INDIVIDUAL")==""||get("CAJA_INDIVIDUAL")==null){
						mxg+="Caja Individual es requerido\n";
						valida_campos = false;
					}

					if(get("ESTIMACION_POF")==""||get("ESTIMACION_POF")==null){
						mxg+="Estimaci�n de primera orden de fabricaci�n es requerido\n";
						valida_campos = false;
					}
					/*if(get("ESTIMACION_COMP")==""||get("ESTIMACION_COMP")==null){
						mxg+="Estimaci�n de compra es requerido\n";
						valida_campos = false;
					}*/
					if(!document.getElementById("ESTIMACION_COMP").checked&&(get("ESTIMACION_CLIE")==""||get("ESTIMACION_CLIE")==null)){
						mxg+="Nombre del cliente es requerido\n";
						valida_campos = false;
					}
					if(get("ESTIMACION_VENTA")==""||get("ESTIMACION_VENTA")==null){
						mxg+="Estimaci�n de venta mensual es requerido\n";
						valida_campos = false;
					}
					if(((document.getElementById("FABRICACION_IMP").checked && !document.getElementById("FABRICACION_NAL").checked)||//importado
						(document.getElementById("FABRICACION_IMP").checked && document.getElementById("FABRICACION_NAL").checked)||//mixto
						(document.getElementById("COMPRA_NAL").checked && !document.getElementById("FABRICACION_NAL").checked)//compra nacional
						)&&	get("NOMBRE_COMPRADOR")=="Ninguno"){
						mxg+="Comprador es requerido";
						valida_campos = false;
					}
					else if(!document.getElementById("FABRICACION_IMP").checked && document.getElementById("FABRICACION_NAL").checked){
						document.getElementById("NOMBRE_COMPRADOR").selectedIndex=0;
						document.getElementById("NOMBRE_COMPRADOR").disabled=true;
					}

					if(!valida_campos)
						alert(mxg);
				}

				//Validaciones del formato ANP
				if(id_oper==19){
					p.gestion.setAnpRows(get("rows_anp_table"));

					//llenar el campo SKU con los skus que existan en el formato anp
					var gv = document.getElementById("SKU").value="";
					<%	for(int i=1;i<=anp_rows_table;i++){ %>
							gv+="<%=(i!=1?"|":"")%>"+document.getElementById("no_stock_<%=i%>").value;
					<%	}%>
					document.getElementById("SKU").value=gv;
					p.gestion.setSku(get("SKU"));
					p.gestion.setFechaAlta("sysdate");


					var msg_alert="No pueden existir celdas vac�as.";
					for(i=1;i<=document.getElementById("rows_anp_table").value;i++){
						if(document.getElementById("raiz_"+i).value==""){ valida_campos=false;}
						if(document.getElementById("no_stock_"+i).value==""){ valida_campos=false;}
						if(document.getElementById("descripcion_"+i).value==""){ valida_campos=false;}
						if(document.getElementById("estatus_vigencia_"+i).value=="-1"){ valida_campos=false;}
						if(document.getElementById("estatus_vigencia_"+i).value=="4"&&document.getElementById("DPC_fecha_oferta_"+i).value==""){ valida_campos=false;}
						if(document.getElementById("clasificacion_usu_familia_"+i).value==""){  valida_campos=false;}
						if(document.getElementById("clasificacion_usu_marca_"+i).value==""){valida_campos=false;}
						if(document.getElementById("unidad_medida_"+i).value==""){valida_campos=false;}
						if(document.getElementById("empaque_estandar_"+i).value==""){ valida_campos=false;}
						if(document.getElementById("subcat_linea_sublinea_"+i).value==""){ valida_campos=false;}
						if(document.getElementById("precio_lista_"+i).value==""){ valida_campos=false;}
						if(document.getElementById("precio_minimo_venta_"+i).value==""){ valida_campos=false;}
					}

					if(!valida_campos&&!addRow)
						alert(msg_alert);
				}

				//Validaciones de las pantallas de autorizacion o enterado
				if(id_oper==2||id_oper==7||id_oper==10||id_oper==11||id_oper==12||id_oper==13||id_oper==14||id_oper==15||id_oper==16||id_oper==18||id_oper==20||id_oper==21||id_oper==22||id_oper==23){
				    var autoriza;
	    			if(id_oper==2)
	    				autoriza= document.req_costeo.AUTORIZA_VICEPRESIDENCIA;
	    			if(id_oper==7)
	    				autoriza= document.req_costeo.AUTORIZA_COSTOS;
	    			if(id_oper==10)
	    				autoriza=document.req_costeo.AUTORIZA_GROSS_MKT;
	    			if(id_oper==11)
	    				autoriza=document.req_costeo.AUTORIZA_GROSS_GROUP;
	    			if(id_oper==12)
	    				autoriza=document.req_costeo.AUTORIZA_GROSS_DIR_MKT;
	    			if(id_oper==13)
	    				autoriza=document.req_costeo.AUTORIZA_GROSS_COSTOS;
	    			if(id_oper==14)
	    				autoriza=document.req_costeo.AUTORIZA_GROSS_DIR_VENTAS;
	    			if(id_oper==15)
	    				autoriza=document.req_costeo.AUTORIZA_GROSS_DIR_FINANZAS;
	    			if(id_oper==16)
	    				autoriza=document.req_costeo.AUTORIZA_GROSS_DIR_GENERAL;
	    			if(id_oper==18)
	    				autoriza=document.req_costeo.AUTORIZA_REPORTES;
	    			if(id_oper==20)
	    				autoriza=document.req_costeo.ENTERADO_OPERACIONES;
	    			if(id_oper==21)
	    				autoriza=document.req_costeo.AUTORIZA_CADENA_SUM;
	    			if(id_oper==22)
	    				autoriza=document.req_costeo.ENTERADO_INGENIERIA_INDUSTRIAL;
	    			if(id_oper==23)
	    				autoriza=document.req_costeo.AUTORIZA_COSTO_EN_PRMS;

	    			if(id_oper!=20&&id_oper!=22){//&&id_oper!=23
		    			if(autoriza[0].checked){
							if(confirm("Confirmar Autorizaci�n")){
								valida_campos = true;
								parent.document.getElementById('observaciones').value="Ninguno";
							}
							else
								valida_campos = false;
						}
						else if(autoriza[1].checked) {
							if(confirm("Confirmar que NO Autoriza")&&parent.document.getElementById('observaciones').value!="")
								valida_campos = true;
							else {
								if(parent.document.getElementById('observaciones').value=="")
									alert("Observaciones es requerido");
								valida_campos = false;
							}
					<% if(!vigencia_landed_cost) { %>
						} else if(autoriza[2].checked) {
							if(confirm("Confirmar que NO Autoriza por vigencia vencida en Landed Cost")&&parent.document.getElementById('observaciones').value!="")
								valida_campos = true;
							else {
								if(parent.document.getElementById('observaciones').value=="")
									alert("Observaciones es requerido");
								valida_campos = false;
							}
					<% } %>;
						} else {
							alert("Autorizaci�n es requerido");
							valida_campos = false;
						}
					}

					if(id_oper==20||id_oper==22){
						if(!autoriza.checked){
							alert("Enterado es requerido");
							valida_campos = false;
						}
					}

					/*if(id_oper==23){
						if(!autoriza.checked){
							alert("�El producto ya tiene costo en PRMS?, es requerido");
							valida_campos = false;
						}
					}*/
	    		}
		} catch (e) {
			window.alert("inicioOnSubmit: Error: " + e.message);
			return false;
		}
		return valida_campos;
	}

	//Validacion de documentos requeridos en cada paso del flujo
	function inicioPostSubmit(id_oper) {
		var valida_campos = true;

	    		if(id_oper==1||id_oper==5||id_oper==6||id_oper==8||id_oper==9||id_oper==17){
					<%CasoBusinessLogic cbl = new CasoBusinessLogic("jdbc/gestion"); %>
	    		}
			    if(id_oper==1){
    		    	<%

    				FortimaxFile[] solicitudes_calidad= cbl.getArchivosDeDocumento("ANP",objCaso.getIdGabinete(),2,1);
    				FortimaxFile[] solicitudes_cotizacion= cbl.getArchivosDeDocumento("ANP",objCaso.getIdGabinete(),3,1);
    				FortimaxFile[] solicitudes_landed= cbl.getArchivosDeDocumento("ANP",objCaso.getIdGabinete(),4,1);
    				FortimaxFile[] ips_ficha_tec= cbl.getArchivosDeDocumento("ANP",objCaso.getIdGabinete(),5,1);

    				%>
    				var msg="";
    				if(<%=solicitudes_calidad.length%>==0){
    					msg='Documento requerido en Solicitudes de calidad\n';//nacional,importado,mixto
    				}
    				if(<%=solicitudes_cotizacion.length%>==0&&(
    									(!document.getElementById("FABRICACION_IMP").checked && document.getElementById("FABRICACION_NAL").checked)||
    									(document.getElementById("FABRICACION_IMP").checked && document.getElementById("FABRICACION_NAL").checked)//||
    									//(document.getElementById("COMPRA_NAL").checked)
    									)){
    					msg+='Documento requerido en Solicitudes de cotizaci�n\n';//nacional
    				}
    				if(<%=solicitudes_landed.length%>==0&&(//importado o mixto o compra nacional
    									(document.getElementById("FABRICACION_IMP").checked && !document.getElementById("FABRICACION_NAL").checked)||
    									(document.getElementById("FABRICACION_IMP").checked && document.getElementById("FABRICACION_NAL").checked)||
    									(document.getElementById("COMPRA_NAL").checked)
    									)){
    					msg+='Documento requerido en Solicitudes de landed cost\n';
    				}
    				if(<%=ips_ficha_tec.length%>==0){//nacional,importado, mixto
    					msg+='Documento requerido en IPS o ficha t�cnica\n';
	    			}
	    			if(msg!=""){
	    				alert(msg);
						valida_campos = false;
	    			}
	    		}

	    		if(id_oper==5){
	    			<%FortimaxFile[] cotizaciones= cbl.getArchivosDeDocumento("ANP",objCaso.getIdGabinete(),6,1);%>
	    			var msg="";
	    	    	if(<%=cotizaciones.length%>==0){
    					msg+='Documento requerido en Cotizaciones\n';
	    			}
	    			if(msg!=""){
	    				alert(msg);
						valida_campos = false;
	    			}
	    		}
	    		if(id_oper==6){
	    			<%FortimaxFile[] landed_cost= cbl.getArchivosDeDocumento("ANP",objCaso.getIdGabinete(),7,1);%>
	    			var msg="";
	    	    	if(<%=landed_cost.length%>==0){
    					msg+='Documento requerido en Landed Cost\n';
	    			}
	    			<%if(!vigencia_landed_cost){%>
	    				msg+='Vigencia vencida en Landed Cost, favor de sustituirlo.\n';
	    			<%}%>
	    			if(msg!=""){
	    				alert(msg);
						valida_campos = false;
	    			}
	    		}
	    		<%if(vigencia_landed_cost){%> //El Gross margin solo sera requerido si el landed esta vigente
	    		if(id_oper==8||id_oper==9){
	    			<%FortimaxFile[] gross_margin= cbl.getArchivosDeDocumento("ANP",objCaso.getIdGabinete(),8,1);%>
	    			var msg="";
	    	    	if(<%=gross_margin.length%>==0){
    					msg+='Documento requerido en Gross Margin\n';
	    			}
	    			if(<%=!vigencia_gross_margin%>){
    					msg+='Favor de actualizar Gross Margin.\n';
	    			}
	    			if(msg!=""){
	    				alert(msg);
						valida_campos = false;
	    			}
	    		}
	    		<%}%>
	    		if(id_oper==17){
	    			<%
	    			FortimaxFile[] reportes_calidad= cbl.getArchivosDeDocumento("ANP",objCaso.getIdGabinete(),9,1);
	    			ArrayList arte_y_empaque = new ArrayList();
	    			arte_y_empaque=cbl.getDocumentosDeCarpeta("ANP",objCaso.getIdGabinete(),10);
	    			%>
	    			var msg="";
	    	    	if(<%=reportes_calidad.length%>==0){
    					msg+='Documento requerido en Reportes de Calidad\n';
	    			}
	    			if(<%=arte_y_empaque.size()%><2){
    					msg+='Documento requerido en Arte y Empaque\n';
	    			}
	    			else{
		    			<%for(int i=0;i<arte_y_empaque.size();i++){//viendo que tengan paginas
		    				Documento d =(Documento)arte_y_empaque.get(i);
			    			if(d.getNumeroPaginas()==0){%>
		    					msg+='Documentos requeridos en Arte y Empaque\n';
			    			<%
			    				break;
			    			}
		    			}
		    			%>
	    			}
	    			if(msg!=""){
	    				alert(msg);
						valida_campos = false;
	    			}
	    		}


	    		return valida_campos;

	}

	function ResponsableSig(id_oper){
		var regresa=null;
		//if(id_oper==1)
		//	return "VICEPRESIDENCIA";
		//if(id_oper==2){
		//	if(document.getElementById("AUTORIZA_VICEPRESIDENCIA").checked)
		//		return "<%=objCaso.getCasoDato("GERENTE_PRODUCTO").getValor()%>";
		//	else
		//		return "CONTINUAR";
		//}
		//if(id_oper==3)   //Se decidio juntar la 3 y la 4 por ser el mismo usuario por lo tanto la 4 queda en desuso
		//	return "MARKETING";
		if(id_oper==1 && document.getElementById("FABRICACION_NAL").checked && !document.getElementById("FABRICACION_IMP").checked)//nacional
			return "INGENIERIA_INDUSTRIAL";
		if(id_oper==1 && !document.getElementById("FABRICACION_NAL").checked && document.getElementById("FABRICACION_IMP").checked)//importado
			//return "COMPRAS";
			return document.getElementById("NOMBRE_COMPRADOR").value; //tenia objCaso.getCasoDato("COMPRADOR").getValor()
		if(id_oper==1 && document.getElementById("FABRICACION_NAL").checked && document.getElementById("FABRICACION_IMP").checked)//mixto
			//return "COMPRAS";
			return document.getElementById("NOMBRE_COMPRADOR").value; //tenia objCaso.getCasoDato("COMPRADOR").getValor()
		if(id_oper==1 && document.getElementById("COMPRA_NAL").checked)//Compra nacional, mismo comportamiento de importado o mixto
			return document.getElementById("NOMBRE_COMPRADOR").value; //tenia objCaso.getCasoDato("COMPRADOR").getValor()
		if(id_oper==5)
			return "COSTOS";
		if(id_oper==6 && document.getElementById("FABRICACION_NAL").checked && document.getElementById("FABRICACION_IMP").checked)//mixto
			return "INGENIERIA_INDUSTRIAL";
		if(id_oper==6 && !document.getElementById("FABRICACION_NAL").checked && document.getElementById("FABRICACION_IMP").checked)//importacion
			return "COSTOS";
		if(id_oper==6 && document.getElementById("COMPRA_NAL").checked)//Compra nacional, mismo comportamiento de importado o mixto
			return "COSTOS";
		if(id_oper==7  && document.getElementById("AUTORIZA_COSTOS").checked && !document.getElementById("GERENCIA_VENTAS_EX").checked)//ventas nacionales
			return "<%=objCaso.getCasoDato("GERENTE_PRODUCTO").getValor()%>";
		if(id_oper==7  && document.getElementById("AUTORIZA_COSTOS").checked && document.getElementById("GERENCIA_VENTAS_EX").checked)//ventas exportacion
			return "VENTAS_EXPORTACION";
		if(id_oper==7  && !document.getElementById("AUTORIZA_COSTOS").checked){
			if(document.req_costeo.AUTORIZA_COSTOS[1].checked)
				return "CONTINUAR";
			else
				return "<%=objCaso.getCasoDato("GERENTE_PRODUCTO").getValor()%>";//no por vigencia
		}
		if(id_oper==8)
			return "<%=objCaso.getCasoDato("GERENTE_PRODUCTO").getValor()%>";
		if(id_oper==9)
			return "<%=objCaso.getCasoDato("GERENTE_PRODUCTO").getValor()%>";
		if(id_oper==10){
			if(document.getElementById("AUTORIZA_GROSS_MKT").checked)
				return "DIRECCION_MKT"; //tenia GROUPER_MARKETING Ethiel decidieron brincarse este paso, no se elimina el grupo de BD por si se arrepienten
			else if(document.req_costeo.AUTORIZA_GROSS_MKT[1].checked)
				return (document.getElementById("GERENCIA_VENTAS_EX").checked?"VENTAS_EXPORTACION":"<%=objCaso.getCasoDato("GERENTE_PRODUCTO").getValor()%>");
			else
				return "<%=objCaso.getCasoDato("GERENTE_PRODUCTO").getValor()%>";	//no por vigencia
		}
		/*if(id_oper==11){ Ethiel, se decidio brincar este paso desde la op 10
			if(document.getElementById("AUTORIZA_GROSS_GROUP").checked)
				return "DIRECCION_MKT";
			else
				return "MARKETING";
		}*/
		if(id_oper==12){
			if(document.getElementById("AUTORIZA_GROSS_DIR_MKT").checked)
				return "COSTOS";
			else if(document.req_costeo.AUTORIZA_GROSS_DIR_MKT[1].checked)
				return (document.getElementById("GERENCIA_VENTAS_EX").checked?"VENTAS_EXPORTACION":"<%=objCaso.getCasoDato("GERENTE_PRODUCTO").getValor()%>");
			else
				return "<%=objCaso.getCasoDato("GERENTE_PRODUCTO").getValor()%>";
		}
		if(id_oper==13){
			if(document.getElementById("AUTORIZA_GROSS_COSTOS").checked)
				return "DIRECCION_VENTAS";
			else if(document.req_costeo.AUTORIZA_GROSS_COSTOS[1].checked)
				return (document.getElementById("GERENCIA_VENTAS_EX").checked?"VENTAS_EXPORTACION":"<%=objCaso.getCasoDato("GERENTE_PRODUCTO").getValor()%>");
			else
				return "<%=objCaso.getCasoDato("GERENTE_PRODUCTO").getValor()%>";
		}
		if(id_oper==14){
			if(document.getElementById("AUTORIZA_GROSS_DIR_VENTAS").checked)
				return "DIRECCION_FINANZAS";
			else if(document.req_costeo.AUTORIZA_GROSS_DIR_VENTAS[1].checked)
				return (document.getElementById("GERENCIA_VENTAS_EX").checked?"VENTAS_EXPORTACION":"<%=objCaso.getCasoDato("GERENTE_PRODUCTO").getValor()%>");
			else
				return "<%=objCaso.getCasoDato("GERENTE_PRODUCTO").getValor()%>";
		}
		if(id_oper==15){
			if(document.getElementById("AUTORIZA_GROSS_DIR_FINANZAS").checked)
				return "DIRECCION_GENERAL";
			else if(document.req_costeo.AUTORIZA_GROSS_DIR_FINANZAS[1].checked)
				return (document.getElementById("GERENCIA_VENTAS_EX").checked?"VENTAS_EXPORTACION":"<%=objCaso.getCasoDato("GERENTE_PRODUCTO").getValor()%>");
			else
				return "<%=objCaso.getCasoDato("GERENTE_PRODUCTO").getValor()%>";
		}
		if(id_oper==16){
			if(document.getElementById("AUTORIZA_GROSS_DIR_GENERAL").checked)
				return "<%=objCaso.getCasoDato("GERENTE_PRODUCTO").getValor()%>";
			else if(document.req_costeo.AUTORIZA_GROSS_DIR_GENERAL[1].checked)
				return (document.getElementById("GERENCIA_VENTAS_EX").checked?"VENTAS_EXPORTACION":"<%=objCaso.getCasoDato("GERENTE_PRODUCTO").getValor()%>");
			else
				return "<%=objCaso.getCasoDato("GERENTE_PRODUCTO").getValor()%>";
		}
		if(id_oper==17)
			return "<%=vigencia_landed_cost?"DIRECCION_MKT":objCaso.getCasoDato("GERENTE_PRODUCTO").getValor()%>";
		if(id_oper==18){
			if(document.getElementById("AUTORIZA_REPORTES").checked)
				return "<%=objCaso.getCasoDato("GERENTE_PRODUCTO").getValor()%>";
			else if(document.req_costeo.AUTORIZA_REPORTES[1].checked)
				return "CONTINUAR";
			else
				return "<%=objCaso.getCasoDato("GERENTE_PRODUCTO").getValor()%>";
		}
		if(id_oper==19)
			return "CADENA_DE_SUMINISTRO";//tenia OPERACIONES pero decidieron eliminar el paso 20
		//if(id_oper==20)
		//	return "CADENA_DE_SUMINISTRO";
		if(id_oper==21){
			if(document.getElementById("AUTORIZA_CADENA_SUM").checked)
				return "INGENIERIA_INDUSTRIAL";
			else
				return "<%=objCaso.getCasoDato("GERENTE_PRODUCTO").getValor()%>";
		}
		if(id_oper==22)
			return "COSTOS";
		if(id_oper==23)
			if(document.getElementById("AUTORIZA_COSTO_EN_PRMS").checked)
				return "CONTINUAR";
			else if(document.req_costeo.AUTORIZA_COSTO_EN_PRMS[1].checked)
				return "INGENIERIA_INDUSTRIAL";


		//if(id_oper==2&&window.parent.gestion.getRechazado()=="SI")
		//	regresa="f_registra";


	}
	function OperacionSig(id_oper){

		//if(id_oper==1)
		//	return "autoriza_req_costeo";
		//if(id_oper==2){
		//	if(document.getElementById("AUTORIZA_VICEPRESIDENCIA").checked)
		//		return "adjunta_sol_calidad";
		//	else
		//		return "CONTINUAR";
		//}
		//if(id_oper==3) //Se decidio juntar la 3 y la 4 por ser el mismo usuario por lo tanto la 4 queda en desuso
		//	return "solicita_coti_landed";
		if(id_oper==1 && document.getElementById("FABRICACION_NAL").checked && !document.getElementById("FABRICACION_IMP").checked)//nacional
			return "adjunta_cotizacion";
		if(id_oper==1 && !document.getElementById("FABRICACION_NAL").checked && document.getElementById("FABRICACION_IMP").checked)//importado
			return "adjunta_landed_cost";
		if(id_oper==1 && document.getElementById("FABRICACION_NAL").checked && document.getElementById("FABRICACION_IMP").checked)//mixto
			return "adjunta_landed_cost";
		if(id_oper==1 && document.getElementById("COMPRA_NAL").checked)//Compra nacional, mismo comportamiento de importado o mixto
			return "adjunta_landed_cost";
		if(id_oper==5)
			return "vobo_costos";
		if(id_oper==6 && document.getElementById("FABRICACION_NAL").checked && document.getElementById("FABRICACION_IMP").checked)//mixto)
			return "adjunta_cotizacion";
		if(id_oper==6 && !document.getElementById("FABRICACION_NAL").checked && document.getElementById("FABRICACION_IMP").checked)//importacion
			return "vobo_costos";
		if(id_oper==6 && document.getElementById("COMPRA_NAL").checked)//Compra nacional, mismo comportamiento de importado o mixto
			return "vobo_costos";
		if(id_oper==7  && document.getElementById("AUTORIZA_COSTOS").checked && !document.getElementById("GERENCIA_VENTAS_EX").checked)//ventas nacionales
			return "gross_margin_nal";
		if(id_oper==7  && document.getElementById("AUTORIZA_COSTOS").checked && document.getElementById("GERENCIA_VENTAS_EX").checked)//ventas exportacion
			return "gross_margin_exp";
		if(id_oper==7  && !document.getElementById("AUTORIZA_COSTOS").checked){
			if(document.req_costeo.AUTORIZA_COSTOS[1].checked)
				return "CONTINUAR";
			else
				return "requisicion_costeo";//"adjunta_sol_calidad";
		}
		if(id_oper==8)
			return "<%=vigencia_landed_cost?"autoriza_gross_mkt":"requisicion_costeo"%>";
		if(id_oper==9)
			return "<%=vigencia_landed_cost?"autoriza_gross_mkt":"requisicion_costeo"%>";
		if(id_oper==10){
			if(document.getElementById("AUTORIZA_GROSS_MKT").checked)
				return "autoriza_gross_dmkt";//autoriza_gross_grup Ethiel, decidieron brincarse este paso, no se elimina la operacion en BD por si se arrepienten
			else if(document.req_costeo.AUTORIZA_GROSS_MKT[1].checked)
				//return "requisicion_costeo";
				return (document.getElementById("GERENCIA_VENTAS_EX").checked?"gross_margin_exp":"gross_margin_nal");
			else
				return "requisicion_costeo";//"adjunta_sol_calidad";
		}
		/*if(id_oper==11){Ethiel, se decidio brincar este paso desde la op 10
			if(document.getElementById("AUTORIZA_GROSS_GROUP").checked)
				return "autoriza_gross_dmkt";
			else
				return "requisicion_costeo";
		}*/
		if(id_oper==12){
			if(document.getElementById("AUTORIZA_GROSS_DIR_MKT").checked)
				return "autoriza_gross_cost";
			else if(document.req_costeo.AUTORIZA_GROSS_DIR_MKT[1].checked)
				//return "requisicion_costeo";
				return (document.getElementById("GERENCIA_VENTAS_EX").checked?"gross_margin_exp":"gross_margin_nal");
			else
				return "requisicion_costeo";//"adjunta_sol_calidad";
		}
		if(id_oper==13){
			if(document.getElementById("AUTORIZA_GROSS_COSTOS").checked)
				return "autoriza_gross_dven";
			else if(document.req_costeo.AUTORIZA_GROSS_COSTOS[1].checked)
				//return "requisicion_costeo";
				return (document.getElementById("GERENCIA_VENTAS_EX").checked?"gross_margin_exp":"gross_margin_nal");
			else
				return "requisicion_costeo";//"adjunta_sol_calidad";
		}
		if(id_oper==14){
			if(document.getElementById("AUTORIZA_GROSS_DIR_VENTAS").checked)
				return "autoriza_gross_dfina";
			else if(document.req_costeo.AUTORIZA_GROSS_DIR_VENTAS[1].checked)
				//return "requisicion_costeo";
				return (document.getElementById("GERENCIA_VENTAS_EX").checked?"gross_margin_exp":"gross_margin_nal");
			else
				return "requisicion_costeo";//"adjunta_sol_calidad";
		}
		if(id_oper==15){
			if(document.getElementById("AUTORIZA_GROSS_DIR_FINANZAS").checked)
				return "autoriza_gross_dgen";
			else if(document.req_costeo.AUTORIZA_GROSS_DIR_FINANZAS[1].checked)
				//return "requisicion_costeo";
				return (document.getElementById("GERENCIA_VENTAS_EX").checked?"gross_margin_exp":"gross_margin_nal");
			else
				return "requisicion_costeo";//"adjunta_sol_calidad";
		}
		if(id_oper==16){
			if(document.getElementById("AUTORIZA_GROSS_DIR_GENERAL").checked)
				return "adjunta_res_calidad";
			else if(document.req_costeo.AUTORIZA_GROSS_DIR_GENERAL[1].checked)
				//return "requisicion_costeo";
				return (document.getElementById("GERENCIA_VENTAS_EX").checked?"gross_margin_exp":"gross_margin_nal");
			else
				return "requisicion_costeo";//"adjunta_sol_calidad";
		}
		if(id_oper==17)
			return "<%=vigencia_landed_cost?"autoriza_reportes":"requisicion_costeo"%>";
		if(id_oper==18){
			if(document.getElementById("AUTORIZA_REPORTES").checked)
				return "captura_formato_anp";
			else if(document.req_costeo.AUTORIZA_REPORTES[1].checked)
				return "CONTINUAR";
			else
				return "requisicion_costeo";
		}
		if(id_oper==19)
			return "autoriza_cadena_sumi";//tenia enterado_operaciones pero decidieron eliminar el paso 20
		//if(id_oper==20)
		//	return "autoriza_cadena_sumi";
		if(id_oper==21){
			if(document.getElementById("AUTORIZA_CADENA_SUM").checked)
				return "enterado_ing_ind";
			else
				return "captura_formato_anp";
		}
		if(id_oper==22)
			return "verifica_tiene_costo";
		if(id_oper==23)
			if(document.getElementById("AUTORIZA_COSTO_EN_PRMS").checked)
				return "CONTINUAR";
			else if(document.req_costeo.AUTORIZA_COSTO_EN_PRMS[1].checked)
				return "enterado_ing_ind";
	}

	function mensaje(){
			//document.getElementById('ventana').style.display = 'block';
	}
	function validaNumerico(campo){
		//alert('entra a valida numero');
		if(isNaN(campo.value)&&campo.value!=""){
			alert('Se espera un N�mero');
			campo.focus();
		}
	}

	function appendRow(tblId){

		var index = document.getElementById("rows_anp_table").value;
		index++;
		document.getElementById("rows_anp_table").value=index;
		var tbl = document.getElementById(tblId);
		var newRow = tbl.insertRow(tbl.rows.length);
		var newCell = newRow.insertCell(0);
		newCell.innerHTML = '<input type="text" name="raiz_'+index+'" id="raiz_'+index+'" size="8" maxlength="8" value="" onblur="javascript:validaNumerico(this)" <%=(id_oper>19?"disabled":"") %>>';
		newCell = newRow.insertCell(1);
		newCell.innerHTML = '<input type="text" name="no_stock_'+index+'" id="no_stock_'+index+'" size="15" maxlength="15" value="" <%=(id_oper>19?"disabled":"") %>>';
		newCell = newRow.insertCell(2);
		newCell.innerHTML = '<textarea style="font-size: 10pt; font-family: arial, helvetica, sans-serif;" name="descripcion_'+index+'" id="descripcion_'+index+'" cols="30" rows="1" value="" <%=(id_oper>19?"readonly":"") %>></textarea>';
		newCell = newRow.insertCell(3);
		newCell.innerHTML ='<select name="estatus_vigencia_'+index+'" id="estatus_vigencia_'+index+'"  <%=(id_oper>19?"disabled":"")%>  onchange="fechaOferta(this,'+index+');">'+
										'<option value="-1">seleccionar</option>'+
										<%if(id_oper>=19){
											for(int j=0;j<lstimx_status.length;j++){ %>
											'<option value="<%=lstimx_status[j].getConsecutivo() %>"><%=lstimx_status[j].getValor() %></option>'+
										<%}
										} %>
									'</select>'+
									'<br>Fecha de oferta<br>De&nbsp;<input disabled type="text" name="DPC_fecha_oferta_'+index+'" id="DPC_fecha_oferta_'+index+'" value="" readonly="readonly" datepicker_format="DD/MM/YYYY" datepicker="false" datepicker_min="<%=today %>" <%=(id_oper!=19?"disabled":"")%> maxlength="10" size="10"/><br>A&nbsp;&nbsp;<input disabled type="text" name="DPC_fecha_oferta_fin_'+index+'" id="DPC_fecha_oferta_fin_'+index+'" value="" readonly="readonly" datepicker_format="DD/MM/YYYY" datepicker="false" datepicker_min="<%=today %>" <%=(id_oper!=19?"disabled":"")%> maxlength="10" size="10"/>';
		newCell = newRow.insertCell(4);
		newCell.innerHTML = '<input type="text" name="clasificacion_usu_familia_'+index+'" id="clasificacion_usu_familia_'+index+'" size="8" maxlength="3" value="" <%=(id_oper>19?"disabled":"") %>>';
		newCell = newRow.insertCell(5);
		newCell.innerHTML = '<input type="text" name="clasificacion_usu_marca_'+index+'" id="clasificacion_usu_marca_'+index+'" size="8" maxlength="3" value="" <%=(id_oper>19?"disabled":"") %>>';
		newCell = newRow.insertCell(6);
		newCell.innerHTML = '<input type="text" name="unidad_medida_'+index+'" id="unidad_medida_'+index+'" size="8" maxlength="8" value="" <%=(id_oper>19?"disabled":"") %>>';
		newCell = newRow.insertCell(7);
		newCell.innerHTML = '<input type="text" name="empaque_estandar_'+index+'" id="empaque_estandar_'+index+'" size="8" maxlength="8" value="" onblur="javascript:validaNumerico(this)" <%=(id_oper>19?"disabled":"") %>>';
		newCell = newRow.insertCell(8);
		newCell.innerHTML = '<textarea style="font-size: 10pt; font-family: arial, helvetica, sans-serif;" name="subcat_linea_sublinea_'+index+'" id="subcat_linea_sublinea_'+index+'" cols="10" rows="1" value="" <%=(id_oper>19?"readonly":"") %>></textarea>';
		newCell = newRow.insertCell(9);
		newCell.innerHTML = '<textarea style="font-size: 10pt; font-family: arial, helvetica, sans-serif;" name="precio_lista_'+index+'" id="precio_lista_'+index+'" cols="10" rows="1" value=""  <%=(id_oper>19?"readonly":"") %>></textarea>';
		newCell = newRow.insertCell(10);
		newCell.innerHTML = '<textarea style="font-size: 10pt; font-family: arial, helvetica, sans-serif;" name="precio_minimo_venta_'+index+'" id="precio_minimo_venta_'+index+'" cols="10" rows="1" value=""  <%=(id_oper>19?"readonly":"") %>></textarea>';
		//para que salga el control del datepicker tiene que estar guardado el campo
		parent.document.getElementById("pb_save_row").click();
		//document.location.reload(1);
	}
	function gerencia_ventas(campo){
		if(campo.value=="EX" && campo.checked){
		<%for(int i=0;i<lstimx_gerencia_ventas.length;i++){ %>
				if('<%=lstimx_gerencia_ventas[i].getConsecutivo()%>'!='EX')
					document.getElementById("GERENCIA_VENTAS_<%=lstimx_gerencia_ventas[i].getConsecutivo()%>").checked=false;
		<%}%>
		}
		if(campo.value!="EX" && campo.checked){
			document.getElementById("GERENCIA_VENTAS_EX").checked=false;
		}
	}
	function checks_fabricacion(campo){
		if(campo.value=="COMPRA_NACIONAL" && campo.checked){
			document.getElementById("FABRICACION_NAL").checked=false;
			document.getElementById("FABRICACION_IMP").checked=false;
			document.getElementById("NOMBRE_COMPRADOR").disabled=false;
		}
		if((campo.value=="NACIONAL"||campo.value=="IMPORTADO") && campo.checked){
			document.getElementById("COMPRA_NAL").checked=false;
			document.getElementById("NOMBRE_COMPRADOR").disabled=false;
		}
		if(campo.value=="NACIONAL"&& campo.checked&&!document.getElementById("FABRICACION_IMP").checked){
			//document.getElementById("NOMBRE_COMPRADOR").selectedIndex=0;
			document.getElementById("NOMBRE_COMPRADOR").disabled=true;
		}
		if(campo.value=="IMPORTADO"&&!campo.checked&&document.getElementById("FABRICACION_NAL").checked){
			//document.getElementById("NOMBRE_COMPRADOR").selectedIndex=0;
			document.getElementById("NOMBRE_COMPRADOR").disabled=true;
		}
	}
	function fechaOferta(campo_status,index){
		if(campo_status.value==4){//ofertas
			//document.getElementById(campo_fechaOf).disabled=false;
			document.getElementById("DPC_fecha_oferta_"+index).disabled=false;
			document.getElementById("DPC_fecha_oferta_fin_"+index).disabled=false;
		}
		else{
			document.getElementById("DPC_fecha_oferta_"+index).value="";
			document.getElementById("DPC_fecha_oferta_"+index).disabled=true;
			document.getElementById("DPC_fecha_oferta_fin_"+index).value="";
			document.getElementById("DPC_fecha_oferta_fin_"+index).disabled=true;
		}
	}
	</script>
  </head>
  <body onload="javascript:inicioOnLoad();">

  <!--

  	<div id="ventana" name="ventana" style="position: relative; width: 800px; height: 800px; display: none;">
		<br/><br/>
		<div align="center"><strong>Espere un momento por favor...<strong></div>
		<br/><br/>
	</div>

	-->

	<form name="req_costeo" id="req_costeo" method="post">
		<input type="hidden" name="GERENCIA_VENTAS" id="GERENCIA_VENTAS" value=""/>
		<input type="hidden" name="SKU" id="SKU" value=""/>

		<input type="hidden" name="rows_anp_table" id="rows_anp_table" value="1"/>
  		<div id="requisicion" style="position: relative; width: 100%; height: 800px; display: <%=(id_oper<19||id_oper==23?"block;":"none;") %>">

  		<table width="100%" border="0">
  			<tr><td colspan="4" align="center" bgcolor="#000000"><font color="#FFA500" style="font-size: 22pt; font-family: arial, helvetica, sans-serif;"><b>Requisici&oacute;n de Costeo</b></font></td></tr>
  			<tr>
  				<td align="right"><b>No. RDC:</b></td><td><input name="FOLIO" id="FOLIO" type="text" size="30" value="<%=folio %>" disabled/></td>
  				<!-- td align="right"><b>No. SNP:</b></td><td><input name="SNP" id="SNP" type="text" maxlength="30" size="30" value="" <%=(id_oper>1?"disabled":"")%>/></td-->
  				<td align="right"><b>Fecha de Solicitud:</b></td><td><input name="FECHA_SOLICITUD" id="FECHA_SOLICITUD" type="text" maxlength="10" size="30" value="<%=today%>" disabled/></td>
  			</tr>
  			 <tr>
  				<td align="right"><b>Solicitante:</b></td><td><input name="SOLICITANTE" id="SOLICITANTE" type="text" size="30" <%=(id_oper==1?"value=\""+solicitante+"\"":"value=\"\"") %> disabled/></td>
  				<td align="right"><b>Fecha de Lanzamiento:</b></td><td><input name="DPC_FECHA_LANZAMIENTO" id="DPC_FECHA_LANZAMIENTO" type="text" readonly="readonly" datepicker_format="DD/MM/YYYY" datepicker="false"  datepicker_min="<%=today %>" <%=(id_oper>1?"disabled":"")%> maxlength="10" size="30"/></td>
  				<!-- td colspan="2">&nbsp;</td-->
  			</tr>
  			<tr>
  				<td  align="right"><b>Categor&iacute;a:</b></td><td><input name="CATEGORIA" id="CATEGORIA" type="text"  size="30" value="<%=categoria %>" disabled/></td>

  				<td  align="right"><b>Comprador:</b></td><td>
  															<%if(id_oper==1){ %>
  															<select name="NOMBRE_COMPRADOR" id="NOMBRE_COMPRADOR"  <%=(id_oper>1?"disabled":"")%>>
  															<option value="Ninguno" selected>Ninguno</option>
  															<%for (Iterator iter = compradores.keySet().iterator(); iter.hasNext();) {
																String name = (String) iter.next();
																Usuario nombre_u = (Usuario) compradores.get(name); %>
                      											<option value="<%=nombre_u.getNombre()%>"><%=nombre_u.getNombre()%></option>
  															<%} %>
  															</select>
  															<%}else{ %>
  															<input name="NOMBRE_COMPRADOR" id="NOMBRE_COMPRADOR" type="text"  size="50" value="" disabled/>
  															<%} %>
  															</td>
  				<!-- td colspan="2">&nbsp;</td-->
  			</tr>
  			<tr><td colspan="4" align="center"  bgColor="#696969"><font color="#FFA500" size="3" face="arial, helvetica, sans-serif"><b>Detalle del Proyecto</b></font></td></tr>
  			<tr><td colspan="4"><b>Nombre del producto:</b><br><textarea name="NOMBRE_PRODUCTO" id="NOMBRE_PRODUCTO"  style="font-size: 10pt; font-family: arial, helvetica, sans-serif;" rows="3" cols="100" <%=(id_oper>1?"readonly":"")%>></textarea></td></tr>
  			<tr>
  				<td align="right"><b>Fabricaci&oacute;n:<br/><br/>Compra:</b></td>
  				<td>Nacional<input type="checkbox" name="FABRICACION_NAL" id="FABRICACION_NAL" value="NACIONAL" onclick="checks_fabricacion(this)" <%=(id_oper>1?"disabled":"")%>/>
  				&nbsp;&nbsp;&nbsp;Importado<input type="checkbox" name="FABRICACION_IMP" id="FABRICACION_IMP" value="IMPORTADO" onclick="checks_fabricacion(this)" <%=(id_oper>1?"disabled":"")%>/>
  				<br/><br/>Nacional<input type="checkbox" name="COMPRA_NAL" id="COMPRA_NAL" value="COMPRA_NACIONAL" onclick="checks_fabricacion(this)" <%=(id_oper>1?"disabled":"")%>/>
  				</td>
  				<td ><b>Gerencia de Ventas:</b><BR><BR>
  						<%for(int i=0;i<lstimx_gerencia_ventas.length;i++){
  							if(!"EX".equals(lstimx_gerencia_ventas[i].getConsecutivo())){
  						%>

  							<input type="checkbox" name="GERENCIA_VENTAS_<%=lstimx_gerencia_ventas[i].getConsecutivo() %>" id="GERENCIA_VENTAS_<%=lstimx_gerencia_ventas[i].getConsecutivo() %>" value="<%=lstimx_gerencia_ventas[i].getConsecutivo() %>" onclick="gerencia_ventas(this)" <%=(id_oper>1?"disabled":"")%>/><%=lstimx_gerencia_ventas[i].getValor() %><br>
  						<%}
  						} %>
  				</td>
  				<td valign="top" ><BR><BR>
  						<%for(int i=0;i<lstimx_gerencia_ventas.length;i++){
  							if("EX".equals(lstimx_gerencia_ventas[i].getConsecutivo())){
  						%>
  							<input type="checkbox" name="GERENCIA_VENTAS_<%=lstimx_gerencia_ventas[i].getConsecutivo() %>" id="GERENCIA_VENTAS_<%=lstimx_gerencia_ventas[i].getConsecutivo() %>" value="<%=lstimx_gerencia_ventas[i].getConsecutivo() %>" onclick="gerencia_ventas(this)" <%=(id_oper>1?"disabled":"")%>/><%=lstimx_gerencia_ventas[i].getValor() %><br>
  						<%}
  						} %>
  				</td>
  			</tr>
  			<tr><td colspan="4" valign="baseline"><b>Descripci&oacute;n detallada:</b><br><textarea name="DESCRIPCION" id="DESCRIPCION" style="font-size: 10pt; font-family: arial, helvetica, sans-serif;" maxlength="1500" rows="4" cols="100" <%=(id_oper>1?"readonly":"")%>></textarea></td></tr>
  			<tr>
  				<td align="right"><b>Herramienta:</b></td><td>Si<input type="radio" name="HERRAMIENTA" id="HERRAMIENTA" value="Si"   <%=(id_oper>1?"disabled":"checked")%>>
  								&nbsp;&nbsp;&nbsp;No<input type="radio" name="HERRAMIENTA" id="HERRAMIENTA" value="No"   <%=(id_oper>1?"disabled":"")%>>
  				</td>
  				<td align="right" ><b>Evaluar si necesita herramienta:</b></td><td><input type="text" name="EVALUAR_HERRAMIENTA" id="EVALUAR_HERRAMIENTA" maxlength="65" size="30" value="" <%=(id_oper>1?"disabled":"")%>/></td>
  				<!-- td colspan="2">&nbsp;</td-->
  			</tr>
  			<tr>
  				<td align="right"><b>Reproceso:</b></td><td>Si<input type="radio" name="REPROCESO" id="REPROCESO" value="Si"   <%=(id_oper>1?"disabled":"checked")%>>
  								&nbsp;&nbsp;&nbsp;No<input type="radio" name="REPROCESO" id="REPROCESO" value="No"   <%=(id_oper>1?"disabled":"")%>>
  				</td>
  				<td colspan="2">&nbsp;</td>
  			</tr>
  			<tr>
				<td align="right"><b>Empaque:</b></td><td><select name="EMPAQUE" id="EMPAQUE"  <%=(id_oper>1?"disabled":"")%> onchange="if(this[this.selectedIndex].value==0){document.getElementById('OTRO_EMPAQUE').disabled=false;document.getElementById('OTRO_EMPAQUE').focus;} else {document.getElementById('OTRO_EMPAQUE').value=''; document.getElementById('OTRO_EMPAQUE').disabled=true;}">
					<option value="-1">seleccionar</option>
					<%for(int i=0;i<lstimx_empaque.length;i++){ %>
						<option value="<%=lstimx_empaque[i].getConsecutivo() %>"><%=lstimx_empaque[i].getValor() %></option>
					<%} %>
					<option value="0">OTRO</option>
					</select>
				</td>
				<td align="right"><b>Otro empaque:</b></td><td><input type="text" name="OTRO_EMPAQUE" id="OTRO_EMPAQUE" maxlength="50" size="30" value="" disabled/></td>
  				<!-- td colspan="2">&nbsp;</td-->
			</tr>
			<tr>
				<td align="right"><b>Piezas por caja:</b></td><td><input name="PIEZAS_CAJA" id="PIEZAS_CAJA" type="text" maxlength="10" size="30" value="" onblur="javascript:validaNumerico(this)" <%=(id_oper>1?"disabled":"")%>/></td>
				<td align="right"><b>Volumen estimado:</b></td><td><input name="VOLUMEN_ESTIMADO" id="VOLUMEN_ESTIMADO" type="text" maxlength="10" size="30" value="" onblur="javascript:validaNumerico(this)" <%=(id_oper>1?"disabled":"")%>/></td>
				<!-- td colspan="2">&nbsp;</td-->
			</tr>
			<tr>
				<td align="right"><b>Fecha req. cotizaci&oacute;n:</b></td><td><input name="DPC_FECHA_REQ_COTIZACION" id="DPC_FECHA_REQ_COTIZACION" type="text" value="" readonly="readonly" datepicker_format="DD/MM/YYYY" datepicker="false" datepicker_min="<%=today %>" <%=(id_oper>1?"disabled":"")%> maxlength="10" size="30"/></td>
				<td align="right"><b>Presupuesto:</b></td><td>Si<input type="radio" name="PRESUPUESTO" id="PRESUPUESTO" value="Si"  <%=(id_oper>1?"disabled":"checked")%>>
											&nbsp;&nbsp;&nbsp;No<input type="radio" name="PRESUPUESTO" id="PRESUPUESTO" value="No"  <%=(id_oper>1?"disabled":"")%>>
				</td>
				<!-- td colspan="2">&nbsp;</td-->
			</tr>
			<tr>
				<!-- td align="right"><b>Cantidad de venta:</b></td><td><input name="CANTIDAD" id="CANTIDAD" type="text" maxlength="20" size="30" value=""  <%=(id_oper>1?"disabled":"")%>/></td-->
				<td align="right"><b>Precio promedio de venta:</b></td><td><input name="PRECIO_PROMEDIO" id="PRECIO_PROMEDIO" type="text" maxlength="15" size="30" value="" onblur="javascript:validaNumerico(this)" <%=(id_oper>1?"disabled":"")%>/></td>
				<td colspan="2">&nbsp;</td>
				<!-- td colspan="2">&nbsp;</td-->
			</tr>
			<tr>
				<td align="right"><b>Estimaci&oacute;n de primera orden de Compra:</b></td><!-- Estimaci&oacute;n de primera orden de fabricaci&oacute;n -->
				<td colspan="5"><input name="ESTIMACION_POF" id="ESTIMACION_POF" type="text" maxlength="80" size="80" value=""  <%=(id_oper>1?"disabled":"")%>/></td>
			</tr>
  			<tr>
  				<td align="right">&nbsp;</td><!-- <b>Estimaci&oacute;n de compra:</b> -->
  				<td>Inventario&nbsp;<input type="radio" name="ESTIMACION_COMP" id="ESTIMACION_COMP" value="INVENTARIO" onclick="javascript:document.getElementById('ESTIMACION_CLIE').value=''; document.getElementById('ESTIMACION_CLIE').disabled=true;" <%=(id_oper>1?"disabled":"checked")%>/>
  				<br>Cliente&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type="radio" name="ESTIMACION_COMP" id="ESTIMACION_COMP" value="CLIENTE" onclick="javascript:document.getElementById('ESTIMACION_CLIE').disabled=false;" <%=(id_oper>1?"disabled":"")%>/>
  				</td>

  				<td align="right"><b>Nombre del cliente:</b></td>
  				<td><input name="ESTIMACION_CLIE" id="ESTIMACION_CLIE" type="text" maxlength="30" size="30" value=""  disabled/></td>
  				<!-- td colspan="2">&nbsp;</td-->
			</tr>
			<tr>
				<td align="right"><b>Estimaci&oacute;n de venta mensual:</b></td>
				<td colspan="5"><input name="ESTIMACION_VENTA" id="ESTIMACION_VENTA" type="text" maxlength="80" size="80" value=""  <%=(id_oper>1?"disabled":"")%>/></td>
			</tr>
			<tr><td colspan="4" align="center" bgColor="#696969"><font color="#FFA500" size="3" face="arial, helvetica, sans-serif"><b>Medidas</b></font></td></tr>
			<tr>
				<!-- td align="right"><b>Caja:</b></td><td><input name="CAJA" id="CAJA" type="text" maxlength="25" size="30" value=""  <%=(id_oper>1?"disabled":"")%>/></td-->
				<td align="right"><b>Caja master:</b></td><td><input name="CAJA_MASTER" id="CAJA_MASTER" type="text" maxlength="15" size="30" value=""  <%=(id_oper>1?"disabled":"")%>/></td>
				<td align="right"><b>Caja individual:</b></td><td><input name="CAJA_INDIVIDUAL" id="CAJA_INDIVIDUAL" type="text" maxlength="25" size="30" value=""  <%=(id_oper>1?"disabled":"")%>/></td>
			</tr>
			<tr>
				<td align="right"><b>Palet:</b></td><td><input name="PALET" id="PALET" type="text" maxlength="25" size="30" value=""  <%=(id_oper>1?"disabled":"")%>/></td>
				<td align="right"><b>Estibas:</b></td><td><input name="ESTIBAS" id="ESTIBAS" type="text" maxlength="25" size="30" value=""  <%=(id_oper>1?"disabled":"")%>/></td>
				<!-- td colspan="2">&nbsp;</td-->
			</tr>

			<%if(objCaso.getCasoOperacion(0).getObservacion() != null && !"".equals(objCaso.getCasoOperacion(0).getObservacion())){ %>
				<tr><td colspan="4" align="center" bgColor="#696969"><font color="#FFA500" size="3" face="arial, helvetica, sans-serif"><b>Observaciones</b></font></td></tr>

    		<tr><td colspan="4" align="left" bgcolor="#f5f5f5"><%=(objCaso.getCasoOperacion(0).getObservacion() == null) ? "" : objCaso.getCasoOperacion(0).getObservacion()%></td></tr>
    		<%} %>
			<%if(id_oper==2||id_oper==7||id_oper==10||id_oper==11||id_oper==12||id_oper==13||id_oper==14||id_oper==15||id_oper==16||id_oper==18){
			    String vobo=null;
			    String titulo_autorizacion=null;
    			if(id_oper==2)
    				vobo= "VICEPRESIDENCIA";
    			if(id_oper==7)
    				vobo= "COSTOS";
    			if(id_oper==10)
    				vobo="GROSS_MKT";
    			if(id_oper==11)
    				vobo="GROSS_GROUP";
    			if(id_oper==12)
    				vobo="GROSS_DIR_MKT";
    			if(id_oper==13)
    				vobo="GROSS_COSTOS";
    			if(id_oper==14)
    				vobo="GROSS_DIR_VENTAS";
    			if(id_oper==15)
    				vobo="GROSS_DIR_FINANZAS";
    			if(id_oper==16)
    				vobo="GROSS_DIR_GENERAL";
    			if(id_oper==18)
    				vobo="REPORTES";

    		%>

			<tr><td colspan="4" align="center" bgColor="#696969"><font color="#FFA500" size="3" face="arial, helvetica, sans-serif"><b>Autorizaci&oacute;n</b></font></td></tr>
			<tr><td colspan="4" align="center" ><b>Si</b><input type="radio" name="AUTORIZA_<%=vobo %>" id="AUTORIZA_<%=vobo %>" value="Si" <%=(!vigencia_landed_cost&&(id_oper>=7&&id_oper<=18)?"disabled":"") %>>
														 &nbsp;&nbsp;&nbsp;<b>No</b><input type="radio" name="AUTORIZA_<%=vobo %>" id="AUTORIZA_<%=vobo %>" value="No" >
														 <%if(!vigencia_landed_cost&&(id_oper>=7&&id_oper<=18)){%>
														 &nbsp;&nbsp;&nbsp;<b>Vigencia vencida en Landed Cost.</b><input type="radio" name="AUTORIZA_<%=vobo %>" id="AUTORIZA_<%=vobo %>" value="vigencia_vencida" >
														 <%} %>
				</td></tr>
			<%} %>

  		</table>
  		</div>

  		<div id="formato_anp" style="position: relative; width: 800px; height: 800px; display: <%=(id_oper>=19?"block;":"none;") %>">
			<table width="100%" border="1" bordercolor="#000000" cellspacing="0">

	  			<tr><td>
	  			<table width="100%" border="1" bordercolor="#000000" cellspacing="0" id="anp_table">
	  				<tr><td colspan="11" align="center" bgcolor="#000000"><font color="#FFA500" style="font-size: 22pt; font-family: arial, helvetica, sans-serif;"><b>Alta de Nuevos Productos</b></font></td></tr>
	  				<tr>
	  					<td colspan="11" align="center"><h1><b><input type="checkbox" name="NUEVOS_PRODUCTOS" id="NUEVOS_PRODUCTOS" value="1" checked  <%=(id_oper>19?"disabled":"") %>>NUEVOS PRODUCTOS&nbsp;&nbsp;&nbsp;<input type="checkbox" name="PROYECTO" id="PROYECTO" value="1"  <%=(id_oper>19?"disabled":"") %>>PROYECTO&nbsp;&nbsp;&nbsp;<input type="checkbox" name="CAMBIO_CODIGO" id="CAMBIO_CODIGO" value="1"  <%=(id_oper>19?"disabled":"") %>>CAMBIO DE C�DIGO</b></h1></td>
	  				</tr>

	  				<tr align="center">
	  					<td rowspan="2"><b>Ra&iacute;z</b></td>
	  					<td rowspan="2"><b>N&uacute;mero de stock</b></td>
	  					<td rowspan="2"><b>Descripci&oacute;n</b></td>
	  					<td rowspan="2"><b>Estatus / Vigencia (Solo OF)</b></td>
	  					<td colspan="2"><b>Clasificaci&oacute;n de usuario</b></td>
	  					<td rowspan="2"><b>Unidad de medida (Pz, Jgo)</b></td>
	  					<td rowspan="2"><b>Empaque est&aacute;ndar</b></td>
	  					<td rowspan="2"><b>Subcat. / L&iacute;nea / Subl&iacute;nea</b></td>
	  					<td colspan="2"><b>Precios</b></td>
	  				</tr>
	  				<tr align="center">
	  					<td><b>1<br/>[Familia]</b></td>
	  					<td><b>2<br/>[Marca]</b></td>
	  					<td><b>De lista</b></td>
	  					<td><b>M&iacute;nimo de venta</b></td>
	  				</tr>

	  					<%for(int i=1;i<=anp_rows_table;i++){%>
	  						<tr bgcolor="#f5f5f5">
	  							<td><input type="text" name="raiz_<%=i%>" id="raiz_<%=i%>" size="8" maxlength="8" value="" onblur="javascript:validaNumerico(this)" <%=(id_oper>19?"disabled":"") %>></td>
								<td><input type="text" name="no_stock_<%=i%>" id="no_stock_<%=i%>" size="15" maxlength="15" value=""  <%=(id_oper>19?"disabled":"") %>></td>
								<td><textarea style="font-size: 10pt; font-family: arial, helvetica, sans-serif;" name="descripcion_<%=i%>" id="descripcion_<%=i%>" cols="30" rows="1" <%=(id_oper>19?"readonly":"") %>></textarea></td>
								<td>
									<select name="estatus_vigencia_<%=i%>" id="estatus_vigencia_<%=i%>"  <%=(id_oper>19?"disabled":"")%> onchange="fechaOferta(this,<%=i%>);">
										<option value="-1">seleccionar</option>
										<%if(id_oper>=19){
											for(int j=0;j<lstimx_status.length;j++){ %>
											<option value="<%=lstimx_status[j].getConsecutivo() %>"><%=lstimx_status[j].getValor() %></option>
										<%}
										} %>
									</select>
									Fecha de oferta<br>De&nbsp;<input disabled type="text" name="DPC_fecha_oferta_<%=i%>" id="DPC_fecha_oferta_<%=i%>" value="" readonly="readonly" datepicker_format="DD/MM/YYYY" datepicker="false" datepicker_min="<%=today %>" <%=(id_oper!=19?"disabled":"")%> maxlength="10" size="10"/><br>A&nbsp;&nbsp;<input disabled type="text" name="DPC_fecha_oferta_fin_<%=i%>" id="DPC_fecha_oferta_fin_<%=i%>" value="" readonly="readonly" datepicker_format="DD/MM/YYYY" datepicker="false" datepicker_min="<%=today %>" <%=(id_oper!=19?"disabled":"")%> maxlength="10" size="10"/>
								</td>
								<td><input type="text" name="clasificacion_usu_familia_<%=i%>" id="clasificacion_usu_familia_<%=i%>" size="8" maxlength="3" value="" <%=(id_oper>19?"disabled":"") %>></td>
								<td><input type="text" name="clasificacion_usu_marca_<%=i%>" id="clasificacion_usu_marca_<%=i%>" size="8" maxlength="3" value="" <%=(id_oper>19?"disabled":"") %>></td>
								<td><input type="text" name="unidad_medida_<%=i%>" id="unidad_medida_<%=i%>" size="8" maxlength="2" value="" <%=(id_oper>19?"disabled":"") %>></td>
								<td><input type="text" name="empaque_estandar_<%=i%>" id="empaque_estandar_<%=i%>" size="8" maxlength="5" value="" onblur="javascript:validaNumerico(this)" <%=(id_oper>19?"disabled":"") %>></td>
								<td><textarea style="font-size: 10pt; font-family: arial, helvetica, sans-serif;" name="subcat_linea_sublinea_<%=i%>" id="subcat_linea_sublinea_<%=i%>" cols="10" rows="1" value="" <%=(id_oper>19?"readonly":"") %>></textarea></td>
								<td><textarea style="font-size: 10pt; font-family: arial, helvetica, sans-serif;" name="precio_lista_<%=i%>" id="precio_lista_<%=i%>" cols="10" rows="1" value=""  <%=(id_oper>19?"readonly":"") %>></textarea></td>
								<td><textarea style="font-size: 10pt; font-family: arial, helvetica, sans-serif;" name="precio_minimo_venta_<%=i%>" id="precio_minimo_venta_<%=i%>" cols="10" rows="1" value=""  <%=(id_oper>19?"readonly":"") %>></textarea></td>
							</tr>
	  					<%}%>

				</table>
				</td></tr>
				<%if(objCaso.getCasoOperacion(0).getObservacion() != null && !"".equals(objCaso.getCasoOperacion(0).getObservacion())){ %>
					<tr><td align="center" bgColor="#696969"><font color="#FFA500" size="3" face="arial, helvetica, sans-serif"><b>Observaciones</b></font></td></tr>

    			<tr><td align="left" bgcolor="#f5f5f5"><%=(objCaso.getCasoOperacion(0).getObservacion() == null) ? "" : objCaso.getCasoOperacion(0).getObservacion()%></td></tr>
 				<%} %>

				<tr><td>
	  			<table  width="100%" border="1" bordercolor="#000000" cellspacing="0">

	  				<%if(id_oper==20){ %>
						<tr><td  align="center" bgColor="#696969"><font color="#FFA500" size="3" face="arial, helvetica, sans-serif"><b>ENTERADO</b></font></td></tr>
						<tr><td  align="center" >Si<input type="checkbox" name="ENTERADO_OPERACIONES" id="ENTERADO_OPERACIONES" value="Si"></td></tr>
	  				<%} %>
	  				<%if(id_oper==21){ %>
						<tr><td  align="center" bgColor="#696969"><font color="#FFA500" size="3" face="arial, helvetica, sans-serif"><b>Autorizaci&oacute;n</b></font></td></tr>
						<tr><td  align="center" >Autoriza:&nbsp;&nbsp;&nbsp;Si<input type="radio" name="AUTORIZA_CADENA_SUM" id="AUTORIZA_CADENA_SUM" value="Si">
														  &nbsp;&nbsp;&nbsp;No<input type="radio" name="AUTORIZA_CADENA_SUM" id="AUTORIZA_CADENA_SUM" value="No">
							</td>
						</tr>
	  				<%} %>
	  				<%if(id_oper==22){ %>
						<tr><td  align="center" bgColor="#696969"><font color="#FFA500" size="3" face="arial, helvetica, sans-serif"><b>ENTERADO</b></font></td></tr>
						<tr><td  align="center" >�El producto ha sido registrado en PRMS?&nbsp;Si<input type="checkbox" name="ENTERADO_INGENIERIA_INDUSTRIAL" id="ENTERADO_INGENIERIA_INDUSTRIAL" value="Si"></td></tr>
	  				<%} %>

	  				<%if(id_oper==23){ %>
						<tr><td  align="center" bgColor="#696969"><font color="#FFA500" size="3" face="arial, helvetica, sans-serif"><b>Terminar caso y notificar al Gerente de Categor&iacute;a</b></font></td></tr>
						<tr><td  align="center" >�El producto ya tiene costo en PRMS?&nbsp;&nbsp;&nbsp;Si<input type="radio" name="AUTORIZA_COSTO_EN_PRMS" id="AUTORIZA_COSTO_EN_PRMS" value="Si">
																					&nbsp;&nbsp;&nbsp;No<input type="radio" name="AUTORIZA_COSTO_EN_PRMS" id="AUTORIZA_COSTO_EN_PRMS" value="No">
							</td>
						</tr>
	  				<%} %>
	  			</table>
	  			</td></tr>
	  		</table>
	  		<%if(id_oper==19){ %>
	  			<input type="button" name="Agregar" id="Agregar" value="Agregar filas" onclick="javascript:appendRow('anp_table');"  <%=(id_oper>19?"disabled":"") %>>
	  		<%} %>
  		</div>
	</form>
  </body>
</html>
