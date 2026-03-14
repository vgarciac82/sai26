package com.axtel.cfdi.stamp.service;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.axtel.cfdi.exceptions.FileManagmentException;
import com.axtel.cfdi.stamp.core.Drive;
import com.axtel.cfdi.stamp.core.FileManagment;
import com.axtel.cfdi.stamp.core.VirtualFile;
import com.axtel.cfdi.stamp.core.Volumen;
import com.axtel.cfdi.stamp.core.VolumenRepositoryInterface;
import com.axtel.cfdi.stamp.repository.DriveRepositoryInterface;

public class VolumenService implements VolumenServiceInterface {

    private static final String IP_HEX;

    private static final String[] letters = { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z" };

    private static final Logger log = LoggerFactory.getLogger(VolumenService.class);

    static {
        try {
            StringBuilder strIPHex = new StringBuilder("");
            String strTemp = "";
            InetAddress addr = InetAddress.getLocalHost();
            byte[] ipaddr = addr.getAddress();
            for (int i = 0; i < ipaddr.length; i++) {
                Byte b = new Byte(ipaddr[i]);
                strTemp = Integer.toHexString(b.intValue() & 0x000000ff);
                while (strTemp.length() < 2) strTemp = '0' + strTemp;
                strIPHex.append(strTemp);
            }
            IP_HEX = strTemp.toString();
        } catch (Exception e) {
            throw new RuntimeException(e.toString(), e.getCause());
        }
    }

    private static String nextVolumenCharacter(String ch) {
        String sequenceChar = "";
        try {
            sequenceChar = String.valueOf(Integer.parseInt(ch) + 1);
        } catch (NumberFormatException e) {
            for (int i = 0; i < letters.length; i++) {
                if (ch.equals(letters[i])) {
                    sequenceChar = letters[i + 1];
                }
            }
        }
        return sequenceChar;
    }

    public DriveRepositoryInterface getDriveRepository() {
        return driveRepository;
    }

    public void setDriveRepository(DriveRepositoryInterface driveRepository) {
        this.driveRepository = driveRepository;
    }

    public VolumenRepositoryInterface getVolumenRepository() {
        return volumenRepository;
    }

    public void setVolumenRepository(VolumenRepositoryInterface volumenRepository) {
        this.volumenRepository = volumenRepository;
    }

    private DriveRepositoryInterface driveRepository;

    private VolumenRepositoryInterface volumenRepository;

    @Override
    public VirtualFile generateFileLocation(Connection conn, String extension) throws FileManagmentException {
        try {
            Volumen volumen = getVolumen(conn);
            StringBuilder fileName = nexFileName();
            File f = new File(volumen.getPath(), fileName.toString() + "." + FileManagment.DEFAULT_EXTENSION);
            log.debug("Object: {}", "Se genero la ruta de archivo definitivo: " + f.getAbsolutePath());
            log.trace("Validando que la ruta generada no exista, de ser asi se lanza excepcion ya que no fue capaz de generar una ruta unica");
            log.trace("Object: {}", "Existe? " + f.exists());
            if (f.exists())
                throw new FileManagmentException("Se genero nombre de archivo que colisionaria con uno existente.");
            Path path = f.toPath();
            log.debug("Object: {}", "Se genero PATH: " + path);
            log.info("Object: {}", "Generada ubicacion de archivo " + fileName + " en [" + volumen + "]");
            VirtualFile vFile = new VirtualFile();
            vFile.setVolumen(volumen.getVolumen());
            vFile.setFilePath(path);
            vFile.setFileType(StringUtils.upperCase(extension));
            return vFile;
        } catch (NoSuchAlgorithmException e) {
            throw new FileManagmentException("No fue posible generar el nombre del archivo debido a la falta de algoritmo: " + e.toString(), e.getCause());
        }
    }

    @Override
    public Volumen getVolumen(Connection connection) throws FileManagmentException {
        log.trace("Solicitando volumen activo");
        try {
            Drive drive = driveRepository.selectActive(connection);
            if (drive == null)
                throw new FileManagmentException("No hay unidad activa");
            Volumen volumen = volumenRepository.getActive(connection, drive);
            if (volumen == null)
                throw new FileManagmentException("No hay volumen activo");
            String currentVol = volumen.getVolumen();
            volumen.setVolumen(manageVolumen(connection, volumen));
            if (!currentVol.equals(volumen.getVolumen())) {
                volumenRepository.closeVolumen(connection, currentVol, volumen);
                volumen.setDirectoryPath(makeDir(volumen.getDriveUnit().getDrive(), volumen.getDriveUnit().getBasePath(), volumen.getDirectoryPath(), 0));
                volumenRepository.insert(connection, volumen);
                return volumen;
            }
            File v = new File(volumen.getDriveUnit().getDrive() + volumen.getDriveUnit().getBasePath() + volumen.getDirectoryPath());
            try {
                if (!v.exists())
                    if (!makeDirectories(v))
                        throw new SQLException("No se logro crear directorio " + v.getPath());
            } catch (Exception exc) {
                throw new SQLException(exc);
            }
            return volumen;
        } catch (SQLException e) {
            throw new FileManagmentException(e);
        }
    }

    private String makeDir(String drive, String basePath, String directoryPath, int level) throws SQLException {
        String newDirPath = directoryPath;
        if (directoryPath == null)
            throw new SQLException("La ruta_directorio no debe ser nula");
        if (((null + File.separator).equals(directoryPath)) && (level > 0))
            throw new SQLException("Se alcanzo el limite maximo de directorios");
        File pathDir = new File(newDirPath);
        File realDir = new File(drive + basePath + newDirPath);
        String[] dirList = realDir.list(new FilenameFilter() {

            public boolean accept(File f, String s) {
                return f.isDirectory();
            }
        });
        int totDir = (dirList == null) ? 0 : dirList.length;
        if (totDir >= FileManagment.MAX_DIR_VOL) {
            newDirPath = makeDir(drive, basePath, pathDir.getParent() + File.separator, level + 1);
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
                if (!makeDirectories(d))
                    throw new SQLException("No se logro crear directorio " + d.getPath());
                newDirPath = pathDir.getPath() + File.separator + newDirname + ((dirSuffix[level] != null) ? dirSuffix[level] + File.separator : File.separator);
            } else if (!realDir.exists()) {
                if (!realDir.mkdirs())
                    throw new SQLException("No se logro crear directorio " + realDir.getPath());
            }
        }
        return newDirPath;
    }

