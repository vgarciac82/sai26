<%@ page language="java" contentType="text/html; charset=ISO-8859-1"%><!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page import="com.syc.viewer.servlet.ViewerParametersInterface,com.syc.gestion.core.Caso,com.syc.gestion.servlet.GestionInterface,java.util.Iterator,com.syc.fortimax.core.TipoDocumento"%><html>
<%@ taglib uri="/WEB-INF/tlds/imagetag.tld" prefix="image" %><head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link rel="stylesheet" href="../css/imageview.css" type="text/css"/>
<link href="../css/gestion.css" rel="stylesheet">
<style>
html body, form {
	padding: 0px;
	margin: 0px;
	border: 0px;
}
html body {
	overflow: hidden;
}
</style>
<title>Documento de imagenes</title>
<script language="javascript" src="../js/imageview.js"></script>
<%	String strIdx = (String) session.getAttribute(ViewerParametersInterface.SEL_INDEX);
	int selIdx = -1;
	if (strIdx != null)
		selIdx = Integer.parseInt(strIdx);

	session.removeAttribute(ViewerParametersInterface.SEL_INDEX);
	Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
	String idNode = request.getParameter("select");
	int outputIndex = -1;
	int outputMaxIndex = 0;
	
	String indexVal = request.getParameter("image.index");
	if (indexVal != null)
		outputIndex = Integer.parseInt(indexVal);

	String strMaxIndex = (String) session.getAttribute(ViewerParametersInterface.INDEX_MAX);
	if (strMaxIndex == null)
		strMaxIndex = request.getParameter(ViewerParametersInterface.INDEX_MAX);
	if (strMaxIndex != null)
		outputMaxIndex = Integer.parseInt(strMaxIndex);
%><script language="JavaScript">
<!--
function selectOption() {<%if (selIdx != -1) {%>
	var sel = document.getElementById("docSel");
	sel.selectedIndex = <%=selIdx%><%}%>
}
function getNodeSelect() {
	return "<%=idNode%>";
}

