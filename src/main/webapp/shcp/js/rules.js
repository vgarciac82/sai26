
var	currentElement;
var changeCurrent = true;

var caracteresAlf = new Array("a","b","c","d","e","f","g","h","i","j",
				"k","l","m","n","o","p","q","r","s","t",
				"u","v","w","x","y","z");

var caracteres = new Array("a","b","c","d","e","f","g","h","i","j",
				"k","l","m","n","o","p","q","r","s","t",
				"u","v","w","x","y","z","1","2","3","4",
				"5","6","7","8","9","0"," " );

var caracteresNum = new Array("1","2","3","4","5","6","7","8","9","0");

var valido = false;		
var a = 0;
var i = 0;

var Rules = {
// Para campos requeridos y alfabeticos
	'.reqWhiteAlf:focus': function(element, event) {
		if (changeCurrent) { currentElement = element; }
	},
	'.reqWhiteAlf:blur': function(element, event) {
			if (currentElement != element) {
				changeCurrent = true;
			} else if (isWhitespace(element.value)) {
				changeCurrent = false;
				window.alert("El dato de "+ element.name + " es requerido");
				element.focus();
			} else { changeCurrent = true; }
	},
	'.reqWhiteAlf:keyup': function(element, event) {
		for(i=0;i<element.value.length;i++){
			for(a=0;a<caracteresAlf.length;a++){
				if(element.value.substr(i,1).toLowerCase() == caracteresAlf[a]){
					valido = true;
					break;
				}
			}
			if(!valido){
				element.value = element.value.substr(0,i) + element.value.substr(i+1);
			}
			valido = false;
		}
	},
// Para campos requeridos y alfanumericos
	'.reqWhiteAlfNum:focus': function(element, event) {
		if (changeCurrent) { currentElement = element; }
	},
	'.reqWhiteAlfNum:blur': function(element, event) {
			if (currentElement != element) {
				changeCurrent = true;
			} else if (isWhitespace(element.value)) {
				changeCurrent = false;
				window.alert("El dato de "+ element.name + " es requerido");
				element.focus();
			} else { changeCurrent = true; }
	},
	'.reqWhiteAlfNum:keyup': function(element, event) {
		//esta funcion se pone en el campo deseado de la siguiente forma:
		//<input type="..." name="..." onKeyUp="validateCharsAlf(this);">
		for(i=0;i<element.value.length;i++){
			for(a=0;a<caracteres.length;a++){
				if(element.value.substr(i,1).toLowerCase() == caracteres[a]){
					valido = true;
					break;
				}
			}
			if(!valido){
				element.value = element.value.substr(0,i) + element.value.substr(i+1);
			}
			valido = false;
		}
	},
// Para campos requeridos y numericos
	'.reqWhiteNum:focus': function(element, event) {
		if (changeCurrent) { currentElement = element; }
	},
	'.reqWhiteNum:blur': function(element, event) {
			if (currentElement != element) {
				changeCurrent = true;
			} else if (isWhitespace(element.value)) {
				changeCurrent = false;
				window.alert("El dato de "+ element.name + " es requerido");
				element.focus();
			} else { changeCurrent = true; }
	},
	'.reqWhiteNum:keyup': function(element, event) {
		//esta funcion se pone en el campo deseado de la siguiente forma:
		//<input type="..." name="..." onKeyUp="validateCharsAlf(this);">
		for(i=0;i<element.value.length;i++){
			for(a=0;a<caracteresNum.length;a++){
				if(element.value.substr(i,1).toLowerCase() == caracteresNum[a]){
					valido = true;
					break;
				}
			}

			if(!valido){
				element.value = element.value.substr(0,i) + element.value.substr(i+1);
			}
			valido = false;
		}
	},
// Para campos RFC requeridos
	'.reqWhiteRfc:focus': function(element, event) {
		if (changeCurrent) { currentElement = element; }
	},
	'.reqWhiteRfc:blur': function(element, event) {
			if (currentElement != element) {
				changeCurrent = true;
			} else if (isWhitespace(element.value) || element.value.length != 13) {
				changeCurrent = false;
				window.alert("El dato de "+ element.name + " es requerido");
				element.focus();
			} else { changeCurrent = true; }
	},
	'.reqWhiteRfc:keyup': function(element, event) {
		//esta funcion se pone en el campo deseado de la siguiente forma:
		//<input type="..." name="..." onKeyUp="validateCharsAlf(this);">
		for(i=0;i<element.value.length;i++){
			if (i < 4) {
				for(a=0;a<caracteresAlf.length;a++){
					if(element.value.substr(i,1).toLowerCase() == caracteresAlf[a]){
						valido = true;
						break;
					}
				}
			}
			else if (i > 3 && i < 10) {
				for(a=0;a<caracteresNum.length;a++){
					if(element.value.substr(i,1).toLowerCase() == caracteresNum[a]){
						valido = true;
						break;
					}
				}
			}
			else if (i > 9) {
				for(a=0;a<caracteres.length;a++){
					if(element.value.substr(i,1).toLowerCase() == caracteres[a]){
						valido = true;
						break;
					}
				}
			}

			if(!valido){
				element.value = element.value.substr(0,i) + element.value.substr(i+1);
			}
			valido = false;
		}
	}
	
};
