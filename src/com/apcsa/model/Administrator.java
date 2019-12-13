package com.apcsa.model;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.apcsa.model.User;

public class Administrator extends User {
	
	private int administratorId;
    private String firstName;
    private String lastName;
    private String jobTitle;
    
    public Administrator(User user, ResultSet rs) throws SQLException {
		super(user);
		this.setJobTitle(rs.getString("job_title"));
		this.lastName = rs.getString("last_name");
		this.firstName = rs.getString("first_name");
		this.administratorId = rs.getInt("administrator_id");
    }

	/*
	 * Getters
	 */
    
	public int getAdministratorId() {
		return administratorId;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getJobTitle() {
		return jobTitle;
	}
	
    /*
	 * Setters
	 */
	
	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}
}