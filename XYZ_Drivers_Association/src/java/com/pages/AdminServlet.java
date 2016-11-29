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
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        response.setContentType("text/html;charset=UTF-8");

        HttpSession session = request.getSession();

        String cmd = request.getParameter("request");

        String user = null, name = null, pswd = null, addr = null, status = null;
        float balance = 0.0f;
        Date dob, dor;

        String claimID = null, claimResponse = null;

        if (cmd.equals("AcceptClaim")) {
            claimID = request.getParameter("claimId");
            claimResponse = "ACCEPTED";
            cmd = "respondClaim";
        } else if (cmd.equals("RejectClaim")) {
            claimID = request.getParameter("claimId");
            claimResponse = "REJECTED";
            cmd = "respondClaim";
        }

        JDBC1 jdbc = new JDBC1();

        jdbc.connect((Connection) request.getServletContext().getAttribute("connection"));
        session.setAttribute("dbConn", jdbc);

        if ((Connection) request.getServletContext().getAttribute("connection") == null) {
            request.getRequestDispatcher("/WEB-INF/conErr.jsp").forward(request, response);
        } else {

            switch (cmd) {
                case "listMembers":
                    String memberList = jdbc.doList("Members", "*");
                    request.setAttribute("messageList", memberList);
                    RequestDispatcher view = request.getRequestDispatcher("results.jsp");
                    view.forward(request, response);
                    break;
                case "listClaims":
                    String claimList = jdbc.doList("Claims", "*");
                    request.setAttribute("messageList", claimList);
                    view = request.getRequestDispatcher("ProcessClaim.jsp");
                    view.forward(request, response);
                    break;
                case "listApplications":
                    String applicationList = jdbc.doList("Members", "status ='APPLIED'");
                    request.setAttribute("messageList", applicationList);
                    view = request.getRequestDispatcher("results.jsp");
                    view.forward(request, response);
                    break;
                case "listBalances":
                    String balanceList = jdbc.doList("Members", "balance > '0.0f'");
                    request.setAttribute("messageList", balanceList);
                    view = request.getRequestDispatcher("results.jsp");
                    view.forward(request, response);
                    break;
                case "respondClaim":
                    String result = null;
                    result = jdbc.respondClaim(claimID, claimResponse);
                    request.setAttribute("message", result);

                    String unrespondedList = jdbc.doList("Claims", "*");
                    request.setAttribute("messageList", unrespondedList);

                    view = request.getRequestDispatcher("ProcessClaim.jsp");
                    view.forward(request, response);
                    break;
                case "unrespondedClaims":
                    unrespondedList = jdbc.doList("Claims", "status = 'PENDING'");
                    request.setAttribute("messageList", unrespondedList);
                    view = request.getRequestDispatcher("ProcessClaim.jsp");
                    view.forward(request, response);
                    break;
                case "processApplication":
                    String upgraded = null;

                    user = (String) request.getParameter("username");
                    upgraded = user + jdbc.appliedToMember(user);
                    memberList = jdbc.doList("Members", "*");

                    request.setAttribute("message", upgraded);

                    request.setAttribute("messageList", memberList);
                    view = request.getRequestDispatcher("results.jsp");
                    view.forward(request, response);
                    break;
                case "suspendMember":
                    String suspended = null;

                    user = (String) request.getParameter("username");
                    suspended = user + jdbc.suspendMember(user);
                    memberList = jdbc.doList("Members", "*");

                    request.setAttribute("message", suspended);

                    request.setAttribute("messageList", memberList);
                    view = request.getRequestDispatcher("results.jsp");
                    view.forward(request, response);
                    break;
                case "resumeMember":
                    break;
                case "reportTurnover":
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
