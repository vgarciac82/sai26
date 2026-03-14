package com.syc.altaproveedor.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.axtel.proveedores.exception.ProveedorException;
import com.syc.adquisiciones.core.Respuesta;
import com.syc.altaproveedor.AltaProveedorBusinessLogic;
import com.syc.altaproveedor.DatosCtaBancaria;
import com.syc.altaproveedor.DatosProveedor;
import com.syc.altaproveedor.DatosProveedorIncumplido;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "AltaProveedoresServlet", urlPatterns = { "/servlet/AltaProveedoresServlet" })
public class AltaProveedoresServlet extends HttpServlet implements GestionInterface {

    private static final long serialVersionUID = 6559267648468839026L;

    private static Logger log = LoggerFactory.getLogger(AltaProveedoresServlet.class);

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

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        int tipoOperacion = 0;
        JSONObject jsonObj = null;
        JSONArray arrayObj = null;
        PrintWriter out = null;
        String mensaje = "";
        boolean respuesta = false;
        Respuesta resp;
        Usuario u = null;
        DatosProveedor datProveedor = null;
        DatosCtaBancaria datCtaBancaria = null;
        List<DatosCtaBancaria> listaCtas = null;
        DatosProveedorIncumplido provIncumplido = null;
        try {
            if (session == null) {
                log.warn("No hay sesion");
                response.sendRedirect("../index.jsp");
                return;
            }
            u = (Usuario) session.getAttribute(ATT_USER);
            arrayObj = new JSONArray();
            out = response.getWriter();
            tipoOperacion = (null == request.getParameter("operacion") || "".equals(request.getParameter("operacion"))) ? 0 : Integer.parseInt(request.getParameter("operacion"));
            AltaProveedorBusinessLogic altaProveedor = new AltaProveedorBusinessLogic();
            switch(tipoOperacion) {
                case 0:
                    log.warn("No se recibió el tipo de operación");
                    mensaje = "No se recibió el tipo de operación";
                    respuesta = false;
                    break;
                case // consulta
                1:
                    datProveedor = fillDatProvedor(request);
                    jsonObj = altaProveedor.consultaInfoProveedor(datProveedor, u);
                    if (datProveedor.getnIdOper() == 2) {
                        jsonObj.put("HAYCUENTAS", altaProveedor.thereIsNewBanckAcount(datProveedor));
                    }
                    respuesta = true;
                    break;
                case // valida si ya se encuentra registrado el RFC
                2:
                    datProveedor = fillDatProvedor(request);
                    jsonObj = altaProveedor.existeRFCDadoDeAlta(datProveedor);
                    respuesta = true;
                    break;
                case // Guarda datos
                3:
                    datProveedor = fillDatProvedor(request);
                    resp = altaProveedor.guardaInfoProveedor(datProveedor, u);
                    respuesta = resp.isResp();
                    mensaje = resp.getMsg();
                    jsonObj = new JSONObject();
                    if (datProveedor.getnIdOper() == 1) {
                        jsonObj.put("msgEFOS", resp.getMsg2());
                    }
                    if (datProveedor.getnIdOper() == 2) {
                        jsonObj.put("HAYCUENTAS", resp.isNewCtas());
                    }
                    break;
                case // consulta documentos
                4:
                    datProveedor = fillDatProvedor(request);
                    resp = altaProveedor.consultaDocumentacion(datProveedor);
                    respuesta = resp.isResp();
                    mensaje = resp.getMsg();
                    jsonObj = new JSONObject();
                    break;
                case // valida datos de cuentas bancarias
                5:
                    datProveedor = fillDatProvedor(request);
                    resp = altaProveedor.validaCuentasBancarias(datProveedor, u);
                    respuesta = resp.isResp();
                    mensaje = resp.getMsg();
                    jsonObj = new JSONObject();
                    break;
                case // consulta documentos cta bancaria
                6:
                    datProveedor = fillDatProvedor(request);
                    resp = altaProveedor.consultaDocumentacionCtaBancaria(datProveedor);
                    respuesta = resp.isResp();
                    mensaje = resp.getMsg();
                    jsonObj = new JSONObject();
                    break;
                case // valida proveedor
                7:
                    datProveedor = fillDatProvedor(request);
                    resp = altaProveedor.validaAltaProveedor(datProveedor, u);
                    respuesta = resp.isResp();
                    mensaje = resp.getMsg();
                    jsonObj = new JSONObject();
                    break;
                case // Autoriza Proveedor
                8:
                    datProveedor = fillDatProvedor(request);
                    resp = altaProveedor.autorizaAltaProveedor(datProveedor, u);
                    respuesta = resp.isResp();
                    mensaje = resp.getMsg();
                    jsonObj = new JSONObject();
                    break;
                case // Modifica alta Proveedor
                9:
                    datProveedor = fillDatProvedor(request);
                    resp = altaProveedor.modificaAltaProv(datProveedor, u);
                    respuesta = resp.isResp();
                    mensaje = resp.getMsg();
                    jsonObj = new JSONObject();
                    break;
                case // Enviar (Captura, devolución de validación y
                10:
                    // modificación de proveedor)
                    datProveedor = fillDatProvedor(request);
                    resp = altaProveedor.enviar(datProveedor, u);
                    respuesta = resp.isResp();
                    mensaje = resp.getMsg();
                    jsonObj = new JSONObject();
                    break;
                case // regresa a consulta el folio
                11:
                    datProveedor = fillDatProvedor(request);
                    resp = altaProveedor.liberaCaso(datProveedor, u);
                    respuesta = resp.isResp();
                    mensaje = resp.getMsg();
                    jsonObj = new JSONObject();
                    break;
                case // Eminina ctas bancarias
                12:
                    datProveedor = fillDatProvedor(request);
                    datCtaBancaria = fillDatCtaProvedor(request, u);
                    listaCtas = new ArrayList<DatosCtaBancaria>();
                    listaCtas.add(datCtaBancaria);
                    datProveedor.setCuentasBancarias(listaCtas);
                    resp = altaProveedor.EliminaCtaBancaria(datProveedor, u);
                    respuesta = resp.isResp();
                    mensaje = resp.getMsg();
                    jsonObj = new JSONObject();
                    break;
                case //Captura proveedor incumplido
                13:
                    provIncumplido = fillDatProvedorIncumplido(request, u);
                    resp = altaProveedor.saveProveedorIncumplido(provIncumplido);
                    respuesta = resp.isResp();
                    mensaje = resp.getMsg();
                    jsonObj = new JSONObject();
                    break;
                default:
                    respuesta = false;
                    mensaje = "Operación desconocida";
                    log.warn("Operación desconocida");
                    break;
            }
        } catch (Exception e) {
            jsonObj = new JSONObject();
            arrayObj = new JSONArray();
            mensaje = "Error: \n" + e.getMessage().toString();
            respuesta = false;
            log.error("Object: {}", e.getMessage().toString());
            e.printStackTrace();
        } finally {
            if (listaCtas != null) {
                listaCtas.clear();
            }
            datCtaBancaria = null;
            listaCtas = null;
            datProveedor = null;
            provIncumplido = null;
            u = null;
            try {
                jsonObj.put("RESPUESTA", respuesta);
                jsonObj.put("MENSAJE", mensaje);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            String destino = new String(arrayObj.put(jsonObj).toString().getBytes("UTF-8"), "ISO-8859-1");
            out.println(destino);
            if (out != null) {
                out.flush();
                out.close();
            }
            out = null;
            jsonObj = null;
            arrayObj = null;
        }
    }

