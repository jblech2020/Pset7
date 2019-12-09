package com.apcsa.model;

import java.sql.ResultSet;

import com.apcsa.model.User;

public class Teacher extends User {

    private int teacherId;
    private int departmentId;
    private String firstName;
    private String lastName;

    public Teacher(int teacherId, int departmentId, String firstName, String lastName) {
        this.teacherId = teacherId;
        this.departmentId = departmentId;
        this.firstName = firstName;
        this.lastName = lastName;
    }

	public Teacher(User user, ResultSet rs) {
		// TODO Auto-generated constructor stub
	}

//    public Teacher(int teacherId, int departmentId, String firstName, String lastName) {
//        this.teacherId = teacherId;
//        this.departmentId = departmentId;
//        this.firstName = firstName;
//        this.lastName = lastName;
//    }


}
