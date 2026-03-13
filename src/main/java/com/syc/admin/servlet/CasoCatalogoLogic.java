package com.syc.admin.servlet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.jenkov.prizetags.tree.impl.Tree;
import com.jenkov.prizetags.tree.itf.ITree;
import com.syc.dsmngr.DataSourceManager;
import com.syc.fortimax.core.Aplicacion;
import com.syc.fortimax.core.AplicacionManager;
import com.syc.fortimax.core.Carpeta;
import com.syc.fortimax.core.CarpetaManager;
import com.syc.fortimax.core.Descripcion;
import com.syc.fortimax.core.Documento;
import com.syc.fortimax.core.DocumentoManager;
import com.syc.fortimax.core.Fortimax;
import com.syc.fortimax.core.OrgCarpeta;
import com.syc.fortimax.core.OrgCarpetaManager;
import com.syc.fortimax.core.Pagina;
import com.syc.fortimax.core.PaginaManager;
import com.syc.fortimax.core.TipoDocumento;
import com.syc.fortimax.core.TipoDocumentoManager;
import com.syc.fortimax.core.Volumen;
import com.syc.fortimax.core.VolumenManager;
import com.syc.fortimax.exceptions.FortimaxException;
import com.syc.gestion.core.AlarmaManager;
import com.syc.gestion.core.BitacoraManager;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoDato;
import com.syc.gestion.core.CasoDatoManager;
import com.syc.gestion.core.CasoManager;
import com.syc.gestion.core.CasoOperacion;
import com.syc.gestion.core.CasoOperacionManager;
import com.syc.gestion.core.FortimaxFile;
import com.syc.gestion.core.GestionException;
import com.syc.gestion.core.Operacion;
import com.syc.gestion.core.OperacionManager;
import com.syc.gestion.core.TipoCasoManager;
import com.syc.gestion.core.TreeManager;
import com.syc.gestion.core.URLDocumento;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.custom.FolioGeneratorInterface;

public class CasoCatalogoLogic extends DataSourceManager {

	private static String CASO_END = "TERMINAR";
	private static Logger log = Logger.getLogger(CasoCatalogoLogic.class);

	private static String OPER_CONTINUE = "CONTINUAR";

	public CasoCatalogoLogic(String jniName) {

		super.init(jniName);
	}

	public synchronized Caso actualizaCasoDato(Caso c, Map data) throws GestionException {

		Connection conn = null;

		try {
			conn = getConnection();

			CasoDatoManager.update(conn, c.getIdTC(), c.getIdCaso(), data);

			AplicacionManager.updateExpediente(conn, c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), data);

			conn.commit();
		} catch (SQLException exc) {
			try {
				conn.rollback();
			} catch (SQLException ex) {
				log.warn("Error en rollback", ex);
			}

			log.error("Actualizando caso", exc);
			throw new GestionException(exc);
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException exc) {
				log.warn("Cerrando conexion a base de datos", exc);
			}

