package com.syc.web.filters;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.syc.gestion.servlet.GestionInterface;

public class ResponseHeaderFilter implements Filter {
	private static final Logger							log		= Logger.getLogger(ResponseHeaderFilter.class);

	FilterConfig										fc;
	private static final SystemQueryLogBusinessLogic	sqlbl	= new SystemQueryLogBusinessLogic(GestionInterface.ATT_CONEXION);

	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		HttpServletResponse response = (HttpServletResponse) res;
		HttpServletRequest request = (HttpServletRequest) req;

		String uri = request.getRequestURI();

		if (!(uri.endsWith(".css") || uri.endsWith(".js") || uri.endsWith(".png") || uri.endsWith(".gif") || uri.endsWith(".jpg"))) {
			sqlbl.insertLog(request);
		}
		
		// Asigna los parametros de respuesta HTTP
		for (Enumeration e = fc.getInitParameterNames(); e.hasMoreElements();) {
			String headerName = (String) e.nextElement();
			response.addHeader(headerName, fc.getInitParameter(headerName));

		}

		// Forzar modo de comptatibilidad en todos los IE
		response.setHeader("X-UA-Compatible", "IE=EmulateIE7");
		// response.setCharacterEncoding("ISO-8859-1"); //Ethiel, se agrega para
		// corregir el error de las  
		// response.setLocale(new java.util.Locale("es_MX"));
		// req.setCharacterEncoding("ISO-8859-1");

		// Pasa los encabezados asignados
		chain.doFilter(req, response);
	}

	public void init(FilterConfig filterConfig) {
		this.fc = filterConfig;
	}

	public void destroy() {
		this.fc = null;
	}
}
