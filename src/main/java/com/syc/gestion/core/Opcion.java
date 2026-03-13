package com.syc.gestion.core;

import java.io.Serializable;

public class Opcion implements Serializable, Cloneable {

	private final static long serialVersionUID = 1;

	private int		id_opcion;
	private String	o_descripcion;
	private String	o_estaenmenu;
	private String	o_action;
	private String	o_target;
	private int		o_id_parent;
	/* ********************************************************************
	 * linea agregada para utilizar el campo o_orden de la tabla cg_opcion
	 * 08/06/2012
	 * Martha Aurora Sánchez Valdivieso
	 */
	private int		o_orden;
	// ********************************************************************
	private int		id_producto;
	
	final static public String OPC_EN_MENU    = "S";
	final static public String OPC_NO_EN_MENU = "N";
	
	public int getId_opcion() {
		return id_opcion;
	}
	public void setId_opcion(int id_opcion) {
		this.id_opcion = id_opcion;
	}
	public String getO_descripcion() {
		return o_descripcion;
	}
	public void setO_descripcion(String o_descripcion) {
		this.o_descripcion = o_descripcion;
	}
	public String getO_estaenmenu() {
		return o_estaenmenu;
	}
	public void setO_estaenmenu(String o_estaenmenu) {
		this.o_estaenmenu = o_estaenmenu;
	}
	public String getO_action() {
		return o_action;
	}
	public void setO_action(String o_action) {
		this.o_action = o_action;
	}
	public String getO_target() {
		return o_target;
	}
	public void setO_target(String o_target) {
		this.o_target = o_target;
	}
	public int getO_id_parent() {
		return o_id_parent;
	}
	public void setO_id_parent(int o_id_parent) {
		this.o_id_parent = o_id_parent;
	}
	/* ********************************************************************
	 * métodos agregados para utilizar el campo o_orden de la tabla cg_opcion
	 * 08/06/2012
	 * Martha Aurora Sánchez Valdivieso
	 */
	public int getO_orden() {
		return o_orden;
	}
	public void setO_orden(int o_orden) {
		this.o_orden = o_orden;
	}
	// **********************************************************************
	public int getId_producto() {
		return id_producto;
	}
	public void setId_producto(int id_producto) {
		this.id_producto = id_producto;
	}

	//@Override
	protected Object clone() throws CloneNotSupportedException {
		Opcion retVal = new Opcion();
		retVal.setId_opcion(this.id_opcion);
		retVal.setO_action(this.o_action);
		retVal.setO_descripcion(this.o_descripcion);
		retVal.setO_estaenmenu(this.o_estaenmenu);
		retVal.setO_target(this.o_target);
		retVal.setO_id_parent(this.o_id_parent);
		retVal.setO_orden(this.o_orden);
		retVal.setId_producto(this.id_producto);

		return retVal;
	}
}
