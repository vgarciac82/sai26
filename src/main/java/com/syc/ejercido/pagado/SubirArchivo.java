package com.syc.ejercido.pagado;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.json.JSONObject;
import com.syc.cfdi.utils.CloseObject;
import com.syc.contable.core.AplicacionContable;
import com.syc.dsmngr.DataSourceManager;
import sun.net.www.URLConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class SubirArchivo extends DataSourceManager {

    private static Logger log = LoggerFactory.getLogger(AplicacionContable.class);

    public JSONObject envioArchivo(String nombreArchivo) throws SQLException, Exception {
        Connection conn = null;
        CallableStatement clc = null;
        PreparedStatement ps = null, pss = null, psSICOP = null, psPAGO = null;
        JSONObject json = new JSONObject();
        //String mensaje = "";
        //String nombreTabla = "";
        String ruta = "";
        try {
            conn = getConnection();
            if (nombreArchivo.equals("CLC_SICOP")) {
                ruta = "C:/SubirArchivo/CLC_SICOP.csv";
            } else if (nombreArchivo.equals("CLC_SICOP_RETENCION")) {
                ruta = "C:/SubirArchivo/CLC_SICOP_RETENCION.csv";
            } else if (nombreArchivo.equals("CLC_SIAFF_ENC")) {
                ruta = "C:/SubirArchivo/CLC_SIAFF_ENC.csv";
            } else if (nombreArchivo.equals("CLC_SICOP_PAGO")) {
                ruta = "C:/SubirArchivo/CLC_SICOP_PAGO.csv";
            }
            System.out.println(nombreArchivo + " / " + ruta);
            clc = conn.prepareCall(" { CALL SubirArchivo (?, ?) } ");
            clc.setString(1, nombreArchivo);
            clc.setString(2, ruta);
            int res = clc.executeUpdate();
            System.out.println("Registros: " + res);
            if (res > 0) {
                json.put("estatus", "correcto");
                System.out.println("correcto:" + res);
                if (nombreArchivo.equals("CLC_SICOP_PAGO")) {
                    /**
                     * SCRIPTS DE JANISE **
                     */
                    psSICOP = conn.prepareStatement("DELETE CLC_SICOP WHERE DOC_HAPLICADO IN ('2','0')");
                    psSICOP.executeUpdate();
                    psPAGO = conn.prepareStatement("DELETE dbo.CLC_SICOP_PAGO WHERE LEN(COD_SEMARNAT_2) < 12 OR COD_SEMARNAT_2 IS NULL OR SUBSTRING(COD_SEMARNAT_2,3,2) = 'DD'");
                    psPAGO.executeUpdate();
                    /**
                     * *********************
                     */
                    ps = conn.prepareStatement("UPDATE CLC_SICOP SET NCTR_47 = COD_SEMARNAT_2 FROM CLC_SICOP_PAGO WHERE SPAG_176 = NO_DOCTO and ISNULL(NCTR_47,'') <> COD_SEMARNAT_2 AND PRCS_CLAVE NOT LIKE ('%AJENAS%') AND  TDOC_71 !=('NOM')  AND COD_SEMARNAT_2 NOT LIKE '%NC%' AND COD_SEMARNAT_2 NOT LIKE '%CO%' AND COD_SEMARNAT_2 NOT LIKE '%AI%'");
                    ps.executeUpdate();
                    /**
                     * Update Para Actualizar las NC Nomina **
                     */
                    pss = conn.prepareStatement("UPDATE SICOP SET SICOP.NCTR_47 = pago.COD_SEMARNAT_2 FROM CLC_SICOP SICOP INNER JOIN CLC_SICOP_PAGO pago ON NO_DOCTO = SICOP.NCLC_43 WHERE COD_SEMARNAT_2 LIKE '10NC%' AND SICOP.TDOC_71 ='NOM' ");
                    pss.executeUpdate();
                    /**
                     * Update Para Actualizar las AI de Ajenas **
                     */
                    pss = conn.prepareStatement("UPDATE SICOP SET SICOP.NCTR_47 = pago.COD_SEMARNAT_2 FROM CLC_SICOP SICOP INNER JOIN CLC_SICOP_PAGO pago ON NO_DOCTO = SICOP.NCLC_43 WHERE SUBSTRING(COD_SEMARNAT_2,3,2) in ('AI','PI') AND SICOP.PRCS_CLAVE='WF_CLC_AJENAS_2C' ");
                    pss.executeUpdate();
                }
            } else {
                json.put("estatus", "incorrecto");
                System.out.println("incorrecto:" + res);
            }
            //clc.close();
            conn.commit();
        } catch (Exception ex) {
            log.warn("Error: Subir Archivo ", ex);
            try {
                json.put("estatus", "incorrecto");
            } catch (Exception ee) {
                log.warn("Error: ", ee);
            }
        } finally {
            CloseObject.closeObject(psSICOP, false);
            CloseObject.closeObject(psPAGO, false);
            CloseObject.closeObject(clc, false);
            CloseObject.closeObject(ps, false);
            CloseObject.closeObject(pss, false);
            CloseObject.closeObject(conn, false);
        }
        return json;
    }

    public JSONObject envioArchivoFTP(String nombreArchivo) {
        JSONObject json = new JSONObject();
        String pass = "";
        String localPath = "C:\\SubirArchivoRemote\\" + nombreArchivo + ".csv";
        String remotePath = "/" + nombreArchivo + ".csv";
        String user = "hector";
        //localhost
        String server = "127.0.0.1";
        InputStream inp = null;
        try {
            //URL url = new URL("ftp://" + user + ":" + pass + "@" + server + remotePath + ";type=i");
            URL url = new URL("ftp://" + user + ": @" + server + remotePath + ";type=i");
            URLConnection urlc = (URLConnection) url.openConnection();
            OutputStream os = urlc.getOutputStream();
            File fichero = new File(localPath);
            inp = new FileInputStream(fichero);
            byte[] bytes = new byte[1024];
            int readCount = 0;
            while ((readCount = inp.read(bytes)) > 0) {
                os.write(bytes, 0, readCount);
            }
            os.flush();
            os.close();
            inp.close();
            json.put("estatus", "correcto");
        } catch (Exception ex) {
            log.warn("Error occurred", "Error: Cerrando Conexion " + ex);
            try {
                json.put("estatus", "incorrecto");
            } catch (Exception e) {
                log.warn("Error occurred", "Error: Cerrando Conexion " + e);
            }
        }
        return json;
    }
}
