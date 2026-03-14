package com.syc.gestion.custom;

import java.io.IOException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.syc.gestion.core.GestionException;
import java.util.Base64;

public interface GenericInterface {

    public void execute(HttpServletRequest req, HttpServletResponse resp, String jniName, String action) throws GestionException, IOException;
}
