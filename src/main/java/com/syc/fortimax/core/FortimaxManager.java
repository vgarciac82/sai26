package com.syc.fortimax.core;


import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;

import com.syc.fortimax.exceptions.FortimaxException;


public class FortimaxManager {

	public synchronized static Fortimax saveFile( Connection conn, Documento document, File f ) throws FortimaxException {
		try {

			Volumen vol = VolumenManager.getVolumen( conn );

			DocumentoManager.insertPaginaDocumento( conn, vol, document, "A", 0 );
			document = DocumentoManager.buscaDocumento( conn, document.getTituloAplicacion(), document.getIdGabinete(), document.getIdCarpetaPadre(), document.getIdDocumento() );

			File targetFile = new File( PaginaManager.getFilenamePath( conn, document.getTituloAplicacion(), document.getIdGabinete(), document.getIdCarpetaPadre(), document.getIdDocumento() ) );
			Files.copy( f.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES );

			Pagina p = document.getPaginaDocumento( 0 );
			p.setTamanoBytes( f.length() );

			PaginaManager.updatePagina( conn, p );
			return new Fortimax( document.getTituloAplicacion(), document.getIdGabinete(), document.getIdCarpetaPadre(), document.getIdDocumento() );
		} catch ( Exception exc ) {
			throw new FortimaxException( exc );
		}
	}

	public synchronized static Fortimax copyFile( Connection conn, Fortimax origen, String userCreation, String appDest, int cabinetDest ) throws FortimaxException {
		try {

			Fortimax fmxCopy = new Fortimax( appDest, cabinetDest, 0, 0 );

			Documento source = DocumentoManager.buscaDocumento( conn, origen );
			Carpeta sourceFolder = CarpetaManager.getCarpeta( conn, origen );

			Carpeta folderDest = CarpetaManager.getCarpetaByName( conn, appDest, cabinetDest, sourceFolder.getNombreCarpeta() );
			if ( folderDest == null )
				folderDest = CarpetaManager.createFolder( conn, fmxCopy, sourceFolder.getNombreCarpeta(), userCreation );

			Documento docCopy = DocumentoManager.buscaDocumento( conn, appDest, cabinetDest, folderDest.getIdCarpeta(), source.getNombreDocumento() );

			if ( docCopy == null )
				docCopy = DocumentoManager.creaDocumento( conn, folderDest, source.getNombreDocumento(), source.getExtension(), userCreation );

			docCopy.setExtension( source.getExtension() );

			if ( docCopy.getPaginasDocumento() == null || docCopy.getPaginasDocumento().length == 0 )
				FortimaxManager.saveFile( conn, docCopy, new File( source.getFullPathFilesNames()[0] ) );

			return new Fortimax( docCopy.getTituloAplicacion(), docCopy.getIdGabinete(), docCopy.getIdCarpetaPadre(), docCopy.getIdDocumento() );
		} catch ( Exception exc ) {
			throw new FortimaxException( exc );
		}
	}
}
