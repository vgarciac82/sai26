package com.syc.cfdi.v3332.Comprobante;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.xerces.dom.ElementNSImpl;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.axtel.cfdi.ComplementoCombustible.Bonificacion;
import com.axtel.cfdi.ComplementoCombustible.CargoECC;
import com.axtel.cfdi.ComplementoCombustible.custom.AdendaECC;
import com.axtel.cfdi.ComplementoCombustible.vales.Addenda;
import com.axtel.cfdi.ComplementoCombustible.vales.AddendaEfectivale;
import com.syc.sai.interfaces.CFDIInterface;

import mx.grupocorasa.sat.cfd._33.Comprobante.CfdiRelacionados.CfdiRelacionado;
import mx.grupocorasa.sat.cfd._33.Comprobante.Complemento;
import mx.grupocorasa.sat.cfd._33.Comprobante.Conceptos.Concepto;
import mx.grupocorasa.sat.cfd._40.Comprobante.CfdiRelacionados;
import mx.grupocorasa.sat.cfdi.v3.CFDv33;
import mx.grupocorasa.sat.cfdi.v4.CFDv40;
import mx.grupocorasa.sat.common.EstadoDeCuentaCombustible12.EstadoDeCuentaCombustible;
import mx.grupocorasa.sat.common.EstadoDeCuentaCombustible12.EstadoDeCuentaCombustible.Conceptos.ConceptoEstadoDeCuentaCombustible;
import mx.grupocorasa.sat.common.EstadoDeCuentaCombustible12.EstadoDeCuentaCombustible.Conceptos.ConceptoEstadoDeCuentaCombustible.Traslados.Traslado;
import mx.grupocorasa.sat.common.Pagos10.Pagos;
import mx.grupocorasa.sat.common.TimbreFiscalDigital11.TimbreFiscalDigital;


public class Comprobante {

	public static DateTimeFormatter					formatter				= DateTimeFormatter.ISO_DATE_TIME;
	private static final Logger						log						= Logger.getLogger( Comprobante.class );
	/**
	 * Adenda de estado de cuenta de combustible.
	 */
	private AdendaECC								adendaECC;
	/**
	 * Bonificacion registrada en un ECC
	 */
	private Bonificacion							bonificacion			= new Bonificacion();

	private boolean									cfd33					= false;
	private boolean									cfd40					= false;
	/**
	 * Indica si es un comprobante con complemento de combustible.
	 */
	private boolean									complementoCombustible	= false;
	private mx.grupocorasa.sat.cfd._32.Comprobante	comprobante32;
	private mx.grupocorasa.sat.cfd._33.Comprobante	comprobante33;
	private mx.grupocorasa.sat.cfd._40.Comprobante	comprobante40;
	private Conceptos								conceptos;
	private EstadoDeCuentaCombustible				estadoDeCuentaCombustible;
	private List<?>									pagos;
	/*
	 * VGC20231115 Se agrega lectura de adenda de vales de combustible.
	 */
	private boolean									facturaVales;
	private AddendaEfectivale						adenda;

	public Comprobante( Object comprobante ) throws Exception {

		if ( comprobante instanceof mx.grupocorasa.sat.cfd._32.Comprobante ) {
			setComprobante32( ( mx.grupocorasa.sat.cfd._32.Comprobante ) comprobante );
		} else if ( comprobante instanceof mx.grupocorasa.sat.cfd._33.Comprobante ) {
			setCfd33( true );
			setComprobante33( ( mx.grupocorasa.sat.cfd._33.Comprobante ) comprobante );
		} else if ( comprobante instanceof mx.grupocorasa.sat.cfd._40.Comprobante ) {
			setCfd40( true );
			setComprobante40( ( mx.grupocorasa.sat.cfd._40.Comprobante ) comprobante );

		} else
			throw new Exception( "El objeto recibido no corresponde con un CFD de version 3.2, 3.3 o 4.0" );

	}

	public double calculaImpuestosTrasladados() {
		double montoIVA = 0;
		if ( isCfd40() ) {
			if ( getComprobante40().getImpuestos() != null ) {
				mx.grupocorasa.sat.cfd._40.Comprobante.Impuestos.Traslados traslados = getComprobante40().getImpuestos().getTraslados();
				if ( traslados != null )
					for ( Iterator<mx.grupocorasa.sat.cfd._40.Comprobante.Impuestos.Traslados.Traslado> j = traslados.getTraslado().iterator(); j.hasNext(); ) {
						mx.grupocorasa.sat.cfd._40.Comprobante.Impuestos.Traslados.Traslado t = j.next();
						if ( "iva".equalsIgnoreCase( t.getImpuesto().value() ) || "002".equalsIgnoreCase( t.getImpuesto().value() ) )
							montoIVA += ( t.getImporte() == null ? 0.00d : t.getImporte().doubleValue() );
					}
			}
		} else if ( isCfd33() ) {
			if ( getComprobante33().getImpuestos() != null ) {
				mx.grupocorasa.sat.cfd._33.Comprobante.Impuestos.Traslados traslados = getComprobante33().getImpuestos().getTraslados();
				if ( traslados != null )
					for ( Iterator<mx.grupocorasa.sat.cfd._33.Comprobante.Impuestos.Traslados.Traslado> j = traslados.getTraslado().iterator(); j.hasNext(); ) {
						mx.grupocorasa.sat.cfd._33.Comprobante.Impuestos.Traslados.Traslado t = j.next();
						if ( "iva".equalsIgnoreCase( t.getImpuesto().value() ) || "002".equalsIgnoreCase( t.getImpuesto().value() ) )
							montoIVA += t.getImporte().doubleValue();
					}
			}
		} else {
			mx.grupocorasa.sat.cfd._32.Comprobante.Impuestos.Traslados traslados = getComprobante32().getImpuestos().getTraslados();
			if ( traslados != null )
				for ( Iterator<mx.grupocorasa.sat.cfd._32.Comprobante.Impuestos.Traslados.Traslado> j = traslados.getTraslado().iterator(); j.hasNext(); ) {
					mx.grupocorasa.sat.cfd._32.Comprobante.Impuestos.Traslados.Traslado t = j.next();
					if ( "iva".equalsIgnoreCase( t.getImpuesto() ) || "002".equalsIgnoreCase( t.getImpuesto() ) )
						montoIVA += t.getImporte().doubleValue();
				}
		}
		return montoIVA;
	}

	/**
	 * Determina si el CFDI contiene complemento de combustible.
	 * 
	 * @param comp
	 *            Comprobante CFDI 3.3
	 * @return true si y solo si se encuentra el complemento de estado de cuenta
	 *         de combustible.
	 */
	public boolean esCFDICombustible() {
		if ( isCfd40() ) {

			boolean esCFDICombustible = false;

			List<?> complementos = getComprobante40().getComplemento().getAny();

			if ( complementos != null && complementos.size() > 0 )
				for ( Object complementoObj : complementos ) {

					if ( complementoObj instanceof EstadoDeCuentaCombustible ) {
						esCFDICombustible = true;
						break;
					}

					if ( esCFDICombustible )
						break;
				}
			return esCFDICombustible;
		} else {
			boolean esCFDICombustible = false;

			List<Complemento> complementos = getComprobante33().getComplemento();

			if ( complementos != null && complementos.size() > 0 )
				for ( Complemento complemento : complementos ) {

					for ( int counter = 0; counter < complemento.getAny().size(); counter++ ) {
						Object o = complemento.getAny().get( counter );

						if ( o instanceof EstadoDeCuentaCombustible ) {
							esCFDICombustible = true;
							break;
						}

					}
					if ( esCFDICombustible )
						break;
				}

			return esCFDICombustible;
		}
	}