			conn = null;
		}

		return getCaso(c.getIdCaso(), c.getCasoOperacion(0).getIdCasoOper());
	}

	public synchronized void actualizaDocumentoGestion(Fortimax fimx) throws GestionException {

		Connection conn = null;

		try {
			conn = getConnection();

			DocumentoManager.upateDocumento(conn, fimx.getTituloAplicacion(), fimx.getIdGabinete(),
					fimx.getIdCarpeta(), fimx.getIdDocumento(), "IMAX_FILE");

			conn.commit();
		} catch (SQLException exc) {
			try {
				conn.rollback();
			} catch (SQLException ex) {
				log.warn("En rollback", ex);
			}

			log.error("Actualizando Documento a tipo Fortimax", exc);
			throw new GestionException(exc);
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException exc) {
				log.warn("Cerrando conexion a base de datos", exc);
			}

			conn = null;
		}
	}

	public synchronized Caso avanzaCaso(Caso c, String u_login, String observ, String resp[], String oper[], Map data,
			String pathPrefix) throws GestionException {

		Caso rco = null;
		Connection conn = null;
		boolean delete = true;

		if (resp.length != oper.length) {
			log.error("Las dimensiones no coinciden rep[" + resp.length + "] vs oper[" + oper.length + "]");
			throw new GestionException("Las dimensiones no coinciden rep[" + resp.length + "] vs oper[" + oper.length
					+ "]");
		}

		try {
			int[] idCasoOperSgte = new int[resp.length];

			conn = getConnection();


			CasoDatoManager.update(conn, c.getIdTC(), c.getIdCaso(), data);

			for (int i = 0; i < resp.length; i++) {

				if (OPER_CONTINUE.equalsIgnoreCase(oper[i].trim())) {
					continue;
				} else if (CASO_END.equalsIgnoreCase(oper[i].trim())) {
					delete = false;
					for (int j = i + 1; j < idCasoOperSgte.length; j++)
						idCasoOperSgte[j] = -1;
					BitacoraManager.registraCasoOperacion(conn, u_login, c, idCasoOperSgte, resp, oper, true);
					CasoManager.terminaCaso(conn, c, resp, oper);
					break;
				}

				Operacion o = new Operacion();
				o.setIdTC(c.getIdTC());
				o.setNombre(oper[i].trim());

				o = OperacionManager.select(conn, o);
				if (o == null) {
					log.error("No se localizo la Operacion \"" + oper[i] + "\"");
					throw new GestionException("No se localizo la Operacion \"" + oper[i] + "\"");
				}

				CasoOperacion co = CasoOperacionManager.nuevoCasoOperacion(conn, resp[i].trim(), observ, c, o);
				CasoOperacionManager.insert(conn, co);

				idCasoOperSgte[i] = co.getIdCasoOper();
			}

			if (delete) {
				BitacoraManager.registraCasoOperacion(conn, u_login, c, idCasoOperSgte, resp, oper);
				CasoOperacionManager.delete(conn, c.getIdCaso(), c.getCasoOperacion(0).getIdCasoOper());
			}

			if ((c.getStatus() & Caso.MSG_SENDED) == Caso.MSG_SENDED)
				c.setStatus(c.getStatus() ^ Caso.MSG_SENDED);

			c.setStatus(c.getStatus() ^ Caso.EXECUTED);

			CasoManager.update(conn, c);

			conn.commit();
		}   catch (SQLException exc) {
			try {
				conn.rollback();
			} catch (SQLException ex) {
				log.warn("En rollback", ex);
			}

			log.error("No se logro avanzar el caso (" + c.getIdCaso() + ", " + c.getCasoOperacion(0).getIdCasoOper()
					+ ")", exc);
			throw new GestionException("No se logro avanzar el caso (" + c.getIdCaso() + ", "
					+ c.getCasoOperacion(0).getIdCasoOper() + ")", exc);
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException exc) {
				log.warn("Cerrando conexion a base de datos", exc);
			}

			conn = null;
		}

		return rco;
	}

	public synchronized String borraDocumento(String nodeId,
											  String OCRProgramPath, 
										      String OCRParameter1, 
										      String OCRParameter2, 
										      String luceneDbPath, 
										      String luceneStopwordsPath, 
										      int luceneMergeFactor, 
										      int luceneMaxMergeDocs) 
									throws GestionException {

		String doc_name = null;
		Connection conn = null;

		try {
			conn = getConnection();

			Fortimax fimx = new Fortimax(nodeId);
			FortimaxFile[] files = PaginaManager.getPaginasDeDocumento(conn, fimx.getTituloAplicacion(), fimx
					.getIdGabinete(), fimx.getIdCarpeta(), fimx.getIdDocumento());

			for (int i = 0; i < files.length; i++) {
				if (!files[i].getFile().delete()) {
					log.warn("No se logro borrar archivo \"" + files[i].getFisicalName() + "\"");
					files[i].getFile().deleteOnExit();
				}
			}

			Documento d = DocumentoManager.selectDocumento(conn, fimx.getTituloAplicacion(), fimx.getIdGabinete(), fimx
					.getIdCarpeta(), fimx.getIdDocumento());

			doc_name = d.getNombreDocumento();

			PaginaManager.delete(conn, 
								fimx.getTituloAplicacion(), 
								fimx.getIdGabinete(), 
								fimx.getIdCarpeta(), 
								fimx.getIdDocumento(),
								 OCRProgramPath, 
							     OCRParameter1, 
							     OCRParameter2, 
							     luceneDbPath, 
							     luceneStopwordsPath, 
							     luceneMergeFactor, 
							     luceneMaxMergeDocs);

			DocumentoManager.delete(conn, fimx.getTituloAplicacion(), fimx.getIdGabinete(), fimx.getIdCarpeta(), fimx
					.getIdDocumento());

			conn.commit();
		} catch (Exception exc) {
			try {
				conn.rollback();
			} catch (SQLException ex) {
				log.warn("En rollback", ex);
			}

			log.error("Borrando pagina", exc);
			throw new GestionException(exc);
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException exc) {
				log.warn("Cerrando conexion a base de datos", exc);
			}

			conn = null;
		}

		return doc_name;
	}

	public synchronized int borraPaginaDocumento(String nodeId, 
												 int pagina,												   String OCRProgramPath, 
												 String OCRParameter1, 
												 String OCRParameter2, 
												 String luceneDbPath, 
												 String luceneStopwordsPath, 
												 int luceneMergeFactor, 
												 int luceneMaxMergeDocs) 
						throws GestionException {

		Connection conn = null;
		FortimaxFile[] ff = new FortimaxFile[0];

		try {
			conn = getConnection();

			Fortimax f = new Fortimax(nodeId);
			ff = PaginaManager.getPaginasDeDocumento(conn, f.getTituloAplicacion(), f.getIdGabinete(),
					f.getIdCarpeta(), f.getIdDocumento());

			Pagina p = PaginaManager.selectPagina(conn, f.getTituloAplicacion(), f.getIdGabinete(), f.getIdCarpeta(), f
					.getIdDocumento(), ff[pagina].getFisicalName());

			PaginaManager.deletePagina(conn, 
									   p,
									   OCRProgramPath, 
									   OCRParameter1, 
									   OCRParameter2, 
									   luceneDbPath, 
									   luceneStopwordsPath, 
									   luceneMergeFactor, 
									   luceneMaxMergeDocs);

			if (!ff[pagina].getFile().delete()) {
				ff[pagina].getFile().deleteOnExit();
				log.warn("No se logro borrar pagina \"" + ff[pagina].getFile().getAbsolutePath() + "\"");
			}

			conn.commit();
		} catch (Exception exc) {
			try {
				conn.rollback();
			} catch (SQLException ex) {
				log.warn("En rollback", ex);
			}

			log.error("Borrando pagina", exc);
			throw new GestionException(exc);
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException exc) {
				log.warn("Cerrando conexion a base de datos", exc);
			}

			conn = null;
		}

		return ff.length;
	}

	public Caso consultaCaso(int id_caso, int id_caso_oper) throws GestionException {

		Caso rc = null;
		Connection conn = null;

		try {
			conn = getConnection();

			Caso srchCase = new Caso();
			srchCase.setIdCaso(id_caso);

			rc = CasoManager.select(conn, srchCase, id_caso_oper);
		} catch (SQLException exc) {
			log.error("No se logro consultar el caso (" + id_caso + ", " + id_caso_oper + ")", exc);
			throw new GestionException("No se logro consultar el caso (" + id_caso + ", " + id_caso_oper + ")", exc);
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException exc) {
				log.warn("Cerrando conexion a base de datos", exc);
			}

			conn = null;
		}

		return rc;
	}

	public synchronized void copiaPaginaADocumento(Caso c, 
												   Fortimax f, 
												   int index, 
												   String titulo_aplicacion, 
												   int idDocto,
												   String OCRProgramPath, 
												   String OCRParameter1, 
												   String OCRParameter2, 
												   String luceneDbPath, 
												   String luceneStopwordsPath, 
												   int luceneMergeFactor, 
												   int luceneMaxMergeDocs )
			throws Exception {

		Connection conn = null;

		try {
			conn = getConnection();

			FortimaxFile[] ff = PaginaManager.getPaginasDeDocumento(conn, f.getTituloAplicacion(), f.getIdGabinete(), f
					.getIdCarpeta(), f.getIdDocumento());

			Volumen vol = VolumenManager.getVolumen(conn);
			
			TipoDocumento td = TipoDocumentoManager.buscaTipoDocumento(conn, f.getTituloAplicacion(), idDocto);
			Documento d = DocumentoManager.buscaDocumento(conn, f.getTituloAplicacion(), f.getIdGabinete(), td
					.getNombreTipoDocto());

			if (!d.getNombreTipoDocto().equals("IMAX_FILE"))
				DocumentoManager.upateDocumento(conn, d.getTituloAplicacion(), d.getIdGabinete(),
						d.getIdCarpetaPadre(), d.getIdDocumento(), "IMAX_FILE");

			// Se recupera para tener los cambios del update
			d = DocumentoManager.selectDocumento(conn, d.getTituloAplicacion(), d.getIdGabinete(), d
					.getIdCarpetaPadre(), d.getIdDocumento());

			File file = ff[index].getFile();
			DocumentoManager.insertPaginaDocumento(conn, 
												   vol, 
												   c, 
												   d, 
												   "A", 
												   file.length(), 
												   OCRProgramPath, 
												   OCRParameter1, 
												   OCRParameter2,
												   luceneDbPath,
												   luceneStopwordsPath,
												   luceneMergeFactor,
												   luceneMaxMergeDocs);

			// Se recupera para actualizar paginas
			d = DocumentoManager.selectDocumento(conn, d.getTituloAplicacion(), d.getIdGabinete(), d
					.getIdCarpetaPadre(), d.getIdDocumento());

			String filename = d.getFullPathFilesNames()[d.getFullPathFilesNames().length - 1];

			DataInputStream in = new DataInputStream(new FileInputStream(file));
			DataOutputStream out = new DataOutputStream(new FileOutputStream(filename));

			int fileLength = 0, length = 0;
			byte[] buffer = new byte[4 * 1024];
			while ((in != null) && ((length = in.read(buffer)) != -1)) {
				out.write(buffer, 0, length);
				fileLength += length;
			}

			in.close();
			out.close();

			conn.commit();
		} catch (IOException exc) {
			try {
				conn.rollback();
			} catch (SQLException ex) {
				log.warn("En rollback", ex);
			}

			log.error("Actualizando Documento a tipo Fortimax", exc);
			throw new GestionException(exc);
		} catch (SQLException exc) {
			try {
				conn.rollback();
			} catch (SQLException ex) {
				log.warn("En rollback", ex);
			}

			log.error("Actualizando Documento a tipo Fortimax", exc);
			throw new GestionException(exc);
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException exc) {
				log.warn("Cerrando conexion a base de datos", exc);
			}

			conn = null;
		}
	}

	public synchronized void creaCarpeta(Usuario u, Fortimax fimx, String nombre, String desc) throws GestionException {

		Connection conn = null;

		try {
			conn = getConnection();

			Carpeta c = new Carpeta();

			c.setTituloAplicacion(fimx.getTituloAplicacion());
			c.setIdGabinete(fimx.getIdGabinete());
			c.setIdCarpeta(CarpetaManager.getNextIdCarpeta(conn, fimx.getTituloAplicacion(), fimx.getIdGabinete()));
			c.setNombreCarpeta(nombre);
			c.setNombreUsuario(u.getLogin());
			c.setBanderaRaiz("N");
			c.setFechaCreacion(new Timestamp(System.currentTimeMillis()));
			c.setFechaModificacion(new Timestamp(System.currentTimeMillis()));
			c.setNumeroAccesos(0);
			c.setNumeroCarpetas(0);
			c.setNumeroDocumentos(0);
			c.setDescripcion(desc);
			c.setPassword("-1");

			CarpetaManager.insert(conn, c);

			OrgCarpeta oc = new OrgCarpeta();

			oc.setTituloAplicacion(c.getTituloAplicacion());
			oc.setIdGabinete(c.getIdGabinete());
			oc.setIdCarpetaHija(c.getIdCarpeta());
			oc.setIdCarpetaPadre(fimx.getIdCarpeta());
			oc.setNombreHija(nombre);

			OrgCarpetaManager.insert(conn, oc);

			conn.commit();
		} catch (SQLException exc) {
			try {
				conn.rollback();
			} catch (SQLException ex) {
				log.warn("En rollback", ex);
			}

			log.error("No se logro crear la carpeta (" + nombre + ")", exc);
			throw new GestionException("No se logro crear la carpeta (" + nombre + ")", exc);
		}
	}

	public synchronized void creaDocumento(Usuario u, Fortimax fimx, String nombre, String desc, boolean isImgDoc)
			throws GestionException {

		Connection conn = null;

		try {
			conn = getConnection();

			Documento d = new Documento();

			d.setTituloAplicacion(fimx.getTituloAplicacion());
			d.setIdGabinete(fimx.getIdGabinete());
			d.setIdCarpetaPadre(fimx.getIdCarpeta());
			d.setIdDocumento(-1);
			d.setNombreDocumento(nombre);
			d.setNombreUsuario(u.getLogin());
			d.setDescripcion(desc);
			d.setNombreTipoDocto((isImgDoc ? "IMAX_FILE" : "EXTERNO"));

			DocumentoManager.insertDocumento(conn, d);

			conn.commit();
		} catch (SQLException | FortimaxException exc) {
			try {
				conn.rollback();
			} catch (SQLException ex) {
				log.warn("En rollback", ex);
			}

			log.error("No se logro crear la carpeta (" + nombre + ")", exc);
			throw new GestionException("No se logro crear la carpeta (" + nombre + ")", exc);
		}
	}

	public synchronized int creaExpediente(String u_login, Caso c) throws GestionException {

		int id_gabinete = -1;
		Connection conn = null;

		try {
			conn = getConnection();

			Aplicacion app = AplicacionManager.select(conn, c.getTipoCaso().getGavetaAsociada());
			id_gabinete = AplicacionManager.createExpediente(conn, u_login, c, app);

			if (id_gabinete < 0) {
				log.error("Identificador de Gabiente invalido (< 0)");
				throw new SQLException("Identificador de Gabiente invalido (< 0)");
			}

			c.setIdGabinete(id_gabinete);
			CasoManager.update(conn, c);

			conn.commit();
		} catch (SQLException exc) {
			try {
				conn.rollback();
			} catch (SQLException ex) {
				log.warn("En rollback", ex);
			}

			log.error("No se logro crear el Expediente del caso (" + c.getIdCaso() + ")", exc);
			throw new GestionException("No se logra crear el Expediente del caso (" + c.getIdCaso() + ")", exc);
		}

		return id_gabinete;
	}

	public synchronized Caso ejecutaCaso(int id_caso, int id_caso_oper, String u_nombre) throws GestionException {

		Caso rc = null;
		Connection conn = null;

		try {
			conn = getConnection();

			Caso srchCase = new Caso();
			srchCase.setIdCaso(id_caso);

			// Solo para Afirme
			//			if (CasoPendienteManager.casoPendienteActivo(conn, id_caso, id_caso_oper)) {
			//				CasoPendiente cp = CasoPendienteManager.getCasoPendienteActivo(conn, id_caso, id_caso_oper);
			//
			//				cp.setFechaTermino(new Timestamp(System.currentTimeMillis()));
			//
			//				CasoPendienteManager.update(conn, cp.getIdCaso(), cp.getIdCasoOper(), cp.getIdPendiente(), cp
			//						.getFechaInicio(), cp.getFechaTermino());
			//			}
			// Fin Solo para Afirme

			rc = CasoManager.select(conn, srchCase, id_caso_oper);

			CasoOperacion co = rc.getCasoOperacion(0);

			co.setResponsable(u_nombre);
			co.setStatus(co.getStatus() | CasoOperacion.EXECUTED);

			CasoOperacionManager.update(conn, co);

			rc.setCasoOperacion(new Vector());
			rc.setCasoOperacion(co);
			rc.setStatus(rc.getStatus() | Caso.EXECUTED);

			CasoManager.update(conn, rc);

			conn.commit();
		} catch (SQLException exc) {
			try {
				conn.rollback();
			} catch (SQLException ex) {
				log.warn("En rollback", ex);
			}

			log.error("No se logro ejecutar el caso (" + id_caso + ", " + id_caso_oper + ")", exc);
			throw new GestionException("No se logro ejecutar el caso (" + id_caso + ", " + id_caso_oper + ")", exc);
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException exc) {
				log.warn("Cerrando conexion a base de datos", exc);
			}

			conn = null;
		}

		return rc;
	}

	public Map getAllTipoCaso() throws GestionException {

		Connection conn = null;
		Map rm = new LinkedHashMap();

		try {
			conn = getConnection();

			rm = TipoCasoManager.selectAllTipoCasos(conn);
		} catch (SQLException exc) {
			log.error("Recuperando todos los Tipos de Caso", exc);
			throw new GestionException(exc);
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException exc) {
				log.warn("Error cerrando conexian a base de datos", exc);
			}

			conn = null;
		}

		return rm;
	}

	public Map getAllTipoCaso(String u_login) throws GestionException {

		Connection conn = null;
		Map rm = new LinkedHashMap();

		try {
			conn = getConnection();

			rm = TipoCasoManager.selectAllTipoCasos(conn, u_login);
		} catch (SQLException exc) {
			log.error("Recuperando Tipos de Caso por Usuario", exc);
			throw new GestionException(exc);
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException exc) {
				log.warn("Error cerrando conexian a base de datos", exc);
			}

			conn = null;
		}

		return rm;
	}

	public ITree getArbolCaso(Caso c) throws GestionException {

		Connection conn = null;
		ITree tree = new Tree();

		try {
			conn = getConnection();

			tree = TreeManager.getTree(conn, c);
		} catch (SQLException exc) {
			log.error("Creando el Arbol del Caso", exc);
			throw new GestionException(exc);
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException exc) {
				log.warn("Error cerrando conexian a base de datos", exc);
			}

			conn = null;
		}

		return tree;
	}

	public FortimaxFile[] getArchivosDeDocumento(String titApp, int idGab, int idCarp, int idDoc)
			throws GestionException {

		FortimaxFile[] files = new FortimaxFile[0];
		Connection conn = null;

		try {
			conn = getConnection();

			files = PaginaManager.getPaginasDeDocumento(conn, titApp, idGab, idCarp, idDoc);
		} catch (Exception exc) {
			log.error("Creando el Arbol del Caso", exc);
			throw new GestionException(exc);
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException exc) {
				log.warn("Error cerrando conexian a base de datos", exc);
			}

			conn = null;
		}

		return files;
	}

	public File[] getArchivosDocumento(String titApp, int idGab, int idCarp, int idDoc) throws GestionException {

		File[] files = new File[0];
		Connection conn = null;

		try {
			conn = getConnection();

			String[] filenames = PaginaManager.getPaginasDocumento(conn, titApp, idGab, idCarp, idDoc);

			files = new File[filenames.length];
			for (int i = 0; i < filenames.length; i++)
				files[i] = new File(filenames[i]);
		} catch (SQLException exc) {
			log.error("Creando el Arbol del Caso", exc);
			throw new GestionException(exc);
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException exc) {
				log.warn("Error cerrando conexian a base de datos", exc);
			}

			conn = null;
		}

		return files;
	}

	public Caso getCaso(int id_caso) throws GestionException {

		Connection conn = null;
		Caso rc = null;

		try {
			conn = getConnection();

			Caso sc = new Caso();
			sc.setIdCaso(id_caso);

			rc = CasoManager.select(conn, sc);
		} catch (SQLException exc) {
			log.error("Obteniendo Caso(" + id_caso + ")", exc);
			throw new GestionException(exc);
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException exc) {
				log.warn("Cerrando conexion a base de datos", exc);
			}

			conn = null;
		}

		return rc;
	}

	public Caso getCaso(int id_caso, int id_caso_operacion) throws GestionException {

		Connection conn = null;
		Caso rc = null;

		try {
			conn = getConnection();

			Caso sc = new Caso();
			sc.setIdCaso(id_caso);

			rc = CasoManager.select(conn, sc, id_caso_operacion);
		} catch (SQLException exc) {
			log.error("Obteniendo Caso(" + id_caso + "), Caso Operacion(" + id_caso_operacion + ")", exc);
			throw new GestionException(exc);
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException exc) {
				log.warn("Cerrando conexion a base de datos", exc);
			}

			conn = null;
		}

		return rc;
	}

	public String getFilenamePath(Caso c) throws GestionException {

		Connection conn = null;
		String filename = null;

		try {
			conn = getConnection();

			Documento d = DocumentoManager.getDocumento(conn, c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(),
					0, c.getFolio());

			if (d != null)
				filename = PaginaManager.getFilenamePath(conn, d.getTituloAplicacion(), d.getIdGabinete(), d
						.getIdCarpetaPadre(), d.getIdDocumento());
		} catch (SQLException | FortimaxException exc) {
			log.error("Obteniendo el Path del Archivo", exc);
			throw new GestionException(exc);
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException exc) {
				log.warn("Cerrando conexion a base de datos", exc);
			}
		}

		return filename;
	}

	public Map getGavetasFortimax() throws GestionException {

		Connection conn = null;
		Map rm = new LinkedHashMap();

		try {
			conn = getConnection();

			rm = AplicacionManager.select(conn);
		} catch (SQLException exc) {
			try {
				conn.rollback();
			} catch (SQLException ex) {
				log.warn("En rollback", ex);
			}

			log.error("Recuperando gavetas Fortimax", exc);
			throw new GestionException(exc);
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException exc) {
				log.warn("Cerrando conexion a base de datos", exc);
			}

			conn = null;
		}

		return rm;
	}

	public URLDocumento getIdNodeDocumento(Caso c, String doc_nombre) throws GestionException {

		Connection conn = null;
		URLDocumento urlDoc = null;

		try {
			conn = getConnection();

			Documento d = DocumentoManager.buscaDocumento(conn, c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(),
					doc_nombre);

			if (d == null)
				throw new GestionException("No se localizo el documento '" + doc_nombre + "'");

			urlDoc = new URLDocumento(d.getTituloAplicacion() + "_G" + d.getIdGabinete() + "C" + d.getIdCarpetaPadre()
					+ "D" + d.getIdDocumento(), "IMAX_FILE".equals(d.getNombreTipoDocto()));
		} catch (SQLException exc) {
			try {
				conn.rollback();
			} catch (SQLException ex) {
				log.warn("En rollback", ex);
			}

			log.error("Recuperando documento por nombre '" + doc_nombre + "' del caso " + c.getIdCaso(), exc);
			throw new GestionException(exc);
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException exc) {
				log.warn("Cerrando conexion a base de datos", exc);
			}

			conn = null;
		}

		return urlDoc;
	}

	//public synchronized Caso IniciaCaso(String u_nombre, int id_tc, FolioGeneratorInterface folioGenerator)
	public synchronized Caso IniciaCaso(Usuario u, int id_tc, FolioGeneratorInterface folioGenerator)
			throws GestionException {

		Caso rc = null;
		Connection conn = null;

		try {
			conn = getConnection();

			rc = CasoManager.nuevoCaso(conn, u, id_tc, folioGenerator);

			conn.commit();
		} catch (SQLException exc) {
			try {
				conn.rollback();
			} catch (SQLException ex) {
				log.warn("En rollback", ex);
			}

			log.error("No se logro crear caso (" + id_tc + ")", exc);
			throw new GestionException("No se logro crear caso (" + id_tc + ")", exc);
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException exc) {
				log.warn("Cerrando conexion a base de datos", exc);
			}

			conn = null;
		}

		return rc;
	}

	public synchronized void recibeDocumentoGestion(Caso c, 
													DataInputStream in,
													String OCRProgramPath, 
												    String OCRParameter1, 
												    String OCRParameter2, 
												    String luceneDbPath, 
												    String luceneStopwordsPath, 
												    int luceneMergeFactor, 
												    int luceneMaxMergeDocs ) throws GestionException {

		recibeDocumentoGestion(c, 
							   0, 
							   c.getFolio(), 
							   "xml", 
							   in, 
							   false,
							   OCRProgramPath, 
							   OCRParameter1, 
							   OCRParameter2, 
							   luceneDbPath, 
							   luceneStopwordsPath, 
							   luceneMergeFactor, 
							   luceneMaxMergeDocs);
	}

	public synchronized void recibeDocumentoGestion(Caso c, 
													int id_carpeta, 
													String nombreDocumento, 
													String ext,
													DataInputStream in, 
													boolean addPage,
													String OCRProgramPath, 
												    String OCRParameter1, 
												    String OCRParameter2, 
												    String luceneDbPath, 
												    String luceneStopwordsPath, 
												    int luceneMergeFactor, 
												    int luceneMaxMergeDocs ) 
							throws GestionException {

		Connection conn = null;
		FileOutputStream fos = null;

		try {
			conn = getConnection();

			VolumenManager vm = new VolumenManager();

			Volumen vol = vm.getVolumen(conn);

			Documento d = DocumentoManager.getDocumento(conn, c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(),
					id_carpeta, nombreDocumento);

			if (d == null) {

				d = new Documento();

				d.setTituloAplicacion(c.getTipoCaso().getGavetaAsociada());
				d.setIdGabinete(c.getIdGabinete());
				d.setIdCarpetaPadre(CarpetaManager.getIdCarpetaPadre(conn, d.getTituloAplicacion(), d.getIdGabinete()));
				// FIXME Se debe identificar el tipo de documento
				if ("imx".equals(ext))
					d.setNombreTipoDocto("IMAX_FILE");
				else
					d.setNombreTipoDocto("EXTERNO");
				d.setNombreDocumento(nombreDocumento);
				d.setNombreUsuario(c.getCasoOperacion(0).getResponsable());
				d.setExtension(ext);

				DocumentoManager.insertDocumento(conn, d);

				DocumentoManager.insertPaginaDocumento(conn, 
													   vol, 
													   c, 
													   d, 
													   "A", 
													   0,
													   OCRProgramPath, 
													   OCRParameter1, 
													   OCRParameter2, 
													   luceneDbPath, 
													   luceneStopwordsPath, 
													   luceneMergeFactor, 
													   luceneMaxMergeDocs);
				// Lee nuevamente el documento para actualizar las paginas
				d = DocumentoManager.getDocumento(conn, c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(),
						id_carpeta, nombreDocumento);
			}

			String filename = PaginaManager.getFilenamePath(conn, d.getTituloAplicacion(), d.getIdGabinete(), d
					.getIdCarpetaPadre(), d.getIdDocumento());

			if ((d.getExtension() == null) || ("".equals(d.getExtension())))
				d.setExtension(ext);

			// FIXME Hay que buscar el documento y despues la pagina
			if ((filename == null) || addPage) {
				DocumentoManager.insertPaginaDocumento(conn, 
													   vol, 
													   c, 
													   d, 
													   "A", 
													   0,
													   OCRProgramPath, 
													   OCRParameter1, 
													   OCRParameter2, 
													   luceneDbPath, 
													   luceneStopwordsPath, 
													   luceneMergeFactor, 
													   luceneMaxMergeDocs);
				// Lee nuevamente el documento para actualizar las paginas
				d = DocumentoManager.getDocumento(conn, c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(),
						id_carpeta, nombreDocumento);
				filename = d.getFullPathFilesNames()[d.getFullPathFilesNames().length - 1];
			}

			fos = new FileOutputStream(filename);

			int fileLength = 0, length = 0;
			byte[] buffer = new byte[4 * 1024];
			while ((in != null) && ((length = in.read(buffer)) != -1)) {
				fos.write(buffer, 0, length);
				fileLength += length;
			}

			String lowerFilename = nombreDocumento.toLowerCase() + ".xml";
			String filenames[] = d.getFilesNames();
			for (int i = 0; i < filenames.length; i++) {
				if (lowerFilename.equals(filenames[i].toLowerCase())) {

					Pagina p = d.getPaginaDocumento(i);
					p.setTamanoBytes(fileLength);

					PaginaManager.updatePagina(conn, p);
					break;
				}
			}

			conn.commit();
		} catch (SQLException exc) {
			try {
				conn.rollback();
			} catch (SQLException ex) {
				log.warn("En rollback", ex);
			}

			log.error("Actualizando Documento Gestion", exc);
			throw new GestionException(exc);
		} catch (IOException exc) {
			try {
				conn.rollback();
			} catch (SQLException ex) {
				log.warn("En rollback", ex);
			}

			log.error("Actualizando Documento Gestion", exc);
			throw new GestionException(exc);
		} catch (Exception exc) {
			try {
				conn.rollback();
			} catch (SQLException ex) {
				log.warn("En rollback", ex);
			}

			log.error("General", exc);
			throw new GestionException(exc);
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException exc) {
				log.warn("Cerrando conexion  a base de datos", exc);
			}

			try {
				if (fos != null)
					fos.close();

				if (in != null)
					in.close();
			} catch (IOException exc) {
				log.warn("Cerrando archivo " + c.getFolio() + ".xml", exc);
			}

			fos = null;
			in = null;
			conn = null;
		}
	}

	public void revisaTiempoLimiteDeCasos(String prefixPath) throws GestionException {

		Connection conn = null;

		try {
			conn = getConnection();

			CasoManager.revisaTiempoLimiteDeCasos(conn, prefixPath);

			conn.commit();
		} catch (IOException exc) {
			try {
				conn.rollback();
			} catch (SQLException ex) {
				log.warn("En rollback", ex);
			}

			log.error("Revisando Tiempo Limite de Casos", exc);
			throw new GestionException(exc);
		} catch (SQLException exc) {
			try {
				conn.rollback();
			} catch (SQLException ex) {
				log.warn("En rollback", ex);
			}

			log.error("Revisando Tiempo Limite de Casos", exc);
			throw new GestionException(exc);
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException exc) {
				log.warn("Cerrando conexion a base de datos", exc);
			}

			conn = null;
		}
	}

	public List validaCasoDatos(Caso c, Map data) throws GestionException {

		List errList = new ArrayList();
		Connection conn = null;

		try {
			conn = getConnection();

			Aplicacion app = AplicacionManager.select(conn, c.getTipoCaso().getGavetaAsociada());

			for (Iterator iter = c.getCasoDato().keySet().iterator(); iter.hasNext();) {
				String name = (String) iter.next();
				CasoDato cd = c.getCasoDato(name);

				if (!cd.getTipoCasoVariable().isEnGaveta())
					continue;

				String value = (String) data.get(name);
				Descripcion d = app.getCamposDescripcion(name);

				if (d.isRequerido() && "".equals(value))
					errList.add("La variable del caso \"" + name + "\" es requerida");
			}
		}catch(Exception exc){
		//	log.error(exc);
		//} catch (SQLException exc) {
			log.error("Validando Caso Datos", exc);
			throw new GestionException(exc);
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException exc) {
				log.warn("Cerrando conexion a base de datos", exc);
			}

			conn = null;
		}

		return errList;
	}
}
