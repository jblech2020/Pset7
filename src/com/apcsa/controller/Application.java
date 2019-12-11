package com.apcsa.controller;

import java.util.Scanner;
import com.apcsa.data.PowerSchool;
import com.apcsa.model.User;

public class Application {

    private Scanner in;
    private User activeUser;

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

            if (login(username, password)) {
                this.activeUser = this.activeUser.isAdministrator()
                    ? PowerSchool.getAdministrator(this.activeUser) : this.activeUser.isTeacher()
                    ? PowerSchool.getTeacher(this.activeUser) : this.activeUser.isStudent()
                    ? PowerSchool.getStudent(this.activeUser) : this.activeUser.isRoot()
                    ? this.activeUser : null;

                if (isFirstLogin() && !this.activeUser.isRoot()) {
                	changePassword(true);
                }

                // create and show the user interface
                //
                // remember, the interface will be difference depending on the type
                // of user that is logged in (root, administrator, teacher, student)
                
                if (this.activeUser.isStudent()) {
                	studentUI();
                } else if (this.activeUser.isTeacher()) {
                	teacherUI();
                } else if (this.activeUser.isRoot()) {
                	rootUI();
                } else if (this.activeUser.isAdministrator()) {
                	adminUI();
                }
                
            } else {
                System.out.println("\nInvalid username and/or password.");
            }
        }
    }
    
    /**
     * Displays the student UI
     * 
     * 
     */
    
    public void studentUI() {
    	
    }
    
    /**
     * Displays the teacher UI
     * 
     * 
     */
    
    public void teacherUI() {
    	
    }
    
    /**
     * Displays the root UI
     * 
     * 
     */
    
    public void rootUI() {
    	
    }
    
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


   /**
    * Logs the user out of their account.
    *
    *
    */

    public void logout(){
        activeUser = null;
        Application.startup();
    }

    /**
     * Prompts user for their initial password, and a new password and updates the account.
     * If it is the user's first login, it won't prompt for the old login
     *
     * @return their new account information
     */

    public void changePassword(boolean firstLogin){
    	  String oldPassword = null;
    	  if (firstLogin) {
		      while (oldPassword != activeUser.getPassword()){
		    	  System.out.print("Enter your current password: ");
		    	  oldPassword = this.in.nextLine();
		      }
    	  }
	
    	  System.out.print("Enter your new password: ");
    	  String newPassword = this.in.nextLine();
    	  activeUser.setPassword(newPassword);
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
