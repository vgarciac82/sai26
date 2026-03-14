package com.syc.adquisiciones.core;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.syc.dsmngr.DataSourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class ComparativaBusinessLogic extends DataSourceManager {

    public String NombreReporte = "";

    private static Logger log = LoggerFactory.getLogger(ComparativaBusinessLogic.class);

    public ComparativaBusinessLogic(String jniName) {
        super.init(jniName);
    }

    public String ReporteComparativa(String cEjercicio, String cIdTipoProcedimiento, String cIdUnidadEjecutora, int nIdConsecutivo, String cTipoReporte) {
        Connection conn = null;
        StringBuffer sb = new StringBuffer();
        List<Object> partidasList = new ArrayList<Object>();
        List<Object> partidas = new ArrayList<Object>();
        List<Object> lineasList = new ArrayList<Object>();
        List<Object> proveedorList = new ArrayList<Object>();
        List<Object> montoList = new ArrayList<Object>();
        List<Object> firmantesList = new ArrayList<Object>();
        List<Object> procedimientoList = new ArrayList<Object>();
        List rfcList = new ArrayList();
        try {
            ProcedimientoComparativa rComparativa;
            ProcedimientoComparativa rLista;
            ProcedimientoComparativa rProvedor;
            ProcedimientoComparativa rFirmante;
            ProcedimientoComparativa rProcedimiento;
            ProcedimientoComparativa rComparativaPartidas;
            ///// comparativa de montos
            ProcedimientoComparativa rProveedorComparativa;
            ProcedimientoComparativa rPartidasComparativa;
            ProcedimientoComparativa rPartidaOrdenada;
            List<Object> ProveedorComparativaList = new ArrayList<Object>();
            List<Object> PartidasComparativaList = new ArrayList<Object>();
            List<Object> PartidaOrdenadaList = new ArrayList<Object>();
            List<Object> ProveedorComparativaList3 = new ArrayList<Object>();
            List<Object> ProveedorComparativaList2 = new ArrayList<Object>();
            List<Object> ProveedorObservacionesList = new ArrayList<Object>();
            conn = getConnection();
            String tabla = "";
            String tablaCotizadas = "";
            String tablaDesierta = "";
            String tablaNoAdjudica = "";
            String encabezado = "";
            String encabezadoNoAdjudica = "";
            String encabezadoCotizadas = "";
            String encabezadoDesierta = "";
            int row = 0;
            int numObs = 0;
            String imprime = "";
            if (cTipoReporte.equals("partidasComparativa")) {
                //imprime=tr;
                ////////////////////////////////////Tabla comparativa de partidas/////////////////////////////////////
                PartidasComparativaList = ComparativaManager.callLista(conn, cEjercicio, cIdTipoProcedimiento, cIdUnidadEjecutora, nIdConsecutivo);
                if (PartidasComparativaList.isEmpty()) {
                    tabla = "<tr class=\"alternateRow\">" + "	<td colspan=\"4\"><i>No hay informaci&oacute;n para mostrar</i></td>" + "</tr>";
                    //sb.append(tr);
                } else {
                    row++;
                    encabezado = "<tr class=" + ((row % 2) == 0 ? "AlternateRow" : "NormalRow") + "><td colspan='2' style='border-top:1.0pt solid black;border-right:1.0pt solid black;border-bottom:1.0pt solid black;'>Partida/Proveedor</td>";
                    ProveedorComparativaList = ComparativaManager.callProveedor(conn, cEjercicio, cIdTipoProcedimiento, cIdUnidadEjecutora, nIdConsecutivo);
                    if (ProveedorComparativaList.isEmpty()) {
                        tabla = "<tr class=\"alternateRow\">" + "	<td colspan=\"4\"><i>1</i></td>" + "</tr>";
                        sb.append(tabla);
                    } else {
                        int col = 1;
                        for (int i = 0; i < ProveedorComparativaList.size(); i++) {
                            int x = i + 1;
                            col = 2 + ProveedorComparativaList.size() * 4;
                            rProveedorComparativa = null;
                            rProveedorComparativa = (ProcedimientoComparativa) ProveedorComparativaList.get(i);
                            encabezado += "<td colspan='4' align='center' style='border-top:1.0pt solid black;border-right:1.0pt solid black;border-bottom:1.0pt solid black;'>" + x + "</td>";
                        }
                        encabezado += "</tr>";
                        encabezadoDesierta = "<tr><td colspan=" + col + " align='center' style='border-top:1.0pt solid black;border-right:1.0pt solid black;border-bottom:1.0pt solid black;'></td></tr>" + "<tr><td colspan=" + col + " align='center' style='border-top:1.0pt solid black;border-right:1.0pt solid black;border-bottom:1.0pt solid black;'>Partidas Desiertas</td></tr>";
                        encabezadoCotizadas = "<tr><td colspan=" + col + " align='center' style='border-top:1.0pt solid black;border-right:1.0pt solid black;border-bottom:1.0pt solid black;'></td></tr>" + "<tr><td colspan=" + col + " align='center' style='border-top:1.0pt solid black;border-right:1.0pt solid black;border-bottom:1.0pt solid black;'>Partidas Cotizadas</td></tr>";
                        encabezadoNoAdjudica = "<tr><td colspan=" + col + " align='center' style='border-top:1.0pt solid black;border-right:1.0pt solid black;border-bottom:1.0pt solid black;'></td></tr>" + "<tr><td colspan=" + col + " align='center' style='border-top:1.0pt solid black;border-right:1.0pt solid black;border-bottom:1.0pt solid black;'>Partidas No adjudicadas</td></tr>";
                    }
                    for (Iterator<?> iter = PartidasComparativaList.iterator(); iter.hasNext(); ) {
                        rProveedorComparativa = (ProcedimientoComparativa) iter.next();
                        int linea = 0;
                        linea = rProveedorComparativa.getLineaConsolidado();
                        ProveedorComparativaList = ComparativaManager.callProveedorOrdenado(conn, cEjercicio, cIdTipoProcedimiento, cIdUnidadEjecutora, nIdConsecutivo, linea, 1);
                        int total = ProveedorComparativaList.size();
                        if (ProveedorComparativaList.isEmpty()) {
                            tabla = "<tr class=\"alternateRow\">" + "	<td colspan=\"4\"><i>1</i></td>" + "</tr>";
                            sb.append(tabla);
                        } else {
                            PartidaOrdenadaList = ComparativaManager.callPartidaOrdenado(conn, cEjercicio, cIdTipoProcedimiento, cIdUnidadEjecutora, nIdConsecutivo, linea);
                            if (PartidaOrdenadaList.isEmpty()) {
                                tabla = "<tr class=\"alternateRow\">" + "	<td colspan=\"4\"><i>2</i></td>" + "</tr>";
                                sb.append(tabla);
                            } else {
                                String Partida = "<tr class=" + ((row % 2) == 0 ? "AlternateRow" : "NormalRow") + ">";
                                for (Iterator<?> iter1 = PartidaOrdenadaList.iterator(); iter1.hasNext(); ) {
                                    rPartidaOrdenada = (ProcedimientoComparativa) iter1.next();
                                    Partida += "<td rowspan='3' style='border-right:1.0pt solid black;border-bottom:1.0pt solid black'>" + rPartidaOrdenada.getLineaConsolidado() + "</td>";
                                    Partida += rPartidaOrdenada.getDescripcion();
                                }
                                //	tabla+=Partida;
                                for (int i = 0; i < ProveedorComparativaList.size(); i++) {
                                    rProveedorComparativa = null;
                                    rProveedorComparativa = (ProcedimientoComparativa) ProveedorComparativaList.get(i);
                                    //	String razon=rProveedorComparativa.getcrazonSocial();
                                    //	tabla+=rProveedorComparativa.getcrazonSocial();
                                    Partida += rProveedorComparativa.getcrazonSocial();
                                }
                                //tabla+="</tr><tr>";
                                Partida += "</tr><tr class=" + ((row % 2) == 0 ? "AlternateRow" : "NormalRow") + ">";
                                //////// titulos :precio unitario, tipo de cambio, total
                                for (int i = 0; i < ProveedorComparativaList.size(); i++) {
                                    rProveedorComparativa = null;
                                    rProveedorComparativa = (ProcedimientoComparativa) ProveedorComparativaList.get(i);
                                    //style='border-right:0.1pt solid black;border-bottom:0.1pt solid black'
                                    Partida += "<td >Cantidad</td>" + "<td>Precio Unitario</td>" + "<td>Tipo de Cambio</td>" + "<td style='border-right:1.0pt solid black'>Total</td>";
                                }
                                Partida += "</tr><tr>";
                                float montoUnit = 0;
                                int partidaDesierta = 0;
                                int partidaNo = 0;
                                ProveedorComparativaList2 = ComparativaManager.callProveedorOrdenado(conn, cEjercicio, cIdTipoProcedimiento, cIdUnidadEjecutora, nIdConsecutivo, linea, 2);
                                for (int i = 0; i < ProveedorComparativaList2.size(); i++) {
                                    rProveedorComparativa = null;
                                    rProveedorComparativa = (ProcedimientoComparativa) ProveedorComparativaList2.get(i);
                                    /// partida desierta =0 partida activa=1
                                    partidaDesierta = rProveedorComparativa.getNumRfc();
                                    // style='border-right:1.0pt solid black;border-bottom:1.0pt solid black;'
                                    montoUnit = rProveedorComparativa.getmontoMinimo();
                                    Partida += "<td style='border-bottom:1.0pt solid black;'>" + rProveedorComparativa.getCantidad() + "</td>";
                                    Partida += rProveedorComparativa.getmontoMinimoTexto();
                                    Partida += "<td align='right' style='border-bottom:1.0pt solid black;'>$" + rProveedorComparativa.getcTipoCambio() + "</td>";
                                    Partida += "<td align='right' style='border-right:1.0pt solid black;border-bottom:1.0pt solid black;'>$" + rProveedorComparativa.getmontoMaximoBruto() + "</td>";
                                }
                                /////////////////////////////////////////////////////////////Montos en cero
                                ProveedorComparativaList3 = ComparativaManager.callProveedorOrdenado(conn, cEjercicio, cIdTipoProcedimiento, cIdUnidadEjecutora, nIdConsecutivo, linea, 3);
                                for (int i = 0; i < ProveedorComparativaList3.size(); i++) {
                                    rProveedorComparativa = null;
                                    rProveedorComparativa = (ProcedimientoComparativa) ProveedorComparativaList3.get(i);
                                    /// partida desierta =0 partida activa=1
                                    partidaDesierta = rProveedorComparativa.getNumRfc();
                                    montoUnit = rProveedorComparativa.getmontoMinimo();
                                    Partida += "<td style='border-right:1.0pt solid black;border-bottom:1.0pt solid black;'>" + rProveedorComparativa.getCantidad() + "</td>";
                                    //	Partida+=rProveedorComparativa.getmontoMinimoTexto();
                                    Partida += "<td align='right' style='border-right:1.0pt solid black;border-bottom:1.0pt solid black'>No cotiza</td>";
                                    Partida += "<td align='right' style='border-right:1.0pt solid black;border-bottom:1.0pt solid black'>No cotiza</td>";
                                    Partida += "<td align='right' style='border-right:1.0pt solid black;border-bottom:1.0pt solid black'>No cotiza</td>";
                                    //Partida+="<td align='right' style='border-right:1.0pt solid black;border-bottom:1.0pt solid black'>$"+rProveedorComparativa.getcTipoCambio() +"</td>";
                                    //Partida+="<td align='right' style='border-right:1.0pt solid black;border-bottom:1.0pt solid black'>$"+rProveedorComparativa.getmontoMaximoBruto() +"</td>";
                                }
                                Partida += "</tr>";
                                if (partidaDesierta == 0) {
                                    tablaDesierta += Partida;
                                } else {
                                    if (total == ProveedorComparativaList3.size()) {
                                        tablaNoAdjudica += Partida;
                                    } else {
                                        tablaCotizadas += Partida;
                                    }
                                }
                            }
                        }
                    }
                }
                tabla = encabezadoCotizadas + tablaCotizadas;
                tabla += encabezadoNoAdjudica + tablaNoAdjudica;
                tabla += encabezadoDesierta + tablaDesierta;
                imprime = tabla;
            } else {
                if (cTipoReporte.equals("tablaComparativa")) {
                    //imprime=tabla;
                    ////////////////////////////////////////////Tabla comparativa//////////////////////////////////////////////////
                    String tr = "";
                    String obs = "";
                    String estadoPartida = "";
                    row++;
                    obs += "<tr>";
                    proveedorList = ComparativaManager.callProveedor(conn, cEjercicio, cIdTipoProcedimiento, cIdUnidadEjecutora, nIdConsecutivo);
                    procedimientoList = ComparativaManager.callProcedimiento(conn, cEjercicio, cIdTipoProcedimiento, cIdUnidadEjecutora, nIdConsecutivo, 2);
                    if (procedimientoList.isEmpty()) {
                        tr = "<tr>" + "	<td colspan=\"4\"><i>No hay procedimiento</i></td>" + "</tr>";
                        sb.append(tr);
                    } else {
                        //class="+((row % 2) == 0 ? "AlternateRow": "NormalRow")+"
                        tr = "<tr><td colspan='2'>TABLA COMPARATIVA DE COTIZACIONES DE:</td>";
                        for (int i = 0; i < procedimientoList.size(); i++) {
                            numObs = proveedorList.size() * 4;
                            rProcedimiento = null;
                            rProcedimiento = (ProcedimientoComparativa) procedimientoList.get(i);
                            tr += "<td colspan='2'>" + rProcedimiento.getProcedimiento() + "</td>";
                            tr += "<td colspan='" + proveedorList.size() * 4 + "'>AREA SOLICITANTE:   VARIAS</td></tr>";
                            tr += "<tr><td colspan='2'>" + rProcedimiento.getcCategoria() + "</td>" + "<td td colspan='2'>S.C. : VARIAS</td>";
                            tr += "<td colspan='" + proveedorList.size() * 4 + "'>DISPONIBILIDAD PRESUPUESTAL:  SUFICIENTE</td></tr>";
                        }
                    }
                    tr += "<tr><td rowspan='5' colspan='2'>FECHA DE ELABORACIÓN:</td>";
                    if (proveedorList.isEmpty()) {
                        tr = "<tr class=\"alternateRow\">" + "	<td colspan=\"4\"><i>No hay proveedor</i></td>" + "</tr>";
                        //	sb.append(tr);
                    } else {
                        tr += "<td colspan='2'>PROVEEDOR</td>";
                        for (int i = 0; i < proveedorList.size(); i++) {
                            int x = i + 1;
                            rProvedor = null;
                            rProvedor = (ProcedimientoComparativa) proveedorList.get(i);
                            tr += "<td colspan='4' style='border-top:1.0pt solid black;border-right:1.0pt solid black;border-left:1.0pt solid black;'>" + x + "</td>";
                        }
                        tr += "</tr><tr>" + "<td colspan='2'>RFC</td>";
                        for (int i = 0; i < proveedorList.size(); i++) {
                            rProvedor = null;
                            rProvedor = (ProcedimientoComparativa) proveedorList.get(i);
                            tr += "<td colspan='4' style='border-top:1.0pt solid black;border-right:1.0pt solid black;border-left:1.0pt solid black;'>" + rProvedor.getIdRFC() + "</td>";
                            obs += "<td colspan='4' style='border-top:1.0pt solid black;border-right:1.0pt solid black;border-left:1.0pt solid black;'>" + rProvedor.getcrazonSocial() + "</td>";
                            rfcList.add(rProvedor.getIdRFC());
                        }
                        tr += "</tr><tr>" + "<td colspan='2'>NOMBRE O RAZÓN SOCIAL:</td>";
                        obs += "</tr><tr>";
                        for (int i = 0; i < proveedorList.size(); i++) {
                            rProvedor = null;
                            rProvedor = (ProcedimientoComparativa) proveedorList.get(i);
                            tr += "<td colspan='4' style='border-right:1.0pt solid black;border-left:1.0pt solid black'>" + rProvedor.getcrazonSocial() + "</td>";
                        }
                        //tr+="</tr>";
                        tr += "</tr><tr>" + "<td colspan='2'>VIGENCIA DE LA COTIZACIÓN:</td>";
                        for (int i = 0; i < proveedorList.size(); i++) {
                            rProvedor = null;
                            rProvedor = (ProcedimientoComparativa) proveedorList.get(i);
                            tr += "<td colspan='4' style='border-left:1.0pt solid black;border-right:1.0pt solid black'>Vigencia</td>";
                        }
                        tr += "</tr><tr>" + "<td colspan='2' style='border-bottom:1.0pt solid black'>FECHA DE SU COTIZACIÓN:</td>";
                        for (int i = 0; i < proveedorList.size(); i++) {
                            rProvedor = null;
                            rProvedor = (ProcedimientoComparativa) proveedorList.get(i);
                            tr += "<td colspan='4' style='border-left:1.0pt solid black;border-right:1.0pt solid black'>" + rProvedor.getFecha() + "</td>";
                        }
                        tr += "</tr>";
                        tr += "<tr>" + "<td rowspan=2 style='border-left:1.0pt solid black;border-right:1.0pt solid black;border-top:1.0pt solid black;border-bottom:1.0pt solid black'>PART.</td>" + "<td rowspan=2 style='border-left:1.0pt solid black;border-right:1.0pt solid black;border-top:1.0pt solid black;border-bottom:1.0pt solid black'>DESCRIPCION DE LOS BIENES O SERVICIOS</td>" + "<td rowspan=2 style='border-left:1.0pt solid black;border-right:1.0pt solid black;border-top:1.0pt solid black;border-bottom:1.0pt solid black'>CANTIDAD</td>" + "<td rowspan=2 style='border-left:1.0pt solid black;border-right:1.0pt solid black;border-top:1.0pt solid black;border-bottom:1.0pt solid black'>UNIDAD</td>";
                        for (Iterator<?> iter1 = proveedorList.iterator(); iter1.hasNext(); ) {
                            rProvedor = (ProcedimientoComparativa) iter1.next();
                            tr += "<td colspan='4' style='border-left:1.0pt solid black;border-right:1.0pt solid black;border-top:1.0pt solid black;border-bottom:1.0pt solid black'>PRECIOS NETOS</td>";
                        }
                        tr += "</tr><tr>";
                        for (Iterator<?> iter1 = proveedorList.iterator(); iter1.hasNext(); ) {
                            rProvedor = (ProcedimientoComparativa) iter1.next();
                            tr += "<td style='border-left:1.0pt solid black;border-right:1.0pt solid black;border-top:1.0pt solid black;border-bottom:1.0pt solid black'>Lugar ocupado</td>" + "<td style='border-left:1.0pt solid black;border-right:1.0pt solid black;border-top:1.0pt solid black;border-bottom:1.0pt solid black'>Cumple E.T.</td>" + "<td style='border-left:1.0pt solid black;border-right:1.0pt solid black;border-top:1.0pt solid black;border-bottom:1.0pt solid black'>Unitario</td>" + "<td style='border-left:1.0pt solid black;border-right:1.0pt solid black;border-top:1.0pt solid black;border-bottom:1.0pt solid black'>Total</td>";
                        }
                    }
                    /// fin de proveedores
                    tr += "<td style='border-left:1.0pt solid black;border-right:1.0pt solid black;border-top:1.0pt solid black;border-bottom:1.0pt solid black'>Estado Partida</td>";
                    lineasList = ComparativaManager.callLista(conn, cEjercicio, cIdTipoProcedimiento, cIdUnidadEjecutora, nIdConsecutivo);
                    if (lineasList.isEmpty()) {
                        tr = "<tr class=\"alternateRow\">" + "	<td colspan=\"4\"><i>No hay informaci&oacute;n para mostrar</i></td>" + "</tr>";
                        //sb.append(tr);
                    } else {
                        for (Iterator<?> iter = lineasList.iterator(); iter.hasNext(); ) {
                            rLista = (ProcedimientoComparativa) iter.next();
                            int linea = 0;
                            linea = rLista.getLineaConsolidado();
                            //and agregaProveedor=1
                            /////////////////// obtener lugar para el ganador sugerido/////////////////////////
                            partidas = ComparativaManager.callComparativa(conn, cEjercicio, cIdTipoProcedimiento, cIdUnidadEjecutora, nIdConsecutivo, linea, " where mMontoMinimo<>0.00  order by ganadorSug");
                            tr += "<tr>";
                            float mMin = 0;
                            float total = 0;
                            float tCambio = 0;
                            int cant = 0;
                            int ganador = 0;
                            int lugarPrecio = 0;
                            List<Float> montosFinalesP = new ArrayList<Float>();
                            List<Object> montosFinales = new ArrayList<Object>();
                            List<Object> rfc = new ArrayList<Object>();
                            List<Integer> evaluacion = new ArrayList<Integer>();
                            List<Integer> posicion = new ArrayList<Integer>();
                            List<Integer> posicionPrecio = new ArrayList<Integer>();
                            for (Iterator<?> iter1 = partidas.iterator(); iter1.hasNext(); row++) {
                                rComparativaPartidas = (ProcedimientoComparativa) iter1.next();
                                cant = rComparativaPartidas.getConsecutivo();
                                mMin = rComparativaPartidas.getmontoMinimo();
                                tCambio = rComparativaPartidas.getcTipoCambio();
                                //rComparativa.getmontoMinimoTexto()
                                mMin = tCambio * mMin;
                                total = cant * mMin;
                                montosFinales.add(total);
                                evaluacion.add(rComparativaPartidas.getEvaluacion());
                                rfc.add(rComparativaPartidas.getIdRFC());
                            }
                            int longitud = montosFinales.size();
                            if (longitud != 0) {
                                posicion.add(1);
                            }
                            for (int i = 1; i < montosFinales.size(); i++) {
                                if (montosFinales.get(i).equals(montosFinales.get(i - 1)) && evaluacion.get(i).equals(1) && evaluacion.get(i - 1).equals(1)) {
                                    posicion.add(posicion.get(i - 1));
                                } else {
                                    if (montosFinales.get(i).equals(montosFinales.get(i - 1)) && evaluacion.get(i).equals(0) && evaluacion.get(i - 1).equals(0)) {
                                        posicion.add(posicion.get(i - 1));
                                    } else {
                                        posicion.add(posicion.get(i - 1) + 1);
                                    }
                                }
                            }
                            for (int m = 0; m < posicion.size(); m++) {
                                int n = ComparativaManager.ordena(conn, cEjercicio, cIdTipoProcedimiento, cIdUnidadEjecutora, nIdConsecutivo, linea, rfc.get(m).toString(), posicion.get(m));
                            }
                            ///////////OBTENER EL LUGAR POR PRECIO
                            List<Object> rfcPrecio = new ArrayList<Object>();
                            partidas = ComparativaManager.callComparativa(conn, cEjercicio, cIdTipoProcedimiento, cIdUnidadEjecutora, nIdConsecutivo, linea, " where mMontoMinimo<>0.00 order by lugarPrecio");
                            for (Iterator<?> iter1 = partidas.iterator(); iter1.hasNext(); row++) {
                                rComparativaPartidas = (ProcedimientoComparativa) iter1.next();
                                cant = rComparativaPartidas.getConsecutivo();
                                mMin = rComparativaPartidas.getmontoMinimo();
                                tCambio = rComparativaPartidas.getcTipoCambio();
                                //rComparativa.getmontoMinimoTexto()
                                mMin = tCambio * mMin;
                                total = cant * mMin;
                                montosFinalesP.add(total);
                                rfcPrecio.add(rComparativaPartidas.getIdRFC());
                            }
                            longitud = montosFinalesP.size();
                            if (longitud != 0) {
                                posicionPrecio.add(1);
                            }
                            float iaux;
                            String rfcaux = "";
                            for (int i = 1; i < longitud; i++) {
                                for (int j = 1; j <= longitud - 1; j++) {
                                    if (montosFinalesP.get(j - 1) > montosFinalesP.get(j)) {
                                        iaux = montosFinalesP.get(j - 1);
                                        rfcaux = rfcPrecio.get(j - 1).toString();
                                        montosFinalesP.set(j - 1, montosFinalesP.get(j));
                                        rfcPrecio.set(j - 1, rfcPrecio.get(j).toString());
                                        montosFinalesP.set(j, iaux);
                                        rfcPrecio.set(j, rfcaux);
                                    }
                                }
                            }
                            for (int i = 1; i < montosFinalesP.size(); i++) {
                                if (montosFinalesP.get(i).equals(montosFinalesP.get(i - 1))) {
                                    posicionPrecio.add(posicionPrecio.get(i - 1));
                                } else {
                                    posicionPrecio.add(posicionPrecio.get(i - 1) + 1);
                                }
                            }
                            for (int m = 0; m < posicionPrecio.size(); m++) {
                                int n = ComparativaManager.ordenaPrecio(conn, cEjercicio, cIdTipoProcedimiento, cIdUnidadEjecutora, nIdConsecutivo, linea, rfcPrecio.get(m).toString(), posicionPrecio.get(m));
                            }
                            partidasList = ComparativaManager.callComparativa(conn, cEjercicio, cIdTipoProcedimiento, cIdUnidadEjecutora, nIdConsecutivo, linea, "");
                            String aux = null;
                            for (Iterator<?> iter1 = partidasList.iterator(); iter1.hasNext(); row++) {
                                rComparativa = (ProcedimientoComparativa) iter1.next();
                                aux = rComparativa.getDescripcion();
                            }
                            tr = tr + aux;
                            for (Iterator<?> iter1 = partidasList.iterator(); iter1.hasNext(); row++) {
                                rComparativa = (ProcedimientoComparativa) iter1.next();
                                cant = rComparativa.getConsecutivo();
                                mMin = rComparativa.getmontoMinimo();
                                tCambio = rComparativa.getcTipoCambio();
                                //rComparativa.getmontoMinimoTexto()
                                mMin = tCambio * mMin;
                                total = cant * mMin;
                                lugarPrecio = rComparativa.getNumRfc();
                                ganador = rComparativa.getGanador();
                                if (ganador == 1) {
                                    tr += "<td style='border-left:1.0pt solid black;border-right:1.0pt solid black;border-top:1.0pt solid black;border-bottom:1.0pt solid black;background:#00FF00'>" + rComparativa.getcganadorSug() + "°/" + lugarPrecio + "°</td>" + "<td style='border-left:1.0pt solid black;border-right:1.0pt solid black;border-top:1.0pt solid black;border-left:1.0pt solid black;border-right:1.0pt solid black;border-top:1.0pt solid black;border-bottom:1.0pt solid black;background:#00FF00'>" + rComparativa.getEvaluacionTecnica() + "</td>" + "<td style='border-left:1.0pt solid black;border-right:1.0pt solid black;border-top:1.0pt solid black;border-bottom:1.0pt solid black;background:#00FF00'>" + rComparativa.getmontoMinimoTexto() + "</td>" + "<td  style='border-left:1.0pt solid black;border-right:1.0pt solid black;border-top:1.0pt solid black;border-bottom:1.0pt solid black;background:#00FF00'>$" + total + "</td>";
                                } else {
                                    tr += "<td style='border-left:1.0pt solid black;border-right:1.0pt solid black;border-top:1.0pt solid black;border-bottom:1.0pt solid black'>" + rComparativa.getcganadorSug() + "°/" + lugarPrecio + "°</td>" + "<td style='border-left:1.0pt solid black;border-right:1.0pt solid black;border-top:1.0pt solid black;border-left:1.0pt solid black;border-right:1.0pt solid black;border-top:1.0pt solid black;border-bottom:1.0pt solid black'>" + rComparativa.getEvaluacionTecnica() + "</td>" + "<td style='border-left:1.0pt solid black;border-right:1.0pt solid black;border-top:1.0pt solid black;border-bottom:1.0pt solid black'>" + rComparativa.getmontoMinimoTexto() + "</td>" + "<td style='border-left:1.0pt solid black;border-right:1.0pt solid black;border-top:1.0pt solid black;border-bottom:1.0pt solid black'>$" + total + "</td>";
                                }
                                obs += "<td colspan='4' style='border-left:1.0pt solid black;border-right:1.0pt solid black;border-top:1.0pt solid black;border-bottom:1.0pt solid black'>" + rComparativa.getcObservaciones() + "</td>";
                                estadoPartida = "<td style='border-left:1.0pt solid black;border-right:1.0pt solid black;border-top:1.0pt solid black;border-bottom:1.0pt solid black'>" + rComparativa.getcFirmante() + "</td>";
                                //	 auxtotal=auxtotal+total;
                            }
                            ///columna de partida desierta
                            tr += estadoPartida;
                            tr += "</tr>";
                            obs += "</tr>";
                            //}
                        }
                        // fin de lista de lineas
                    }
                    /// fin de comparativa
                    ///////Montos totales
                    String rfc = null;
                    float tot = 0;
                    float total1 = 0;
                    tr += "<tr><td colspan='2' style='border-left:1.0pt solid black;border-top:1.0pt solid black'>Acotaciones</td>" + "<td colspan='2' style='border-right:1.0pt solid black;border-top:1.0pt solid black'>Subtotal</td>";
                    for (int i = 0; i < rfcList.size(); i++) {
                        rfc = rfcList.get(i).toString();
                        montoList = ComparativaManager.callMontosTotales(conn, cEjercicio, cIdTipoProcedimiento, cIdUnidadEjecutora, nIdConsecutivo, rfc);
                        tot = Float.parseFloat(montoList.get(0).toString());
                        tr += "<td colspan='3' style='border-top:1.0pt solid black;border-right:1.0pt solid black'>&nbsp;</td>" + "<td style='border-top:1.0pt solid black;border-right:1.0pt solid black'>$" + new BigDecimal(tot).setScale(2, BigDecimal.ROUND_UP) + "</td>";
                    }
                    tr += "</tr>";
                    float iva = 0;
                    float ivat = 0;
                    float totalneto = 0;
                    float totalIVA = 0;
                    String renglon = null;
                    montoList = ComparativaManager.callMontosTotales(conn, cEjercicio, cIdTipoProcedimiento, cIdUnidadEjecutora, nIdConsecutivo, rfc);
                    iva = Float.parseFloat(montoList.get(1).toString());
                    ivat = (float) (iva / 0.01);
                    tr += "<tr><td colspan='2' style='border-left:1.0pt solid black'></td>" + "<td colspan='2' style='border-right:1.0pt solid black'>+" + ivat + "% DEL IVA</td>";
                    for (int i = 0; i < rfcList.size(); i++) {
                        rfc = rfcList.get(i).toString();
                        montoList = ComparativaManager.callMontosTotales(conn, cEjercicio, cIdTipoProcedimiento, cIdUnidadEjecutora, nIdConsecutivo, rfc);
                        tot = Float.parseFloat(montoList.get(0).toString());
                        iva = Float.parseFloat(montoList.get(1).toString());
                        totalIVA = tot * iva;
                        tr += "<td colspan='3'>&nbsp;</td>" + "<td style='border-left:1.0pt solid black;border-right:1.0pt solid black' >$" + new BigDecimal(totalIVA).setScale(2, BigDecimal.ROUND_UP) + "</td>";
                    }
                    tr += "</tr>";
                    tr += "<tr><td colspan='2' style='border-left:1.0pt solid black;border-bottom:1.0pt solid black'></td>" + "<td colspan='2' style='border-right:1.0pt solid black;border-bottom:1.0pt solid black'>Total Neto</td>";
                    for (int i = 0; i < rfcList.size(); i++) {
                        rfc = rfcList.get(i).toString();
                        montoList = ComparativaManager.callMontosTotales(conn, cEjercicio, cIdTipoProcedimiento, cIdUnidadEjecutora, nIdConsecutivo, rfc);
                        tot = Float.parseFloat(montoList.get(0).toString());
                        iva = Float.parseFloat(montoList.get(1).toString());
                        totalIVA = tot * iva;
                        totalneto = tot + totalIVA;
                        tr += "<td colspan='3' style='border-bottom:1.0pt solid black;border-right:1.0pt solid black'>&nbsp;</td>" + "<td style='border-bottom:1.0pt solid black;border-right:1.0pt solid black'>$" + new BigDecimal(totalneto).setScale(2, BigDecimal.ROUND_UP) + "</td>";
                    }
                    tr += "</tr>";
                    ////// cuadro de resultados de evaluaciones
                    int col = 4 + rfcList.size() * 4;
                    String requisitos = null;
                    String especificaciones = null;
                    String linea = null;
                    String tiempoEntrega = null;
                    String condiciones = null;
                    String condicionesL = null;
                    String origen = null;
                    String periodo = null;
                    String experiencia = null;
                    tr += "<tr><td colspan='" + col + "'  style='border-bottom:1.0pt solid black;border-top:1.0pt solid black;border-left:1.0pt solid black;border-right:1.0pt solid black;'>Resultado de las Evaluaciones</td></tr>";
                    tr += "<tr><td colspan='2' style='border-right:1.0pt solid black'>DE LOS REQUISITOS    (CUMPLE O NO CUMPLE)</td><td colspan='2' style='border-right:1.0pt solid black'></td>";
                    requisitos = "<td colspan='4' style='border-right:1.0pt solid black'>CUMPLE</td>";
                    especificaciones = "<td colspan='4' style='border-right:1.0pt solid black;border-bottom:1.0pt solid black'>CUMPLE</td>";
                    linea = "<td colspan='2' style='border-right:1.0pt solid black'></td><td colspan='2' style='border-right:1.0pt solid black;border-bottom:1.0pt solid black;border-top:1.0pt solid black'></td>";
                    tiempoEntrega = "<td colspan='4' style='border-right:1.0pt solid black'></td>";
                    condiciones = "<td colspan='4' style='border-right:1.0pt solid black'></td>";
                    condicionesL = "<td colspan='4' style='border-right:1.0pt solid black'></td>";
                    origen = "<td colspan='4' style='border-right:1.0pt solid black'></td>";
                    periodo = "<td colspan='4' style='border-right:1.0pt solid black'></td>";
                    experiencia = "<td colspan='4' style='border-right:1.0pt solid black;border-bottom:1.0pt solid black'></td>";
                    for (int m = 0; m < rfcList.size() - 1; m++) {
                        requisitos += "<td colspan='4' style='border-right:1.0pt solid black'>CUMPLE</td>";
                        especificaciones += "<td colspan='4' style='border-right:1.0pt solid black;border-bottom:1.0pt solid black'>CUMPLE</td>";
                        linea += "<td colspan='4' style='border-right:1.0pt solid black;border-bottom:1.0pt solid black'></td>";
                        tiempoEntrega += "<td colspan='4' style='border-right:1.0pt solid black'></td>";
                        condiciones += "<td colspan='4' style='border-right:1.0pt solid black'></td>";
                        condicionesL += "<td colspan='4' style='border-right:1.0pt solid black'></td>";
                        origen += "<td colspan='4' style='border-right:1.0pt solid black'></td>";
                        periodo += "<td colspan='4' style='border-right:1.0pt solid black'></td>";
                        experiencia += "<td colspan='4' style='border-right:1.0pt solid black;border-bottom:1.0pt solid black'></td>";
                    }
                    tr += requisitos + "</tr>";
                    tr += "<tr><td colspan='2' style='border-right:1.0pt solid black;border-bottom:1.0pt solid black'>DE LAS ESPECIFICACIONES TÉCNICAS (CUMPLE O NO CUMPLE)</td><td colspan='2' style='border-right:1.0pt solid black'></td>";
                    tr += especificaciones + "</tr>";
                    tr += "<tr>" + linea + "<td colspan='4' style='border-right:1.0pt solid black;border-bottom:1.0pt solid black'></td></tr>";
                    tr += "<tr><td colspan='2' style='border-right:1.0pt solid black;border-top:1.0pt solid black' >TIEMPO DE ENTREGA</td><td colspan='2' style='border-right:1.0pt solid black'></td>";
                    tr += tiempoEntrega + "</tr>";
                    tr += "<tr><td colspan='2' style='border-right:1.0pt solid black'>CONDICIONES DE PAGO</td><td colspan='2' style='border-right:1.0pt solid black'></td>";
                    tr += condiciones + "</tr>";
                    tr += "<tr><td colspan='2' style='border-right:1.0pt solid black'>CONDICIONES L.A.B. _____ X _____ OTRA ________</td><td colspan='2' style='border-right:1.0pt solid black'></td>";
                    tr += condicionesL + "</tr>";
                    tr += "<tr><td colspan='2' style='border-right:1.0pt solid black'>LUGAR DE ORIGEN DE LOS BIENES</td><td colspan='2' style='border-right:1.0pt solid black'></td>";
                    tr += origen + "</tr>";
                    tr += "<tr><td colspan='2' style='border-right:1.0pt solid black'>PERIODO DE GARANTÍA</td><td colspan='2' style='border-right:1.0pt solid black'></td>";
                    tr += periodo + "</tr>";
                    tr += "<tr><td colspan='2' style='border-right:1.0pt solid black;border-bottom:1.0pt solid black'>EXPERIENCIA PREVIA CON EL PROVEEDOR</td><td colspan='2' style='border-right:1.0pt solid black;border-bottom:1.0pt solid black'></td>";
                    tr += experiencia + "</tr>";
                    ////////firmantes
                    firmantesList = ComparativaManager.callFirmantes(conn, cIdTipoProcedimiento, cIdUnidadEjecutora, nIdConsecutivo);
                    if (firmantesList.isEmpty()) {
                        tr = "<tr class=\"alternateRow\">" + "	<td colspan=\"4\"><i>No hay informaci&oacute;n Partida</i></td>" + "</tr>";
                        sb.append(tr);
                    } else {
                        rFirmante = (ProcedimientoComparativa) firmantesList.get(0);
                        tr += "<tr><td rowspan='8' colspan='8' style='border-right:1.0pt solid black;border-bottom:1.0pt solid black'></td>" + "<td colspan='4' style='border-right:1.0pt solid black'> Elaboro</td></tr>" + "<tr><td colspan='4' style='border-right:1.0pt solid black'></td></tr>" + "<tr><td colspan='4' style='border-right:1.0pt solid black'>_________________________________</td></tr>" + "<tr><td colspan='4' style='border-right:1.0pt solid black'>" + rFirmante.getcFirmante() + "</td></tr>" + "<tr><td colspan='4' style='border-right:1.0pt solid black'>Aprobo</td></tr>";
                        tr += "<tr><td colspan='4' style='border-right:1.0pt solid black'></td></tr>" + "<tr><td colspan='4' style='border-right:1.0pt solid black'>_________________________________</td></tr>";
                        rFirmante = (ProcedimientoComparativa) firmantesList.get(1);
                        tr += "<tr><td colspan='4' style='border-right:1.0pt solid black;border-bottom:1.0pt solid black'>" + rFirmante.getcFirmante() + "</td></tr>";
                    }
                    String obsPartidas = "<tr>";
                    /////// observaciones
                    PartidasComparativaList = ComparativaManager.callLista(conn, cEjercicio, cIdTipoProcedimiento, cIdUnidadEjecutora, nIdConsecutivo);
                    if (PartidasComparativaList.isEmpty()) {
                        obsPartidas = "<tr class=\"alternateRow\">" + "	<td colspan=\"4\"><i>No hay informaci&oacute;n para mostrar</i></td>" + "</tr>";
                        //sb.append(tr);
                    } else {
                        //class="+((row % 2) == 0 ? "AlternateRow": "NormalRow")+
                        row++;
                        obsPartidas = "<tr>" + "<td colspan='4' style='border-top:1.0pt solid black;border-right:1.0pt solid black;border-bottom:1.0pt solid black;'>Partida/Proveedor</td></tr>";
                        for (Iterator<?> iter = PartidasComparativaList.iterator(); iter.hasNext(); ) {
                            rProveedorComparativa = (ProcedimientoComparativa) iter.next();
                            int linea1 = 0;
                            linea1 = rProveedorComparativa.getLineaConsolidado();
                            ProveedorObservacionesList = ComparativaManager.callProveedorOrdenado(conn, cEjercicio, cIdTipoProcedimiento, cIdUnidadEjecutora, nIdConsecutivo, linea1, 1);
                            //				int total=ProveedorComparativaList.size();
                            if (ProveedorObservacionesList.isEmpty()) {
                            } else {
                                PartidaOrdenadaList = ComparativaManager.callPartidaOrdenado(conn, cEjercicio, cIdTipoProcedimiento, cIdUnidadEjecutora, nIdConsecutivo, linea1);
                                if (PartidaOrdenadaList.isEmpty()) {
                                } else {
                                    for (Iterator<?> iter1 = PartidaOrdenadaList.iterator(); iter1.hasNext(); ) {
                                        rPartidaOrdenada = (ProcedimientoComparativa) iter1.next();
                                        obsPartidas += "<tr>" + "<td style='border-right:1.0pt solid black;border-bottom:1.0pt solid black'>" + rPartidaOrdenada.getLineaConsolidado() + "</td>";
                                        obsPartidas += rPartidaOrdenada.getUnidadEjecutora() + "</tr>";
                                    }
                                }
                            }
                        }
                    }
                    obsPartidas = "<table>" + obsPartidas + "</table>";
                    obs = "<table>" + obs + "</table>";
                    String partidasObservaciones = "<table><tr><td colspan='4'></td><td colspan=" + numObs + "></td></tr>" + "<tr><td colspan='4'></td><td colspan=" + numObs + ">Observaciones</td></tr>" + "<tr><td colspan='4'>" + obsPartidas + "</td><td colspan=" + numObs + ">" + obs + "</td></tr></table>";
                    /* tabla=encabezadoCotizadas+tablaCotizadas;
			 tabla+=encabezadoNoAdjudica+tablaNoAdjudica;
			 tabla+=encabezadoDesierta+tablaDesierta;*/
                    tr = "<table width='100%' border='2'>" + tr + "</table>";
                    tr = tr + partidasObservaciones;
                    //tablaComparativa partidasComparativa
                    /*	String imprime="";
				if (cTipoReporte.equals("tablaComparativa")){
				imprime=tr;
			}else{
				if (cTipoReporte.equals("partidasComparativa")){
					imprime=tabla;
				}	
			}*/
                    imprime = tr;
                }
            }
            sb.append(imprime);
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
            }
            log.error("Actualizando caso", exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return sb.toString();
    }
}
