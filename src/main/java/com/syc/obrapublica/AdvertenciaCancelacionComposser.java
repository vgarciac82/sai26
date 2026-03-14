package com.syc.obrapublica;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import com.syc.dsmngr.DataSourceManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import com.syc.utils.ModuleProperties;
import com.syc.utils.mail.MessageComposer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdvertenciaCancelacionComposser extends DataSourceManager implements MessageComposer {

    private static final Logger log = LoggerFactory.getLogger(AdvertenciaCancelacionComposser.class);

    private static ModuleProperties moduleProperties;

    int diasMaximoApartado = -1;

    int diasAdvertencia = -1;

    private static final String ENCABEZADO_CORREO = "<b>Atenci\u00F3n.</b>" + "<br><br>El sistema ha detectado los siguientes contratos con recursos apartados autorizados a punto de llegar a la fecha l\u00EDmite de tolerancia. " + "<br>Se hace este hecho de su conocimiento para que tome las acciones pertinentes." + "<br>Listado de contratos:";

    private static final String FIRMA_CORREO = "<br><br>" + "\nAtte. " + "\nSistema de Administraci\u00F3n Integral SAI" + "\nM\u00F3dulo de Obra P\u00FAblica";

    public boolean correoProduccion = true;

    public AdvertenciaCancelacionComposser() throws Exception {
        synchronized (this) {
            if (moduleProperties == null)
                moduleProperties = new ModuleProperties("mop");
            String strDiasMaximoApartado = moduleProperties.getProperty("max.dias.apartado");
            if (strDiasMaximoApartado == null || "".equals(strDiasMaximoApartado))
                throw new Exception("No se especifico en las propiedades del sistema el parametro mop.max.dias.apartado");
            String strDiasAdvertencia = moduleProperties.getProperty("dias.alerta");
            if (strDiasAdvertencia == null || "".equals(strDiasAdvertencia))
                throw new Exception("No se especifico en las propiedades del sistema el parametro mop.dias.alerta");
            this.diasAdvertencia = Integer.parseInt(strDiasAdvertencia);
            this.diasMaximoApartado = Integer.parseInt(strDiasMaximoApartado);
        }
    }

    // IRD RO-0003 Se agrega parámetro MotivoCancela
    public List<AdvertenciaCancelacionCorreo> composeMessageList(String listaFolios, String motivoCancela) throws Exception {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<AdvertenciaCancelacionCorreo> advertencias = new ArrayList<AdvertenciaCancelacionCorreo>();
        if (!correoProduccion)
            return advertencias;
        //IRD 20131008 Se agrega tabla cg_usuario uApart para tener un correo cuando el responable es pe captura_obrapublica
        String query = "SELECT v.foliosai, " + "      CASE  " + "         WHEN v.ccvecontrato IS NULL  " + "               OR v.ccvecontrato = '' THEN 'No Capturado'  " + "         ELSE v.ccvecontrato  " + "       END                                                       AS ccvecontrato  " + "       ,  " + "       CONVERT(VARCHAR, v.faplicacion, 103)                      AS  " + "       faplicacion,  " + "       '$'  " + "       + CONVERT( VARCHAR, ( CONVERT( MONEY, v.nmontoconiva) ) ) AS nmontoconiva  " + "       ,  " + "       v.u_login,  " + "       v.diasapartado,  " + "       ? - v.diasapartado                                        AS  " + "       diasRestantes, " + "      isnull(caso.u_nombre,uApart.u_nombre)  AS nombre, " + "       isnull(caso.u_email,uApart.u_email)  AS correo, isnull(gp_valor,'') mailcopy " + " FROM   vobra_publica_apartado_pendiente v INNER JOIN  " + "		(SELECT	a.c_folio,  " + "				a.co_responsable,  " + "				u.u_login, " + "				u.u_email, " + "				u.u_nombre " + "		FROM	( " + "					SELECT	c.c_folio, " + "							o.co_responsable " + "					FROM  	cg_caso c WITH( NOLOCK )  " + "							INNER JOIN cg_caso_operacion o WITH( NOLOCK ) " + "									ON c.ID_CASO = o.ID_CASO " + "					WHERE	c.ID_TC = 23 " + "				) AS a LEFT OUTER JOIN cg_usuario u " + "						    ON a.co_responsable = u.u_nombre " + "		) as caso ON v.foliosai = caso.c_folio left join cg_usuario uApart on uApart.u_login = v.u_login " + "     left join cg_grupo_propiedades gp on G_NOMBRE ='PREFERENCIAS_CLIENTE' and GP_NOMBRE= 'mop.mail.copy' " + " WHERE  diasapartado >= ?  " + "       AND ? - v.diasapartado > 0 order by isnull(caso.u_email,uApart.u_email) , diasRestantes";
        try {
            conn = getConnection();
            if ("".equalsIgnoreCase(listaFolios)) {
                ps = conn.prepareStatement(query);
                ps.setInt(1, diasMaximoApartado);
                ps.setInt(2, diasMaximoApartado - diasAdvertencia);
                ps.setInt(3, diasMaximoApartado);
            } else {
                // IRD RO-0003 Se agrega parámetro MotivoCancela. Si listaFolios viene de una cancelación msiva y el query es diferente
                query = "select distinct v.foliosai, CASE   " + " 			         WHEN v.ccvecontrato IS NULL   " + " 			               OR v.ccvecontrato = '' THEN 'No Capturado'   " + " 			         ELSE v.ccvecontrato   " + " 			       END                                                       AS ccvecontrato   " + " 			       ,  CONVERT(VARCHAR, v.faplicacion, 103)                      AS   " + " 			       faplicacion,   " + " 			       '$' + CONVERT( VARCHAR, ( CONVERT( MONEY, v.nmontoconiva) ) ) AS nmontoconiva,   " + " 			       v.u_login, Datediff(day, v.faplicacion, Getdate()) diasapartado, 0 diasRestantes       ,  " + " 			      isnull(caso.u_nombre,uApart.u_nombre)  AS nombre,  " + " 			       isnull(caso.u_email,uApart.u_email)  AS correo, '' mailcopy " + " 			 from tObraPublicaApartadoEncabezado v " + "               INNER JOIN   " + " 					(SELECT	a.c_folio,   " + " 							a.co_responsable,   " + " 							u.u_login,  " + " 							u.u_email,  " + " 							u.u_nombre  " + " 					FROM	(  " + " 								SELECT	c.c_folio,  " + " 										o.co_responsable  " + " 								FROM  	cg_caso c WITH( NOLOCK )   " + " 										INNER JOIN cg_caso_operacion o WITH( NOLOCK )  " + " 												ON c.ID_CASO = o.ID_CASO  " + " 								WHERE	c.ID_TC = 23  " + " 							) AS a LEFT OUTER JOIN cg_usuario u  " + " 									    ON a.co_responsable = u.u_nombre  " + " 					) as caso ON v.foliosai = caso.c_folio left join cg_usuario uApart on uApart.u_login = v.u_login " + " 								 LEFT OUTER JOIN tobrapublicaprecompromisoencabezado p WITH(nolock)   ON v.foliosai = p.foliosai  where v.FolioSAI in(" + listaFolios + ") order by isnull(caso.u_email,uApart.u_email) , diasRestantes";
                ps = conn.prepareStatement(query);
            }
            rs = ps.executeQuery();
            String correoAnterior = "";
            StringBuffer messageBody = new StringBuffer();
            // IRD 20131008	Se formatea el correo en una tabla con todos los contratos por usuario para no enviar tantos correos, se agrega mop.mail.copy para enviar copia con la totalidad de los contratos y debe existir el registro en cg_grupo_propiedades
            StringBuffer messageCopy = new StringBuffer();
            messageCopy.append("<br>");
            messageCopy.append("<br>");
            messageCopy.append("<br>");
            messageCopy.append("<table>");
            messageCopy.append("<tr>");
            messageCopy.append("<td><b>Contrato</b></td>");
            messageCopy.append("<td><b>Folio SAI</b></td>");
            messageCopy.append("<td><b>Monto Apartado</b></td>");
            messageCopy.append("<td><b>Autorizacion apartado</b></td>");
            messageCopy.append("<td><b>Usuario Responsable</b></td>");
            messageCopy.append("<td><b>Dias Apartado</b></td>");
            messageCopy.append("<td><b>Dias restantes</b></td>");
            messageCopy.append("</tr>");
            boolean huboDatos = false;
            String mailCopy = "";
            while (rs.next()) {
                huboDatos = true;
                /*messageBody.append("<br>Contrato: <b>" + rs.getString("ccvecontrato") +"</b>");
				messageBody.append("<br>&nbsp;&nbsp;&nbsp;&nbsp;<b>Folio SAI:</b>" + rs.getString("foliosai"));
				messageBody.append("<br>&nbsp;&nbsp;&nbsp;&nbsp;<b>Monto Apartado:</b>" + rs.getString("nmontoconiva"));
				messageBody.append("<br>&nbsp;&nbsp;&nbsp;&nbsp;<b>Autorizacion apartado:</b>" + rs.getString("faplicacion"));
				messageBody.append("<br>&nbsp;&nbsp;&nbsp;&nbsp;<b>Usuario Responsable:</b>" + rs.getString("u_login"));
				messageBody.append("<br>&nbsp;&nbsp;&nbsp;&nbsp;<b>Dias Apartado:</b>" + rs.getString("diasapartado"));
				messageBody.append("<br>&nbsp;&nbsp;&nbsp;&nbsp;<b>Dias restantes:</b>" + rs.getString("diasRestantes"));
				messageBody.append("<br><br>");
				*/
                if (!correoAnterior.equalsIgnoreCase(rs.getString("correo"))) {
                    if (!"".equalsIgnoreCase(correoAnterior)) {
                        messageBody.append("</table>");
                        // IRD RO-0003 Si viene de cancelación masiva el correo es diferente
                        AdvertenciaCancelacionCorreo advertencia = null;
                        if ("".equalsIgnoreCase(listaFolios)) {
                            advertencia = new AdvertenciaCancelacionCorreo(ENCABEZADO_CORREO + messageBody.toString() + FIRMA_CORREO, correoAnterior);
                        } else {
                            final String ENCABEZADO_CORREO2 = "<b>Atenci\u00F3n.</b>" + "<br><br>El administrador ha cancelado los contratos listados a continuación por el motivo :'" + motivoCancela + "'.<br> " + "<br>Se hace este hecho de su conocimiento para que tome las acciones pertinentes.<br>Listado de contratos:";
                            advertencia = new AdvertenciaCancelacionCorreo(ENCABEZADO_CORREO2 + messageBody.toString() + FIRMA_CORREO, correoAnterior);
                        }
                        advertencias.add(advertencia);
                        messageBody = new StringBuffer();
                    }
                    messageBody.append("<br>");
                    messageBody.append("<br>");
                    messageBody.append("<br>");
                    messageBody.append("<table>");
                    messageBody.append("<tr>");
                    messageBody.append("<td><b>Contrato</b></td>");
                    messageBody.append("<td><b>Folio SAI</b></td>");
                    messageBody.append("<td><b>Monto Apartado</b></td>");
                    messageBody.append("<td><b>Autorizacion apartado</b></td>");
                    messageBody.append("<td><b>Usuario Responsable</b></td>");
                    messageBody.append("<td><b>Dias Apartado</b></td>");
                    messageBody.append("<td><b>Dias restantes</b></td>");
                    messageBody.append("</tr>");
                }
                messageBody.append("<tr>");
                messageBody.append("<td>" + rs.getString("ccvecontrato") + "</td>");
                messageBody.append("<td>" + rs.getString("foliosai") + "</td>");
                messageBody.append("<td>" + rs.getString("nmontoconiva") + "</td>");
                messageBody.append("<td>" + rs.getString("faplicacion") + "</td>");
                messageBody.append("<td>" + rs.getString("u_login") + "</td>");
                messageBody.append("<td>" + rs.getString("diasapartado") + "</td>");
                messageBody.append("<td>" + rs.getString("diasRestantes") + "</td>");
                messageBody.append("</tr>");
                messageCopy.append("<tr>");
                messageCopy.append("<td>" + rs.getString("ccvecontrato") + "</td>");
                messageCopy.append("<td>" + rs.getString("foliosai") + "</td>");
                messageCopy.append("<td>" + rs.getString("nmontoconiva") + "</td>");
                messageCopy.append("<td>" + rs.getString("faplicacion") + "</td>");
                messageCopy.append("<td>" + rs.getString("u_login") + "</td>");
                messageCopy.append("<td>" + rs.getString("diasapartado") + "</td>");
                messageCopy.append("<td>" + rs.getString("diasRestantes") + "</td>");
                messageCopy.append("</tr>");
                correoAnterior = rs.getString("correo");
                mailCopy = rs.getString("mailcopy");
            }
            if (huboDatos) {
                messageBody.append("</table>");
                // IRD RO-0003 Se viene de cancelacion masiva el correo es diferente
                AdvertenciaCancelacionCorreo ultAdvertencia = null;
                if ("".equalsIgnoreCase(listaFolios)) {
                    ultAdvertencia = new AdvertenciaCancelacionCorreo(ENCABEZADO_CORREO + messageBody.toString() + FIRMA_CORREO, correoAnterior);
                } else {
                    final String ENCABEZADO_CORREO2 = "<b>Atenci\u00F3n.</b>" + "<br><br>El administrador ha cancelado los contratos listados a continuación por el motivo :'" + motivoCancela + "'.<br> " + "<br>Se hace este hecho de su conocimiento para que tome las acciones pertinentes.<br>Listado de contratos:";
                    ultAdvertencia = new AdvertenciaCancelacionCorreo(ENCABEZADO_CORREO2 + messageBody.toString() + FIRMA_CORREO, correoAnterior);
                }
                advertencias.add(ultAdvertencia);
                messageCopy.append("</table>");
                String[] arreglo_copy = mailCopy.replace("'", "").split(";");
                for (int i = 0; i < arreglo_copy.length; i++) {
                    // IRD RO-0003 Si es cancelación masiva no se envía copia
                    if ("".equalsIgnoreCase(listaFolios)) {
                        ultAdvertencia = new AdvertenciaCancelacionCorreo(ENCABEZADO_CORREO + messageCopy.toString() + FIRMA_CORREO, arreglo_copy[i]);
                        advertencias.add(ultAdvertencia);
                    }
                }
            }
            return advertencias;
        } finally {
            try {
                CloseObject.closeObject(ps, false);
                CloseObject.closeObject(rs, false);
                CloseObject.closeObject(conn, false);
            } catch (Exception e) {
                log.warn(e.getMessage(), e);
            }
        }
    }

    public String composeMessage() throws Exception {
        return "";
    }
}
