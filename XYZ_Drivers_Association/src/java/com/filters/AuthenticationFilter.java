/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.filters;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Luke James
 */
public class AuthenticationFilter implements Filter {
    
    private static final boolean debug = true;

    // The filter configuration object we are associated with.  If
    // this value is null, this filter instance is not currently
    // configured. 
    private FilterConfig filterConfig = null;
    private ServletContext context;
    
    public AuthenticationFilter() {
    }    
    
    /**
     *
     * @param request The servlet request we are processing
     * @param response The servlet response we are creating
     * @param chain The filter chain we are processing
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet error occurs
     */
    
        /**
     * FILTER: AUTHENTICATION FILTER
     * ROLE: ENSURE USERS MUST BE LOGGED IN TO ACCESS BEYOND LOGIN/REGISTRATION PAGES
     * INCLUDES: CHECKS A SESSION IS ALIVE AND THE RESOURCES REQUESTED MATCH THOSE THAT ARE ALLOWED 
     * IF NO SESSION IS AVAILABLE
     * 
    */
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        
        String resourceRequested = req.getRequestURI();
        this.context.log("Requested Resource: " + resourceRequested);
        
        HttpSession session = req.getSession(false);
        
        if (session == null && !(resourceRequested.endsWith("login.html") || 
                resourceRequested.endsWith("doLogin") ||
                resourceRequested.endsWith("index.jsp") ||
                resourceRequested.endsWith("register.html") ||
                resourceRequested.endsWith("RegisterServlet") ||
                resourceRequested.endsWith("RegistrationSuccess.jsp") ||
                resourceRequested.endsWith("logout"))){
            this.context.log("Unauthorised access request from client.");
            resp.sendRedirect("index.jsp");
            return;
        } else {
            chain.doFilter(request, response);
        }
    }

    /**
     * Destroy method for this filter
     */
    public void destroy() {        
    }
    /**
     * Init method for this filter
     */
    public void init(FilterConfig filterConfig) {        
        this.context = filterConfig.getServletContext();
        this.context.log("Authentication filter initialised");
    }
}