function getPageActual(){
	return "<%=outputIndex%>";
}
function distribuye() {
	var frm = document.getElementById("disPage");
	var sidx = document.getElementById("selIdx");
	var sel = document.getElementById("docSel");
	var docId = document.getElementById("docId");
	sidx.value = sel.selectedIndex;
	if (sel.options[sel.selectedIndex].value != -1) {
		docId.value = sel.options[sel.selectedIndex].value;
		if (window.confirm("Desea copiar la página actual en el documento seleccionado?"))
			frm.submit();
	} else {
		window.alert("Debe seleccionar un documento");
		sel.focus();
	}
}
-->
</script>
</head>
<body onload="selectOption()">
<table width="100%" height="100%" cellpadding="0" cellspacing="0">
  <tr>
    <td height="1%" align="left">
      <table>
        <tr>
          <td>
            <table cellpadding="0" cellspacing="0">
              <tr>
                <td><a class="button" href="javascript:imageManager(1)"><img src="../images/escaner.gif" alt="Escanear" width="22" height="22" border="0"></a></td>
                <td><a class="button" href="javascript:imageManager(2)"><img src="../images/importar.gif" alt="Agregar foto" width="22" height="22" border="0"></a></td>
                <td>&nbsp;</td>
                <td><a class="button" href="javascript:imageManager(7)"><img src="../images/ir_primero.gif" alt="Primera" width="22" height="22" border="0"></a></td>
                <td><a class="button" href="javascript:imageManager(8)"><img src="../images/atras.gif" alt="Anterior" width="22" height="22" border="0"></a></td>
                <td><table cellpadding="0" cellspacing="0">
                    <tr>
                      <td><span>P&aacute;gina</span>&nbsp;</td>
                      <td><input name="currPage" type="text" readonly value="<%=outputIndex + 1%>" size="3" maxlength="3">
                      </td>
                      <td>&nbsp;<span>de</span>&nbsp;</td>
                      <td><input name="totPage" type="text" readonly value="<%=outputMaxIndex%>" size="3" maxlength="3">
                      </td>
                    </tr>
                  </table>
                <td><a class="button" href="javascript:imageManager(9)"><img src="../images/adelante.gif" alt="Siguiente" width="22" height="22" border="0"></a></td>
                <td><a class="button" href="javascript:imageManager(10)"><img src="../images/ir_ultimo.gif" alt="&Uacute;ltima" width="22" height="22" border="0"></a></td>
              </tr>
            </table>
          </td>
          <td>&nbsp;</td>
          <td>
            <table cellpadding="0" cellspacing="0">
              <tr>
                <td><a class="button" href="javascript:imageManager(11)"><img src="../images/zoom_mas.gif" alt="Zoom +" width="22" height="22" border="0"></a></td>
                <td><a class="button" href="javascript:imageManager(12)"><img src="../images/zoom_menos.gif" alt="Zoom -" width="22" height="22" border="0"></a></td>
                <td>&nbsp;</td>
                <td><a class="button" href="javascript:imageManager(13)"><img src="../images/rotar_izq.gif" alt="Rotar Izquierda" width="22" height="22" border="0"></a></td>
                <td><a class="button" href="javascript:imageManager(14)"><img src="../images/rotar_der.gif" alt="Rotar derecha" width="22" height="22" border="0"></a></td>
                <td>&nbsp;</td>
                <td><a class="button" href="javascript:imageManager(15)"><img src="../images/restablecer.gif" alt="Restablecer" width="22" height="22" border="0"></a></td>
                <td>&nbsp;</td>
                <td><a href="javascript:imageManager(16)"><img src="../images/eliminar.gif" alt="Eliminar foto" width="22" height="22" border="0"></a></td>
              </tr>
            </table>
          </td>
        </tr>
      </table>
    </td>
  </tr>
  <tr>
    <td height="1%" align="left">
      <table>
        <tr>
          <td>
            <select id="docSel"><%if (c.getTipoCaso().getDocumentos().isEmpty()) {%>
              <option value="-1">Vacio</option><%} else { %>
              <option value="-1">&lt;Seleccione&gt;</option><%
              for (Iterator iter = c.getTipoCaso().getDocumentos().iterator(); iter.hasNext();) {
                TipoDocumento tc = (TipoDocumento) iter.next();%>  <option value="<%=tc.getIdTipoDocto()%>"><%=tc.getNombreTipoDocto()%></option>
              <%}}%></select>
          </td>
          <td><input type="button" value="Copiar" onclick="distribuye()" title="Copia página a documento seleccionado"></td>
        </tr>
      </table>
    </td>
  </tr>
  <tr>
    <td height="98%">
      <div id="imgDiv" style="position: absolute; width: 100%; height: 100%; overflow: auto; border: 1px solid #333399;">
        <form name="mngPage" id="mngPage" action="VisualizadorDeImagen.jsp?select=<%=idNode%>&image.index=<%=outputIndex%>" method="post">
          <image:imageviewer id="imgView" param="select" quality="1"/>
        </form>
        <form name="delPage" id="delPage" target="_self" action="../imgmng/delpagekeeper?select=<%=idNode%>&image.index=<%=outputIndex%>" method="post" style="display: none;"></form>
        <form name="disPage" id="disPage" target="_self" action="../imgmng/dispagekeeper" method="post" style="display: none;">
          <input type="hidden" id="srcNode" name="srcNode" value="<%=idNode%>">
          <input type="hidden" id="srcIdx" name="srcIdx" value="<%=outputIndex%>">
          <input type="hidden" id="selIdx" name="selIdx">
          <input type="hidden" id="docId" name="docId">
        </form>
      </div>
    </td>
  </tr>
</table>
</body>
</html>
<%	String loadType = request.getParameter("image.load");
	if (loadType != null) {%><script language="javascript">
<%	if ("last".equals(loadType)) {
%>updateImagesList(parent.listFrame.document.getElementById("paginate"), parseInt(document.getElementsByName("totPage")[0].value) - 1);
<%	} else if ("current".equals(loadType)){
%>updateImagesList(parent.listFrame.document.getElementById("paginate"), <%=Integer.parseInt(indexVal)%>);
<%	} %></script><% } %>
