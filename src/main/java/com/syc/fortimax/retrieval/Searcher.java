package com.syc.fortimax.retrieval;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.Term;
import org.apache.lucene.misc.ChainedFilter;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MultiSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.QueryFilter;
import org.apache.lucene.search.TermQuery;

public class Searcher {
	public static final String INDEX_DIR ="./Index";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public static Hits searchSingleIndex(String pathToSearch, 
										 String pathToStopTable, 
										 String queryString,
										 String[] filters){
		Hits myHits = null;
		
		// Create a searcher from the index file 
		//String indexFile = "/tmp/fileindex"; 
		IndexSearcher searcher = null;
		
		if (pathToSearch== null ||
			pathToSearch.trim().length()==0) {
			pathToSearch = INDEX_DIR;
		}
		
		try { 
				searcher = new IndexSearcher(pathToSearch); 
				
		} catch(IOException e) { 
				System.out.println("Unable to open index file: " + pathToSearch); 
				//System.exit(1); 
		}
	
		// parse the query String. 
		Query query = null; 
		try { 
				// If no prefix for a word is given, then search in 
				// content by default 
				QueryParser qp = new QueryParser("contents", new SpanishAnalyzer(pathToStopTable));
				//QueryParser qp = new QueryParser("contents", new org.apache.lucene.analysis.KeywordAnalyzer());
				query = qp.parse(queryString); 
		} catch(ParseException e) { 
			close(searcher); 
			System.out.println("Unable to parse: " + queryString); 
			//System.exit(1); 
		}
	
		// get the hits from the searcher for 
		// the given query 
		try { 
				Filter myFilter = buildFilters(filters);
				if (myFilter != null) {
					myHits = searcher.search(query, myFilter);
				} else {
					myHits = searcher.search(query);
				}
				
		} catch(IOException e) { 
			close(searcher); 
			System.out.println("IO Error."); 
			e.printStackTrace(); 
			//System.exit(1);
		}
	
		// iterate over the results 
		// the results are an array of document 
		// display the first 10 results 
		if(myHits.length() > 0) { 
			for(int i = 0; i < myHits.length(); i++) { 
				// retrieve the indexed Fields from the result documents. 
				// we could also get a persisten object key and load 
				// the objects for further attributes to display 
				try {
				System.out.println("Path=["			+myHits.doc(i).get("path")			+ "], " +
								   "Aplicacion=["	+myHits.doc(i).get("aplicacion")	+ "], " +
								   "Gabinete=["		+myHits.doc(i).get("gabinete")		+ "], " +
								   "Carpeta=["		+myHits.doc(i).get("carpeta")		+ "], " +
								   "Documento=["	+myHits.doc(i).get("documento")		+ "], " +
								   "Pagina=["		+myHits.doc(i).get("pagina")		+ "], ");
				} catch (CorruptIndexException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();					
				}
			} 
		} else { 
			System.out.println("No matching files found."); 
		}
		return myHits;
	}

