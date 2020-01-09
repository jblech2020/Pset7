package com.apcsa.model;

import java.sql.*;
import java.util.*;
import com.apcsa.data.*;
import com.apcsa.controller.*;
import com.apcsa.model.User;

public class Teacher extends User {

    private int teacherId;
    private int departmentId;
    private String firstName;
    private String lastName;
    private String departmentName;

    /**
     * Teacher constructor with only a ResultSet and user.
     * @param user user (in most cases active user)
     * @param rs ResultSet
     * @throws SQLException
     */
    
    public Teacher(User user, ResultSet rs) throws SQLException {
    	super(user);

    	this.teacherId = rs.getInt("teacher_id");
    	this.departmentId = rs.getInt("department_id");
    	this.firstName = rs.getString("first_name");
        this.lastName = rs.getString("last_name");
    }

    /**
     * Teacher constructor with only a ResultSet.
     * @param rs ResultSet
     * @throws SQLException
     */
    
    public Teacher(ResultSet rs) throws SQLException {
		super(rs.getInt("user_id"), rs.getString("account_type"), rs.getString("username"), rs.getString("auth"), rs.getString("last_login"));
		this.teacherId = rs.getInt("teacher_id");
		this.firstName = rs.getString("first_name");
		this.lastName = rs.getString("last_name");
        this.departmentName = rs.getString("title");
        this.departmentId = rs.getInt("department_id");
	}

    public String getFirstName() {
    	return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public String getDepartmentName() {
        return this.departmentName;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public int getTeacherId() {
        return teacherId;
    }

    /*
     * METHODS THAT DO ALL DATABASE MANIPULATION
     */

     /**
      * Method that checks students enrolled in a course and displays either their active grade
      * or no grade if they currently have no assignments.
      * @param in the Scanner
      */

     public void enrollment(Scanner in) {

        int input = 0;
        boolean assignments = false;
        ArrayList<String> course_nos = getTeacherCourseList();

		try {
			input = in.nextInt();
		} catch (InputMismatchException e) {
			System.out.println("\nYour input was invalid. Please try again.");
		} finally {
			in.nextLine();
        }

        System.out.print("\n");
        try (Connection conn = PowerSchool.getConnection()) {
             PreparedStatement stmt = conn.prepareStatement(QueryUtils.GET_STUDENT_ENROLLMENT_BY_COURSE_NO);
             stmt.setString(1, course_nos.get(input - 1));
             try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    //Checks to see if assignments exist, as if they do, the grade must be shown.
                    //If they don't exist, just "--" is put in place of the grade.
                    try (Connection conn2 = PowerSchool.getConnection()) {
                        PreparedStatement stmt2 = conn2.prepareStatement("SELECT * FROM assignment_grades WHERE student_id = ?");
                        stmt2.setInt(1, rs.getInt("STUDENT_ID"));
                        try (ResultSet rs2 = stmt2.executeQuery()) {
                            if (rs2.next()) {
                                assignments = true;
                            }
                        }
                    }

                    if (assignments) {
                        System.out.println(rs.getString("last_name") + ", " + rs.getString("first_name") + " / " + rs.getInt("grade"));
                    } else {
                        System.out.println(rs.getString("last_name") + ", " + rs.getString("first_name") + " / " + "--");
                    }

                assignments = false;
                }
             }
        } catch (SQLException e) {
            System.out.println(e);
        }


     }

     /**
      * Method that adds an assignment by asking for a course, marking period, and weight in points.
      * @param in the Scanner
      */
     public void addAssignment(Scanner in) {

        int courseInput = 0;
        ArrayList<String> course_nos = getTeacherCourseList();

        try {
			courseInput = in.nextInt();
		} catch (InputMismatchException e) {
			System.out.println("Your input was invalid. Please try again.");
		} finally {
			in.nextLine();
        }

        int mp = getMarkingPeriodSelection(in);

        try {
            addAssignmentHelper(in, mp, course_nos.get(courseInput - 1));
        } catch (SQLException e) {
            System.out.println(e);
        }

     }

