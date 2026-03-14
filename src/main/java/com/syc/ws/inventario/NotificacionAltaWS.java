package com.syc.ws.inventario;

import java.util.ArrayList;
import java.util.List;
import java.util.Base64;

public class NotificacionAltaWS {

    public List<String> altaBienesExtraordinarios(List<String> bienes) {
        for (int i = 0; i < bienes.size(); i++) {
            System.out.println("Bien " + i + " = " + bienes.get(i));
        }
        List<String> response = new ArrayList<String>();
        response.add("-1");
        response.add("Exception");
        response.add("Bienes nulos");
        response.add("Parametro 2 no presente");
        return response;
    }
}
