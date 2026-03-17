<%@ page import="com.jenkov.prizetags.tree.itf.ITreeIteratorElement"%>
<%@page import="com.syc.fortimax.core.GetDatosNodo"%>
<%@page import="com.syc.fortimax.core.DocumentoManager"%>
<%@page import="com.syc.fortimax.core.Documento"%>
<%@page import="com.syc.gestion.core.NodeInformation"%>
<%@ taglib uri="/WEB-INF/tlds/treetag.tld" prefix="tree"%>
<%@ taglib uri="/WEB-INF/tlds/requesttags.tld" prefix="request" %>
<%@ taglib uri="/WEB-INF/tlds/ajaxtags.tld" prefix="ajax" %>

<script type="text/javascript" src="../js/jenkov_ajaxScript.js"></script>

<table   style="font-family:Arial;">
	<tree:tree tree="tree.model" node="tree.node" includeRootNode="true">
		<tr>
			<td nowrap="nowrap">
				<table  >
					<tr>
				<tree:nodeIndent node="tree.node" indentationType="type">
					<tree:nodeIndentVerticalLine indentationType="type">
						<td><img src="../images/tree/verticalLine.gif"></td>
					</tree:nodeIndentVerticalLine>
					<tree:nodeIndentBlankSpace indentationType="type">
						<td><img src="../images/tree/blankSpace.gif"></td>
					</tree:nodeIndentBlankSpace>
				</tree:nodeIndent>
				<tree:nodeMatch node="tree.node" hasChildren="true" expanded="false" isLastChild="false">
						<td><ajax:link targetElement="treeContainer"><a href="arbol.jsp?expand=<tree:nodeId node="tree.node"/>"><img src="../images/tree/collapsedMidNode.gif" border="0"></a></ajax:link></td>
				</tree:nodeMatch>
				<tree:nodeMatch node="tree.node" hasChildren="true" expanded="true" isLastChild="false">
						<td><ajax:link targetElement="treeContainer"><a href="arbol.jsp?collapse=<tree:nodeId node="tree.node"/>"><img src="../images/tree/expandedMidNode.gif" border="0"></a></ajax:link></td>
				</tree:nodeMatch>
				<tree:nodeMatch node="tree.node" hasChildren="true" expanded="false" isLastChild="true">
						<td><ajax:link targetElement="treeContainer"><a href="arbol.jsp?expand=<tree:nodeId node="tree.node"/>"><img src="../images/tree/collapsedLastNode.gif" border="0"></a></ajax:link></td>
				</tree:nodeMatch>
				<tree:nodeMatch node="tree.node" hasChildren="true" expanded="true" isLastChild="true">
						<td><ajax:link targetElement="treeContainer"><a href="arbol.jsp?collapse=<tree:nodeId node="tree.node"/>"><img src="../images/tree/expandedLastNode.gif" border="0"></a></ajax:link></td>
				</tree:nodeMatch>
				<tree:nodeMatch node="tree.node" hasChildren="false" isLastChild="false">
						<td><img src="../images/tree/noChildrenMidNode.gif" border="0"></td>
				</tree:nodeMatch>
				<tree:nodeMatch node="tree.node" hasChildren="false" isLastChild="true">
						<td><img src="../images/tree/noChildrenLastNode.gif" border="0"></td>
				</tree:nodeMatch>
				<tree:nodeMatch node="tree.node" hasChildren="true" expanded="true">
						<td><img src="../images/tree/openFolder.gif"  alt="<tree:nodeToolTip node="tree.node"/>"></td>
				</tree:nodeMatch>
				<tree:nodeMatch node="tree.node" hasChildren="true" expanded="false">
						<td><img src="../images/tree/closedFolder.gif" alt="<tree:nodeToolTip node="tree.node"/>"></td>
				</tree:nodeMatch>
				<tree:nodeMatch node="tree.node" hasChildren="false" expanded="false" type="carpeta.*">
						<td><img src="../images/tree/closedFolder.gif" alt="<tree:nodeToolTip node="tree.node"/>"></td>
				</tree:nodeMatch>
				<tree:nodeMatch node="tree.node" hasChildren="false" type="docto.frtimx">
						<td><img src="../images/tree/nonFolderFrtImx.gif" alt="<tree:nodeToolTip node="tree.node"/>"></td>
				</tree:nodeMatch>
				<tree:nodeMatch node="tree.node" hasChildren="false" type="docto.externo">
						<td><img src="../images/tree/nonFolder.gif" alt="<tree:nodeToolTip node="tree.node"/>"></td>
				</tree:nodeMatch>
				<tree:nodeMatch node="tree.node" selected="false" type="carpeta.*">
						<td nowrap="nowrap"><ajax:link targetElement="treeContainer"><a href="arbol.jsp?select=<tree:nodeId node="tree.node"/>" title="<tree:nodeToolTip node="tree.node"/>"><tree:nodeName node="tree.node" /></a></ajax:link></td>
				</tree:nodeMatch>
				<tree:nodeMatch node="tree.node" selected="true" type="carpeta.*">
						<td nowrap="nowrap"><strong title="<tree:nodeToolTip node="tree.node"/>"><tree:nodeName node="tree.node" /></strong></td>
						<%	ITreeIteratorElement n = (ITreeIteratorElement)request.getAttribute("tree.node"); 
							if ("Sin Expediente".equals(n.getName())) {
								if(n!=null&&n.getName()!=null&&n.getNode()!=null&&n.getNode().getObject()!=null){%>
								<script type="text/javascript">parent.acciones('all','<tree:nodeId node="tree.node"/>','<%=((NodeInformation)n.getNode().getObject()).getNombreUsuario()%>');</script>
						<%		}
							} else {%>
							<script type="text/javascript">if(parent.acciones!=null)parent.acciones('fld','<tree:nodeId node="tree.node"/>','<%=((NodeInformation)n.getNode().getObject()).getNombreUsuario()%>');</script>
						<%	}%>
				</tree:nodeMatch>
				<tree:nodeMatch node="tree.node" selected="false" type="docto.*">
						<td nowrap="nowrap"><ajax:link targetElement="treeContainer"><a href="arbol.jsp?select=<tree:nodeId node="tree.node"/>" title="<tree:nodeToolTip node="tree.node"/>"><tree:nodeName node="tree.node" /></a></ajax:link></td>
				</tree:nodeMatch>
				<tree:nodeMatch node="tree.node" selected="true" type="docto.externo">
						<td nowrap="nowrap"><a href="../filestore?select=<tree:nodeId node="tree.node"/>" target="main" title="<tree:nodeToolTip node="tree.node"/>"><strong><tree:nodeName node="tree.node" /></strong></a></td>
						<%	ITreeIteratorElement n = (ITreeIteratorElement)request.getAttribute("tree.node");
						%>
						<script type="text/javascript">parent.acciones('doc','<tree:nodeId node="tree.node"/>','<%=((NodeInformation)n.getNode().getObject()).getNombreUsuario()%>');</script>
				</tree:nodeMatch>
				<tree:nodeMatch node="tree.node" selected="true" type="docto.frtimx">
						<td nowrap="nowrap"><a href="../imgmng/image-viewer.jsp?select=<tree:nodeId node="tree.node"/>" target="main" title="<tree:nodeToolTip node="tree.node"/>"><strong><tree:nodeName node="tree.node" /></strong></a></td>
						<td>&nbsp;<a href="../filedownload?select=<tree:nodeId node="tree.node"/>&zip=true"><img src="../images/b_recuperar.gif" title="Descargar a mi PC" width="24" height="22"></a></td>
						<%	ITreeIteratorElement n = (ITreeIteratorElement)request.getAttribute("tree.node");
						%>
						<script type="text/javascript">parent.acciones('doc','<tree:nodeId node="tree.node"/>','<%=((NodeInformation)n.getNode().getObject()).getNombreUsuario()%>');</script>
				</tree:nodeMatch>
					</tr>
				</table>
			</td>
		</tr>
	</tree:tree>
</table>
