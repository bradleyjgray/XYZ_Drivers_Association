<%-- 
    Document   : ClaimManagement
    Created on : 28-Nov-2016, 12:13:30
    Author     : bgray
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title><center>Claims Management</center></title>
    </head>
    <body> <center>
        <h1 style="font-size:300%;">Claims Management</h1>

        <form action="AdminServlet">
            <p>
                <label><b>Claim ID</b></label>
                <input type="text" placeholder="Claim ID" name="claimId" required>
            </p>
            <p>
                <button name="request" type="submit" value="AcceptClaim">Accept</button>
                <button name="request" type="submit" value="RejectClaim">Reject</button>
        </form>
                <%
            String message = (String)request.getAttribute("message");
            String messageList = (String)request.getAttribute("messageList");
            
            
            if (message != null){
            out.println("<p><font color=red>Database Response:: " + message + "</p></font>");
            }
            out.println("<br/><br/>Claim List: " + messageList);
        %>
    </center>
    </body>
</html>
