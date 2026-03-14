package com.syc.utils.mail;

import java.util.Base64;

public class ParametrosCorreo {

    int idAmbiente;

    int idProceso;

    String Ambiente;

    String NombreProceso;

    String PersonaDe;

    String PersonaPara;

    String PersonaCC;

    String MetodoProceso;

    String Plantilla;

    public String getAmbiente() {
        return Ambiente;
    }

    public void setAmbiente(String ambiente) {
        Ambiente = ambiente;
    }

    public String getNombreProceso() {
        return NombreProceso;
    }

    public int getIdAmbiente() {
        return idAmbiente;
    }

    public void setIdAmbiente(int idAmbiente) {
        this.idAmbiente = idAmbiente;
    }

    public int getIdProceso() {
        return idProceso;
    }

    public void setIdProceso(int idProceso) {
        this.idProceso = idProceso;
    }

    public String getPersonaPara() {
        return PersonaPara;
    }

    public void setPersonaPara(String personaPara) {
        PersonaPara = personaPara;
    }

    public void setNombreProceso(String nombreProceso) {
        NombreProceso = nombreProceso;
    }

    public String getPersonaDe() {
        return PersonaDe;
    }

    public void setPersonaDe(String personaDe) {
        PersonaDe = personaDe;
    }

    public String getPersonaCC() {
        return PersonaCC;
    }

    public void setPersonaCC(String personaCC) {
        PersonaCC = personaCC;
    }

    public String getMetodoProceso() {
        return MetodoProceso;
    }

    public void setMetodoProceso(String metodoProceso) {
        MetodoProceso = metodoProceso;
    }

    public String getPlantilla() {
        return Plantilla;
    }

    public void setPlantilla(String plantilla) {
        Plantilla = plantilla;
    }
}
