
function cgVariables() {
    var xmlDoc;
    this.getXMLData = function () {
        if (document.implementation && document.implementation.createDocument) {
            xmlDoc = document.implementation.createDocument("", "", null);
            xmlDoc.onload = loadHTMLData;
        } else {
            if (window.ActiveXObject) {
                xmlDoc = new ActiveXObject("Microsoft.XMLDOM");
                xmlDoc.onreadystatechange = function () {
                    if (xmlDoc.readyState == 4) {
                        loadHTMLData(true);
                    }
                };
            } else {
                alert("Su browser no puede ejecutar este script");
                return;
            }
        }
        xmlDoc.load("../xml/gestion.xml");
    };
    this.setXMLData = function () {
        readHTMLData(datawork.document);
        var p = datawork.document.createElement("p");
        p.appendChild(datawork.document.createTextNode(xmlDoc.xml));
        datawork.document.body.appendChild(p);
        loadHTMLData(false);
    };
    function XMLRequest() {
        this.makeRequest = function (url, xmlDocto, responseHandler) {
            var httpRequest = (window.XMLHttpRequest) ? new XMLHttpRequest() : new ActiveXObject("MSXML2.XMLHTTP");
            httpRequest.onreadystatechange = function () {
                if (httpRequest.readyState == 4) {
                    responseHandler(httpRequest);
                }
            };
            httpRequest.open("POST", url, true);
            if ((window.XMLHttpRequest) && (httpRequest.setRequestHeader)) {
                httpRequest.setRequestHeader("Content-Type", "text/xml");
            }
            httpRequest.send(xmlDocto);
        };
    }
    this.sendXMLData = function (url, responseHandler) {
        var xmlReq = new XMLRequest();
        xmlReq.makeRequest(url, xmlDoc, responseHandler);
    };
    function loadHTMLData(removeNodes) {
        if (xmlDoc.parseError.errorCode != 0) {
        	//Ethiel, se comenta porque sale indiscriminadamente este mensaje y de cualquier forma deja avanzar el caso
			//y guarda bien las calificaciones en BD, de modo que solo resulta molesto para el usuario ver el mensaje
            //alert("Error: Cargando archivo \"" + xmlDoc.parseError.url + "\"\nRazon: " + xmlDoc.parseError.reason);
            return;
        }
        var x = xmlDoc.getElementsByTagName("form");
        var idx = 0;
        if (x.length <= 0) {
            return;
        }
        var xmlUsed = new Array(x[0].childNodes.length);
        for (var i = 0; i < x[0].childNodes.length; i++) {
            if (x[0].childNodes[i].nodeType != 1) {
                continue;
            }
            var tagName = x[0].childNodes[i].attributes(0).nodeValue;
            var htmlEl = datawork.document.getElementsByName(tagName);
            if (htmlEl.length <= 0) {
                continue;
            }
            xmlUsed[idx++] = x[0].childNodes[i];
            var xmlNode = x[0].childNodes[i].cloneNode(true);
            for (var j = 0; j < xmlNode.childNodes.length; j++) {
                var value = (xmlNode.childNodes[j].nodeType == 1) ? xmlNode.childNodes[j].text : xmlNode.childNodes[j].nodeValue;
                if (j >= htmlEl.length) {
                    var table = searchParentNode(htmlEl[j - 1], "TABLE");
                    var count = table.rows.length;
                    var newRow = table.insertRow(-1);
                    var copyRow = table.rows(((count % 2) == 0 ? 2 : 1));
                    for (var k = 0; k < copyRow.cells.length; k++) {
                        var cell = newRow.insertCell(k);
                        cell.innerHTML = copyRow.cells[k].innerHTML;
                        cell.value = "";
                    }
                }
                switch (htmlEl[j].tagName) {
                  case "INPUT":
                    if ((htmlEl[j].type == "radio") || (htmlEl[j].type == "checkbox")) {
                        for (var k = 0; k < htmlEl.length; k++) {
                            htmlEl[k].checked = (value == htmlEl[k].value);
                        }
                        break;
                    }
                  case "SELECT":
                    htmlEl[j].value = value;
                    break;
                }
            }
        }
        if (removeNodes) {
            for (var i = 0; i < idx; i++) {
                x[0].removeChild(xmlUsed[i]);
            }
        }
    }
    function searchParentNode(obj, tagName) {
        o = obj.parentNode;
        if (o.tagName == tagName) {
            return o;
        }
        return searchParentNode(o, tagName);
    }
    function readHTMLData(root) {
        try {
            for (var i = 0; i < root.childNodes.length; i++) {
                var node = root.childNodes.item(i);
                switch (node.nodeName) {
                  case "INPUT":
                    if (((node.type == "radio") || (node.type == "checkbox")) && (node.checked == true)) {
                        if ((node.name == "") || (node.name == undefined)) {
                            break;
                        }
                        var srchNode = xmlDoc.selectNodes("//gestion/form/data[@id='" + node.name + "']");
                        processNode(srchNode, node);
                        break;
                    } else {
                        if (node.type != "text") {
                            break;
                        }
                    }
                  case "SELECT":
                    if ((node.name == "") || (node.name == undefined)) {
                        break;
                    }
                    var srchNode = xmlDoc.selectNodes("//gestion/form/data[@id='" + node.name + "']");
                    processNode(srchNode, node);
                    break;
                }
                if (node.hasChildNodes()) {
                    readHTMLData(node);
                }
            }
        }
        catch (e) {
            alert("Error: " + e.message);
        }
    }
    function processNode(srchNode, htmlNode) {
        var xForm = xmlDoc.selectSingleNode("//gestion/form");
        if (xForm == null) {
            return;
        }
        if (srchNode.length == 0) {
            var newNode = xmlDoc.createElement("data");
            var attr = xmlDoc.createAttribute("id");
            attr.nodeValue = htmlNode.name;
            newNode.attributes.setNamedItem(attr);
            newNode.text = htmlNode.value;
            xForm.appendChild(newNode);
        } else {
            if (srchNode.length > 0) {
                var newNode = xmlDoc.selectSingleNode("//gestion/form/data[@id='" + htmlNode.name + "']");
                if ((srchNode.length == 1) && (srchNode[0].childNodes.length == 1)) {
                    var text = srchNode[0].text;
                    xForm.removeChild(srchNode[0]);
                    newNode = xmlDoc.createElement("data");
                    var attr = xmlDoc.createAttribute("id");
                    attr.nodeValue = htmlNode.name;
                    newNode.attributes.setNamedItem(attr);
                    newNode = xForm.appendChild(newNode);
                    var nNode = xmlDoc.createElement("data");
                    nNode.text = text;
                    newNode.appendChild(nNode);
                }
                var xChNode = xmlDoc.createElement("data");
                xChNode.text = htmlNode.value;
                newNode.appendChild(xChNode);
            }
        }
    }
    var _folio = "";
    this.getFolio = function () {
        return _folio;
    };
    this.setFolio = function (folio) {
        _folio = folio;
    };
    var _cteApePat = "";
    this.getCteApePat = function () {
        return _cteApePat;
    };
    this.setCteApePat = function (cteApePat) {
        _cteApePat = cteApePat;
    };
    var _cteApeMat = "";
    this.getCteApeMat = function () {
        return _cteApeMat;
    };
    this.setCteApeMat = function (cteApeMat) {
        _cteApeMat = cteApeMat;
    };
    var _cteNom = "";
    this.getCteNom = function () {
        return _cteNom;
    };
    this.setCteNom = function (cteNom) {
        _cteNom = cteNom;
    };
    var _rfc = "";
    this.getRfc = function () {
        return _rfc;
    };
    this.setRfc = function (rfc) {
        _rfc = rfc;
    };
    var _CN = "";
    this.getCN = function () {
        return _CN;
    };
    this.setCN = function (CN) {
        _CN = CN;
    };
    var _fechaIngreso = "";
    this.getFechaIngreso = function () {
        return _fechaIngreso;
    };
    this.setFechaIngreso = function (fechaIngreso) {
        _fechaIngreso = fechaIngreso;
    };
    var _ejecResp = "";
    this.getEjecResp = function () {
        return _ejecResp;
    };
    this.setEjecResp = function (ejecResp) {
        _ejecResp = ejecResp;
    };
    var _respCaptura = "";
    this.getRespCaptura = function () {
        return _respCaptura;
    };
    this.setRespCaptura = function (respCaptura) {
        _respCaptura = respCaptura;
    };
    var _respSolicitud = "";
    this.getRespSolicitud = function () {
        return _respSolicitud;
    };
    this.setRespSolicitud = function (respSolicitud) {
        _respSolicitud = respSolicitud;
    };
}
var gestion = new cgVariables();

