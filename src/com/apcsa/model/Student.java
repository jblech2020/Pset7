package com.apcsa.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.apcsa.controller.Utils;
import com.apcsa.data.*;
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
		this.graduationYear = rs.getInt("graduation");  //j didn't have "set"s, some are blue on here, might need to change to the format from line 31
		this.setGpa(rs.getDouble("gpa"));
		this.firstName = rs.getString("first_name");
		this.lastName = rs.getString("last_name");
	}
	
	public Student(ResultSet rs) throws SQLException {
		//user id, account type, username, password, last login
		super(rs.getInt("user_id"), rs.getString("account_type"), rs.getString("username"), rs.getString("auth"), rs.getString("last_login"));

		this.studentId = rs.getInt("student_id");
    	this.classRank = rs.getInt("class_rank");
    	this.gradeLevel = rs.getInt("grade_level");
    	this.graduationYear = rs.getInt("graduation");
    	this.gpa = rs.getDouble("gpa");
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

  public void changePassword(Scanner in) {
		System.out.println("\nEnter current password:");
        String currentPassword = in.nextLine();
        currentPassword = Utils.getHash(currentPassword);

    	if (currentPassword.equals(this.password)) {
    		System.out.println("\nEnter a new password:");
    		String password = Utils.getHash((in.nextLine()));
    		this.setPassword(password);
        	try {
        		Connection conn = PowerSchool.getConnection();
        		PowerSchool.updatePassword(conn, this.getUsername(), password);
        	} catch (SQLException e){
        		System.out.println(e);
        	}
    	}else {
    		System.out.println("\nIncorrect current password.");
    	}

	}

	public void viewAssignmentGradesByCourse(Scanner in) {
		System.out.print("\n");
		ArrayList<String> course_nos = new ArrayList<String>();

		int count = 1;
		int input = 0;
		int selection = 0;
		String selectionString = "";

		try (Connection conn = PowerSchool.getConnection()) {
			PreparedStatement stmt = conn.prepareStatement(QueryUtils.GET_STUDENT_COURSES);
			stmt.setInt(1, this.getStudentId());
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					System.out.println("[" + count + "] " + rs.getString("course_no"));
					count++;
					course_nos.add(rs.getString("course_no"));
				}
			} catch (SQLException e) {
				System.out.println(e);
			}
		} catch (SQLException e) {
			System.out.println(e);
		}

		try {
			input = in.nextInt();
		} catch (InputMismatchException e) {
			System.out.println("\nYour input was invalid. Please try again.");
		} finally {
			in.nextLine();
		}

		System.out.println("\n[1] MP1 Assignment.");
		System.out.println("[2] MP2 Assignment.");
		System.out.println("[3] MP3 Assignment.");
		System.out.println("[4] MP4 Assignment.");
		System.out.println("[5] Midterm Exam.");
		System.out.println("[6] Final Exam.");


		try {
			selection = in.nextInt();
		} catch (InputMismatchException e) {
			System.out.println(e);
		} finally {
			in.nextLine();
		}

		switch (selection) {
			case 1:
				selectionString = "mp1";
				break;
			case 2:
				selectionString = "mp2";
				break;
			case 3:
				selectionString = "mp3";
				break;
			case 4:
				selectionString = "mp4";
				break;
			case 5:
				selectionString = "midterm_exam";
				break;
			case 6:
				selectionString = "final_exam";
		}



		try (Connection conn = PowerSchool.getConnection()) {
			PreparedStatement stmt = conn.prepareStatement("SELECT * FROM courses INNER JOIN course_grades ON courses.course_id = course_grades.course_id INNER JOIN students ON students.student_id = course_grades.student_id WHERE students.student_id = ? AND courses.course_no = ?");
			stmt.setInt(1, this.getStudentId());
			stmt.setString(2, course_nos.get(input - 1));
			try (ResultSet rs = stmt.executeQuery()) {
				System.out.print("\n");
				while (rs.next()) {
					System.out.println(rs.getInt("assignment_id") + ". " + rs.getString("title"));
				}
			}
		} catch (SQLException e) {
			System.out.println(e);
		}
	}

  public void viewCourseGrades() {
		System.out.print("\n");
		try (Connection conn = PowerSchool.getConnection()) {
			PreparedStatement stmt = conn.prepareStatement(QueryUtils.GET_STUDENT_COURSES);
			stmt.setInt(1, this.getStudentId());
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					System.out.println(rs.getString("title") + " / " + rs.getInt("grade"));
				}
			}
		} catch (SQLException e) {
			System.out.println(e);
		}
	}

}
