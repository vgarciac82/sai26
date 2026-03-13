package com.syc.gestion.custom;

import java.io.File;
import javax.servlet.http.HttpServletRequest;
import com.syc.fortimax.core.Fortimax;
import com.syc.gestion.CasoBusinessLogic;
import com.syc.gestion.core.GestionException;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.viewer.custom.ImageViewerInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImageViewerGestion implements GestionInterface, ImageViewerInterface {

    private static Logger log = LoggerFactory.getLogger(ImageViewerGestion.class);

    private String jniName = null;

    private Fortimax f = null;

    public void init(HttpServletRequest req, String jniName) {
        if (jniName == null)
            throw new RuntimeException("jniName no puede ser nulo");
        this.jniName = jniName;
        String select = req.getParameter("select");
        if (select == null) {
            log.warn("No se recibio parametro 'select'");
            throw new RuntimeException("No se recibio parametro 'select'");
        }
        f = new Fortimax(select);
    }

    public File[] getFilesList() {
        File[] files = new File[0];
        CasoBusinessLogic cbl = new CasoBusinessLogic(jniName);
        try {
            files = cbl.getArchivosDocumento(f.getTituloAplicacion(), f.getIdGabinete(), f.getIdCarpeta(), f.getIdDocumento());
        } catch (GestionException exc) {
            files = new File[0];
            log.warn("Recuperando Archivos", exc);
        }
        return files;
    }
}
