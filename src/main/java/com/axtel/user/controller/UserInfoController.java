package com.axtel.user.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.google.gson.JsonObject;
import com.syc.gestion.EmpleadoBusinessLogic;
import com.syc.gestion.core.Empleado;
import com.syc.gestion.core.EmpleadoArea;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;

@WebServlet("/user/info")
public class UserInfoController extends HttpServlet {

    private static final long serialVersionUID = -6157138843722747581L;

    private static final EmpleadoBusinessLogic ebl = new EmpleadoBusinessLogic(GestionInterface.ATT_CONEXION);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JsonObject objError = new JsonObject();
        try {
            objError = new JsonObject();
            HttpSession session = req.getSession(false);
            if (session == null) {
                objError.addProperty("error", "La sesion ha expirado");
                Util.sendJSONResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, objError);
                return;
            }
            Usuario u = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
            if (u == null) {
                objError.addProperty("error", "La sesion ha expirado");
                Util.sendJSONResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, objError);
                return;
            }
            Empleado e = new Empleado();
            e.setClaveUsuario(u.getLogin());
            e = ebl.getEmpleado(e);
            EmpleadoArea ea = new EmpleadoArea();
            ea.setId(e.getClaveArea());
            ea = ebl.getEmpleadoArea(ea);
            Map<String, String> userInfo = new HashMap<>();
            userInfo.put("login", u.getLogin());
            userInfo.put("nombre", u.getNombre());
            userInfo.put("puesto", e.getCargo());
            userInfo.put("unidadEjecutora", u.getU_UR());
            Util.sendJSON(resp, userInfo);
        } catch (Exception e) {
            objError.addProperty("error", e.toString());
        }
    }
}
