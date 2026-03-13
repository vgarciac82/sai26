package com.syc.ejercido.pagado;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.jfree.util.Log;
import com.syc.contable.core.AplicacionContable;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "SubirArchivosBaseServlet", urlPatterns = { "/gstnmngr/SubirArchivosBase" })
public class SubirArchivosBaseServlet extends HttpServlet {

    /**
     */
    private static final long serialVersionUID = 1L;

    private static Logger log = LoggerFactory.getLogger(AplicacionContable.class);

    /**
     * Constructor of the object.
     */
    public SubirArchivosBaseServlet() {
        super();
    }

    /**
     * Destruction of the servlet. <br>
     */
    public void destroy() {
        // Just puts "destroy" string in log
        super.destroy();
        // Put your code here
    }

    /**
     * The doGet method of the servlet. <br>
     *
     * This method is called when a form has its tag value method equals to get.
     *
     * @param request
     *            the request send by the client to the server
     * @param response
     *            the response send by the server to the client
     * @throws ServletException
     *             if an error occurred
     * @throws IOException
     *             if an error occurred
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
        out.println("<HTML>");
        out.println("  <HEAD><TITLE>A Servlet</TITLE></HEAD>");
        out.println("  <BODY>");
        out.print("    This is ");
        out.print(this.getClass());
        out.println(", using the GET method");
        out.println("  </BODY>");
        out.println("</HTML>");
        out.flush();
        out.close();
    }

    /**
     * The doPost method of the servlet. <br>
     *
     * This method is called when a form has its tag value method equals to
     * post.
     *
     * @param request
     *            the request send by the client to the server
     * @param response
     *            the response send by the server to the client
     * @throws ServletException
     *             if an error occurred
     * @throws IOException
     *             if an error occurred
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect("index.jsp");
            return;
        }
        List<FileItem> fileItems = new ArrayList<FileItem>();
        Map<String, String> fieldMap = new Hashtable<String, String>();
        List<FileItem> fileList = new ArrayList<FileItem>();
        String pathUrl = request.getContextPath();
        String pathBase = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + pathUrl + "/";
        String mensaje = "";
        try {
            /*
			 * DiskFileUpload fu = new DiskFileUpload(); fu.setSizeMax(1024 *
			 * 512 * 10); // 512 K fu.setSizeThreshold(4096); List fileItems =
			 * fu.parseRequest(request);
			 * 
			 * String tipoArchivo = ""; Iterator i = fileItems.iterator();
			 * FileItem actual = null; while(i.hasNext()){ actual = (FileItem)
			 * i.next(); System.out.println("fielName::"+actual.getFieldName());
			 * if(actual.getFieldName().equals("tipoArchivo")){ tipoArchivo =
			 * actual.getString();
			 * //System.out.println("tipoArchivo::"+actual.getString()); } }
			 */
            fileItems = procesaArchivos(request);
            for (FileItem item : fileItems) {
                if (item.isFormField()) {
                    fieldMap.put(item.getFieldName(), item.getString());
                } else {
                    fileList.add(item);
                }
            }
            String tipoArchivo = fieldMap.get("tipoArchivo");
            InputStream in = fileList.get(0).getInputStream();
            String valor = enviaRuta(in, tipoArchivo);
            if (valor == "guardado") {
                mensaje = "Archivo Cargado";
            } else if (valor.equals("no_guardado")) {
                mensaje = "Error: No Se Guardaron Los Registros Correctamente";
            } else {
                mensaje = "Error: En el Orden de Columnas. Favor de verificar la columna " + valor;
            }
        } catch (Exception e) {
            log.error("Error: " + e);
            mensaje = "Error: No Se Guardaron Los Registros Correctamente";
        } finally {
            // if (in != null){ in.close(); }
            // in = null;
        }
        response.sendRedirect(pathBase + "Generador/SubirArchivo.jsp?mensaje=" + mensaje);
    }

    @SuppressWarnings("unchecked")
    public InputStream cargarArchivo(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathArchivo = "";
        try {
            // Se construye un objeto para que parsee la petición
            DiskFileUpload fu = new DiskFileUpload();
            // Tamaño máximo que aceptará el archivo
            // El tamaño no importa
            fu.setSizeMax(-1);
            // Si excede el 1 Gb en memoria lo escribe a disco.
            fu.setSizeThreshold(1048576);
            pathArchivo = getServletContext().getRealPath("upload/ejercidoPagado");
            File file = new File(pathArchivo);
            if (!file.exists()) {
                file.mkdirs();
            }
            //System.out.println("Ruta:" + pathArchivo);
            fu.setRepositoryPath(pathArchivo);
            List<FileItem> fileItems = fu.parseRequest(request);
            Iterator<FileItem> i = fileItems.iterator();
            FileItem actual = null;
            while (i.hasNext()) {
                actual = i.next();
                if (actual.isFormField())
                    continue;
                return actual.getInputStream();
            }
        } catch (Exception e) {
            log.error("Error: " + e);
            throw new IOException(e);
        }
        throw new IOException("No se encontro archivo alguno en la peticion");
    }

    public String enviaRuta(InputStream in, String tipoArchivo) throws FileNotFoundException {
        // Si es true se agrega para ejecutar el procedimeinto y si es false es para insertar directamente en columna
        boolean subirInformacion = false;
        List<String> mapa = new ArrayList<String>();
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String sCadena = "";
        Hashtable<String, Integer> nomColumna = new Hashtable<String, Integer>();
        String valorReturn = "";
        boolean columnas = true;
        String guardaLinea = "";
        String limpiaTabla = "";
        try {
            CargaArchivo guardaSicop = new CargaArchivo();
            int primerLinea = 1;
            int commitCien = 1;
            while ((sCadena = br.readLine()) != null) {
                System.out.println("linea:" + primerLinea);
                if (subirInformacion) {
                } else {
                    //System.out.println("cadena:" + sCadena);
                    String[] celdas = sCadena.split(",");
                    if (tipoArchivo.equals("SIAFF")) {
                        if (primerLinea == 1) {
                            if (!celdas[0].trim().equals("RAMO_CR")) {
                                guardaLinea = "RAMO_CR";
                                break;
                            }
                            if (!celdas[1].trim().equals("UNIDAD_CR")) {
                                guardaLinea = "UNIDAD_CR";
                                break;
                            }
                            if (!celdas[2].trim().equals("TIPO_CLC")) {
                                guardaLinea = "TIPO_CLC";
                                break;
                            }
                            if (!celdas[3].trim().equals("FOLIO_CLC")) {
                                guardaLinea = "FOLIO_CLC";
                                break;
                            }
                            if (!celdas[4].trim().equals("FOLIO_DEPENDENCIA")) {
                                guardaLinea = "FOLIO_DEPENDENCIA";
                                break;
                            }
                            if (!celdas[5].trim().equals("ESTATUS_CLC")) {
                                guardaLinea = "ESTATUS_CLC";
                                break;
                            }
                            if (!celdas[6].trim().equals("PROCESO")) {
                                guardaLinea = "PROCESO";
                                break;
                            }
                            if (!celdas[7].trim().equals("FECHA_CAPTURA")) {
                                guardaLinea = "FECHA_CAPTURA";
                                break;
                            }
                            if (!celdas[8].trim().equals("FECHA_APLICACION")) {
                                guardaLinea = "FECHA_APLICACION";
                                break;
                            }
                            if (!celdas[9].trim().equals("APLICACION_CONTABLE")) {
                                guardaLinea = "APLICACION_CONTABLE";
                                break;
                            }
                            if (!celdas[10].trim().equals("USUARIO_CAPTURA")) {
                                guardaLinea = "USUARIO_CAPTURA";
                                break;
                            }
                            if (!celdas[11].trim().equals("APROB_REVISOR")) {
                                guardaLinea = "APROB_REVISOR";
                                break;
                            }
                            if (!celdas[12].trim().equals("USUARIO_REVISOR")) {
                                guardaLinea = "USUARIO_REVISOR";
                                break;
                            }
                            if (!celdas[13].trim().equals("FECHA_REVISION")) {
                                guardaLinea = "FECHA_REVISION";
                                break;
                            }
                            if (!celdas[14].trim().equals("APROB_AUTORIZADOR")) {
                                guardaLinea = "APROB_AUTORIZADOR";
                                break;
                            }
                            if (!celdas[15].trim().equals("USUARIO_AUTORIZADOR")) {
                                guardaLinea = "USUARIO_AUTORIZADOR";
                                break;
                            }
                            if (!celdas[16].trim().equals("FECHA_AUTORIZACION")) {
                                guardaLinea = "FECHA_AUTORIZACION";
                                break;
                            }
                            if (!celdas[17].trim().equals("NUMERO_AMF")) {
                                guardaLinea = "NUMERO_AMF";
                                break;
                            }
                            if (!celdas[18].trim().equals("LEYENDA")) {
                                guardaLinea = "LEYENDA";
                                break;
                            }
                            if (!celdas[19].trim().equals("DESC_LEYENDA")) {
                                guardaLinea = "DESC_LEYENDA";
                                break;
                            }
                            if (!celdas[20].trim().equals("REFERENCIA1")) {
                                guardaLinea = "REFERENCIA1";
                                break;
                            }
                            if (!celdas[21].trim().equals("REFERENCIA2")) {
                                guardaLinea = "REFERENCIA2";
                                break;
                            }
                            if (!celdas[22].trim().equals("FOLIO_AMF")) {
                                guardaLinea = "FOLIO_AMF";
                                break;
                            }
                            if (!celdas[23].trim().equals("NO_OF_AMF")) {
                                guardaLinea = "NO_OF_AMF";
                                break;
                            }
                            if (!celdas[24].trim().equals("FECHA_REFERENCIA1")) {
                                guardaLinea = "FECHA_REFERENCIA1";
                                break;
                            }
                            if (!celdas[25].trim().equals("DIVISA")) {
                                guardaLinea = "DIVISA";
                                break;
                            }
                            if (!celdas[26].trim().equals("TOTAL_DIVISA")) {
                                guardaLinea = "TOTAL_DIVISA";
                                break;
                            }
                            if (!celdas[27].trim().equals("TIPO_CAMBIO")) {
                                guardaLinea = "TIPO_CAMBIO";
                                break;
                            }
                            if (!celdas[28].trim().equals("TOTAL_MN")) {
                                guardaLinea = "TOTAL_MN";
                                break;
                            }
                            if (!celdas[29].trim().equals("CLAVE_BENEFICIARIO")) {
                                guardaLinea = "CLAVE_BENEFICIARIO";
                                break;
                            }
                            if (!celdas[30].trim().equals("BENEFICIARIO")) {
                                guardaLinea = "BENEFICIARIO";
                                break;
                            }
                            if (!celdas[31].trim().equals("CTA_BANCARIA")) {
                                guardaLinea = "CTA_BANCARIA";
                                break;
                            }
                            if (!celdas[32].trim().equals("FECHA_PROPUESTA")) {
                                guardaLinea = "FECHA_PROPUESTA";
                                break;
                            }
                            if (!celdas[33].trim().equals("MEDIO_PAGO")) {
                                guardaLinea = "MEDIO_PAGO";
                                break;
                            }
                            if (!celdas[34].trim().equals("FECHA_PAGO")) {
                                guardaLinea = "FECHA_PAGO";
                                break;
                            }
                            limpiaTabla = guardaSicop.limpiarTabla("CLC_SIAFF_ENC");
                            System.out.println(limpiaTabla);
                        } else {
                            String idRamoCrS = celdas[0].trim();
                            String idUnidadCrS = celdas[1].trim();
                            String tipoCLC = celdas[2].trim();
                            String folioCLC = celdas[3].trim();
                            String folioDependendia = celdas[4].trim();
                            String estatusCLC = celdas[5].trim();
                            String proceso = celdas[6].trim();
                            String fechaCaptura = celdas[7].trim();
                            String fechaAplicacion = celdas[8].trim();
                            String aplicacionContable = celdas[9].trim();
                            String usuarioCaptura = celdas[10].trim();
                            String aprobRevisor = celdas[11].trim();
                            String usuarioRevisor = celdas[12].trim();
                            String fechaRevision = celdas[13].trim();
                            String aprobAutorizador = celdas[14].trim();
                            String usuarioAutorizador = celdas[15].trim();
                            String fechaAutorizacion = celdas[16].trim();
                            String numeroAMF = celdas[17].trim();
                            String leyenda = celdas[18].trim();
                            String descLeyenda = celdas[19].trim();
                            String referencia1 = celdas[20].trim();
                            String referencia2 = celdas[21].trim();
                            String folioAMF = celdas[22].trim();
                            String noOfAMF = celdas[23].trim();
                            String fechaReferencia1 = celdas[24].trim();
                            String divisa = celdas[25].trim();
                            String totalDivisa = celdas[26].trim();
                            String tipoCambio = celdas[27].trim();
                            String totalMN = celdas[28].trim();
                            String claveBeneficiario = celdas[29].trim();
                            String beneficiario = celdas[30].trim();
                            String ctaBancaria = celdas[31].trim();
                            String fechaPropuesta = celdas[32].trim();
                            String medioPago = celdas[33].trim();
                            String fechaPago = "";
                            if (celdas.length >= 35) {
                                fechaPago = celdas[34];
                            }
                            String cuantos = String.valueOf(commitCien);
                            guardaLinea = guardaSicop.insertaLineaSiaff(idRamoCrS, idUnidadCrS, tipoCLC, folioCLC, folioDependendia, estatusCLC, proceso, fechaCaptura, fechaAplicacion, aplicacionContable, usuarioCaptura, aprobRevisor, usuarioRevisor, fechaRevision, aprobAutorizador, usuarioAutorizador, fechaAutorizacion, numeroAMF, leyenda, descLeyenda, referencia1, referencia2, folioAMF, noOfAMF, fechaReferencia1, divisa, totalDivisa, tipoCambio, totalMN, claveBeneficiario, beneficiario, ctaBancaria, fechaPropuesta, medioPago, fechaPago, cuantos);
                            if (guardaLinea.equals("no_guardado")) {
                                break;
                            }
                            System.out.println("guardaLineaS:" + guardaLinea);
                        }
                    } else {
                        //Archivo SICOP
                        if (primerLinea == 1) {
                            if (!celdas[0].trim().equals("HOJA_VISOR")) {
                                guardaLinea = "HOJA_VISOR";
                                break;
                            }
                            if (!celdas[1].trim().equals("ID_RAMO_CR")) {
                                guardaLinea = "ID_RAMO_CR";
                                break;
                            }
                            if (!celdas[2].trim().equals("ID_UNIDAD_CR")) {
                                guardaLinea = "ID_UNIDAD_CR";
                                break;
                            }
                            if (!celdas[3].trim().equals("FOLIO_CLC_45")) {
                                guardaLinea = "FOLIO_CLC_45";
                                break;
                            }
                            if (!celdas[4].trim().equals("FECHA_EXP")) {
                                guardaLinea = "FECHA_EXP";
                                break;
                            }
                            if (!celdas[5].trim().equals("FECHA_APL")) {
                                guardaLinea = "FECHA_APL";
                                break;
                            }
                            if (!celdas[6].trim().equals("TTRANS_34")) {
                                guardaLinea = "TTRANS_34";
                                break;
                            }
                            if (!celdas[7].trim().equals("TSOL_35")) {
                                guardaLinea = "TSOL_35";
                                break;
                            }
                            if (!celdas[8].trim().equals("TPPTO_36")) {
                                guardaLinea = "TPPTO_36";
                                break;
                            }
                            if (!celdas[9].trim().equals("TMON_37")) {
                                guardaLinea = "TMON_37";
                                break;
                            }
                            if (!celdas[10].trim().equals("TCAM_38")) {
                                guardaLinea = "TCAM_38";
                                break;
                            }
                            if (!celdas[11].trim().equals("VOLANTE_39")) {
                                guardaLinea = "VOLANTE_39";
                                break;
                            }
                            if (!celdas[12].trim().equals("FECHA_OFICIO_42")) {
                                guardaLinea = "FECHA_OFICIO_42";
                                break;
                            }
                            if (!celdas[13].trim().equals("NCLC_43")) {
                                guardaLinea = "NCLC_43";
                                break;
                            }
                            if (!celdas[14].trim().equals("CPAG_44")) {
                                guardaLinea = "CPAG_44";
                                break;
                            }
                            if (!celdas[15].trim().equals("ESTIMACION_46")) {
                                guardaLinea = "ESTIMACION_46";
                                break;
                            }
                            if (!celdas[16].trim().equals("NCTR_47")) {
                                guardaLinea = "NCTR_47";
                                break;
                            }
                            if (!celdas[17].trim().equals("SOLP_48")) {
                                guardaLinea = "SOLP_48";
                                break;
                            }
                            if (!celdas[18].trim().equals("TIPO_MOVTO_51")) {
                                guardaLinea = "TIPO_MOVTO_51";
                                break;
                            }
                            if (!celdas[19].trim().equals("NO_POLIZA_56")) {
                                guardaLinea = "NO_POLIZA_56";
                                break;
                            }
                            if (!celdas[20].trim().equals("NO_POLIZA_CANCELA_57")) {
                                guardaLinea = "NO_POLIZA_CANCELA_57";
                                break;
                            }
                            if (!celdas[21].trim().equals("TIPO_POLIZA_58")) {
                                guardaLinea = "TIPO_POLIZA_58";
                                break;
                            }
                            if (!celdas[22].trim().equals("FECHA_INI_59")) {
                                guardaLinea = "FECHA_INI_59";
                                break;
                            }
                            if (!celdas[23].trim().equals("FECHA_FIN_60")) {
                                guardaLinea = "FECHA_FIN_60";
                                break;
                            }
                            if (!celdas[24].trim().equals("TIPO_CLC_67")) {
                                guardaLinea = "TIPO_CLC_67";
                                break;
                            }
                            if (!celdas[25].trim().equals("CVE_LEYENDA_68")) {
                                guardaLinea = "CVE_LEYENDA_68";
                                break;
                            }
                            if (!celdas[26].trim().equals("BENE_69")) {
                                guardaLinea = "BENE_69";
                                break;
                            }
                            if (!celdas[27].trim().equals("CTAB_70")) {
                                guardaLinea = "CTAB_70";
                                break;
                            }
                            if (!celdas[28].trim().equals("RFC_6")) {
                                guardaLinea = "RFC_6";
                                break;
                            }
                            if (!celdas[29].trim().equals("APEPAT_APEMAT_NOMBRE")) {
                                guardaLinea = "APEPAT_APEMAT_NOMBRE";
                                break;
                            }
                            if (!celdas[30].trim().equals("REPLACE")) {
                                guardaLinea = "REPLACE";
                                break;
                            }
                            if (!celdas[31].trim().equals("TDOC_71")) {
                                guardaLinea = "TDOC_71";
                                break;
                            }
                            if (!celdas[32].trim().equals("DESCRIPCION_1000_92")) {
                                guardaLinea = "DESCRIPCION_1000_92";
                                break;
                            }
                            if (!celdas[33].trim().equals("NEGOCIABLE_97")) {
                                guardaLinea = "NEGOCIABLE_97";
                                break;
                            }
                            if (!celdas[34].trim().equals("CIFI_98")) {
                                guardaLinea = "CIFI_98";
                                break;
                            }
                            if (!celdas[35].trim().equals("CTIF_99")) {
                                guardaLinea = "CTIF_99";
                                break;
                            }
                            if (!celdas[36].trim().equals("NACU_100")) {
                                guardaLinea = "NACU_100";
                                break;
                            }
                            if (!celdas[37].trim().equals("FECHA_NEGO_101")) {
                                guardaLinea = "FECHA_NEGO_101";
                                break;
                            }
                            if (!celdas[38].trim().equals("NACU_CP_102")) {
                                guardaLinea = "NACU_CP_102";
                                break;
                            }
                            if (!celdas[39].trim().equals("TOTAL_103")) {
                                guardaLinea = "TOTAL_103";
                                break;
                            }
                            if (!celdas[40].trim().equals("TOTAL_DIVISA_104")) {
                                guardaLinea = "TOTAL_DIVISA_104";
                                break;
                            }
                            if (!celdas[41].trim().equals("FECHA_REF_105")) {
                                guardaLinea = "FECHA_REF_105";
                                break;
                            }
                            if (!celdas[42].trim().equals("FECHA_PAGO_106")) {
                                guardaLinea = "FECHA_PAGO_106";
                                break;
                            }
                            if (!celdas[43].trim().equals("REFERENCIA1_108")) {
                                guardaLinea = "REFERENCIA1_108";
                                break;
                            }
                            if (!celdas[44].trim().equals("REFERENCIA2_109")) {
                                guardaLinea = "REFERENCIA2_109";
                                break;
                            }
                            if (!celdas[45].trim().equals("FOLIO_SIAFF_112")) {
                                guardaLinea = "FOLIO_SIAFF_112";
                                break;
                            }
                            if (!celdas[46].trim().equals("FECHA_SIAFF_113")) {
                                guardaLinea = "FECHA_SIAFF_113";
                                break;
                            }
                            if (!celdas[47].trim().equals("CC_CTRERROR_116")) {
                                guardaLinea = "CC_CTRERROR_116";
                                break;
                            }
                            if (!celdas[48].trim().equals("CENTRO_CONTABLE")) {
                                guardaLinea = "CENTRO_CONTABLE";
                                break;
                            }
                            if (!celdas[49].trim().equals("DSUFPRE_172")) {
                                guardaLinea = "DSUFPRE_172";
                                break;
                            }
                            if (!celdas[50].trim().equals("AOT_173")) {
                                guardaLinea = "AOT_173";
                                break;
                            }
                            if (!celdas[51].trim().equals("PAGADO_175")) {
                                guardaLinea = "PAGADO_175";
                                break;
                            }
                            if (!celdas[52].trim().equals("RFC_184")) {
                                guardaLinea = "RFC_184";
                                break;
                            }
                            if (!celdas[53].trim().equals("ESTATUS")) {
                                guardaLinea = "ESTATUS";
                                break;
                            }
                            if (!celdas[54].trim().equals("PRCS_CLAVE")) {
                                guardaLinea = "PRCS_CLAVE";
                                break;
                            }
                            if (!celdas[55].trim().equals("PROC_CLAVE")) {
                                guardaLinea = "PROC_CLAVE";
                                break;
                            }
                            if (!celdas[56].trim().equals("ID_USUARIO_CANCELA")) {
                                guardaLinea = "ID_USUARIO_CANCELA";
                                break;
                            }
                            if (!celdas[57].trim().equals("FECHA_CANCELA")) {
                                guardaLinea = "FECHA_CANCELA";
                                break;
                            }
                            if (!celdas[58].trim().equals("ID_USUARIO_APLICA")) {
                                guardaLinea = "ID_USUARIO_APLICA";
                                break;
                            }
                            if (!celdas[59].trim().equals("ID_USUARIO_CR")) {
                                guardaLinea = "ID_USUARIO_CR";
                                break;
                            }
                            if (!celdas[60].trim().equals("ID_ROL_CR")) {
                                guardaLinea = "ID_ROL_CR";
                                break;
                            }
                            if (!celdas[61].trim().equals("ID_RENGLON")) {
                                guardaLinea = "ID_RENGLON";
                                break;
                            }
                            if (!celdas[62].trim().equals("ID_EVENTO")) {
                                guardaLinea = "ID_EVENTO";
                                break;
                            }
                            if (!celdas[63].trim().equals("EVENTO")) {
                                guardaLinea = "EVENTO";
                                break;
                            }
                            if (!celdas[64].trim().equals("ID_RAMO_ML")) {
                                guardaLinea = "ID_RAMO_ML";
                                break;
                            }
                            if (!celdas[65].trim().equals("ID_UNIDAD_ML")) {
                                guardaLinea = "ID_UNIDAD_ML";
                                break;
                            }
                            if (!celdas[66].trim().equals("NCOM_15")) {
                                guardaLinea = "NCOM_15";
                                break;
                            }
                            if (!celdas[67].trim().equals("CBEN_16")) {
                                guardaLinea = "CBEN_16";
                                break;
                            }
                            if (!celdas[68].trim().equals("NRES_17")) {
                                guardaLinea = "NRES_17";
                                break;
                            }
                            if (!celdas[69].trim().equals("NOIF_18")) {
                                guardaLinea = "NOIF_18";
                                break;
                            }
                            if (!celdas[70].trim().equals("ISR_20")) {
                                guardaLinea = "ISR_20";
                                break;
                            }
                            if (!celdas[71].trim().equals("IVA_21")) {
                                guardaLinea = "IVA_21";
                                break;
                            }
                            if (!celdas[72].trim().equals("MIL5_22")) {
                                guardaLinea = "MIL5_22";
                                break;
                            }
                            if (!celdas[73].trim().equals("IVADES_23")) {
                                guardaLinea = "IVADES_23";
                                break;
                            }
                            if (!celdas[74].trim().equals("ANTICIPO_24")) {
                                guardaLinea = "ANTICIPO_24";
                                break;
                            }
                            if (!celdas[75].trim().equals("IVAANT_25")) {
                                guardaLinea = "IVAANT_25";
                                break;
                            }
                            if (!celdas[76].trim().equals("IMP_RETE_26")) {
                                guardaLinea = "IMP_RETE_26";
                                break;
                            }
                            if (!celdas[77].trim().equals("IMP_DIVISA_27")) {
                                guardaLinea = "IMP_DIVISA_27";
                                break;
                            }
                            if (!celdas[78].trim().equals("IMP_ESTIM_28")) {
                                guardaLinea = "IMP_ESTIM_28";
                                break;
                            }
                            if (!celdas[79].trim().equals("IMP_5000_32")) {
                                guardaLinea = "IMP_5000_32";
                                break;
                            }
                            if (!celdas[80].trim().equals("TCONC_49")) {
                                guardaLinea = "TCONC_49";
                                break;
                            }
                            if (!celdas[81].trim().equals("CONC_MOV_50")) {
                                guardaLinea = "CONC_MOV_50";
                                break;
                            }
                            if (!celdas[82].trim().equals("FECHA_DOCTO_75")) {
                                guardaLinea = "FECHA_DOCTO_75";
                                break;
                            }
                            if (!celdas[83].trim().equals("IMP_NETO_107")) {
                                guardaLinea = "IMP_NETO_107";
                                break;
                            }
                            if (!celdas[84].trim().equals("CC_MSGERROR_115")) {
                                guardaLinea = "CC_MSGERROR_115";
                                break;
                            }
                            if (!celdas[85].trim().equals("TPAG_117")) {
                                guardaLinea = "TPAG_117";
                                break;
                            }
                            if (!celdas[86].trim().equals("IMP_REMA_133")) {
                                guardaLinea = "IMP_REMA_133";
                                break;
                            }
                            if (!celdas[87].trim().equals("IMPORTE_148")) {
                                guardaLinea = "IMPORTE_148";
                                break;
                            }
                            if (!celdas[88].trim().equals("MES_149")) {
                                guardaLinea = "MES_149";
                                break;
                            }
                            if (!celdas[89].trim().equals("CANI_150")) {
                                guardaLinea = "CANI_150";
                                break;
                            }
                            if (!celdas[90].trim().equals("CGFU_151")) {
                                guardaLinea = "CGFU_151";
                                break;
                            }
                            if (!celdas[91].trim().equals("CFUN_152")) {
                                guardaLinea = "CFUN_152";
                                break;
                            }
                            if (!celdas[92].trim().equals("CSFU_153")) {
                                guardaLinea = "CSFU_153";
                                break;
                            }
                            if (!celdas[93].trim().equals("CPRG_154")) {
                                guardaLinea = "CPRG_154";
                                break;
                            }
                            if (!celdas[94].trim().equals("CAIN_155")) {
                                guardaLinea = "CAIN_155";
                                break;
                            }
                            if (!celdas[95].trim().equals("CPPT_156")) {
                                guardaLinea = "CPPT_156";
                                break;
                            }
                            if (!celdas[96].trim().equals("CCAP_157")) {
                                guardaLinea = "CCAP_157";
                                break;
                            }
                            if (!celdas[97].trim().equals("CCON_158")) {
                                guardaLinea = "CCON_158";
                                break;
                            }
                            if (!celdas[98].trim().equals("CPARG_300")) {
                                guardaLinea = "CPARG_300";
                                break;
                            }
                            if (!celdas[99].trim().equals("CPAR_159")) {
                                guardaLinea = "CPAR_159";
                                break;
                            }
                            if (!celdas[100].trim().equals("CTGA_160")) {
                                guardaLinea = "CTGA_160";
                                break;
                            }
                            if (!celdas[101].trim().equals("CFIN_161")) {
                                guardaLinea = "CFIN_161";
                                break;
                            }
                            if (!celdas[102].trim().equals("CCAU_162")) {
                                guardaLinea = "CCAU_162";
                                break;
                            }
                            if (!celdas[103].trim().equals("CCOP_163")) {
                                guardaLinea = "CCOP_163";
                                break;
                            }
                            if (!celdas[104].trim().equals("CGEO_164")) {
                                guardaLinea = "CGEO_164";
                                break;
                            }
                            if (!celdas[105].trim().equals("CPLA_165")) {
                                guardaLinea = "CPLA_165";
                                break;
                            }
                            if (!celdas[106].trim().equals("CPPI_166")) {
                                guardaLinea = "CPPI_166";
                                break;
                            }
                            if (!celdas[107].trim().equals("OFIN_167")) {
                                guardaLinea = "OFIN_167";
                                break;
                            }
                            if (!celdas[108].trim().equals("AUX1_168")) {
                                guardaLinea = "AUX1_168";
                                break;
                            }
                            if (!celdas[109].trim().equals("AUX2_169")) {
                                guardaLinea = "AUX2_169";
                                break;
                            }
                            if (!celdas[110].trim().equals("AUX3_170")) {
                                guardaLinea = "AUX3_170";
                                break;
                            }
                            /*
							 * if(!celdas[100].trim().equals("CTGA_160")
							 * ){guardaLinea = "CTGA_160"; break; }
							 * if(!celdas[101].trim().equals("CFIN_161")
							 * ){guardaLinea = "CFIN_161"; break; }
							 * if(!celdas[102].trim().equals("CGEO_164")
							 * ){guardaLinea = "CGEO_164"; break; }
							 * if(!celdas[103].trim().equals("CPPI_166")
							 * ){guardaLinea = "CPPI_166"; break; }
							 * if(!celdas[104].trim().equals("CCAU_162")
							 * ){guardaLinea = "CCAU_162"; break; }
							 * if(!celdas[105].trim().equals("CCOP_163")
							 * ){guardaLinea = "CCOP_163"; break; }
							 * if(!celdas[106].trim().equals("CPLA_165")
							 * ){guardaLinea = "CPLA_165"; break; }
							 * if(!celdas[107].trim().equals("OFIN_167")
							 * ){guardaLinea = "OFIN_167"; break; }
							 * if(!celdas[108].trim().equals("AUX1_168")
							 * ){guardaLinea = "AUX1_168"; break; }
							 * if(!celdas[109].trim().equals("AUX2_169")
							 * ){guardaLinea = "AUX2_169"; break; }
							 * if(!celdas[110].trim().equals("AUX3_170")
							 * ){guardaLinea = "AUX3_170"; break; }
							 */
                            if (!celdas[111].trim().equals("SPAG_176")) {
                                guardaLinea = "SPAG_176";
                                break;
                            }
                            if (!celdas[112].trim().equals("PPAG_177")) {
                                guardaLinea = "PPAG_177";
                                break;
                            }
                            if (!celdas[113].trim().equals("TNOM_178")) {
                                guardaLinea = "TNOM_178";
                                break;
                            }
                            if (!celdas[114].trim().equals("COBG_183")) {
                                guardaLinea = "COBG_183";
                                break;
                            }
                            if (!celdas[115].trim().equals("TUNR_80")) {
                                guardaLinea = "TUNR_80";
                                break;
                            }
                            limpiaTabla = guardaSicop.limpiarTabla(" CLC_SICOP ");
                            //System.out.println(limpiaTabla);
                            //primerLinea = primerLinea + 1;
                        } else {
                            String hojaVisor = celdas[0].trim();
                            String idRamoCr = celdas[1].trim();
                            String idUnidadCr = celdas[2].trim();
                            String folioClc45 = celdas[3].trim();
                            String fechaExp = celdas[4].trim();
                            String FechaApl = celdas[5].trim();
                            String TTRANS34 = celdas[6].trim();
                            String TSOL35 = celdas[7].trim();
                            String TPPTO36 = celdas[8].trim();
                            String TMON37 = celdas[9].trim();
                            String TCAM38 = celdas[10].trim();
                            String Volante39 = celdas[11].trim();
                            String FechaOficio42 = celdas[12].trim();
                            String NCLC43 = celdas[13].trim();
                            String CPAG44 = celdas[14].trim();
                            String Estimacion46 = celdas[15].trim();
                            String NCTR47 = celdas[16].trim();
                            String SOLP48 = celdas[17].trim();
                            String TIPOMOVTO_51 = celdas[18].trim();
                            String NoPoliza56 = celdas[19].trim();
                            String NoPolizaCancela57 = celdas[20].trim();
                            String TipoPoliza58 = celdas[21].trim();
                            String FechaIni59 = celdas[22].trim();
                            String FechaFin60 = celdas[23].trim();
                            String TipoCLC67 = celdas[24].trim();
                            String CVELeyenda68 = celdas[25].trim();
                            String Bene69 = celdas[26].trim();
                            String CTAB70 = celdas[27].trim();
                            String RFC6 = celdas[28].trim();
                            String ApepatApematNombre = celdas[29].trim();
                            String Replace = celdas[30].trim();
                            String TDOC71 = celdas[31].trim();
                            String Descripcion100092 = celdas[32].trim();
                            String Negociable97 = celdas[33].trim();
                            String CIFI98 = celdas[34].trim();
                            String CTIF99 = celdas[35].trim();
                            String NACU100 = celdas[36].trim();
                            String FechaNego101 = celdas[37].trim();
                            String NacuCP102 = celdas[38].trim();
                            String Total103 = celdas[39].trim();
                            String TotalDivisa104 = celdas[40].trim();
                            String FechaRef105 = celdas[41].trim();
                            String FechaPago106 = celdas[42].trim();
                            String Referencia1108 = celdas[43].trim();
                            String Referencia2109 = celdas[44].trim();
                            String FolioSiaff112 = celdas[45].trim();
                            String FechaSiaff113 = celdas[46].trim();
                            String CCCTRERROR116 = celdas[47].trim();
                            String CentroContable = celdas[48].trim();
                            String DSUFPRE172 = celdas[49].trim();
                            String AOT173 = celdas[50].trim();
                            String Pagado175 = celdas[51].trim();
                            String RFC184 = celdas[52].trim();
                            String Estatus = celdas[53].trim();
                            String PRCSClave = celdas[54].trim();
                            String ProcClave = celdas[55].trim();
                            String IdUsuarioCancela = celdas[56].trim();
                            String FechaCancela = celdas[57].trim();
                            String IdUsuarioAplica = celdas[58].trim();
                            String IdUsuarioCR = celdas[59].trim();
                            String IdRolCR = celdas[60].trim();
                            String IdRenglon = celdas[61].trim();
                            String IdEvento = celdas[62].trim();
                            String Evento = celdas[63].trim();
                            String IdRamoML = celdas[64].trim();
                            String IdUnidadML = celdas[65].trim();
                            String NCOM15 = celdas[66].trim();
                            String CBEN16 = celdas[67].trim();
                            String NRES17 = celdas[68].trim();
                            String NOIF18 = celdas[69].trim();
                            String ISR20 = celdas[70].trim();
                            String Iva21 = celdas[71].trim();
                            String MIL522 = celdas[72].trim();
                            String IvaDes23 = celdas[73].trim();
                            String Anticipo24 = celdas[74].trim();
                            String IVAANT25 = celdas[75].trim();
                            String ImpRete26 = celdas[76].trim();
                            String ImpDivisa27 = celdas[77].trim();
                            String ImpEstim28 = celdas[78].trim();
                            String Imp500032 = celdas[79].trim();
                            String TCONC49 = celdas[80].trim();
                            String ConcMov50 = celdas[81].trim();
                            String FechaDocto75 = celdas[82].trim();
                            String ImpNeto107 = celdas[83].trim();
                            String CCMSGError115 = celdas[84].trim();
                            String TPAG117 = celdas[85].trim();
                            String ImpRema133 = celdas[86].trim();
                            String Importe148 = celdas[87].trim();
                            String Mes149 = celdas[88].trim();
                            String Cani150 = celdas[89].trim();
                            String CGFU151 = celdas[90].trim();
                            String CFun152 = celdas[91].trim();
                            String CSFU153 = celdas[92].trim();
                            String CPRG154 = celdas[93].trim();
                            String Cain155 = celdas[94].trim();
                            String CPPT156 = celdas[95].trim();
                            String Ccap157 = celdas[96].trim();
                            String Ccon158 = celdas[97].trim();
                            String CParg300 = celdas[98].trim();
                            String CPar159 = celdas[99].trim();
                            String CTGA160 = celdas[100].trim();
                            String CFin161 = celdas[101].trim();
                            String CCau162 = celdas[102].trim();
                            String CCop163 = celdas[103].trim();
                            String CGeo164 = celdas[104].trim();
                            String CPla165 = celdas[105].trim();
                            String CPpi166 = celdas[106].trim();
                            String OFin167 = celdas[107].trim();
                            String AUX1168 = celdas[108].trim();
                            String AUX2169 = celdas[109].trim();
                            String AUX3170 = celdas[110].trim();
                            /*
							 * String CTGA160= celdas[100].trim(); String
							 * CFin161= celdas[101].trim(); String CGeo164=
							 * celdas[102].trim(); String CPpi166=
							 * celdas[103].trim(); String CCau162=
							 * celdas[104].trim(); String CCop163=
							 * celdas[105].trim(); String CPla165=
							 * celdas[106].trim(); String OFin167=
							 * celdas[107].trim(); String AUX1168=
							 * celdas[108].trim(); String AUX2169=
							 * celdas[109].trim(); String AUX3170=
							 * celdas[110].trim();
							 */
                            String SPag176 = celdas[111].trim();
                            String PPag177 = celdas[112].trim();
                            String TNom178 = celdas[113].trim();
                            String COBG183 = celdas[114].trim();
                            String Tunr80 = celdas[115].trim();
                            guardaLinea = guardaSicop.insertaLineaSicop(hojaVisor, idRamoCr, idUnidadCr, folioClc45, fechaExp, FechaApl, TTRANS34, TSOL35, TPPTO36, TMON37, TCAM38, Volante39, FechaOficio42, NCLC43, CPAG44, Estimacion46, NCTR47, SOLP48, TIPOMOVTO_51, NoPoliza56, NoPolizaCancela57, TipoPoliza58, FechaIni59, FechaFin60, TipoCLC67, CVELeyenda68, Bene69, CTAB70, RFC6, ApepatApematNombre, Replace, TDOC71, Descripcion100092, Negociable97, CIFI98, CTIF99, NACU100, FechaNego101, NacuCP102, Total103, TotalDivisa104, FechaRef105, FechaPago106, Referencia1108, Referencia2109, FolioSiaff112, FechaSiaff113, CCCTRERROR116, CentroContable, DSUFPRE172, AOT173, Pagado175, RFC184, Estatus, PRCSClave, ProcClave, IdUsuarioCancela, FechaCancela, IdUsuarioAplica, IdUsuarioCR, IdRolCR, IdRenglon, IdEvento, Evento, IdRamoML, IdUnidadML, NCOM15, CBEN16, NRES17, NOIF18, ISR20, Iva21, MIL522, IvaDes23, Anticipo24, IVAANT25, ImpRete26, ImpDivisa27, ImpEstim28, Imp500032, TCONC49, ConcMov50, FechaDocto75, ImpNeto107, CCMSGError115, TPAG117, ImpRema133, Importe148, Mes149, Cani150, CGFU151, CFun152, CSFU153, CPRG154, Cain155, CPPT156, Ccap157, Ccon158, CParg300, CPar159, CTGA160, CFin161, CGeo164, CPpi166, CCau162, CCop163, CPla165, OFin167, AUX1168, AUX2169, AUX3170, SPag176, PPag177, TNom178, COBG183, Tunr80);
                            if (guardaLinea.equals("no_guardado")) {
                                break;
                            }
                            //System.out.println("guardaLinea:" + guardaLinea);
                        }
                    }
                }
                primerLinea = primerLinea + 1;
                commitCien = (commitCien >= 100) ? commitCien = 0 : commitCien + 1;
                //System.out.println("linea:" + primerLinea);
            }
            valorReturn = guardaLinea;
        } catch (Exception se) {
            log.error("Error: " + se);
            se.printStackTrace();
            valorReturn = "Error";
        } finally {
            try {
                if (br != null)
                    br.close();
            } catch (Exception exc) {
                Log.warn("Cerrando BufferedReader", exc);
            }
            br = null;
        }
        //System.out.println("valorReturn:" + valorReturn);
        return valorReturn;
    }

    // se lee el archivo ingresado más las condiciones (campo accion y
    @SuppressWarnings("unchecked")
    public List<FileItem> procesaArchivos(HttpServletRequest request) {
        String szPath;
        List<FileItem> fileItems = new ArrayList<FileItem>();
        try {
            // Se construye un objeto para que parsee la petición
            DiskFileUpload fu = new DiskFileUpload();
            // Tamaño máximo que aceptará el archivo
            // El tamaño no importa
            fu.setSizeMax(-1);
            // Si excede el 1 Gb en memoria lo escribe a disco
            fu.setSizeThreshold(1048576);
            szPath = getServletContext().getRealPath("/upload/ejercidoPagado");
            File file = new File(szPath);
            if (!file.exists()) {
                file.mkdirs();
            }
            fu.setRepositoryPath(szPath);
            fileItems = fu.parseRequest(request);
        } catch (Exception e) {
            log.error("Error: " + e);
            System.out.println("Error de Aplicación " + e.getMessage());
        }
        return fileItems;
    }

    /**
     * Initialization of the servlet. <br>
     *
     * @throws ServletException
     *             if an error occurs
     */
    public void init() throws ServletException {
        // Put your code here
    }
}
