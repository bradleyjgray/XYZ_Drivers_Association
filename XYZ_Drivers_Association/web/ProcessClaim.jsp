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
        <title>Claims Management</title>
    </head>
    <body>
        <h1 style="font-size:300%;">Claims Management</h1>

        <form action="AdminServlet">
            <p>
                <label><b>Claim ID</b></label>
                <input type="text" placeholder="Claim ID" name="claimId" required>
            </p>
            <p>
                <label><b>Response</b></label>
                <input type="text" placeholder="Approved/Rejected" name="response" required>
            </p>
            <p>
                <button name="request" type="submit" value="respondClaim">Respond</button>
        </form>
                <%
            String message = (String)request.getAttribute("message");
            String messageList = (String)request.getAttribute("messageList");
            
            
            if (message != null){
            out.println("Database Response:: " + message);
            }
            out.println("<br/><br/>Claim List: " + messageList);
        %>
    </body>
</html>
