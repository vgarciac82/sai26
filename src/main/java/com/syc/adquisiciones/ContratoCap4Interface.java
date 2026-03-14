package com.syc.adquisiciones;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import com.syc.adquisiciones.core.DatosContratoCap4;
import com.syc.gestion.core.Usuario;
import java.util.Base64;

public interface ContratoCap4Interface {

    public boolean nuevo(Connection conn, HttpSession session, Usuario usuario, DatosContratoCap4 datosContrato) throws SQLException;

    public boolean guardaContrato(Connection conn, Usuario usuario, DatosContratoCap4 datosContrato) throws SQLException;

    public boolean guardaContratoPartidas(Connection conn, Usuario usuario, DatosContratoCap4 datosContrato) throws Exception;

    public boolean apruebaContrato(Connection conn, HttpServletRequest request, Usuario usuario, DatosContratoCap4 datosContrato, String folioGenerator, String jndiName, String prefixPath) throws Exception;

    public boolean devuelveContrato(Connection conn, Usuario usuario, DatosContratoCap4 datosContrato) throws SQLException;

    public boolean precomprometer(Connection conn, HttpServletRequest request, Usuario usuario, DatosContratoCap4 datosContrato, ArrayList<List<String>> tabla, String prefixPath, String jndiName) throws Exception;

    public boolean devuelvePrecompromiso(Connection conn, HttpServletRequest request, Usuario usuario, DatosContratoCap4 datosContrato, String prefixPath, String jndiName) throws Exception;

    public boolean comprometer(Connection conn, HttpServletRequest request, Usuario usuario, DatosContratoCap4 datosContrato, String jndiName, String prefixPath) throws Exception;

    public boolean apruebaAmpliacionContrato(Connection conn, Usuario usuario, DatosContratoCap4 datosContrato) throws SQLException;

    public boolean devuelveAmpliacion(Connection conn, Usuario usuario, DatosContratoCap4 datosContrato) throws Exception;

    public boolean precomprometeAmpliacion(Connection conn, HttpServletRequest request, ArrayList<List<String>> tabla, Usuario usuario, DatosContratoCap4 datosContrato, String jndiName, String prefixPath) throws Exception;

    public boolean devuelvePrecompromisoAmpliacion(Connection conn, HttpServletRequest request, Usuario usuario, DatosContratoCap4 datosContrato, String jndiName, String prefixPath) throws Exception;

    public boolean comprometeAmpliacion(Connection conn, HttpServletRequest request, Usuario usuario, DatosContratoCap4 datosContrato, String jndiName, String prefixPath) throws Exception;

    public boolean nuevoConvenio(Connection conn, HttpSession session, Usuario usuario, DatosContratoCap4 datosContrato) throws SQLException;

    public boolean updateConvenio(Connection conn, HttpSession session, Usuario usuario, DatosContratoCap4 datosContrato) throws SQLException;

    public boolean deleteConvenio(Connection conn, HttpSession session, Usuario usuario, DatosContratoCap4 datosContrato) throws SQLException;

    public boolean migraContratoPlurianual(Connection conn, HttpSession session, Usuario usuario, DatosContratoCap4 datosContrato) throws Exception;

    public boolean savePartidasContPlurianual(Connection conn, HttpSession session, Usuario usuario, DatosContratoCap4 datosContrato) throws Exception;

    public boolean apruebaContratoPluri(Connection conn, HttpServletRequest request, Usuario usuario, DatosContratoCap4 datosContrato, String folioGenerator, String jndiName, String prefixPath) throws Exception;

    public boolean devuelveContratoPluri(Connection conn, Usuario usuario, DatosContratoCap4 datosContrato) throws SQLException;

    public boolean precomprometerContratoPluri(Connection conn, HttpServletRequest request, Usuario usuario, DatosContratoCap4 datosContrato, ArrayList<List<String>> tabla, String prefixPath, String jndiName) throws Exception;

    public boolean devuelvePrecompromisoContratoPluri(Connection conn, HttpServletRequest request, Usuario usuario, DatosContratoCap4 datosContrato, String prefixPath, String jndiName) throws Exception;

    public boolean comprometerContratoPluri(Connection conn, HttpServletRequest request, Usuario usuario, DatosContratoCap4 datosContrato, String jndiName, String prefixPath) throws Exception;
}
