/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 *
 * @author Luke James
 */
public class JDBC1 {

    Connection connection = null;
    ResultSet result = null;
    Statement statement = null;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws SQLException {

        String str = "select * from users";
        String insert = "INSERT INTO `Users` (`username`, `password`)";
        String update = "UPDATE users(*) SET username&password ";
        String db = "MyDB";

        Jdbc jdbc = new Jdbc(str);
        Connection connection = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://hostinger.co.uk:3306/u667329521_xyz" + db.trim(), "u667329521_user", "Scarlet");
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Java Exception:" + e);
        }
        jdbc.connect(connection);

    }
    
    public void connect(Connection con){
        connection = con;
    }
    
    public boolean usrExists(String usr){
        boolean bool = false;
        
        try{
            select("Select user: " + usr);
           if (result.next()){
               System.out.println("EXISTS");
               bool = true;
           }
           else {
               System.out.println("NON-EXISTENT");
               bool = false;
           }
        }
        catch (SQLException e){
            System.out.println("err"+e);
        }
        return bool;
    }
    
    private void select (String dbQuery){
        
        try{
            statement = connection.createStatement();
            result = statement.executeQuery(dbQuery);
        }
        catch (SQLException e) {
            System.out.println("err"+e);
        }
    }
    
    public void insert (String[] str){
        PreparedStatement pStatement = null;
        
        try{
            pStatement = connection.prepareStatement("Insert USER VALUES ::", PreparedStatement.RETURN_GENERATED_KEYS);
            pStatement.setString(0, str[0]);
            pStatement.setString(1, str[1]);
            pStatement.executeUpdate();
        }
        catch (SQLException e){
            System.out.println("err"+e);
        }
        
    }

}
