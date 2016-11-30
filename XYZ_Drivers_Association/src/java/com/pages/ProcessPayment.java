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
        HttpSession session = request.getSession();
            
        JDBC1 jdbc = new JDBC1();
            
        String userName = null;
            
        Cookie[] cookies = request.getCookies();
            
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("user")) {
                    userName = cookie.getValue();
            }
        }
            
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
            float amount = Float.parseFloat(request.getParameter("amount"));
            String paymentType = request.getParameter("paymentType");
            
            String paymentsID = jdbc.getPaymentID(userName);
            
            String[] separatePayments = paymentsID.split("-");
            String nextID = "";
            
            if(separatePayments[0].equals("") || separatePayments[0].equals("0")) {
                nextID = "1";
            }
            else
            {
               nextID = calculateNextPaymentID(separatePayments); 
            }
            
            jdbc.makePayment(nextID, userName, amount, paymentType);
            
            jdbc.updateBalance(nextID, userName);
            
            RequestDispatcher view = request.getRequestDispatcher("membersDashboard.jsp");
                
            view.forward(request, response);
        }
    }
    
    private String calculateNextPaymentID(String[] paymentArray) {
        String nextClaimID = "";
        
        int greatestValue = 0;
        int currentValue = 0;
        
        for(int i = 0; i < paymentArray.length; i++) {
            currentValue = Integer.parseInt(paymentArray[i]);
            
            if(currentValue > greatestValue) {
                greatestValue = currentValue;
            }
        }
        
        nextClaimID = Integer.toString(greatestValue + 1);
        
        return nextClaimID;
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
