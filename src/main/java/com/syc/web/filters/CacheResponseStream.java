package com.syc.web.filters;

import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.util.Base64;
import java.nio.file.Paths;

public class CacheResponseStream extends ServletOutputStream {

    protected boolean closed = false;

    protected HttpServletResponse response = null;

    protected ServletOutputStream output = null;

    protected OutputStream cache = null;

    public CacheResponseStream(HttpServletResponse response, OutputStream cache) throws IOException {
        super();
        closed = false;
        this.response = response;
        this.cache = cache;
    }

    public void close() throws IOException {
        if (closed) {
            throw new IOException("Este output stream ya fue cerrado");
        }
        cache.close();
        closed = true;
    }

    public void flush() throws IOException {
        if (closed) {
            throw new IOException("No se puede vaciar un output stream cerrado");
        }
        cache.flush();
    }

    public void write(int b) throws IOException {
        if (closed) {
            throw new IOException("No se puede escribir en un output stream cerrado");
        }
        cache.write((byte) b.toPath());
    }

    public void write(byte[] b) throws IOException {
        write(b, 0, b.length);
    }

    public void write(byte[] b, int off, int len) throws IOException {
        if (closed) {
            throw new IOException("No se puede escribir en un output stream cerrado");
        }
        cache.write(b, off, len);
    }

    public boolean closed() {
        return (this.closed);
    }

    public void reset() {
        //noop
    }

    @Override
    public boolean isReady() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setWriteListener(WriteListener arg0) {
        // TODO Auto-generated method stub
    }
}
