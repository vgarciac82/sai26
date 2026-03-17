// validaciones.js

function validateChars(campo){
	//esta funcion se pone en el campo deseado de la siguiente forma:
	//<input type="..." name="..." onKeyUp="validateChars(this);">
	var caracteres = new Array("a","b","c","d","e","f","g","h","i","j",
					"k","l","m","n","o","p","q","r","s","t",
					"u","v","w","x","y","z","1","2","3","4",
					"5","6","7","8","9","0","."," " );
	var valido = false;		
	var a = 0;
	var i = 0;			
	for(i=0;i<campo.value.length;i++){
		for(a=0;a<caracteres.length;a++){
			if(campo.value.substr(i,1).toLowerCase() == caracteres[a]){
				valido = true;
				break;
			}
		}
		
		if(!valido){
			campo.value = campo.value.substr(0,i) + campo.value.substr(i+1);
		}
		valido = false;
	}
}

function validateCharsAlf(campo){
	//esta funcion se pone en el campo deseado de la siguiente forma:
	//<input type="..." name="..." onKeyUp="validateCharsAlf(this);">
	var caracteres = new Array("a","b","c","d","e","f","g","h","i","j",
					"k","l","m","n","o","p","q","r","s","t",
					"u","v","w","x","y","z");
	var valido = false;		
	var a = 0;
	var i = 0;			
	for(i=0;i<campo.value.length;i++){
		for(a=0;a<caracteres.length;a++){
			if(campo.value.substr(i,1).toLowerCase() == caracteres[a]){
				valido = true;
				break;
			}
		}
		
		if(!valido){
			campo.value = campo.value.substr(0,i) + campo.value.substr(i+1);
		}
		valido = false;
	}
}


function validateCharsNumbers(campo){
	//esta funcion se pone en el campo deseado de la siguiente forma:
	//<input type="..." name="..." onKeyUp="validateChars(this);">
	var caracteres = new Array("1","2","3","4","5","6","7","8","9","0");
	var valido = false;		
	var a = 0;
	var i = 0;			
	for(i=0;i<campo.value.length;i++){
		for(a=0;a<caracteres.length;a++){
			if(campo.value.substr(i,1).toLowerCase() == caracteres[a]){
				valido = true;
				break;
			}
		}
		
		if(!valido){
			campo.value = campo.value.substr(0,i) + campo.value.substr(i+1);
		}
		valido = false;
	}
}

function checkDate(d1, d2){
	//d1 es inicial, d2 final
	if(d2 < d1)
		return false;
		
	return true;
}

function brincaCampo(num_element){	
	sapf.elements[++num_element].disabled = false;
	sapf.elements[num_element].focus();
}

function blurCampos(campo){
	if(campo.value=='' || campo.value.length == 0){
		alert(('*** El dato '+campo.id+' es requerido ***').toUpperCase());
	}
	campo.value="";
	return(false);
}