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
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author bgray
 */
public class SubmitClaim extends HttpServlet {

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
        //Create HTTP session
        HttpSession session = request.getSession();

        //Create instance of JDBC
        JDBC1 jdbc = new JDBC1();

        String userName = null;

        //Get cookies of current session
        Cookie[] cookies = request.getCookies();

        //Find username in cookies and store in userName value, for use later
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("user")) {
                userName = cookie.getValue();
            }
        }

        //Try to connect to database, if connection cannot be made then throw exception
        try {
            jdbc.connect((Connection) request.getServletContext().getAttribute("connection"));
        } catch (SQLException ex) {
            Logger.getLogger(RegisterServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        session.setAttribute("dbConn", jdbc);

        if ((Connection) request.getServletContext().getAttribute("connection") == null) {
            request.getRequestDispatcher("/WEB-INF/conErr.jsp").forward(request, response);
        } 
        else
        {
            //Get claim rationale
            String description = request.getParameter("description");
            //Get claim amount
            float amount = Float.parseFloat(request.getParameter("amount"));
            //Method to insert information about claim into the database
            String result = jdbc.makeClaim(userName, description, amount);
            //Forward to membersDashboard page
            RequestDispatcher view = request.getRequestDispatcher("membersDashboard.jsp");
            request.setAttribute("result", result);
            view.forward(request, response);
        }
    }

    /*private String calculateNextClaimID(String[] claimArray) {
        String nextClaimID = "";

        int greatestValue = 0;
        int currentValue = 0;

        for (int i = 0; i < claimArray.length; i++) {
            currentValue = Integer.parseInt(claimArray[i]);

            if (currentValue > greatestValue) {
                greatestValue = currentValue;
            }
        }

        nextClaimID = Integer.toString(greatestValue + 1);

        return nextClaimID;
    }*/

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
            Logger.getLogger(SubmitClaim.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(SubmitClaim.class.getName()).log(Level.SEVERE, null, ex);
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
