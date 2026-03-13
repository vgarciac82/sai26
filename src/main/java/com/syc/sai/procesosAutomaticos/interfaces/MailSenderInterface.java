package com.syc.sai.procesosAutomaticos.interfaces;

/**
 * Interface para el envio de correos automatico. Debido a los direntes tipos de
 * servicios de mensajeria encontrado en los clientes, se realiza esta
 * interface. Lo mas comun sera que la informacion se envie por un servidor de
 * correos SMTP con lo que las clases destinadas para ello sirven perfectamente.
 * En casos especificos se debe crear un objeto que herede de esta clase y
 * defina el metodo EnviaCorreo de la manera que se indique en el cliente.
 * 
 * @author Vicente Garcia Carrillo
 * @version 1.0
 * @since 1.0
 */
public abstract class MailSenderInterface {

	/**
	 * IP del host. Servidor de SMTP o RELAY
	 */
	private String	ipHost;
	/**
	 * Puerto del host.
	 */
	private int		port;
	/**
	 * Protocolo.
	 */
	private String	protocol;
	/**
	 * Remitente
	 */
	private String	from;
	/**
	 * Destinatarios separados por ;
	 */
	private String	to;
	/**
	 * Mensaje a enviar.
	 */
	private String	message;

	/**
	 * Obtienen la IP del Host
	 * 
	 * @return IP del host
	 */
	public String getIpHost() {
		return ipHost;
	}

	/**
	 * Establece la IP del HOST
	 * 
	 * @param ipHost
	 *            IP del HOST
	 */
	public void setIpHost(String ipHost) {
		this.ipHost = ipHost;
	}

	/**
	 * Devuelve el puerto por el que se conectara al servidor HOST. Por ejemplo
	 * para SMTP el puerto default es el 25
	 * 
	 * @return Puerto de conexion
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Establece el puerto por el que se conectara al servidor HOST. Por ejemplo
	 * para SMTP el puerto default es el 25
	 * 
	 * @param port
	 *            Puerto de conexion
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * Devuelve el protoclo a utilizar para el envio de correos. Por lo general
	 * es SMTP
	 * 
	 * @return protoclo de conexion
	 */
	public String getProtocol() {
		return protocol;
	}

	/**
	 * Establece el protoclo a utilizar para el envio de correos. Por lo general
	 * es SMTP
	 * 
	 * @param protocol
	 *            protoclo de conexion
	 */
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	/**
	 * Devuelve el remitente del correo (Quien envia)
	 * 
	 * @return Remitente
	 */
	public String getFrom() {
		return from;
	}

	/**
	 * Establece el remitente del correo (Quien envia)
	 * 
	 * @param from
	 *            Remitente
	 */
	public void setFrom(String from) {
		this.from = from;
	}

	/**
	 * Devuelve la lista de destinatarios separados por ; En caso de ser solo un
	 * receptor tambien debera separarse por ;
	 * 
	 * @return Lista de destinatarios separados por ";"
	 */
	public String getTo() {
		return to;
	}

	/**
	 * Establece la lista de destinatarios separados por ; En caso de ser solo
	 * un receptor tambien debera separarse por ;
	 * 
	 * @param to
	 *            Lista de destinatarios separados por ";"
	 */
	public void setTo(String to) {
		this.to = to;
	}

	/**
	 * Regresa el mensage a enviar.
	 * 
	 * @return mensaje a enviar
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Establece el mensaje a evniar
	 * 
	 * @param message
	 *            mensaje a enviar
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * Constructor
	 * 
	 * @param ipHost
	 *            Direccion IP del HOST
	 * @param port
	 *            Puerto
	 * @param protocol
	 *            Protocolo
	 * @param from
	 *            Remitente
	 * @param to
	 *            Destinatario
	 * @param message
	 *            Mensaje
	 */
	public MailSenderInterface(String ipHost, int port, String protocol, String from, String to, String message) {
		super();
		this.ipHost = ipHost;
		this.port = port;
		this.protocol = protocol;
		this.from = from;
		this.to = to;
		this.message = message;
	}

	/**
	 * Constructor
	 * 
	 * @param ipHost
	 *            Direccion IP del HOST
	 * @param port
	 *            Puerto
	 * @param protocol
	 *            Protocolo
	 */
	public MailSenderInterface(String ipHost, int port, String protocol) {
		super();
		this.ipHost = ipHost;
		this.port = port;
		this.protocol = protocol;
	}

	/**
	 * Constructor.
	 */
	public MailSenderInterface() {
		super();
	}

	/**
	 * Envia un mensaje segun los valores establecidos.
	 * 
	 * @return true si y solo si envia el correo sin recibir reporte de errores
	 * @throws Exception
	 */
	public abstract boolean sendMail() throws Exception;

	/**
	 * Envia correo a la lista de destinatarios definidos.
	 * 
	 * @param from
	 *            Remitente del correo
	 * @param to
	 *            Lista de destinatarios separados por ;
	 * @param message
	 *            Mensaje
	 * @return true si y solo si envia el correo sin recibir reporte de errores
	 */
	public abstract boolean sendMail(String from, String to, String message);

}