    private DatosProveedor fillDatProvedor(HttpServletRequest request) throws Exception {
        DatosProveedor datProveedor = new DatosProveedor();
        datProveedor.setcApellidoMat(null == (request.getParameter("cApellidoMat")) ? "" : request.getParameter("cApellidoMat"));
        datProveedor.setcApellidoPat(null == (request.getParameter("cApellidoPat")) ? "" : request.getParameter("cApellidoPat"));
        datProveedor.setcCalle(null == (request.getParameter("cCalle")) ? "" : (request.getParameter("cCalle")));
        datProveedor.setcCodigoPost(null == (request.getParameter("cCodigoPost")) ? "" : request.getParameter("cCodigoPost"));
        datProveedor.setcColonia(null == (request.getParameter("cColonia")) ? "" : (request.getParameter("cColonia")));
        datProveedor.setcCurp(null == (request.getParameter("cCurp")) ? "" : request.getParameter("cCurp"));
        datProveedor.setcEmail(null == (request.getParameter("cEmail")) ? "" : (request.getParameter("cEmail")));
        datProveedor.setcFolio(null == (request.getParameter("cFolio")) ? "" : request.getParameter("cFolio"));
        datProveedor.setcGiro(null == (request.getParameter("cGiro")) ? "" : (request.getParameter("cGiro")));
        datProveedor.setcNombre(null == (request.getParameter("cNombre")) ? "" : (request.getParameter("cNombre")));
        datProveedor.setcNumeroExt(null == (request.getParameter("cNumeroExt")) ? "" : (request.getParameter("cNumeroExt")));
        datProveedor.setcNumeroInt(null == (request.getParameter("cNumeroInt")) ? "" : (request.getParameter("cNumeroInt")));
        datProveedor.setcPaginaWeb(null == (request.getParameter("cPaginaWeb")) ? "" : (request.getParameter("cPaginaWeb")));
        datProveedor.setcPais(null == (request.getParameter("cPais")) ? "" : (request.getParameter("cPais")));
        datProveedor.setcRazonSocial(null == (request.getParameter("cRazonSocial")) ? "" : (request.getParameter("cRazonSocial")));
        datProveedor.setcTelefono(null == (request.getParameter("cTelefono")) ? "" : request.getParameter("cTelefono"));
        datProveedor.setcTipoPB(null == (request.getParameter("cTipoPB")) ? "" : request.getParameter("cTipoPB"));
        datProveedor.setnExtranjero((null == request.getParameter("isExtranjero") || "".equals(request.getParameter("isExtranjero"))) ? 0 : Integer.parseInt(request.getParameter("isExtranjero")));
        datProveedor.setnEntidadFederativa((null == request.getParameter("nEntidadFederativa") || "null".equals(request.getParameter("nEntidadFederativa")) || "".equals(request.getParameter("nEntidadFederativa"))) ? 0 : Integer.parseInt(request.getParameter("nEntidadFederativa")));
        datProveedor.setnIdCaso((null == request.getParameter("nIdCaso") || "".equals(request.getParameter("nIdCaso"))) ? 0 : Integer.parseInt(request.getParameter("nIdCaso")));
        datProveedor.setnIdOper((null == request.getParameter("nIdOper") || "".equals(request.getParameter("nIdOper"))) ? 0 : Integer.parseInt(request.getParameter("nIdOper")));
        datProveedor.setnMunicipio((null == request.getParameter("nMunicipio") || "".equals(request.getParameter("nMunicipio")) || "null".equals(request.getParameter("nMunicipio"))) ? 0 : Integer.parseInt(request.getParameter("nMunicipio")));
        datProveedor.setnNumEmpleado((null == request.getParameter("nNumEmpleado") || "".equals(request.getParameter("nNumEmpleado"))) ? 0 : Integer.parseInt(request.getParameter("nNumEmpleado")));
        datProveedor.setnPyme((null == request.getParameter("nPyme") || "null".equals(request.getParameter("nPyme")) || "".equals(request.getParameter("nPyme"))) ? 0 : Integer.parseInt(request.getParameter("nPyme")));
        datProveedor.setnTipoPersona((null == request.getParameter("nTipoPersona") || "null".equals(request.getParameter("nTipoPersona")) || "".equals(request.getParameter("nTipoPersona"))) ? 0 : Integer.parseInt(request.getParameter("nTipoPersona")));
        datProveedor.setnTipoTelefono((null == request.getParameter("nTipoTelefono") || "null".equals(request.getParameter("nTipoTelefono")) || "".equals(request.getParameter("nTipoTelefono"))) ? 0 : Integer.parseInt(request.getParameter("nTipoTelefono")));
        datProveedor.setRfc(null == (request.getParameter("cIdRFC")) ? "" : (request.getParameter("cIdRFC")));
        datProveedor.setRfc1(null == (request.getParameter("cIdRFC1")) ? "" : (request.getParameter("cIdRFC1")));
        datProveedor.setRfc2(null == (request.getParameter("cIdRFC2")) ? "" : (request.getParameter("cIdRFC2")));
        datProveedor.setRfc3(null == (request.getParameter("cIdRFC3")) ? "" : (request.getParameter("cIdRFC3")));
        datProveedor.setcObservaciones(null == request.getParameter("observaciones") ? "" : (request.getParameter("observaciones")));
        datProveedor.setcTituloAplicacion(null == (request.getParameter("cTituloAplicacion")) ? "" : request.getParameter("cTituloAplicacion"));
        datProveedor.setcDocumentoHaplicado(null == (request.getParameter("cDocumentoHaplicado")) ? "" : request.getParameter("cDocumentoHaplicado"));
        datProveedor.setAutoriza(null == (request.getParameter("cCodigoPost")) || "false".equalsIgnoreCase(request.getParameter("autoriza")) ? false : true);
        datProveedor.setcLocalidad((null == (request.getParameter("cLocalidad")) || "".equals(request.getParameter("nTipoPersona"))) ? "0001" : (request.getParameter("cLocalidad")));
        datProveedor.setNumRepse(StringUtils.trimToEmpty(request.getParameter("numRepse")));
        String idRegimen = StringUtils.trimToEmpty(request.getParameter("idRegimen"));
        datProveedor.setIdRegimenFiscal("".equals(idRegimen) ? Integer.parseInt("0") : Integer.parseInt(idRegimen));
        return datProveedor;
    }

