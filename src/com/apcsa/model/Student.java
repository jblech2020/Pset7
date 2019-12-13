package com.apcsa.model;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.apcsa.model.User;

public class Student extends User {
  
	private int studentId;
    private int classRank;
    private int gradeLevel;
    private int graduationYear;
    private double gpa;
    private String firstName;
    private String lastName;
	
	public Student(User user, ResultSet rs) throws SQLException {
		super(user);
		this.studentId = rs.getInt("student_id");
		this.setClassRank(rs.getInt("class_rank"));
		this.setGradeLevel(rs.getInt("grade_level"));
		this.graduationYear = rs.getInt("graduation");
		this.setGpa(rs.getDouble("gpa"));
		this.firstName = rs.getString("first_name");
		this.lastName = rs.getString("last_name");
	}

	/*
	 * Getters
	 */
	
	public int getStudentId() {
		return studentId;
	}

	public int getClassRank() {
		return classRank;
	}

	public int getGradeLevel() {
		return gradeLevel;
	}

	public double getGpa() {
		return gpa;
	}
	
	public int getGraduationYear() {
		return graduationYear;
	}
	
	public String getFirstName() {
		return firstName;
	}
	
	public String getLastName() {
		return lastName;
	}
	
	/*
	 * Setters
	 */

	public void setGradeLevel(int gradeLevel) {
		this.gradeLevel = gradeLevel;
	}
	
	public void setClassRank(int classRank) {
		this.classRank = classRank;
	}

	public void setGpa(double gpa) {
		this.gpa = gpa;
	}

}

