package com.syc.sai.interfaces;


import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;
import javax.xml.bind.DatatypeConverter;

import org.apache.log4j.Logger;

import com.syc.cfdi.core.FacturaManager;
import com.syc.cfdi.security.TripleDesEncryption;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.gestion.CasoBusinessLogic;
import com.syc.gestion.CasoOperacionBusinessLogic;
import com.syc.gestion.core.AlarmaManager;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoOperacion;
import com.syc.gestion.core.GestionException;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.custom.FolioGeneratorInterface;
import com.syc.sai.contabilidad.utils.db.CloseObject;


public class CFDIBusinessLogic extends DataSourceManager {

	private static final Logger	log				= Logger.getLogger( CFDIBusinessLogic.class );
	private X509Certificate		cert;
	private String				certString;
	private String				concepto		= null;
	private String				descuento		= null;
	private String				domicilioFiscal	= null;
	private String				FechaEmision	= null;
	private String				formaPago		= null;
	private String				impuesto		= null;
	private String				jniName			= "";
	private PrivateKey			key;
	private String				keyPwd;
	private String				maternoCliente	= null;
	private String				metodoPago		= null;
	private String				nCuenta			= null;
	private int					nFolioCFDI		= -1;
	private String				nombreCliente	= null;
	private String				paternoCliente	= null;
	private String				RFCCliente		= null;
	private String				subTotal		= null;
	private String				tipoComprobante	= null;
	private String				total			= null;

	public CFDIBusinessLogic( String jniName ) {
		super.init( jniName );
		this.jniName = jniName;
	}

	public CFDIBusinessLogic( ) {
	}

	public String desencriptaInfo( String cadenaEnc ) throws Exception {
		String[] b64Array = cadenaEnc.split( ":=" );

		if ( b64Array.length < 2 ) {
			throw new Exception( "Cadena de Token mal Formada. Sin datos" );
		}

		byte[] bKey = DatatypeConverter.parseBase64Binary( b64Array[0] );
		byte[] bData = DatatypeConverter.parseBase64Binary( b64Array[1] );

		SecretKey key = TripleDesEncryption.generateKey( bKey );
		byte[] clearData = TripleDesEncryption.decrypt( key, bData );
		return new String( clearData );
	}

	public Caso generaCaso( Usuario u, int idTCaso, FolioGeneratorInterface fg, String opResponsable ) throws GestionException {
		CasoBusinessLogic casoTx = new CasoBusinessLogic( jniName );
		Caso c = casoTx.IniciaCaso( u, idTCaso, fg );

		Date date = Calendar.getInstance().getTime();
		SimpleDateFormat sdf = new SimpleDateFormat( "dd/MM/yyyy" );
		String fecha = sdf.format( date );
		// Variables del caso
		c.getCasoDato( "FOLIO" ).setValor( c.getFolio() );
		c.getCasoDato( "OPERADOR" ).setValor( u.getLogin() );
		c.getCasoDato( "FECHA_DOCUMENTO" ).setValor( fecha );
		try {
			c.getCasoDato( "EJERCICIO_FISCAL" ).setValor( obtieneEjecicioFiscal() );
		} catch ( Exception e ) {
			e.printStackTrace();
		}

		Map<String, String> m = new HashMap<String, String>();
		m.put( "FOLIO", c.getFolio() );
		m.put( "OPERADOR", u.getLogin() );
		m.put( "FECHA_DOCUMENTO", fecha );
		try {
			m.put( "EJERCICIO_FISCAL", obtieneEjecicioFiscal() );
		} catch ( Exception e ) {
			e.printStackTrace();
		}

		c.setIdGabinete( casoTx.creaExpediente( u.getLogin(), c ) );

		// Guarda las variables de caso.
		c = casoTx.actualizaCasoDato( c, m );

		// Cambia el usuario al grupo ventanilla para que aparezca en el inbox.
		CasoOperacion co = ( ( CasoOperacion ) c.getCasoOperacion().get( 0 ) );
		CasoOperacionBusinessLogic cobl = new CasoOperacionBusinessLogic( jniName );
		cobl.updateCasoResponsable( co, opResponsable );
		return c;
	}

	public X509Certificate getCert() {
		return cert;
	}

	public String getCertString() {
		return certString;
	}