     /**
      * Method that deletes an assignment by asking for a course, marking period, and assignment selection.
      * @param in
      */
     public void deleteAssignment(Scanner in) {

        int courseInput = 0;
        ArrayList<String> course_nos = getTeacherCourseList();

        try {
			courseInput = in.nextInt();
		} catch (InputMismatchException e) {
			System.out.println("\nYour input was invalid. Please try again.");
		} finally {
			in.nextLine();
        }

        int mp = getMarkingPeriodSelection(in);

        deleteAssignmentHelper(in, mp, course_nos.get(courseInput - 1));
     }

     public void enterGrade(Scanner in) {
        int courseInput = 0;
        ArrayList<String> course_nos = getTeacherCourseList();

        try {
			courseInput = in.nextInt();
		} catch (InputMismatchException e) {
			System.out.println("\nYour input was invalid. Please try again.");
		} finally {
			in.nextLine();
        }

        int mp = getMarkingPeriodSelection(in);

        enterGradeHelper(in, mp, course_nos.get(courseInput - 1));
     }

     /**
      * Function that changes both the password property of the object as well as the password in the database.
      * @param in the Scanner
      */

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

    /**
     * Method that obtains a course list as well as displays the list of courses.
     * @return an ArrayList of the course numbers (ex: EN4000).
     */
    private ArrayList<String> getTeacherCourseList() {
        System.out.print("\n");
        ArrayList<String> course_nos = new ArrayList<String>();

        int count = 1;

        try (Connection conn = PowerSchool.getConnection()) {
			PreparedStatement stmt = conn.prepareStatement(QueryUtils.GET_TEACHER_COURSES);
			stmt.setInt(1, this.getTeacherId());
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
        System.out.print("\n::: ");

        return course_nos;
    }

    /**
     * Obtains marking period selection.
     * @param in the Scanner
     * @return selection integer
     */
    private int getMarkingPeriodSelection(Scanner in) {
        int output = 0;
        do {
            System.out.println("\n[1] MP1 Assignment.");
            System.out.println("[2] MP2 Assignment.");
            System.out.println("[3] MP3 Assignment.");
            System.out.println("[4] MP3 Assignment.");
            System.out.println("[5] Midterm exam.");
            System.out.println("[6] Final exam.");
            System.out.print("\n::: ");

            try {
                output = in.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Your input was invalid. Please try again.\n");
            }
            in.nextLine();
        } while (output < 1 || output > 6);



        return output;
    }

    private void addAssignmentHelper(Scanner in, int mp, String title) throws SQLException {
        int isFinal = (mp == 6) ? 1 : 0;
        int isMidterm = (mp == 5) ? 1 : 0;
        int markingPeriod = (mp > 4) ? 0 : mp;
        String assignmentTitle = "";
        int pointValue = -1;

        System.out.print("\nAssignment Title: ");
        try {
            assignmentTitle = in.nextLine();
        } catch (InputMismatchException e) {
            System.out.println("Your input was invalid. Please try again.");
            addAssignmentHelper(in, mp, title);
        }

        System.out.print("\nPoint Value: ");
        while (pointValue > 100 || pointValue < 1) {
            try {
                pointValue = in.nextInt();
            } catch (InputMismatchException e){
                System.out.println("Incorrect input.");
            }
            in.nextLine();

            if (pointValue > 100 || pointValue < 1) {
                System.out.println("Point values must be between 1 and 100.");
            }
        }

        boolean intent = Utils.confirm(in, "\nAre you sure you want to create this assignment? (y/n) ");

        if (intent) {
            //get course id from title
            int course_id = this.getCourseIdFromTitle(title);

            //next follows generating an assignment id
            int assignment_id = Utils.generateAssignmentId();

            try (Connection conn = PowerSchool.getConnection()) {
                PreparedStatement stmt = conn.prepareStatement("INSERT INTO assignments (course_id, assignment_id, marking_period, is_midterm, is_final, title, point_value) VALUES (?, ?, ?, ?, ?, ?, ?)");
                stmt.setInt(1, course_id);
                stmt.setInt(2, assignment_id);
                stmt.setInt(3, markingPeriod);
                stmt.setInt(4, isMidterm);
                stmt.setInt(5, isFinal);
                stmt.setString(6, assignmentTitle);
                stmt.setInt(7, pointValue);

                stmt.executeUpdate();
            } catch (SQLException e) {
                System.out.println(e);
            }
        }
    }

    private void deleteAssignmentHelper(Scanner in, int mp, String title) {

        //get course id from title
        int course_id = this.getCourseIdFromTitle(title);


        ArrayList<String> assignments = new ArrayList<String>();
        ArrayList<Integer> assignmentPoints = new ArrayList<Integer>();
        String statement = !(mp > 4) ? "SELECT * FROM assignments WHERE course_id = ? AND marking_period = ?"
        : (mp == 5) ? "SELECT * FROM assignments WHERE course_id = ? AND is_midterm = 1" : "SELECT * FROM assignments WHERE course_id = ? AND is_final = 1";

        try (Connection conn = PowerSchool.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(statement);
            stmt.setInt(1, course_id);
            if (mp > 0 && mp < 5) {
                stmt.setInt(2, mp);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    assignments.add(rs.getString("title"));
                    assignmentPoints.add(rs.getInt("point_value"));
                }
            }

        } catch (SQLException e) {
            System.out.println(e);
        }


        if (assignments.size() != 0) {
            System.out.println("\nChoose an assignment. ");
            for (int i = 0; i < assignments.size(); i++) {
                System.out.printf("[%d] %s (%d pts)\n", i + 1, assignments.get(i), assignmentPoints.get(i));
            }

            int assignmentSelection = -1;

            while (assignmentSelection > assignments.size() || assignmentSelection < 0) {
                try {
                    assignmentSelection = in.nextInt();
                } catch (InputMismatchException e) {
                    System.out.println("\nYour input was invalid. Please try again.");
                    System.out.println("\nChoose an assignment. ");
                    for (int i = 0; i < assignments.size(); i++) {
                        System.out.printf("[%d] %s (%d pts)\n", i + 1, assignments.get(i), assignmentPoints.get(i));
                    }
                    System.out.print("\n");
                } finally {
                    in.nextLine();
                }
            }

            try (Connection conn = PowerSchool.getConnection()) {
                PreparedStatement stmt = conn.prepareStatement("DELETE FROM assignments WHERE course_id = ? AND title = ?");
                stmt.setInt(1, course_id);
                stmt.setString(2, assignments.get(assignmentSelection - 1));
                stmt.executeUpdate();
            } catch (SQLException e) {
                System.out.println(e);
            }

            System.out.printf("\nSuccessfully deleted %s.", assignments.get(assignmentSelection - 1));
        } else {
            System.out.println("\nNo assignments to show.");
        }

    }

