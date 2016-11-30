<%-- 
    Document   : CheckStatus
    Created on : 29-Nov-2016, 16:02:32
    Author     : bgray
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Member Status</title>
    </head>
    <body>
        <center>
            <h1 style="font-size:300%;">Member Status</h1>
        
            <%
                String memberInfo = (String)request.getAttribute("memberInfo");
                String claim = (String)request.getAttribute("claims");
            
                out.print("<h2 style=\"font-size:150%;\">Info</h2>");
                out.print(memberInfo);
                out.print("<br />");
                out.print("<h2 style=\"font-size:150%;\">Claims</h2>");
                out.print(claim);
                out.print("<br />");
            %>
            
            <form action="membersDashboard.jsp">
                <input type="submit" value="Return to Dashboard" />
            </form>
        </center>
    </body>
</html>
