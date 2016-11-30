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
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;

/**
 *
 * @author Luke James
 */
/**
 * CLASS: JDBC1 ROLE: TAKE IN PARAMETERS AND USE THEM TO RUN SQL QUERIES ON THE
 * XYZ ASSOCIATION DATABASE AND RETURN RESULTS TO CONTROLLER. INCLUDES: ALL
 * PROGRAM FUNCTIONALITY. LISTING, TABLES, CLAIMS, PAYMENTS, USER AUTHORISATION,
 * RUN QUERIES ETC
 *
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
    //CONNECTS TO DATABASE VIA LISTENER PARAMETER
    public void connect(Connection con) throws SQLException {

        connection = con;
    }

    /**
     *
     * @param usr
     * @return boolean
     *
     * METHOD RUNS SELECT QUERY ON DATABASE TO CHECK IF USER EXISTS.
     */
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

    /**
     *
     * @param dbQuery
     *
     * METHOD USES dbQuery PARAM AS DB QUERY STRING. USES STATEMENT FUNCTION TO
     * CREATE AND PUSH STATEMENT TO DATABASE TO RETRIEVE RESULTS WITHIN RESULT
     * SET.
     */
    private void select(String dbQuery) {

        try {
            statement = connection.createStatement();
            result = statement.executeQuery(dbQuery);
        } catch (SQLException e) {
            System.out.println("err" + e);
        }
    }

    /**
     *
     * @param userID
     * @param select
     * @return MEMBERS LIST (HTML TABLE AS STRING) WHERE USED ID MATCHES PARAMETER
     */
    public String getInfoForUser(String userID, String select) {

        String resultTbl = null;
        String query = null;

        query = "SELECT " + select + " FROM Members WHERE id='" + userID + "'";

        select(query);

        try {
            resultTbl = resultTable(resultList());
        } catch (SQLException ex) {
            Logger.getLogger(JDBC1.class.getName()).log(Level.SEVERE, null, ex);
        }
        return resultTbl;
    }
    /**
     * 
     * @param userID
     * @param select
     * @return CLAIMS LIST (HTML TABLE AS STRING)
     * 
     * METHOD RUNS QUERY ON DATABASE AND RETURNS RESULT SET AS HTML TABLE (AS STRING)
     */
    public String getClaimsForUser(String userID, String select) {

        String resultTbl = null;
        String query = null;

        query = "SELECT " + select + " FROM Claims WHERE mem_id='" + userID + "'";

        select(query);

        try {
            resultTbl = resultTable(resultList());
        } catch (SQLException ex) {
            Logger.getLogger(JDBC1.class.getName()).log(Level.SEVERE, null, ex);
        }
        return resultTbl;
    }

    /**
     * 
     * @param userID
     * @return int (number of claims)
     *  
     * QUERY THE DATABASE AND USE RESULT SET TO COUNT NUMBER OF CLAIMS FOR A MEMBER
     */
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

    /**
     * 
     * @return ARRYALIST (RESULT SET AS ARRAYLIST)
     * @throws SQLException 
     * 
     * PREPARATION FOR HTML TABLE, USE THIS METHOD TO PUT DATA INTO 
     * ROWS AND COLUMNS FOR TABLE
     */
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

    /**
     * 
     * @param entries
     * @return HTML TABLE AS STRING
     * 
     * METHOD RETURNS RESULT SET WITHIN ARRAYLIST AS HTML TABLE
     */
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

    /**
     * 
     * @param str 
     * 
     * METHOD INSERTS VALUES INTO DATABASE FROM STRING[]
     */
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

    /**
     * 
     * @param name
     * @param pass
     * @return 
     * 
     * INSERT USER INTO DATABASE (W/ PASSWORD AND MEMBERSHIP STATUS)
     */
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

    /**
     * 
     * @param str 
     * 
     * METHOD UPDATES PASSWORD FOR USER VIA PREPARED STATEMENT
     */
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

    /**
     * 
     * @param user 
     * 
     * DELETES USER FROM USERS TABLE -- FUNCTIONALITY NOT IN SCOPE 
     * FOR CURRENT BUILD
     */
    public void delete(String user) {
        String del = "DELETE FROM users WHERE username ='" + user.trim() + "'";

        try {
            statement = connection.createStatement();
            statement.executeUpdate(del);
        } catch (SQLException e) {
            System.out.println("DEL FAILED: " + e);
        }
    }

    /**
     * SECURITY :: CLOSE RESULT SET AND STATEMENT CONNECTIONS
     */
    public void closeAll() {
        try {
            result.close();
            statement.close();
        } catch (SQLException e) {
            System.out.println("CLOSE FAILED:: " + e);
        }
    }

    /**
     * 
     * @param name
     * @return USERNAME (STRING)
     * 
     * METHOD SPLICES NAME TO CREATE USERNAME FOR USER/MEMBER
     */
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

    /**
     * 
     * @param dob
     * @return PASSWORD (STRING)
     * 
     * METHOD USES DATE OF BIRTH OF USER TO GENERATE PASSWORD (DDMMYY)
     */
    public String genPass(String dob) {
        String pass = dob;

        if (pass.contains("/")) {
            pass = dob.replace("/", "-");
            String[] dateEdit = pass.split("-");
            String[] year = dateEdit[2].split("");
            String newDate = dateEdit[0] + dateEdit[1] + year[3] + year[4];
            pass = newDate;
        }

        return pass;
    }

    /**
     * 
     * @param newFee
     * @return MEMBERSHIPFEE
     * 
     * METHOD SETS NEW MEMBERSHIP FEE BASED ON INPUT VALUE
     */
    public float setMemberFee(float newFee) {
        membershipFee = newFee;

        return membershipFee;
    }

    /**
     * 
     * @param id
     * @param name
     * @param addr
     * @param dob_String
     * @param status
     * @throws SQLException 
     * 
     * METHOD CREATES MEMBER IN MEMBERS TABLE WITH THE ABOVE PARAMETERS.
     * METHOD MUST WORK OUT CURRENT DATE FOR DOR (DATE OF REGISTRATION)
     * AND SET MEMBERS BALANCE AT 10 FOR THE JOINING FEE.
     */
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

    /**
     * 
     * @param userId
     * @return SUSPEND SUCCESS/FAILURE
     * @throws SQLException 
     * 
     * METHOD WILL SUSPEND A USER UPON ADMINS REQUEST.  MEMBER SUSPENDED WILL 
     * MATCH THE USER ID SELECTED WITHIN THE JSP VIEW.
     * METHOD WILL UPDATE MEMBERS AND USERS TABLE
     */
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

    /**
     * 
     * @param id_user
     * @return MEMBERSHIP UPDATE STATUS (STRING)
     * @throws SQLException 
     * 
     * UPON REQUEST OF ADMIN, METHOD WILL UPDATE USERS AND MEMBERS TABLE FOR MEMBER
     * TO SHOW UPDATED STATUS OF APPROVED ONCE INITIAL JOINING FEE HAS BEEN PAYED.
     * 
     */
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

    /**
     * 
     * @param id
     * @return 
     * 
     * METHOD DEPRECATED IN CURRENT SCOPE -- NO LONGER IN USE!!!
     */
    public String getPaymentID(String id) {
        String payments = "";

        String query = "SELECT id from payments WHERE mem_id='" + id + "'";

        select(query);

        try {
            while (result.next()) {
                String paymentID = (String) result.getString("id");

                payments = payments + paymentID + "-";
            }
        } catch (SQLException ex) {
            Logger.getLogger(JDBC1.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (result == null) {
            payments = "1";
        }

        return payments;
    }

    /**
     * 
     * @param paymentAmount
     * @param id
     * @return PAYMENT STATUS (STRING)
     * @throws SQLException 
     * 
     * METHOD WILL QUERY THE DATABASE FOR CURRENT USER BALANCE THEN UPDATE (REDUCE) THE VALUE
     * BY VALUE OF PAYMENT BEING MADE.  IF VALUE OF PAYMENT IS BIGGER THAN BALANCE,
     * USER MUST SELECT A NEW PAYMENT AMOUNTS - MUST NOT ALLOW -VE BALANCE AMOUNTS!
     */
    public String updateBalance(float paymentAmount, String id) throws SQLException {
        String paymentQuery = "SELECT amount FROM payments WHERE mem_id='" + id + "'";
        String balanceQuery = "SELECT balance FROM Members WHERE id='" + id + "'";

        //select(paymentQuery);
        float balanceAmount = 0;

//        try {
//            while (result.next()) {
//                paymentAmount = (float)result.getFloat("amount");
//            }
//        } catch (SQLException ex) {
//            Logger.getLogger(JDBC1.class.getName()).log(Level.SEVERE, null, ex);
//        }
        select(balanceQuery);

        try {
            while (result.next()) {
                balanceAmount = result.getFloat("balance");
            }
        } catch (SQLException ex) {
            Logger.getLogger(JDBC1.class.getName()).log(Level.SEVERE, null, ex);
        }

        //float remainingBalance = 0;
//        if(paymentAmount > balanceAmount) {
//            remainingBalance = 0;
//        }
//        else
//        {
//            remainingBalance = balanceAmount - paymentAmount;
//        }
        //String updateBalanceQuery = "UPDATE Members SET balance=" + remainingBalance + "WHERE id='" + id + "'";
        PreparedStatement ps = null;
        if (paymentAmount <= balanceAmount) {
            try {
                ps = connection.prepareStatement("UPDATE Members Set balance=? where id=?", PreparedStatement.RETURN_GENERATED_KEYS);
                ps.setFloat(1, balanceAmount - paymentAmount);
                ps.setString(2, id);
                ps.executeUpdate();

                ps.close();
                return " payment complete!";
            } catch (SQLException e) {
                System.out.println("UPDATE FAILED! " + e);
            }
        } else {
            return " payment too large!  Please enter a smaller payment amount!";
        }

//        try {
//            statement = connection.createStatement();
//            statement.executeUpdate(updateBalanceQuery);
//        } catch (SQLException e) {
//            System.out.println("UPDATE FAILED: " + e);
//        }
        return "PAYMENT ERROR!";
    }

    public void makePayment(String memId, float amount, String payType) {

        //paytype: EITHER BALANCE (Balance) or MEMBERSHIP (Membership)
        PreparedStatement pStatement = null;

        //Store today's date
        Date today = new Date();
        //Create date format
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        String datePayment = "";

        //Convert today's date into correct date format
        try {
            datePayment = dateFormat.format(today);
            today = dateFormat.parse(datePayment);
        } catch (ParseException ex) {
            Logger.getLogger(JDBC1.class.getName()).log(Level.SEVERE, null, ex);
        }

        //Convert java time into sql time
        java.sql.Date sqlDOP = new java.sql.Date(today.getTime());

        try {
            //Create prepared statement to insert information into the database
            pStatement = connection.prepareStatement("INSERT INTO payments (mem_id, type_of_payment, amount, date) VALUES (?,?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS);
            pStatement.setString(1, memId);
            pStatement.setString(2, payType);
            pStatement.setFloat(3, amount);
            pStatement.setDate(4, (java.sql.Date) sqlDOP);

            //Execute query
            pStatement.executeUpdate();

            //End statement
            pStatement.close();
            System.out.println("1 line added.");
        } catch (SQLException ex) {
            //If insert fails, throw an exception
            System.out.println("FAILED to INSERT PAYMENT!" + ex);
        }
    }

    public int yearlyClaimCount(String memId) {
        int count = 0;

        //Query database to select information from claims using the user ID
        select("SELECT * FROM Claims WHERE mem_id='" + memId + "'");
        //If result set contains data
        if (result != null) {
            //Count each row to find how many claims have been submitted
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

        //Select from DB
        String query = "SELECT * from Claims where mem_id='" + memId + "'";
        select(query);

        //Format todays date
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date today = new Date();

        //Get date one year ago
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -1);

        //For date conversion/parsing
        String todayDate = null;

        int claimCount = 0;

        try {
            //Convert date into correct format
            todayDate = dateFormat.format(today);
            today = dateFormat.parse(todayDate);
        } catch (ParseException ex) {
            Logger.getLogger(JDBC1.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            //While result has next value
            while (result.next()) {
                //Get member ID
                String memberId = result.getString("mem_ID");
                //Get the date of the claim
                Date claimDate = (Date) result.getDate("date");
                //Get the status of the claim
                String status = result.getString("status");
                //Check if date is after 12 months ago and before today
                if (claimDate.after(calendar.getTime()) && claimDate.before(today)) {
                    if (memId.equals(memberId)) {
                        //If status of claim is accepted, claimCount is incremented
                        if (status.equals("ACCEPTED")) {
                            claimCount++;
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            //If result set is empty throw an exception
            Logger.getLogger(JDBC1.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("RESULT SET EMPTY!");
        }
        return claimCount;
    }

    public String respondClaim(String claimId, String response) throws SQLException {

        //CLAIMS MADE SET AS 'PENDING' STATUS.
        PreparedStatement pStatement = null;

        //Query to get information on a claim using claim ID
        String query = "SELECT * from Claims where id='" + claimId + "'";
        String mem_id = null;
        String claimStatus = null;

        select(query);

        //While result has data
        while (result.next()) {
            //Get member ID
            mem_id = result.getString("mem_id");
            //Get claim status
            claimStatus = result.getString("status");
            break;
        }

        if (!claimStatus.equals(response)) {
            //If claim is accepted, but member has two or more claims
            if (response.equals("ACCEPTED") && claimCount(mem_id) >= 2) {
                //Third claim will be rejected
                response = "REJECTED";
                try {
                    //Prepared statement to update the status of claims for member
                    pStatement = connection.prepareStatement("UPDATE claims SET status=? WHERE id=?", PreparedStatement.RETURN_GENERATED_KEYS);
                    pStatement.setString(1, response);
                    pStatement.setString(2, claimId);
                    //Execute update statement
                    pStatement.executeUpdate();

                    pStatement.close();
                    System.out.println("1 line updated.");
                } catch (SQLException ex) {
                    Logger.getLogger(JDBC1.class.getName()).log(Level.SEVERE, null, ex);
                }
                //Return message saying claim is rejected if user already has two accepted claims in the last 12 months
                return "CLAIM REJECTED:: USER ALREADY HAS 2 ACCEPTED CLAIMS IN THE LAST 12 MONTHS!";
            }

            if (response != null) {
                //Update claims table if status is accepted or rejected
                if (response.equals("ACCEPTED") || response.equals("REJECTED")) {
                    try {
                        //Prepare statement to update claims for member
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
        } else {
            return " RESPONSE IDENTICAL TO ENTRY :: NO CHANGE!";
        }
        return "CLAIM " + response;
    }

    public String getClaimsID(String id) {
        String claims = "";

        //Query to get IDs of claims
        String query = "SELECT id from Claims WHERE mem_id='" + id + "'";

        select(query);

        try {
            while (result.next()) {
                //Get each ID and add to string with a -
                String claimsID = (String) result.getString("id");

                claims = claims + claimsID + "-";
            }
        } catch (SQLException ex) {
            Logger.getLogger(JDBC1.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (result == null) {
            claims = "1";
        }

        return claims;
    }

    public String makeClaim(String memId, String rationale, float amount) throws SQLException {

        //Query to get information for a certain member
        String query = "Select * from Members where id='" + memId + "'";
        String memStatus = null;

        //Create date of claim
        Date doc = new Date();
        Calendar cal = new GregorianCalendar();

        select(query);

        while (result.next()) {
            //Get status of member
            memStatus = result.getString("status");
            //Set time
            cal.setTime(result.getDate("dor"));
        }

        cal.add(Calendar.MONTH, 6);

        //If member is approved, a claim can be made
        if (memStatus.equals("APPROVED")) {
            if (doc.after(cal.getTime())) {

                //Date will be formatted as yyyy-MM-dd
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date today = new Date();

                //Set claim status to pending
                String status = "PENDING";

                PreparedStatement pStatement = null;

                String dateClaim;

                try {
                    //Convert date to correct format
                    dateClaim = dateFormat.format(today);
                    today = dateFormat.parse(dateClaim);
                } catch (ParseException ex) {
                    Logger.getLogger(JDBC1.class.getName()).log(Level.SEVERE, null, ex);
                }

                //Convert dat from java time to sql time
                java.sql.Date sqlDOC = new java.sql.Date(today.getTime());

                try {
                    //Prepare statement to insert claim into claim table
                    pStatement = connection.prepareStatement("INSERT INTO Claims (mem_id,date,rationale,status,amount) VALUES (?,?,?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS);
                    pStatement.setString(1, memId);
                    pStatement.setDate(2, (java.sql.Date) sqlDOC);
                    pStatement.setString(3, rationale);
                    pStatement.setString(4, status);
                    pStatement.setFloat(5, amount);
                    //Execute insert query
                    pStatement.executeUpdate();

                    pStatement.close();
                    System.out.println("1 line added.");
                    return " CLAIM SUBMITTED FOR APPROVAL!";
                } catch (SQLException ex) {
                    Logger.getLogger(JDBC1.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                return " YOU NEED TO BE A FULL MEMBER FOR 6 MONTHS TO MAKE A CLAIM!";
            }
        } else {

            return " YOU NEED TO BE A FULL MEMBER TO MAKE A CLAIM!";
        }
        return " CLAIM FAILED!";
    }

    public String calcMembershipFee() {

        //Queries for getting information from claims, members and payments
        String query = "SELECT * from Claims where status='ACCEPTED'";
        String memQuery = "Select * from Members where status='APPROVED'";
        String paymentQuery = "Select * from payments";

        float amount = 0.0f, income = 0.0f;
        int memberCount = 0;

        select(query);

        //Format todays date
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date today = new Date();

        //Get date one year ago
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -1);

        //For date conversion/parsing
        String todayDate = null;

        try {
            //Convert date to correct format
            todayDate = dateFormat.format(today);
            today = dateFormat.parse(todayDate);
        } catch (ParseException ex) {
            Logger.getLogger(JDBC1.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            //While result has next value
            while (result.next()) {
                //Get date
                Date date = (Date) result.getDate("date");
                //If date is a year ago and before todays date
                if (date.after(calendar.getTime()) && date.before(today)) {
                    //Increase amount
                    amount += result.getFloat("amount");
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(JDBC1.class.getName()).log(Level.SEVERE, null, ex);
        }

        select(memQuery);

        try {
            //While result has next value
            while (result.next()) {
                //Get status from result set
                String status = result.getString("status");
                //If status is approved, increase memberCount
                if (status.equals("APPROVED")) {
                    memberCount++;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(JDBC1.class.getName()).log(Level.SEVERE, null, ex);
        }

        select(paymentQuery);

        try {
            while (result.next()) {
                Date date = (Date) result.getDate("date");
                if (date.after(calendar.getTime()) && date.before(today)) {
                    income += result.getFloat("amount");
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(JDBC1.class.getName()).log(Level.SEVERE, null, ex);
        }

        //Membership fee is the sum of all claims / the total number of members
        membershipFee = (amount / memberCount);

        if (memberCount > 1) {
            DecimalFormat df = new DecimalFormat();
            df.setMaximumFractionDigits(2);
            membershipFee = Float.valueOf(df.format(membershipFee));
        }

        //Return string of total amount, member count, membership fee and income
        return String.valueOf(amount) + "," + String.valueOf(memberCount)
                + "," + String.valueOf(membershipFee) + "," + String.valueOf(income);
    }

    public String chargeMembers() throws SQLException {

        //Query to select all information from Members table
        String query = "Select * from Members";

        select(query);

        while (result.next()) {
            try {
                //Get members balance
                float balance = result.getFloat("balance");
                //Get members ID
                String id = result.getString("id");
                //Get members status
                String memStatus = result.getString("status");
                //If members status is approved
                if (memStatus.equals("APPROVED")) {
                    //Prepared stament to update the members balance with there anual fee
                    PreparedStatement ps = null;

                    ps = connection.prepareStatement("UPDATE Members Set balance=? where id=?", PreparedStatement.RETURN_GENERATED_KEYS);
                    ps.setFloat(1, balance + membershipFee);
                    ps.setString(2, id);
                    ps.executeUpdate();

                    ps.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(JDBC1.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return " BALANCE UPDATE SUCCESS :: " + membershipFee + " ADDED TO ALL (FULL) MEMBER BALANCES!";
    }

    public String authLogin(String user, String pass) throws SQLException {

        //Query to select information about user from users table
        String query = "select * from users where id='" + user + "'";

        String authKey = null;

        select(query);

        //If result set contains no data
        if (!result.next()) {
            //Authentication failed
            authKey = "failed";
            return authKey;
        } else {
            result.beforeFirst();
            try {
                //While result set has data
                while (result.next()) {
                    //Get user password
                    String pswd = result.getString("password");
                    //Get user status
                    String status = result.getString("status");
                    //If password matches
                    if (pass.equals(pswd)) {
                        //Authentication equals status
                        authKey = status;
                        if(status.equals("ADMIN")) {
                            //Do nothing if user is admin
                        }
                        else
                        {
                            //Check member fee if approved
                            checkMemberFee(user);
                        }

                    } else {
                        authKey = "failed";
                    }
                }
            } catch (SQLException ex) {
                Logger.getLogger(JDBC1.class.getName()).log(Level.SEVERE, null, ex);
                authKey = "failed!";
                return authKey;
            }
        }
        return authKey;
    }

    public String doList(String lookUp, String where) {

        String resultTbl = null;
        String query = null;
        
        //Build query based on what table you want to get information from
        if (where.equals("*")) {
            query = "SELECT * from " + lookUp;
        } else {
            query = "SELECT * from " + lookUp + " WHERE " + where + "";
        }
        select(query);
        try {
            //Store resultTable as a string
            resultTbl = resultTable(resultList());
        } catch (SQLException ex) {
            Logger.getLogger(JDBC1.class.getName()).log(Level.SEVERE, null, ex);
        }
        return resultTbl;
    }

    public boolean checkMemberFee(String memId) throws SQLException {

        int yearMember = 1;
        //Query to get information on member using member ID
        String query = "SELECT * from Members where id='" + memId + "'";
        float balance = 0;
        String memStatus = null;
        
        //Get todays date
        Date today = new Date();
        Date dor = null;
        Calendar calToday = new GregorianCalendar();
        Calendar calDor = new GregorianCalendar();

        select(query);

        while (result.next()) {
            //Get date of registration
            dor = result.getDate("dor");
            //Get members current balance
            balance = result.getFloat("balance");
            //Get members status
            memStatus = result.getString("status");

        }

        //If members status is approved
        if (memStatus.equals("APPROVED")) {
            calToday.setTime(today);
            calDor.setTime(dor);
            //If the difference between todays date and date of registration is greater than a year
            if ((calToday.get(Calendar.YEAR) - calDor.get(Calendar.YEAR) >= yearMember)
                    && (calToday.get(Calendar.MONTH) >= calDor.get(Calendar.MONTH))) {
                //Increment yearMember
                yearMember++;
                PreparedStatement ps = null;
                
                //Prepare statement to update balance
                ps = connection.prepareStatement("UPDATE Members Set balance=? where id=?", PreparedStatement.RETURN_GENERATED_KEYS);
                ps.setFloat(1, balance + 10.00f);
                ps.setString(2, memId);
                ps.executeUpdate();

                ps.close();

                return true;
            } else {
                return false;
            }
        }
        return false;
    }
}
