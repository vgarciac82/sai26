
var isIE;
function XMLHttp(url) {
    return XMLHttp(url, null);
}
function XMLHttp(url, callback) {
    var req = init();
    req.onreadystatechange = processRequest;
    function init() {
        if (window.XMLHttpRequest) {
            return new XMLHttpRequest();
        } else {
            if (window.ActiveXObject) {
                isIE = true;
                return new ActiveXObject("Microsoft.XMLHTTP");
            }
        }
    }
    function processRequest() {
        if ((req.readyState == 4) && (req.status == 200) && (callback)) {
        	var a = req;
            callback(req.responseXML);
        }
    }
    this.doGet = function () {
        req.open("GET", url, true);
        req.send(null);
    };
    this.doPost = function (body) {
        req.open("POST", url, true);
        req.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
        req.send(body);
    };
}
function getElementY(element) {
    var targetTop = 0;
    if (element.offsetParent) {
        while (element.offsetParent) {
            targetTop += element.offsetTop;
            element = element.offsetParent;
        }
    } else {
        if (element.y) {
            targetTop += element.y;
        }
    }
    return targetTop;
}