	public boolean esCFDIValesCombustible( Comprobante comp ) {
		if ( isCfd33() ) {
			mx.grupocorasa.sat.cfd._33.Comprobante c = getComprobante33();

			mx.grupocorasa.sat.cfd._33.Comprobante.Conceptos conceptos = c.getConceptos();
			for ( Iterator<Concepto> itConcepto = conceptos.getConcepto().iterator(); itConcepto.hasNext(); ) {
				Concepto cncpto = itConcepto.next();
				if ( CFDIInterface.CLAVE_PROD_GASOLINA.equalsIgnoreCase( cncpto.getClaveProdServ() ) && CFDIInterface.RFC_PROVEEDOR_GASOLINA.equals( comp.getRFCEmisor() ) && CFDIInterface.DESC_SERVICIO_VALES.equalsIgnoreCase( cncpto.getDescripcion() ) )
					return true;

			}

			return false;
		} else {
			mx.grupocorasa.sat.cfd._40.Comprobante c = getComprobante40();

			mx.grupocorasa.sat.cfd._40.Comprobante.Conceptos conceptos = c.getConceptos();
			for ( Iterator<mx.grupocorasa.sat.cfd._40.Comprobante.Conceptos.Concepto> itConcepto = conceptos.getConcepto().iterator(); itConcepto.hasNext(); ) {
				mx.grupocorasa.sat.cfd._40.Comprobante.Conceptos.Concepto cncpto = itConcepto.next();
				if ( CFDIInterface.CLAVE_PROD_GASOLINA.equalsIgnoreCase( cncpto.getClaveProdServ() ) && CFDIInterface.RFC_PROVEEDOR_GASOLINA.equals( comp.getRFCEmisor() ) && CFDIInterface.DESC_SERVICIO_VALES.equalsIgnoreCase( cncpto.getDescripcion() ) )
					return true;

			}

			return false;
		}
	}

	/**
	 * Devuelve la adenda de estado de cuenta de combustible
	 * 
	 * @return null si el CFDI no cuenta con adenda o la adenda.
	 */
	public AdendaECC getAdendaECC() {
		return this.adendaECC;
	}

	/**
	 * @return the bonificacion
	 */
	public Bonificacion getBonificacion() {
		return bonificacion;
	}

	public String getCertificadoSAT() {
		List<?> l = null;
		String certificadoSAT = null;

		if ( isCfd40() )
			l = ( List<?> ) getComprobante40().getComplemento().getAny();
		else if ( isCfd33() )
			l = getComprobante33().getComplemento();
		else
			l = getComprobante32().getComplemento().getAny();

		if ( l != null ) {
			for ( Iterator<?> k = l.iterator(); k.hasNext(); ) {
				Object o = k.next();

				if ( o instanceof mx.grupocorasa.sat.common.TimbreFiscalDigital10.TimbreFiscalDigital )
					certificadoSAT = ( ( mx.grupocorasa.sat.common.TimbreFiscalDigital10.TimbreFiscalDigital ) o ).getNoCertificadoSAT();
				else if ( o instanceof mx.grupocorasa.sat.common.TimbreFiscalDigital11.TimbreFiscalDigital )
					certificadoSAT = ( ( mx.grupocorasa.sat.common.TimbreFiscalDigital11.TimbreFiscalDigital ) o ).getNoCertificadoSAT();

			}
		}
		return certificadoSAT;
	}

	/**
	 * @return the comprobante32
	 */
	public mx.grupocorasa.sat.cfd._32.Comprobante getComprobante32() {
		return comprobante32;
	}

	/**
	 * @return the comprobante33
	 */
	public mx.grupocorasa.sat.cfd._33.Comprobante getComprobante33() {
		return comprobante33;
	}

	/**
	 * @return the comprobante40
	 */
	public mx.grupocorasa.sat.cfd._40.Comprobante getComprobante40() {
		return comprobante40;
	}

	public Conceptos getConceptos() {
		return this.conceptos;
	}

	public BigDecimal getDescuento() {
		if ( isCfd40() )
			return getComprobante40().getDescuento();
		else if ( isCfd33() )
			return getComprobante33().getDescuento();
		else
			return getComprobante32().getDescuento();
	}

	/**
	 * @return the estadoDeCuentaCombustible
	 */

	public EstadoDeCuentaCombustible getEstadoDeCuentaCombustible() {
		return this.estadoDeCuentaCombustible;
	}

	public long getFechaCertificadoMillis() {

		LocalDateTime fechaCertificado = null;

		List<?> l = null;
		String uuid = null;

		if ( isCfd40() ) {
			l = ( List<?> ) getComprobante40().getComplemento().getAny();
			if ( l != null )
				for ( Iterator<?> k = l.iterator(); k.hasNext(); ) {
					Object o = k.next();
					if ( o instanceof mx.grupocorasa.sat.common.TimbreFiscalDigital10.TimbreFiscalDigital ) {
						fechaCertificado = ( ( mx.grupocorasa.sat.common.TimbreFiscalDigital10.TimbreFiscalDigital ) o ).getFechaTimbrado();
						break;
					} else if ( o instanceof mx.grupocorasa.sat.common.TimbreFiscalDigital11.TimbreFiscalDigital ) {
						fechaCertificado = ( ( mx.grupocorasa.sat.common.TimbreFiscalDigital11.TimbreFiscalDigital ) o ).getFechaTimbrado();
						break;
					}

					if ( !StringUtils.isBlank( uuid ) )
						break;
				}
		} else if ( isCfd33() ) {
			l = getComprobante33().getComplemento();
			if ( l != null )
				for ( Iterator<?> k = l.iterator(); k.hasNext(); ) {
					Object o = k.next();
					List<Object> complementos = ( ( mx.grupocorasa.sat.cfd._33.Comprobante.Complemento ) o ).getAny();
					for ( Iterator<?> compIterator = complementos.iterator(); compIterator.hasNext(); ) {
						Object complemento = compIterator.next();
						if ( complemento instanceof mx.grupocorasa.sat.common.TimbreFiscalDigital10.TimbreFiscalDigital ) {
							fechaCertificado = ( ( mx.grupocorasa.sat.common.TimbreFiscalDigital10.TimbreFiscalDigital ) complemento ).getFechaTimbrado();
							break;
						} else if ( complemento instanceof mx.grupocorasa.sat.common.TimbreFiscalDigital11.TimbreFiscalDigital ) {
							fechaCertificado = ( ( mx.grupocorasa.sat.common.TimbreFiscalDigital11.TimbreFiscalDigital ) complemento ).getFechaTimbrado();
							break;
						}
					}

					if ( !StringUtils.isBlank( uuid ) )
						break;
				}
		}

		return fechaCertificado.atZone( ZoneId.of( "America/Mexico_City" ) ).toInstant().toEpochMilli();

	}

	public LocalDateTime getFechaExpedicion() {

		LocalDateTime fechaExpedicion = null;
		if ( isCfd40() ) {
			fechaExpedicion = getComprobante40().getFecha();
		} else if ( isCfd33() ) {
			fechaExpedicion = getComprobante33().getFecha();
		}

		return fechaExpedicion;

	}

	public long getFechaExpedicionMillis() {

		LocalDateTime fechaExpedicion = null;
		if ( isCfd40() ) {
			fechaExpedicion = getComprobante40().getFecha();
		} else if ( isCfd33() ) {
			fechaExpedicion = getComprobante33().getFecha();
		} else {
			fechaExpedicion = getComprobante32().getFecha();
		}

		return fechaExpedicion.atZone( ZoneId.of( "America/Mexico_City" ) ).toInstant().toEpochMilli();

	}

