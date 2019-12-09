package com.apcsa.model;

import java.sql.ResultSet;

import com.apcsa.model.User;

public class Administrator extends User {

    public Administrator(int userId, String accountType, String username, String password, String lastLogin) {
		super(userId, accountType, username, password, lastLogin);
		// TODO Auto-generated constructor stub
	}
    
	public Administrator(User user, ResultSet rs) {
		// TODO Auto-generated constructor stub
	}

	private int administratorId;
    private String firstName;
    private String lastName;
    private String jobTitle;

}