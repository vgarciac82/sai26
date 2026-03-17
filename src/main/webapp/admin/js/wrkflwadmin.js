	var gRst=null;
	var gRegistrosPorPagina=7;
	var gPagina=1;
	var a_tags = null; // Se utiliza en la funcin, grdDatoRenglon_onclick(ren,col)
	var param_array = null;
	var opc_tab=-1;
	var nom_catalogo = '';
	function opcTab(i){
		var divLst  = document.getElementById('divListaDatos');
		
		divLst.style.display='block';
		opc_tab = i;
		switch (i){
			case 0: // Usuarios
				nom_catalogo = 'USUARIOS';
				ctrlVisible(true, false, false, false,false,false,false,false,false);
				//alert("Cambiar!");
				//a_tags = new Array("txtIdUsuario", "txtPwd", "txtNombre", "txtDesc");
				a_tags = new Array("txtIdUsuario", "txtPwd", "txtSaludo", "txtAPaterno", "txtAMaterno", "txtNombres", "txtDirigidoA", "txtCargo");
				param_array = new Array("u_login", "u_password", "salutacion", "ce_ap_paterno", "ce_ap_materno", "ce_nombre_completo", "id_area", "cargo");
				//alert("Cambiar!");
				activaBotones(false, true, true);
				break;
			case 1: // Usuarios-Grupos
				nom_catalogo = 'USUARIOS-GRUPOS';
				ctrlVisible(false, true, false, false,false,false,false,false,false);
				a_tags = new Array("txtPersona3", "txtGrupoUsuario");				
				param_array = new Array("u_login", "g_nombre");
				break;
			case 2: // Propiedades
				nom_catalogo = 'PROPIEDADES';
				ctrlVisible(false, false, true, false,false,false,false,false,false);
				a_tags = new Array("txtIdUsuarioProp", "txtNombreProp", "txtValorProp");
				param_array = new Array("u_login", "up_nombre", "up_valor");
				break;
			case 3: // Role
				nom_catalogo = 'ROLE';
				ctrlVisible(false, false, false, true,false,false,false,false,false);
				a_tags = new Array("txtIdUsuarioRole", "txtNombreRole");
				param_array = new Array("u_login", "r_nombre");
				break;
			case 4: // MTTO. GRUPOS
				nom_catalogo = 'MTTO. GRUPOS';
				ctrlVisible(false, false, false,false,true,false,false,false,false);
				a_tags = new Array("txtNombreGrupo", "txtDescripcionGrupo");
				param_array = new Array("g_nombre", "g_descripcion");
				break;	
			case 5: // MTTO. ROLES
				nom_catalogo = 'MTTO. ROLES';
				ctrlVisible(false, false, false,false,false,true,false,false,false);
				a_tags = new Array("txtMNombreRole", "txtDescripcionRole");
				param_array = new Array("r_nombre", "r_descripcion");
				break;
			case 6: // MTTO. GRUPOS PROP
				nom_catalogo = 'MTTO. GRUPOS PROP';
				ctrlVisible(false, false, false,false,false,false,true,false,false);
				a_tags = new Array("txtNombreGrupoPropG", "txtNombrePropiedadPropG","txtValorPropiedadGrupoPropG");
				param_array = new Array("g_nombre", "gp_nombre" ,"gp_valor");
				break;
			case 7: // AREAS
				nom_catalogo = 'AREAS';
				ctrlVisible(false, false, false,false,false,false,false,true, false);
				a_tags = new Array("txtMClaveArea","txtMDescripcionArea","txtTipoArea","txtMPrefijoFolio","txtAreaPadre","txtBandejaCompartidaEntrada","txtBandejaCompartidaSalida");
				param_array = new Array("id_area", "d_descripcion" ,"tipo_area", "prefijo_folio", "id_area_padre", "bandeja_compartida_in", "bandeja_compartida_out");
				break;
			case 8: // USUARIO_COPY_MOVE
				nom_catalogo = 'USUARIO_COPY_MOVE';
				ctrlVisible(false, false, false,false,false,false,false,false,true);
				a_tags = new Array("txtUCMFrom","txtUCMTo","chkUCMInfo");
				param_array = new Array("txtUCMFrom", "txtUCMTo" ,"chkUCMInfo");
				/*
				if (document.getElementById('txtIdUsuario').value =="")
				{
					alert("Seleccione un usuario antes");
					opc_tab = 0;
					nom_catalogo = 'USUARIOS';
					ctrlVisible(true, false, false, false,false,false,false,false,false);
					a_tags = new Array("txtIdUsuario", "txtPwd", "txtSaludo", "txtAPaterno", "txtAMaterno", "txtNombres", "txtDirigidoA", "txtCargo");
					param_array = new Array("u_login", "u_password", "salutacion", "ce_ap_paterno", "ce_ap_materno", "ce_nombre_completo", "id_area", "cargo");
					activaBotones(false, true, true);
				}
				else
				{
					document.getElementById('txtUCMFrom').value = document.getElementById('txtIdUsuario').value;
					activaBotones(false, true, true);
				}
				*/
				
				break;
		}
		getCatalogoAdmin(i);
	}
	
	function ctrlVisible(Usr, Grp, Prop, Role, MGrp, MRole, MGrpProp, Areas, UCopyMove){
		var divUsr  = document.getElementById('divCapturaUsr');
		var divGrp  = document.getElementById('divCapturaUsrGrp');
		var divProp = document.getElementById('divCapturaProp');
		var divRole = document.getElementById('divCapturaRole');
		var divMGrp = document.getElementById('divCapturaMttoGrp');
		var divMRole = document.getElementById('divCapturaMttoRole');
		var divMGrpProp = document.getElementById('divCapturaMttoGrpProp');
		var divAreas = document.getElementById('divCapturaAreas');
		var divUCM = document.getElementById('divUCopyMove');
		var divDataList = document.getElementById('divListaDatos');
		
		divUsr.style.display = (Usr)? 'block': 'none';
		//divUsr.style.top = "10px";
		divGrp.style.display = (Grp)? 'block': 'none';	
		divProp.style.display = (Prop)? 'block': 'none';
		divRole.style.display = (Role)? 'block': 'none';
		divMGrp.style.display = (MGrp)? 'block': 'none';
		divMRole.style.display = (MRole)? 'block': 'none';
		divMGrpProp.style.display = (MGrpProp)? 'block': 'none';
		divAreas.style.display = (Areas)? 'block': 'none';
		divUCM.style.display = (UCopyMove)? 'block': 'none';
		divDataList.style.display = (!UCopyMove)? 'block': 'none';
	}
	
	function ctrlReadOnly(opc_accion){
		switch (opc_tab){
			case 0: // Usuarios
				//alert("Cambiar!");				
				var resetPwd	= document.getElementById("chkResetPwd");
				var id 			= document.getElementById("txtIdUsuario");
				var pwd 		= document.getElementById("txtPwd");
				var saludo		= document.getElementById("txtSaludo");
				var apat 		= document.getElementById("txtAPaterno");
				var amat 		= document.getElementById("txtAMaterno");
				var noms 		= document.getElementById("txtNombres");
				var area 		= document.getElementById("txtDirigidoA");
				var cargo 		= document.getElementById("txtCargo");
				
				var msgId  		= document.getElementById("divIdUsuario");
				var msgPwd 		= document.getElementById("divPwd");
				var saludo		= document.getElementById("divSaludo");
				var msgApat  	= document.getElementById("divAPaterno");
				var msgAmat  	= document.getElementById("divAMaterno");
				var msgNoms  	= document.getElementById("divNombres");
				var msgArea  	= document.getElementById("divDirigidoA");
				var msgCargo  	= document.getElementById("divCargo");
				
				//alert("Cambiar!");				
				switch (opc_accion){
					case 0:
						id.readOnly = "";
						pwd.readOnly = "";
						saludo.readOnly = "";
						apat.readOnly = "";
						amat.readOnly = "";
						noms.readOnly = "";
						area.readOnly = "";
						cargo.readOnly = "";
						
						msgId.innerHTML = "<font color='red'>Editable</font>";
						msgPwd.innerHTML = "<font color='red'>Editable</font>";
						msgSaludo.innerHTML = "<font color='red'>Editable</font>";
						msgApat.innerHTML = "<font color='red'>Editable</font>";;
						msgAmat.innerHTML = "<font color='red'>Editable</font>";;
						msgNoms.innerHTML = "<font color='red'>Editable</font>";;
						msgArea.innerHTML = "<font color='red'>Editable</font>";;
						msgCargo.innerHTML = "<font color='red'>Editable</font>";
						
						id.value = "";
						pwd.value = "";
						saludo.value = "";
						apat.value = "";
						amat.value = "";
						noms.value = "";
						area.value = "";
						cargo.value = "";
						
						resetPwd.checked = false;
					break;
					default:
						if(!resetPwd.checked){
							id.readOnly = "readOnly";
							pwd.readOnly = "";
							saludo.readOnly = "";
							apat.readOnly = "";
							amat.readOnly = "";
							noms.readOnly = "";
							area.readOnly = "";
							cargo.readOnly = "";

							msgId.innerHTML = "<font color='red'>Solo lectura</font>";
							msgPwd.innerHTML = "<font color='red'>Editable</font>";
							msgSaludo.innerHTML = "<font color='red'>Editable</font>";
							msgApat.innerHTML = "<font color='red'>Editable</font>";;
							msgAmat.innerHTML = "<font color='red'>Editable</font>";;
							msgNoms.innerHTML = "<font color='red'>Editable</font>";;
							msgArea.innerHTML = "<font color='red'>Editable</font>";;
							msgCargo.innerHTML = "<font color='red'>Editable</font>";
							
						} else {
							pwd.value = "";
							pwd.readOnly = "";
							id.readOnly = "readOnly";
							saludo.readOnly = "readOnly";
							apat.readOnly = "readOnly";
							amat.readOnly = "readOnly";
							noms.readOnly = "readOnly";
							area.value = "readOnly";
							cargo.value = "readOnly";
							
							msgId.innerHTML = "<font color='red'>Solo lectura</font>";
							msgPwd.innerHTML = "<font color='red'>Editable</font>";
							msgSaludo.innerHTML = "<font color='red'>Solo lectura</font>";
							msgApat.innerHTML = "<font color='red'>Solo lectura</font>";;
							msgAmat.innerHTML = "<font color='red'>Solo lectura</font>";;
							msgNoms.innerHTML = "<font color='red'>Solo lectura</font>";;
							msgArea.innerHTML = "<font color='red'>Solo lectura</font>";;
							msgCargo.innerHTML = "<font color='red'>Solo lectura</font>";

							activaBotones(true, false, true);
							/*
							id.value = "";
						    'pwd.value = "";
						    'nom.value = "";
						    'desc.value = "";
						    */
						}
					break;
				}
				break;
			case 1: // Grupos
				var id = document.getElementById("txtIdUsuarioGrp");
				
				break;
			case 2: // Propiedades
				var id = document.getElementById("txtIdUsuarioProp");
				
				break;
			case 3: // Role
				var id = document.getElementById("txtIdUsuarioRole");
				break;
			case 4: // MTTO. GRUPOS
				var id = document.getElementById("txtNombreGrupo");
				break;
			case 5: // MTTO. ROLES
				var id = document.getElementById("txtMNombreRole");
				break;
			case 6: // MTTO. GRUPOS PROP
				var id = document.getElementById("txtNombreGrupoPropG");
				break;					
			case 7: // AREAS
				var id = document.getElementById("txtMClaveArea");
				break;
		}
	}
	
	function resetContrasena(){
		ctrlReadOnly(1)
	}
	
	function getCatalogoAdmin(opc)
	{
   	
	    var sql = "";
	    var where = "";
	    
	    if(opc == 0){
			//alert("Cambiar!");				
		    sql += "Select u_login, u_password, salutacion, ce_ap_paterno, ce_ap_materno, ce_nombre_completo, id_area, cargo "
		    sql += "From  cg_usuario, cg_cat_empleado "
		    sql += "where cg_cat_empleado.ce_os_responsable = cg_usuario.u_login";
			//alert("Cambiar!");				
	    } else if(opc == 1){
	    	sql += "Select ";
		    sql += " u_login, g_nombre ";
		    sql += "From  cg_usuario_grupo ";
	    } else if(opc == 2){
	    	sql += "Select ";
		    sql += " u_login, up_nombre, up_valor ";
		    sql += "From  cg_usuario_propiedades ";
	    } else if(opc == 3){
		    sql += "Select ";
		    sql += " u_login, r_nombre ";
		    sql += "From  cg_usuario_role ";
	    }else if(opc == 4){
		    sql += "Select ";
		    sql += " g_nombre, g_descripcion ";
		    sql += "From  CG_GRUPO ";
	    }else if(opc == 5){
		    sql += "Select ";
		    sql += " r_nombre, r_descripcion ";
		    sql += "From  CG_ROLE ";
	    }else if(opc == 6){
		    sql += "Select ";
		    sql += " g_nombre, gp_nombre, gp_valor ";
		    sql += "From CG_GRUPO_PROPIEDADES ";
	    }else if(opc == 7){
		    sql += "Select id_area, d_descripcion, tipo_area, ";
		    sql += "prefijo_folio, id_area_padre, ";
		    sql += "bandeja_compartida_in, bandeja_compartida_out ";
		    sql += "From CG_CAT_AREAS ";
	    }else if(opc == 8){
	    	return;
	    }
	    
	    for (i=0; i<a_tags.length; i++) {
	    	var valor 	= document.getElementById(a_tags[i]).value;
	    	var columna = param_array[i];
	    	
	    	if(valor!="") {
	    		if (where.length>0) {
	    			where += " and ";
	    		}
	    		where += columna;
	    		if (valor.indexOf("%")>-1) {
	    			where += " like ";
	    		} else {
	    			where += " = ";
	    		} 
	    		where += "'" + escape(valor) +"' ";
	    	}
	    }
	    
	    if (where.length>0) {
	    	if(sql.toLowerCase().indexOf("where")<0) {
	    		where = " where " + where;
	    	} else {
	    		where = " and " + where;
	    	}
	    }
	    //alert("total=["+sql+where+"]");
	    	    
	  	var out = document.getElementById('tableContainer');				                
		out.innerHTML = '<span class=\'Estilo8\'>Buscando información de acuerdo a los criterios</span>';
				
		execQuery('json', sql+where);
	}
	
	function execQuery(rType, sql) {
		//alert("execQuery(rType, sql)=["+sql+"]");
		var query = new JSQuery(onServerResponseCatalogos, onServerResponseError, rType);
		query.execute(sql);
	}

	function consultarFiltro(){
		var o_sel = document.getElementById("tc_Caso");
		var o_selGrpo = document.getElementById("g_grupos");
		var o_selOper = document.getElementById("id_oper");
		detalleRegistros(true);
		getUserList(o_sel.value, o_selGrpo.value, o_selOper.value);
	}
	
	function obtenOperacionesPorTipoCaso(){
		var o_sel = document.getElementById("tc_Caso");
		var o_selGrpo = document.getElementById("g_grupos");
		getOperacionesPorTipoCaso(o_sel.value, o_selGrpo.value);
	}	

	function agregaLista(selectId, index, text, value){
		var oSel = document.getElementById(selectId);
		var opcion = new Option(text, value);   
        oSel.options[index] = opcion;
        oSel.selectedIndex = 0;   
	}
	
	function borraLista(selectId){
		var oSel = document.getElementById(selectId);
        oSel.length = 0;   
	}

	function onServerResponseCatalogos(resultSet) {
		setRst(resultSet);
	    if (getRows().length>=1){
	        detalleRegistros(false);
			pagina_actual();
		} else {
			var out = document.getElementById('tableContainer');
	       //alert('No existe informacin.\n\nVerifique el numero de cliente o \n capture la informacion');
	       out.innerHTML = "<span class=\'Estilo8\'>No hay información para mostrar</span>";
		}  
	}
	
	//Grid
	function setRst(rst){
		gRst = rst;
	}
	
	function getEncabezados(){
		return gRst.column;
	}
	
	function getRows(){
		var rows = gRst.row;
		return rows;
	}
	
	function detalleRegistros(ini){
		var o_totReg = document.getElementById("totalRegistros");
		var o_tot_Pag = document.getElementById("totalPaginas");
		    
		if(ini){
			o_totReg.innerHTML = ' 0';
	    	o_tot_Pag.innerHTML = '&nbsp;|&nbsp;<strong>Total de Páginas&nbsp;:</strong>&nbsp;0&nbsp;|';
		} else { 
	    	o_totReg.innerHTML = ' ' + getRows().length;
	    	o_tot_Pag.innerHTML = '&nbsp;|&nbsp;<strong>Total de Páginas&nbsp;:</strong>&nbsp;' + (getTotalPaginas()) + '&nbsp;|';
	    }
	}
	
	function pagina_actual(){
		var strHTML = '';
		var out = document.getElementById('tableContainer');				                
		var ini = (getPagina() - 1) * getRegistrosPorPagina();
        var fin = (getPagina()) * getRegistrosPorPagina();
        fin = ((fin > getRows().length)? getRows().length : fin);
  		
  		detalleRegistros(false);
        setBarraPaginaActual();
        out.innerHTML = strHTML;
		if (getRows().length >= 1){    	
	    	
	        strHTML+='<table width=\'\' border=\'0\' cellpadding=\'0\' cellspacing=\'0\' class=\'scrollTable\'><TR>';	
			
			var header = getEncabezados();
			/*
			for (var jj = 0; jj < header.length; jj++)
			{
				strHTML+='	<TH bgcolor=\'DDDDDD\' ALIGN=\'center\' width=\'20%\'><font face=\'Verdana\' size=\'-1\'><b> [' + header[jj] + ']	</b></font></TH>';
			}
			*/
			
		switch (opc_tab){
			case 0://Usuarios
			strHTML+='	<thead class=\'fixedHeader\' id=\'fixedHeader\'>';
			strHTML+='	<TH bgcolor=\'DDDDDD\' ALIGN=\'center\' width=\'10%\'><font face=\'Verdana\' size=\'-1\'><b>ID_USUARIO</b></font></TH>';
			strHTML+='	<TH bgcolor=\'DDDDDD\' ALIGN=\'center\' width=\'10%\'><font face=\'Verdana\' size=\'-1\'><b>CONTRASEÑA</b></font></TH>'; 
			strHTML+='	<TH bgcolor=\'DDDDDD\' ALIGN=\'center\' width=\'10%\'><font face=\'Verdana\' size=\'-1\'><b>SALUDO</b></font></TH>';				
			strHTML+='	<TH bgcolor=\'DDDDDD\' ALIGN=\'center\' width=\'10%\'><font face=\'Verdana\' size=\'-1\'><b>AP. PATERNO</b></font></TH>';
			strHTML+='	<TH bgcolor=\'DDDDDD\' ALIGN=\'center\' width=\'10%\'><font face=\'Verdana\' size=\'-1\'><b>AP. MATERNO</b></font></TH>';
			strHTML+='	<TH bgcolor=\'DDDDDD\' ALIGN=\'center\' width=\'10%\'><font face=\'Verdana\' size=\'-1\'><b>NOMBRES</b></font></TH>'; 
			strHTML+='	<TH bgcolor=\'DDDDDD\' ALIGN=\'center\' width=\'10%\'><font face=\'Verdana\' size=\'-1\'><b>CVE. AREA</b></font></TH>';				
			strHTML+='	<TH bgcolor=\'DDDDDD\' ALIGN=\'center\' width=\'10%\'><font face=\'Verdana\' size=\'-1\'><b>CARGO</b></font></TH>';
			break;
			case 1: // Grupos
			strHTML+='	<thead class=\'fixedHeader\' id=\'fixedHeader\'>';
			strHTML+='	<TH bgcolor=\'DDDDDD\' ALIGN=\'center\' width=\'20%\'><font face=\'Verdana\' size=\'-1\'><b> ID_USUARIO </b></font></TH>';
			strHTML+='	<TH bgcolor=\'DDDDDD\' ALIGN=\'center\' width=\'20%\'><font face=\'Verdana\' size=\'-1\'><b> NOMBRE </b></font></TH>'; 
			break;
			case 2: // Propiedades
			strHTML+='	<thead class=\'fixedHeader\' id=\'fixedHeader\'>';
			strHTML+='	<TH bgcolor=\'DDDDDD\' ALIGN=\'center\' width=\'20%\'><font face=\'Verdana\' size=\'-1\'><b> ID_USUARIO </b></font></TH>';
			strHTML+='	<TH bgcolor=\'DDDDDD\' ALIGN=\'center\' width=\'20%\'><font face=\'Verdana\' size=\'-1\'><b> NOMBRE </b></font></TH>'; 
			strHTML+='	<TH bgcolor=\'DDDDDD\' ALIGN=\'center\' width=\'20%\'><font face=\'Verdana\' size=\'-1\'><b> VALOR </b></font></TH>';
			break;
			case 3: // Roles
			strHTML+='	<thead class=\'fixedHeader\' id=\'fixedHeader\'>';
			strHTML+='	<TH bgcolor=\'DDDDDD\' ALIGN=\'center\' width=\'20%\'><font face=\'Verdana\' size=\'-1\'><b> ID_USUARIO </b></font></TH>';
			strHTML+='	<TH bgcolor=\'DDDDDD\' ALIGN=\'center\' width=\'20%\'><font face=\'Verdana\' size=\'-1\'><b> NOMBRE </b></font></TH>'; 
			break;
			case 4: // MTTO. GRUPOS
			strHTML+='	<thead class=\'fixedHeader\' id=\'fixedHeader\'>';
			strHTML+='	<TH bgcolor=\'DDDDDD\' ALIGN=\'center\' width=\'20%\'><font face=\'Verdana\' size=\'-1\'><b> ID_GRUPO </b></font></TH>';
			strHTML+='	<TH bgcolor=\'DDDDDD\' ALIGN=\'center\' width=\'20%\'><font face=\'Verdana\' size=\'-1\'><b> DESCRIPCION </b></font></TH>'; 
			break;
			case 5: // MTTO. ROLES
			strHTML+='	<thead class=\'fixedHeader\' id=\'fixedHeader\'>';
			strHTML+='	<TH bgcolor=\'DDDDDD\' ALIGN=\'center\' width=\'20%\'><font face=\'Verdana\' size=\'-1\'><b> ID_ROLE </b></font></TH>';
			strHTML+='	<TH bgcolor=\'DDDDDD\' ALIGN=\'center\' width=\'20%\'><font face=\'Verdana\' size=\'-1\'><b> DESCRIPCION </b></font></TH>'; 
			break;
			case 6: // MTTO. GRUPOS PROP
			strHTML+='	<thead class=\'fixedHeader\' id=\'fixedHeader\'>';
			strHTML+='	<TH bgcolor=\'DDDDDD\' ALIGN=\'center\' width=\'20%\'><font face=\'Verdana\' size=\'-1\'><b> ID_GRUPO </b></font></TH>';
			strHTML+='	<TH bgcolor=\'DDDDDD\' ALIGN=\'center\' width=\'20%\'><font face=\'Verdana\' size=\'-1\'><b> ID_PROPIEDAD </b></font></TH>';
			strHTML+='	<TH bgcolor=\'DDDDDD\' ALIGN=\'center\' width=\'20%\'><font face=\'Verdana\' size=\'-1\'><b> VALOR </b></font></TH>'; 
			break;
			case 7: // AREAS
			strHTML+='	<thead class=\'fixedHeader\' id=\'fixedHeader\'>';
			strHTML+='	<TH bgcolor=\'DDDDDD\' ALIGN=\'center\' width=\'20%\'><font face=\'Verdana\' size=\'-1\'><b> ID_AREA </b></font></TH>';
			strHTML+='	<TH bgcolor=\'DDDDDD\' ALIGN=\'center\' width=\'20%\'><font face=\'Verdana\' size=\'-1\'><b> DESCRIPCION </b></font></TH>';
			strHTML+='	<TH bgcolor=\'DDDDDD\' ALIGN=\'center\' width=\'20%\'><font face=\'Verdana\' size=\'-1\'><b> TIPO AREA </b></font></TH>'; 
			strHTML+='	<TH bgcolor=\'DDDDDD\' ALIGN=\'center\' width=\'20%\'><font face=\'Verdana\' size=\'-1\'><b> PREFIJO FOLIO </b></font></TH>';
			strHTML+='	<TH bgcolor=\'DDDDDD\' ALIGN=\'center\' width=\'20%\'><font face=\'Verdana\' size=\'-1\'><b> AREA PADRE </b></font></TH>';
			strHTML+='	<TH bgcolor=\'DDDDDD\' ALIGN=\'center\' width=\'20%\'><font face=\'Verdana\' size=\'-1\'><b> BANDEJA COMPARTIDA ENTRADA </b></font></TH>'; 
			strHTML+='	<TH bgcolor=\'DDDDDD\' ALIGN=\'center\' width=\'20%\'><font face=\'Verdana\' size=\'-1\'><b> BANDEJA COMPARTIDA SALIDA </b></font></TH>'; 
			break;
			
		}	
		
/*			
			strHTML+='	<TH bgcolor=\'DDDDDD\' ALIGN=\'center\' width=\'20%\'><font face=\'Verdana\' size=\'-1\'><b> CL_READ<br>	</b></font></TH>';
			strHTML+='	<TH bgcolor=\'DDDDDD\' ALIGN=\'center\' width=\'20%\'><font face=\'Verdana\' size=\'-1\'><b> CL_UPDATE<br>	</b></font></TH>';
			strHTML+='	<TH bgcolor=\'DDDDDD\' ALIGN=\'center\' width=\'20%\'><font face=\'Verdana\' size=\'-1\'><b> CL_DELETE<br>	</b></font></TH>';
			strHTML+='	<TH bgcolor=\'DDDDDD\' ALIGN=\'center\' width=\'20%\'><font face=\'Verdana\' size=\'-1\'><b> CL_REQUERIDO<br>	</b></font></TH>';
			strHTML+='	<TH bgcolor=\'DDDDDD\' ALIGN=\'center\' width=\'20%\'><font face=\'Verdana\' size=\'-1\'><b> CL_MOSTRAR<br>	</b></font></TH>';
			
			strHTML+='	<TH bgcolor=\'DDDDDD\' ALIGN=\'center\' width=\'20%\'><font face=\'Verdana\' size=\'-1\'><b> CL_DESC_LABEL<br>	</b></font></TH>';
			strHTML+='	<TH bgcolor=\'DDDDDD\' ALIGN=\'center\' width=\'20%\'><font face=\'Verdana\' size=\'-1\'><b> CL_DESC_SNIPPET_HTML<br>	</b></font></TH>';
			
			strHTML+='	<TH bgcolor=\'DDDDDD\' ALIGN=\'center\' width=\'20%\'><font face=\'Verdana\' size=\'-1\'><b> CL_VENCIMIENTO_LABEL<br>	</b></font></TH>';
			strHTML+='	<TH bgcolor=\'DDDDDD\' ALIGN=\'center\' width=\'20%\'><font face=\'Verdana\' size=\'-1\'><b> CL_VENCIMIENTO_SNIPPET_HTML<br>	</b></font></TH>';
			
			strHTML+='	<TH bgcolor=\'DDDDDD\' ALIGN=\'center\' width=\'20%\'><font face=\'Verdana\' size=\'-1\'><b> CL_TITULO_LABEL<br>	</b></font></TH>';
			strHTML+='	<TH bgcolor=\'DDDDDD\' ALIGN=\'center\' width=\'20%\'><font face=\'Verdana\' size=\'-1\'><b> CL_TITULO_SNIPPET_HTML<br>	</b></font></TH>';
			
			strHTML+='	<TH bgcolor=\'DDDDDD\' ALIGN=\'center\' width=\'20%\'><font face=\'Verdana\' size=\'-1\'><b> CL_AUTOR_LABEL<br>	</b></font></TH>';
			strHTML+='	<TH bgcolor=\'DDDDDD\' ALIGN=\'center\' width=\'20%\'><font face=\'Verdana\' size=\'-1\'><b> CL_AUTOR_SNIPPET_HTML<br>	</b></font></TH>';
			
			strHTML+='	<TH bgcolor=\'DDDDDD\' ALIGN=\'center\' width=\'20%\'><font face=\'Verdana\' size=\'-1\'><b> CL_MATERIA_LABEL<br>	</b></font></TH>';
			strHTML+='	<TH bgcolor=\'DDDDDD\' ALIGN=\'center\' width=\'20%\'><font face=\'Verdana\' size=\'-1\'><b> CL_MATERIA_SNIPPET_HTML<br>	</b></font></TH>';
			
			strHTML+='	<TH bgcolor=\'DDDDDD\' ALIGN=\'center\' width=\'20%\'><font face=\'Verdana\' size=\'-1\'><b> CL_DESC_REQUIRED<br>	</b></font></TH>';
			strHTML+='	<TH bgcolor=\'DDDDDD\' ALIGN=\'center\' width=\'20%\'><font face=\'Verdana\' size=\'-1\'><b> CL_VENCIMIENTO_REQUIRED<br>	</b></font></TH>';
			strHTML+='	<TH bgcolor=\'DDDDDD\' ALIGN=\'center\' width=\'20%\'><font face=\'Verdana\' size=\'-1\'><b> CL_TITULO_REQUIRED<br>	</b></font></TH>';
			strHTML+='	<TH bgcolor=\'DDDDDD\' ALIGN=\'center\' width=\'20%\'><font face=\'Verdana\' size=\'-1\'><b> CL_AUTOR_REQUIRED<br>	</b></font></TH>';
			strHTML+='	<TH bgcolor=\'DDDDDD\' ALIGN=\'center\' width=\'20%\'><font face=\'Verdana\' size=\'-1\'><b> CL_MATERIA_REQUIRED<br>	</b></font></TH>';
			*/
			
			/*
			strHTML+='	<TH bgcolor=\'DDDDDD\' ALIGN=\'center\' width=\'20%\'><font face=\'Verdana\' size=\'-1\'><b> CL_AUTOR_REQUIRED<br>	</b></font></TH>';
			strHTML+='	<TH bgcolor=\'DDDDDD\' ALIGN=\'center\' width=\'20%\'><font face=\'Verdana\' size=\'-1\'><b> CL_MATERIA_REQUIRED<br>	</b></font></TH>';
			*/
			strHTML+='	<TH bgcolor=\'FFFFFF\' ALIGN=\'center\' width=\'5%\'>&nbsp;</TH></thead></TR>';
	       	//Aqui se utiliza param_array 
	       	//for (var i = 0; (i < rows.length); i++)
	       	for (var i = ini; (i < fin); i++)
		    {
		    	var params='';
		        strHTML+='<tbody class=\'scrollContent\'><tr>';
		        
		        for (var j = 0; j < getEncabezados().length; j++){
		        	
                    strHTML+='<td bgcolor=\'EEEEEE\' align=\'center\' width=\'' + getAnchoCol(j) + '\' nowrap><span class=\'Estilo6\'>';
                    strHTML+= "<a href=\"javascript:grdDatoRenglon_onclick(" + i + "," + j + ")\">" + eval("getRows()[" + i + "]." + getEncabezados()[j]) + "</a>";
                    strHTML+='</span></td>';
					params+= ((j>=0)? '&':'') + a_tags[j] + "=" + eval("getRows()[" + i + "]." + getEncabezados()[j]);
                }//for j
                
                strHTML+= '<td bgcolor=\'FFFFFF\' align=\'left\'><a href="javascript:requestBorraRegistro(\'' + params + '\')"><img SRC=\'../images/minus.gif\' BORDER=\'0\'/></a></td>';
                strHTML+= '</tr>';
            }
            
            strHTML+='</tbody>';
	        strHTML+='</table>';
	        
		    out.innerHTML = strHTML;
	    } else {
            //alert('No existe informacián.\n\nVerifique el numero de cliente o \n capture la informacion');
            var out = document.getElementById('tableContainer');
            out.innerHTML = '<span class=\'Estilo8\'>No hay informacián para mostrar</span>'
	    }//if
	}

	function getTotalPaginas(){
        return Math.ceil((getRows().length / getRegistrosPorPagina()));
    }
    function getPagina(){
        return gPagina;
    }
    function setBarraPaginaActual(){
    	var barrPagActual = document.getElementById("numPagActual");
    	barrPagActual.innerHTML = "&nbsp;&nbsp;<strong>Página Actual:</strong>&nbsp;&nbsp;&nbsp;" + getPagina();
    }
    function setRegistrosPorPagina(regPorPag){
        gRegistrosPorPagina = regPorPag;
    }
    function getRegistrosPorPagina(){
        return gRegistrosPorPagina;
    }
    function getSiguientePagina() {
        if(gPagina < getTotalPaginas())
	        gPagina++;
	    var out = document.getElementById('tableContainer');
	    out.innerHTML = "";
		pagina_actual();
    }
    function getAnteriorPagina() {
		if(gPagina>1)
	        gPagina--;
		pagina_actual();
    }

	function getPrimeraPagina(){
		gPagina = 1;
		pagina_actual();
	}
    function getUltimaPagina(){
    	gPagina = getTotalPaginas();
    	pagina_actual();
    }
	/*var a_tags = new Array("txtIdArea", "txtDesc", 
						   "tipo_area");
		*/				   
 /*
 " 0 hidIdCaso"," 1 hidIdOper"," 2 txtIdArea", " 3 txtDesc", 
 " 4 tipo_area"
 */
	
	function grdDatoRenglon_onclick(ren,col){
		//alert("Renglon : " + ren + " Columna : " + col);
		var out = document.getElementById('tableContainer');
		var strValue = '';
		opcTab(opc_tab);
		if (getRows().length >= 1){
			
			//for (var i = 0; (i < ren); i++){
				for (var j = 0; (j < getEncabezados().length); j++){
					var o_tag = document.getElementById(a_tags[j]);
					
					strValue = eval("getRows()[" + ren + "]." + getEncabezados()[j]);
					if (a_tags[j] == 'txtPwd'){
						//strValue = Convertir(strValue);	
					}
					if(a_tags[j].indexOf("txt") > -1){
						o_tag.value = strValue;
					} else if (a_tags[j].indexOf("chk") > -1){
						//alert (a_tags[j]);
						o_tag.checked = (strValue == 'N')? false: true;
					} else if (a_tags[j].indexOf("hid") > -1){
						o_tag.value = strValue;
					}
				}
			//}
			activaBotones(true, false, false);
			ctrlReadOnly(1);
		} else {
            out.innerHTML = '<span class=\'Estilo8\'>No hay informacion para mostrar</span>'
	    }
	}
	// Fin Grid


	function activaBotones(b_agrega, b_actualiza, b_elimina){
		
		
		
		var o_botonA1 = document.getElementById("cmdAgrega1");
		var o_botonU1 = document.getElementById("cmdActualiza1");
		var o_botonE1 = document.getElementById("cmdElimina1");
		var resetPwd = document.getElementById("chkResetPwd");
		
		resetPwd.disabled = (   (!b_agrega)? true: false);
		o_botonA1.disabled = (   (b_agrega)? true: false);
		o_botonU1.disabled = ((b_actualiza || opc_tab==1 || opc_tab==3)? true: false);
		o_botonE1.disabled = (  (b_elimina)? true: false);
		
		document.getElementById("cmdCancelar").disabled = ((opc_tab==8)? true : false );	
	}
	
	function accion(tipo_accion){
		
		switch(tipo_accion){
			case 0: //add
				if(confirm("Desea registrar los datos?")) {
					activaBotones(false, true, true);
					requestFichaCaptura("add");
				}	
				break;
			case 1: //up
				if(confirm("Desea aplicar los cambios?")) {
					//activaBotones(true, false, false); 
					activaBotones(false, true, true);		
					//alert("Aplicando cambios");
					requestFichaCaptura("up");
				}
				limpiaFichaCaptura;
				ctrlReadOnly(0);
				break;
			case 2: //del
				if(confirm("Continuar con la eliminacion del registro?")) {
					//activaBotones(true, true, false); 
					activaBotones(false, true, true);		
					//alert("Eliminando registro");
					requestFichaCaptura("del");
				}
				limpiaFichaCaptura;
				ctrlReadOnly(0);
				break;
			case 3: // cancel
				getCatalogoAdmin(opc_tab);
				activaBotones(false, true, true);
				limpiaFichaCaptura;
				//opcTab(0);
				ctrlReadOnly(0);
		}
		
	}
	
	function getParams(){
		var s_params='&';
		var s_value='';
		for (var i = 0; (i <= a_tags.length -1); i++){
			var o_tag = document.getElementById(a_tags[i]);
			
			if(a_tags[i].indexOf("txt") > -1){
				s_value = escape(o_tag.value);
			} else if (a_tags[i].indexOf("chk") > -1){
				//s_value = (o_tag.checked)? 0: 1;
				s_value = (o_tag.checked)? "S": "N";
			} else if (a_tags[i].indexOf("hid") > -1){
				s_value = o_tag.value;
			}
			s_params += ((i>0)? "&":"") + a_tags[i] + "=" + s_value ; 
		}
		//alert(s_params);
		//var o_idcaso = document.getElementById("hidIdCaso");
		//var o_idoper = document.getElementById("hidIdOper");
		return s_params;
	}
	
	function validaFichaCaptura(){
		var s_cadena = 'Los siguiente campos son requeridos:';
		var o_tag = null;
		
		switch (opc_tab){
			case 0: // Usuarios
				//alert("Cambiar!");				
				o_tag = document.getElementById(a_tags[0]);
				if (o_tag.value == ''){
					s_cadena += "\nId de Usuario es requerido."
				}
				
				o_tag = document.getElementById(a_tags[1]);
				if (o_tag.value == ''){
					s_cadena += "\nLa Contraseña es requerido."
				}
				
				o_tag = document.getElementById(a_tags[3]);
				if (o_tag.value == ''){
					s_cadena += "\nEl Apellido Paterno es requerido."
				}
				
				o_tag = document.getElementById(a_tags[5]);
				if (o_tag.value == ''){
					s_cadena += "\nEl Nombre es requerido."
				}

				o_tag = document.getElementById(a_tags[6]);
				if (o_tag.value == ''){
					s_cadena += "\nLa Clave de Area es requerida."
				}

				o_tag = document.getElementById(a_tags[7]);
				if (o_tag.value == ''){
					s_cadena += "\nEl Cargo es requerido."
				}
				//alert("Cambiar!");				
				
				break;
			case 1: // Grupos

				o_tag = document.getElementById(a_tags[0]);
				if (o_tag.value == ''){
					s_cadena += "\nId de Usuario es requerido."
				}
				
				o_tag = document.getElementById(a_tags[1]);
				if (o_tag.value == ''){
					s_cadena += "\nEl Nombre del Grupo es requerido."
				}
				
				break;
			case 2: // Propiedades
				a_tags = new Array("txtIdUsuarioProp", "txtNombreProp", "txtValorProp");
				
				o_tag = document.getElementById(a_tags[0]);
				if (o_tag.value == ''){
					s_cadena += "\nId de Usuario es requerido."
				}
				
				o_tag = document.getElementById(a_tags[1]);
				if (o_tag.value == ''){
					s_cadena += "\nEl Nombre Propiedad es requerido."
				}
				
				o_tag = document.getElementById(a_tags[2]);
				if (o_tag.value == ''){
					s_cadena += "\nEl Valor es requerido."
				}
				
				
				break;
			case 3: // Role
				
				o_tag = document.getElementById(a_tags[0]);
				if (o_tag.value == ''){
					s_cadena += "\nId de Usuario es requerido."
				}
				
				o_tag = document.getElementById(a_tags[1]);
				if (o_tag.value == ''){
					s_cadena += "\nEl Nombre del Role es requerido."
				}
				break;
			case 4: // MTTO. GRUPOS
				
				o_tag = document.getElementById(a_tags[0]);
				if (o_tag.value == ''){
					s_cadena += "\nId de grupo es requerido."
				}
				
				o_tag = document.getElementById(a_tags[1]);
				if (o_tag.value == ''){
					s_cadena += "\nLa descripcion de grupo es requerido."
				}
				break;
			case 5: // MTTO. ROLES
				
				o_tag = document.getElementById(a_tags[0]);
				if (o_tag.value == ''){
					s_cadena += "\nId de Role es requerido."
				}
				
				o_tag = document.getElementById(a_tags[1]);
				if (o_tag.value == ''){
					s_cadena += "\nEl Nombre del Role es requerido."
				}
				break;	
			case 8: // Usuario Copiar/Mover
				//alert("Cambiar!");				
				o_tag = document.getElementById(a_tags[0]);
				if (o_tag.value == ''){
					s_cadena += "\nId de Usuario origen es requerido."
				}
				
				o_tag = document.getElementById(a_tags[1]);
				if (o_tag.value == ''){
					s_cadena += "\nId de Usuario destino es requerido."
				}
				//alert("Cambiar!");				
				
				break;
		}
			
		if (s_cadena.length > 36){
			alert(s_cadena);
			return false;
		} else {
			return true;
		}	
	}
	
	function limpiaFichaCaptura(){

		switch (opc_tab){
			case 0: // Usuarios
				//alert("Cambiar!");				
				document.getElementById("txtIdUsuario").value="";
				document.getElementById("txtPwd").value="";
				document.getElementById("txtSaludo").value="";
				document.getElementById("txtAPaterno").value="";
				document.getElementById("txtAMaterno").value="";
				document.getElementById("txtNombres").value="";
				document.getElementById("txtDirigidoA").value="";
				document.getElementById("lbDirigidoADesc").value="";
				document.getElementById("txtCargo").value="";
				//alert("Cambiar!");				
				
				break;
			case 1: // Grupos
				document.getElementById("txtIdUsuarioGrp").value="";
				document.getElementById("txtNombreGrp").value="";
				
				break;
			case 2: // Propiedades
				document.getElementById("txtIdUsuarioProp").value="";				
				document.getElementById("txtNombreProp").value="";
				document.getElementById("txtValorProp").value="";
								
				break;
			case 3: // Role
				document.getElementById("txtIdUsuarioRole").value="";
				document.getElementById("txtNombreRole").value="";

				break;
			case 4: // MTTO. GRUPOS
				
				document.getElementById("txtNombreGrupo").value="";
				document.getElementById("txtDescripcionGrupo").value="";

				break;
			case 5: // MTTO. ROLES
				
				document.getElementById("txtMNombreRole").value="";
				document.getElementById("txtDescripcionRole").value="";

				break;
					
			case 7: // Areas
				//alert("Cambiar!");				
				document.getElementById("txtMClaveArea").value="";
				document.getElementById("txtMDescripcionArea").value="";
				document.getElementById("txtMPrefijoFolio").value="";
				document.getElementById("txtAreaPadre").value="";
				document.getElementById("lbAreaPadreDesc").value="seleccionar...";
				document.getElementById("txtTipoArea").value="";
				document.getElementById("txtBandejaCompartidaEntrada").value="";
				document.getElementById("txtBandejaCompartidaSalida").value="";
		}

	}

	function onServerResponseError(status, message) {
		window.alert(message);
	}   
	
	function getAnchoCol(col){
		var ancho='';
		switch(col){
			case 0: 
				ancho = "10%"; 
				break;
			case 1: 
				ancho = "80%px";
				break;
			default: ancho = "10%"; 
				break;
		}
		return ancho;
	}
	
	function validaParams(opc){
		if (opc == "usr"){
		
		} else if(opc == "grp") {
			
		} else if(opc == "rls") {
		
		}
	}
	
	function requestFichaCaptura(accion) {
	
		if (!validaFichaCaptura()){
			alert('no valida');
			return -1;
		}

		var params = getParams();
		var url = "../admin/SeguridadAdmin?accion=" + accion + "&catalogo=" + nom_catalogo + params;
		//alert("url: " + url);
		var req = new XMLHttp(url, onServerResponseResultado);
		req.doPost();
		limpiaFichaCaptura();
		//return true;
	}

	function requestBorraRegistro(params) {
		if(confirm("Continuar con la eliminacion del registro?")) {
			var url = "../admin/SeguridadAdmin?accion=del&catalogo=" + nom_catalogo + params;
			var req = new XMLHttp(url, onServerResponseResultado);
			req.doPost();
		}	
	}
	
	function onServerResponseResultado(xml){
	 	alert("Operacion finalizada." + xml);
	 	getCatalogoAdmin(opc_tab);
	 	//activaBotones(false, false, false); 
	}

	function onServerResponseUsuario(responseXML) {
		//var tbl = document.getElementById("tblCN");
		var o_msg = document.getElementById("msg");
		o_msg.innerHTML = "Cargando informacion...";
		alert("onServerResponseUsuario : " + responseXML);
		//getUserList();
		
		o_msg.innerHTML = "";
	}
		
	function Convertir(oPassword){
  	   var newPassword = hex_md5(oPassword);
  	   return newPassword;
  	}
		