	public static Hits searchMultipleIndexes(String pathToSearch, 
										     String pathToStopTable, 
											 String queryString,
											 String[] filters){
		Hits myHits = null;
		
		Vector mySearchVector = new Vector();
		
		File f = new File(pathToSearch);
		if (f.exists() && f.isDirectory()) {
			//el indice ha sido creado
			IndexSearcher searcher = null;
			try { 
				searcher = new IndexSearcher(pathToSearch); 
			} catch(IOException e) { 
					System.out.println("Unable to open index file: " + pathToSearch); 
					//System.exit(1); 
			}				
			if (searcher!=null) {
				mySearchVector.add(searcher);
			}
		}	
		
		
		for (int i=0; i<10; i++) {
			String myPath = pathToSearch;
			if (myPath== null ||
				myPath.trim().length()==0) {
				myPath = INDEX_DIR;
			}
			myPath +=  i + "";
			
			f = new File(myPath);
			if (f.exists() && f.isDirectory()) {
				//el indice ha sido creado
				IndexSearcher searcher = null;
				try { 
					searcher = new IndexSearcher(myPath); 
				} catch(IOException e) { 
						System.out.println("Unable to open index file: " + myPath); 
						//System.exit(1); 
				}				
				if (searcher!=null) {
					mySearchVector.add(searcher);
				}
			}	
		}
		//convierte el vector de buscadores en un array
		if (mySearchVector.size()>0) {
			IndexSearcher[] searchers = new IndexSearcher[mySearchVector.size()];
			mySearchVector.toArray(searchers);
			MultiSearcher multiSearch = null;
			try { 
				multiSearch = new MultiSearcher(searchers);
				// parse the query String. 
				Query query = null; 
				try { 
						// If no prefix for a word is given, then search in 
						// content by default 
						QueryParser qp = new QueryParser("contents", new SpanishAnalyzer(pathToStopTable));
						//QueryParser qp = new QueryParser("contents", new org.apache.lucene.analysis.KeywordAnalyzer());
						query = qp.parse(queryString); 
				} catch(ParseException e) { 
					close(multiSearch); 
					System.out.println("Unable to parse: " + queryString); 
				}
				// get the hits from the searcher for 
				// the given query 
				Filter myFilter = buildFilters(filters);
				if (myFilter != null) {
					myHits = multiSearch.search(query, myFilter);
				} else {
					myHits = multiSearch.search(query);
				}
			} catch(IOException e) { 
				close(multiSearch); 
				System.out.println("IO Error."); 
				e.printStackTrace(); 
			}

			// iterate over the results 
			// the results are an array of document 
			// display the first 10 results 
			if(myHits.length() > 0) { 
				/*
				for(int i = 0; i < myHits.length(); i++) { 
					// retrieve the indexed Fields from the result documents. 
					// we could also get a persisten object key and load 
					// the objects for further attributes to display 
					try {
					System.out.println("Path=["			+myHits.doc(i).get("path")			+ "], " +
									   "Aplicacion=["	+myHits.doc(i).get("aplicacion")	+ "], " +
									   "Gabinete=["		+myHits.doc(i).get("gabinete")		+ "], " +
									   "Carpeta=["		+myHits.doc(i).get("carpeta")		+ "], " +
									   "Documento=["	+myHits.doc(i).get("documento")		+ "], " +
									   "Pagina=["		+myHits.doc(i).get("pagina")		+ "], ");
					} catch (CorruptIndexException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();					
					}
				}
				*/ 
			} else { 
				System.out.println("No matching files found."); 
			}
		}
		return myHits;
	}
	
	// close searcher 
	public static void close(IndexSearcher mySearcher) { 
		if(null != mySearcher) { 
			try { 
					mySearcher.close(); 
			} catch(Exception e) { 
				
			} 
		} 
	}
	
	// close searcher 
	public static void close(MultiSearcher multiSearch) {
		if(null != multiSearch) { 
			try { 
				multiSearch.close(); 
			} catch(Exception e) { 
				
			} 
		} 
	}
	
	public static Filter buildFilters(String[] filters) {
		Filter retVal = null;
		Filter[] filterGroup = null;
		Vector tmpVec = new Vector();
		//
		// FILTRO DE APLICACION
		//
		Filter Filter1 = null;
		if (filters != null &&
			filters[0] != null &&
			filters[0].trim().length()>0) {
			Query aplicacionQuery  = new TermQuery(new Term("aplicacion",filters[0]));
			Filter1 = new QueryFilter(aplicacionQuery);
			tmpVec.add(Filter1);
		}        
		//
		// FILTRO DE GABINETE
		//
		Filter Filter2 = null;
		if (filters != null &&
			filters[1] != null &&
			filters[1].trim().length()>0) {
			int myGab = -1;
			try {
				myGab = Integer.parseInt(filters[1]);
			} catch (NumberFormatException nfe) {
				//ignore
			}
			if (myGab>-1) {
				Query gabineteQuery  = new TermQuery(new Term("gabinete",filters[1]));
				Filter2 = new QueryFilter(gabineteQuery);
				tmpVec.add(Filter2);
			}
		}        
		//
		// FILTRO DE CARPETA
		//
		Filter Filter3 = null;
		if (filters != null &&
			filters[2] != null &&
			filters[2].trim().length()>0) {
			int myCar = -1;
			try {
				myCar = Integer.parseInt(filters[2]);
			} catch (NumberFormatException nfe) {
				//ignore
			}
			if (myCar>-1) {
				Query carpetaQuery  = new TermQuery(new Term("carpeta",filters[2]));
				Filter3 = new QueryFilter(carpetaQuery);
				tmpVec.add(Filter3);
			}
		} 
		
		if (tmpVec.size()>0) {
			if (tmpVec.size()>1) {
				filterGroup = new Filter[tmpVec.size()];
				tmpVec.toArray(filterGroup);
				retVal = new ChainedFilter(filterGroup, ChainedFilter.AND);
			} else {
				retVal = (Filter) tmpVec.get(0);
			}
		}

		return retVal;
	}
}
