package com.syc.gestion;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import com.syc.dsmngr.DataSourceManager;
import com.syc.gestion.core.BitacoraCaso;
import com.syc.gestion.core.BitacoraCasoManager;
import com.syc.gestion.core.CasoOperacion;
import com.syc.gestion.core.CasoOperacionManager;
import com.syc.gestion.core.Empleado;
import com.syc.gestion.core.EmpleadoArea;
import com.syc.gestion.core.EmpleadoAreaManager;
import com.syc.gestion.core.EmpleadoManager;
import com.syc.gestion.core.GestionException;
import com.syc.gestion.core.Grupo;
import com.syc.gestion.core.GrupoManager;
import com.syc.gestion.core.GrupoPropiedades;
import com.syc.gestion.core.GrupoPropiedadesManager;
import com.syc.gestion.core.Role;
import com.syc.gestion.core.RoleManager;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.core.UsuarioGrupo;
import com.syc.gestion.core.UsuarioGrupoManager;
import com.syc.gestion.core.UsuarioManager;
import com.syc.gestion.core.UsuarioPropiedades;
import com.syc.gestion.core.UsuarioPropiedadesManager;
import com.syc.gestion.core.UsuarioRole;
import com.syc.gestion.core.UsuarioRoleManager;
import com.syc.gestion.util.PaginaData;
import com.syc.utils.Encripta;
import com.syc.xml.XmlFileManager;
import com.syc.xml.XmlFileSearchManager;
import com.syc.zip.ZipEntry;
import com.syc.zip.ZipOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;
import java.nio.file.Paths;

public class SeguridadBusinessLogic extends DataSourceManager {

    private static final Logger log = LoggerFactory.getLogger(SeguridadBusinessLogic.class);

    public SeguridadBusinessLogic(String jniName) {
        super.init(jniName);
    }

    public int agregaEmpleado(Empleado e) throws GestionException {
        int retVal = -1;
        Connection conn = null;
        try {
            conn = getConnection();
            retVal = EmpleadoManager.insert(conn, e);
            conn.commit();
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                log.error("En rollback", ex);
            }
            log.error("Error al agregar usuario (insert)", exc);
            throw new GestionException(exc);
        } catch (Exception exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                log.error("En rollback", ex);
            }
            log.error("Error al agregar usuario (insert)", exc);
            throw new GestionException(exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return retVal;
    }

