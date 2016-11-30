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
public class MemberFilter implements Filter {
    
    private static final boolean debug = true;

    // The filter configuration object we are associated with.  If
    // this value is null, this filter instance is not currently
    // configured. 
    private FilterConfig filterConfig = null;
    private ServletContext context;
    
    public MemberFilter() {
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
     * FILTER: MEMBER
     * ROLE: ENSURE THAT MEMBERS/ADMINS CAN ONLY ACCESS RESPECTIVE DASHBOARDS
     * INCLUDES: CHECKS USER AUTHKEY & RESOURCES REQUESTED AND HANDLES AS APPROPRIATE
     * 
    */
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        
        String resourceRequested = req.getRequestURI();
        this.context.log("Requested Resource: " + resourceRequested);
        
        HttpSession session = req.getSession();
        
        if (session.getAttribute("authKey").equals("MEMBER") && !(resourceRequested.endsWith(".html") || 
                resourceRequested.endsWith("_member"))){
            this.context.log("Unauthorised access request from full member.");
            resp.sendRedirect("membersDashboard.jsp");
            
        } else if (session.getAttribute("authKey").equals("APPLIED") && !(resourceRequested.endsWith(".html") || resourceRequested.endsWith("_applied"))){
            this.context.log("Unauthorised access request from applied member.");
            resp.sendRedirect("membersDashboard.jsp");
        }  else {
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
