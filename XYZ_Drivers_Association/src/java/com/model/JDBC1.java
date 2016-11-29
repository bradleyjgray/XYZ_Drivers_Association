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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

    String status = "APPLIED";
    float membershipFee = 0.00f;
    float balance = 0.00f;

    /**
     * @param args the command line arguments
     * @throws java.sql.SQLException
     */
    public void connect(Connection con) throws SQLException {

        connection = con;
    }

    public boolean usrExists(String usr) {
        boolean bool = false;

        try {
            select("SELECT id FROM users WHERE ='" + usr + "'");
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
    
    public int claimCounter(String userID) {
        int count = 0;
        
        select("SELECT * FROM Claims WHERE mem_id='" + userID + "'");
        
        
        if (result != null) {  
            try {
                result.beforeFirst();
                result.last();  
                count = result.getRow();
            } catch (SQLException ex) {
                Logger.getLogger(JDBC1.class.getName()).log(Level.SEVERE, null, ex);
            }
        }  
        
        return count;
    }

    private ArrayList resultList() throws SQLException {
        ArrayList resultList = new ArrayList<>();

        //get column count from result set data
        int columns = result.getMetaData().getColumnCount();
        while (result.next()) {
            String[] entry = new String[columns];
            for (int i = 1; i < columns; i++) {
                entry[i - 1] = result.getString(i);

            }
            entry[columns - 1] = String.valueOf(result.getFloat(columns));
            resultList.add(entry);
        }
        return resultList;
    }

    private String resultTable(ArrayList entries) {
        StringBuilder sb = new StringBuilder();
        String[] row;

        if (!entries.isEmpty()) {
            sb.append("<table border=\"3\">");
            for (Object e : entries) {
                sb.append("<tr>");
                row = (String[]) e;
                for (String entry : row) {
                    sb.append("<td>");
                    sb.append(" " + entry + " ");
                    sb.append("</td>");
                }
                sb.append("</tr>\n");
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

    public boolean addUsr(String name, String pass) {

        PreparedStatement pStatement = null;
        String status = "APPLIED";

        try {
            pStatement = connection.prepareStatement("insert into users(id, password, status) values (?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);
            pStatement.setString(1, name);
            pStatement.setString(2, pass);
            pStatement.setString(3, status);
            pStatement.executeUpdate();

            pStatement.close();
            System.out.println("USER ADDED!");
        } catch (SQLException ex) {
            Logger.getLogger(JDBC1.class.getName()).log(Level.SEVERE, null, ex);
        }

        return false;
    }

    public void updatePwd(String[] str) {
        PreparedStatement pStatement = null;

        try {
            pStatement = connection.prepareStatement("Update Users Set password=? where id=?", PreparedStatement.RETURN_GENERATED_KEYS);
            pStatement.setString(0, str[0]);
            pStatement.setString(1, str[1]);
            pStatement.executeUpdate();

            pStatement.close();
            System.out.println("Updated.");
        } catch (SQLException e) {
            System.out.println("err" + e);
        }
    }

    public void delete(String user) {
        String del = "DELETE FROM users WHERE username ='" + user.trim() + "'";

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

    public String genUsr(String name) {

        String usrName = null;

        String[] nameSplit = name.split(" ");

        String[] charSplit = name.split("");

        usrName = charSplit[0].toLowerCase() + charSplit[1].toLowerCase() + "-" + nameSplit[1].toLowerCase();

        /*while (usrExists(usrName) == true) {
            Random random = new Random();
            int rand = random.nextInt(100 - 0) + 0;
            String randNum = Integer.toString(rand);
            usrName = usrName + randNum;
        }*/
        return usrName;
    }

    public String genPass(String dob) {
        String pass = dob;

        if (pass.contains("/")) {
            pass = dob.replace("/", "-");
            String[] dateEdit = pass.split("-");
            String[] year = dateEdit[2].split("");
            String newDate = dateEdit[0] + dateEdit[1] + year[2] + year[3];
            pass = newDate;
        }

        return pass;
    }

    public float setMemberFee(float newFee) {
        membershipFee = newFee;

        return membershipFee;
    }

    public void createMember(String id, String name, String addr, String dob_String, String status) throws SQLException {

        PreparedStatement pStatement = null;

        dob_String = dob_String.replace("/", "-");
        String[] split = dob_String.split("-");
        dob_String = split[2] + "-" + split[1] + "-" + split[0];

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date dor = new Date();
        Date dob = null;

        try {
            dob = dateFormat.parse(dob_String);
        } catch (ParseException ex) {
            Logger.getLogger(JDBC1.class.getName()).log(Level.SEVERE, null, ex);
        }

        String dateReg;

        balance = setMemberFee(10);

        try {
            dateReg = dateFormat.format(dor);
            dor = dateFormat.parse(dateReg);
        } catch (ParseException ex) {
            Logger.getLogger(JDBC1.class.getName()).log(Level.SEVERE, null, ex);
        }

        java.sql.Date sqlDOB = new java.sql.Date(dob.getTime());
        java.sql.Date sqlDOR = new java.sql.Date(dor.getTime());

        try {
            pStatement = connection.prepareStatement("insert into Members (id, name, address, dob, dor, status, balance) values (?,?,?,?,?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS);
            pStatement.setString(1, id);
            pStatement.setString(2, name);
            pStatement.setString(3, addr);
            pStatement.setDate(4, (java.sql.Date) sqlDOB);
            pStatement.setDate(5, (java.sql.Date) sqlDOR);
            pStatement.setString(6, status);
            pStatement.setFloat(7, balance);
            pStatement.executeUpdate();

            pStatement.close();
            System.out.println("1 line added.");
        } catch (SQLException e) {
            System.out.println("FAILED to INSERT MEMBER!" + e);
        }

    }

    public String suspendMember(String userId) throws SQLException {

        //String query = "SELECT * from Members where id='" + userId + "'";
        PreparedStatement pStatement = null;

        //select(query);
        try {
            pStatement = connection.prepareStatement(" UPDATE Members SET status =? where id=?", PreparedStatement.RETURN_GENERATED_KEYS);
            pStatement.setString(1, "SUSPENDED");
            pStatement.setString(2, userId);
            pStatement.executeUpdate();

            pStatement.close();

            pStatement = connection.prepareStatement(" UPDATE Users SET status =? where id=?", PreparedStatement.RETURN_GENERATED_KEYS);
            pStatement.setString(1, "SUSPENDED");
            pStatement.setString(2, userId);
            pStatement.executeUpdate();

            pStatement.close();
            System.out.println("USER SUSPENDED!");
        } catch (SQLException e) {
            System.out.println("FAILED TO SUSPEND USER");
            return " :: MEMBERSHIP SUSPEND FAILED!";
        }
        return " SUSPENDED";
    }

    public String appliedToMember(String id_user) throws SQLException {

        String query = "SELECT * from users where id='" + id_user + "'";
        PreparedStatement pStatement = null;

        try {
            pStatement = connection.prepareStatement("UPDATE users SET status=? WHERE id=?", PreparedStatement.RETURN_GENERATED_KEYS);
            pStatement.setString(1, "APPROVED");
            pStatement.setString(2, id_user);
            pStatement.executeUpdate();

            pStatement.close();

            pStatement = connection.prepareStatement("UPDATE Members SET status=? WHERE id=?", PreparedStatement.RETURN_GENERATED_KEYS);
            pStatement.setString(1, "APPROVED");
            pStatement.setString(2, id_user);
            pStatement.executeUpdate();

            pStatement.close();

            System.out.println("1 line updated across 2 tables.");
        } catch (SQLException e) {
            System.out.println("FAILED to UPDATE MEMBER STATUS! " + e);
            return "MEMBERSHIP UPGRADE FAILED!";
        }

        return " IS NOW AN APPROVED MEMBER.";
    }

    public void makePayment(String memId, float amount, String payType) {

        //paytype: EITHER BALANCE (Balance) or MEMBERSHIP (Membership)
        PreparedStatement pStatement = null;

        Date today = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        String datePayment = null;

        try {
            datePayment = dateFormat.format(today);
            today = dateFormat.parse(datePayment);
        } catch (ParseException ex) {
            Logger.getLogger(JDBC1.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            pStatement = connection.prepareStatement("INSERT INTO payments VALUES (?,?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS);
            pStatement.setString(1, memId);
            pStatement.setString(2, payType);
            pStatement.setFloat(3, amount);
            pStatement.setDate(4, (java.sql.Date) today);

            pStatement.close();
            System.out.println("1 line added.");
        } catch (SQLException ex) {
            Logger.getLogger(JDBC1.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public int yearlyClaimCount(String memId){
        int count = 0;
        
        select("SELECT * FROM Claims WHERE mem_id='" + memId + "'");
        
        
        if (result != null) {  
            try {
                result.beforeFirst();
                result.last();  
                count = result.getRow();
            } catch (SQLException ex) {
                Logger.getLogger(JDBC1.class.getName()).log(Level.SEVERE, null, ex);
            }
        }  
        
        return count;
    }

    public int claimCount(String memId) {

        //select from DB
        String query = "SELECT * from Claims where mem_id='" + memId + "'";
        select(query);

        //format todays date
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date today = new Date();

        //get date one year ago
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -1);

        //for date conversion/parsing
        String todayDate = null;

        int claimCount = 0;

        try {
            todayDate = dateFormat.format(today);
            today = dateFormat.parse(todayDate);
        } catch (ParseException ex) {
            Logger.getLogger(JDBC1.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            while (result.next()) {
                String memberId = result.getString("mem_ID");
                Date claimDate = (Date) result.getDate("date");
                String status = result.getString("status");
                //check if date is after 12 months ago and before today
                if (claimDate.after(calendar.getTime()) && claimDate.before(today)) {
                    if (memId.equals(memberId)) {
                        if (status.equals("ACCEPTED")) {
                            claimCount++;
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(JDBC1.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("RESULT SET EMPTY!");
        }
        return claimCount;
    }

    public String respondClaim(String claimId, String response) throws SQLException {

        //CLAIMS MADE SET AS 'PENDING' STATUS.
        PreparedStatement pStatement = null;

        String query = "SELECT * from Claims where id='" + claimId + "'";
        String mem_id = null;
        String claimStatus = null;

        select(query);

        while (result.next()) {
            mem_id = result.getString("mem_id");
            claimStatus = result.getString("status");
            break;
        }
        
        if (!claimStatus.equals(response)){

        if (response.equals("ACCEPTED") && claimCount(mem_id) >= 2) {
            response = "REJECTED";
            try {
                    pStatement = connection.prepareStatement("UPDATE claims SET status=? WHERE id=?", PreparedStatement.RETURN_GENERATED_KEYS);
                    pStatement.setString(1, response);
                    pStatement.setString(2, claimId);
                    pStatement.executeUpdate();

                    pStatement.close();
                    System.out.println("1 line updated.");
                } catch (SQLException ex) {
                    Logger.getLogger(JDBC1.class.getName()).log(Level.SEVERE, null, ex);
                }
            return "CLAIM REJECTED:: USER ALREADY HAS 2 ACCEPTED CLAIMS IN THE LAST 12 MONTHS!";
        }

        if (response != null) {
            if (response.equals("ACCEPTED") || response.equals("REJECTED")) {
                try {
                    pStatement = connection.prepareStatement("UPDATE claims SET status=? WHERE id=?", PreparedStatement.RETURN_GENERATED_KEYS);
                    pStatement.setString(1, response);
                    pStatement.setString(2, claimId);
                    pStatement.executeUpdate();

                    pStatement.close();
                    System.out.println("1 line updated.");
                } catch (SQLException ex) {
                    Logger.getLogger(JDBC1.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                return "CLAIM NOT AVAILABLE:: Please check claim number!";
            }
        } else {
            System.out.println("Please enter valid claim response: ACCEPTED/REJECTED");
        }
        }
        else {
            return " RESPONSE IDENTICAL TO ENTRY :: NO CHANGE!";
        }
        return "CLAIM " + response;
    }

    public void makeClaim(String memId, String rationale, float amount) {

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date today = new Date();

        String claimDate = null;
        String status = "PENDING";

        PreparedStatement pStatement = null;

        try {
            claimDate = dateFormat.format(today);
            today = dateFormat.parse(claimDate);
        } catch (ParseException ex) {
            Logger.getLogger(JDBC1.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            pStatement = connection.prepareStatement("INSERT INTO Claims VALUES (?,?,?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS);
            pStatement.setString(1, memId);
            pStatement.setDate(2, (java.sql.Date) today);
            pStatement.setString(3, rationale);
            pStatement.setString(4, status);
            pStatement.setFloat(5, amount);

            pStatement.close();
            System.out.println("1 line added.");
        } catch (SQLException ex) {
            Logger.getLogger(JDBC1.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

//    public ArrayList listMemberClaims(String memId) throws SQLException {
//
//        String query = "SELECT * from Claims where mem_id='" + memId + "'";
//
//        select(query);
//
//        ArrayList memberClaims = new ArrayList<>(resultList());
//
//        return memberClaims;
//    }
    public float calcMembershipFee() {

        String query = "SELECT * from Claims";
        String memQuery = "Select * from Members";

        float amount = 0.0f;
        int memberCount = 0;

        select(query);

        //format todays date
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date today = new Date();

        //get date one year ago
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -1);

        //for date conversion/parsing
        String todayDate = null;

        try {
            todayDate = dateFormat.format(today);
            today = dateFormat.parse(todayDate);
        } catch (ParseException ex) {
            Logger.getLogger(JDBC1.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            while (result.next()) {
                Date date = result.getDate("date");
                if (date.after(calendar.getTime()) && date.before(today)) {
                    amount += result.getFloat("amount");
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(JDBC1.class.getName()).log(Level.SEVERE, null, ex);
        }

        select(memQuery);

        try {
            while (result.next()) {
                memberCount++;
            }
        } catch (SQLException ex) {
            Logger.getLogger(JDBC1.class.getName()).log(Level.SEVERE, null, ex);
        }

        membershipFee = (amount / memberCount);

        return membershipFee;
    }

    public String authLogin(String user, String pass) {

        String query = "select * from users where id='" + user + "'";

        String authKey = null;

        select(query);

        try {
            while (result.next()) {
                String pswd = result.getString("password");
                String status = result.getString("status");
                if (pass.equals(pswd)) {
                    authKey = status;
                } else {
                    authKey = "failed";
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(JDBC1.class.getName()).log(Level.SEVERE, null, ex);
            authKey = "failed!";
            return authKey;
        }

        return authKey;
    }

    public String doList(String lookUp, String where) {

        String resultTbl = null;
        String query = null;

        if (where.equals("*")) {
            query = "SELECT * from " + lookUp;
        } else {
            query = "SELECT * from " + lookUp + " WHERE " + where + "";
        }
        select(query);
        try {
            resultTbl = resultTable(resultList());
        } catch (SQLException ex) {
            Logger.getLogger(JDBC1.class.getName()).log(Level.SEVERE, null, ex);
        }
        return resultTbl;
    }

}
