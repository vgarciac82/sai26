package com.syc.gestion;

import java.sql.Connection;
import com.syc.dsmngr.DataSourceManager;
import com.syc.gestion.core.BitacoraCorreo;
import com.syc.gestion.core.BitacoraCorreoManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class BitacoraCorreosBusinessLogic extends DataSourceManager {

    private static final Logger log = LoggerFactory.getLogger(BitacoraCorreosBusinessLogic.class);

    public BitacoraCorreosBusinessLogic(String jniName) {
        super.init(jniName);
        log.debug("Correos Pendientes Business Logic");
    }

    public BitacoraCorreosBusinessLogic() {
        log.debug("Correos Pendientes Business Logic");
    }

    public int insertaBitacoraCorreo(BitacoraCorreo bc) throws Exception {
        return insertaBitacoraCorreo(bc.getDestinatarios(), bc.getMensaje(), bc.getSubject(), bc.getEstatus());
    }

    private int insertaBitacoraCorreo(String destinatario, String mensaje, String subject, String estatus) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            BitacoraCorreo bc = new BitacoraCorreo();
            bc.setDestinatarios(destinatario);
            bc.setMensaje(mensaje);
            bc.setSubject(subject);
            bc.setEstatus(estatus);
            BitacoraCorreoManager.create(conn, bc);
            conn.commit();
            return 1;
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Problemas realizando rollback: " + e2.toString(), e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn, false);
        }
    }
}
