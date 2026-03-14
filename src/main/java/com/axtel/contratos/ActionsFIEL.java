package com.axtel.contratos;

import java.util.List;
import java.util.Map;
import com.syc.gestion.core.Usuario;
import com.syc.sai.firmaElectronica.interfaces.SolicitudFirmaElectronica;
import java.util.Base64;

public interface ActionsFIEL {

    public List<String> sign(SolicitudFirmaElectronica sfe, String cerFileName, String keyFileName) throws Exception;

    public List<String> reject(Usuario u, Map<String, String> objMap) throws Exception;

    public List<String> aceptProcess(Usuario u, Map<String, String> objMap) throws Exception;
}
