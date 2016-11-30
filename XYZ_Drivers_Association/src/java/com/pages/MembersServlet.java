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
public class MembersServlet extends HttpServlet {

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
        
        //Create a command for which function is chosen
        String cmd = request.getParameter("request");
        
        RequestDispatcher view;
        
        //Try to connect to database, if connection cannot be made then throw exception
        try {
            jdbc.connect((Connection) request.getServletContext().getAttribute("connection"));
        } catch (SQLException ex) {
            Logger.getLogger(MembersServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        session.setAttribute("dbConn", jdbc);

        if ((Connection) request.getServletContext().getAttribute("connection") == null) {
            request.getRequestDispatcher("/WEB-INF/conErr.jsp").forward(request, response);
        } 
        else
        {
            switch(cmd) {
                //If cmd checkStatus, then forward to StatusServlet to view member status
                case "checkStatus":
                    view = request.getRequestDispatcher("StatusServlet");
                    view.forward(request, response);   
                    break;
                //If cmd makeClaim, then forward to MakeClaim jsp to fill out claim form
                case "makeClaim":
                    view = request.getRequestDispatcher("MakeClaim.jsp");
                    view.forward(request, response); 
                    break;
                //If cmd makePayment, then forward to PaymentForm jsp to make a payment
                case "makePayment":
                    view = request.getRequestDispatcher("PaymentForm.jsp");
                    view.forward(request, response); 
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
