package com.syc.sai.contabilidad.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.TablasBusinessLogic;
import com.syc.gestion.servlet.GestionInterface;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import jakarta.servlet.annotation.WebServlet;
import java.util.Base64;

@WebServlet(name = "InsertServlet", urlPatterns = { "/export/Insert" })
public class InsertServlet extends HttpServlet implements GestionInterface {

    private static final long serialVersionUID = -5034769853645642993L;

    /*protected String              html= new String();
	protected int                 numCols=0;
    protected String[]            nombreCols;
    protected String[]            formatoCols;
    protected ArrayList <String>  datos= new ArrayList<String>();
	protected String              titulo="";
	protected String              centroContable="";
	protected String              filtroReporte="";
	protected SimpleDateFormat    fecha = new SimpleDateFormat("EEEE d' de 'MMMM' del 'yyyy",new Locale("es","MX"));
	protected Calendar            gc=GregorianCalendar.getInstance();
	protected String              fechaFormateada=fecha.format(gc.getTime()).toUpperCase();*/
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null) {
            sendError(response, "Session Terminada. Ingrese nuevamente al sistema");
            return;
        }
        Usuario u = (Usuario) session.getAttribute(ATT_USER);
        if (u == null) {
            sendError(response, "No hay usuario en session. Ingrese nuevamente al sistema");
            return;
        }
        String[] datosFijos = new String[6];
        datosFijos[0] = request.getParameter("inFolioDocPoliza");
        datosFijos[1] = request.getParameter("ihPolCtroContable");
        datosFijos[2] = request.getParameter("icTipoPoliza");
        datosFijos[3] = request.getParameter("iaejerciciofiscal");
        datosFijos[4] = request.getParameter("inTipoAjuste").equals("") ? "0" : request.getParameter("inTipoAjuste");
        datosFijos[5] = request.getParameter("iperiodo13");
        insertPolizaEvento(new String(request.getParameter("dTable").getBytes("ISO-8859-1"), "UTF-8"), datosFijos);
    }

    public boolean insertPolizaEvento(String datos, String[] datosFijos) {
        String[] data = datos.split("\\|");
        TablasBusinessLogic tb = new TablasBusinessLogic("jdbc/gestion");
        final int COLS = 15;
        final int VALUES = 22;
        int filas = data.length / COLS;
        int pointer = 0;
        boolean ok = true;
        //=new String[VALUES];
        String[] tmp1;
        String initQuery = "INSERT INTO tdocpolizadetalle  (nfoliodocpoliza, ndocrenglon, ncuenta,  nsubcuenta,  cevento, mimporte,  ccentrocontable, aejerciciofiscal, ctipopoliza, cconcepto, nTipoAjuste, Periodo13, parcial, cCABMS, cCUCOP, cPartida, nIdGrupoEvento, nIdSubGrupoEvento, cIdEventoManual, nNumeroEvento )VALUES";
        StringBuffer tmp = new StringBuffer();
        int ndocrenglon = 0;
        String INSERT = "";
        String Query = "";
        String doubleTmp = "";
        for (int f = 0; f < filas; f++) {
            System.out.println("DATA POINTER 5=" + data[pointer + 5]);
            doubleTmp = (data[pointer + 5].replace("$", "").equals("0.00") ? data[pointer + 6] : data[pointer + 5]);
            INSERT += "(" + datosFijos[0] + "," + "" + (ndocrenglon += 1) + "," + "" + "'" + data[pointer + 1] + "'," + "'" + data[pointer + 3] + "'," + "'" + (data[pointer + 5].replace("$", "").equals("0.00") ? "ABONO" : "CARGO") + "'," + "" + doubleTmp.replace(",", "").replace("$", "") + "," + "'" + datosFijos[1] + "'," + "'" + datosFijos[3] + "'," + "'" + datosFijos[2] + "'," + "'" + data[pointer + 4] + "'," + "" + datosFijos[4] + "," + "'" + datosFijos[5] + "'," + "'" + data[pointer + 7] + "'," + "" + (data[pointer + 12].equals("") ? "NULL" : "'" + data[pointer + 12] + "'") + "," + "" + (data[pointer + 13].equals("") ? "NULL" : data[pointer + 13]) + "," + "" + (data[pointer + 11].equals("") ? "NULL" : "'" + data[pointer + 11] + "'") + "," + "" + data[pointer + 8] + "," + "" + data[pointer + 9] + "," + "'" + data[pointer + 10] + "'," + "" + data[pointer + 14] + "";
            if (f == (filas - 1))
                INSERT += ")";
            else
                INSERT += "),";
            pointer += COLS;
        }
        initQuery += INSERT;
        System.out.println(":::QERY::::" + initQuery);
        ok = tb.insertPolizaDetalle(initQuery) == 1 ? true : false;
        ok = tb.actualizaEncabezado(datosFijos[0]) == 1 ? true : false;
        return ok;
    }

    //insert poliza
    public String getValor(String data) {
        return (data == null ? "" : data);
    }

    protected static void sendError(HttpServletResponse resp, String msg) throws IOException {
        PrintWriter out = resp.getWriter();
        out.println("<html>");
        out.println("\t<body>");
        out.println("\t\t<h1>Se presento el siguiente problema mientras se llenaba el reporte</h1><br>");
        out.println("\t\t<br>" + msg + "<br>");
        out.println("\t</body>");
        out.println("</html>");
        out.flush();
        out.close();
    }
}