	/**
	 * @return the concepto
	 */
	public String getConcepto() {
		return concepto;
	}

	/**
	 * @return the descuento
	 */
	public String getDescuento() {
		return descuento;
	}

	/**
	 * @return the domicilioFiscal
	 */
	public String getDomicilioFiscal() {
		return domicilioFiscal;
	}

	/**
	 * @return the fechaEmision
	 */
	public String getFechaEmision() {
		return FechaEmision;
	}

	/**
	 * @return the formaPago
	 */
	public String getFormaPago() {
		return formaPago;
	}

	/**
	 * @return the impuesto
	 */
	public String getImpuesto() {
		return impuesto;
	}

	public PrivateKey getKey() {
		return key;
	}

	public String getKeyPwd() {
		return keyPwd;
	}

	/**
	 * @return the maternoCliente
	 */
	public String getMaternoCliente() {
		return maternoCliente;
	}

	/**
	 * @return the metodoPago
	 */
	public String getMetodoPago() {
		return metodoPago;
	}

	/**
	 * @return the nCuenta
	 */
	public String getnCuenta() {
		return nCuenta;
	}

	/**
	 * @return the nFolioCFDI
	 */
	public int getnFolioCFDI() {
		return nFolioCFDI;
	}

	/**
	 * @return the nombreCliente
	 */
	public String getNombreCliente() {
		return nombreCliente;
	}

	/**
	 * @return the paternoCliente
	 */
	public String getPaternoCliente() {
		return paternoCliente;
	}

	/**
	 * @return the rFCCliente
	 */
	public String getRFCCliente() {
		return RFCCliente;
	}

	/**
	 * @return the subTotal
	 */
	public String getSubTotal() {
		return subTotal;
	}

	/**
	 * @return the tipoComprobante
	 */
	public String getTipoComprobante() {
		return tipoComprobante;
	}

	/**
	 * @return the total
	 */
	public String getTotal() {
		return total;
	}

	public void insertaInformacion( Usuario u, Caso c ) throws Exception {
		Connection conn = null;
		try {
			conn = getConnection();
			CFDIManager.insert( conn, u, this );
			try {
				AlarmaManager.procesaAlarmaCNF( conn, null, c.getCasoOperacion( 0 ), c, "[MENSAJE DE PRUEBA]Validacion de informacion desde CUSTF en espera", "bsanchezr@conafor.gob.mx", "Se le notifica que se ha capturado una solicitud desde CUSTF.<br> Para revisarla ingrese al sistema y seleccione el folio <b> " + c.getFolio() + "</b>" );
			} catch ( Exception e ) {
				log.warn( "No se pudo enviar correo" );
			}
			conn.commit();

		} catch ( Exception e ) {
			try {
				if ( conn != null )
					conn.rollback();
			} catch ( Exception e2 ) {
				log.warn( e2 );
			}
			throw e;
		} finally {
			CloseObject.closeObject( conn );
		}
	}

	public void notificaREPFaltantes( Usuario u, String unidadEjecutora, String rFCBeneficiario ) throws Exception {
		Connection conn = null;
		try {
			conn = getConnection();
			notificaREPFaltantes( conn, u, unidadEjecutora, rFCBeneficiario );
		} finally {
			CloseObject.closeObject( conn );
		}
	}

	public void notificaREPFaltantes( Connection conn, Usuario u, String unidadEjecutora, String rFCBeneficiario ) throws Exception {
		String notificaionPara = CFDIManager.getNotificacionREPCorreo( conn, rFCBeneficiario );
		String cuerpoCorreo = CFDIManager.generaHTMLNotificaREPFaltante( conn, u, unidadEjecutora, rFCBeneficiario );
		AlarmaManager.procesaAlarmaCNF( conn,null, null, null,  "Solicitud de Comprobante de Pago CONAFOR", notificaionPara,"","", cuerpoCorreo );
	}

	public String obtieneEjecicioFiscal() throws SQLException {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String EjercicioFiscal = "";
		try {

			conn = getConnection();

			String queryEf = "select aEjercicioFiscal from tEjercicioFiscal where cActivo = 1 ";
			ps = conn.prepareStatement( queryEf );
			rs = ps.executeQuery();

			if ( rs.next() ) {
				EjercicioFiscal = rs.getString( "aEjercicioFiscal" );
			}

		} catch ( SQLException e ) {
			e.printStackTrace();

		} finally {
			if ( conn != null ) {
				conn.close();
			}
			if ( ps != null ) {
				ps.close();
			}
			if ( rs != null ) {
				rs.close();
			}

		}

		return EjercicioFiscal;
	}

