package com.syc.contable.caja;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CargaMasivaSNPManager {

    private static final Logger log = LoggerFactory.getLogger(CargaMasivaSNPManager.class);

    public static void generaReporteExpedientesFaltantes(Connection conn, List<String> filtros, String cUnidadEjecutora, String plantillaPath) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "SELECT	titulo_aplicacion,	nfolio	,canocontrarrecibo,	fechaaplicacion , cunidadresponsable, Solicitud_de_Pago_Firmada, CLC,	" + " Comprobante_Banco, Otros, TipoTramite, tipoIngreso, cDescripcionPoliza, cTipoPoliza, oficioPago " + "  FROM	v_ControlExpedientesSAI with(nolock) " + "  WHERE	cumpleExpediente = 'N' ";
        String cond = "";
        String token = "";
        try {
            for (Iterator<String> i = filtros.iterator(); i.hasNext(); ) {
                i.next();
                cond = cond + " " + token + " " + "?";
                token = ",";
            }
            cond = "   AND titulo_aplicacion IN ( " + cond + ")";
            query = query + cond;
            if (cUnidadEjecutora != null)
                query = query + " AND cunidadresponsable = ?";
            log.debug("Object: {}", "Query a ejecutar: " + query);
            ps = conn.prepareStatement(query);
            int cnt = 1;
            for (Iterator<String> i = filtros.iterator(); i.hasNext(); ) {
                ps.setString(cnt, i.next());
                cnt++;
            }
            if (cUnidadEjecutora != null)
                ps.setString(cnt, cUnidadEjecutora);
            rs = ps.executeQuery();
            InputStream fsArchivo = new FileInputStream(plantillaPath);
            Workbook workbook = new HSSFWorkbook(fsArchivo);
            fsArchivo.close();
            Sheet sheet0 = workbook.getSheetAt(0);
            Util.resultSetToExcel(rs, sheet0, 5);
            File fsalida = new File(plantillaPath);
            FileOutputStream fos = new FileOutputStream(fsalida);
            BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);
            workbook.write(bos);
            workbook.close();
            bos.flush();
            bos.close();
            fos.flush();
            fos.close();
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }
}
