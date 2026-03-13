package com.syc.gestion.core;


import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.jenkov.prizetags.tree.impl.Tree;
import com.jenkov.prizetags.tree.impl.TreeNode;
import com.jenkov.prizetags.tree.impl.TreeTableMapping;
import com.jenkov.prizetags.tree.itf.IResultSetProcessor;
import com.jenkov.prizetags.tree.itf.ITree;
import com.jenkov.prizetags.tree.itf.ITreeFilter;
import com.jenkov.prizetags.tree.itf.ITreeIteratorElement;
import com.jenkov.prizetags.tree.itf.ITreeNode;


public class TreeManager implements Serializable, IResultSetProcessor, ITreeFilter {

	private static final long	serialVersionUID	= 1L;
	private static Logger		log					= Logger.getLogger( TreeManager.class );

	private NodesToView			nodesToView			= new NodesToView();

	public static ITree getTree( Connection conn, BitacoraOperacion bo ) throws SQLException {

		PreparedStatement pstmnt = null;
		ITree tree = new Tree();

		try {

			TreeManager tm = new TreeManager();
			tm.setNodesToView( bo );

			tree.setSingleSelectionMode( true );
			tree.setFilter( tm );

			pstmnt = conn.prepareStatement( "SELECT * FROM vimx_arbol WHERE titulo_aplicacion = ? " + "AND id_gabinete = ? ORDER BY typerow, id_carpeta_padre, id_carpeta_hija" );

			pstmnt.setString( 1, bo.getTituloAplicacion() );
			pstmnt.setInt( 2, bo.getIdGabinete() );

			DataBaseTreeReader dtr = new DataBaseTreeReader( new TreeTableMapping( "vimx_arbol", "idColumn", "parentIdColumn", null, "nameColumn", "typeColumn", null ), tm );

			ITreeNode root = dtr.readTree( pstmnt.executeQuery() );

			tree.setRoot( root );
			tree.expandAll();
		} finally {
			if ( pstmnt != null )
				pstmnt.close();

			pstmnt = null;
		}

		tree.select( tree.getRoot().getId() );
		return tree;
	}

	public static ITree getTree( Connection conn, Caso c ) throws SQLException {

		PreparedStatement pstmnt = null;
		ITree tree = new Tree();

		if ( c.getIdGabinete() == -1 ) {
			ITreeNode n = new TreeNode( "vacio", "Sin Expediente", "carpeta.exproot" );
			n.setToolTip( "Debe guardar el Caso para crear un expediente" );
			tree.setRoot( n );
			tree.select( "vacio" );
			return tree;
		}

		try {

			TreeManager tm = new TreeManager();
			tm.setNodesToView( c );

			tree.setSingleSelectionMode( true );
			tree.setFilter( tm );

			pstmnt = conn.prepareStatement( "SELECT * FROM vimx_arbol WHERE titulo_aplicacion = ? " + "AND id_gabinete = ? ORDER BY typerow, id_carpeta_padre, id_carpeta_hija" );

			pstmnt.setString( 1, c.getTipoCaso().getGavetaAsociada() );
			pstmnt.setInt( 2, c.getIdGabinete() );

			// DataBaseTreeReader dtr = new DataBaseTreeReader(new
			// TreeTableMapping("vimx_arbol", "idColumn",
			// "parentIdColumn", null, "nameColumn", "typeColumn", null), tm);
			DataBaseTreeReader dtr = new DataBaseTreeReader( new TreeTableMappingFortimax( "vimx_arbol", "idColumn", "parentIdColumn", null, "nameColumn", "typeColumn", null, "nombre_usuario" ), tm );

			ITreeNode root = dtr.readTree( pstmnt.executeQuery() );

			tree.setRoot( root );
			tree.expandAll();
		} finally {
			if ( pstmnt != null )
				pstmnt.close();

			pstmnt = null;
		}

		tree.select( tree.getRoot().getId() );
		return tree;
	}

	public void init( ITree tree ) {
	}

	public boolean accept( ITree tree, ITreeIteratorElement node ) {

		if ( "vacio".equals( node.getId() ) )
			return true;

		if ( nodesToView.getNodesId().contains( node.getId() ) )
			return true;

		for ( Iterator iter = nodesToView.getNodesId().iterator(); iter.hasNext(); ) {
			ITreeNode[] path = tree.getNodePath( ( String ) iter.next() );
			for ( int i = 0; i < path.length; i++ ) {
				if ( path[i].equals( node.getNode() ) )
					return true;
			}
		}

		return false;
	}

