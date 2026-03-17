// JavaScript Document
/////OCULTAR MENU///
function $(e) { return document.getElementById(e); }

function toggleVerMain(elem, id1, id2){ 
	//var tdM = $(id1);
	var tg = 0;
	if(elem=="NONE" || elem=="none"){
		elem=null;
		elem= document.getElementById("control_menu");
		tg= 1;
		
	}
	var tdM = document.getElementById(id1);//se substituye la linea anterior debido a que se cambia la versión de jquery y dejó de funcionar MASV 13/06/2012
	var tdP = $(id2);
	//var tablaH = $(id3);
	elem.src = (tdM.style.display == "none") ? "../imagenes/mCerrar.png" : "../imagenes/mAbrir.png";
	elem.title = ((tdM.style.display == "none") ? "Ocultar" : "Mostrar") + " men\u00FA";
	tdP.width = (tdM.style.display == "none") ? "" : "100%";
	tdM.style.display = (tdM.style.display == "none") ? "": "none";

//	tablaH.style.display = (tablaH.style.display == "none") ? "block": "none";

	var win = $("[name='content-iframe']"); //this = window
	var bar = 0;
	if ($("#menup").is(":visible")){
		bar = $("#menup").width();
	}else {
		bar = 0;
	}
	win.width($( window ).width()-(bar+50));
}

function toggleHorMain(elem, id1, id2){
	var trM = $(id1);
	var trP = $(id2);
	elem.src = (trM.style.display == "none") ? "../imagenes/mCerrar.png" : "../imagenes/mAbrir.png";
	elem.title = ((trM.style.display == "none") ? "Ocultar" : "Mostrar") + " men\u00FA";
	trP.height = (trM.style.display == "none") ? "" : "100%";
	trM.style.display = (trM.style.display == "none") ? "": "none";
}

function mostrarOcultarTablas(id){
	mostrado=0;
	elem = document.getElementById(id);
	if(elem.style.display=='block')mostrado=1;
	elem.style.display='none';
	if(mostrado!=1)elem.style.display='block';
}
function cambiar_color_over(celda){
   celda.style.backgroundColor="#66ff33"
}
function cambiar_color_out(celda){
   celda.style.backgroundColor="#dddddd"
} 