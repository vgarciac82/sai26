package com.syc.web.filters;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

public class SystemQueryLog {
	
	private int		contentLength;
	private String	contentType;
	private String	localAddr;
	private String	locale;
	private String	localName;
	private int		localPort;
	private String	method;
	private String	pathInfo;
	private String	pathTranslated;
	private String	protocol;
	private Date	queryDate;
	private String	queryString;
	private String	remoteAddr;
	private String	remoteHost;
	private int		remotePort;
	private String	remoteUser;
	private String	requestedSessionId;
	private String	scheme;
	private String	serverName;
	private String	servletPath;
	private long	systemTime;
	private String	uri;

	public int getContentLength() {
		return contentLength;
	}

	public String getContentType() {
		return contentType;
	}

	public String getLocalAddr() {
		return localAddr;
	}

	public String getLocale() {
		return locale;
	}
	public String getLocalName() {
		return localName;
	}

	public int getLocalPort() {
		return localPort;
	}

	public String getMethod() {
		return method;
	}

	public String getPathInfo() {
		return pathInfo;
	}

	public String getPathTranslated() {
		return pathTranslated;
	}

	public String getProtocol() {
		return protocol;
	}

	public Date getQueryDate() {
		return queryDate;
	}

	public String getQueryString() {
		return queryString;
	}

	public String getRemoteAddr() {
		return remoteAddr;
	}

	public String getRemoteHost() {
		return remoteHost;
	}

	public int getRemotePort() {
		return remotePort;
	}

	public String getRemoteUser() {
		return remoteUser;
	}

	public String getRequestedSessionId() {
		return requestedSessionId;
	}

	public String getScheme() {
		return scheme;
	}

	public String getServerName() {
		return serverName;
	}

	public String getServletPath() {
		return servletPath;
	}

	public long getSystemTime() {
		return systemTime;
	}

	public String getUri() {
		return uri;
	}

	public void setContentLength(int contentLength) {
		this.contentLength = contentLength;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public void setLocalAddr(String localAddr) {
		this.localAddr = localAddr;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public void setLocalName(String localName) {
		this.localName = localName;
	}

	public void setLocalPort(int localPort) {
		this.localPort = localPort;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public void setPathInfo(String pathInfo) {
		this.pathInfo = pathInfo;
	}

	public void setPathTranslated(String pathTranslated) {
		this.pathTranslated = pathTranslated;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public void setQueryDate(Date queryDate) {
		this.queryDate = queryDate;
	}

	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}

	public void setRemoteAddr(String remoteAddr) {
		this.remoteAddr = remoteAddr;
	}

	public void setRemoteHost(String remoteHost) {
		this.remoteHost = remoteHost;
	}

	public void setRemotePort(int remotePort) {
		this.remotePort = remotePort;
	}

	public void setRemoteUser(String remoteUser) {
		this.remoteUser = remoteUser;
	}

	public void setRequestedSessionId(String requestedSessionId) {
		this.requestedSessionId = requestedSessionId;
	}

	public void setScheme(String scheme) {
		this.scheme = scheme;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public void setServletPath(String servletPath) {
		this.servletPath = servletPath;
	}

	public void setSystemTime(long systemTime) {
		this.systemTime = systemTime;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}
	
	public static SystemQueryLog instanceFromRequest(HttpServletRequest request) {
		SystemQueryLog sql = new SystemQueryLog();

		sql.setUri(request.getRequestURI());
		sql.setContentType(request.getContentType());
		sql.setLocalName(request.getLocalName());
		sql.setMethod(request.getMethod());
		sql.setPathInfo(request.getPathInfo());
		sql.setPathTranslated(request.getPathTranslated());
		sql.setProtocol(request.getProtocol());
		sql.setQueryString(request.getQueryString());
		sql.setRemoteAddr(request.getRemoteAddr());
		sql.setRemoteHost(request.getRemoteHost());
		sql.setRemotePort(request.getRemotePort());
		sql.setRemoteUser(request.getRemoteUser());
		sql.setRequestedSessionId(request.getRequestedSessionId());
		sql.setScheme(request.getScheme());
		sql.setServerName(request.getServerName());
		sql.setServletPath(request.getServletPath());
		sql.setContentLength(request.getContentLength());
		sql.setLocale(request.getLocale() == null ? "" : request.getLocale().getDisplayName());
		sql.setLocalPort(request.getLocalPort());
		sql.setSystemTime(System.currentTimeMillis());
		sql.setQueryDate( new Date() );
		sql.setLocalAddr( request.getLocalAddr() );
		return sql;
	}
}
