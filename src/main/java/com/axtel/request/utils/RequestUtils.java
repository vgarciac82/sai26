package com.axtel.request.utils;
import java.nio.charset.StandardCharsets;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload2.core.FileItem;
import com.axtel.request.MultipartObject;
import com.syc.gestion.util.Util;
import java.util.Base64;

public class RequestUtils {

    private static Iterator<?> parse(HttpServletRequest req, String tempDir, long maxFileSize) throws ServletException {
        List<?> fileItems = Util.parseRequest(req, tempDir, -1);
        Iterator<?> iter = fileItems.iterator();
        return iter;
    }

    public static MultipartObject processMultiPart(HttpServletRequest req, String tempDir, long maxFileSize) throws IOException, ServletException {
        Iterator<?> iter = parse(req, tempDir, maxFileSize);
        MultipartObject result = new MultipartObject();
        final File TEMP_DIR = new File(tempDir);
        if (!TEMP_DIR.exists())
            TEMP_DIR.mkdirs();
        while (iter.hasNext()) {
            FileItem item = (FileItem) iter.next();
            if (item.isFormField()) {
                result.getParams().put(item.getFieldName(), item.getString(java.nio.charset.StandardCharsets.UTF_8));
                item.delete();
                continue;
            }
            String nombreArchivo = item.getName();
            String extension = Util.getFileExtencion(nombreArchivo);
            File f = File.createTempFile("tmp_", "." + extension, TEMP_DIR);
            DataInputStream archivoCargaStream = new DataInputStream(item.getInputStream());
            result.getFiles().put(item.getFieldName(), f);
            Util.copiaArchivo(archivoCargaStream, f.getAbsoluteFile());
            archivoCargaStream.close();
            item.delete();
        }
        return result;
    }

    public static MultipartArrayObject processArrayMultiPart(HttpServletRequest req, String tempDir, long maxFileSize) throws IOException, ServletException {
        Iterator<?> iter = parse(req, tempDir, maxFileSize);
        MultipartArrayObject result = new MultipartArrayObject();
        final File TEMP_DIR = new File(tempDir);
        if (!TEMP_DIR.exists())
            TEMP_DIR.mkdirs();
        while (iter.hasNext()) {
            FileItem item = (FileItem) iter.next();
            if (item.isFormField()) {
                if (result.getParams().get(item.getFieldName()) == null)
                    result.getParams().put(item.getFieldName(), new ArrayList<>());
                result.getParams().get(item.getFieldName()).add(item.getString(java.nio.charset.StandardCharsets.UTF_8));
                item.delete();
                continue;
            }
            String nombreArchivo = item.getName();
            String extension = Util.getFileExtencion(nombreArchivo);
            File f = File.createTempFile("tmp_", "." + extension, TEMP_DIR);
            DataInputStream archivoCargaStream = new DataInputStream(item.getInputStream());
            if (result.getFiles().get(item.getFieldName()) == null)
                result.getFiles().put(item.getFieldName(), new ArrayList<>());
            result.getFiles().get(item.getFieldName()).add(f);
            Util.copiaArchivo(archivoCargaStream, f.getAbsoluteFile());
            archivoCargaStream.close();
            item.delete();
        }
        return result;
    }
}
