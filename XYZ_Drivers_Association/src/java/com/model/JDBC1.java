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
            select("SELECT username FROM users WHERE username='" + usr + "'");
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

    private ArrayList resultList() throws SQLException {
        ArrayList resultList = new ArrayList<>();

        //get column count from result set data
        int columns = result.getMetaData().getColumnCount();
        while (result.next()) {
            String[] entry = new String[columns];
            for (int i = 1; i < columns; i++) {
                entry[i - 1] = result.getString(i);
            }
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
        String usr = genUsr(name);
        String pswd = genPass(pass);
        String status = "APPLIED";

        try {
            pStatement = connection.prepareStatement("INSERT INTO Users VALUES (?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);
            pStatement.setString(0, usr);
            pStatement.setString(1, pswd);
            pStatement.setString(2, status);

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

        if(pass.contains("/")) {
            pass = dob.replace("/", "");
        }
        else
        {
            pass = dob.replace("-", "");
        }
        
        return pass;
    }

    public float setMemberFee(float newFee) {
        membershipFee = newFee;

        return membershipFee;
    }

    public void createMember(String id, String name, String addr, String dob_String, String status) throws SQLException {

        PreparedStatement pStatement = null;

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date dor = new Date();
        Date dob = new Date();

        String dateReg;

        balance = setMemberFee(10);

        try {
            dateReg = dateFormat.format(dor);
            dor = dateFormat.parse(dateReg);
            dob = dateFormat.parse(dob_String);
        } catch (ParseException ex) {
            Logger.getLogger(JDBC1.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            pStatement = connection.prepareStatement("INSERT INTO Members VALUES (?,?,?,?,?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS);
            pStatement.setString(0, id);
            pStatement.setString(1, name);
            pStatement.setString(2, addr);
            pStatement.setDate(3, (java.sql.Date) dob);
            pStatement.setDate(4, (java.sql.Date) dor);
            pStatement.setString(5, status);
            pStatement.setFloat(6, balance);

            pStatement.close();
            System.out.println("1 line added.");
        } catch (SQLException e) {
            System.out.println("FAILED to INSERT MEMBER!" + e);
        }

    }

    public void suspendMember(String userId) throws SQLException {

        //String query = "SELECT * from Members where id='" + userId + "'";
        PreparedStatement pStatement = null;

        //select(query);
        pStatement = connection.prepareStatement(" UPDATE Members SET status =? where id=?", PreparedStatement.RETURN_GENERATED_KEYS);
        pStatement.setString(0, "APPLIED");
        pStatement.setString(1, userId);

        pStatement.close();

        pStatement = connection.prepareStatement(" UPDATE Users SET status =? where id=?", PreparedStatement.RETURN_GENERATED_KEYS);
        pStatement.setString(0, "APPLIED");
        pStatement.setString(1, userId);

        pStatement.close();
        System.out.println("1 line updated across 2 tables.");
    }

    public String appliedToMember(String id_user) throws SQLException {

        String query = "SELECT * from users where id='" + id_user + "'";
        PreparedStatement pStatement = null;

        select(query);

        while (result.next()) {
            String id = result.getString("id");
            String pswd = result.getString("password");
            String status = result.getString("status");

            if (id_user.equals(id)) {
                try {
                    pStatement = connection.prepareStatement("Update users Set status =? where id=?", PreparedStatement.RETURN_GENERATED_KEYS);
                    pStatement.setString(0, "MEMBER");
                    pStatement.setString(1, id_user);

                    pStatement.close();

                    pStatement = connection.prepareStatement("Update Members Set status=? where id=?", PreparedStatement.RETURN_GENERATED_KEYS);
                    pStatement.setString(0, "MEMBER");
                    pStatement.setString(1, id_user);

                    pStatement.close();

                    System.out.println("1 line updated across 2 tables.");
                } catch (SQLException e) {
                    System.out.println("FAILED to UPDATE MEMBER STATUS! " + e);
                }
                break;
            }
        }
        return "2 lines updated.";
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
                String memberId = result.getString("memID");
                Date claimDate = result.getDate("date");
                //check if date is after 12 months ago and before today
                if (claimDate.after(calendar.getTime()) && claimDate.before(today)) {
                    if (memId.equals(memberId)) {
                        claimCount++;
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

        select(query);

        while (result.next()) {
            mem_id = result.getString("mem_id");
            break;
        }

        if (claimCount(mem_id) > 2) {
            return "REJECTED:: Two claims already made this year!";
        }

        if (response != null) {
            if (response.equals("ACCEPTED") || response.equals("REJECTED")) {
                try {
                    pStatement = connection.prepareStatement("Update Claims Set status=? where mem_id=?", PreparedStatement.RETURN_GENERATED_KEYS);
                    pStatement.setString(0, response);
                    pStatement.setString(1, claimId);

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
        return response;
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
