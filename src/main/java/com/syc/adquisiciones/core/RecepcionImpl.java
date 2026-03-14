package com.syc.adquisiciones.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.syc.adquisiciones.RecepcionInterfacce;
import com.syc.adquisiciones.util.Util;
import com.syc.gestion.core.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RecepcionImpl implements RecepcionInterfacce {

    private static Logger log = LoggerFactory.getLogger(RecepcionImpl.class);

    public boolean crear(Connection conn, Usuario usuario, DatosRecepcion datosRecepcion) {
        // TODO Auto-generated method stub
        boolean resp = false;
        if (datosRecepcion.isRecepMat()) {
            resp = creaRecepcionMaterial();
        } else {
            resp = creaRecepcionAnticipo();
        }
        return resp;
    }

    public boolean enviar(Connection conn, Usuario usuario, DatosRecepcion datosRecepcion) throws SQLException {
        // TODO Auto-generated method stub
        boolean resp = false;
        if (datosRecepcion.isRecepMat()) {
            resp = enviarRecepcionMaterial(conn, usuario, datosRecepcion);
            Util.bitacoraMovimientos(datosRecepcion.getcIdPedContDef(), "RECEPCIÓN EMITIDA PARA SU PAGO", usuario.getLogin(), conn);
        } else {
            resp = enviarRecepcionAnticipo(conn, usuario, datosRecepcion);
            Util.bitacoraMovimientos(datosRecepcion.getcIdPedContDef(), "ANTICIPO EMITIDO PARA SU PAGO", usuario.getLogin(), conn);
        }
        return resp;
    }

    public boolean devolver(Connection conn, Usuario usuario, DatosRecepcion datosRecepcion) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean cancelar(Connection conn, Usuario usuario, DatosRecepcion datosRecepcion) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean modificar(Connection conn, Usuario usuario, DatosRecepcion datosRecepcion) {
        // TODO Auto-generated method stub
        return false;
    }

    private boolean creaRecepcionMaterial() {
        try {
        } finally {
        }
        return false;
    }

    private boolean creaRecepcionAnticipo() {
        try {
        } finally {
        }
        return false;
    }

    private boolean enviarRecepcionMaterial(Connection conn, Usuario usuario, DatosRecepcion datosRecepcion) throws SQLException {
        double[] montoAnticipos = new double[3];
        double[] montoAnticiposEjerAnt = new double[3];
        double[] montosAmortizados = new double[3];
        double[] montosRecep = new double[3];
        double[] montosRemanenteSinIVA = new double[2];
        double[] montosRemanenteIVA = new double[2];
        double[] montosRemanenteConIVA = new double[2];
        double[] montoRemanenteAnt = new double[3];
        double[] montoRemanenteRM = new double[3];
        boolean resp = false;
        boolean hayAnticipo = false;
        try {
            if (!datosRecepcion.isFactorAmortizacion()) {
                //Sin factor de amortización
                hayAnticipo = hayAnticipos(conn, datosRecepcion);
                if (hayAnticipo) {
                    //Obtener montos de anticipo
                    String queryAnticipos = "select sum(isnull(rma.mMontoAnticipoConIVA,0.00)) mMontoAnticipoConIVA,sum(isnull(rma.mMontoAnticipoSinIVA,0.00))mMontoAnticipoSinIVA,sum(isnull(rma.mMontoAnticipoIVA,0.00))mMontoAnticipoIVA " + " from mRecepcionpMat as rm with(Nolock) inner join mRecepcionpMatAnticipo as rma with(Nolock) on rm.cIdRecepMat=rma.cIdRecepMat and rm.cIdpedContDef=rma.cIdpedContDef " + " and rm.nIdConsecutivoRecepM=rma.nIdConsecutivoRecepM	where rm.cIdpedContDef='" + datosRecepcion.getcIdPedContDef() + "' and rm.cIdRecepMat like 'RA-'+SUBSTRING('" + datosRecepcion.getcIdRecepcionMat() + "',4,3)+'%' and nIdEstadoRecepMat<>4";
                    montoAnticipos = obtieneMontos(conn, queryAnticipos);
                }
                log.info("Object: {}", "Anticipo ejercicio actual. montoAntConIVA=" + montoAnticipos[0] + "; montoAntSinIVA=" + montoAnticipos[1] + "; montoAntIVA=" + montoAnticipos[2]);
                //codigo para anticipos no ejercidos de ejercicios anteriores. "PLU".indexOf(datosRecepcion.getcIdPedContDef())>0
                if (datosRecepcion.getcIdPedContDef().indexOf("PLU") != -1) {
                    String queryAnticiposEjerAnt = "select sum(isnull(rmaEjeAnt.mMontoAnticipoConIVA,0.00)) mMontoAnticipoConIVA " + ",sum(isnull(rmaEjeAnt.mMontoAnticipoSinIVA,0.00))mMontoAnticipoSinIVA,sum(isnull(rmaEjeAnt.mMontoAnticipoIVA,0.00))mMontoAnticipoIVA " + "From mRecepcionpMatAnticipoEjerciciosAnt rmaEjeAnt with(Nolock) where cIdpedContDef='" + datosRecepcion.getcIdPedContDef() + "'";
                    log.info("Object: {}", queryAnticiposEjerAnt.toString());
                    montoAnticiposEjerAnt = obtieneMontos(conn, queryAnticiposEjerAnt);
                    montoAnticipos[0] = montoAnticipos[0] + montoAnticiposEjerAnt[0];
                    montoAnticipos[1] = montoAnticipos[1] + montoAnticiposEjerAnt[1];
                    montoAnticipos[2] = montoAnticipos[2] + montoAnticiposEjerAnt[2];
                    log.info("Object: {}", "Anticipo ejercicio anterior. montoAntConIVA=" + montoAnticiposEjerAnt[0] + "; montoAntSinIVA=" + montoAnticiposEjerAnt[1] + "; montoAntIVA=" + montoAnticiposEjerAnt[2]);
                }
                //Obtener montos amortizados total o por partes
                if (hayAnticipo || datosRecepcion.getcIdPedContDef().indexOf("PLU") != -1) {
                    String queryRecepAmortizadas = "select sum(rml.mMontoConIVARML)-sum(rm.mMontoConIVARM) mMontoConIVA,sum(rml.mMontoSinIVARML)-sum(rm.mMontoSinIVARM) mMontoSinIVA " + ",sum(rml.mMontoIVARML)-sum(rm.mMontoIVARM) mMontoIVA from(select sum(isnull(rm.mMontoConIVA,0.00))mMontoConIVARM ,sum(isnull(rm.mMontoSinIVA,0.00)) mMontoSinIVARM " + ",sum(isnull(rm.mMontoIVA,0.00)) mMontoIVARM ,rm.cIdpedContDef,rm.nIdConsecutivoRecepM,rm.cIdRecepMat,nIdEstadoRecepMat " + " from mRecepcionpMat as rm with(Nolock) group by  rm.cIdpedContDef,rm.nIdConsecutivoRecepM,rm.cIdRecepMat,nIdEstadoRecepMat " + " )rm inner join (select sum(isnull(rml.mMontoConIVA-isnull(mDescuentoConIVA,0.00),0.00))mMontoConIVARML ,sum(isnull(rml.mMontoSinIVA-isnull(mDescuentoSinIVA,0.00),0.00))mMontoSinIVARML" + "	,sum(isnull(rml.mMontoIVA-isnull(mDescuentoIVA,0.00),0.00))mMontoIVARML,rml.cIdpedContDef,cIdRecepMat,rml.nIdConsecutivoRecepM " + "	from mRecepcionpMatLineas as rml with(Nolock) group by rml.cIdpedContDef,cIdRecepMat,rml.nIdConsecutivoRecepM " + " )rml on rm.cIdRecepMat=rml.cIdRecepMat and rm.cIdpedContDef=rml.cIdpedContDef and rm.nIdConsecutivoRecepM=rml.nIdConsecutivoRecepM " + " and rm.nIdConsecutivoRecepM=rml.nIdConsecutivoRecepM where rm.cIdpedContDef='" + datosRecepcion.getcIdPedContDef() + "' and nIdEstadoRecepMat in(2,3,5)";
                    log.info("Object: {}", queryRecepAmortizadas.toString());
                    montosAmortizados = obtieneMontos(conn, queryRecepAmortizadas);
                    montoAnticipos[0] = montoAnticipos[0] - montosAmortizados[0];
                    montoAnticipos[1] = montoAnticipos[1] - montosAmortizados[1];
                    montoAnticipos[2] = montoAnticipos[2] - montosAmortizados[2];
                    log.info("Object: {}", "Anticipo total. montoAntConIVA=" + montoAnticipos[0] + "; montoAntSinIVA=" + montoAnticipos[1] + "; montoAntIVA=" + montoAnticipos[2]);
                }
            } else {
                //Con factor de amortización
                log.info("Contratos de las unidades I0%");
                //No se amortiza por eso no hay codificación
            }
            //Obtener los montos de la recepción a pagar.
            String queryRecep = "select isnull(mMontoConIVA,0.00) ,isnull(mMontoSinIVA,0.00),isnull(mMontoIVA,0.00) " + " from mRecepcionpMat with(Nolock) where cIdpedContDef='" + datosRecepcion.getcIdPedContDef() + "' and cIdRecepMat='" + datosRecepcion.getcIdRecepcionMat() + "'";
            montosRecep = obtieneMontos(conn, queryRecep);
            log.info("Object: {}", "montoRecepMatConIVA=" + montosRecep[0] + "; montoRecepMatSinIVA=" + montosRecep[1] + "; montoRecepMatIVA=" + montosRecep[2]);
            //Validar montos
            montosRemanenteSinIVA = validaMontos(montoAnticipos[1], montosRecep[1]);
            log.info("Object: {}", "montosRemanenteAntSinIVA=" + montosRemanenteSinIVA[0] + "; montosRemanenteRMSinIVA=" + montosRemanenteSinIVA[1]);
            montosRemanenteIVA = validaMontos(montoAnticipos[2], montosRecep[2]);
            log.info("Object: {}", "montosRemanenteAntIVA=" + montosRemanenteIVA[0] + "; montosRemanenteRMIVA=" + montosRemanenteIVA[1]);
            montosRemanenteConIVA = validaMontos(montoAnticipos[0], montosRecep[0]);
            log.info("Object: {}", "montosRemanenteAntConIVA=" + montosRemanenteConIVA[0] + "; montosRemanenteRMConIVA=" + montosRemanenteConIVA[1]);
            //Actualizar Anticipo  y recepción
            int nEstatus = 5;
            if (montoAnticipos[0] < montosRecep[0]) {
                nEstatus = 2;
            }
            montoRemanenteAnt[0] = montosRemanenteConIVA[0];
            montoRemanenteAnt[1] = montosRemanenteSinIVA[0];
            montoRemanenteAnt[2] = montosRemanenteIVA[0];
            montoRemanenteRM[0] = montosRemanenteConIVA[1];
            montoRemanenteRM[1] = montosRemanenteSinIVA[1];
            montoRemanenteRM[2] = montosRemanenteIVA[1];
            if (montoRemanenteRM[0] < montoRemanenteRM[1]) {
                montoRemanenteRM[0] = montoRemanenteRM[1];
            }
            resp = actualizaAnticipoAndRM(conn, datosRecepcion, montoRemanenteAnt, montoRemanenteRM, nEstatus);
        } finally {
            montoAnticipos = null;
            montosAmortizados = null;
            montosRecep = null;
            montosRemanenteSinIVA = null;
            montosRemanenteIVA = null;
            montosRemanenteConIVA = null;
            montoRemanenteAnt = null;
            montoRemanenteRM = null;
            montoAnticiposEjerAnt = null;
        }
        return resp;
    }

    private boolean enviarRecepcionAnticipo(Connection conn, Usuario usuario, DatosRecepcion datosRecepcion) {
        try {
        } finally {
        }
        return false;
    }

    private boolean hayAnticipos(Connection conn, DatosRecepcion datosRecepcion) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean resp = false;
        try {
            String query = "select *from mRecepcionpMat with(Nolock) where cIdpedContDef='" + datosRecepcion.getcIdPedContDef() + "' and cIdRecepMat like 'RA-'+SUBSTRING('" + datosRecepcion.getcIdRecepcionMat() + "',4,3)+'%'" + " and nIdEstadoRecepMat<>4";
            log.info("Object: {}", query.toString());
            pstmt = conn.prepareStatement(query);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                resp = true;
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (pstmt != null) {
                pstmt.close();
            }
            rs = null;
            pstmt = null;
        }
        return resp;
    }

    private double[] obtieneMontos(Connection conn, String query) throws SQLException {
        double[] montos = new double[3];
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            log.info("Object: {}", query.toString());
            pstmt = conn.prepareStatement(query);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                //monto con iva
                montos[0] = rs.getDouble(1);
                //monto sin iva
                montos[1] = rs.getDouble(2);
                //monto iva
                montos[2] = rs.getDouble(3);
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (pstmt != null) {
                pstmt.close();
            }
            rs = null;
            pstmt = null;
        }
        return montos;
    }

    private double[] validaMontos(double montoAnticipo, double montoRecepMat) {
        double[] montos = new double[2];
        if (montoAnticipo < montoRecepMat) {
            //montoRemanenteAnt
            montos[0] = 0;
            //montoRemanenteRM
            montos[1] = montoRecepMat - montoAnticipo;
        } else {
            //montoRemanenteAnt
            montos[0] = montoAnticipo - montoRecepMat;
            //montoRemanenteRM
            montos[1] = 0;
        }
        return montos;
    }

    private boolean actualizaAnticipoAndRM(Connection conn, DatosRecepcion datosRecepcion, double[] montoRemanenteAnt, double[] montoRemanenteRM, int nEstatus) throws SQLException {
        boolean resp = false;
        PreparedStatement pstmAnt = null, pstmRM = null;
        try {
            //Actualiza Anticipo
            String queryAnticipo = "update mRecepcionpMat set mMontoConIVA=" + montoRemanenteAnt[0] + ",mMontoSinIVA=" + montoRemanenteAnt[1] + ",mMontoIVA=" + montoRemanenteAnt[2] + " where cIdpedContDef='" + datosRecepcion.getcIdPedContDef() + "' and cIdRecepMat='" + datosRecepcion.getcIdRecepAnticipo() + "' and nIdEstadoRecepMat<>4";
            log.info("Object: {}", queryAnticipo.toString());
            pstmAnt = conn.prepareStatement(queryAnticipo);
            pstmAnt.executeUpdate();
            //Actualiza Recepción de Material
            String queryRM = "update mRecepcionpMat set mMontoConIVA=" + montoRemanenteRM[0] + ",mMontoSinIVA=" + montoRemanenteRM[1] + ",mMontoIVA=" + montoRemanenteRM[2] + ",nIdEstadoRecepMat=" + nEstatus + " where cIdpedContDef='" + datosRecepcion.getcIdPedContDef() + "' and cIdRecepMat='" + datosRecepcion.getcIdRecepcionMat() + "'";
            log.info("Object: {}", queryRM.toString());
            pstmRM = conn.prepareStatement(queryRM);
            pstmRM.executeUpdate();
            resp = true;
        } finally {
            if (pstmAnt != null) {
                pstmAnt.close();
            }
            if (pstmRM != null) {
                pstmRM.close();
            }
            pstmAnt = null;
            pstmRM = null;
        }
        return resp;
    }
}
