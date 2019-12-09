package com.apcsa.model;

import java.sql.ResultSet;

import com.apcsa.model.User;

public class Student extends User {
  
	private int studentId;
    private int classRank;
    private int gradeLevel;
    private int graduationYear;
    private double gpa;
    private String firstName;
    private String lastName;
	private User user;
	private ResultSet rs;
	
	public Student(int userId, String accountType, String username, String password, String lastLogin) {
		super(userId, accountType, username, password, lastLogin);
	}

    public Student(User user, ResultSet rs) {
      this.user = user;
      this.rs = rs;
    }

}
