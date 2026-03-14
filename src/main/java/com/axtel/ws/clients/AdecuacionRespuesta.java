package com.axtel.ws.clients;

import java.util.List;
import com.syc.contable.adecuaciones.UsuarioNotificado;
import java.util.Base64;

public class AdecuacionRespuesta {

    private int status;

    private String mensaje;

    private List<UsuarioNotificado> usuariosNotificar;

    /**
     * @return the status
     */
    public int getStatus() {
        return status;
    }

    /**
     * @param status
     *            the status to set
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * @return the mensaje
     */
    public String getMensaje() {
        return mensaje;
    }

    /**
     * @param mensaje
     *            the mensaje to set
     */
    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    /**
     * @return the usuariosNotificar
     */
    public List<UsuarioNotificado> getUsuariosNotificar() {
        return usuariosNotificar;
    }

    /**
     * @param usuariosNotificar
     *            the usuariosNotificar to set
     */
    public void setUsuariosNotificar(List<UsuarioNotificado> usuariosNotificar) {
        this.usuariosNotificar = usuariosNotificar;
    }

    @Override
    public String toString() {
        return "AdecuacionRespuesta [status=" + status + ", mensaje=" + mensaje + ", usuariosNotificar=" + usuariosNotificar + "]";
    }

    /**
     * @param status
     * @param mensaje
     * @param usuariosNotificar
     */
    public AdecuacionRespuesta(int status, String mensaje, List<UsuarioNotificado> usuariosNotificar) {
        super();
        this.status = status;
        this.mensaje = mensaje;
        this.usuariosNotificar = usuariosNotificar;
    }

    public AdecuacionRespuesta() {
        super();
    }
}
