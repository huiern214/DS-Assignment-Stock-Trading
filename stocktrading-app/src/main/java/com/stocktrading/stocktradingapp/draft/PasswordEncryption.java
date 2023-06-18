package com.stocktrading.stocktradingapp.draft;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Project/Maven2/JavaApp/src/main/java/${packagePath}/${mainClassName}.java to edit this template
 */

 /**
  *
  * @author abdullahiibrahim
  */
 import org.mindrot.jbcrypt.BCrypt;
 import java.util.Scanner;
 
 public class PasswordEncryption {
 
     public static String hashPassword(String plainPassword) {
         String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt());
         return hashedPassword;
     }
 
     public static boolean verifyPassword(String plainPassword, String hashedPassword) {
         return BCrypt.checkpw(plainPassword, hashedPassword);
     }
 
     public static void main(String[] args) {
         Scanner scanner = new Scanner(System.in);
 
         System.out.print("Enter a password: ");
         String userPassword = scanner.nextLine();
 
         // Hash the user's password
         String hashedPassword = hashPassword(userPassword);
 
         System.out.println("Original Password: " + userPassword);
         System.out.println("Hashed Password: " + hashedPassword);
 
         // Verify the user's password
         System.out.print("Enter the password to verify: ");
         String passwordToVerify = scanner.nextLine();
         boolean passwordMatch = verifyPassword(passwordToVerify, hashedPassword);
 
         System.out.println("Password Match: " + passwordMatch);
 
         scanner.close();
     }
 }
 
 