package com.syc.gestion.custom;

import java.sql.Connection;
import java.sql.SQLException;

import com.syc.gestion.core.Caso;
import com.syc.gestion.core.GestionException;
import com.syc.gestion.core.Usuario;

public interface FolioGeneratorInterface {

	public String getNextFolio(Connection conn, String u_nombre, Caso c) throws SQLException,GestionException;

	public String getNextFolioUsr(Connection conn, Usuario u, Caso c) throws SQLException,GestionException;
}
