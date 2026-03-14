package com.syc.fortimax.core;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import com.syc.cfdi.db.CloseObject;
import com.syc.utils.TiffFilenameFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VolumenManager {

    // private DataSource ds = null;
    private static Logger log = LoggerFactory.getLogger(VolumenManager.class);

    private static Object sync = new Object();

    /*
	 * 3 - 600 - 999
	 */
    public static int VOL_MAX_DIR = 999;

    /*
	 * 5 - 3072
	 */
    public static int VOL_MAX_ARCH = 3072;

    public static Volumen getVolumen(Connection conn) throws SQLException {
        Volumen vol = new Volumen(null);
        PreparedStatement pstmnt = null, pstmntDir = null, pstmntUpd = null, pstmntInsert = null, pstmntVolumen = null;
        ResultSet rs = null, rsVolumen = null, rsDir = null;
        synchronized (sync) {
            try {
                pstmnt = conn.prepareStatement("SELECT unidad, tipo_dispositivo, ruta_base FROM imx_unidad_volumen WHERE estado_unidad = ?");
                pstmnt.setInt(1, vol.getEstadoUnidad());
                rs = pstmnt.executeQuery();
                if (!rs.next())
                    throw new SQLException("No se encontro unidad activa");
                vol.setUnidad(rs.getString("unidad"));
                vol.setTipoDispositivo(rs.getString("tipo_dispositivo"));
                vol.setRutaBase(rs.getString("ruta_base"));
                pstmntVolumen = conn.prepareStatement("SELECT volumen FROM imx_volumen WITH (NOLOCK) WHERE unidad_disco = ?  AND tipo_volumen = ? AND capacidad = ?");
                pstmntVolumen.setString(1, vol.getUnidad());
                pstmntVolumen.setString(2, vol.getTipoVolumen());
                pstmntVolumen.setString(3, vol.getCapacidad());
                rsVolumen = pstmntVolumen.executeQuery();
                if (!rsVolumen.next())
                    throw new SQLException("No se encontro un volumen activo");
                vol.setVolumen(rsVolumen.getString(1));
                pstmntDir = conn.prepareStatement("SELECT ruta_directorio FROM imx_volumen WITH (NOLOCK) WHERE volumen = ? AND unidad_disco = ? AND tipo_volumen = ?");
                pstmntDir.setString(1, vol.getVolumen());
                pstmntDir.setString(2, vol.getUnidad());
                pstmntDir.setString(3, vol.getTipoVolumen());
                rsDir = pstmntDir.executeQuery();
                if (!rsDir.next())
                    throw new SQLException("No se encontro un volumen activo");
                vol.setRutaDirectorio(rsDir.getString("ruta_directorio"));
                String volumenAnterior = vol.getVolumen();
                vol.setVolumen(administraVolumen(vol));
                if (!volumenAnterior.equals(vol.getVolumen())) {
                    pstmntUpd = conn.prepareStatement("UPDATE imx_volumen SET capacidad = '0' WHERE volumen = ? AND unidad_disco = ? AND tipo_volumen = ? AND capacidad = ?");
                    pstmntUpd.setString(1, volumenAnterior);
                    pstmntUpd.setString(2, vol.getUnidad());
                    pstmntUpd.setString(3, vol.getTipoVolumen());
                    pstmntUpd.setString(4, "1");
                    vol.setRutaDirectorio(creaDirectorio(vol.getUnidad(), vol.getRutaBase(), vol.getRutaDirectorio(), 0));
                    if (pstmntUpd.executeUpdate() != 0) {
                        pstmntInsert = conn.prepareStatement("INSERT INTO imx_volumen (volumen, unidad_disco, ruta_base, ruta_directorio, capacidad, tipo_volumen) VALUES (?, ?, ?, ?, ?, ?)");
                        pstmntInsert.setString(1, vol.getVolumen());
                        pstmntInsert.setString(2, vol.getUnidad());
                        pstmntInsert.setString(3, vol.getRutaBase());
                        pstmntInsert.setString(4, vol.getRutaDirectorio() + vol.getVolumen() + File.separator);
                        pstmntInsert.setString(5, "1");
                        pstmntInsert.setString(6, vol.getTipoVolumen());
                        pstmntInsert.executeUpdate();
                    }
                    File v = new File(vol.getUnidad() + vol.getRutaBase() + vol.getRutaDirectorio() + vol.getVolumen());
                    try {
                        if (!v.exists())
                            if (!makeDirectories(v))
                                throw new SQLException("No se logro crear directorio " + v.getPath());
                    } catch (Exception exc) {
                        log.error(exc.getMessage(), exc);
                        throw new SQLException(exc);
                    }
                } else {
                    File v = new File(vol.getUnidad() + vol.getRutaBase() + vol.getRutaDirectorio() + vol.getVolumen());
                    try {
                        if (!v.exists())
                            if (!makeDirectories(v))
                                throw new SQLException("No se logro crear directorio " + v.getPath());
                    } catch (Exception exc) {
                        log.error(exc.getMessage(), exc);
                        throw new SQLException(exc);
                    }
                }
            } finally {
                CloseObject.closeObject(pstmnt);
                CloseObject.closeObject(pstmntDir);
                CloseObject.closeObject(pstmntUpd);
                CloseObject.closeObject(pstmntInsert);
                CloseObject.closeObject(pstmntVolumen);
                CloseObject.closeObject(rs);
                CloseObject.closeObject(rsVolumen);
                CloseObject.closeObject(rsDir);
            }
            return vol;
        }
    }

    private static String administraVolumen(Volumen vol) throws SQLException {
        File file = new File(vol.getUnidad() + vol.getRutaBase() + vol.getRutaDirectorio() + vol.getVolumen());
        String[] lista = file.list(new TiffFilenameFilter());
        int totFiles = (lista != null) ? lista.length : 0;
        String volPrefix = vol.getVolumen().substring(0, 3);
        String volumen = vol.getVolumen().substring(3);
        if (totFiles >= VOL_MAX_ARCH)
            return (volPrefix + nextVolumenSequence(volumen));
        return vol.getVolumen();
    }

    private static String nextVolumenSequence(String volumen) throws SQLException {
        String value = volumen;
        String lastChar = null;
        if (("zzzzz".equals(value)) || ("".equals(value)) || (value == null))
            throw new SQLException("Numero maximo de volumenes alcanzado");
        lastChar = value.substring(value.length() - 1).toLowerCase();
        if (lastChar.equals("z"))
            value = nextVolumenSequence(value.substring(0, value.length() - 1));
        else if (lastChar.equals("9"))
            value = value.substring(0, value.length() - 1) + "a";
        else
            value = value.substring(0, value.length() - 1) + siguienteCaracter(lastChar);
        return (value + "00000".substring(0, 5 - value.length()));
    }

    private static String siguienteCaracter(String ch) {
        try {
            return String.valueOf(Integer.parseInt(ch) + 1);
        } catch (NumberFormatException e) {
            String[] letters = { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z" };
            for (int i = 0; i < letters.length; i++) {
                if (ch.equals(letters[i])) {
                    return letters[i + 1];
                }
            }
        }
        return null;
    }

    public static String creaDirectorio(String unidad_disco, String ruta_base, String ruta_directorio, int level) throws SQLException {
        String newRutaDirectorio = ruta_directorio;
        if (ruta_directorio == null)
            throw new SQLException("La ruta_directorio no debe ser nula");
        if (((null + File.separator).equals(ruta_directorio)) && (level > 0))
            throw new SQLException("Se alcanzo el limite maximo de directorios");
        File pathDir = new File(newRutaDirectorio);
        File realDir = new File(unidad_disco + ruta_base + newRutaDirectorio);
        String[] dirList = realDir.list(new FilenameFilter() {

            public boolean accept(File f, String s) {
                return f.isDirectory();
            }
        });
        int totDir = (dirList == null) ? 0 : dirList.length;
        if (totDir >= VOL_MAX_DIR) {
            newRutaDirectorio = creaDirectorio(unidad_disco, ruta_base, pathDir.getParent() + File.separator, level + 1);
        } else {
            if (level > 0) {
                // Se ordena, puede venir en desorden
                Arrays.sort(dirList);
                String[] dirSuffix = { null, null, File.separator + "000", File.separator + "000" + File.separator + "000" };
                String newDirname = null;
                try {
                    newDirname = String.valueOf(Integer.parseInt(realDir.list()[realDir.list().length - 1]) + 1);
                } catch (NumberFormatException ne) {
                    newDirname = "";
                }
                newDirname = "000".substring(0, 3 - newDirname.length()) + newDirname;
                File d = new File(realDir.getPath() + File.separator + newDirname + ((dirSuffix[level] != null) ? dirSuffix[level] + File.separator : File.separator));
                // if (!d.mkdirs())
                if (!makeDirectories(d))
                    throw new SQLException("No se logro crear directorio " + d.getPath());
                newRutaDirectorio = pathDir.getPath() + File.separator + newDirname + ((dirSuffix[level] != null) ? dirSuffix[level] + File.separator : File.separator);
            } else if (!realDir.exists()) {
                if (!realDir.mkdirs())
                    throw new SQLException("No se logro crear directorio " + realDir.getPath());
            }
        }
        return newRutaDirectorio;
    }

    private synchronized static boolean makeDirectories(File path) {
        if (path == null)
            throw new NullPointerException("path no debe ser nulo");
        if (path.exists())
            return true;
        if (path.mkdir())
            return true;
        File canonPath = null;
        try {
            canonPath = path.getCanonicalFile();
        } catch (IOException e) {
            return false;
        }
        String parent = canonPath.getParent();
        return (parent != null) && (makeDirectories(new File(parent)) && canonPath.mkdir());
    }
}