	public void setCert( X509Certificate cert ) {
		this.cert = cert;
	}

	public void setCertString( String certString ) {
		this.certString = certString;
	}

	/**
	 * @param concepto
	 *            the concepto to set
	 */
	public void setConcepto( String concepto ) {
		this.concepto = concepto;
	}

	/**
	 * @param descuento
	 *            the descuento to set
	 */
	public void setDescuento( String descuento ) {
		this.descuento = descuento;
	}

	/**
	 * @param domicilioFiscal
	 *            the domicilioFiscal to set
	 */
	public void setDomicilioFiscal( String domicilioFiscal ) {
		this.domicilioFiscal = domicilioFiscal;
	}

	/**
	 * @param fechaEmision
	 *            the fechaEmision to set
	 */
	public void setFechaEmision( String fechaEmision ) {
		FechaEmision = fechaEmision;
	}

	/**
	 * @param formaPago
	 *            the formaPago to set
	 */
	public void setFormaPago( String formaPago ) {
		this.formaPago = formaPago;
	}

	/**
	 * @param impuesto
	 *            the impuesto to set
	 */
	public void setImpuesto( String impuesto ) {
		this.impuesto = impuesto;
	}

	public void setKey( PrivateKey key ) {
		this.key = key;
	}

	public void setKeyPwd( String keyPwd ) {
		this.keyPwd = keyPwd;
	}

	/**
	 * @param maternoCliente
	 *            the maternoCliente to set
	 */
	public void setMaternoCliente( String maternoCliente ) {
		this.maternoCliente = maternoCliente;
	}

	/**
	 * @param metodoPago
	 *            the metodoPago to set
	 */
	public void setMetodoPago( String metodoPago ) {
		this.metodoPago = metodoPago;
	}

	/**
	 * @param nCuenta
	 *            the nCuenta to set
	 */
	public void setnCuenta( String nCuenta ) {
		this.nCuenta = nCuenta;
	}

	/**
	 * @param nFolioCFDI
	 *            the nFolioCFDI to set
	 */
	public void setnFolioCFDI( int nFolioCFDI ) {
		this.nFolioCFDI = nFolioCFDI;
	}

	/**
	 * @param nombreCliente
	 *            the nombreCliente to set
	 */
	public void setNombreCliente( String nombreCliente ) {
		this.nombreCliente = nombreCliente;
	}

	/**
	 * @param paternoCliente
	 *            the paternoCliente to set
	 */
	public void setPaternoCliente( String paternoCliente ) {
		this.paternoCliente = paternoCliente;
	}

	/**
	 * @param rFCCliente
	 *            the rFCCliente to set
	 */
	public void setRFCCliente( String rFCCliente ) {
		RFCCliente = rFCCliente;
	}

	/**
	 * @param subTotal
	 *            the subTotal to set
	 */
	public void setSubTotal( String subTotal ) {
		this.subTotal = subTotal;
	}

	/**
	 * @param tipoComprobante
	 *            the tipoComprobante to set
	 */
	public void setTipoComprobante( String tipoComprobante ) {
		this.tipoComprobante = tipoComprobante;
	}

	/**
	 * @param total
	 *            the total to set
	 */
	public void setTotal( String total ) throws Exception {
		this.total = total;
	}

	public boolean existeFirma() throws SQLException {
		Connection conn = null;
		try {
			conn = getConnection();
			return FacturaManager.existeFirma( conn );
		} finally {
			CloseObject.closeObject( conn );
		}
	}

	public void guardaFirma( String nombreArchivoCert, String nombreArchivoKey, String password ) throws Exception {
		Connection conn = null;
		try {
			conn = getConnection();
			FacturaManager.guardaFirma( conn, nombreArchivoCert, nombreArchivoKey, password );
			conn.commit();
		} catch ( Exception e ) {
			if ( conn != null )
				try {
					conn.rollback();
				} catch ( Exception e2 ) {
					log.warn( e2 );
				}
			throw e;
		} finally {
			CloseObject.closeObject( conn );
		}
	}

}
