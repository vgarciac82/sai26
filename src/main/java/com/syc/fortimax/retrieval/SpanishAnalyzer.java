/**
 * 
 */
package com.syc.fortimax.retrieval;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.ISOLatin1AccentFilter;
import org.apache.lucene.analysis.LengthFilter;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.WordlistLoader;
import org.apache.lucene.analysis.standard.StandardTokenizer;

/**
 * @author Guidny
 *
 */
public class SpanishAnalyzer extends Analyzer {
	public static String[] SPANISH_STOP_WORDS = null; //NOT FINAL

	/**
	 * Contains the stopwords used with the StopFilter.
	 */
	private Set stopTable = new HashSet();
	
	/**
	 * Contains words that should be indexed but not stemmed.
	 */
	private Set excludeTable = new HashSet();

	/**
	 * 
	 */
	public SpanishAnalyzer(String pathToStopTable) {
		if (SPANISH_STOP_WORDS==null) {
			SPANISH_STOP_WORDS = parseSpanishStopWords(pathToStopTable);
		}
		stopTable = StopFilter.makeStopSet( SPANISH_STOP_WORDS );

		//System.out.println("ya quedo con SPANISH_STOP_WORDS!");
 	}

	public SpanishAnalyzer(String[] someStopTable) {
		stopTable = StopFilter.makeStopSet( someStopTable );

		// TODO Auto-generated constructor stub
		//System.out.println("ya quedo con otra tabla de StopWords!");
	}
	/* (non-Javadoc)
	 * @see org.apache.lucene.analysis.Analyzer#tokenStream(java.lang.String, java.io.Reader)
	 */
	public TokenStream tokenStream(String arg0, Reader arg1) {
		// TODO Auto-generated method stub
		TokenStream result = new StandardTokenizer( arg1 );
		//result = new StandardFilter( result );
		
		/*
		result = new LengthFilter(result, 3, 30);
		result = new LowerCaseFilter( result );
		result = new ISOLatin1AccentFilter(result);
		result = new StopFilter( result, stopTable );
		*/
		
		//result = new StandardFilter( result );
		result = new LengthFilter(result, 3, 30);
		result = new LowerCaseFilter( result );
		result = new ISOLatin1AccentFilter(result);
		result = new BadScanFilter( result );
		result = new StopFilter( result, stopTable );
		
		
		//result = new BrazilianStemFilter( result, excltable );
		// Convert to lowercase after stemming!
		return result;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SpanishAnalyzer sa = new SpanishAnalyzer("./spanishStopWords.dic");
		//System.out.println(sa.toString());
	}
	
	private String[] parseSpanishStopWords(String pathToFile){
		String[] retVal = null;
		
		File stopWordsFile = new File(pathToFile);
		try {			
				FileReader fr = new FileReader(stopWordsFile);
				BufferedReader br = new BufferedReader(fr);
				String strLine = null;
				Vector tmpVec = new Vector();
				while ((strLine=br.readLine())!=null) {
					tmpVec.add(strLine);
				}
				if (tmpVec!=null) {
					retVal = new String[tmpVec.size()];
					tmpVec.toArray(retVal);
				}
		} catch (FileNotFoundException e) {
			//IGNORE THIS!
		} catch (IOException e) {
			//IGNORE THIS!
		}
		return retVal;
	}

	public Set getStopTable() {
		return stopTable;
	}

	public void setStopTable(Set stopTable) {
		this.stopTable = stopTable;
	}

	public Set getExcludeTable() {
		return excludeTable;
	}

	public void setExcludeTable(Set excludeTable) {
		this.excludeTable = excludeTable;
	}

	/**
	 * Builds an exclusionlist from an array of Strings.
	 */
	public void setStemExclusionTable( String[] exclusionlist ) {
		excludeTable = StopFilter.makeStopSet( exclusionlist );
	}
	
	/**
	 * Builds an exclusionlist from the words contained in the given file.
	 */
	public void setStemExclusionTable( File exclusionlist ) throws IOException {
		excludeTable = WordlistLoader.getWordSet( exclusionlist );
	}


}
