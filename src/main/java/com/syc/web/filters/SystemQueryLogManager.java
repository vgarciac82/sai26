package com.syc.web.filters;

import java.sql.Connection;
import java.sql.PreparedStatement;

import com.syc.sai.contabilidad.utils.db.CloseObject;

public class SystemQueryLogManager {

	public static void insertLog(Connection conn, SystemQueryLog sql) throws Exception {
		PreparedStatement ps = null;
		String query = "INSERT INTO tloghttprequest(curi, ccontenttype, clocaladdr, cmethod, cpathinfo, " 
		             + "							cpathtranslated, cprotocol, cquerystring, cremoteaddr, " 
			         + "							cremotehost, nremoteport,  cremoteuser, crequestedsessionid, "
  			         + "							cscheme, cservername, cservletpath, ncontentlength, clocale, " 
			         + "							nlocalport, nsystemtime)" 
  			         + "VALUES     (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) ";

		try {
			ps = conn.prepareStatement(query);

			ps.setString(1, sql.getUri());
			ps.setString(2, sql.getContentType());
			ps.setString(3, sql.getLocalAddr());
			ps.setString(4, sql.getMethod());
			ps.setString(5, sql.getPathInfo());
			ps.setString(6, sql.getPathTranslated());
			ps.setString(7, sql.getProtocol());
			ps.setString(8, sql.getQueryString());
			ps.setString(9, sql.getRemoteAddr());
			ps.setString(10, sql.getRemoteHost());
			ps.setInt(11, sql.getRemotePort());
			ps.setString(12, sql.getRemoteUser());
			ps.setString(13, sql.getRequestedSessionId());
			ps.setString(14, sql.getScheme());
			ps.setString(15, sql.getServerName());
			ps.setString(16, sql.getServletPath());
			ps.setInt(17, sql.getContentLength());
			ps.setString(18, sql.getLocale());
			ps.setInt(19, sql.getLocalPort());
			ps.setLong(20, sql.getSystemTime());

			ps.executeUpdate();
			
		} finally {
			CloseObject.closeObject(ps);
		}

	}
}
