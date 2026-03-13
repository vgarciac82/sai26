package com.axtel.sisecop.web;


import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.axtel.sai.sicove.expedient.repositories.ExpedientRepository;
import com.axtel.sai.sicove.expedient.repositories.impl.JDBCExpedientRepostory;
import com.axtel.sisecop.entities.ProyectoServicioTDR;
import com.axtel.sisecop.services.ProjectProcurementProcessService;
import com.axtel.sisecop.services.ProjectTDRService;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;


@WebServlet( urlPatterns = { "/SISECOP/cvExpedient" } )
@MultipartConfig
public class ProcurementProcessDocumentationController extends HttpServlet {

	private static final long	serialVersionUID	= -7044347273418051491L;
	private static final Logger	log					= Logger.getLogger( ProcurementProcessDocumentationController.class );
	private String				jniName;
	private ProjectProcurementProcessService	projectTDRService;
	private ExpedientRepository	expedientRepository;

	@Override
	public void init( ServletConfig config ) throws ServletException {
		super.init( config );
		try {
			InitialContext ic = new InitialContext();
			jniName = ( String ) ic.lookup( "java:comp/env/dataSourceRefName" );

			if ( jniName == null ) {
				jniName = "jdbc/gestion";
				log.info( "Environment Entry \"dataSourceRefName\" nula usando default \"" + jniName + "\"" );
			} else
				log.info( "dataSourceRefName=" + jniName );
		} catch ( NamingException exc ) {
			jniName = "jdbc/gestion";
			log.info( "Environment Entry \"dataSourceRefName\" no definida usando default \"" + jniName + "\"" );
		}

		expedientRepository = new JDBCExpedientRepostory();
		projectTDRService = new ProjectProcurementProcessService( jniName );
		projectTDRService.setExpedientRepository( expedientRepository );

	}

	@Override
	protected void doPost( HttpServletRequest request, HttpServletResponse response ) throws IOException {

		log.trace( "Adding document CV to expedient." );

		try {

			HttpSession session = request.getSession( false );
			if ( session == null )
				response.sendRedirect( "../../index.jsp" );

			Usuario user = ( Usuario ) session.getAttribute( GestionInterface.ATT_USER );
			if ( user == null )
				response.sendRedirect( "../../index.jsp" );

			log.trace( "Saving document. Reading info from Resume" );
			int servicioId = Integer.parseInt( request.getParameter( "servicioId" ) );
			int idProcess = Integer.parseInt( request.getParameter( "idProcess" ) );
			String folderName = request.getParameter( "folderName" );
			String tdrDescription = request.getParameter( "tdrDescription" );

			log.info( "Saving TDR Document. \nService ID: " + servicioId + " \nProcess ID: " + idProcess + "\n Folder: " + folderName + "\n Document Description: " + tdrDescription );

			Part filePart = request.getPart( "documentFile" );
			String fileName = filePart.getSubmittedFileName();
			log.trace( "Now reading file:" + fileName );

			File file = File.createTempFile( "upload_", "_" + fileName );
			log.trace( "Temp File created successfully: " + file.getAbsolutePath() );
			log.trace( "Moving to temp file" );
			FileUtils.copyInputStreamToFile( filePart.getInputStream(), file );

			log.debug( "File: " + fileName + " readed successfully" );

			log.trace( "Traying to save TDR Info" );
			ProyectoServicioTDR projectTDR = projectTDRService.addProjectTDR( idProcess, servicioId, tdrDescription, folderName, file, user.getLogin() );

			log.info( "TDR Saved!!!: " + projectTDR );

			Map<String, Object> result = new HashMap<>();
			result.put( "success", "true" );
			result.put( "message", "El archivo se adjuntó correctamente." );
			result.put( "tdr", projectTDR );

			Util.sendJSON( response, result );

		} catch ( Exception ex ) {
			log.error( ex, ex );
			Util.sendJSONError( response, ex );
		}
	}

	@Override
	protected void doDelete( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
		try {

			HttpSession session = request.getSession( false );
			if ( session == null )
				throw new RuntimeException( "Sesion expirada." );

			Usuario user = ( Usuario ) session.getAttribute( GestionInterface.ATT_USER );
			if ( user == null )
				throw new RuntimeException( "Sesion expirada." );

			int idTDR = Integer.parseInt( request.getParameter( "idTDR" ) );

			log.info( "Deleting reference term: " + idTDR );

			projectTDRService.deleteProjectTDR( idTDR );

			log.info( "Deleted row: " + idTDR + " succesfully" );

			Map<String, String> result = new HashMap<>();

			result.put( "success", "true" );
			result.put( "message", "El registro se elimino correctamente." );

			Util.sendJSON( response, result );

		} catch ( Exception ex ) {
			log.error( ex, ex );
			Util.sendJSONError( response, ex );
		}
	}

}
