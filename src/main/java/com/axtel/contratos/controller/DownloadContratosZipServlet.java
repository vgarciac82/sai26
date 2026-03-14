package com.axtel.contratos.controller;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebInitParam;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.syc.contable.ContratoDiversoBusinessLogic;

@WebServlet(name = "DownloadContratosZipServlet", urlPatterns = { "/contratos/downloadzip" }, initParams = { @WebInitParam(name = "jndiName", value = "jdbc/gestion") })
public class DownloadContratosZipServlet extends HttpServlet {

    private static final long serialVersionUID = -3079222475340548716L;

    private ContratoDiversoBusinessLogic cdbl;

    @Override
    public void init() throws ServletException {
        String jndiName = getServletConfig().getInitParameter("jndiName");
        cdbl = new ContratoDiversoBusinessLogic(jndiName);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String contractList = request.getParameter("contracts");
        if (contractList == null || contractList.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parámetro 'contracts' obligatorio.");
            return;
        }
        response.setContentType("application/zip");
        response.setHeader("Content-Disposition", "attachment; filename=contratos.zip");
        //		cdbl.downloadContractFiles( contractList, response.getOutputStream() );
    }
}