	public String getFechaTimbrado() {

		List<?> l = null;
		String fechaTimbrado = null;

		if ( isCfd40() )
			l = ( List<?> ) getComprobante40().getComplemento();
		else if ( isCfd33() )
			l = getComprobante33().getComplemento();
		else
			l = getComprobante32().getComplemento().getAny();

		if ( l != null ) {
			for ( Iterator<?> k = l.iterator(); k.hasNext(); ) {
				Object o = k.next();
				List<Object> complementos = ( ( mx.grupocorasa.sat.cfd._33.Comprobante.Complemento ) o ).getAny();
				for ( Iterator<?> compIterator = complementos.iterator(); compIterator.hasNext(); ) {
					Object complemento = compIterator.next();
					if ( complemento instanceof mx.grupocorasa.sat.common.TimbreFiscalDigital10.TimbreFiscalDigital ) {
						fechaTimbrado = ( ( ( mx.grupocorasa.sat.common.TimbreFiscalDigital10.TimbreFiscalDigital ) complemento ).getFechaTimbrado() ).format( formatter );
						break;
					} else if ( complemento instanceof mx.grupocorasa.sat.common.TimbreFiscalDigital11.TimbreFiscalDigital ) {
						fechaTimbrado = ( ( ( mx.grupocorasa.sat.common.TimbreFiscalDigital11.TimbreFiscalDigital ) complemento ).getFechaTimbrado() ).format( formatter );
						;
						break;
					}
				}
			}
		}

		return fechaTimbrado;

	}

	public String getFolioFiscalOrig() {
		if ( isCfd40() )
			return getComprobante40().getFolio();
		else if ( isCfd33() )
			return getComprobante33().getFolio();
		else
			return getComprobante32().getFolioFiscalOrig();

	}

	public String getFolioSerie() {
		if ( isCfd40() ) {
			return getComprobante40().getSerie();
		} else {
			return getComprobante33().getSerie();
		}
	}
	
	public String getFolio() {
		if ( isCfd40() ) {
			return getComprobante40().getFolio();
		} else {
			return getComprobante33().getFolio();
		}
	}

	public String getFormaDePago() {
		if ( isCfd40() ) {
			return getComprobante40().getFormaPago().value();
		} else if ( isCfd33() ) {
			return getComprobante33().getFormaPago().value();
		} else {
			return getComprobante32().getFormaDePago();
		}
	}

	public String getFormaPago() {
		if ( isCfd40() ) {
			return getComprobante40().getFormaPago().value();
		} else {
			return getComprobante33().getFormaPago().value();
		}
	}

	public String getLugarExpedicion() {
		if ( isCfd40() ) {
			return getComprobante40().getLugarExpedicion();
		} else if ( isCfd33() ) {
			return getComprobante33().getLugarExpedicion();
		} else {
			return getComprobante32().getLugarExpedicion();
		}

	}

	public String getMetodoPago() {
		if ( isCfd40() )
			return getComprobante40().getMetodoPago() == null ? "" : getComprobante40().getMetodoPago().value();
		else if ( isCfd33() )
			return getComprobante33().getMetodoPago() == null ? "" : getComprobante33().getMetodoPago().value();
		else
			return getComprobante32().getMetodoDePago();
	}

	public String getMoneda() {
		if ( isCfd40() ) {
			return getComprobante40().getMoneda().value();
		} else if ( isCfd33() ) {
			return getComprobante33().getMoneda().value();
		} else {
			return getComprobante32().getMoneda();
		}
	}

	public String getNoCertificado() {
		String certificado = null;

		if ( isCfd40() )
			certificado = getComprobante40().getNoCertificado();
		else if ( isCfd33() )
			certificado = getComprobante33().getNoCertificado();
		else
			certificado = getComprobante32().getNoCertificado();

		return certificado;
	}

	public String getNombreEmisor() {
		if ( isCfd40() )
			return getComprobante40().getEmisor().getNombre();
		else if ( isCfd33() )
			return getComprobante33().getEmisor().getNombre();
		else
			return getComprobante32().getEmisor().getNombre();

	}

	public String getNombreReceptor() {
		if ( isCfd40() )
			return getComprobante40().getReceptor().getNombre();
		else if ( isCfd33() )
			return getComprobante33().getReceptor().getNombre();
		else
			return getComprobante32().getReceptor().getNombre();

	}

	/**
	 * @return the pagos
	 */
	public List<?> getPagos() {
		return pagos;
	}

	public String getRegimenEmisor() {
		if ( isCfd40() ) {
			return getComprobante40().getEmisor().getRegimenFiscal().value();
		} else if ( isCfd33() ) {
			return getComprobante33().getEmisor().getRegimenFiscal().value();
		} else {
			return getComprobante32().getEmisor().getRegimenFiscal().get( 0 ).getRegimen();
		}
	}

	public String getRFCEmisor() {
		if ( isCfd40() ) {
			return getComprobante40().getEmisor().getRfc();
		} else if ( isCfd33() ) {
			return getComprobante33().getEmisor().getRfc();
		} else {
			return getComprobante32().getEmisor().getRfc();
		}

	}

	public String getRFCReceptor() {
		if ( isCfd40() )
			return getComprobante40().getReceptor().getRfc();
		else if ( isCfd33() )
			return getComprobante33().getReceptor().getRfc();
		else
			return getComprobante32().getReceptor().getRfc();

	}

	public String getSelloCFD() {

		String selloCFD = null;

		if ( isCfd40() ) {
			List<?> complementos = getComprobante40().getComplemento().getAny();
			for ( Object complementoObj : complementos ) {

				if ( complementoObj instanceof TimbreFiscalDigital ) {

					selloCFD = ( ( TimbreFiscalDigital ) complementoObj ).getSelloCFD();

				}

			}
		} else {
			List<Complemento> complementos = getComprobante33().getComplemento();
			for ( Complemento complemento : complementos ) {

				for ( int counter = 0; counter < complemento.getAny().size(); counter++ ) {

					Object o = complemento.getAny().get( counter );

					if ( o instanceof mx.grupocorasa.sat.common.TimbreFiscalDigital10.TimbreFiscalDigital ) {

						selloCFD = ( ( TimbreFiscalDigital ) o ).getSelloCFD();

					}

				}

			}
		}
		return selloCFD;
	}

	public String getSelloSAT() {
		List<?> l = null;
		String selloSAT = null;

		if ( isCfd40() )
			l = ( List<?> ) getComprobante40().getComplemento().getAny();
		else if ( isCfd33() )
			l = getComprobante33().getComplemento();
		else
			l = getComprobante32().getComplemento().getAny();

		if ( l != null ) {
			for ( Iterator<?> k = l.iterator(); k.hasNext(); ) {
				Object o = k.next();

				if ( o instanceof mx.grupocorasa.sat.common.TimbreFiscalDigital10.TimbreFiscalDigital )
					selloSAT = ( ( mx.grupocorasa.sat.common.TimbreFiscalDigital10.TimbreFiscalDigital ) o ).getSelloSAT();
				else if ( o instanceof mx.grupocorasa.sat.common.TimbreFiscalDigital11.TimbreFiscalDigital )
					selloSAT = ( ( mx.grupocorasa.sat.common.TimbreFiscalDigital11.TimbreFiscalDigital ) o ).getSelloSAT();
			}
		}
		return selloSAT;
	}

	public BigDecimal getSubTotal() {
		if ( isCfd40() )
			return getComprobante40().getSubTotal();
		else if ( isCfd33() )
			return getComprobante33().getSubTotal();
		else
			return getComprobante32().getSubTotal();
	}

