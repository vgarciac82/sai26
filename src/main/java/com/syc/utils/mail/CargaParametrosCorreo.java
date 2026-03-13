package com.syc.utils.mail;

import java.sql.Connection;
import com.syc.dsmngr.DataSourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CargaParametrosCorreo extends DataSourceManager {

    private static Logger log = LoggerFactory.getLogger(CargaParametrosCorreo.class);

    public boolean correoProduccion = false;

    private String jniName = null;

    public CargaParametrosCorreo(String jniName) {
        super.init(jniName);
    }

    public ParametrosCorreo CargaParametros(String TipoProceso) {
        ParametrosCorreo ParamMail = new ParametrosCorreo();
        Connection conn = null;
        try {
            conn = getConnection();
            //			CargaParametrosBD CPBD = new CargaParametrosBD();
            try {
                //			ParamMail = CPBD.getParameterDB(conn, TipoProceso );
            } catch (Exception e) {
                System.out.println("Error en facade de ParametrosCorreo ------> " + e.getMessage());
                e.fillInStackTrace();
            }
        } catch (Exception e) {
            System.out.println("Error en facade de ParametrosCorreo ------> " + e.getMessage());
            e.fillInStackTrace();
        }
        return ParamMail;
    }
}
