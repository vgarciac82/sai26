package com.syc.web.filters;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class CacheResponseWrapper extends HttpServletResponseWrapper {
	protected HttpServletResponse origResponse = null;
	protected ServletOutputStream stream = null;
	protected PrintWriter writer = null;
	protected OutputStream cache = null;

	public CacheResponseWrapper(HttpServletResponse response, OutputStream cache) {
		super(response);
		origResponse = response;
		this.cache = cache;
	}

	public ServletOutputStream createOutputStream() throws IOException {
		return (new CacheResponseStream(origResponse, cache));
	}

	public void flushBuffer() throws IOException {
		stream.flush();
	}

	public ServletOutputStream getOutputStream() throws IOException {
		if (writer != null) {
			throw new IllegalStateException("getWriter() ya fue llamado antes!");
		}

		if (stream == null)
			stream = createOutputStream();
		return (stream);
	}

	public PrintWriter getWriter() throws IOException {
		if (writer != null) {
			return (writer);
		}

		if (stream != null) {
			throw new IllegalStateException("getOutputStream() ya fue llamado antes!");
		}

		stream = createOutputStream();
		// BUG FIX 2003-12-01 Reuse content's encoding, don't assume UTF-8
		//   writer = new PrintWriter(new OutputStreamWriter(stream, "UTF-8"));
		writer =
			new PrintWriter(new OutputStreamWriter(stream, origResponse.getCharacterEncoding()));
		return (writer);
	}
}