	public String getTipoComprobante() {
		if ( isCfd40() )
			return getComprobante40().getTipoDeComprobante().value();
		else
			return getComprobante33().getTipoDeComprobante().value();

	}

	public BigDecimal getTotal() {
		if ( isCfd40() )
			return getComprobante40().getTotal();
		else if ( isCfd33() )
			return getComprobante33().getTotal();
		else
			return getComprobante32().getTotal();
	}

	public BigDecimal getTotalImpuestosTrasladados() {
		if ( isCfd40() )
			if ( getComprobante40().getImpuestos() != null )
				return getComprobante40().getImpuestos().getTotalImpuestosTrasladados();
			else
				return new BigDecimal( 0.0d );
		else if ( isCfd33() )
			if ( getComprobante33().getImpuestos() != null )
				return getComprobante33().getImpuestos().getTotalImpuestosTrasladados();
			else
				return new BigDecimal( 0.0d );
		else
			return getComprobante32().getImpuestos().getTotalImpuestosTrasladados();
	}

	public BigDecimal getTotalRetenciones() {

		BigDecimal total = new BigDecimal( 0.0d );
		total.setScale( 2, BigDecimal.ROUND_UP );
		Map<String, BigDecimal> retenciones = listaRetenciones();

		for ( Iterator<String> i = retenciones.keySet().iterator(); i.hasNext(); ) {
			String retenNombre = i.next();
			total = total.add( retenciones.get( retenNombre ) );
		}

		return total;
	}

	public String getUUID() {

		List<?> l = null;
		String uuid = null;

		if ( isCfd40() ) {
			l = ( List<?> ) getComprobante40().getComplemento().getAny();
			if ( l != null )
				for ( Iterator<?> k = l.iterator(); k.hasNext(); ) {
					Object o = k.next();
					if ( o instanceof mx.grupocorasa.sat.common.TimbreFiscalDigital10.TimbreFiscalDigital ) {
						uuid = ( ( mx.grupocorasa.sat.common.TimbreFiscalDigital10.TimbreFiscalDigital ) o ).getUUID();
						break;
					} else if ( o instanceof mx.grupocorasa.sat.common.TimbreFiscalDigital11.TimbreFiscalDigital ) {
						uuid = ( ( mx.grupocorasa.sat.common.TimbreFiscalDigital11.TimbreFiscalDigital ) o ).getUUID();
						break;
					}

					if ( !StringUtils.isBlank( uuid ) )
						break;
				}
		} else if ( isCfd33() ) {
			l = getComprobante33().getComplemento();
			if ( l != null )
				for ( Iterator<?> k = l.iterator(); k.hasNext(); ) {
					Object o = k.next();
					List<Object> complementos = ( ( mx.grupocorasa.sat.cfd._33.Comprobante.Complemento ) o ).getAny();
					for ( Iterator<?> compIterator = complementos.iterator(); compIterator.hasNext(); ) {
						Object complemento = compIterator.next();
						if ( complemento instanceof mx.grupocorasa.sat.common.TimbreFiscalDigital10.TimbreFiscalDigital ) {
							uuid = ( ( mx.grupocorasa.sat.common.TimbreFiscalDigital10.TimbreFiscalDigital ) complemento ).getUUID();
							break;
						} else if ( complemento instanceof mx.grupocorasa.sat.common.TimbreFiscalDigital11.TimbreFiscalDigital ) {
							uuid = ( ( mx.grupocorasa.sat.common.TimbreFiscalDigital11.TimbreFiscalDigital ) complemento ).getUUID();
							break;
						}
					}

					if ( !StringUtils.isBlank( uuid ) )
						break;
				}
		} else {
			l = getComprobante32().getComplemento().getAny();
			if ( l != null ) {
				for ( Iterator<?> k = l.iterator(); k.hasNext(); ) {
					Object o = k.next();

					if ( o instanceof mx.grupocorasa.sat.common.TimbreFiscalDigital10.TimbreFiscalDigital ) {
						uuid = ( ( mx.grupocorasa.sat.common.TimbreFiscalDigital10.TimbreFiscalDigital ) o ).getUUID();
						break;
					} else if ( o instanceof mx.grupocorasa.sat.common.TimbreFiscalDigital11.TimbreFiscalDigital ) {
						uuid = ( ( mx.grupocorasa.sat.common.TimbreFiscalDigital11.TimbreFiscalDigital ) o ).getUUID();
						break;
					}

					if ( !StringUtils.isBlank( uuid ) )
						break;
				}

			}
		}

		return uuid;

	}

	/**
	 * @return the cfd33
	 */
	public boolean isCfd33() {
		return cfd33;
	}

	/**
	 * @return the cfd40
	 */
	public boolean isCfd40() {
		return cfd40;
	}

	/**
	 * @return the complementoCombustible
	 */
	public boolean isComplementoCombustible() {
		return complementoCombustible;
	}

	/**
	 * Devuelve el estado de cuenta de combustible.
	 * 
	 * @param comp
	 *            Comprobante CFDI 3.3
	 * @return Complemento de estado de cuenta de combustible.
	 */
	public EstadoDeCuentaCombustible leeEstadoDeCuenta() {
		EstadoDeCuentaCombustible estadoCta = null;

		if ( isCfd40() ) {
			List<?> complementos = getComprobante40().getComplemento().getAny();

			if ( complementos != null && complementos.size() > 0 )
				for ( Object complementoObj : complementos ) {
					if ( complementoObj instanceof EstadoDeCuentaCombustible ) {
						estadoCta = ( EstadoDeCuentaCombustible ) complementoObj;
						break;
					}

					if ( estadoCta != null )
						break;
				}
		} else {
			List<Complemento> complementos = getComprobante33().getComplemento();

			if ( complementos != null && complementos.size() > 0 )
				for ( Complemento complemento : complementos ) {

					for ( int counter = 0; counter < complemento.getAny().size(); counter++ ) {
						Object o = complemento.getAny().get( counter );

						if ( o instanceof EstadoDeCuentaCombustible ) {
							estadoCta = ( EstadoDeCuentaCombustible ) o;
							break;
						}

					}
					if ( estadoCta != null )
						break;
				}
		}
		return estadoCta;

	}

	public void leePagos() throws Exception {

		if ( isCfd40() ) {

			CFDv40 cfdi = new CFDv40( getComprobante40(), "mx.grupocorasa.sat.common.Pagos20" );
			List<?> complementos = ( ( mx.grupocorasa.sat.cfd._40.Comprobante ) cfdi.getComprobanteDocument() ).getComplemento().getAny();

			for ( Object complementoObj : complementos ) {

				log.debug( complementoObj.getClass() );

				if ( complementoObj instanceof mx.grupocorasa.sat.common.Pagos20.Pagos ) {
					pagos = ( ( mx.grupocorasa.sat.common.Pagos20.Pagos ) complementoObj ).getPago();
				}

			}

		} else if ( isCfd33() ) {

			CFDv33 cfdi = new CFDv33( getComprobante33(), "mx.grupocorasa.sat.common.Pagos10" );
			List<Complemento> complementos = ( ( mx.grupocorasa.sat.cfd._33.Comprobante ) cfdi.getComprobanteDocument() ).getComplemento();

			for ( Complemento complemento : complementos ) {

				for ( int counter = 0; counter < complemento.getAny().size(); counter++ ) {

					Object o = complemento.getAny().get( counter );
					log.debug( o.getClass() );

					if ( o instanceof Pagos ) {
						pagos = ( ( Pagos ) o ).getPago();
					}

				}

			}
		}
	}