    public int actualizaEmpleado(Empleado e) throws GestionException {
        int retVal = -1;
        Connection conn = null;
        try {
            conn = getConnection();
            retVal = EmpleadoManager.update(conn, e);
            conn.commit();
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                log.error("En rollback", ex);
            }
            log.error("Error al actualizar usuario (update)", exc);
            throw new GestionException(exc);
        } catch (Exception exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                log.error("En rollback", ex);
            }
            log.error("Error al actualizar usuario (update)", exc);
            throw new GestionException(exc);
        } finally {
            try {
                conn.commit();
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return retVal;
    }

    public int borraEmpleado(Empleado e) throws GestionException {
        int retVal = -1;
        Connection conn = null;
        try {
            conn = getConnection();
            retVal = EmpleadoManager.delete(conn, e.getId());
            conn.commit();
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                log.error("En rollback", ex);
            }
            log.error("Error al actualizar usuario (delete)", exc);
            throw new GestionException(exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return retVal;
    }

    public Hashtable[] transfiereUsuario(Usuario uFrom, Usuario uTo, int tipoTransferencia, String param1, String param2, String separator, String charEnc, String pathToSaveBackup) throws GestionException {
        int retVal = -1;
        Connection conn = null;
        Empleado eFrom = null;
        Empleado eTo = null;
        EmpleadoArea eaTo = null;
        Hashtable htCasos = null;
        Hashtable htCasosBuenos = null;
        Hashtable htCasosMalos = null;
        //GAF 2010-08-10
        charEnc = "UTF-8";
        try {
            conn = getConnection();
            Usuario findUsr = new Usuario();
            findUsr.setLogin(uTo.getLogin());
            findUsr = UsuarioManager.select(conn, findUsr);
            eFrom = new Empleado();
            eTo = new Empleado();
            if (findUsr == null) {
                // Si no existe uTo se crea con datos de uFrom
                uTo = copyUser(conn, uFrom, uTo);
            }
            eFrom.setClaveUsuario(uFrom.getLogin());
            eFrom = EmpleadoManager.select(conn, eFrom);
            eTo.setClaveUsuario(uTo.getLogin());
            eTo = EmpleadoManager.select(conn, eTo);
            eaTo = new EmpleadoArea();
            eaTo.setId(eTo.getClaveArea());
            eaTo = EmpleadoAreaManager.select(conn, eaTo);
            // Obtener Operaciones asignadas y moverlas
            PaginaData pd = null;
            PaginaData param_pd = new PaginaData();
            List operaciones = new ArrayList();
            param_pd.setNumeroPagina(0);
            param_pd.setTamanoPaginas(1000);
            pd = CasoOperacionManager.transferCasoOperacion(conn, uFrom.getLogin(), tipoTransferencia, param1, param2, separator);
            operaciones = pd.getLista();
            htCasos = new Hashtable();
            htCasosBuenos = new Hashtable();
            htCasosMalos = new Hashtable();
            //PARA FORMATEAR EL ID_CASO
            NumberFormat nf = NumberFormat.getInstance();
            nf.setMinimumIntegerDigits(10);
            nf.setMaximumIntegerDigits(10);
            nf.setMinimumFractionDigits(0);
            nf.setMaximumFractionDigits(0);
            nf.setGroupingUsed(false);
            //Primero itero para obtener las rutas de los archivos XML
            //y respaldarlos en un zip.
            for (int iIndex = 0; iIndex < operaciones.size(); iIndex++) {
                Vector myVector = (Vector) operaciones.get(iIndex);
                CasoOperacion co = (CasoOperacion) myVector.get(0);
                BitacoraCaso bc = new BitacoraCaso();
                bc.setIdCaso(co.getIdCaso());
                bc = BitacoraCasoManager.select(conn, bc);
                String strIdCaso = nf.format(co.getIdCaso());
                String[] htElement = new String[5];
                htElement[0] = strIdCaso;
                htElement[1] = bc.getTituloAplicacion();
                htElement[2] = Integer.toString(bc.getIdGabinete());
                String pathFile = XmlFileSearchManager.select(conn, bc.getTituloAplicacion(), bc.getIdGabinete());
                if (pathFile == null) {
                    htElement[3] = "";
                } else {
                    htElement[3] = pathFile;
                }
                htElement[4] = bc.getFolio();
                htCasos.put(strIdCaso, htElement);
                //SearchXml2 xmlBl = new SearchXml2();
                File xmlFile = null;
                try {
                    xmlFile = new File(pathFile);
                } catch (Exception e) {
                    String myOut = "El caso=[" + htElement[0] + "], folio=[" + htElement[4] + "], titulo_aplicacion=[" + bc.getTituloAplicacion() + "], gabinete=[" + bc.getIdGabinete() + "] regresa nulo el path para el archivo XML";
                    System.out.println(myOut);
                    Hashtable htCm = new Hashtable();
                    htCm.put("folio", bc.getFolio());
                    htCm.put("out", myOut);
                    htCm.put("error", e.getMessage());
                    htCasosMalos.put(strIdCaso, htCm);
                    continue;
                }
                Hashtable ht = new Hashtable();
                if (xmlFile != null && xmlFile.exists() && xmlFile.length() > 0) {
                    if (!htCasosBuenos.containsKey(strIdCaso) && !htCasosMalos.containsKey(strIdCaso)) {
                        try {
                            ht = XmlFileManager.getAllElementsAsHashtable(xmlFile, charEnc, htElement);
                        } catch (Exception e) {
                            e.printStackTrace();
                            String myOut = "El caso=[" + htElement[0] + "], folio=[" + htElement[4] + "], titulo_aplicacion=[" + bc.getTituloAplicacion() + "], gabinete=[" + bc.getIdGabinete() + "] no pudo leer los elementos del archivo XML";
                            System.out.println(myOut);
                            Hashtable htCm = new Hashtable();
                            htCm.put("folio", bc.getFolio());
                            htCm.put("out", myOut);
                            htCm.put("error", e.getMessage());
                            htCasosMalos.put(strIdCaso, htCm);
                        } catch (Throwable th) {
                            System.out.println("Es un throwable!");
                            th.printStackTrace();
                            String myOut = "El caso=[" + htElement[0] + "], folio=[" + htElement[4] + "], titulo_aplicacion=[" + bc.getTituloAplicacion() + "], gabinete=[" + bc.getIdGabinete() + "] no pudo leer los elementos del archivo XML";
                            System.out.println(myOut);
                            Hashtable htCm = new Hashtable();
                            htCm.put("folio", bc.getFolio());
                            htCm.put("out", myOut);
                            htCm.put("error", th.getMessage());
                            htCasosMalos.put(strIdCaso, htCm);
                        }
                        if (ht.size() > 0) {
                            htCasosBuenos.put(strIdCaso, ht);
                        } else {
                            String myOut = "El parser de XML obtuvo cero elementos del archivo [" + htElement[3] + "] del caso [" + htElement[0] + "]";
                            System.out.println(myOut);
                            Hashtable htCm = new Hashtable();
                            htCm.put("folio", bc.getFolio());
                            htCm.put("out", myOut);
                            htCm.put("error", "");
                            htCasosMalos.put(strIdCaso, htCm);
                        }
                    }
                } else {
                    String myOut = "No se encontro el archivo (o esta vacio) [" + htElement[3] + "] del caso [" + htElement[0] + "]";
                    System.out.println(myOut);
                    Hashtable htCm = new Hashtable();
                    htCm.put("folio", bc.getFolio());
                    htCm.put("out", myOut);
                    htCm.put("error", "");
                    htCasosMalos.put(strIdCaso, htCm);
                }
                xmlFile = null;
            }
            //ya que tenemos los casos, realizamos el respaldo
            //y leemos el XML
            if (htCasosBuenos.size() > 0) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                String zipFileName = pathToSaveBackup + "RESPALDO_TRANSFERENCIA_ASUNTOS_DE_" + uFrom.getLogin() + "_A_" + uTo.getLogin() + "_" + sdf.format(new Date()) + ".zip";
                createZipFile(zipFileName, htCasosBuenos, htCasos);
            }
            //despues seguiria modificar los archivos XML
            //(si falla, lo adjuntamos a la coleccion de malos
            Enumeration en = htCasosBuenos.keys();
            Vector myVector = new Vector();
            while (en.hasMoreElements()) {
                String llave = (String) en.nextElement();
                String[] valor = (String[]) htCasos.get(llave);
                Hashtable ht = (Hashtable) htCasosBuenos.get(llave);
                //ESTO NO ES NECESARIO!!!!
                //seria bueno comentarlo
                //File readFile = new File(valor[3]);
                //StringBuffer sbIn = new StringBuffer();
                //BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(readFile), charEnc));
                //String tmpLine = br.readLine();
                //while (tmpLine != null) {
                //	sbIn.append(tmpLine);
                //	tmpLine = br.readLine();
                //}
                ht = modificaXMLElements(ht, uFrom, uTo, eTo, eaTo);
                boolean result = writeXMLFile(valor[3], ht, charEnc);
                //SI NO SE PUDO ESCRIBIR EL ARCHIVO
                //LO AGREGA A LOS MALOS
                if (!result) {
                    String myOut = "No pudo modificar el contenido del archivo (o esta vacio) [" + valor[3] + "] del caso [" + valor[0] + "]";
                    System.out.println(myOut);
                    Hashtable htCm = new Hashtable();
                    htCm.put("folio", ht.get("folio"));
                    htCm.put("out", myOut);
                    htCm.put("error", "");
                    htCasosMalos.put(valor[0], htCm);
                }
                //aqui obtengo el folio
                String folio = (String) ht.get("folio");
                String user12 = (String) ht.get("user12");
                String query1 = " UPDATE IMXEXPEDIENTES SET REMINULOGIN = '" + uTo.getLogin() + "', REMINUNOMBRE = '" + findUsr.getNombre() + "', REMINPTONOM  = '" + eTo.getCargo() + "', REMINDDESC   = '" + eaTo.getDescripcion() + "' WHERE " + "   REMINULOGIN  = '" + uFrom.getLogin() + "' AND " + " " + "  FOLIO = '" + folio + "' ";
                String query2 = " UPDATE IMXEXPEDIENTES SET RESULOGIN = '" + uTo.getLogin() + "', RESUNOMBRE   = '" + findUsr.getNombre() + "', RESPTONOMBRE = '" + eTo.getCargo() + "', RESDDESC     = '" + eaTo.getDescripcion() + "' WHERE " + "   RESULOGIN    = '" + uFrom.getLogin() + "' AND " + "   FOLIO = '" + folio + "' ";
                String query3 = " UPDATE IMXEXPEDIENTES SET USER09 = '" + uTo.getLogin() + "' WHERE " + "   USER09    = '" + uFrom.getLogin() + "' AND " + "   FOLIO = '" + folio + "' ";
                String query4 = "";
                String query5 = "";
                if (user12 != null && user12.indexOf(uFrom.getLogin()) > -1) {
                    String user12mod = user12.replaceAll(uFrom.getLogin(), uTo.getLogin());
                    query4 = " UPDATE IMXEXPEDIENTES SET USER12 = '" + user12mod + "' WHERE " + "   FOLIO = '" + folio + "' ";
                    query5 = " UPDATE cg_caso_dato SET CD_VALOR = '" + user12mod + "' WHERE " + "   id_caso = " + valor[0] + "  AND id_tc = 8 and id_cd = 41 ";
                }
                //REMITENTE INTERNO
                String query6 = " UPDATE cg_caso_dato SET CD_VALOR = '" + findUsr.getNombre() + "' WHERE " + "  id_caso = " + valor[0] + "  AND id_tc = 8 AND id_cd = 16 " + "  AND '" + uFrom.getLogin() + "' = (select cd_valor from cg_caso_dato " + "where id_cd=15 and id_caso=" + valor[0] + ")";
                String query7 = " UPDATE cg_caso_dato SET CD_VALOR = '" + eTo.getCargo() + "' WHERE " + "  id_caso = " + valor[0] + "  AND id_tc = 8 AND id_cd = 17 " + "  AND '" + uFrom.getLogin() + "' = (select cd_valor from cg_caso_dato " + "where id_cd=15 and id_caso=" + valor[0] + ")";
                String query8 = " UPDATE cg_caso_dato SET CD_VALOR = '" + eaTo.getDescripcion() + "' WHERE " + "  id_caso = " + valor[0] + "  AND id_tc = 8 AND id_cd = 18 " + "  AND '" + uFrom.getLogin() + "' = (select cd_valor from cg_caso_dato " + "where id_cd=15 and id_caso=" + valor[0] + ")";
                String query9 = " UPDATE cg_caso_dato SET CD_VALOR = '" + uTo.getLogin() + "' WHERE " + "  id_caso = " + valor[0] + "  AND id_tc = 8 " + "  AND id_cd = 15 " + "  AND id_cd = 26 " + "  AND CD_VALOR = '" + uFrom.getLogin() + "' ";
                //RESPONSABLE
                String query10 = " UPDATE cg_caso_dato SET CD_VALOR = '" + findUsr.getNombre() + "' WHERE " + "  id_caso = " + valor[0] + "  AND id_tc = 8 AND id_cd = 27 " + "  AND '" + uFrom.getLogin() + "' = (select cd_valor from cg_caso_dato " + "where id_cd=26 and id_caso=" + valor[0] + ")";
                String query11 = " UPDATE cg_caso_dato SET CD_VALOR = '" + eTo.getCargo() + "' WHERE " + "  id_caso = " + valor[0] + "  AND id_tc = 8 AND id_cd = 28 " + "  AND '" + uFrom.getLogin() + "' = (select cd_valor from cg_caso_dato " + "where id_cd=26 and id_caso=" + valor[0] + ")";
                String query12 = " UPDATE cg_caso_dato SET CD_VALOR = '" + eaTo.getDescripcion() + "' WHERE " + "  id_caso = " + valor[0] + "  AND id_tc = 8 AND id_cd = 29 " + "  AND '" + uFrom.getLogin() + "' = (select cd_valor from cg_caso_dato " + "where id_cd=26 and id_caso=" + valor[0] + ")";
                String query13 = " UPDATE cg_caso_dato SET CD_VALOR = '" + uTo.getLogin() + "' WHERE " + "  id_caso = " + valor[0] + "  AND id_tc = 8 " + "  AND id_cd = 26 " + "  AND CD_VALOR = '" + uFrom.getLogin() + "' ";
                //38	USER09	Coordinador
                String query14 = " UPDATE cg_caso_dato SET CD_VALOR = '" + uTo.getLogin() + "' WHERE " + "  id_caso = " + valor[0] + "  AND id_tc = 8 " + "  AND id_cd = 38 " + "  AND CD_VALOR = '" + uFrom.getLogin() + "' ";
                //Faltan hacer actualizaciones de los userXX que guardan varios valores!!!!
                myVector.add(query1);
                myVector.add(query2);
                myVector.add(query3);
                if (query4.trim().length() > 0) {
                    myVector.add(query4);
                    myVector.add(query5);
                }
                myVector.add(query6);
                myVector.add(query7);
                myVector.add(query8);
                myVector.add(query9);
                myVector.add(query10);
                myVector.add(query11);
                myVector.add(query12);
                myVector.add(query13);
                myVector.add(query14);
            }
            //Convierto el vector en arreglo de Strings
            String[] gavetaQueries = null;
            if (myVector.size() > 0) {
                gavetaQueries = new String[myVector.size()];
                myVector.toArray(gavetaQueries);
            }
            String whereClause = getWhereClause(htCasosMalos);
            System.out.println("whereClause=[" + whereClause + "]");
            int registrosModificados = transferenciaExecuteQueries(conn, uFrom, uTo, eTo, tipoTransferencia, param1, param2, separator, gavetaQueries, whereClause);
            if (registrosModificados > -1) {
                conn.commit();
            } else {
                conn.rollback();
            }
        } catch (Exception exc) {
            try {
                conn.rollback();
            } catch (SQLException e) {
                log.error("En rollback", e);
            }
            log.error("Error al agregar usuario (insert)", exc);
            throw new GestionException(exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return new Hashtable[] { htCasosBuenos, htCasosMalos };
    }

    public int agregaUsuario(Usuario u, boolean encripta) throws GestionException {
        int retVal = -1;
        Connection conn = null;
        try {
            conn = getConnection();
            if (encripta) {
                u.setPassword(Encripta.code32(u.getPassword()));
            } else {
                u.setPassword(u.getPassword());
            }
            retVal = UsuarioManager.insert(conn, u);
            conn.commit();
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException e) {
                log.error("En rollback", e);
            }
            log.error("Error al agregar usuario (insert)", exc);
            throw new GestionException(exc);
        } catch (Exception exc) {
            try {
                conn.rollback();
            } catch (SQLException e) {
                log.error("En rollback", e);
            }
            log.error("Error al agregar usuario (insert)", exc);
            throw new GestionException(exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return retVal;
    }

    public int actualizaUsuario(Usuario u, boolean encripta) throws GestionException {
        int retVal = -1;
        Connection conn = null;
        try {
            conn = getConnection();
            if (encripta) {
                u.setPassword(Encripta.code32(u.getPassword()));
            } else {
                u.setPassword(u.getPassword());
            }
            retVal = UsuarioManager.update(conn, u);
            conn.commit();
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException e) {
                log.error("En rollback", e);
            }
            log.error("Error al actualizar usuario (update)", exc);
            throw new GestionException(exc);
        } catch (Exception exc) {
            try {
                conn.rollback();
            } catch (SQLException e) {
                log.error("En rollback", e);
            }
            log.error("Error al actualizar usuario (update)", exc);
            throw new GestionException(exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return retVal;
    }

    public int borraUsuario(Usuario u) throws GestionException {
        int retVal = -1;
        Connection conn = null;
        try {
            conn = getConnection();
            retVal = UsuarioManager.delete(conn, u.getLogin());
            conn.commit();
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException e) {
                log.error("En rollback", e);
            }
            log.error("Error al actualizar usuario (delete)", exc);
            throw new GestionException(exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return retVal;
    }

    public int agregaGrupo(UsuarioGrupo ug) throws GestionException {
        int retVal = -1;
        Connection conn = null;
        try {
            conn = getConnection();
            retVal = UsuarioGrupoManager.insert(conn, ug);
            conn.commit();
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException e) {
                log.error("En rollback", e);
            }
            log.error("Error al agregar usuario grupo (insert)", exc);
            throw new GestionException(exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return retVal;
    }

    public int actualizaGrupo(UsuarioGrupo ug) throws GestionException {
        int retVal = -1;
        Connection conn = null;
        try {
            conn = getConnection();
            retVal = UsuarioGrupoManager.update(conn, ug);
            conn.commit();
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException e) {
                log.error("En rollback", e);
            }
            log.error("Error al actualizar usuario grupo (update)", exc);
            throw new GestionException(exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return retVal;
    }

    public int borraGrupo(UsuarioGrupo ug) throws GestionException {
        int retVal = -1;
        Connection conn = null;
        try {
            conn = getConnection();
            retVal = UsuarioGrupoManager.delete(conn, ug.getLogin(), ug.getNombre());
            conn.commit();
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException e) {
                log.error("En rollback", e);
            }
            log.error("Error al actualizar usuario grupo (delete)", exc);
            throw new GestionException(exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return retVal;
    }

    public int agregaPropiedades(UsuarioPropiedades up) throws GestionException {
        int retVal = -1;
        Connection conn = null;
        try {
            conn = getConnection();
            retVal = UsuarioPropiedadesManager.insert(conn, up);
            conn.commit();
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException e) {
                log.error("En rollback", e);
            }
            log.error("Error al agregar usuario grupo (insert)", exc);
            throw new GestionException(exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return retVal;
    }

    public int actualizaPropiedades(UsuarioPropiedades up) throws GestionException {
        int retVal = -1;
        Connection conn = null;
        try {
            conn = getConnection();
            retVal = UsuarioPropiedadesManager.update(conn, up);
            conn.commit();
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException e) {
                log.error("En rollback", e);
            }
            log.error("Error al actualizar usuario grupo (update)", exc);
            throw new GestionException(exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return retVal;
    }

    public int borraPropiedades(UsuarioPropiedades up) throws GestionException {
        int retVal = -1;
        Connection conn = null;
        try {
            conn = getConnection();
            retVal = UsuarioPropiedadesManager.delete(conn, up.getLogin());
            conn.commit();
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException e) {
                log.error("En rollback", e);
            }
            log.error("Error al actualizar usuario propiedades (delete)", exc);
            throw new GestionException(exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return retVal;
    }

    public int agregaRole(UsuarioRole ur) throws GestionException {
        int retVal = -1;
        Connection conn = null;
        try {
            conn = getConnection();
            retVal = UsuarioRoleManager.insert(conn, ur);
            conn.commit();
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException e) {
                log.error("En rollback", e);
            }
            log.error("Error al agregar usuario grupo (insert)", exc);
            throw new GestionException(exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return retVal;
    }

    public int actualizaRole(UsuarioRole ur) throws GestionException {
        int retVal = -1;
        Connection conn = null;
        try {
            conn = getConnection();
            retVal = UsuarioRoleManager.update(conn, ur);
            conn.commit();
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException e) {
                log.error("En rollback", e);
            }
            log.error("Error al actualizar usuario role (update)", exc);
            throw new GestionException(exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return retVal;
    }

    public int borraRole(UsuarioRole up) throws GestionException {
        int retVal = -1;
        Connection conn = null;
        try {
            conn = getConnection();
            retVal = UsuarioRoleManager.delete(conn, up.getLogin(), up.getNombre());
            conn.commit();
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException e) {
                log.error("En rollback", e);
            }
            log.error("Error al actualizar usuario role (delete)", exc);
            throw new GestionException(exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return retVal;
    }

    public int agregaMttoGrupo(Grupo gr) throws GestionException {
        int retVal = -1;
        Connection conn = null;
        try {
            conn = getConnection();
            retVal = GrupoManager.insert(conn, gr);
            conn.commit();
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException e) {
                log.error("En rollback", e);
            }
            log.error("Error al actualizar usuario role (delete)", exc);
            throw new GestionException(exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return retVal;
    }

    public int actualizaMttoGrupo(Grupo gr) throws GestionException {
        int retVal = -1;
        Connection conn = null;
        try {
            conn = getConnection();
            retVal = GrupoManager.update(conn, gr);
            conn.commit();
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException e) {
                log.error("En rollback", e);
            }
            log.error("Error al actualizar usuario role (delete)", exc);
            throw new GestionException(exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return retVal;
    }

    public int borraMttoGrupo(Grupo gr) throws GestionException {
        int retVal = -1;
        Connection conn = null;
        try {
            conn = getConnection();
            retVal = GrupoManager.delete(conn, gr.getNombre());
            conn.commit();
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException e) {
                log.error("En rollback", e);
            }
            log.error("Error al actualizar usuario role (delete)", exc);
            throw new GestionException(exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return retVal;
    }

    public int agregaMttoRole(Role ro) throws GestionException {
        int retVal = -1;
        Connection conn = null;
        try {
            conn = getConnection();
            retVal = RoleManager.insert(conn, ro);
            conn.commit();
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException e) {
                log.error("En rollback", e);
            }
            log.error("Error al actualizar usuario role (delete)", exc);
            throw new GestionException(exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return retVal;
    }

    public int actualizaMttoRole(Role ro) throws GestionException {
        int retVal = -1;
        Connection conn = null;
        try {
            conn = getConnection();
            retVal = RoleManager.update(conn, ro);
            conn.commit();
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException e) {
                log.error("En rollback", e);
            }
            log.error("Error al actualizar usuario role (delete)", exc);
            throw new GestionException(exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return retVal;
    }

    public int borraMttoRole(Role ro) throws GestionException {
        int retVal = -1;
        Connection conn = null;
        try {
            conn = getConnection();
            retVal = RoleManager.delete(conn, ro.getNombre());
            conn.commit();
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException e) {
                log.error("En rollback", e);
            }
            log.error("Error al actualizar usuario role (delete)", exc);
            throw new GestionException(exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return retVal;
    }

    public int agregaMttoGpoProp(GrupoPropiedades go) throws GestionException {
        int retVal = -1;
        Connection conn = null;
        try {
            conn = getConnection();
            retVal = GrupoPropiedadesManager.insert(conn, go);
            conn.commit();
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException e) {
                log.error("En rollback", e);
            }
            log.error("Error al actualizar usuario role (delete)", exc);
            throw new GestionException(exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return retVal;
    }

    public int actualizaMttoGpoProp(GrupoPropiedades go) throws GestionException {
        int retVal = -1;
        Connection conn = null;
        try {
            conn = getConnection();
            retVal = GrupoPropiedadesManager.update(conn, go);
            conn.commit();
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException e) {
                log.error("En rollback", e);
            }
            log.error("Error al actualizar usuario role (delete)", exc);
            throw new GestionException(exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return retVal;
    }

    public int borraMttoGpoProp(GrupoPropiedades go) throws GestionException {
        int retVal = -1;
        Connection conn = null;
        try {
            conn = getConnection();
            retVal = GrupoPropiedadesManager.delete(conn, go.getGrupo(), go.getNombre());
            conn.commit();
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException e) {
                log.error("En rollback", e);
            }
            log.error("Error al actualizar usuario role (delete)", exc);
            throw new GestionException(exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return retVal;
    }

    public int agregaEmpleadoArea(EmpleadoArea ea) throws GestionException {
        int retVal = -1;
        Connection conn = null;
        try {
            conn = getConnection();
            retVal = EmpleadoAreaManager.insert(conn, ea);
            conn.commit();
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException e) {
                log.error("En rollback", e);
            }
            log.error("Error al agregar usuario (insert)", exc);
            throw new GestionException(exc);
        } catch (Exception exc) {
            try {
                conn.rollback();
            } catch (SQLException e) {
                log.error("En rollback", e);
            }
            log.error("Error al agregar usuario (insert)", exc);
            throw new GestionException(exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return retVal;
    }

    public int actualizaEmpleadoArea(EmpleadoArea ea) throws GestionException {
        int retVal = -1;
        Connection conn = null;
        try {
            conn = getConnection();
            retVal = EmpleadoAreaManager.update(conn, ea);
            conn.commit();
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException e) {
                log.error("En rollback", e);
            }
            log.error("Error al actualizar usuario (update)", exc);
            throw new GestionException(exc);
        } catch (Exception exc) {
            try {
                conn.rollback();
            } catch (SQLException e) {
                log.error("En rollback", e);
            }
            log.error("Error al actualizar usuario (update)", exc);
            throw new GestionException(exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return retVal;
    }

    public int borraEmpleadoArea(EmpleadoArea ea) throws GestionException {
        int retVal = -1;
        Connection conn = null;
        try {
            conn = getConnection();
            retVal = EmpleadoAreaManager.delete(conn, ea.getId());
            conn.commit();
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException e) {
                log.error("En rollback", e);
            }
            log.error("Error al actualizar usuario (delete)", exc);
            throw new GestionException(exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return retVal;
    }

    private Hashtable modificaXMLElements(Hashtable htElements, Usuario uFrom, Usuario uTo, Empleado eTo, EmpleadoArea eaTo) {
        //REMITENTE INTERNO
        if (htElements.size() > 0) {
            String login = (String) htElements.get("h_res_u_login");
            if (login != null && login.toLowerCase().equals(uFrom.getLogin().toLowerCase())) {
                htElements.put("h_res_u_login", uTo.getLogin());
            }
            login = (String) htElements.get("rem_in_u_login");
            if (login != null && login.toLowerCase().equals(uFrom.getLogin().toLowerCase())) {
                htElements.put("rem_in_u_login", uTo.getLogin());
                htElements.put("rem_in_u_nombre", eTo.getNombreCompleto());
                htElements.put("rem_in_pto_nombre", (eTo.getCargo() != null) ? eTo.getCargo() : "");
                htElements.put("rem_in_d_descripcion", eaTo.getDescripcion());
                htElements.put("rem_in_id_area", eTo.getClaveArea());
            }
            //RESPONSABLE
            login = (String) htElements.get("res_u_login");
            if (login != null && login.toLowerCase().equals(uFrom.getLogin().toLowerCase())) {
                htElements.put("res_u_login", uTo.getLogin());
                htElements.put("res_u_nombre", eTo.getNombreCompleto());
                htElements.put("res_pto_nombre", (eTo.getCargo() != null) ? eTo.getCargo() : "");
                htElements.put("res_d_desc", eaTo.getDescripcion());
            }
            //TURNADO
            login = (String) htElements.get("per_u_login");
            if (login != null && login.toLowerCase().equals(uFrom.getLogin().toLowerCase())) {
                htElements.put("per_u_login", uTo.getLogin());
                htElements.put("per_u_nombre", eTo.getNombreCompleto());
                htElements.put("per_salutacion", (eTo.getSalutacion() != null) ? eTo.getSalutacion() : "");
                htElements.put("per_pto_nombre", (eTo.getCargo() != null) ? eTo.getCargo() : "");
                htElements.put("per_d_descripcion", eaTo.getDescripcion());
            }
            //CCP
            login = (String) htElements.get("ccp_u_login");
            if (login != null && login.toLowerCase().equals(uFrom.getLogin().toLowerCase())) {
                htElements.put("ccp_u_login", uTo.getLogin());
                htElements.put("ccp_u_nombre", eTo.getNombreCompleto());
                htElements.put("ccp_salutacion", (eTo.getSalutacion() != null) ? eTo.getSalutacion() : "");
                htElements.put("ccp_pto_nombre", (eTo.getCargo() != null) ? eTo.getCargo() : "");
                htElements.put("ccp_d_descripcion", eaTo.getDescripcion());
            }
        }
        return htElements;
    }

    private Usuario copyUser(Connection conn, Usuario uFrom, Usuario uTo) throws GestionException {
        int resultado = -1;
        Usuario retVal = null;
        Usuario u;
        u = new Usuario();
        try {
            u = UsuarioManager.select(conn, uFrom);
            if (u == null) {
                throw new GestionException("No existe el usuario " + uFrom.getLogin());
            }
            u.setLogin(uTo.getLogin());
            u.setGrupos(new Hashtable());
            u.setRoles(new Hashtable());
            resultado = UsuarioManager.insert(conn, u);
            if (resultado > 0) {
                // Obtener propiedades y copiarlas
                Iterator it = null;
                Set s = null;
                Map usrProperties = new Hashtable();
                usrProperties = UsuarioPropiedadesManager.select(conn, uFrom.getLogin());
                s = usrProperties.keySet();
                it = s.iterator();
                while (it.hasNext()) {
                    String key = (String) it.next();
                    UsuarioPropiedades up = (UsuarioPropiedades) usrProperties.get(key);
                    up.setLogin(uTo.getLogin());
                    UsuarioPropiedadesManager.insert(conn, up);
                }
                // Obtener grupos y copiarlos
                Map grupos = new Hashtable();
                grupos = UsuarioGrupoManager.selectGrupos(conn, uFrom.getLogin());
                s = null;
                s = grupos.keySet();
                it = s.iterator();
                while (it.hasNext()) {
                    String key = (String) it.next();
                    Grupo g = (Grupo) grupos.get(key);
                    UsuarioGrupo ug = new UsuarioGrupo();
                    ug.setLogin(uTo.getLogin());
                    ug.setNombre(g.getNombre());
                    UsuarioGrupoManager.insert(conn, ug);
                }
                // Obtener roles y copiarlos
                Map roles = new Hashtable();
                roles = UsuarioRoleManager.selectRoles(conn, uFrom.getLogin());
                s = null;
                s = roles.keySet();
                it = s.iterator();
                while (it.hasNext()) {
                    String key = (String) it.next();
                    Role r = (Role) roles.get(key);
                    UsuarioRole ur = new UsuarioRole();
                    ur.setNombre(r.getNombre());
                    ur.setLogin(uTo.getLogin());
                    UsuarioRoleManager.insert(conn, ur);
                }
            }
        } catch (SQLException e) {
            throw new GestionException(e.toString());
        }
        retVal = u;
        return retVal;
    }

    public boolean writeXMLFile(String pathFile, Hashtable htElements, String charEnc) {
        boolean retVal = false;
        //renombra archivo de gestion original
        File originalXMLFile = new File(pathFile);
        File modifiedXMLFile = new File(pathFile + ".bak");
        originalXMLFile.renameTo(modifiedXMLFile);
        originalXMLFile = null;
        modifiedXMLFile = null;
        originalXMLFile = new File(pathFile);
        //escribe archivo de gestion modificado
        StringBuffer sbOut = new StringBuffer();
        sbOut.append("<gestion>\n");
        sbOut.append("\t<plantilla>\n");
        Enumeration en2 = htElements.keys();
        while (en2.hasMoreElements()) {
            String key = (String) en2.nextElement();
            String value = (String) htElements.get(key);
            sbOut.append("\t\t<data id=\"");
            sbOut.append(key);
            sbOut.append("\">\n");
            sbOut.append("\t\t\t<value>");
            sbOut.append(value);
            sbOut.append("</value>\n");
            sbOut.append("\t\t</data>\n");
        }
        sbOut.append("\t</plantilla>\n");
        sbOut.append("</gestion>\n");
        CharsetEncoder encoder = Charset.forName(charEnc).newEncoder();
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(originalXMLFile), encoder));
            bw.write(sbOut.toString());
            bw.flush();
            bw.close();
            bw = null;
            retVal = true;
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return retVal;
    }

    private String getWhereClause(Hashtable ht) {
        //OBTIENE LA PARTE DE LA CLAUSULA WHERE
        //QUE EXCLUYE LOS CASOS QUE NO PUDO LEER
        StringBuilder sb = new StringBuilder();
        if (ht.size() > 0) {
            sb.append(" AND ID_CASO NOT IN(");
            Enumeration en = ht.keys();
            while (en.hasMoreElements()) {
                sb.append((String) en.nextElement());
                sb.append(",");
            }
            sb.append("-99999)");
        }
        return sb.toString();
    }

    private int transferenciaExecuteQueries(Connection conn, Usuario uFrom, Usuario uTo, Empleado eTo, int tipoTransferencia, String filtroArea, String filtroFolios, String separador, String[] gavetaQueries, String whereClause) {
        int registrosModificados = -1;
        boolean errorSQL = false;
        PreparedStatement pstmnt = null;
        //EL ULTIMO QUERY SERA EL DE CG_BITACORA_OPERACION
        //PARA QUE FUNCIONEN CON LA CLAUSULA WHERECLAUSEPART2
        String whereClausePart2 = " AND ID_CASO IN (" + " SELECT distinct bo.id_caso " + " from cg_bitacora_operacion bo " + " inner join cg_bitacora_caso bc " + " on bc.id_caso=bo.id_caso and " + " bc.cerrado='N' and " + " (bo.remitente_id = '" + uFrom.getLogin() + "' or bo.responsable_id = '" + uFrom.getLogin() + "')";
        //SI SE APLICO FILTRO POR AREA
        if (filtroArea != null && filtroArea.trim().length() > 0) {
            whereClausePart2 += " AND (bo.remitente_area = '" + filtroArea + "' or bo.responsable_area = '" + filtroArea + "' or bo.turnado_area = '" + filtroArea + "') ";
            //SI SE APLICO FILTRO POR FOLIOS
        } else if (filtroFolios != null && filtroFolios.trim().length() > 0) {
            whereClausePart2 += " AND bo.ID_CASO IN (" + " SELECT c.id_caso " + " from cg_caso c " + " where " + " c.c_folio in (select element from fx_Split('" + filtroFolios + "','" + separador + "'))) ";
        }
        whereClausePart2 += " where bo.folio not like 'TMP%')";
        String query = " UPDATE CG_CASO_OPERACION SET CO_RESPONSABLE = ? " + " WHERE CO_RESPONSABLE = ? " + whereClause + whereClausePart2;
        System.out.println("query=[" + query + "]");
        try {
            pstmnt = conn.prepareStatement(query);
            pstmnt.setString(1, uTo.getLogin());
            pstmnt.setString(2, uFrom.getLogin());
            registrosModificados = pstmnt.executeUpdate();
        } catch (SQLException exc) {
            System.out.println("Error en el query de CG_CASO_OPERACION - CO_RESPONSABLE");
            exc.printStackTrace();
            errorSQL = true;
        } finally {
            if (pstmnt != null) {
                try {
                    pstmnt.close();
                } catch (SQLException exc) {
                    //ignorar.. solo estamos cerrando el preparedStatement
                }
            }
            pstmnt = null;
        }
        if (!errorSQL) {
            query = " UPDATE CG_BITACORA_CASO SET REMITENTE_ID = ?, REMITENTE_AREA = ? " + " WHERE REMITENTE_ID = ? " + whereClause + whereClausePart2;
            System.out.println("query=[" + query + "]");
            try {
                pstmnt = conn.prepareStatement(query);
                pstmnt.setString(1, uTo.getLogin());
                pstmnt.setString(2, eTo.getClaveArea());
                pstmnt.setString(3, uFrom.getLogin());
                registrosModificados += pstmnt.executeUpdate();
            } catch (SQLException exc) {
                System.out.println("Error en el query de CG_BITACORA_CASO - REMITENTE_ID");
                exc.printStackTrace();
                errorSQL = true;
            } finally {
                if (pstmnt != null) {
                    try {
                        pstmnt.close();
                    } catch (SQLException exc) {
                        //ignorar.. solo estamos cerrando el preparedStatement
                    }
                }
                pstmnt = null;
            }
        }
        if (!errorSQL) {
            query = " UPDATE CG_BITACORA_CASO SET RESPONSABLE_ID = ?, RESPONSABLE_AREA = ? " + " WHERE RESPONSABLE_ID = ? " + whereClause + whereClausePart2;
            System.out.println("query=[" + query + "]");
            try {
                pstmnt = conn.prepareStatement(query);
                pstmnt.setString(1, uTo.getLogin());
                pstmnt.setString(2, eTo.getClaveArea());
                pstmnt.setString(3, uFrom.getLogin());
                registrosModificados += pstmnt.executeUpdate();
            } catch (SQLException exc) {
                System.out.println("Error en el query de CG_BITACORA_CASO - RESPONSABLE_ID");
                errorSQL = true;
                exc.printStackTrace();
            } finally {
                if (pstmnt != null) {
                    try {
                        pstmnt.close();
                    } catch (SQLException exc) {
                        //ignorar.. solo estamos cerrando el preparedStatement
                    }
                }
                pstmnt = null;
            }
        }
        if (!errorSQL) {
            query = " UPDATE CG_BITACORA SET B_CO_RESPONSABLE_EJEC = ? " + " WHERE B_CO_RESPONSABLE_EJEC = ? " + whereClause.replace("AND ID_CASO", "AND B_ID_CASO") + whereClausePart2.replace("AND ID_CASO", "AND B_ID_CASO");
            System.out.println("query=[" + query + "]");
            try {
                pstmnt = conn.prepareStatement(query);
                pstmnt.setString(1, uTo.getLogin());
                pstmnt.setString(2, uFrom.getLogin());
                registrosModificados += pstmnt.executeUpdate();
            } catch (SQLException exc) {
                System.out.println("Error en el query de CG_BITACORA - B_CO_RESPONSABLE_EJEC");
                errorSQL = true;
                exc.printStackTrace();
            } finally {
                if (pstmnt != null) {
                    try {
                        pstmnt.close();
                    } catch (SQLException exc) {
                        //ignorar.. solo estamos cerrando el preparedStatement
                    }
                }
                pstmnt = null;
            }
        }
        if (!errorSQL) {
            query = " UPDATE CG_BITACORA SET B_CO_RESPONSABLE_SIGTE = ? " + " WHERE B_CO_RESPONSABLE_SIGTE = ? " + whereClause.replace("AND ID_CASO", "AND B_ID_CASO") + whereClausePart2.replace("AND ID_CASO", "AND B_ID_CASO");
            System.out.println("query=[" + query + "]");
            try {
                pstmnt = conn.prepareStatement(query);
                pstmnt.setString(1, uTo.getLogin());
                pstmnt.setString(2, uFrom.getLogin());
                registrosModificados += pstmnt.executeUpdate();
            } catch (SQLException exc) {
                System.out.println("Error en el query de CG_BITACORA - B_CO_RESPONSABLE_SIGTE");
                errorSQL = true;
                exc.printStackTrace();
            } finally {
                if (pstmnt != null) {
                    try {
                        pstmnt.close();
                    } catch (SQLException exc) {
                        //ignorar.. solo estamos cerrando el preparedStatement
                    }
                }
                pstmnt = null;
            }
        }
        if (!errorSQL) {
            query = " UPDATE CG_BITACORA_OPERACION SET REMITENTE_ID = ?, REMITENTE_AREA = ? " + " WHERE REMITENTE_ID = ? " + whereClause + whereClausePart2;
            System.out.println("query=[" + query + "]");
            try {
                pstmnt = conn.prepareStatement(query);
                pstmnt.setString(1, uTo.getLogin());
                pstmnt.setString(2, eTo.getClaveArea());
                pstmnt.setString(3, uFrom.getLogin());
                registrosModificados += pstmnt.executeUpdate();
            } catch (SQLException exc) {
                System.out.println("Error en el query de CG_BITACORA_OPERACION - REMITENTE");
                errorSQL = true;
                exc.printStackTrace();
            } finally {
                if (pstmnt != null) {
                    try {
                        pstmnt.close();
                    } catch (SQLException exc) {
                        //ignorar.. solo estamos cerrando el preparedStatement
                    }
                }
                pstmnt = null;
            }
        }
        if (!errorSQL) {
            query = " UPDATE CG_BITACORA_OPERACION SET RESPONSABLE_ID = ?, RESPONSABLE_AREA = ? " + " WHERE RESPONSABLE_ID = ? " + whereClause + whereClausePart2;
            System.out.println("query=[" + query + "]");
            try {
                pstmnt = conn.prepareStatement(query);
                pstmnt.setString(1, uTo.getLogin());
                pstmnt.setString(2, eTo.getClaveArea());
                pstmnt.setString(3, uFrom.getLogin());
                registrosModificados += pstmnt.executeUpdate();
            } catch (SQLException exc) {
                System.out.println("Error en el query de CG_BITACORA_OPERACION - RESPONSABLE");
                errorSQL = true;
                exc.printStackTrace();
            } finally {
                if (pstmnt != null) {
                    try {
                        pstmnt.close();
                    } catch (SQLException exc) {
                        //ignorar.. solo estamos cerrando el preparedStatement
                    }
                }
                pstmnt = null;
            }
        }
        //AQUI FALTARIA EL QUERY PARA QUE SUSTITUYA LAS OCURRENCIAS EN CG_BITACORA_OPERACION.TURNADO_ID
        //INCLUYENDO LOS CAMPOS USERXX y CG_CASO_DATO
        //update <Table> set textcolumn=Replace(SUBSTRING(textcolumn,1,DATALENGTH(textcolumn)),'findtext','replacetext') where <Condition>
        //MODIFICA EL EXPEDIENTE DE FORTIMAX
        //utilizando los queries que se guardaron en el array de strings
        if (!errorSQL && gavetaQueries != null) {
            for (int i = 0; i < gavetaQueries.length; i++) {
                query = gavetaQueries[i];
                try {
                    pstmnt = conn.prepareStatement(query);
                    int tmpRM = pstmnt.executeUpdate();
                    registrosModificados += tmpRM;
                    System.out.println("query=[" + query + "] registros modificados=[" + tmpRM + "] acumulado=[" + registrosModificados + "]");
                } catch (SQLException exc) {
                    System.out.println("Error en el query de IMXEXPEDIENTES - REMITENTE INTERNO");
                    errorSQL = true;
                    exc.printStackTrace();
                } finally {
                    if (pstmnt != null) {
                        try {
                            pstmnt.close();
                        } catch (SQLException exc) {
                            //ignorar.. solo estamos cerrando el preparedStatement
                        }
                    }
                    pstmnt = null;
                }
                //si hay error, ya no continua ejecutando los queries
                if (errorSQL) {
                    break;
                }
            }
        }
        //si hay error regreso negativo para que haga rollback
        if (errorSQL) {
            registrosModificados = registrosModificados * -1;
        }
        return registrosModificados;
    }

    private void createZipFile(String zipFileName, Hashtable ht1, Hashtable ht2) {
        byte[] buf = new byte[1024];
        try {
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFileName));
            Enumeration en = ht1.keys();
            while (en.hasMoreElements()) {
                String llave = (String) en.nextElement();
                String[] valores = (String[]) ht2.get(llave);
                FileInputStream in = new FileInputStream(valores[3]);
                out.putNextEntry(new ZipEntry(valores[3]));
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.closeEntry();
                in.close();
            }
            out.finish();
            out.close();
            out.flush();
            out = null;
        } catch (IOException e) {
        } finally {
        }
    }
}
