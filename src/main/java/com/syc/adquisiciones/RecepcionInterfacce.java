package com.syc.adquisiciones;

import java.sql.Connection;
import java.sql.SQLException;
import com.syc.adquisiciones.core.DatosRecepcion;
import com.syc.gestion.core.Usuario;
import java.util.Base64;

public interface RecepcionInterfacce {

    public boolean crear(Connection conn, Usuario usuario, DatosRecepcion datosRecepcion) throws SQLException;

    public boolean enviar(Connection conn, Usuario usuario, DatosRecepcion datosRecepcion) throws SQLException;

    public boolean devolver(Connection conn, Usuario usuario, DatosRecepcion datosRecepcion) throws SQLException;

    public boolean cancelar(Connection conn, Usuario usuario, DatosRecepcion datosRecepcion) throws SQLException;

    public boolean modificar(Connection conn, Usuario usuario, DatosRecepcion datosRecepcion) throws SQLException;
}
