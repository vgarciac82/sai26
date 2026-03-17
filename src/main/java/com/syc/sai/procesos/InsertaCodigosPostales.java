package com.syc.sai.procesos;
import java.nio.file.Paths;

import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.Statement;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import java.util.Base64;

public class InsertaCodigosPostales {

    private String fileOrigen;

    private int insertados = 0;

    public InsertaCodigosPostales(String file) {
        this.fileOrigen = file;
    }

    public static void main(String[] args) {
        String file = args[0];
        InsertaCodigosPostales icp = new InsertaCodigosPostales(file);
        icp.insertaCP();
    }

    private void insertaCP() {
        try {
            Files.lines(Paths.get(this.fileOrigen)).forEach(this::insertaRenglon);
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
        }
    }

    private void insertaRenglon(String cmd) {
        Connection conn = null;
        Statement smt = null;
        try {
            System.out.println("Insertando Renglon: " + insertados + " CMD: " + cmd);
            conn = Util.getStandAloneConnection();
            smt = conn.createStatement();
            smt.executeUpdate(cmd);
            insertados++;
            conn.commit();
        } catch (Exception e) {
            Util.rollback(conn);
            System.out.println("No se ejecuto el comando [" + cmd + "] debido al error: " + e.toString());
        } finally {
            CloseObject.closeObject(smt);
            CloseObject.closeObject(conn);
        }
    }
}
