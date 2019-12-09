package com.apcsa.model;

import com.apcsa.model.User;

public class Student extends User {

  
	public Student(int userId, String accountType, String username, String password, String lastLogin) {
		super(userId, accountType, username, password, lastLogin);
	}

	private int studentId;
    private int classRank;
    private int gradeLevel;
    private int graduationYear;
    private double gpa;
    private String firstName;
    private String lastName;
	private String user;
	private String rs;

    public void student(String user, String rs){
      this.user = user;
      this.rs = rs;
    }

}
