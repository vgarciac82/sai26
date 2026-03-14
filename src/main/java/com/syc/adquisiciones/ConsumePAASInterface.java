package com.syc.adquisiciones;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import com.syc.adquisiciones.core.DatosPagoDirectoPAAS;
import com.syc.adquisiciones.core.Respuesta;
import com.syc.gestion.core.Usuario;
import java.util.Base64;

public interface ConsumePAASInterface {

    public Respuesta agregaLineasPAASPagoDirecto(Usuario usuario, ArrayList<List<String>> tabla, DatosPagoDirectoPAAS datos) throws Exception;

    public Respuesta actualizaLineasPAASPagoDirecto(Usuario usuario, ArrayList<List<String>> tabla, DatosPagoDirectoPAAS datos) throws Exception;

    public Respuesta eliminaLineasPAASPagoDirecto(Usuario usuario, DatosPagoDirectoPAAS datos) throws Exception;

    public BigDecimal montoMaximoTipoPago(String tipoPago) throws Exception;

    public BigDecimal getTotalCapturado(int folio) throws Exception;

    public int liberaPaasPago(Connection conn, int folio) throws Exception;

    String validaPAASvsSuficiencia(Connection conn, int folio) throws Exception;
}
