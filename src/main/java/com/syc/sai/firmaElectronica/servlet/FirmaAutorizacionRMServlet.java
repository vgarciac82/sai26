/**
 * 
 */
package com.syc.sai.firmaElectronica.servlet;


import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.axtel.web.exceptions.SessionExpiredException;
import com.syc.adquisiciones.core.DatosRecepcionFIEL;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.sai.firmaElectronica.core.RecepcionMaterialFIEL;
import com.syc.sai.firmaElectronica.interfaces.SolicitudFirmaElectronica;


/**
 * @author vicente.garcia
 *
 */
public class FirmaAutorizacionRMServlet extends HttpServlet implements GestionInterface {

	private static final long	serialVersionUID	= 1L;
	private static final Logger	log					= LogManager.getLogger( FirmaAutorizacionRMServlet.class );
	private static String		REPORT_PATH			= "";
	private static boolean esReenviaEmail			=false;

	@Override
	protected void doPost( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException {

		boolean success = false;
		String msg = "";
		try {

			HttpSession session = req.getSession( false );
			if ( session == null ) {
				log.warn( "Usuario sin sesion. " );
				throw new SessionExpiredException( "Su sesion ha terminado. Ingrese nuevamente al sistema." );
			}

			Usuario u = ( Usuario ) session.getAttribute( ATT_USER );
			if ( u == null ) {
				log.warn( "Sesion sin objeto usuario. " );
				throw new SessionExpiredException( "Su sesion ha terminado. Ingrese nuevamente al sistema." );
			}

			DatosRecepcionFIEL rm = instanciaRecepcionMaterial( req );
			rm.setIdUsuarioCaptura( u.getLogin() );
			
			SolicitudFirmaElectronica rmf = new RecepcionMaterialFIEL();
			rmf.setDocName( "Atenta Nota ".concat( rm.getcIdRecepcionMat() ) );
			rmf.setFileExtension( "pdf" );
			rmf.setReportPath( FirmaAutorizacionRMServlet.REPORT_PATH );
			rmf.setUsuario( u );
			rmf.setDocument( "CONTRATODIVERSO" );


			( ( RecepcionMaterialFIEL ) rmf ).setRecepcionMaterial( rm );			
			
			if(esReenviaEmail) {
				( ( RecepcionMaterialFIEL ) rmf ).reenviaEmailRM();
			}else {
				( ( RecepcionMaterialFIEL ) rmf ).solicitaAutorizacionRM();
			}
			
			resp.getWriter();
			success = true;
		} catch ( Exception e ) {
			log.error( e, e );
			msg = e.getMessage();
		}

		try {
			JSONObject obj = new JSONObject();
			obj.put( "success", success );
			obj.put( "message", msg );

			PrintWriter out = resp.getWriter();
			resp.setContentType( "application/json;charset=UTF-8" );
			resp.setCharacterEncoding( "UTF-8" );
			out.print( obj.toString() );
			out.flush();
			out.close();

		} catch ( Exception e ) {
			log.error( "Error procesando respuesta! " + e, e );
			throw new ServletException( e );
		}

	}

	/**
	 * Genera una nueva recepcion de material leyendo la informacion del
	 * request.
	 * 
	 * @param req
	 *            Request
	 * @return Recepcion de Material.
	 * @throws UnsupportedEncodingException
	 */
	private DatosRecepcionFIEL instanciaRecepcionMaterial( HttpServletRequest req ) throws UnsupportedEncodingException {
		
		DatosRecepcionFIEL rm = new DatosRecepcionFIEL();
		
		rm.setcIdPedContDef( new String( req.getParameter( "cIdContratoDefinitivo" ).getBytes( "ISO-8859-1" ), "UTF-8" ) );
		rm.setcIdRecepcionMat( new String( req.getParameter( "cIDRecepMat" ).getBytes( "ISO-8859-1" ), "UTF-8" ) );
		rm.setFolioNota( new String( req.getParameter( "cFolioNota" ).getBytes( "ISO-8859-1" ), "UTF-8" ) );
		rm.setMotivoAutorizacion( new String(  req.getParameter( "dMotivoNota" ).getBytes( "ISO-8859-1" ), "UTF-8") );
		rm.setNumeroEmpleado( Integer.parseInt( req.getParameter( "cNumeroEmpleado" ) ) );
		rm.setnIdEntraAlmacen( Integer.parseInt( req.getParameter( "nIdEntraAlmacen" ) ) );
		rm.setnIdEstatusAtentaNotaFirmada( Integer.parseInt( req.getParameter( "nIdEstatusAtentaNotaFirmada" ) ) );
		rm.setRequiereAtentaNota( Integer.parseInt( req.getParameter( "requiereAtentaNota" ) ) );
		esReenviaEmail=StringUtils.isBlank(req.getParameter( "reenviaEmail" ))? false:"true".equalsIgnoreCase( req.getParameter( "reenviaEmail" ) );
		return rm;
	}

	@Override
	public void init( ServletConfig config ) throws ServletException {
		super.init( config );
		REPORT_PATH = getServletContext().getRealPath( "Reportes" );
	}

}
