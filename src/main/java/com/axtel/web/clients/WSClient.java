package com.axtel.web.clients;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.glassfish.jersey.jackson.JacksonFeature;

/**
 * Clase padre de los clientes de WS
 * 
 * @author vicente.garcia
 *
 */
public abstract class WSClient {

	/**
	 * Objeto para realizar peticiones HTTP
	 */
	private Client client;
	/**
	 * URL del EndPoint que valida el acceso al usuario
	 */
	private String urlService;

	/**
	 * @return the client
	 */
	public Client getClient() {
		return client;
	}

	/**
	 * @param client the client to set
	 */
	public void setClient(Client client) {
		this.client = client;
	}

	/**
	 * @return the urlService
	 */
	public String getUrlService() {
		return urlService;
	}

	/**
	 * @param urlService the urlService to set
	 */
	public void setUrlService(String urlService) {
		this.urlService = urlService;
	}

	/**
	 * Construye un nuevo objeto cliente
	 */
	public WSClient() {
		super();
		this.client = ClientBuilder.newClient().register(new JacksonFeature());
	}

}
