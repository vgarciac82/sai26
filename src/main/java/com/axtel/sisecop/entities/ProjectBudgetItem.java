package com.axtel.sisecop.entities;


import java.io.Serializable;


public class ProjectBudgetItem implements Serializable {

	private static final long	serialVersionUID	= 5660261164249666674L;
	private String				administrativeUnit;

	private String				budgetItem;

	private int					endYear;

	private int					id;

	private int					initialYear;

	private String				management;

	public String getAdministrativeUnit() {
		return administrativeUnit;
	}

	public String getBudgetItem() {
		return budgetItem;
	}

	public int getEndYear() {
		return endYear;
	}

	public int getId() {
		return id;
	}

	public int getInitialYear() {
		return initialYear;
	}

	public String getManagement() {
		return management;
	}

	public void setAdministrativeUnit( String administrativeUnit ) {
		this.administrativeUnit = administrativeUnit;
	}

	public void setBudgetItem( String budgetItem ) {
		this.budgetItem = budgetItem;
	}

	public void setEndYear( int endYear ) {
		this.endYear = endYear;
	}

	public void setId( int id ) {
		this.id = id;
	}

	public void setInitialYear( int initialYear ) {
		this.initialYear = initialYear;
	}

	public void setManagement( String management ) {
		this.management = management;
	}

	@Override
	public String toString() {
		return "ProyectoServicioClave [id=" + id + ", endYear=" + endYear + ", initialYear=" + initialYear + ", management=" + management + ", budgetItem=" + budgetItem + ", administrativeUnit=" + administrativeUnit + "]";
	}

}
