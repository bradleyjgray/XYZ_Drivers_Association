<%-- 
    Document   : RegistrationSuccess
    Created on : 28-Nov-2016, 22:58:21
    Author     : bgray
--%>

<%@ page import="java.util.*" %>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Registration</title>
    </head>
    <body>
        <h1>Registration Successful!</h1>
        <p>
        <%
            String genName = (String)request.getAttribute("genName");
            String genPass = (String)request.getAttribute("genPass");
            
            out.print("Username: " + genName);
            out.print("\nPassword: " + genPass);
        %>
        </p>
        <form action="login.html">
            <input type="submit" value="Return to Login" />
        </form>
    </body>
</html>
