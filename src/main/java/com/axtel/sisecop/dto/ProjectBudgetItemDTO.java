package com.axtel.sisecop.dto;


import java.io.Serializable;


public class ProjectBudgetItemDTO implements Serializable {

	private static final long	serialVersionUID	= -898128868812639658L;
	private String				administrativeUnit;
	private String				budgetItem;
	private int					endYear;
	private int					idService;
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

	public int getIdService() {
		return idService;
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

	public void setIdService( int idService ) {
		this.idService = idService;
	}

	public void setInitialYear( int initialYear ) {
		this.initialYear = initialYear;
	}

	public void setManagement( String management ) {
		this.management = management;
	}

	@Override
	public String toString() {
		return "ProyectoServicioClaveDTO [idService=" + idService + ", endYear=" + endYear + ", initialYear=" + initialYear + ", management=" + management + ", budgetItem=" + budgetItem + ", administrativeUnit=" + administrativeUnit + "]";
	}
}
