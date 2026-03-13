package com.syc.contable;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

public class ArchivoExcel {

	private HSSFWorkbook libro;
	private HSSFSheet hoja;
	private HSSFRow fila;
	private HSSFCell celda;
	private HSSFRichTextString texto;
	
	public HSSFWorkbook getLibro() {
		return libro;
	}

	public void setLibro(HSSFWorkbook libro) {
		this.libro = libro;
	}

	public HSSFSheet getHoja() {
		return hoja;
	}

	public void setHoja(HSSFSheet hoja) {
		this.hoja = hoja;
	}

	public HSSFRow getFila() {
		return fila;
	}

	public void setFila(HSSFRow fila) {
		this.fila = fila;
	}

	public HSSFCell getCelda() {
		return celda;
	}

	public void setCelda(HSSFCell celda) {
		this.celda = celda;
	}

	public HSSFRichTextString getTexto() {
		return texto;
	}

	public void setTexto(HSSFRichTextString texto) {
		this.texto = texto;
	}

	public ArchivoExcel(String cArchivo, String cTipoOperacion, String  cNombreLeyaut, ArrayList<String[]> arrcDet ) throws IOException{
		int IterCelElx=0;
		
		if (cTipoOperacion.equals("W")){
			//Es crear Archivo
			
		}else{
			//Es LEctura
			FileInputStream fileInputStream = new FileInputStream(cArchivo);
			POIFSFileSystem fsFileSystem = new POIFSFileSystem(fileInputStream);
			
			HSSFWorkbook workBook = new HSSFWorkbook(fsFileSystem);
			HSSFSheet hssfSheet = workBook.getSheetAt(0);
			Iterator rowIterator = hssfSheet.rowIterator();
			while (rowIterator.hasNext()){
				HSSFRow hssfRow = (HSSFRow) rowIterator.next();
				Iterator iterator = hssfRow.cellIterator();
				List cellTempList = new ArrayList();
				while (iterator.hasNext()){
					IterCelElx++;
					HSSFCell hssfCell = (HSSFCell) iterator.next();
				}
			}
			workBook.close();
		}
	}
	
	//@SuppressWarnings("deprecation")
	public FileOutputStream CreaArchivoExcel(String cNombreEcel ){
		
		FileOutputStream elFichero =null;
		//Crear Libro
		libro=getLibro();
		//Crear Hoja
		hoja=getHoja();
		//Crea Renglon
		fila=getFila();
		//crea Celda
		celda=getCelda();
		// Crea Texto enriquecido
		texto=getTexto();
		//asigna texto a la selda
		celda.setCellValue(texto);
		
		try {
			//Crea Archivo
			   elFichero = new FileOutputStream(cNombreEcel);
			   //Escribe el libro en el archivo 
			   libro.write(elFichero);
			} catch (Exception e) {
			   e.printStackTrace();
			}
			return elFichero;
	}
	
	public HSSFWorkbook CreateLibroXLS(){
		libro = new HSSFWorkbook();
		setLibro(libro);
		return libro;
	}
	
	public HSSFSheet CreaHojasXLS(HSSFWorkbook LibroXLS){
		hoja = libro.createSheet();
		setHoja(hoja);
		return hoja;
		
	}
	
	public HSSFRow CreaRenglonXLS(HSSFSheet HojaXLS){
		fila = hoja.createRow(1);
		setFila(fila);
		return fila;
		
	}
	
	public HSSFCell CreateCeldaXLS(HSSFRow RenglonXLS){
		//celda = fila.createCell((short)1); // ya está obsoleto este método
		celda = fila.createCell(1);
		setCelda(celda);
		return celda;
	}
	
	public HSSFRichTextString CreaTextoEnCelda(String cTextoxls){
		texto = new HSSFRichTextString(cTextoxls);
		setTexto(texto);
		return texto;
	}
	
	public FileInputStream EditaArchivo(String cFileExcel) throws FileNotFoundException{
		FileInputStream fileInputStream =null;
		try {
			fileInputStream = new FileInputStream(cFileExcel);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return fileInputStream; 
	}

	public boolean CierraExcel(FileInputStream fileInputStream){
		boolean errorCerando= true;
		try {
			fileInputStream.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}	
		
		return errorCerando;
	}
	
	public boolean  EscribeExcel(String cArchivo, String cRutArchivo, ArrayList<String[]> arrcDet){
		boolean rError=true;
		
		return rError;
		
	}
	
	public ArrayList<String[]> LeerExcel (String cArchivo, String cRutArchivo){
		ArrayList<String[]> aContenidoExcel = new ArrayList<String[]>();
		
		return aContenidoExcel;
		
		
	}
}
