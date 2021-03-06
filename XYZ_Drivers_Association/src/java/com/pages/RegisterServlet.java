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
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author bgray
 */
public class RegisterServlet extends HttpServlet {

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
            
            //Create iinstance of JDBC
            JDBC1 jdbc = new JDBC1();
            
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
                //Get values for name, address and D.O.B
                String name = request.getParameter("name");
                String addr = request.getParameter("addr");
                String dob = request.getParameter("dob");
            
                //Use name and D.O.B to genrate a username and password for the user
                String nameResult = jdbc.genUsr(name);
                String passResult = jdbc.genPass(dob);
            
                try {
                    //Create a member and insert into member table in the database
                    jdbc.createMember(nameResult, name, addr, dob, "APPLIED");
                    //Add user to user table
                    jdbc.addUsr(nameResult, passResult);
                } catch (SQLException ex) {
                    Logger.getLogger(RegisterServlet.class.getName()).log(Level.SEVERE, null, ex);
                }
            
                //Set attributes for username and password so they can be displayed to the user in a jsp
                request.setAttribute("genName", nameResult);
                request.setAttribute("genPass", passResult);
            
                //Forward to RegistrationSuccess
                RequestDispatcher view = request.getRequestDispatcher("RegistrationSuccess.jsp");
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
