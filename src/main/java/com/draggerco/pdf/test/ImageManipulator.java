package com.draggerco.pdf.test;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class ImageManipulator {

    private static final Logger log = LoggerFactory.getLogger(ImageManipulator.class);

    private static final String WORK_DIR = "/tmp/ExpedientExport/";

    public static void main(String[] args) throws Exception {
        String path = "/Vicente/sp";
        String[] files = (new File(path)).list();
        List<String> procesar = new ArrayList<String>();
        procesarArchivos(path, files, procesar);
        ImageManipulator manipulator = new ImageManipulator();
        int nFile = 1;
        String concepto = "gastos de alimentos por día laborado, a los brigadistas que se encuentren desarrollando tareas de combate, incluyendo los días de traslado al sitio de combate y los días de retorno del combate a su centro de trabajo habitual";
        for (String fProc : procesar) {
            log.info("Object: {}", "Se procesara archivo " + nFile + " de " + procesar.size() + ": " + fProc);
            manipulator.protegeSolicitudPago(fProc, concepto);
            nFile++;
        }
    }

    public static void procesarArchivos(String parentPath, String[] pathList, List<String> result) {
        for (String path : pathList) {
            File f = new File(parentPath + "/" + path + "/");
            if (f.isDirectory()) {
                procesarArchivos(f.getPath(), f.list(), result);
            } else if ("Solicitud de Pago Firmada.pdf".equalsIgnoreCase(f.getName()))
                result.add(f.getAbsolutePath());
        }
    }

    public ImageManipulator() {
        File f = new File(WORK_DIR);
        if (!f.exists())
            f.mkdirs();
        f = null;
    }

    private void closeInputStream(InputStream stream) {
        if (stream != null)
            try {
                stream.close();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            } finally {
                stream = null;
            }
    }

    private boolean copyFile(InputStream source, String copyPath) throws IOException {
        File outpuFile = new File(copyPath);
        OutputStream outStream = new FileOutputStream(outpuFile);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = source.read(buffer)) > 0) {
            outStream.write(buffer, 0, length);
        }
        outStream.flush();
        outStream.close();
        outStream = null;
        return true;
    }

    public String cutImage(String sourceImage) throws Exception {
        BufferedImage image = null;
        BufferedImage cutImage1 = null;
        File fOut = new File(sourceImage);
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(fOut);
            image = ImageIO.read(fis);
            int widthImg = image.getWidth();
            int heightImg = image.getHeight();
            int x = 0;
            //			int y = heightImg - Integer.parseInt( String.valueOf( Math.round( heightImg * 0.25 ) ) );
            int y = heightImg - Integer.parseInt(String.valueOf(Math.round(heightImg * 0.27))) - (heightImg / 31);
            int width = widthImg;
            //			int height = Integer.parseInt( String.valueOf( Math.round( heightImg * 0.25 ) ) );
            cutImage1 = image.getSubimage(x, y, width, heightImg - y);
            ImageIO.write(cutImage1, "PNG", fOut);
            return sourceImage;
        } finally {
            closeInputStream(fis);
            image = null;
            cutImage1 = null;
            fOut = null;
        }
    }

    public void extractImage(String pathPDFSource, String imageOutputPath) throws Exception {
        InputStream pdfWorkIS = null;
        try {
            pdfWorkIS = new FileInputStream(pathPDFSource);
            PDDocument document = PDDocument.load(pdfWorkIS);
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            BufferedImage image = pdfRenderer.renderImageWithDPI(0, 300, ImageType.GRAY);
            // BufferedImage image = pdfRenderer.renderImage( 0 );
            log.info("Object: {}", String.valueOf(image));
            ImageIOUtil.writeImage(image, imageOutputPath, 300);
            document.close();
        } finally {
            closeInputStream(pdfWorkIS);
        }
    }

    private String generateImagePDF(String imageInputPath) throws Exception {
        PDDocument document = null;
        InputStream inImg = null;
        BufferedImage bimg = null;
        PDPageContentStream contentStream = null;
        String pdfPath = ImageManipulator.WORK_DIR + getFileWithoutExtencion(getFileName(imageInputPath)) + "_modified.pdf";
        try {
            inImg = new FileInputStream(imageInputPath);
            bimg = ImageIO.read(inImg);
            float width = bimg.getWidth();
            float height = bimg.getHeight();
            document = new PDDocument();
            PDPage page = new PDPage(new PDRectangle(width, height));
            document.addPage(page);
            // PDImageXObject img = JPEGFactory.createFromStream( document,
            // inPDFImg );
            PDImageXObject img = LosslessFactory.createFromImage(document, bimg);
            contentStream = new PDPageContentStream(document, page);
            contentStream.drawImage(img, 0, 0);
            contentStream.close();
            document.save(pdfPath);
            return pdfPath;
        } finally {
            closeInputStream(inImg);
            if (document != null)
                try {
                    document.close();
                } catch (Exception e) {
                    log.warn("Object: {}", "Problemas cerrando documento: " + e);
                }
        }
    }

    /**
     * @param nameFile
     * @return
     */
    private String getFileName(String nameFile) {
        if (nameFile.indexOf("\\") > 0 || nameFile.indexOf("/") >= 0) {
            String separador = nameFile.indexOf("\\") > 0 ? "\\\\" : "/";
            String[] componentes = nameFile.split(separador);
            String file = componentes[componentes.length - 1];
            return file;
        } else
            return nameFile;
    }

    /*
	 * 
	 */
    private String getFileWithoutExtencion(String fileName) {
        String sinExt = "";
        int indexPunto = fileName.lastIndexOf(".");
        if (indexPunto > 0)
            sinExt = fileName.substring(0, indexPunto);
        return sinExt;
    }

    public void processImage(String imagePath) throws Exception {
        BufferedImage img = null;
        Graphics2D g = null;
        try {
            img = ImageIO.read(new File(imagePath));
            g = img.createGraphics();
            int imxWidth = img.getWidth();
            int imxHeight = img.getHeight();
            int x1Testar = Math.round(imxWidth * 0.5f);
            int y1Testar = Math.round(imxHeight * 0.25f);
            int x2Testar = imxWidth;
            int y2Testar = Math.round(imxHeight * 0.5f);
            g.setColor(Color.WHITE);
            g.fillRect(0, y1Testar, x1Testar, Math.round(imxHeight * .0973f));
            g.fillRect(0, y2Testar, x2Testar, Math.round(imxHeight * .25f));
            File outputfile = new File(imagePath);
            ImageIO.write(img, "png", outputfile);
        } finally {
            if (g != null)
                g.dispose();
            img = null;
            g = null;
        }
    }

    /**
     * Metodo para bloquear la cuenta bancaria en la solicitud de pago.
     * <ul>
     * <li>Paso 1 Copiar archivo para previnir edicion accidental.</li>
     * <li>Paso 2: Extraer imagen de pagina 1.</li>
     * <li>Paso 2.1: Si no es imagen reportar.</li>
     * <li>Paso 3: Validar que la imagen esta vertical ( x<y ) paso</li>
     * <li>Paso 3.1: Rotar si esta en horizontal.</li>
     * <li>Paso 4 Escalar a la medida estandar a 1700 X 2200 de requerirse.</li>
     * <li>Paso 5: Editar imagen.</li>
     * <li>Paso 6: Crear PDF e Insertar la imagen en el pdf.</li>
     * <li>Paso 7: Cerrar flujos.</li>
     * </ul>
     *
     * @param path
     * @return
     * @throws Exception
     */
    public String protegeSolicitudPago(String path, String concepto) throws Exception {
        log.info("Initilizating image protection");
        String pathResult = "";
        InputStream is = null;
        File workinCopyFile = null;
        String workingCopyPath = "";
        String workingImagePath = "";
        synchronized (this) {
            workingCopyPath = ImageManipulator.WORK_DIR + "_work_" + System.currentTimeMillis() + ".pdf";
            workingImagePath = ImageManipulator.WORK_DIR + "_work_" + System.currentTimeMillis() + ".png";
        }
        try {
            workinCopyFile = new File(workingCopyPath);
            is = new FileInputStream(path);
            copyFile(is, workinCopyFile.getAbsolutePath());
            is.close();
            is = null;
            extractImage(workinCopyFile.getAbsolutePath(), workingImagePath);
            // scaleImage( workingImagePath, 1700, 2200 );
            cutImage(workingImagePath);
            // processImage( workingImagePath );
            //			ImageJoiner.joinImageAndText( workingImagePath, concepto );
            pathResult = generateImagePDF(workingImagePath);
            return pathResult;
        } finally {
            closeInputStream(is);
            workingCopyPath = null;
            if (workinCopyFile != null) {
                workinCopyFile.delete();
                workinCopyFile = null;
            }
            File f = new File(workingImagePath);
            f.delete();
            workingImagePath = null;
        }
    }

    public String protegeSolicitudPagoImg(String path, String imgType) throws Exception {
        log.info("Initilizating image protection");
        String pathResult = "";
        InputStream is = null;
        File workinCopyFile = null;
        String workingCopyPath = ImageManipulator.WORK_DIR + "_work_" + System.currentTimeMillis() + "." + imgType;
        try {
            workinCopyFile = new File(workingCopyPath);
            is = new FileInputStream(path);
            copyFile(is, workinCopyFile.getAbsolutePath());
            is.close();
            is = null;
            cutImage(workinCopyFile.getAbsolutePath());
            pathResult = generateImagePDF(workinCopyFile.getAbsolutePath());
            return pathResult;
        } finally {
            closeInputStream(is);
            workingCopyPath = null;
            if (workinCopyFile != null) {
                workinCopyFile.delete();
                workinCopyFile = null;
            }
        }
    }

    public void scaleImage(String sourceImage, int width, int height) throws Exception {
        Image img;
        BufferedImage bufferedThumbnail = null;
        File fOut = new File(sourceImage);
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(fOut);
            img = ImageIO.read(fis);
            if (img.getWidth(null) == width && img.getHeight(null) == height) {
                log.debug("Width and heigth are same as image dimensions. Do nothing");
                return;
            }
            img = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            bufferedThumbnail = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_RGB);
            bufferedThumbnail.getGraphics().drawImage(img, 0, 0, null);
            ImageIO.write(bufferedThumbnail, "PNG", fOut);
        } finally {
            closeInputStream(fis);
            img = null;
            bufferedThumbnail = null;
            fOut = null;
        }
    }
}