	public void process( ResultSet result, ITreeNode node ) {

		String parent = null;
		String unidad = new String();
		String base = new String();
		String dir = new String();
		String fisicalFilename = new String();
		String logicFilename = new String();
		String nombreUsusario = new String();

		try {
			parent = result.getString( "parentIdColumn" );
			unidad = result.getString( "unidad_disco" );
			base = result.getString( "ruta_base" );
			dir = result.getString( "ruta_directorio" );
			fisicalFilename = result.getString( "nom_archivo_vol" );
			logicFilename = result.getString( "nom_archivo_org" );
			nombreUsusario = result.getString( "nombre_usuario" );

			node.setToolTip( node.getName() );
		} catch ( SQLException exc ) {
			log.warn( exc );
		}

		if ( nodesToView.getNames().contains( node.getName() ) || nodesToView.getNames().contains( "all" ) ) {

			NodeInformation nd = new NodeInformation( nodesToView.getPermisos(), node.getName(), unidad, base, dir, fisicalFilename, logicFilename, nombreUsusario );

			node.setObject( nd );

			nodesToView.setNodeId( node.getId() );
		} else if ( parent != null ) {
			for ( Iterator iter = nodesToView.getChildren().iterator(); iter.hasNext(); ) {
				NodesToView ct = ( NodesToView ) iter.next();
				if ( ( nodesToView.getNodesId().contains( parent ) || nodesToView.getNames().contains( "all" ) ) && ( ct.getNames().contains( node.getName() ) || ct.getNames().contains( "all" ) ) && ( !node.getType().startsWith( "carpeta" ) ) ) {

					NodeInformation nd = new NodeInformation( nodesToView.getPermisos(), node.getName(), unidad, base, dir, fisicalFilename, logicFilename, nombreUsusario );

					node.setObject( nd );

					nodesToView.setNodeId( node.getId() );
				}
			}
		}
	}

	private void setNodesToView( BitacoraOperacion bo ) {

		Operacion o = bo.getOperacion();
		if ( o == null )
			return;

		if ( o.getFolderDocto() == null )
			return;

		String filter = o.getFolderDocto();
		if ( ( !filter.startsWith( "fld-" ) ) || ( filter.indexOf( "->doc-" ) == -1 ) )
			throw new RuntimeException( "Sintaxis invalida" );

		filter = filter.trim();
		String[] dupla = filter.split( "->" );

		int dobleDot = dupla[0].indexOf( ":" );
		if ( dobleDot == -1 )
			throw new RuntimeException( "Sintaxis invalida en carpeta" );

		nodesToView = new NodesToView();
		nodesToView.setPermisos( dupla[0].substring( 4, dobleDot ) );
		nodesToView.setNames( dupla[0].substring( dobleDot + 1 ).split( "," ) );

		dobleDot = dupla[0].indexOf( ":" );
		if ( dobleDot == -1 )
			throw new RuntimeException( "Sintaxis invalida en documento" );

		NodesToView doc = new NodesToView();
		doc.setPermisos( dupla[1].substring( 4, dobleDot ) );
		doc.setNames( dupla[1].substring( dobleDot + 1 ).split( "," ) );

		nodesToView.setChild( doc );
	}

	private void setNodesToView( Caso c ) {

		CasoOperacion co = c.getCasoOperacion( 0 );
		if ( co == null )
			return;

		Operacion o = co.getOperacion();
		if ( o == null )
			return;

		if ( o.getFolderDocto() == null )
			return;

		String filter = o.getFolderDocto();
		if ( ( !filter.startsWith( "fld-" ) ) || ( filter.indexOf( "->doc-" ) == -1 ) )
			throw new RuntimeException( "Sintaxis invalida" );

		filter = filter.trim();
		String[] dupla = filter.split( "->" );

		int dobleDot = dupla[0].indexOf( ":" );
		if ( dobleDot == -1 )
			throw new RuntimeException( "Sintaxis invalida en carpeta" );

		nodesToView = new NodesToView();
		nodesToView.setPermisos( dupla[0].substring( 4, dobleDot ) );
		nodesToView.setNames( dupla[0].substring( dobleDot + 1 ).split( "," ) );

		dobleDot = dupla[0].indexOf( ":" );
		if ( dobleDot == -1 )
			throw new RuntimeException( "Sintaxis invalida en documento" );

		NodesToView doc = new NodesToView();
		doc.setPermisos( dupla[1].substring( 4, dobleDot ) );
		doc.setNames( dupla[1].substring( dobleDot + 1 ).split( "," ) );

		nodesToView.setChild( doc );
	}

	private class NodesToView implements Serializable {

		private static final long	serialVersionUID	= 1L;

		private String				permisos			= new String();
		private List				children			= new ArrayList();
		private List				names				= new ArrayList();
		private List				nodeId				= new ArrayList();

		public List getNames() {
			return names;
		}

		public void setNames( String[] names ) {
			for ( int i = 0; i < names.length; i++ )
				this.names.add( names[i] );
		}

		public List getNodesId() {
			return nodeId;
		}

		public void setNodeId( String nodeId ) {
			this.nodeId.add( nodeId );
		}

		public String getPermisos() {
			return permisos;
		}

		public void setPermisos( String permisos ) {
			this.permisos = permisos.toLowerCase();
		}

		public void setChild( NodesToView child ) {
			children.add( child );
		}

		public List getChildren() {
			return children;
		}
	}
}
