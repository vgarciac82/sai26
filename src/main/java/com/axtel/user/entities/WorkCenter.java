package com.axtel.user.entities;


public class WorkCenter {

	private String	description;
	private int		id;

	public String getDescription() {
		return description;
	}

	public void setDescription( String description ) {
		this.description = description;
	}

	public int getId() {
		return id;
	}

	public void setId( int id ) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "WorkCenter [description=" + description + ", id=" + id + "]";
	}

}
