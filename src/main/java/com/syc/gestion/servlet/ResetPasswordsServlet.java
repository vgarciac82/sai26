package com.syc.gestion.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import javax.sql.DataSource;
import com.syc.gestion.core.Usuario;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "ResetPasswordsServlet", urlPatterns = { "/gstnmngr/ReinciarContra" })
public class ResetPasswordsServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private DataSource ds = null;

    private String jniName = null;

    private static Logger log = LoggerFactory.getLogger(GestionServlet.class);

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        try {
            InitialContext ic = new InitialContext();
            jniName = (String) ic.lookup("java:comp/env/dataSourceRefName");
            if (jniName == null) {
                jniName = "jdbc/gestion";
                log.info("Object: {}", "Environment Entry \"dataSourceRefName\" nula usando default \"" + jniName + "\"");
            } else
                log.info("Object: {}", "dataSourceRefName=" + jniName);
        } catch (NamingException exc) {
            jniName = "jdbc/gestion";
            log.info("Object: {}", "Environment Entry \"dataSourceRefName\" no definida usando default \"" + jniName + "\"");
        }
        Context initContext;
        try {
            initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            ds = (DataSource) envContext.lookup(jniName);
        } catch (NamingException ne) {
            try {
                initContext = new InitialContext();
                Context envContext = (Context) initContext.lookup("java:comp/env");
                ds = (DataSource) envContext.lookup(jniName);
            } catch (NamingException nexc) {
                try {
                    initContext = new InitialContext();
                    ds = (DataSource) initContext.lookup(jniName);
                } catch (NamingException exc) {
                    ne.printStackTrace();
                    exc.printStackTrace();
                    throw new RuntimeException("No se encontro la fuente '" + jniName + "'");
                }
            }
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Usuario u = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
        String strUsuario = u.getLogin();
        if (session == null) {
            log.warn("No hay sesion");
            response.sendRedirect("../index.jsp");
            return;
        }
        Connection conn = null;
        String comboUsuarios = "";
        try {
            conn = ds.getConnection();
            String sSQL = "select u_login from cg_usuario where U_ESTATUS = 'A' and U_LOGIN in (SELECT U_LOGIN FROM CG_USUARIO_PROPIEDADES WHERE UP_NOMBRE = 'CCENTROCONTABLE') order by 1";
            /*String sSQL="SELECT DISTINCT U.U_LOGIN, UR.R_NOMBRE, ADMIN_DUENO FROM CG_USUARIO U, CG_USUARIO_ROLE UR, CG_ROLE R WHERE U.U_LOGIN=UR.U_LOGIN" +
					"  AND R.R_NOMBRE=UR.R_NOMBRE" +
					"  AND ADMIN_DUENO IN (SELECT UR.R_NOMBRE FROM CG_USUARIO_ROLE ur WHERE ur.U_LOGIN='"+strUsuario+"')" +
					"  AND U.U_LOGIN in (SELECT U_LOGIN FROM CG_USUARIO_PROPIEDADES WHERE UP_NOMBRE = 'CCENTROCONTABLE')";*/
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sSQL);
            while (rs.next()) {
                comboUsuarios += "<option value=\"" + rs.getString(1) + "\">" + rs.getString(1) + "</option>\n";
            }
            rs.close();
            st.close();
        } catch (Exception exc) {
            throw new ServletException(exc.getMessage());
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (Exception exc) {
            }
        }
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<html>");
        out.println("<head>															       ");
        out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" pageEncoding=\"utf-8\"   >						       ");
        out.println("<title>Control de Gesti&oacute;n CG-Flow</title>										       ");
        out.println("<style type=\"text/css\">													       ");
        out.println("	<!--															       ");
        out.println("	.campo {														       ");
        out.println("		border: 1px solid #000000;											       ");
        out.println("		background-color: #FFFFFF;											       ");
        out.println("		font-family: Arial, Helvetica, sans-serif;									       ");
        out.println("		color: #000000;													       ");
        out.println("	}															       ");
        out.println("	.boton {														       ");
        out.println("		border: 1px solid #003366;											       ");
        out.println("		background-color: #FFFFFF;											       ");
        out.println("		font-family: Arial, Helvetica, sans-serif;									       ");
        out.println("		font-size: 0.9em;												       ");
        out.println("		color: #000000;													       ");
        out.println("		cursor: hand;													       ");
        out.println("	}															       ");
        out.println("	td {															       ");
        out.println("		font-family: Arial, Helvetica, sans-serif;									       ");
        out.println("		font-size: .9em;												       ");
        out.println("	}															       ");
        out.println("	.titulo1 {														       ");
        out.println("		border-bottom-width: thin;											       ");
        out.println("		border-bottom-style: solid;											       ");
        out.println("		border-bottom-color: #025C32;											       ");
        out.println("		font-family: Verdana, Arial, Helvetica, sans-serif;								       ");
        out.println("		font-size: 1.5em;												       ");
        out.println("		color: #333333;													       ");
        out.println("	}															       ");
        out.println("	body {															       ");
        out.println("		background-attachment: fixed;											       ");
        out.println("		background-image: url(imagenes/logotipo-cgflow.jpg);								       ");
        out.println("		background-repeat: no-repeat;											       ");
        out.println("		background-position: right bottom;										       ");
        out.println("	}															       ");
        out.println("	-->															       ");
        out.println("	</style>														       ");
        out.println("</head>															       ");
        out.println("<body scroll=\"no\" onload=\"document.getElementById('login').focus()\">							       ");
        out.println("	<form action=\"ContraReiniciada\" method=\"post\">					       ");
        //out.println("		<p class=\"titulo1\">												       ");
        //out.println("			<img src=\"../imagenes/logotipo-cgflow.jpg\" alt=\"CGFLow\" width=\"200\" height=\"54\" align=\"absmiddle\">      ");
        //out.println("		</p>														       ");
        out.println("		<p>														       ");
        out.println("			<br>													       ");
        out.println("		</p>														       ");
        out.println("		<table align=\"left\">											       ");
        out.println("			<tr>													       ");
        out.println("				<td>												       ");
        out.println("					<strong>Usuario:</strong>								       ");
        out.println("				</td>												       ");
        out.println("				<td>												       ");
        out.println("					<select name=\"login\" id=\"login\" tabindex=\"1\">  ");
        out.println("						" + comboUsuarios);
        out.println("					</select>");
        out.println("				</td>												       ");
        out.println("			</tr>													       ");
        out.println("			<tr>													       ");
        out.println("				<td align=\"center\">										       ");
        out.println("					&nbsp;											       ");
        out.println("				</td>											       ");
        out.println("				<td align=\"center\">										       ");
        out.println("					<input name=\"aceptar\" type=\"submit\" class=\"boton\" id=\"aceptar\" value=\"Reiniciar Password\" tabindex=\"4\">");
        out.println("				</td>												       ");
        out.println("			</tr>													       ");
        out.println("			<tr>													       ");
        out.println("			<tr><td colspan = \"2\">&nbsp;</td>													       ");
        out.println("			</tr>													       ");
        out.println("				<td align=\"center\">										       ");
        out.println("					&nbsp;											       ");
        out.println("				</td>												       ");
        out.println("				<td align=\"center\">										       ");
        out.println(request.getParameter("Msg") == null ? "&nbsp;" : request.getParameter("Msg"));
        out.println("				</td>												       ");
        out.println("			</tr>													       ");
        out.println("		</table>													       ");
        out.println("	</form>															       ");
        if (request.getParameter("Err") != null && request.getParameter("Err").length() > 0) {
            out.println("	<script>															       ");
            out.println("	alert(\"" + request.getParameter("Err") + "\");															       ");
            out.println("	</script>															       ");
        }
        out.println("</body>															       ");
        out.println("</html>															       ");
        out.flush();
    }
}
