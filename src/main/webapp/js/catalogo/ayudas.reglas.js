	/***********************************************************************************************************************
	/*	ayudas.reglas.js
	/*
	/*	fecha: 09 de Julio del 2007
	/*
	/*	CopyRight 2007, S&C Constructores de Sistemas S.A.
	/*
	/*	JavaScript que define los atributos para los controles de la clase txtAyuda de las ayudas en linea.
	/*
	/*	Sintaxis:
	/*
	/*			$("#txt**..")			 txt   = estandar para nombrar los controles de captura para el filtro
	/*								  	  **.. = nombre del control <input> de tipo text al que se le van a asignar los atributos
	/*
	/*			$("#lb**..Desc")	lb**..Desc = estandar para nombrar los controles de Descripción de ayudas, lb es de label y 
	/*											 Desc de Descripción
	/*									  **.. = nombre del control <input> de tipo text deshabilitado al que se le van a asignar 
	/*											 el atributo de size. Este control se crea dinamicamente con el nombre del atributo 
	/*											 tabla (especificado en la definicin del <input> del html). (Nota: se paso a el html)
	/*
	/*			atributos:
	/*
	/*						maxLength		: mximo nmero de caracteres a escribir en el control. (Nota: se paso a el html)
	/*						size			: tamao en caracteres del control.  (Nota: se paso a el html)
	/*						campo			: campo de la tabla a la que hace referencia el control.
	/*						dependencias	: nombre del atributo tabla (especificado en la definicin del <input> del html) del cual
	/*										  depende este control, es decir, debe existir un valor en el control de dependencia
	/*										  para poder seleccionar uno de este control. Puede tener ms de una dependencia y van 
	/*										  separadas por coma, s el control no tiene dependencias es necesario llenarlo con la
	/*										  palabra "ninguna".
	/*						tablacampos		: orden y nombres de los campos de la tabla separados por coma que apareceran en el grid de ayuda.
	/*						tablavscontrol	: relacion entre columna y control del html, si dentro del texto trae un "*" este es el valor 
	/*										  devuelto al seleccionar un renglon del grid. solo uno puede tener un asterisco como prefijo.
	/*						tablaHeaders	: letreros de los headers del grid separados por coma y que siguen el orden del atributo tablacampos.
	/*						tablaAnchos		: anchos de los controles en caracteres de filtros y columnas del grid separados por coma y que 
	/*										  siguen el orden del atributo tablacampos.
	/*						tablaTipos		: tipo de los controles de filtros separados por coma y que siguen el orden del atributo tablacampos.
	/*
	/**********************************************************************************************************************/
	

	$.subAtributos = function()
	{
		$("#txtAreaExistente").attr(	{campo:"cra_id_area",	dependencias: "ninguna",		tablacampos: "cra_id_area,cra_descripcion",	        	tablavscontrol: "AreaExistente",					tablaHeaders: "Clave,Descripción",		tablaAnchos:"20,80",		tablaTipos:"string,string" });		
		
		$("#txtRemNombreCompleto").attr({campo:"id_persona",	dependencias: "AreaExistente",	tablacampos: "cra_id_area,id_persona,nombre_completo,puesto",	tablavscontrol: "AreaExistente,RemNombreCompleto,,RemCargo",	tablaHeaders: "Area,ID,Nombre,Puesto",			tablaAnchos:"15,15,50,50",	tablaTipos:"string,string,string,string" });
		
		$("#txtRemCargo").attr({campo:"puesto",	dependencias: "AreaExistente,RemNombreCompleto",	tablavscontrol: "AreaExistente,RemNombreCompleto,,RemCargo",	tablaTipos:"string,string,string" });

		//$("#txtAreaExistente").attr(	{campo:"cra_id_area",	dependencias: "ninguna",		tablacampos: "cra_id_area,cra_descripcion",	        	tablavscontrol: "AreaExistente",					tablaHeaders: "Clave,Descripción",		tablaAnchos:"20,80",		tablaTipos:"string,string" });		
		//$("#txtRemNombreCompleto").attr({campo:"id_persona",	dependencias: "AreaExistente",	tablacampos: "cra_id_area,id_persona,nombre_completo,puesto",	tablavscontrol: "AreaExistente,RemNombreCompleto",	tablaHeaders: "Area,ID,Nombre,Puesto",			tablaAnchos:"15,15,50,50",	tablaTipos:"string,string,string,string" });


		// controles de captura de texto
		$("#txtPrioridad").attr(	{campo:"id",				dependencias: "ninguna",		tablacampos: "id,descripcion",	        			tablavscontrol: "Prioridad,",						tablaHeaders: "Clave,Descripción",				tablaAnchos:"4,15",		tablaTipos:"string,string" });		

		$("#txtCCP").attr(			{campo:"id_area",			dependencias: "ninguna",		tablacampos: "id_area,d_descripcion",	            tablavscontrol: "DirigidoAInternas,",				tablaHeaders: "Clave,Descripción",			    tablaAnchos:"20,80",		tablaTipos:"string,string" });

		$("#txtAreaCaptura").attr(	{campo:"cra_id_area",		dependencias: "ninguna",		tablacampos: "cra_id_area,cra_descripcion",	        tablavscontrol: "AreaCaptura",						tablaHeaders: "Clave,Descripción",				tablaAnchos:"20,80",		tablaTipos:"string,string" });		

		$("#AreaRemitente").attr(	{campo:"cra_id_area",		dependencias: "ninguna",		tablacampos: "cra_id_area,cra_descripcion",	        tablavscontrol: "AreaRemitente",					tablaHeaders: "Clave,Descripción",				tablaAnchos:"20,80",		tablaTipos:"string,string" });		


		$("#txtRemitente").attr(	{campo:"area_id",			dependencias: "ninguna",		tablacampos: "area_id,d_descripcion",	        	tablavscontrol: "Remitente,",						tablaHeaders: "Clave,Descripción",				tablaAnchos:"20,80",		tablaTipos:"string,string" });
		
		$("#txtArea").attr(			{campo:"area_id",			dependencias: "ninguna",		tablacampos: "area_id,d_descripcion",	            tablavscontrol: "Area,",							tablaHeaders: "Clave,Descripción",			    tablaAnchos:"20,80",		tablaTipos:"string,string" });

		$("#txtAreaPadre").attr(	{campo:"area_id",			dependencias: "ninguna",		tablacampos: "area_id,d_descripcion",	            tablavscontrol: "AreaPadre,",						tablaHeaders: "Clave,Descripción",			    tablaAnchos:"20,80",		tablaTipos:"string,string" });
		
		$("#txtTurnadoA").attr(	    {campo:"area_id",			dependencias: "ninguna",		tablacampos: "area_id,d_descripcion",	            tablavscontrol: "TurnadoA,",						tablaHeaders: "Clave,Descripción",			    tablaAnchos:"20,80",		tablaTipos:"string,string" });

		$("#txtDirigidoA").attr(	{campo:"area_id",			dependencias: "ninguna",			tablacampos: "area_id,d_descripcion",	            	tablavscontrol: "DirigidoA,",					tablaHeaders: "Clave,Descripción",			tablaAnchos:"20,80",		tablaTipos:"string,string" });		
		$("#txtPersona").attr(		{campo:"user_id", 			dependencias: "DirigidoA",			tablacampos: "area_id,user_id,nombre_completo,puesto",	tablavscontrol: "DirigidoA,Persona,,CPersona",			tablaHeaders: "Area,Usuario,Nombre,Cargo",	tablaAnchos:"20,10,60,100",	tablaTipos:"string,string,string,string" });
		$("#txtCPersona").attr(		{campo:"puesto",			dependencias: "DirigidoA,Persona",	tablacampos: "area_id,user_id,nombre_completo,puesto",	tablavscontrol: "DirigidoA,Persona,,CPersona",	tablaHeaders: "Area,Usuario,Nombre,Cargo",	tablaAnchos:"20,10,60,100",	tablaTipos:"string,string,string" });
		
		$("#txtPersona2").attr(		{campo:"user_id", 			dependencias: "ninguna",			tablacampos: "area_id,user_id,nombre_completo,puesto",					tablavscontrol: ",Persona2,,CPersona2",			tablaHeaders: "Area,Usuario,Nombre,Cargo",		tablaAnchos:"20,10,60,100",		tablaTipos:"string,string,string,string" });
		$("#txtCPersona2").attr({campo:"puesto",				dependencias: "txtPersona2",	tablavscontrol: "AreaExistente,RemNombreCompleto,,CPersona2",	tablaTipos:"string,string,string,string" });

		$("#txtPersona3").attr(		{campo:"user_id", 			dependencias: "ninguna",			tablacampos: "area_id,user_id,nombre_completo,puesto",					tablavscontrol: ",Persona3,,RemCargoPer3",			tablaHeaders: "Area,Usuario,Nombre,Cargo",		tablaAnchos:"20,10,60,100",		tablaTipos:"string,string,string,string" });
		$("#txtRemCargoPer3").attr( {campo:"puesto",			dependencias: "Persona3",	        tablavscontrol: "AreaExistente,RemNombreCompleto,,RemCargoPer3",	tablaTipos:"string,string,string,string" });
		

		$("#txtUsuario").attr(		{campo:"user_id", 			dependencias: "ninguna",			tablacampos: "area_id,user_id,nombre_completo",					tablavscontrol: "DirigidoA,Persona,,",			tablaHeaders: "Area,Usuario,Nombre",		tablaAnchos:"20,10,60",		tablaTipos:"string,string,string" });

		$("#txtUsrturno").attr(		{campo:"user_id", dependencias: "TurnadoA",		tablacampos: "area_id,user_id,nombre_completo,puesto",	tablavscontrol: "TurnadoA,Usrturno,",		tablaHeaders: "Area,Clave,Empleado,Puesto",		tablaAnchos:"20,10,60,100",		tablaTipos:"string,string,string,string" });

		$("#txtGrupoUsuario").attr(	{campo:"g_nombre",			dependencias: "ninguna",		tablacampos: "g_nombre,g_descripcion",	            tablavscontrol: "GrupoUsuario,",			tablaHeaders: "Clave,Descripción",			    tablaAnchos:"20,80",		tablaTipos:"string,string" });
		/* controles de etiquetas de descripcion
		$("#lbMarcaDesc").attr(     {size: 50} );
		$("#lbModeloDesc").attr(	{size: 50} );
		$("#lbResolucionDesc").attr({size: 50} );*/
	};
	
	
	