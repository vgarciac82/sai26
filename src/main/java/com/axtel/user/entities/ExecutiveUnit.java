package com.axtel.user.entities;


public class ExecutiveUnit {

	private boolean	active;
	
	private String	administrativeUnit;
	
	private String	administrativeUnitName;
	
	private String	areaControl;
	
	private String	budgetProgram;
	
	private String	coordinatorUnit;
	
	private String	executiveUnit;
	
	private String	executiveUnitName;
	
	private String	executiveUnitStands;
	
	private int		idCompany;
	
	private int		idExecutiveUnit;
	
	private int		responsibleCenter;
	
	private String	responsibleUnit;
	
	private int		state;
	
	private int		town;
	
	private int		workplace;
	
	public String getAdministrativeUnit() {
		return administrativeUnit;
	}
	
	public String getAdministrativeUnitName() {
		return administrativeUnitName;
	}
	
	public String getAreaControl() {
		return areaControl;
	}
	
	public String getBudgetProgram() {
		return budgetProgram;
	}
	
	public String getCoordinatorUnit() {
		return coordinatorUnit;
	}
	
	public String getExecutiveUnit() {
		return executiveUnit;
	}
	
	public String getExecutiveUnitName() {
		return executiveUnitName;
	}
	
	public String getExecutiveUnitStands() {
		return executiveUnitStands;
	}
	
	public int getIdCompany() {
		return idCompany;
	}
	
	public int getIdExecutiveUnit() {
		return idExecutiveUnit;
	}
	
	public int getResponsibleCenter() {
		return responsibleCenter;
	}
	
	public String getResponsibleUnit() {
		return responsibleUnit;
	}
	
	public int getState() {
		return state;
	}
	
	public int getTown() {
		return town;
	}
	
	public int getWorkplace() {
		return workplace;
	}
	
	public boolean isActive() {
		return active;
	}
	
	public void setActive( boolean active ) {
		this.active = active;
	}
	public void setAdministrativeUnit( String administrativeUnit ) {
		this.administrativeUnit = administrativeUnit;
	}
	public void setAdministrativeUnitName( String administrativeUnitName ) {
		this.administrativeUnitName = administrativeUnitName;
	}
	public void setAreaControl( String areaControl ) {
		this.areaControl = areaControl;
	}
	public void setBudgetProgram( String budgetProgram ) {
		this.budgetProgram = budgetProgram;
	}
	public void setCoordinatorUnit( String coordinatorUnit ) {
		this.coordinatorUnit = coordinatorUnit;
	}
	public void setExecutiveUnit( String executiveUnit ) {
		this.executiveUnit = executiveUnit;
	}
	public void setExecutiveUnitName( String executiveUnitName ) {
		this.executiveUnitName = executiveUnitName;
	}
	public void setExecutiveUnitStands( String executiveUnitStands ) {
		this.executiveUnitStands = executiveUnitStands;
	}
	public void setIdCompany( int idCompany ) {
		this.idCompany = idCompany;
	}
	public void setIdExecutiveUnit( int idExecutiveUnit ) {
		this.idExecutiveUnit = idExecutiveUnit;
	}
	public void setResponsibleCenter( int responsibleCenter ) {
		this.responsibleCenter = responsibleCenter;
	}
	public void setResponsibleUnit( String responsibleUnit ) {
		this.responsibleUnit = responsibleUnit;
	}
	public void setState( int state ) {
		this.state = state;
	}
	public void setTown( int town ) {
		this.town = town;
	}
	public void setWorkplace( int workplace ) {
		this.workplace = workplace;
	}

	@Override
	public String toString() {
		return "ExecutiveUnit [idExecutiveUnit=" + idExecutiveUnit + ", idCompany=" + idCompany + ", active=" + active + ", executiveUnit=" + executiveUnit + ", administrativeUnit=" + administrativeUnit + ", executiveUnitName=" + executiveUnitName + ", responsibleUnit=" + responsibleUnit + ", coordinatorUnit=" + coordinatorUnit + ", executiveUnitStands=" + executiveUnitStands + ", workplace=" + workplace + ", state=" + state + ", budgetProgram=" + budgetProgram + ", administrativeUnitName=" + administrativeUnitName + ", town=" + town + ", areaControl=" + areaControl + ", responsibleCenter=" + responsibleCenter + "]";
	}

}
