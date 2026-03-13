package com.axtel.sisecop.dto;


public class TopAuthorizationDTO {

	private String	adscription;

	private String	boss;

	private String	email;

	private int		employee;

	private String	employeeStatus;

	private String	fullName;

	private String	jobTitle;

	private String	level;

	private int		positionId;

	private int		project;

	private int		row;

	public String getAdscription() {
		return adscription;
	}

	public String getBoss() {
		return boss;
	}

	public String getEmail() {
		return email;
	}

	public int getEmployee() {
		return employee;
	}

	public String getEmployeeStatus() {
		return employeeStatus;
	}

	public String getFullName() {
		return fullName;
	}

	public String getJobTitle() {
		return jobTitle;
	}

	public String getLevel() {
		return level;
	}

	public int getPositionId() {
		return positionId;
	}

	public int getProject() {
		return project;
	}

	public int getRow() {
		return row;
	}

	public void setAdscription( String adscription ) {
		this.adscription = adscription;
	}

	public void setBoss( String boss ) {
		this.boss = boss;
	}

	public void setEmail( String email ) {
		this.email = email;
	}

	public void setEmployee( int employee ) {
		this.employee = employee;
	}

	public void setEmployeeStatus( String employeeStatus ) {
		this.employeeStatus = employeeStatus;
	}

	public void setFullName( String fullName ) {
		this.fullName = fullName;
	}

	public void setJobTitle( String jobTitle ) {
		this.jobTitle = jobTitle;
	}

	public void setLevel( String level ) {
		this.level = level;
	}

	public void setPositionId( int positionId ) {
		this.positionId = positionId;
	}

	public void setProject( int project ) {
		this.project = project;
	}

	public void setRow( int row ) {
		this.row = row;
	}

	@Override
	public String toString() {
		return "TopAuthorizationDTO [row=" + row + ", employee=" + employee + ", fullName=" + fullName + ", positionId=" + positionId + ", jobTitle=" + jobTitle + ", level=" + level + ", project=" + project + ", adscription=" + adscription + ", boss=" + boss + ", employeeStatus=" + employeeStatus + ", email=" + email + "]";
	}
}
