package com.syc.sai.interfaces;

import java.io.IOException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import com.syc.gestion.UsuarioBusinessLogic;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.custom.FolioGeneratorInterface;
import com.syc.gestion.servlet.GestionInterface;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "CFDIInterface", urlPatterns = { "/interface/SolicitudCFDI" })
public class CFDIInterface extends HttpServlet implements GestionInterface {

    /**
     */
    private static final long serialVersionUID = -2432006515404472071L;

    String jniName = "";

    String folioGenerator = "";

    private static final Logger log = LoggerFactory.getLogger(CFDIInterface.class);

    public static final String EGRESO = "E";

    public static final String INGRESO = "I";

    public static final String CLAVE_PROD_GASOLINA = "84141602";

    public static final String RFC_PROVEEDOR_GASOLINA = "84141602";

    public static final String DESC_SERVICIO_VALES = "CONTRAPRESTACION";

    /*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.
	 * HttpServletRequest , javax.servlet.http.HttpServletResponse)
	 */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Usuario u = new Usuario();
            u.setLogin("admin");
            UsuarioBusinessLogic ubl = new UsuarioBusinessLogic(jniName);
            u = ubl.getUsuario(u);
            if (StringUtils.isEmpty(u.getU_UR()))
                u.setU_UR("A02");
            String tipoComprobante = req.getParameter("tipoComprobante");
            String FechaEmision = req.getParameter("FechaEmision");
            String RFCCliente = req.getParameter("RFCCliente");
            String nombreCliente = req.getParameter("nombreCliente");
            String paternoCliente = req.getParameter("paternoCliente");
            String maternoCliente = req.getParameter("maternoCliente");
            String domicilioFiscal = req.getParameter("domicilioFiscal");
            String concepto = req.getParameter("concepto");
            String metodoPago = req.getParameter("metodoPago");
            String formaPago = req.getParameter("formaPago");
            String nCuenta = req.getParameter("nCuenta");
            String subTotal = req.getParameter("subTotal");
            String impuesto = req.getParameter("impuesto");
            String descuento = req.getParameter("descuento");
            String total = req.getParameter("total");
            FolioGeneratorInterface fg = null;
            try {
                ClassLoader cl = getClass().getClassLoader();
                Class<?> clase = cl.loadClass(folioGenerator);
                fg = (FolioGeneratorInterface) clase.newInstance();
            } catch (ClassNotFoundException exc) {
                log.error("Generador de folios", exc);
                throw new ServletException(exc);
            } catch (InstantiationException exc) {
                log.error("Generador de folios", exc);
                throw new ServletException(exc);
            } catch (IllegalAccessException exc) {
                log.error("Generador de folios", exc);
                throw new ServletException(exc);
            }
            CFDIBusinessLogic cfdibl = new CFDIBusinessLogic(jniName);
            Caso c = cfdibl.generaCaso(u, 57, fg, "REVISOR_CUSTF");
            int nFolioCFDI = Integer.parseInt(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1));
            cfdibl.setnFolioCFDI(nFolioCFDI);
            cfdibl.setConcepto(concepto);
            cfdibl.setDescuento(descuento);
            cfdibl.setDomicilioFiscal(domicilioFiscal);
            cfdibl.setFechaEmision(FechaEmision);
            cfdibl.setFormaPago(formaPago);
            cfdibl.setImpuesto(impuesto);
            cfdibl.setMaternoCliente(maternoCliente);
            cfdibl.setMetodoPago(metodoPago);
            cfdibl.setnCuenta(nCuenta);
            cfdibl.setNombreCliente(nombreCliente);
            cfdibl.setPaternoCliente(paternoCliente);
            cfdibl.setRFCCliente(RFCCliente);
            cfdibl.setSubTotal(subTotal);
            cfdibl.setTipoComprobante(tipoComprobante);
            cfdibl.setTotal(total);
            cfdibl.insertaInformacion(u, c);
            ServletOutputStream out = resp.getOutputStream();
            out.println("<html>");
            out.println("<body>");
            out.println("<p>Se genero exitosamente la informacion en SAI. Puede cerrar esta ventana");
            out.println("</p>");
            out.println("</body>");
            out.println("</html>");
            out.flush();
            out.close();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ServletOutputStream out = resp.getOutputStream();
            out.println("<html>");
            out.println("<body>");
            out.println("<p>Ocurrio el siguiente error:");
            out.println(e.toString());
            out.println("</p>");
            out.println("</body>");
            out.println("</html>");
            out.flush();
            out.close();
        }
    }

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        try {
            InitialContext ic = new InitialContext();
            jniName = (String) ic.lookup("java:comp/env/dataSourceRefName");
            if (jniName == null) {
                jniName = "jdbc/gestion";
                log.info("Environment Entry \"dataSourceRefName\" nula usando default \"" + jniName + "\"");
            } else
                log.info("dataSourceRefName=" + jniName);
        } catch (NamingException exc) {
            jniName = "jdbc/gestion";
            log.info("Environment Entry \"dataSourceRefName\" no definida usando default \"" + jniName + "\"");
        }
        try {
            InitialContext ic = new InitialContext();
            folioGenerator = (String) ic.lookup("java:comp/env/folioGeneratorInterface");
            if (folioGenerator == null) {
                folioGenerator = "com.syc.gestion.custom.DefaultFolioGenerator";
                log.info("Environment Entry \"folioGeneratorInterface\" nula usando default \"" + folioGenerator + "\"");
            } else
                log.info("folioGeneratorInterface=" + folioGenerator);
        } catch (NamingException exc) {
            folioGenerator = "com.syc.gestion.custom.DefaultFolioGenerator";
            log.info("Environment Entry \"folioGeneratorInterface\" no definida usando default \"" + folioGenerator + "\"");
        }
    }
}
