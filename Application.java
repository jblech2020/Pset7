package com.apcsa.controller;

import com.apcsa.model.Teacher;
import java.util.ArrayList;


import java.util.Scanner;
import com.apcsa.data.PowerSchool;
import com.apcsa.model.User;

public class Application {

    private Scanner in;
    private User activeUser;

    enum RootAction { PASSWORD, DATABASE, LOGOUT, SHUTDOWN }

    /**
     * Creates an instance of the Application class, which is responsible for interacting
     * with the user via the command line interface.
     */

    public Application() {
        this.in = new Scanner(System.in);

        try {
            PowerSchool.initialize(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Starts the PowerSchool application.
     */

    public void startup() {
        System.out.println("PowerSchool -- now for students, teachers, and school administrators!");

        // continuously prompt for login credentials and attempt to login

        while (true) {
            System.out.print("\nUsername: ");
            String username = this.in.next();

            System.out.print("Password: ");
            String password = this.in.next();

            // if login is successful, update generic user to administrator, teacher, or student

            try {
                if (login(username, password)) {
                    activeUser = activeUser.isAdministrator()
                        ? PowerSchool.getAdministrator(activeUser) : activeUser.isTeacher()
                        ? PowerSchool.getTeacher(activeUser) : activeUser.isStudent()
                        ? PowerSchool.getStudent(activeUser) : activeUser.isRoot()
                        ? activeUser : null;

                    if (isFirstLogin() && !activeUser.isRoot()) {
                    	changePassword(true);
                    }

                    createAndShowUI();
                } else {
                    System.out.println("\nInvalid username and/or password.");
                }
            } catch (Exception e) {
                shutdown(e);
            }
        }
    }

    /**
     * Displays an user type-specific menu with which the user
     * navigates and interacts with the application.
     */

    public void createAndShowUI() {
        System.out.println("\nHello, again, " + activeUser.getFirstName() + "!");

		if (this.activeUser.isStudent()) {
	    	studentUI();
	    } else if (this.activeUser.isTeacher()) {
	    	teacherUI();
	    } else if (this.activeUser.isRoot()) {
	    	rootUI();
	    } else if (this.activeUser.isAdministrator()) {
	    	adminUI();
	    }
    }

    /**
     * Displays the student UI
     *
     *
     */

    private void studentUI() {

    }

    /**
     * Displays the teacher UI
     *
     *
     */

    private void teacherUI() {

    }

    /////// ROOT METHODS //////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Displays the root UI
     *
     *
     */

    private void rootUI() {
        while (activeUser != null) {
            switch (getRootMenuSelection()) {
                case PASSWORD: resetPassword(); break;
                case DATABASE: factoryReset(); break;
                case LOGOUT: logout(); break;
                case SHUTDOWN: shutdown(); break;
                default: System.out.println("\nInvalid selection."); break;
            }
        }
    }

    /*
     * Retrieves a root user's menu selection.
     *
     * @return the menu selection
     */

    private RootAction getRootMenuSelection() {
        System.out.println();

        System.out.println("[1] Reset user password.");
        System.out.println("[2] Factory reset database.");
        System.out.println("[3] Logout.");
        System.out.println("[4] Shutdown.");
        System.out.print("\n::: ");

        switch (Utils.getInt(in, -1)) {
            case 1: return RootAction.PASSWORD;
            case 2: return RootAction.DATABASE;
            case 3: return RootAction.LOGOUT;
            case 4: return RootAction.SHUTDOWN;
            default: return null;
        }
     }

    /**
     * Prompts user for their initial password, and a new password and updates the account.
     * If it is the user's first login, it won't prompt for the old login
     *
     * @return their new account information
     */

    public void changePassword(boolean firstLogin){
    	//for root user, need to make it so you can change the password of any user

    	  String oldPassword = null;
    	  if (!firstLogin) {
		      while (oldPassword != activeUser.getPassword()){
		    	  System.out.print("Enter your current password: ");
		    	  oldPassword = this.in.nextLine();
		      }
    	  }

    	  System.out.print("Enter your new password: ");
    	  String newPassword = this.in.nextLine();
    	  activeUser.setPassword(newPassword);
    }

    /*
     * Resets the database to its factory settings.
     */

    private void factoryReset() {
        //
        // ask root user to confirm intent to reset the database
        //
        // if confirmed...
        //      call database initialize method with parameter of true
        //      print success message
        //
    }

    /////// ADMIN METHODS //////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Displays the administrator UI
     *
     *
     */

    public void adminUI() {

    }


    /**
     * Logs in with the provided credentials.
     *
     * @param username the username for the requested account
     * @param password the password for the requested account
     * @return true if the credentials were valid; false otherwise
     */

    public boolean login(String username, String password) {
        activeUser = PowerSchool.login(username, password);

        return activeUser != null;
    }

    /**
     * Determines whether or not the user has logged in before.
     *
     * @return true if the user has never logged in; false otherwise
     */

    public boolean isFirstLogin() {
        return activeUser.getLastLogin().equals("0000-00-00 00:00:00.000");
    }

   /////// ALL USER METHODS //////////////////////////////////////////////////////////////////////////////////////////////////////

   /**
    * Logs the user out of their account.
    *
    *
    */

    public void logout(){
    	  //
        // ask root user to confirm intent to logout
        //
        // if confirmed...
        //
    	activeUser = null;
    	Application app = new Application();

        app.startup();
     }

    /**
     * Resets a user's password.
     *
     * @param username the user's username
     */

    public static void changePassword(String username) {
        //
        // get a connection to the database
        // create a prepared statement (both of thses should go in a try-with-resources statement)
        //
        // insert parameters into the prepared statement
        //      - the user's hashed username
        //      - the user's plaintext username
        //
        // execute the update statement
        //
    }

    /*
     * Resets another user's password and last login timestamp.
     */

    //
    // upset the users table
    // two columns need to be updated
    //          - auth
    //          - last_login
    //
    // auth will be set to the hash of the user's username
    // last_login will be reverted to 0000-00-00 00:00:00.000
    //
    // only modify rows where username matches parameter provided

   /////// SHUTDOWN METHODS //////////////////////////////////////////////////////////////

    /*
     * Shuts down the application.
     *
     * @param e the error that initiated the shutdown sequence
     */

    private void shutdown(Exception e) {
        if (in != null) {
            in.close();
        }

        System.out.println("Encountered unrecoverable error. Shutting down...\n");
        System.out.println(e.getMessage());

        System.out.println("\nGoodbye!");
        System.exit(0);
    }

    /*
     * Releases all resources and kills the application.
     */

    private void shutdown() {
        System.out.println();

        if (Utils.confirm(in, "Are you sure? (y/n) ")) {
            if (in != null) {
                in.close();
            }

            System.out.println("\nGoodbye!");
            System.exit(0);
        }
    }

    /////// MAIN METHOD ///////////////////////////////////////////////////////////////////

    /*
     * Starts the PowerSchool application.
     *
     * @param args unused command line argument list
     */

    public static void main(String[] args) {
        Application app = new Application();

        app.startup();
    }
}