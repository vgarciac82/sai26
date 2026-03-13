package com.syc.gestion.core;

import javax.servlet.http.HttpServletRequest;

public class URLDocumento {

	private String idNode;
	private boolean isImaxfile;

	public URLDocumento(String idNode, boolean isImaxFile) {
		this.idNode = idNode;
		this.isImaxfile = isImaxFile;
	}

	public String getURL(HttpServletRequest req) {

		String schema = req.getScheme() + "://";
		String server = req.getServerName();
		String port = (req.getServerPort() == 80) ? "" : ":" + req.getServerPort();
		String context = req.getContextPath();
		String path = (isImaxfile) ? "imgmng/image-viewer.jsp" : "filestore";
		String qryStr = "?select=" + idNode;

		return schema + server + port + context + "/" + path + qryStr;
	}
}
