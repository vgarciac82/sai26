package com.axtel.egresos.entities;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Base64;

public class EgresoExcedeUMA {

    private int partida;

    private BigDecimal montoNeto;

    private String rfc;

    private String razonSocial;

    public int getPartida() {
        return partida;
    }

    public void setPartida(int partida) {
        this.partida = partida;
    }

    public BigDecimal getMontoNeto() {
        return montoNeto;
    }

    public void setMontoNeto(BigDecimal montoNeto) {
        this.montoNeto = montoNeto;
    }

    public String getRfc() {
        return rfc;
    }

    public void setRfc(String rfc) {
        this.rfc = rfc;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    @Override
    public int hashCode() {
        return Objects.hash(montoNeto, partida, razonSocial, rfc);
    }

    @Override
    public String toString() {
        return "EgresoExcedeUMA [partida=" + partida + ", montoNeto=" + montoNeto + ", rfc=" + rfc + ", razonSocial=" + razonSocial + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        EgresoExcedeUMA other = (EgresoExcedeUMA) obj;
        return Objects.equals(montoNeto, other.montoNeto) && partida == other.partida && Objects.equals(razonSocial, other.razonSocial) && Objects.equals(rfc, other.rfc);
    }
}
