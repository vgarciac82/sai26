package com.syc.adquisiciones.core;

import java.util.ArrayList;
import java.util.HashMap;
import com.syc.utils.Formatter;
import java.util.Base64;

public class PedidoModLinea {

    private Integer nIdLineaConsolidado;

    private String tipo = "";

    private String partida = "";

    private String descripcion = "";

    private Integer cantidad = 0;

    private String unidad = "";

    private String precioUN = "";

    private String precioTN = "";

    public Integer getnIdLineaConsolidado() {
        return nIdLineaConsolidado;
    }

    public void setnIdLineaConsolidado(Integer nIdLineaConsolidado) {
        this.nIdLineaConsolidado = nIdLineaConsolidado;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getPartida() {
        return partida;
    }

    public void setPartida(String partida) {
        this.partida = partida;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public String getCantidadS() {
        if (this.cantidad != null && this.cantidad > 0) {
            return this.cantidad + "";
        }
        return "";
    }

    public String getUnidad() {
        return unidad;
    }

    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }

    public String getPrecioUN() {
        return precioUN;
    }

    public void setPrecioUN(String precioUN) {
        this.precioUN = precioUN;
    }

    public String getPrecioTN() {
        return precioTN;
    }

    public void setPrecioTN(String precioTN) {
        this.precioTN = precioTN;
    }

    public static void groupArrays(ArrayList<PedidoModLinea> original, ArrayList<PedidoModLinea> modificacion, HashMap<Integer, PedidoModLinea> oMap, HashMap<Integer, PedidoModLinea> mMap) {
        ArrayList<PedidoModLinea> nuevoOriginal = new ArrayList<PedidoModLinea>();
        ArrayList<PedidoModLinea> nuevoModificado = new ArrayList<PedidoModLinea>();
        Integer idlineaconsm = 0;
        PedidoModLinea lineam = null;
        Integer cont = 0;
        if (modificacion.size() > 0) {
            lineam = modificacion.get(0);
            idlineaconsm = lineam.getnIdLineaConsolidado();
        }
        if (idlineaconsm == 0) {
            if (original.size() == 1) {
                original.get(0).setDescripcion("Misma descripciÃ³n que el pedido original.");
                modificacion.add(original.get(0));
                mMap.put(original.get(0).getnIdLineaConsolidado(), original.get(0));
            } else {
                String partidaInicial = "";
                String partidaFinal = "";
                Double precioNeto = 0.0;
                for (PedidoModLinea lineao : original) {
                    if (partidaInicial.equals(""))
                        partidaInicial = lineao.getPartida();
                    else
                        partidaFinal = lineao.getPartida();
                    String precioTN = lineao.getPrecioTN();
                    if (precioTN != null) {
                        precioTN = precioTN.replace("$", "").replace(",", "");
                        precioNeto += Double.parseDouble(precioTN);
                    }
                }
                PedidoModLinea lineao = original.get(0);
                lineao.setPartida(partidaInicial + "-" + partidaFinal);
                lineao.setDescripcion("Misma descripciÃ³n, cantidad, unidad y precio que el pedido original.");
                lineao.setCantidad(null);
                lineao.setUnidad("");
                lineao.setPrecioUN("");
                Formatter formatter = new Formatter();
                lineao.setPrecioTN(formatter.currency(new Float(precioNeto)));
                original.clear();
                original.add(lineao);
                modificacion.add(lineao);
            }
        } else {
            String partidaInicial = "";
            String partidaFinal = "";
            Integer cantidad = 0;
            String unidad = "";
            String precioU = "";
            Double precioNeto = 0.0;
            for (PedidoModLinea lineao : original) {
                Integer index = original.indexOf(lineao);
                Integer idlineaconso = lineao.getnIdLineaConsolidado();
                if (!idlineaconso.equals(idlineaconsm)) {
                    if (partidaInicial.equals(""))
                        partidaInicial = lineao.getPartida();
                    else
                        partidaFinal = lineao.getPartida();
                    String precioTN = lineao.getPrecioTN();
                    if (precioTN != null) {
                        precioTN = precioTN.replace("$", "").replace(",", "");
                        precioNeto += Double.parseDouble(precioTN);
                    }
                    cantidad = lineao.getCantidad();
                    unidad = lineao.getUnidad();
                    precioU = lineao.getPrecioUN();
                }
                if (idlineaconso.equals(idlineaconsm) || index.equals(original.size() - 1)) {
                    if (!partidaInicial.equals("")) {
                        PedidoModLinea nueva = new PedidoModLinea();
                        nueva.setTipo("A");
                        if (partidaFinal.equals("")) {
                            nueva.setPartida(partidaInicial);
                            nueva.setDescripcion("Misma descripciÃ³n del pedido original.");
                            nueva.setCantidad(cantidad);
                            nueva.setUnidad(unidad);
                            nueva.setPrecioUN(precioU);
                        } else {
                            nueva.setPartida(partidaInicial + "-" + partidaFinal);
                            nueva.setDescripcion("Misma descripciÃ³n, cantidad, unidad y precio del pedido original.");
                            nueva.setCantidad(null);
                            nueva.setUnidad("");
                            nueva.setPrecioUN("");
                        }
                        Formatter formatter = new Formatter();
                        nueva.setPrecioTN(formatter.currency(new Float(precioNeto)));
                        nuevoOriginal.add(nueva);
                        nuevoModificado.add(nueva);
                    }
                    if (idlineaconso.equals(idlineaconsm)) {
                        lineao.setDescripcion("Misma descripciÃ³n, cantidad, unidad y precio del pedido original.");
                        nuevoOriginal.add(lineao);
                        nuevoModificado.add(lineam);
                    }
                    precioNeto = 0.0;
                    partidaInicial = "";
                    partidaFinal = "";
                    cantidad = 0;
                    unidad = "";
                    precioU = "";
                    cont++;
                    if (cont < modificacion.size()) {
                        lineam = modificacion.get(cont);
                        idlineaconsm = lineam.getnIdLineaConsolidado();
                    }
                }
            }
            original.clear();
            original.addAll(nuevoOriginal);
            modificacion.clear();
            modificacion.addAll(nuevoModificado);
        }
    }

    public static void fixLines(ArrayList<PedidoModLinea> original, ArrayList<PedidoModLinea> modificacion) {
        ArrayList<PedidoModLinea> nuevoOriginal = new ArrayList<PedidoModLinea>();
        ArrayList<PedidoModLinea> nuevoModificado = new ArrayList<PedidoModLinea>();
        Integer size = modificacion.size();
        for (int j = 0; j < size; j++) {
            PedidoModLinea lineamod = modificacion.get(j);
            String desc = lineamod.getDescripcion();
            if (desc != null) {
                if (desc.length() > 65) {
                    Integer cont = 0;
                    while (desc.length() > 65) {
                        String newdesc = desc.substring(0, 65);
                        Integer index = newdesc.lastIndexOf(" ");
                        newdesc = desc.substring(0, index);
                        desc = desc.substring(index);
                        if (cont == 0) {
                            lineamod.setDescripcion(newdesc);
                            nuevoModificado.add(lineamod);
                            nuevoOriginal.add(original.get(j));
                        } else {
                            PedidoModLinea nuevalinea = new PedidoModLinea();
                            nuevalinea.setDescripcion(newdesc);
                            nuevoModificado.add(nuevalinea);
                            nuevoOriginal.add(new PedidoModLinea());
                        }
                        cont++;
                    }
                    if (desc.length() > 0) {
                        PedidoModLinea nuevalinea = new PedidoModLinea();
                        nuevalinea.setDescripcion(desc);
                        nuevoModificado.add(nuevalinea);
                        nuevoOriginal.add(new PedidoModLinea());
                    }
                } else {
                    nuevoModificado.add(lineamod);
                    nuevoOriginal.add(original.get(j));
                }
            } else {
                nuevoModificado.add(lineamod);
                nuevoOriginal.add(original.get(j));
            }
        }
        original.clear();
        original.addAll(nuevoOriginal);
        modificacion.clear();
        modificacion.addAll(nuevoModificado);
    }

    public String toString() {
        String value = "";
        if (this.nIdLineaConsolidado != null)
            value += this.nIdLineaConsolidado;
        if (this.tipo != null)
            value += "-" + this.tipo;
        if (this.partida != null)
            value += "-" + this.partida;
        if (this.descripcion != null)
            value += "-" + this.descripcion;
        if (this.cantidad != null)
            value += "-" + this.cantidad;
        if (this.unidad != null)
            value += "-" + this.unidad;
        if (this.precioUN != null)
            value += "-" + this.precioUN;
        if (this.precioTN != null)
            value += "-" + this.precioTN;
        return value;
    }
}