    private synchronized boolean makeDirectories(File path) {
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

    private String manageVolumen(Connection connection, Volumen volumen) throws SQLException {
        Integer totFiles = volumenRepository.countByVolumen(connection, volumen);
        if (totFiles >= FileManagment.MAX_ARCH_VOL) {
            String volPrefix = volumen.getVolumen().substring(0, 3);
            String volumenStr = volumen.getVolumen().substring(3);
            return volPrefix + nextVolumenSequence(connection, volumenStr);
        }
        return volumen.getVolumen();
    }

    private StringBuilder nexFileName() throws NoSuchAlgorithmException {
        StringBuilder fileName = new StringBuilder(IP_HEX);
        StringBuilder timeMillisStr = new StringBuilder(Long.toHexString(System.currentTimeMillis()));
        while (timeMillisStr.length() < 12) timeMillisStr.insert(0, "0");
        fileName.append(timeMillisStr);
        SecureRandom prng = SecureRandom.getInstance("SHA1PRNG");
        StringBuilder randomStr = new StringBuilder(Integer.toHexString(prng.nextInt()));
        while (randomStr.length() < 8) randomStr.insert(0, "0");
        fileName.append(randomStr.substring(4));
        StringBuilder hashObj = new StringBuilder(Long.toHexString(System.identityHashCode((Object) new String())));
        while (hashObj.length() < 8) hashObj.insert(0, "0");
        fileName.append(hashObj);
        return fileName;
    }

    private String nextVolumenSequence(Connection connection, String volumenStr) throws SQLException {
        String value = volumenStr;
        String lastChar = null;
        if (("zzzzz".equals(value)) || ("".equals(value)) || (value == null))
            throw new SQLException("Numero maximo de volumenes alcanzado");
        lastChar = value.substring(value.length() - 1).toLowerCase();
        if (lastChar.equals("z"))
            value = nextVolumenSequence(connection, value.substring(0, value.length() - 1));
        else if (lastChar.equals("9"))
            value = value.substring(0, value.length() - 1) + "a";
        else
            value = value.substring(0, value.length() - 1) + nextVolumenCharacter(lastChar);
        return (value + "00000".substring(0, 5 - value.length()));
    }
}