    private void enterGradeHelper(Scanner in, int mp, String title) {
        //get course id from title
        int course_id = this.getCourseIdFromTitle(title);

        //maybe make into individual object?
        //this gets the assignment names, points, and ids and puts them into arrays
        ArrayList<String> assignments = new ArrayList<String>();
        ArrayList<Integer> assignmentPoints = new ArrayList<Integer>();
        ArrayList<Integer> assignmentIds = new ArrayList<Integer>();
        String statement = !(mp > 4) ? "SELECT * FROM assignments WHERE course_id = ? AND marking_period = ?"
        : (mp == 5) ? "SELECT * FROM assignments WHERE course_id = ? AND is_midterm = 1" : "SELECT * FROM assignments WHERE course_id = ? AND is_final = 1";

        try (Connection conn = PowerSchool.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(statement);
            stmt.setInt(1, course_id);
            if (mp > 0 && mp < 5) {
                stmt.setInt(2, mp);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    assignments.add(rs.getString("title"));
                    assignmentPoints.add(rs.getInt("point_value"));
                    assignmentIds.add(rs.getInt("assignment_id"));
                }
            }

        } catch (SQLException e) {
            System.out.println(e);
        }

        //this gets the specific assignment that the user wants
        if (assignments.size() != 0) {
            System.out.println("\nChoose an assignment. ");
            for (int i = 0; i < assignments.size(); i++) {
                System.out.printf("[%d] %s (%d pts)\n", i + 1, assignments.get(i), assignmentPoints.get(i));
            }

            int assignmentSelection = -1;

            while (assignmentSelection > assignments.size() || assignmentSelection < 0) {
                try {
                    assignmentSelection = in.nextInt();
                } catch (InputMismatchException e) {
                    System.out.println("\nYour input was invalid. Please try again.");
                    System.out.println("\nChoose an assignment. ");
                    for (int i = 0; i < assignments.size(); i++) {
                        System.out.printf("[%d] %s (%d pts)\n", i + 1, assignments.get(i), assignmentPoints.get(i));
                    }
                    System.out.print("\n");
                } finally {
                    in.nextLine();
                }
            }

            //gets the students in the course
            ArrayList<Student> studentsInCourse = new ArrayList<Student>();
            System.out.println("Choose a student: ");
            try (Connection conn  = PowerSchool.getConnection()) {
                PreparedStatement stmt = conn.prepareStatement(QueryUtils.GET_STUDENT_ENROLLMENT_BY_COURSE_ID);
                stmt.setInt(1, course_id);
                try (ResultSet rs = stmt.executeQuery()) {
                    int i = 1;
                    while (rs.next()) {
                        studentsInCourse.add(new Student(rs));
                        System.out.printf("[%d] %s, %s\n", i, rs.getString("last_name"), rs.getString("first_name"));
                        i++;
                    }
                }


            } catch (SQLException e) {
                System.out.println(e);
            }

            int studentSelection = 0;
            while (studentSelection > studentsInCourse.size() || studentSelection < 0) {
                try {
                    studentSelection = in.nextInt();
                } catch (InputMismatchException e) {
                    System.out.println("\nYour input was invalid. Please try again.");
                    System.out.println("Choose a student: ");
                    try (Connection conn  = PowerSchool.getConnection()) {
                        PreparedStatement stmt = conn.prepareStatement(QueryUtils.GET_STUDENT_ENROLLMENT_BY_COURSE_ID);
                        stmt.setInt(1, course_id);
                        try (ResultSet rs = stmt.executeQuery()) {
                            int i = 1;
                            while (rs.next()) {
                                studentsInCourse.add(new Student(rs));
                                System.out.printf("[%d] %s, %s\n", i, rs.getString("last_name"), rs.getString("first_name"));
                                i++;
                            }
                        }
                    } catch (SQLException f) {
                        System.out.println(f);
                    }
                } finally {
                    in.nextLine();
                }
            }


            ArrayList<Integer> currentGrades = new ArrayList<Integer>();
            String noGrade = "--";
            try (Connection conn = PowerSchool.getConnection()) {
                PreparedStatement stmt = conn.prepareStatement("SELECT * FROM assignment_grades WHERE student_id = ? AND course_id = ? AND assignment_id = ?");
                stmt.setInt(1, studentsInCourse.get(studentSelection).getStudentId());
                stmt.setInt(2, course_id);
                stmt.setInt(3, assignmentIds.get(assignmentSelection));
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        currentGrades.add(rs.getInt("points_earned"));
                    }
                }
            } catch (SQLException e) {
                System.out.println(e);
            }

            System.out.printf("Assignment: %s (%d pts)\n", assignments.get(assignmentSelection), assignmentPoints.get(assignmentSelection));
            System.out.printf("Student: %s, %s\n", studentsInCourse.get(studentSelection).getLastName(), studentsInCourse.get(studentSelection).getFirstName());
            System.out.printf("Current Grade: %s\n", (currentGrades.size() == 0) ? noGrade : Integer.toString(currentGrades.get(0)));

            System.out.println("New Grade: ");
        } else {
            System.out.println("\nNo assignments to show.");
        }



    }

    private int getCourseIdFromTitle(String title) {
        try (Connection conn = PowerSchool.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT course_id FROM courses WHERE course_no = ?");
            stmt.setString(1, title);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("course_id");
                }
            } catch (SQLException e) {
                System.out.println(e);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return -1;
    }
}
