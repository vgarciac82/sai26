package com.axtel.gestion.services.implementation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.axtel.gestion.core.Node;
import com.axtel.gestion.exception.DocumentTreeException;
import com.axtel.gestion.services.DocumentTreeService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.syc.cfdi.db.CloseObject;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoManager;


public class DocumentTreeServiceImpl extends DataSourceManager implements DocumentTreeService {

	public DocumentTreeServiceImpl( String jniName) {
		super.init(jniName);
		
	}

	@Override
	public String getTreeDocument(  String idCaso ) throws DocumentTreeException {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement pstmnt = null;
		String id = null;
		String parentId = null;
		String text = null ;
		String type = null;
		String icon = null ;
		boolean opened = true;
		boolean selected = true;
		List<Node> nodes = new ArrayList<>();
		String json = null;		
		try {
			conn= getConnection();
			Caso c = getInfoCaso(idCaso,conn);
			pstmnt = conn.prepareStatement( "SELECT idcolumn, parentidcolumn, namecolumn, typecolumn FROM vimx_arbol WHERE titulo_aplicacion = ? " +
											 "AND id_gabinete = ? ORDER BY typerow, id_carpeta_padre, id_carpeta_hija" );
			pstmnt.setString( 1, c.getTipoCaso().getGavetaAsociada());
			pstmnt.setInt( 2, c.getIdGabinete() );
			rs = pstmnt.executeQuery();
			Map <String, Node> nodesMap = new HashMap<>();
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			
			while (rs.next()) {
		          id = rs.getString("idcolumn");
		          parentId = rs.getString("parentidcolumn");
		          text = rs.getString("namecolumn");
		          type = rs.getString("typecolumn");
		          selected = (parentId == null)&&selected;
		          icon = (type.equals("docto.externo")) ? "jstree-file":"jstree-folder";
		          Node node = new Node(id, text, icon, opened, selected);  
		          nodesMap.put( id, node );
		            
	             if (parentId == null) {
		                // si no tiene padre, es un nodo raíz
	            	 nodes.add(node);	            	
		         } else {
		                // si tiene padre, se establece la relación padre-hijo        	
		             Node parent = nodesMap.get(parentId);	              
		             if (parent != null) {
		               	 List<Node>  childs = new ArrayList<>();	                	
		                 childs = parent.getChildren(); 
		                 if (childs == null){
		                		// no tiene hijos entonces solo se agrega al padre el nuevo hijo
		                		List<Node>  child = new ArrayList<>();	 
		                		child.add( node );
		                		parent.setChildren( child );
		                }else{
		                	// tiene hijos entonces se incrementa a esos hijos uno nuevo 
		                	childs.add( node );
		                	parent.setChildren( childs );
		                	}		                			                	
		              }
		        }		        		           
		    }
			
			json = gson.toJson(nodes);
			
		} catch ( SQLException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				rs.close();
			} catch ( SQLException e ) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if ( pstmnt != null )
				try {
					pstmnt.close();
				} catch ( SQLException e ) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    pstmnt = null;
		    CloseObject.closeObject( conn );
		}
		return json;

	}

	private Caso getInfoCaso(String idCaso,Connection conn) {
		//Generas un caso "generico"
		Caso c = new Caso();
		//Estableces el id_caso
		c.setIdCaso(Integer.parseInt( idCaso ));		 
		//Llamas a caso manager para que te devuelva el caso con toda su info:		 
		try {
			c = CasoManager.select(conn, c);
		} catch ( SQLException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return c;

	}
}