	public Map<String, BigDecimal> listaImpuestos() {
		Map<String, BigDecimal> impuestos = new HashMap<String, BigDecimal>();

		if ( isCfd40() ) {

			if ( getComprobante40().getImpuestos() != null ) {
				mx.grupocorasa.sat.cfd._40.Comprobante.Impuestos.Traslados traslados = getComprobante40().getImpuestos().getTraslados();

				if ( traslados != null ) {
					for ( Iterator<mx.grupocorasa.sat.cfd._40.Comprobante.Impuestos.Traslados.Traslado> i = traslados.getTraslado().iterator(); i.hasNext(); ) {
						mx.grupocorasa.sat.cfd._40.Comprobante.Impuestos.Traslados.Traslado t = i.next();

						if ( impuestos.get( t.getImpuesto().value() ) == null )
							impuestos.put( t.getImpuesto().value(), t.getImporte() );
						else {
							BigDecimal sumando1 = impuestos.get( t.getImpuesto().value() );
							impuestos.put( t.getImpuesto().value(), sumando1.add( t.getImporte() ) );
						}
					}
				}
			}
		} else if ( isCfd33() ) {

			if ( getComprobante33().getImpuestos() != null ) {
				mx.grupocorasa.sat.cfd._33.Comprobante.Impuestos.Traslados traslados = getComprobante33().getImpuestos().getTraslados();

				if ( traslados != null ) {
					for ( Iterator<mx.grupocorasa.sat.cfd._33.Comprobante.Impuestos.Traslados.Traslado> i = traslados.getTraslado().iterator(); i.hasNext(); ) {
						mx.grupocorasa.sat.cfd._33.Comprobante.Impuestos.Traslados.Traslado t = i.next();

						if ( impuestos.get( t.getImpuesto().value() ) == null )
							impuestos.put( t.getImpuesto().value(), t.getImporte() );
						else {
							BigDecimal sumando1 = impuestos.get( t.getImpuesto().value() );
							impuestos.put( t.getImpuesto().value(), sumando1.add( t.getImporte() ) );
						}
					}
				}
			}
		} else {
			mx.grupocorasa.sat.cfd._32.Comprobante.Impuestos.Traslados traslados = getComprobante32().getImpuestos().getTraslados();

			if ( traslados != null ) {
				for ( Iterator<mx.grupocorasa.sat.cfd._32.Comprobante.Impuestos.Traslados.Traslado> i = traslados.getTraslado().iterator(); i.hasNext(); ) {
					mx.grupocorasa.sat.cfd._32.Comprobante.Impuestos.Traslados.Traslado t = i.next();

					if ( impuestos.get( t.getImpuesto() ) == null )
						impuestos.put( t.getImpuesto(), t.getImporte() );
					else {
						BigDecimal sumando1 = impuestos.get( t.getImpuesto() );
						impuestos.put( t.getImpuesto(), sumando1.add( t.getImporte() ) );
					}
				}
			}
		}
		return impuestos;
	}

	public Map<String, BigDecimal> listaRetenciones() {

		Map<String, BigDecimal> impuestos = new HashMap<String, BigDecimal>();
		BigDecimal totalRetenciones = new BigDecimal( 0.0 );

		if ( isCfd40() ) {

			if ( getComprobante40().getImpuestos() != null ) {

				mx.grupocorasa.sat.cfd._40.Comprobante.Impuestos.Retenciones ret = getComprobante40().getImpuestos().getRetenciones();

				if ( ret != null ) {
					for ( Iterator<mx.grupocorasa.sat.cfd._40.Comprobante.Impuestos.Retenciones.Retencion> i = ret.getRetencion().iterator(); i.hasNext(); ) {
						mx.grupocorasa.sat.cfd._40.Comprobante.Impuestos.Retenciones.Retencion r = i.next();
						if ( impuestos.get( r.getImpuesto().value() ) == null )
							impuestos.put( r.getImpuesto().value(), r.getImporte() );
						else {
							BigDecimal sumando1 = impuestos.get( r.getImpuesto().value() );
							impuestos.put( r.getImpuesto().value(), sumando1.add( r.getImporte() ) );
						}

						totalRetenciones = totalRetenciones.add( r.getImporte() );
					}
				}

				if ( getComprobante40().getComplemento() != null ) {

					mx.grupocorasa.sat.cfd._40.Comprobante.Complemento complemento = getComprobante40().getComplemento();

					for ( Iterator<?> itComplemento = complemento.getAny().iterator(); itComplemento.hasNext(); ) {

						Object obj = itComplemento.next();

						if ( obj instanceof mx.grupocorasa.sat.common.implocal10.ImpuestosLocales ) {
							List<Object> retencionesTraslados = ( ( mx.grupocorasa.sat.common.implocal10.ImpuestosLocales ) obj ).getRetencionesLocalesAndTrasladosLocales();
							for ( Iterator<Object> itRetenciones = retencionesTraslados.iterator(); itRetenciones.hasNext(); ) {
								Object objReten = itRetenciones.next();

								if ( objReten instanceof mx.grupocorasa.sat.common.implocal10.ImpuestosLocales.RetencionesLocales ) {
									mx.grupocorasa.sat.common.implocal10.ImpuestosLocales.RetencionesLocales retencion = ( mx.grupocorasa.sat.common.implocal10.ImpuestosLocales.RetencionesLocales ) objReten;

									String retencionNombre = retencion.getImpLocRetenido();
									if ( impuestos.get( retencionNombre ) == null )
										impuestos.put( retencionNombre, retencion.getImporte() );
									else {
										BigDecimal sumando1 = impuestos.get( retencionNombre );
										impuestos.put( retencionNombre, sumando1.add( retencion.getImporte() ) );
									}

									totalRetenciones = totalRetenciones.add( retencion.getImporte() );

								}
							}

						}
					}
				}

			}
		} else if ( isCfd33() ) {

			if ( getComprobante33().getImpuestos() != null ) {
				mx.grupocorasa.sat.cfd._33.Comprobante.Impuestos.Retenciones ret = getComprobante33().getImpuestos().getRetenciones();

				if ( ret != null ) {
					for ( Iterator<mx.grupocorasa.sat.cfd._33.Comprobante.Impuestos.Retenciones.Retencion> i = ret.getRetencion().iterator(); i.hasNext(); ) {
						mx.grupocorasa.sat.cfd._33.Comprobante.Impuestos.Retenciones.Retencion r = i.next();
						if ( impuestos.get( r.getImpuesto().value() ) == null )
							impuestos.put( r.getImpuesto().value(), r.getImporte() );
						else {
							BigDecimal sumando1 = impuestos.get( r.getImpuesto().value() );
							impuestos.put( r.getImpuesto().value(), sumando1.add( r.getImporte() ) );
						}

						totalRetenciones = totalRetenciones.add( r.getImporte() );
					}
				}

				if ( getComprobante33().getComplemento() != null ) {

					List<?> complemento = getComprobante33().getComplemento();
					for ( Iterator<?> itComplemento = complemento.iterator(); itComplemento.hasNext(); ) {

						mx.grupocorasa.sat.cfd._33.Comprobante.Complemento obj = ( mx.grupocorasa.sat.cfd._33.Comprobante.Complemento ) itComplemento.next();
						List<Object> complementos = obj.getAny();

						for ( Iterator<?> itComplementos = complementos.iterator(); itComplementos.hasNext(); ) {
							Object complementoObj = itComplementos.next();

							if ( complementoObj instanceof mx.grupocorasa.sat.common.implocal10.ImpuestosLocales ) {
								List<Object> retencionesTraslados = ( ( mx.grupocorasa.sat.common.implocal10.ImpuestosLocales ) complementoObj ).getRetencionesLocalesAndTrasladosLocales();
								for ( Iterator<Object> itRetenciones = retencionesTraslados.iterator(); itRetenciones.hasNext(); ) {
									Object objReten = itRetenciones.next();

									if ( objReten instanceof mx.grupocorasa.sat.common.implocal10.ImpuestosLocales.RetencionesLocales ) {
										mx.grupocorasa.sat.common.implocal10.ImpuestosLocales.RetencionesLocales retencion = ( mx.grupocorasa.sat.common.implocal10.ImpuestosLocales.RetencionesLocales ) objReten;

										String retencionNombre = retencion.getImpLocRetenido();
										if ( impuestos.get( retencionNombre ) == null )
											impuestos.put( retencionNombre, retencion.getImporte() );
										else {
											BigDecimal sumando1 = impuestos.get( retencionNombre );
											impuestos.put( retencionNombre, sumando1.add( retencion.getImporte() ) );
										}

										totalRetenciones = totalRetenciones.add( retencion.getImporte() );

									}
								}

							}
						}
					}

				}

			}
		} else {
			mx.grupocorasa.sat.cfd._32.Comprobante.Impuestos.Retenciones ret = getComprobante32().getImpuestos().getRetenciones();

			if ( ret != null ) {
				for ( Iterator<mx.grupocorasa.sat.cfd._32.Comprobante.Impuestos.Retenciones.Retencion> i = ret.getRetencion().iterator(); i.hasNext(); ) {
					mx.grupocorasa.sat.cfd._32.Comprobante.Impuestos.Retenciones.Retencion r = i.next();
					if ( impuestos.get( r.getImpuesto() ) == null )
						impuestos.put( r.getImpuesto(), r.getImporte() );
					else {
						BigDecimal sumando1 = impuestos.get( r.getImpuesto() );
						impuestos.put( r.getImpuesto(), sumando1.add( r.getImporte() ) );
					}
				}
			}

			if ( getComprobante32().getComplemento() != null ) {

				List<?> complemento = getComprobante32().getComplemento().getAny();
				for ( Iterator<?> itComplemento = complemento.iterator(); itComplemento.hasNext(); ) {
					Object obj = itComplemento.next();

					if ( obj instanceof ElementNSImpl && "implocal:ImpuestosLocales".equals( ( ( ElementNSImpl ) obj ).getNodeName() ) ) {

						ElementNSImpl el = ( ElementNSImpl ) obj;
						NodeList retencionesList = el.getChildNodes();

						for ( int cntChilds = 0; retencionesList != null && cntChilds < retencionesList.getLength(); cntChilds++ ) {
							Node retencion = retencionesList.item( cntChilds );
							if ( "RetencionesLocales".equalsIgnoreCase( retencion.getLocalName() ) ) {
								String retencionNombre = retencion.getAttributes().getNamedItem( "ImpLocRetenido" ).getNodeValue();
								if ( impuestos.get( retencionNombre ) == null )
									impuestos.put( retencionNombre, new BigDecimal( retencion.getAttributes().getNamedItem( "Importe" ).getNodeValue() ) );
								else {
									BigDecimal sumando1 = impuestos.get( retencionNombre );
									impuestos.put( retencionNombre, sumando1.add( new BigDecimal( retencion.getAttributes().getNamedItem( "Importe" ).getNodeValue() ) ) );
								}
							}
						}
					}
				}

			}

		}

		return impuestos;

	}

