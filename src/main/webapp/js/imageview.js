var print = false;
function imageManager(type) {
	var inc = 1.5;
	var max_inc = 4500;
	var min_inc = 1;
	var imgWidth = document.getElementsByName("image.width")[0];
	var imgHeight = document.getElementsByName("image.height")[0];
	var rotate = document.getElementsByName("image.rotate")[0];
	var imgIndex = document.getElementsByName("image.index")[0];
	var imgList = parent.listFrame.document.getElementById("paginate");
	var href = window.location.href;
	var imgMaxVal = parseInt(document.getElementsByName("totPage")[0].value) - 1;
	var idxPosIni = href.lastIndexOf("=") + 1;
	var indexVal = parseInt(imgIndex.value);
	if ((indexVal < 0) && (type > 2)) {
		return;
	}
	switch (type) {
	  case 1: // Escanear
		var select = getNodeSelect();
		openCenteredWindow("PageDocumentScan.jsp?select=" + select, "_blank", 520, 771);
		break;
	  case 2: // Importar Imagen
		var select = getNodeSelect();
		openCenteredWindow("PageDocumentUpload.jsp?select=" + select, "_blank", 450, 479);
		break;
	  case 8: // Anterior
		imgWidth.value = 0;
		imgHeight.value = 0;
		rotate.value = 0;
		outIndex = ((indexVal - 1) < 0) ? 0 : indexVal - 1;
		href = href.substring(0, idxPosIni) + outIndex;
		imgIndex.value = outIndex;
		if (outIndex != indexVal) {
			window.location.href = href;
			updateImagesList(imgList, outIndex);
		}
		break;
	  case 7: // Primera
		imgWidth.value = 0;
		imgHeight.value = 0;
		rotate.value = 0;
		imgIndex.value = "0";
		href = href.substring(0, idxPosIni) + "0";
		window.location.href = href;
		updateImagesList(imgList, 0);
		break;
	  case 9: // Siguiente
		imgWidth.value = 0;
		imgHeight.value = 0;
		rotate.value = 0;
		outIndex = ((indexVal + 1) > imgMaxVal) ? imgMaxVal : indexVal + 1;
		href = href.substring(0, idxPosIni) + outIndex;
		imgIndex.value = outIndex;
		if (outIndex != indexVal) {
			window.location.href = href;
			updateImagesList(imgList, outIndex);
		}
		break;
	  case 10: // Ultima
		imgWidth.value = 0;
		imgHeight.value = 0;
		rotate.value = 0;
		href = href.substring(0, idxPosIni) + imgMaxVal;
		window.location.href = href;
		imgIndex.value = imgMaxVal;
		updateImagesList(imgList, imgMaxVal);
		break;
	  case 11: // Zoom +
		var objImg = document.getElementById("imgView");
		var currWidth = parseInt(objImg.width);
		var currHeight = parseInt(objImg.height);
		imgWidth.value = (parseInt(currWidth * inc) > max_inc) ? currWidth : parseInt(currWidth * inc);
		imgHeight.value = parseInt((parseInt(imgWidth.value) * currHeight) / currWidth);
		objImg.width = imgWidth.value;
		objImg.height = imgHeight.value;
		objImg.alt = "Tama?o " + imgWidth.value + " x " + imgHeight.value;
		break;
	  case 12: // Zoom -
		var objImg = document.getElementById("imgView");
		var currWidth = parseInt(objImg.width);
		var currHeight = parseInt(objImg.height);
		imgWidth.value = (parseInt(currWidth / inc) < min_inc) ? min_inc : parseInt(currWidth / inc);
		imgHeight.value = parseInt((parseInt(imgWidth.value) * currHeight) / currWidth);
		objImg.width = imgWidth.value;
		objImg.height = imgHeight.value;
		objImg.alt = "Tama?o " + imgWidth.value + " x " + imgHeight.value;
		break;
	  case 13: // Rotar Der.
		rotate.value = (((parseInt(rotate.value) - 90) < 0) ? 270 : parseInt(rotate.value) - 90);
		document.getElementById("mngPage").submit();
		break;
	  case 14: // Rotar Izq.
		rotate.value = (((parseInt(rotate.value) + 90) > 270) ? 0 : parseInt(rotate.value) + 90);
		document.getElementById("mngPage").submit();
		break;
	  case 15: // Restablecer
		imgWidth.value = 0;
		imgHeight.value = 0;
		rotate.value = 0;
		document.getElementById("mngPage").submit();
		break;
	  case 16: // Eliminar
		if (confirm("Desea eliminar la p\xe1gina actual?")) {
			document.getElementById("delPage").submit();
		}
		break;
	}
}
function updateImagesList(imgSelect, currIdx) {
	if (imgSelect) {
		for (i = 0; i < imgSelect.length; i++) {
			pos = imgSelect.options[i].value.indexOf("-");
			imgIni = parseInt(imgSelect.options[i].value.substring(0, pos));
			imgFin = parseInt(imgSelect.options[i].value.substring(pos + 1)) - 1;
			if ((currIdx >= imgIni) && (currIdx <= imgFin) && !imgSelect.options[i].selected) {
				imgSelect.value = imgSelect.options[i].value;
				parent.listFrame.document.forms[0].submit();
				break;
			}
		}
	}
}
function getImageIndex(list) {
	var currName = document.getElementsByName("image.name")[0];
	if (currName) {
		for (var i = 0; i < list.length; i++) {
			var name = list[i].href.substring(list[i].href.indexOf("=") + 1, list[i].href.indexOf("&"));
			if (name == currName.value) {
				return i;
			}
		}
	}
	return -1;
}
function getPagesNumber() {
	var currPage = document.getElementsByName("currPage")[0];
	var totPage = document.getElementsByName("totPage")[0];
	var list = parent.listFrame.document.getElementsByName("imgList");
	if (document.getElementsByName("image.name")) {
		var i = getImageIndex(list);
		currPage.value = i + 1;
		totPage.value = list.length;
	} else {
		currPage.value = 0;
		totPage.value = list.length;
	}
}
function resizeImageViewer() {
	var images = document.getElementById("imgDiv");
	if (navigator.appName.indexOf("Microsoft") != -1) {
		width = (document.body.clientWidth - document.body.leftMargin) + 9;
		if (print) {
			height = "100%";
		} else {
			height = document.body.clientHeight - 59;
		}
		print = false;
	} else {
		width = window.innerWidth - 2;
		height = window.innerHeight - 40;
	}
	images.style.width = width;
	images.style.height = height;
	window.scroll(0, 0);
}
function resize(id) {
	var frame = document.getElementById(id);
	var htmlheight = document.body.parentNode.scrollHeight;
	var windowheight = window.innerHeight;
	if (htmlheight < windowheight) {
		document.body.style.height = windowheight + "px";
		frame.style.height = windowheight + "px";
	} else {
		document.body.style['height'] = htmlheight + "px";
		frame.style.height = htmlheight + "px";
	}
}
function openCenteredWindow(url, name, height, width, parms) {
	var left = Math.floor((screen.width - width) / 2);
	var top = Math.floor((screen.height - height) / 2);
	var winParms = "top=" + top + ",left=" + left + ",height=" + height + ",width=" + width + ",scrollbar=yes,status=yes";
	if (parms) {
		winParms += "," + parms;
	}
	var win = window.open(url, name, winParms);
	if (parseInt(navigator.appVersion) >= 4) {
		win.window.focus();
	}
	return win;
}

