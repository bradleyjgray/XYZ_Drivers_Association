/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.model;

import static java.lang.Math.random;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

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
     * @throws java.sql.SQLException
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

    public void connect(Connection con) {
        connection = con;
    }

    public boolean usrExists(String usr) {
        boolean bool = false;

        try {
            select("select username from users where username='" + usr);
            if (result.next()) {
                System.out.println("EXISTS");
                bool = true;
            } else {
                System.out.println("NON-EXISTENT");
                bool = false;
            }
        } catch (SQLException e) {
            System.out.println("err" + e);
        }
        return bool;
    }

    private void select(String dbQuery) {

        try {
            statement = connection.createStatement();
            result = statement.executeQuery(dbQuery);
        } catch (SQLException e) {
            System.out.println("err" + e);
        }
    }
    
    private ArrayList resultList() throws SQLException{
        ArrayList resultList = new ArrayList<>();
        
        if (!resultList.isEmpty()){
            resultList.clear();
        }
        //get column count from result set data
        int columns = result.getMetaData().getColumnCount();
        while (result.next()){
            String[] entry = new String[columns];
            for (int i = 0; i < columns; i++) {
                entry[i] = result.getString(i);
            }
            resultList.add(entry);
        }
        return resultList; 
    }
    
    private String resultTable(ArrayList entries){
        StringBuilder sb = new StringBuilder();
        String[] row;
        
        if (!entries.isEmpty()){
            sb.append("< table border=\"6\">");
            for (Object e : entries) {
                sb.append("<tr>");
                row = (String[]) e;
                    for (String entry : row){
                        sb.append("<td>");
                        sb.append(entry);
                        sb.append("</td>");
                    }
                sb.append("</tr>");
            }
            sb.append("</table>");
        } else {
            System.out.println("ArrayList :: ENTRIES :: is EMPTY!");   
        }
        return sb.toString(); 
    }

    public void insert(String[] str) {
        PreparedStatement pStatement = null;

        try {
            pStatement = connection.prepareStatement("INSERT INTO Users VALUES (?,?)", PreparedStatement.RETURN_GENERATED_KEYS);
            pStatement.setString(0, str[0]);
            pStatement.setString(1, str[1]);
            pStatement.executeUpdate();

            pStatement.close();
            System.out.println("Line added.");
        } catch (SQLException e) {
            System.out.println("err" + e);
        }
    }    
    
    public boolean addUsr(String[] name, String pass){
       
        PreparedStatement pStatement = null;
        String usr = genUsr(name);
        String pswd = genPass(pass);
        String status = "APPLIED";
       
        try{
             pStatement = connection.prepareStatement("INSERT INTO Users VALUES (?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);
             pStatement.setString(0, usr);
             pStatement.setString(1, pswd);
             pStatement.setString(2, status);
             
             pStatement.close();
             System.out.println("USR ADDED!");
         } catch (SQLException ex) {
            Logger.getLogger(JDBC1.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return false;
    }
    

    public void updatePwd(String[] str) {
        PreparedStatement pStatement = null;

        try {
            pStatement = connection.prepareStatement("Update Users Set password=? where username=?", PreparedStatement.RETURN_GENERATED_KEYS);
            pStatement.setString(0, str[0]);
            pStatement.setString(1, str[1]);
            pStatement.executeUpdate();

            pStatement.close();
            System.out.println("Line added.");
        } catch (SQLException e) {
            System.out.println("err" + e);
        }
    }

    public void delete(String user) {
        String del = "DELETE FROM Users " + "WHERE username = '" + user.trim() + "'";

        try {
            statement = connection.createStatement();
            statement.executeUpdate(del);
        } catch (SQLException e) {
            System.out.println("DEL FAILED: " + e);
        }
    }

    public void closeAll() {
        try {
            result.close();
            statement.close();
        } catch (SQLException e) {
            System.out.println("CLOSE FAILED:: " + e);
        }
    }

    public String genUsr(String[] name) {

        String usrName = null;

        String[] fName = name[0].split("");

        usrName = fName[0] + name[1];

        while (usrExists(usrName) == true) {
            Random random = new Random();
            int rand = random.nextInt(100 - 0) + 0;
            String randNum = Integer.toString(rand);
            usrName = usrName + randNum;
        }
        System.out.println("Username is: " + usrName);
        return usrName;
    }

    public String genPass(String dob) {
        String pass = null;
        
        Date date = new Date();
        
        try{
            Date dobDate = new SimpleDateFormat("dd/mm/yy").parse(dob);        
            pass = dobDate.toString();
        } catch (ParseException e){
            System.out.println("Incorrect Date Format!!");
        }
        return pass;
    }

}