	public CargoECC procesaComplementoCombustible() {

		BigDecimal importeBruto = new BigDecimal( 0.00 );
		BigDecimal importeImpuestos = new BigDecimal( 0.00 );
		EstadoDeCuentaCombustible ecc = leeEstadoDeCuenta();
		List<ConceptoEstadoDeCuentaCombustible> conceptos = ecc.getConceptos().getConceptoEstadoDeCuentaCombustible();

		for ( ConceptoEstadoDeCuentaCombustible concepto : conceptos ) {

			importeBruto = importeBruto.add( concepto.getImporte() );

			List<Traslado> traslados = concepto.getTraslados().getTraslado();

			for ( Traslado traslado : traslados ) {
				importeImpuestos = importeImpuestos.add( traslado.getImporte() );
			}
		}

		return new CargoECC( importeBruto, importeImpuestos, new BigDecimal( 0.0 ) );

	}

	/**
	 * Establece la adenda de estado de cuenta de combustible
	 * 
	 * @param adenda
	 */
	public void setAdendaECC( AdendaECC adenda ) {
		this.adendaECC = adenda;
	}

	/**
	 * @param bonificacion
	 *            the bonificacion to set
	 */
	public void setBonificacion( Bonificacion bonificacion ) {
		this.bonificacion = bonificacion;
	}

	/**
	 * @param cfd33
	 *            the cfd33 to set
	 */
	public void setCfd33( boolean cfd33 ) {
		this.cfd33 = cfd33;
	}

	/**
	 * @param cfd40
	 *            the cfd40 to set
	 */
	public void setCfd40( boolean cfd40 ) {
		this.cfd40 = cfd40;
	}

	/**
	 * @param complementoCombustible
	 *            the complementoCombustible to set
	 */
	public void setComplementoCombustible( boolean complementoCombustible ) {
		this.complementoCombustible = complementoCombustible;
	}

	/**
	 * @param comprobante32
	 *            the comprobante32 to set
	 */
	public void setComprobante32( mx.grupocorasa.sat.cfd._32.Comprobante comprobante32 ) {
		this.comprobante32 = comprobante32;
	}

	/**
	 * @param comprobante33
	 *            the comprobante33 to set
	 */
	public void setComprobante33( mx.grupocorasa.sat.cfd._33.Comprobante comprobante33 ) {
		this.comprobante33 = comprobante33;
		this.conceptos = new Conceptos( ( ( mx.grupocorasa.sat.cfd._33.Comprobante ) comprobante33 ).getConceptos() );
	}

	/**
	 * @param comprobante40
	 *            the comprobante40 to set
	 */
	public void setComprobante40( mx.grupocorasa.sat.cfd._40.Comprobante comprobante40 ) {
		this.comprobante40 = comprobante40;
		this.conceptos = new Conceptos( ( ( mx.grupocorasa.sat.cfd._40.Comprobante ) comprobante40 ).getConceptos() );
	}

	/**
	 * @param edoCtaCombustible
	 *            the edoCtaCombustible to set
	 */
	public void setEstadoDeCuentaCombustible( EstadoDeCuentaCombustible edoCtaCombustible ) {
		this.estadoDeCuentaCombustible = edoCtaCombustible;
	}

	/**
	 * @param pagos
	 *            the pagos to set
	 */
	public void setPagos( List<?> pagos ) {
		this.pagos = pagos;
	}

