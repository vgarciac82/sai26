package com.axtel.user.entities;


public class Job {

	private String			description;
	private int				id;
	private JobAdditional	jobAdditional;

	public String getDescription() {
		return description;
	}

	public int getId() {
		return id;
	}

	public JobAdditional getJobAdditional() {
		return jobAdditional;
	}

	public void setDescription( String description ) {
		this.description = description;
	}

	public void setId( int id ) {
		this.id = id;
	}

	public void setJobAdditional( JobAdditional jobAdditional ) {
		this.jobAdditional = jobAdditional;
	}

	@Override
	public String toString() {
		return "Job [id=" + id + ", description=" + description + ", jobAdditional=" + jobAdditional + "]";
	}

}
