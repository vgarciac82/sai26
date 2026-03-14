package com.axtel.cfdi.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.math.BigDecimal;
import javax.xml.parsers.ParserConfigurationException;
import com.axtel.cfdi.ComplementoCombustible.Bonificacion;
import com.axtel.cfdi.ComplementoCombustible.CargoECC;
import com.axtel.cfdi.ComplementoCombustible.LectorComplementoCombustible;
import com.axtel.cfdi.ComplementoCombustible.custom.AdendaECC;
import com.axtel.cfdi.exceptions.CFDIReadException;
import mx.grupocorasa.sat.cfd._33.Comprobante;
import mx.grupocorasa.sat.cfdi.v3.CFDv33;
import mx.grupocorasa.sat.common.EstadoDeCuentaCombustible12.EstadoDeCuentaCombustible;
import java.util.Base64;

public class TestCombustible {

    public TestCombustible(File[] procesar) throws CFDIReadException {
        LectorComplementoCombustible lc = new LectorComplementoCombustible();
        System.out.println("Archivo,Subtotal,Impuestos,Total,Bonificacion,Impuetos Bonif. Total Bonif. Importe Neto");
        for (int i = 0; i < procesar.length; i++) {
            InputStream is = null;
            try {
                File factura = procesar[i];
                is = new FileInputStream(factura);
                CFDv33 cfdi33 = new CFDv33(is, "mx.grupocorasa.sat.common.EstadoDeCuentaCombustible12");
                Comprobante comp = (Comprobante) cfdi33.getComprobanteDocument();
                com.syc.cfdi.v3332.Comprobante.Comprobante comprobante = new com.syc.cfdi.v3332.Comprobante.Comprobante(comp);
                if (comprobante.esCFDICombustible()) {
                    EstadoDeCuentaCombustible ecc = comprobante.leeEstadoDeCuenta();
                    BigDecimal total = ecc.getTotal();
                    BigDecimal subtotal = ecc.getSubTotal();
                    BigDecimal impto = total.subtract(subtotal);
                    Bonificacion bonificacion = null;
                    try {
                        bonificacion = lc.readBonificacion(factura);
                    } catch (Exception e) {
                        System.out.println("Problemas leyendo bonificacion: " + factura.getAbsolutePath() + e);
                        e.printStackTrace();
                        continue;
                    }
                    System.out.println(factura.getName() + "," + subtotal + "," + total.subtract(subtotal) + "," + total + "," + bonificacion.getImporte() + "," + bonificacion.getTraslado() + "," + bonificacion.getTotal() + "," + total.add(bonificacion.getTotal()));
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (is != null)
                    try {
                        is.close();
                    } catch (Exception e) {
                        System.out.println("Problema cerrando flujo: " + e.toString());
                    } finally {
                        is = null;
                    }
            }
        }
        System.out.println("====================================================================================");
    }

    public void testAdenda(File[] procesar) throws CFDIReadException {
        LectorComplementoCombustible lc = new LectorComplementoCombustible();
        System.out.println("====================================================================================");
        System.out.println("Archivo, uuid, Importe Bruto, Impuestos, Total, Descuento Bruto, Descuento Impuestos, Total Descuento, Total Neto CFDI, Total Descuentos");
        for (int i = 0; i < procesar.length; i++) {
            InputStream is = null;
            try {
                File factura = procesar[i];
                is = new FileInputStream(factura);
                CFDv33 cfdi33 = new CFDv33(is, "mx.grupocorasa.sat.common.EstadoDeCuentaCombustible12");
                Comprobante comp = (Comprobante) cfdi33.getComprobanteDocument();
                com.syc.cfdi.v3332.Comprobante.Comprobante comprobante = new com.syc.cfdi.v3332.Comprobante.Comprobante(comp);
                if (comprobante.esCFDICombustible()) {
                    AdendaECC adenda = lc.readMontosCFDI(factura);
                    BigDecimal importeBruto = new BigDecimal(0.00);
                    BigDecimal importeImpuestos = new BigDecimal(0.00);
                    BigDecimal importeTotal = new BigDecimal(0.00);
                    BigDecimal importeDescuento = new BigDecimal(0.00);
                    BigDecimal importeBonificacion = new BigDecimal(0.00);
                    BigDecimal impuestosBonificacion = new BigDecimal(0.00);
                    BigDecimal bonificacionTotal = new BigDecimal(0.00);
                    BigDecimal importeNeto = new BigDecimal(0.00);
                    for (CargoECC cargo : adenda.getCargos()) {
                        importeBruto = importeBruto.add(cargo.getImporte());
                        importeImpuestos = importeImpuestos.add(cargo.getTraslado());
                        importeDescuento = importeDescuento.add(cargo.getDescuento());
                    }
                    importeTotal = importeBruto.add(importeImpuestos).subtract(importeDescuento);
                    for (Bonificacion bonificacion : adenda.getBonificaciones()) {
                        importeBonificacion = importeBonificacion.add(bonificacion.getImporte());
                        impuestosBonificacion = impuestosBonificacion.add(bonificacion.getTraslado());
                    }
                    bonificacionTotal = importeBonificacion.add(impuestosBonificacion);
                    importeNeto = importeTotal.subtract(bonificacionTotal);
                    System.out.println(factura + "," + comprobante.getUUID() + "," + importeBruto + "," + importeImpuestos + "," + importeTotal + "," + importeBonificacion + "," + impuestosBonificacion + "," + bonificacionTotal + "," + importeNeto + "," + importeDescuento);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (is != null)
                    try {
                        is.close();
                    } catch (Exception e) {
                        System.out.println("Problema cerrando flujo: " + e.toString());
                    } finally {
                        is = null;
                    }
            }
        }
        System.out.println("====================================================================================");
    }

    public void testConceptos(File[] procesar) throws CFDIReadException {
        LectorComplementoCombustible lc = new LectorComplementoCombustible();
        System.out.println("====================================================================================");
        System.out.println("Archivo, uuid, Importe Bruto, Impuestos, Total, Conceptos Bruto, Conceptos Impuestos, Total Conceptos, Diferencia");
        for (int i = 0; i < procesar.length; i++) {
            InputStream is = null;
            try {
                File factura = procesar[i];
                is = new FileInputStream(factura);
                CFDv33 cfdi33 = new CFDv33(is, "mx.grupocorasa.sat.common.EstadoDeCuentaCombustible12");
                Comprobante comp = (Comprobante) cfdi33.getComprobanteDocument();
                com.syc.cfdi.v3332.Comprobante.Comprobante comprobante = new com.syc.cfdi.v3332.Comprobante.Comprobante(comp);
                if (comprobante.esCFDICombustible()) {
                    EstadoDeCuentaCombustible ecc = comprobante.leeEstadoDeCuenta();
                    CargoECC cargos = comprobante.procesaComplementoCombustible();
                    System.out.println(factura + "," + comprobante.getUUID() + "," + ecc.getSubTotal() + "," + (ecc.getTotal().subtract(ecc.getSubTotal())) + "," + ecc.getTotal() + "," + cargos.getImporte() + "," + cargos.getTraslado() + "," + cargos.getTotal() + "," + cargos.getTotal().subtract(ecc.getTotal()));
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (is != null)
                    try {
                        is.close();
                    } catch (Exception e) {
                        System.out.println("Problema cerrando flujo: " + e.toString());
                    } finally {
                        is = null;
                    }
            }
        }
        System.out.println("====================================================================================");
    }

    public static void main(String... strings) throws CFDIReadException, ParserConfigurationException {
        File directorio = new File(strings[0]);
        File[] procesar = directorio.listFiles(new FilenameFilter() {

            public boolean accept(File dir, String name) {
                File pathname = new File(name);
                try {
                    if (pathname.getAbsolutePath().toLowerCase().endsWith(".xml"))
                        return true;
                    else
                        return false;
                } finally {
                    pathname = null;
                }
            }
        });
        TestCombustible test = new TestCombustible(procesar);
        // Elevator.testConceptos(procesar);
        test.testAdenda(procesar);
    }
}