	public long getFechaTimbradoMillis() {

		LocalDateTime fechaCertificado = null;

		List<?> l = null;

		if ( isCfd40() ) {

			l = ( List<?> ) getComprobante40().getComplemento().getAny();
			if ( l != null )
				for ( Iterator<?> k = l.iterator(); k.hasNext(); ) {
					Object o = k.next();
					if ( o instanceof mx.grupocorasa.sat.common.TimbreFiscalDigital10.TimbreFiscalDigital ) {
						fechaCertificado = ( ( mx.grupocorasa.sat.common.TimbreFiscalDigital10.TimbreFiscalDigital ) o ).getFechaTimbrado();
						break;
					} else if ( o instanceof mx.grupocorasa.sat.common.TimbreFiscalDigital11.TimbreFiscalDigital ) {
						fechaCertificado = ( ( mx.grupocorasa.sat.common.TimbreFiscalDigital11.TimbreFiscalDigital ) o ).getFechaTimbrado();
						break;
					}

				}
		} else if ( isCfd33() ) {
			l = getComprobante33().getComplemento();
			if ( l != null )
				for ( Iterator<?> k = l.iterator(); k.hasNext(); ) {
					Object o = k.next();
					List<Object> complementos = ( ( mx.grupocorasa.sat.cfd._33.Comprobante.Complemento ) o ).getAny();
					for ( Iterator<?> compIterator = complementos.iterator(); compIterator.hasNext(); ) {
						Object complemento = compIterator.next();
						if ( complemento instanceof mx.grupocorasa.sat.common.TimbreFiscalDigital10.TimbreFiscalDigital ) {
							fechaCertificado = ( ( mx.grupocorasa.sat.common.TimbreFiscalDigital10.TimbreFiscalDigital ) complemento ).getFechaTimbrado();
							break;
						} else if ( complemento instanceof mx.grupocorasa.sat.common.TimbreFiscalDigital11.TimbreFiscalDigital ) {
							fechaCertificado = ( ( mx.grupocorasa.sat.common.TimbreFiscalDigital11.TimbreFiscalDigital ) complemento ).getFechaTimbrado();
							break;
						}
					}

				}
		} else {

			l = getComprobante32().getComplemento().getAny();
			if ( l != null ) {
				for ( Iterator<?> k = l.iterator(); k.hasNext(); ) {
					Object o = k.next();

					if ( o instanceof mx.grupocorasa.sat.common.TimbreFiscalDigital10.TimbreFiscalDigital ) {
						fechaCertificado = ( ( mx.grupocorasa.sat.common.TimbreFiscalDigital10.TimbreFiscalDigital ) o ).getFechaTimbrado();
						break;
					} else if ( o instanceof mx.grupocorasa.sat.common.TimbreFiscalDigital11.TimbreFiscalDigital ) {
						fechaCertificado = ( ( mx.grupocorasa.sat.common.TimbreFiscalDigital11.TimbreFiscalDigital ) o ).getFechaTimbrado();
						break;

					}

				}

			}

		}

		return fechaCertificado.atZone( ZoneId.of( "America/Mexico_City" ) ).toInstant().toEpochMilli();

	}

	public List<String> getUUIDRelacionado_TipoRelacion() {
		List<String> uuid_tipo = new ArrayList<>();
		String uuidRelacionado = null;
		List<mx.grupocorasa.sat.cfd._40.Comprobante.CfdiRelacionados> relacionados40 = null;
		CfdiRelacionados relacionado = null;
		mx.grupocorasa.sat.cfd._33.Comprobante.CfdiRelacionados relacionados33 = null;
		try {
			if ( isCfd40() ) {
				relacionados40 = getComprobante40().getCfdiRelacionados();
				for ( Iterator<CfdiRelacionados> i = relacionados40.iterator(); i.hasNext(); ) {
					relacionado = i.next();
					uuidRelacionado = relacionado.getCfdiRelacionado().get( 0 ).getUUID();
					uuid_tipo.add( uuidRelacionado );
					uuid_tipo.add( relacionado.getTipoRelacion().value() );
					if ( !StringUtils.isBlank( uuidRelacionado ) )
						break;

				}
			} else if ( isCfd33() ) {
				relacionados33 = getComprobante33().getCfdiRelacionados();
				uuidRelacionado = relacionados33.getCfdiRelacionado().get( 0 ).getUUID();
				uuid_tipo.add( uuidRelacionado );
				uuid_tipo.add( relacionados33.getTipoRelacion().value() );
			} else {
				// Codificar

			}
		} finally {
			// TODO: handle finally clause
			uuidRelacionado = null;
			relacionados40 = null;
			relacionado = null;
			relacionados33 = null;
		}

		return uuid_tipo;
	}

	public List<CfdiRelacionado> getUUIDRelacionado33() {

		List<CfdiRelacionado> uuid_tipo = new ArrayList<>();

		mx.grupocorasa.sat.cfd._33.Comprobante.CfdiRelacionados relacionados33;
		try {
			relacionados33 = getComprobante33().getCfdiRelacionados();

			for ( Iterator<CfdiRelacionado> i = relacionados33.getCfdiRelacionado().iterator(); i.hasNext(); ) {
				CfdiRelacionado relacionado = i.next();
				uuid_tipo.add( relacionado );
			}

		} finally {
			relacionados33 = null;
		}

		return uuid_tipo;
	}

	public List<CfdiRelacionados> getUUIDRelacionado() {

		List<CfdiRelacionados> uuid_tipo = new ArrayList<>();
		List<mx.grupocorasa.sat.cfd._40.Comprobante.CfdiRelacionados> relacionados40 = null;

		try {
			relacionados40 = getComprobante40().getCfdiRelacionados();

			for ( Iterator<CfdiRelacionados> i = relacionados40.iterator(); i.hasNext(); ) {
				CfdiRelacionados relacionado = i.next();
				uuid_tipo.add( relacionado );
			}

		} finally {
			relacionados40 = null;
		}

		return uuid_tipo;
	}

