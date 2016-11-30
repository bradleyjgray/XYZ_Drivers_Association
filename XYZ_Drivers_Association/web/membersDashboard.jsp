
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Members</title>
    </head>
    <body>

    <center>
        <h1 style="font-size:300%;">Members Dashboard</h1>
        
        <%
            String userName = null;
            
            Cookie[] cookies = request.getCookies();
            
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("user")) {
                    userName = cookie.getValue();
                }
            }
            out.print("<center>");
            out.print("<h2 style=\"font-size:150%;\">Welcome " + userName + "</h2>");
            out.print("</center>");
        %>
        
        <table width="50%">
            <tbody>
                <tr>
                    <td><form action="MembersServlet"><center><button name="request" type="submit" value="checkStatus">Check Status</button></center></form></td>

                    <td><form action="MembersServlet"><center><button name="request" type="submit" value="makeClaim">Make a Claim</button></center></form></td>
                    
                    <td><form action="MembersServlet"><center><button name="request" type="submit" value="makePayment">Make a Payment</button></center></form></td>
                </tr>
            </tbody>
        </table>
    </center>

</body>
</html>