    private DatosCtaBancaria fillDatCtaProvedor(HttpServletRequest request, Usuario u) throws Exception {
        DatosCtaBancaria datCtaBancaria = null;
        if (null != request.getParameter("nIdOper") && !"".equals(request.getParameter("nIdOper")) && "2".equals(request.getParameter("nIdOper"))) {
            datCtaBancaria = new DatosCtaBancaria();
            datCtaBancaria.setcBanco(null == (request.getParameter("cBanco")) ? "" : new String((request.getParameter("cBanco")).getBytes("ISO-8859-1"), "UTF-8"));
            datCtaBancaria.setcCuentaBancaria(null == (request.getParameter("cCuentaBancaria")) ? "" : new String((request.getParameter("cCuentaBancaria")).getBytes("ISO-8859-1"), "UTF-8"));
            datCtaBancaria.setcNameBanco(null == (request.getParameter("cNameBanco")) ? "" : new String((request.getParameter("cNameBanco")).getBytes("ISO-8859-1"), "UTF-8"));
            datCtaBancaria.setcPlaza(null == (request.getParameter("cPlaza")) ? "" : new String((request.getParameter("cPlaza")).getBytes("ISO-8859-1"), "UTF-8"));
            datCtaBancaria.setcEstatusCta(null == (request.getParameter("cEstatusCta")) ? "" : new String((request.getParameter("cEstatusCta")).getBytes("ISO-8859-1"), "UTF-8"));
            datCtaBancaria.setcUsuarioModifico(u.getLogin());
            datCtaBancaria.setnBCBEnviadoSICOP((null == request.getParameter("nBCBEnviadoSICOP") || "null".equals(request.getParameter("nBCBEnviadoSICOP"))) ? 0 : Integer.parseInt(request.getParameter("nBCBEnviadoSICOP")));
            datCtaBancaria.setnDigitoVerificador((null == request.getParameter("nDigitoVerificador") || "null".equals(request.getParameter("nDigitoVerificador"))) ? 0 : Integer.parseInt(request.getParameter("nDigitoVerificador")));
            datCtaBancaria.setnEstatusCta((null == request.getParameter("nEstatusCta") || "null".equals(request.getParameter("nEstatusCta"))) ? 0 : Integer.parseInt(request.getParameter("nEstatusCta")));
            datCtaBancaria.setcSucursal(null == (request.getParameter("cSucursal")) ? "" : new String((request.getParameter("cSucursal")).getBytes("ISO-8859-1"), "UTF-8"));
            datCtaBancaria.setcMotivoEliminaCta(null == (request.getParameter("cMotivoEliminaCta")) ? "" : new String((request.getParameter("cMotivoEliminaCta")).getBytes("ISO-8859-1"), "UTF-8"));
            datCtaBancaria.setcClabeInterbancaria(datCtaBancaria.getcBanco() + datCtaBancaria.getcPlaza() + datCtaBancaria.getcCuentaBancaria() + datCtaBancaria.getnDigitoVerificador());
        }
        return datCtaBancaria;
    }

