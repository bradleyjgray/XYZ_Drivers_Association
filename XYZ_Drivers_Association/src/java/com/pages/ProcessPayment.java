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
public class ProcessPayment extends HttpServlet {

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
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        //Create HTTP session
        HttpSession session = request.getSession();
            
        //Create instance of JDBC
        JDBC1 jdbc = new JDBC1();
        
        String result = null;    
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
            //Get amount of payment and store as float
            float amount = Float.parseFloat(request.getParameter("amount"));
            
            String paymentType = null;
            
            //If the amount paid is £10, it is for membership fee. Otherwise the payment will be for outstanding balance
            if (amount == 10.00f){
            paymentType = "MEMBERSHIP";
            } else {
            paymentType = "BALANCE";
            }
            
//            String paymentsID = jdbc.getPaymentID(userName);
//            
//            String[] separatePayments = paymentsID.split("-");
//            String nextID = "";
//            
//            if(separatePayments[0].equals("") || separatePayments[0].equals("0")) {
//                nextID = "1";
//            }
//            else
//            {
//               nextID = calculateNextPaymentID(separatePayments); 
//            }
//            
            //Method inserts details of payment into payment table in the database
            jdbc.makePayment(userName, amount, paymentType);
            
            try {
                //Balance gets updated for member, payment takes money off balance
                result = jdbc.updateBalance(amount, userName);
            } catch (SQLException ex) {
                Logger.getLogger(ProcessPayment.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            //Forward to membersDashboard
            RequestDispatcher view = request.getRequestDispatcher("membersDashboard.jsp");
            request.setAttribute("result", result);
            view.forward(request, response);
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
        processRequest(request, response);
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
        processRequest(request, response);
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
