package com.axtel.egresos.repositories.impl;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import com.axtel.egresos.exceptions.EgresoException;
import com.axtel.egresos.repositories.GreenMexRepository;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JDBCGreenMexRepository implements GreenMexRepository {

    private static final Logger log = LoggerFactory.getLogger(JDBCGreenMexRepository.class);

    public boolean insertaComprobacion(Connection conn, int folioRG, BigDecimal impEjercer, String folioING, String remanenteING, String fecha) throws Exception {
        boolean exito = false;
        PreparedStatement psDetalle = null;
        String queryInsertaDetalle = "";
        String[] folios;
        String[] remanentes;
        BigDecimal diferencia = new BigDecimal(0.00);
        try {
            folios = folioING.split(",");
            remanentes = remanenteING.split(",");
            if (folios.length > 1) {
                for (int i = 0; i < folios.length; i++) {
                    diferencia = diferencia.add(new BigDecimal(remanentes[i]));
                    if (diferencia.compareTo(impEjercer) < 1) {
                        impEjercer = impEjercer.subtract(diferencia);
                        queryInsertaDetalle = "INSERT INTO tEstadoDeCuentaGreenMexDetalle VALUES (" + folios[i] + "," + folioRG + ",1,'" + fecha + "'," + remanentes[i] + ",NULL)";
                        psDetalle = conn.prepareStatement(queryInsertaDetalle);
                        psDetalle.execute();
                    } else {
                        queryInsertaDetalle = "INSERT INTO tEstadoDeCuentaGreenMexDetalle VALUES (" + folios[i] + "," + folioRG + ",1,'" + fecha + "'," + impEjercer + ",NULL)";
                        psDetalle = conn.prepareStatement(queryInsertaDetalle);
                        psDetalle.execute();
                    }
                }
            } else {
                queryInsertaDetalle = "INSERT INTO tEstadoDeCuentaGreenMexDetalle VALUES (" + folios[0] + "," + folioRG + ",1,'" + fecha + "'," + impEjercer + ",NULL)";
                psDetalle = conn.prepareStatement(queryInsertaDetalle);
                psDetalle.execute();
            }
            log.debug("Se insertaron correctamente en el detalle del estado de cuenta GREENMEX.");
            exito = true;
        } catch (SQLException e) {
            throw new EgresoException("Problemas al guardar la comprobacion: " + e, e);
        } finally {
            CloseObject.closeObject(psDetalle);
        }
        return exito;
    }
}
