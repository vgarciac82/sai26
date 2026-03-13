package com.axtel.gestion.controller;

import java.io.IOException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.axtel.gestion.core.DocumentTreeOper;
import com.axtel.gestion.exception.DocumentTreeException;
import com.axtel.gestion.services.DocumentTreeService;
import com.axtel.gestion.services.implementation.DocumentTreeServiceImpl;
import com.syc.gestion.util.Util;


/**
 * Servlet implementation class TreeController
 */
public class DocumentTreeController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private String							jniName				= "";
	DocumentTreeService treeDocService ;
	private static final Logger				log					= LogManager.getLogger(DocumentTreeController.class);
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DocumentTreeController() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Servlet#init(ServletConfig)
	 */
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
		treeDocService = new DocumentTreeServiceImpl(jniName);
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String opt = request.getParameter( "option" );
		
		//Se recibe el id_caso para armar el objeto CASO en Baccked
		String result = null;
		try {
			switch ( opt ) {
				case DocumentTreeOper.GET_DOCUMENT_TREE:
					String idCaso = request.getParameter( "id_Caso" );					
					result = treeDocService.getTreeDocument( idCaso );
					
				break;
	
				default:
				break;
			}
		} catch ( DocumentTreeException e ) {
			log.error( e );
			Util.sendJSONError( response, e );
			
		}
		Util.sendJSON( response, result );
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