    private DatosProveedorIncumplido fillDatProvedorIncumplido(HttpServletRequest request, Usuario u) throws Exception {
        StringBuilder msg = new StringBuilder();
        DatosProveedorIncumplido datProveedorIncump = new DatosProveedorIncumplido();
        try {
            if (null == request.getParameter("cIdRFC") || StringUtils.isBlank(request.getParameter("cIdRFC"))) {
                msg.append("El RFC es un dato requerido.\n");
            }
            if (null == request.getParameter("cOficioSancion") || StringUtils.isBlank(request.getParameter("cOficioSancion"))) {
                msg.append("El Oficio de Sanci\u00f3n es un dato requerido.\n");
            }
            if (null == request.getParameter("cRazonSocial") || StringUtils.isBlank(request.getParameter("cRazonSocial"))) {
                msg.append("La Raz\u00f3n Social es un dato requerido.\n");
            }
            if (null == request.getParameter("fFechaOficioSancion") || StringUtils.isBlank(request.getParameter("fFechaOficioSancion"))) {
                msg.append("La Fecha del Oficio de Sanci\u00f3n es un dato requerido.\n");
            }
            if (null == request.getParameter("fFechaTerminoFirma") || StringUtils.isBlank(request.getParameter("fFechaTerminoFirma"))) {
                msg.append("La Fecha de Termino para Firmar es un dato requerido.\n");
            }
            if (msg.length() > 0) {
                throw new ProveedorException(msg.toString());
            }
            datProveedorIncump.setcCodigoContratoCNET(null == (request.getParameter("cCodigoContratoCNET")) ? "" : request.getParameter("cCodigoContratoCNET"));
            datProveedorIncump.setcCodigoExpedienteCNET(null == (request.getParameter("cCodigoExpedienteCNET")) ? "" : request.getParameter("cCodigoExpedienteCNET"));
            datProveedorIncump.setcDescripcion(null == (request.getParameter("cDescripcion")) ? "" : request.getParameter("cDescripcion"));
            datProveedorIncump.setcEjercicioFiscal(null == (request.getParameter("cEjercicioFiscal")) ? "" : request.getParameter("cEjercicioFiscal"));
            //validar
            datProveedorIncump.setcIdRFC(request.getParameter("cIdRFC"));
            datProveedorIncump.setcLogin(u.getLogin());
            datProveedorIncump.setcNumeroContratoCNET(null == (request.getParameter("cNumeroContratoCNET")) ? "" : request.getParameter("cNumeroContratoCNET"));
            datProveedorIncump.setcNumeroProcedimiento(null == (request.getParameter("cNumeroProcedimiento")) ? "" : request.getParameter("cNumeroProcedimiento"));
            //validar
            datProveedorIncump.setcOficioSancion(request.getParameter("cOficioSancion"));
            //validar
            datProveedorIncump.setcRazonSocial((request.getParameter("cRazonSocial")));
            //validar
            datProveedorIncump.setfFechaOficioSancion(request.getParameter("fFechaOficioSancion"));
            //validar
            datProveedorIncump.setfFechaTerminoFirma(request.getParameter("fFechaTerminoFirma"));
        } finally {
            if (msg.length() > 0) {
                msg.delete(0, msg.length() - 1);
            }
            msg = null;
        }
        return datProveedorIncump;
    }
}
