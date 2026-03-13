package com.axtel.egresos.finDeAnio;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import com.axtel.egresos.finDeAnio.procesosFinAnioManager;
import com.syc.contable.PagosDirectosBussinessLogic;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("unused")
public class procesosFinAnioBussinessLogic extends DataSourceManager {

    private static Logger log = LoggerFactory.getLogger(PagosDirectosBussinessLogic.class);

    public procesosFinAnioBussinessLogic(String jniName) {
        super.init(jniName);
    }

    public void buscaPagos(String listaEvento, String listaFolios, String listaPagos, HttpServletResponse resp, Map<String, String> plantillas, String sUsuario) throws Exception {
        String file = null;
        Connection conn = null;
        try {
            conn = getConnection();
            log.info("Generando layout para los folios: [" + listaFolios + "]");
            file = procesosFinAnioManager.BuscaPagos(conn, listaEvento, listaFolios, listaPagos, plantillas);
            log.debug("Layout generado exitosamente");
            procesosFinAnioManager.bitacora(conn, listaEvento, listaFolios, listaPagos, sUsuario);
            conn.commit();
            File f = new File(file);
            resp.setContentType("application/vnd.ms-excel");
            resp.addHeader("Content-Disposition", "inline; filename=\"" + f.getName() + "\"; ");
            ServletOutputStream out = resp.getOutputStream();
            Util.doDownload(out, file, file, "");
            out.flush();
            out.close();
            f = null;
        } finally {
            CloseObject.closeObject(conn, false);
            if (file != null) {
                File f = new File(file);
                if (!f.delete())
                    f.deleteOnExit();
            }
        }
    }
}
