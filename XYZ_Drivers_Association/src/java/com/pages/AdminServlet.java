/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pages;

import com.model.JDBC1;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Luke James
 */
public class AdminServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     * 
     */
    
    /**
     * SERVLET: ADMIN SERVLET
     * ROLE: ORHCHESTRATE COMMUNICATION BETWEEN ADMIN DASHBOARD AND MODEL CLASS.
     * INCLUDES: LISTING MEMBERS, CLAIMS, OUSTANDING BALANCES, OUTSTANDING APPLICATIONS,
     *          CHARGING MEMBERS, RESPONDING TO CLAIMS, UPGRADING/SUSPENDING ACCOUNTS.
     * 
    */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        response.setContentType("text/html;charset=UTF-8");

        //GET SESSION DETAILS AND TAKE REQUEST INPUT AS CMD TO DECISION POINT.
        HttpSession session = request.getSession();

        String cmd = request.getParameter("request");

        //CREATE VARIABLES FOR MEMBER MANIPULATION
        String user = null, name = null, pswd = null, addr = null, status = null;
        float balance = 0.0f;
        Date dob, dor;

        String claimID = null, claimResponse = null;

        //SET ACCEPT/RESPONSE DECISION FOR RESPOND CLAIM SWITCH STATEMENT
        if (cmd.equals("AcceptClaim")) {
            claimID = request.getParameter("claimId");
            claimResponse = "ACCEPTED";
            cmd = "respondClaim";
        } else if (cmd.equals("RejectClaim")) {
            claimID = request.getParameter("claimId");
            claimResponse = "REJECTED";
            cmd = "respondClaim";
        }

        //INSTANTIATE JDBC1 MODEL CLASS & BUILD CONNECTION TO DATABASE FROM LISTENER
        JDBC1 jdbc = new JDBC1();

        jdbc.connect((Connection) request.getServletContext().getAttribute("connection"));
        session.setAttribute("dbConn", jdbc);
        
        //HANDLE DATABASE CONNECTION FAILURE
        if ((Connection) request.getServletContext().getAttribute("connection") == null) {
            request.getRequestDispatcher("/WEB-INF/conErr.jsp").forward(request, response);
        } else {

            //DECISION POINT FOR REQUEST PARAMETER.  VALUE DICTATES ACTION SENT TO MODEL
            //ONCE ACTION HAS BEEN SENT, ANY RESPONSE FROM JDBC WILL BE SENT BACK TO VIEW
            switch (cmd) {
                case "listMembers":
                    String memberList = jdbc.doList("Members", "*");
                    //SET RETURN ATTRIBUTES FOR JSP 
                    request.setAttribute("messageList", memberList);
                    //RETURN TO JSP 
                    RequestDispatcher view = request.getRequestDispatcher("results.jsp");
                    view.forward(request, response);
                    break;
                case "listClaims":
                    String claimList = jdbc.doList("Claims", "*");
                    //SET RETURN ATTRIBUTES FOR JSP 
                    request.setAttribute("messageList", claimList);
                    //RETURN TO JSP
                    view = request.getRequestDispatcher("ProcessClaim.jsp");
                    view.forward(request, response);
                    break;
                case "listApplications":
                    String applicationList = jdbc.doList("Members", "status ='APPLIED'");
                    //SET RETURN ATTRIBUTES FOR JSP 
                    request.setAttribute("messageList", applicationList);
                    //RETURN TO JSP
                    view = request.getRequestDispatcher("results.jsp");
                    view.forward(request, response);
                    break;
                case "listBalances":
                    String balanceList = jdbc.doList("Members", "balance > '0.0f'");
                    //SET RETURN ATTRIBUTES FOR JSP 
                    request.setAttribute("messageList", balanceList);
                    //RETURN TO JSP
                    view = request.getRequestDispatcher("results.jsp");
                    view.forward(request, response);
                    break;
                case "respondClaim":
                    String result = null;
                    result = jdbc.respondClaim(claimID, claimResponse);
                    //SET RETURN ATTRIBUTES FOR JSP 
                    request.setAttribute("message", result);
                    
                    String unrespondedList = jdbc.doList("Claims", "*");
                    //SET RETURN ATTRIBUTES FOR JSP 
                    request.setAttribute("messageList", unrespondedList);
                    //RETURN ATTRIBUTES TO JSP 
                    view = request.getRequestDispatcher("ProcessClaim.jsp");
                    view.forward(request, response);
                    break;
                case "unrespondedClaims":
                    unrespondedList = jdbc.doList("Claims", "status = 'PENDING'");
                    //SET RETURN ATTRIBUTES FOR JSP 
                    request.setAttribute("messageList", unrespondedList);
                    //RETURN ATTRIBUTES TO JSP
                    view = request.getRequestDispatcher("ProcessClaim.jsp");
                    view.forward(request, response);
                    break;
                case "processApplication":
                    String upgraded = null;

                    user = (String) request.getParameter("username");
                    upgraded = user + jdbc.appliedToMember(user);
                    memberList = jdbc.doList("Members", "*");
                    //SET ATTRIBUTES FOR JSP
                    request.setAttribute("message", upgraded);
                    //RETURN TO JSP 
                    request.setAttribute("messageList", memberList);
                    view = request.getRequestDispatcher("results.jsp");
                    view.forward(request, response);
                    break;
                case "suspendMember":
                    String suspended = null;

                    user = (String) request.getParameter("username");
                    suspended = user + jdbc.suspendMember(user);
                    memberList = jdbc.doList("Members", "*");
                    //SET ATTRIBUTES FOR JSP 
                    request.setAttribute("message", suspended);
                    request.setAttribute("messageList", memberList);
                    //RETURN TO JSP
                    view = request.getRequestDispatcher("results.jsp");
                    view.forward(request, response);
                    break;
                case "reportTurnover":
                    result = null;
                    
                    result = jdbc.calcMembershipFee();
                    //SPLIT STRING INTO DIFFERENT VARIABLES FOR JSP VIEW
                    String[] financials = result.split(",");
                    String claims = financials[0];
                    String members = financials[1];
                    String fee = financials[2];
                    String income = financials[3];
                    //SET ATTRIBUTES FOR JSP 
                    request.setAttribute("memberCount", members);
                    request.setAttribute("claimTotal", claims);
                    request.setAttribute("reccCharge", fee);
                    request.setAttribute("totalIncome", income);
                    //RETURN TO JSP 
                    view = request.getRequestDispatcher("FinancialReports.jsp");
                    view.forward(request, response);
                    break;
                case "chargeMembers":
                    result = jdbc.calcMembershipFee();
                    String resultCharge = jdbc.chargeMembers();
                    
                    //SPLIT STRING INTO DIFFERENT VARIABLES FOR JSP VIEW
                    financials = result.split(",");
                    claims = financials[0];
                    members = financials[1];
                    fee = financials[2];
                    income = financials[3];
                    //SET ATTRIBUTES FOR JSP 
                    request.setAttribute("memberCount", members);
                    request.setAttribute("claimTotal", claims);
                    request.setAttribute("reccCharge", fee);
                    request.setAttribute("totalIncome", income);
                    //RETURN TO JSP 
                    request.setAttribute("chargeResult", resultCharge);
                    view = request.getRequestDispatcher("FinancialReports.jsp");
                    view.forward(request, response);
                    break;
                default:
                    break;
            }

        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (SQLException ex) {
            Logger.getLogger(AdminServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (SQLException ex) {
            Logger.getLogger(AdminServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