	/*
	 * 
	 * public List<RetencionSAT> listaRetenciones() {
	 * 
	 * Map<String, BigDecimal> impuestos = new HashMap<String, BigDecimal>();
	 * 
	 * if ( isCfd40() ) {
	 * 
	 * if ( getComprobante40().getImpuestos() != null ) {
	 * 
	 * mx.grupocorasa.sat.cfd._40.Comprobante.Impuestos.Retenciones ret =
	 * getComprobante40().getImpuestos().getRetenciones();
	 * 
	 * if ( ret != null ) {
	 * 
	 * for (
	 * Iterator<mx.grupocorasa.sat.cfd._40.Comprobante.Impuestos.Retenciones.
	 * Retencion> i = ret.getRetencion().iterator(); i.hasNext(); ) {
	 * 
	 * Retencion r = i.next(); r.
	 * 
	 * // RetencionSAT retencionSAT = new RetencionSAT();
	 * 
	 * } }
	 * 
	 * if ( getComprobante40().getComplemento() != null ) {
	 * 
	 * mx.grupocorasa.sat.cfd._40.Comprobante.Complemento complemento =
	 * getComprobante40().getComplemento();
	 * 
	 * for ( Iterator<?> itComplemento = complemento.getAny().iterator();
	 * itComplemento.hasNext(); ) {
	 * 
	 * Object obj = itComplemento.next();
	 * 
	 * if ( obj instanceof mx.grupocorasa.sat.common.implocal10.ImpuestosLocales
	 * ) { List<Object> retencionesTraslados = ( (
	 * mx.grupocorasa.sat.common.implocal10.ImpuestosLocales ) obj
	 * ).getRetencionesLocalesAndTrasladosLocales(); for ( Iterator<Object>
	 * itRetenciones = retencionesTraslados.iterator(); itRetenciones.hasNext();
	 * ) { Object objReten = itRetenciones.next();
	 * 
	 * if ( objReten instanceof
	 * mx.grupocorasa.sat.common.implocal10.ImpuestosLocales.RetencionesLocales
	 * ) {
	 * mx.grupocorasa.sat.common.implocal10.ImpuestosLocales.RetencionesLocales
	 * retencion = (
	 * mx.grupocorasa.sat.common.implocal10.ImpuestosLocales.RetencionesLocales
	 * ) objReten;
	 * 
	 * String retencionNombre = retencion.getImpLocRetenido(); if (
	 * impuestos.get( retencionNombre ) == null ) impuestos.put(
	 * retencionNombre, retencion.getImporte() ); else { BigDecimal sumando1 =
	 * impuestos.get( retencionNombre ); impuestos.put( retencionNombre,
	 * sumando1.add( retencion.getImporte() ) ); }
	 * 
	 * } }
	 * 
	 * } } }
	 * 
	 * } } else if ( isCfd33() ) {
	 * 
	 * if ( getComprobante33().getImpuestos() != null ) {
	 * mx.grupocorasa.sat.cfd._33.Comprobante.Impuestos.Retenciones ret =
	 * getComprobante33().getImpuestos().getRetenciones();
	 * 
	 * if ( ret != null ) { for (
	 * Iterator<mx.grupocorasa.sat.cfd._33.Comprobante.Impuestos.Retenciones.
	 * Retencion> i = ret.getRetencion().iterator(); i.hasNext(); ) {
	 * mx.grupocorasa.sat.cfd._33.Comprobante.Impuestos.Retenciones.Retencion r
	 * = i.next(); if ( impuestos.get( r.getImpuesto().value() ) == null )
	 * impuestos.put( r.getImpuesto().value(), r.getImporte() ); else {
	 * BigDecimal sumando1 = impuestos.get( r.getImpuesto().value() );
	 * impuestos.put( r.getImpuesto().value(), sumando1.add( r.getImporte() ) );
	 * } } }
	 * 
	 * if ( getComprobante33().getComplemento() != null ) {
	 * 
	 * List<?> complemento = getComprobante33().getComplemento(); for (
	 * Iterator<?> itComplemento = complemento.iterator();
	 * itComplemento.hasNext(); ) {
	 * 
	 * mx.grupocorasa.sat.cfd._33.Comprobante.Complemento obj = (
	 * mx.grupocorasa.sat.cfd._33.Comprobante.Complemento )
	 * itComplemento.next(); List<Object> complementos = obj.getAny();
	 * 
	 * for ( Iterator<?> itComplementos = complementos.iterator();
	 * itComplementos.hasNext(); ) { Object complementoObj =
	 * itComplementos.next();
	 * 
	 * if ( complementoObj instanceof
	 * mx.grupocorasa.sat.common.implocal10.ImpuestosLocales ) { List<Object>
	 * retencionesTraslados = ( (
	 * mx.grupocorasa.sat.common.implocal10.ImpuestosLocales ) complementoObj
	 * ).getRetencionesLocalesAndTrasladosLocales(); for ( Iterator<Object>
	 * itRetenciones = retencionesTraslados.iterator(); itRetenciones.hasNext();
	 * ) { Object objReten = itRetenciones.next();
	 * 
	 * if ( objReten instanceof
	 * mx.grupocorasa.sat.common.implocal10.ImpuestosLocales.RetencionesLocales
	 * ) {
	 * mx.grupocorasa.sat.common.implocal10.ImpuestosLocales.RetencionesLocales
	 * retencion = (
	 * mx.grupocorasa.sat.common.implocal10.ImpuestosLocales.RetencionesLocales
	 * ) objReten;
	 * 
	 * String retencionNombre = retencion.getImpLocRetenido(); if (
	 * impuestos.get( retencionNombre ) == null ) impuestos.put(
	 * retencionNombre, retencion.getImporte() ); else { BigDecimal sumando1 =
	 * impuestos.get( retencionNombre ); impuestos.put( retencionNombre,
	 * sumando1.add( retencion.getImporte() ) ); }
	 * 
	 * } }
	 * 
	 * } } }
	 * 
	 * }
	 * 
	 * } } else { mx.grupocorasa.sat.cfd._32.Comprobante.Impuestos.Retenciones
	 * ret = getComprobante32().getImpuestos().getRetenciones();
	 * 
	 * if ( ret != null ) { for (
	 * Iterator<mx.grupocorasa.sat.cfd._32.Comprobante.Impuestos.Retenciones.
	 * Retencion> i = ret.getRetencion().iterator(); i.hasNext(); ) {
	 * mx.grupocorasa.sat.cfd._32.Comprobante.Impuestos.Retenciones.Retencion r
	 * = i.next(); if ( impuestos.get( r.getImpuesto() ) == null )
	 * impuestos.put( r.getImpuesto(), r.getImporte() ); else { BigDecimal
	 * sumando1 = impuestos.get( r.getImpuesto() ); impuestos.put(
	 * r.getImpuesto(), sumando1.add( r.getImporte() ) ); } } }
	 * 
	 * if ( getComprobante32().getComplemento() != null ) {
	 * 
	 * List<?> complemento = getComprobante32().getComplemento().getAny(); for (
	 * Iterator<?> itComplemento = complemento.iterator();
	 * itComplemento.hasNext(); ) { Object obj = itComplemento.next();
	 * 
	 * if ( obj instanceof ElementNSImpl && "implocal:ImpuestosLocales".equals(
	 * ( ( ElementNSImpl ) obj ).getNodeName() ) ) {
	 * 
	 * ElementNSImpl el = ( ElementNSImpl ) obj; NodeList retencionesList =
	 * el.getChildNodes();
	 * 
	 * for ( int cntChilds = 0; retencionesList != null && cntChilds <
	 * retencionesList.getLength(); cntChilds++ ) { Node retencion =
	 * retencionesList.item( cntChilds ); if (
	 * "RetencionesLocales".equalsIgnoreCase( retencion.getLocalName() ) ) {
	 * String retencionNombre = retencion.getAttributes().getNamedItem(
	 * "ImpLocRetenido" ).getNodeValue(); if ( impuestos.get( retencionNombre )
	 * == null ) impuestos.put( retencionNombre, new BigDecimal(
	 * retencion.getAttributes().getNamedItem( "Importe" ).getNodeValue() ) );
	 * else { BigDecimal sumando1 = impuestos.get( retencionNombre );
	 * impuestos.put( retencionNombre, sumando1.add( new BigDecimal(
	 * retencion.getAttributes().getNamedItem( "Importe" ).getNodeValue() ) ) );
	 * } } } } }
	 * 
	 * }
	 * 
	 * }
	 * 
	 * return impuestos;
	 * 
	 * }
	 * 
	 */

	public String getUsoCFDI() {
		if ( isCfd40() )
			return getComprobante40().getReceptor().getUsoCFDI().value();
		else
			return getComprobante33().getReceptor().getUsoCFDI().value();
	}
	
	public String getCodigoPostalEmisor() {
		if ( isCfd40() )
			return getComprobante40().getLugarExpedicion();
		else
			return getComprobante33().getLugarExpedicion();
	}

	/*
	 * VGC20231115 Se agrega lectura de adenda de vales de combustible.
	 */
	public boolean isFacturaVales() {

		return this.facturaVales;
	}

	/*
	 * VGC20231115 Se agrega lectura de adenda de vales de combustible.
	 */
	public void setFacturaVales( boolean facturaVales ) {

		this.facturaVales = facturaVales;
	}

	/*
	 * VGC20231115 Se agrega lectura de adenda de vales de combustible.
	 */
	public AddendaEfectivale getAdenda() {
		return this.adenda;
	}

	/*
	 * VGC20231115 Se agrega lectura de adenda de vales de combustible.
	 */
	public void setAdenda( AddendaEfectivale adenda ) {
		this.adenda = adenda;
	}

	public String getRegimenReceptor() {
		if ( isCfd40() ) {
			return getComprobante40().getReceptor().getRegimenFiscalReceptor().value();
		} else {
			return "";
		}
		
	}
}
